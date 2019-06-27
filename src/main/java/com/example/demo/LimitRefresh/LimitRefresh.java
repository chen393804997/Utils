package com.example.demo.LimitRefresh;

import java.lang.annotation.*;

/**
 * 防止频繁刷新
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimitRefresh {

    /**
     * redis存放key的前缀
     * @return
     */
    String keyPrefix();

    /**
     * redis存放key的后缀
     * @return
     */
    String keySuffix() default "";

    /**
     * 限制的时间 默认两秒
     * @return
     */
    int seconds() default RedisKeyConfig.TWO_SECOND;

    /**
     * 限制的次数 默认一次
     * @return
     */
    int frequency() default 1;

}
