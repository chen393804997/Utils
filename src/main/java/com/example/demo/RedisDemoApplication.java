package com.example.demo;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.example.demo","com.alicp.jetcache.autoconfigure"})//扫描增加jetcache自动配置对应的包。
@EnableMethodCache(basePackages = "com.example.demo.Jetcache")//制定开启缓存对应的包路径
@EnableCreateCacheAnnotation//开启对应的CreateCache注解。
public class RedisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisDemoApplication.class, args);
    }
}
