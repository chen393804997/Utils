package com.example.demo.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author: czw
 * @Date: 2019/4/26 15:20
 */
@Target( { ElementType.METHOD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface SaveRedisCache {

    String key() ;

    int seconds() default 0;

    Class<?>[] type() default String.class;

}
