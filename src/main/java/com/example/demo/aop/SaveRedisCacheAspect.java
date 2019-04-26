package com.example.demo.aop;

import com.example.demo.redis.SaveRedisCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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

        logger.info("已经记录下操作日志@Around 方法执行前");
        Object result = joinPoint.proceed();
        logger.info("已经记录下操作日志@Around 方法执行后");

        return result;
    }

}
