package com.example.demo.Jetcache.service.impl;

import com.example.demo.Jetcache.bean.User;
import com.example.demo.Jetcache.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer userId) {
        User user = new User();
        user.setUserId(userId);
        user.setAge(16);
        user.setName("张三");
        user.setHeight(18.05);
        List<String> list = new ArrayList<>();
        list.add("元素1");
        list.add("元素2");
        Map<String,String> map = new HashMap<>();
        map.put("key","value");
        user.setList(list);
        user.setMap(map);
        System.out.println("开始查询数据库："+user);
        return user;
    }

    @Override
    public int updateUser(User user) {
        System.out.println("开始修改数据");
        return 1;
    }
}
