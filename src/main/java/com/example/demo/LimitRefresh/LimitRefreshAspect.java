package com.example.demo.LimitRefresh;

import com.example.demo.exception.ResultException;
import com.example.demo.exception.ReturnInfoEnum;
import com.example.demo.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class LimitRefreshAspect {

    private final static Logger logger = LoggerFactory.getLogger(LimitRefreshAspect.class);

    private final static String LIMIT = "local oldNum = redis.call('get',KEYS[1])\n" +
            "  if oldNum then\n" +
            "    if tonumber(oldNum) >= tonumber(ARGV[2]) then\n" +
            "      return 0\n" +
            "    end\n" +
            "  end  \n" +
            "local num = redis.call('incr', KEYS[1])\n" +
            "  if tonumber(num) == 1 then\n" +
            "    redis.call('expire', KEYS[1], ARGV[1])\n" +
            "    return 1\n" +
            "  elseif tonumber(num) > tonumber(ARGV[2]) then\n" +
            "    return 0\n" +
            "  else \n" +
            "    return 1\n" +
            "  end";

    @Pointcut("@annotation(com.example.demo.LimitRefresh.LimitRefresh)")
    public void limitRefreshAspect(){}

    @Around(value = "limitRefreshAspect() && @annotation(annotation)")
    public Object limitRefresh(ProceedingJoinPoint joinPoint, LimitRefresh annotation) throws Throwable {
        long s = System.currentTimeMillis();
        String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        Signature signature = joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String prefix = annotation.keyPrefix();
        String suffix = annotation.keySuffix();
        int seconds = annotation.seconds();
        int frequency = annotation.frequency();
        String key = handleKey(prefix,suffix,signature,args);
        isLimit(key,seconds,frequency);
        Object result = null;
        try {
            result = joinPoint.proceed();
        }catch (ResultException e){
            throw e;
        }catch (Throwable throwable){
            throw throwable;
        }finally {
            long e = System.currentTimeMillis();
            logger.info(method+" the time consumption："+(e-s));
        }
        return result;
    }


    private void isLimit(String key,int seconds,int frequency) throws ResultException {
        List<String> keys = new ArrayList<String>(2);
        keys.add(key);
        List<String> values = new ArrayList<String>(4);
        values.add(String.valueOf(seconds));
        values.add(String.valueOf(frequency));
        int result = Integer.parseInt(RedisUtil.evalsha(LIMIT,keys,values).toString());
        if (result < 1) throw new ResultException(ReturnInfoEnum.parameterError.getCode(),"手速太快啦！！！");
    }

    private String handleKey(String prefix,String suffix,Signature signature,Object[] args) throws ResultException {
        if (StringUtils.isEmpty(suffix))
            return prefix;
        if (!suffix.startsWith("#"))
            return prefix + suffix;
        String newSuffix = parseSpel(((MethodSignature) signature).getMethod(),args,suffix,String.class,null);
        if (StringUtils.isEmpty(newSuffix)) {
            logger.error("调用LimitRefresh出现异常！suffix参数有误："+suffix);
            throw new ResultException(ReturnInfoEnum.parameterError.getCode(), "参数异常");
        }
        return prefix + newSuffix;
    }

    private <T> T parseSpel(Method method, Object[] arguments, String spel, Class<T> clazz, T defaultResult) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], arguments[len]);
        }
        try {
            Expression expression = parser.parseExpression(spel);
            return expression.getValue(context, clazz);
        } catch (Exception e) {
            return defaultResult;
        }
    }

    private ExpressionParser parser = new SpelExpressionParser();

    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

}
