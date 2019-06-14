package com.example.demo.Jetcache.controller;

import com.example.demo.Jetcache.bean.User;
import com.example.demo.Jetcache.service.UserService;
import com.example.demo.redis.SaveRedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/getUserByUserId")
    public User getUserByUserId(Integer userId){
        return userService.getUserByUserId(userId);
    }

    @RequestMapping("/updateUser")
    public int updateUser(String name){
        User user = new User();
        user.setUserId(9528);
        user.setName(name);
        return userService.updateUser(user);
    }

    @RequestMapping("/get")
    @SaveRedisCache(key = "#userId.name",seconds = 1000)
    public User get(User userId){
        System.out.println("userId:"+userId);
        return userId;
    }
}
