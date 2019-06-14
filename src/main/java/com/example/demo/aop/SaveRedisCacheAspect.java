package com.example.demo.aop;

import com.example.demo.redis.SaveRedisCache;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: czw
 * @Date: 2019/4/26 15:28
 */
@Aspect
@Component
public class SaveRedisCacheAspect {

    private final static Logger logger = LoggerFactory.getLogger(SaveRedisCacheAspect.class);


    @Pointcut("@annotation(com.example.demo.redis.SaveRedisCache)")
    public void saveRedisCacheAspect(){}


    @Around(value = "saveRedisCacheAspect() && @annotation(annotation)")
    public Object interceptorApplogic(ProceedingJoinPoint joinPoint,
                                      SaveRedisCache annotation) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String method = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info("参数："+annotation.key()+"过期时间："+annotation.seconds()+"类型："+annotation.type());




        //获取方法传入的参数
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        int timeStampIndex = ArrayUtils.indexOf(parameterNames, annotation.key());
        if (timeStampIndex != -1){
            //获取参数值
            System.out.println("传入的参数值:"+args[timeStampIndex]);
        }



        //解析spel
        String el = parseSpel(((MethodSignature) signature).getMethod(),args,annotation.key(),String.class,null);
        logger.info("解析出来的el："+el);


        logger.info("已经记录下操作日志@Around 方法执行前");
        Object result = joinPoint.proceed();
        logger.info("已经记录下操作日志@Around 方法执行后，方法返回值："+result);

        return result;
    }



    /**
     * 解析 spel 表达式
     *
     * @param method    方法
     * @param arguments 参数
     * @param spel      表达式
     * @param clazz     返回结果的类型
     * @param defaultResult 默认结果
     * @return 执行spel表达式后的结果
     */
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
