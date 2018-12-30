package com.example.demo.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.utils.RedisUtil;
import com.example.demo.utils.ValidateUtil;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 缓存模板
 * @author: czw
 * @create: 2018-12-30 22:54
 **/
public class CacheTemplateService {
    /**
     * 获取缓存模板操作
     * @param key 缓存的键
     * @param clazz 缓存的类型
     * @param loadBack 没有获取到缓存，调用的方法
     * @return
     */
    public <T> T findCache(String key, int seconds, TypeReference<T> clazz, CacheLoadBack<T> loadBack){
        String json = RedisUtil.get(key);

        if(!StringUtils.isEmpty(json)){
            return JSON.parseObject(json, clazz);
        }else{
            T result = loadBack.load();
            if(result!=null&&!"[]".equals(result)&&!"null".equals(result)){
                String value = JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
                if(seconds>0){
                    RedisUtil.setex(key,value,seconds);
                }else{
                    RedisUtil.set(key,value);
                }
            }
            return result;
        }
    }
    /**
     * 获取缓存模板操作，数据可以为空
     * @param key 缓存的键
     * @param clazz 缓存的类型
     * @param loadBack 没有获取到缓存，调用的方法
     * @return
     */
    public <T> T findCacheNull(String key,int seconds,TypeReference<T> clazz,CacheLoadBack<T> loadBack){
        String json = RedisUtil.get(key);

        if(!StringUtils.isEmpty(json)){
            return JSON.parseObject(json, clazz);
        }else{
            T result = loadBack.load();
            String value = JSON.toJSONString(result,SerializerFeature.DisableCircularReferenceDetect);
            if(seconds>0){
                RedisUtil.setex(key, value, seconds);
            }else{
                RedisUtil.set(key,value);
            }
            return (result==null||"".equals(result))?null:result;
        }
    }

    /**
     * 防止缓存击穿，并发量高，查询速度慢可用这个
     * 获取缓存模板操作
     * @param key 缓存的键
     * @param clazz 缓存的类型
     * @param loadBack 没有获取到缓存，调用的方法
     * @return
     */
    public <T> T findCachePreveBreak(String key,int seconds,TypeReference<T> clazz,CacheLoadBack<T> loadBack){

        String json = RedisUtil.get(key);
        if(!StringUtils.isEmpty(json)){
            return JSON.parseObject(json, clazz);
        }else{
            synchronized (this) {
                json = RedisUtil.get(key);
                if(!StringUtils.isEmpty(json)){
                    return JSON.parseObject(json, clazz);
                }
                T result = loadBack.load();
                if(result!=null&&!"[]".equals(result)&&!"null".equals(result)){
                    String value = JSON.toJSONString(result,SerializerFeature.DisableCircularReferenceDetect);
                    if(seconds>0){
                        RedisUtil.setex(key, value, seconds);
                    }else{
                        RedisUtil.set(key,value);
                    }
                }
                return result;
            }

        }
    }


    /**
     * 防止缓存击穿，并发量高，查询速度慢，数据可以为空可用这个
     * 获取缓存模板操作
     * @param key 缓存的键
     * @param clazz 缓存的类型
     * @param loadBack 没有获取到缓存，调用的方法
     * @return
     */
    public <T> T findCachePreveBreakNull(String key,int seconds,TypeReference<T> clazz,CacheLoadBack<T> loadBack){

        String json = RedisUtil.get(key);
        if(!StringUtils.isEmpty(json)){
            return JSON.parseObject(json, clazz);
        }else{
            synchronized (this) {
                json = RedisUtil.get(key);
                if(!StringUtils.isEmpty(json)){
                    return JSON.parseObject(json, clazz);
                }
                T result = loadBack.load();
                String value = JSON.toJSONString(result,SerializerFeature.DisableCircularReferenceDetect);
                if(seconds>0){
                    RedisUtil.setex(key,value,seconds);
                }else{
                    RedisUtil.set(key,value);
                }
                return result;
            }

        }
    }

    /**
     * 防止缓存击穿，并发量高，查询速度慢，集合操作
     * 获取缓存模板操作
     * @param key 缓存的键
     * @param seconds 缓存时间
     * @param clazz 缓存的类型
     * @param cacheLoadBack 没有获取到缓存，调用的方法
     * @return
     */
    public <T> List findCachePreveBreakList(String key, int seconds, int pageSize, Class<T> clazz, CacheLoadBack<T> cacheLoadBack){

        List<String> list = RedisUtil.lrange(key, 0,pageSize );
        if(ValidateUtil.checkListIsNotEmpty(list)){
            return JSON.parseArray(list.toString(), clazz);
        }else{
            synchronized (this) {
                list = RedisUtil.lrange(key, 0, pageSize);
                if(ValidateUtil.checkListIsNotEmpty(list)){
                    return JSON.parseArray(list.toString(), clazz);
                }
                List<T> result = cacheLoadBack.load();
                //如果集合不为空并且元素个数大于等于每页显示的个数
                if(ValidateUtil.checkListIsNotEmpty(result)&&result.size()>=pageSize+1){
                    for(T t:result){
                        String jsonResult = JSON.toJSONString(t,SerializerFeature.DisableCircularReferenceDetect);
                        RedisUtil.rpush(key, jsonResult);
                    }
                    if(seconds>0){
                        RedisUtil.expire(key, seconds);
                    }
                }
                return result;
            }

        }
    }

    /**
     * 防止缓存击穿，并发量高，查询速度慢，集合操作
     * 获取缓存模板操作
     * @param key 缓存的键
     * @param seconds 缓存时间
     * @param clazz 缓存的类型
     * @param cacheLoadBack 没有获取到缓存，调用的方法
     * @return
     */
    public <T> List findCachePreveBreakNewList(String key,int seconds,int pageSize,Class<T> clazz,CacheLoadBack<List<T>> cacheLoadBack){

        List<String> list = RedisUtil.lrange(key, 0,pageSize );
        if(ValidateUtil.checkListIsNotEmpty(list)){
            return JSON.parseArray(list.toString(), clazz);
        }else{
            synchronized (this) {
                list = RedisUtil.lrange(key, 0, pageSize);
                if(ValidateUtil.checkListIsNotEmpty(list)){
                    return JSON.parseArray(list.toString(), clazz);
                }
                List<T> result = cacheLoadBack.load();
                //如果集合不为空并且元素个数大于等于每页显示的个数
                if(ValidateUtil.checkListIsNotEmpty(result)){
                    for(T t:result){
                        String jsonResult = JSON.toJSONString(t,SerializerFeature.DisableCircularReferenceDetect);
                        RedisUtil.rpush(key, jsonResult);
                    }
                    if(seconds>0){
                        RedisUtil.expire(key, seconds);
                    }
                }
                return result;
            }

        }
    }

    /**
     * 获取缓存模板操作int类型
     * 每次在原数据上+1
     * @param key 缓存的键
     * @param clazz 缓存的类型
     * @param loadBack 没有获取到缓存，调用的方法
     * @return
     */
    public Integer findIncrCache(String key,int seconds,CacheLoadBack<Integer> loadBack){

        String json = RedisUtil.get(key);

        if(!StringUtils.isEmpty(json)){
            RedisUtil.incr(key);
            return Integer.parseInt(json);
        }else{
            Integer result = loadBack.load();
            if(result==null){
                result = 0;
            }
            if(seconds>0){
                RedisUtil.setex(key, result+"", seconds);
            }else{
                RedisUtil.set(key,result+"");
            }
            return result;
        }
    }

    public <T>List findCachePreveBreakNewSet(String key, int seconds, Class<T> Clazz, CacheLoadBack<List<T>> cacheLoadBack) {
        Set<String> smembers = RedisUtil.smembers(key);
        if (ValidateUtil.checkSetIsNotEmpty(smembers)) {
            return JSON.parseArray(smembers.toString(), Clazz);
        } else {
            synchronized (this) {
                smembers = RedisUtil.smembers(key);
                if (ValidateUtil.checkSetIsNotEmpty(smembers)) {
                    return JSON.parseArray(smembers.toString(), Clazz);
                }
                List<T> result = cacheLoadBack.load();
                if (ValidateUtil.checkListIsNotEmpty(result)) {
                    for (T t : result) {
                        String jsonResult = JSON.toJSONString(t, SerializerFeature.DisableCircularReferenceDetect);
                        RedisUtil.sAdd(key, jsonResult);
                    }
                    if (seconds > 0) {
                        RedisUtil.expire(key, seconds);
                    }
                }
                return result;
            }

        }
    }
}
