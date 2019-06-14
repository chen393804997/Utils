package com.example.demo.Jetcache.service;

import com.alicp.jetcache.anno.*;
import com.example.demo.Jetcache.bean.User;


public interface UserService {

    /**
     * 根据id获取用户信息
     * @param userId
     * @return
     */
    @Cached(expire = 100,name = "redis-key-Prefix-",key = "#userId",cacheType = CacheType.REMOTE,keyConvertor = KeyConvertor.FASTJSON)
    @CacheRefresh(refresh =  30,stopRefreshAfterLastAccess  =  60,refreshLockTimeout = 10)
    User getUserByUserId(Integer userId);

    @CacheUpdate(name = "redis-key-Prefix-",key = "#user.userId",value = "#user")
    int updateUser(User user);


}
