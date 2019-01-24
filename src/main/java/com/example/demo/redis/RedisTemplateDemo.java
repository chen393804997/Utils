package com.example.demo.redis;

import com.alibaba.fastjson.TypeReference;

/**
 * @author: czw
 * @create: 2018-12-30 23:17
 **/
public class RedisTemplateDemo {


    public static void main(String[] args) {
        CacheTemplateService cache = new CacheTemplateService();
        String result = cache.findCache("test", 10, new TypeReference<String>(){}, new CacheLoadBack<String>() {
            @Override
            public String load() {
                System.out.println("redis没有数据");
                return "新数据";
            }
        });
        System.out.println("返回的数据:"+result);
    }

}
