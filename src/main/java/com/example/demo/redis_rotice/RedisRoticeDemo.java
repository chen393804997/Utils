package com.example.demo.redis_rotice;

import com.example.demo.utils.RedisUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: czw
 * @Date: 2019/4/28 10:00
 */
@RestController
public class RedisRoticeDemo {

    @RequestMapping("/set")
    public void set() throws InterruptedException {
        while (true){
            RedisUtil.setex(Math.random()+"","1",10);
            Thread.sleep(10);
        }
    }

}
