package com.example.demo.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.geo.GeoRadiusParam;

import java.io.*;
import java.util.*;

/**
 * @author: czw
 * @create: 2018-10-28 13:46
 **/
public class RedisUtil {

    public static void vagueDelete(String pattern) {
        Jedis jedis = JedisPoolUtil.getJedis();
        Set<String> sets = jedis.keys(pattern);
        if(sets != null && !sets.isEmpty()) {
            Iterator<String> its = sets.iterator();
            while(its.hasNext()) {
                jedis.del(its.next());
            }
        }
        JedisPoolUtil.returnResource(jedis);
    }

    //default 200F
    public static List<GeoRadiusResponse> getGeoRadiusResponse(Coordinate coordinate, float distance) {
        Jedis jedis = null;
        List<GeoRadiusResponse>  geoRadiusResponses = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            geoRadiusResponses =  jedis.georadius(coordinate.getTableName(),coordinate.getLongitude(),coordinate.getLatitude(),distance,GeoUnit.M, GeoRadiusParam.geoRadiusParam().withDist().sortAscending().count(coordinate.getCurrentPage()*coordinate.getPageSize()));
        } catch (Exception e) {
        } finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return geoRadiusResponses;
    }



    public static int getGeoRadiusResponseCount(Coordinate coordinate,float distance) {
        Jedis jedis = null;
        List<GeoRadiusResponse>  geoRadiusResponses = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            geoRadiusResponses =  jedis.georadius(coordinate.getTableName(),coordinate.getLongitude(),coordinate.getLatitude(),distance,GeoUnit.M, GeoRadiusParam.geoRadiusParam());
            return geoRadiusResponses.size();
        } catch (Exception e) {
        } finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return 0;
    }


    public static String get(String key){
        Jedis jedis = null;
        String value = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            value = jedis.get(key);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return value;
    }

    public static Long incrBy(String key,long times){
        Jedis jedis = null;
        Long value = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            value = jedis.incrBy(key, times);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return value;
    }

    public static void setex(String key,String value,int seconds){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            jedis.setex(key,seconds,value);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    public static void set(String key,String value){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            jedis.set(key,value);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    public static Long expire(String key,Integer seconds){
        Jedis jedis = null;
        Long value = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            value = jedis.expire(key, seconds);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);

        }
        return value;
    }

    public static void addCoordinate(Coordinate coordinate) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            jedis.geoadd(coordinate.getTableName(),coordinate.getLongitude(),coordinate.getLatitude(),coordinate.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    public static  void zAdd(String key,String member,Integer score){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            if(score == null) {
                score =0;
            }
            jedis.zadd(key, score, member);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    public static  Set<String> zrevrange(String key,Long start,Long end){
        Jedis jedis = null;
        Set<String> sets = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            sets = jedis.zrevrange(key, start, end);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return sets;
    }

    public static  Set<String> zrange(String key,Long start,Long end){
        Jedis jedis = null;
        Set<String> sets = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            sets = jedis.zrange(key, start, end);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return sets;
    }

    public static  Long zrem(String key,Integer did ){
        Jedis jedis = null;
        long i = 0;
        try {
            jedis = JedisPoolUtil.getJedis();
            jedis.zrem(key, String.valueOf(did));
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return i;
    }

    public static  Long zrem(String key,String did ){
        Jedis jedis = null;
        long i = 0;
        try {
            jedis = JedisPoolUtil.getJedis();
            i = jedis.zrem(key, did);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return i;
    }

    public static  Long sAdd(String key,String members){
        Jedis jedis = null;
        Long len = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            len = jedis.sadd(key, members);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return len;
    }

    public static Set<String> sMember(String key){
        Jedis jedis = null;
        Set<String> set = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            set = jedis.smembers(key);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return set;
    }

    public static Long sDeleteAll(String key){
        Jedis jedis = null;
        long s = 0;
        try {
            jedis = JedisPoolUtil.getJedis();
            Set<String> set = jedis.smembers(key);
            Iterator<String> it = set.iterator();
            List<String> lists = new ArrayList<String>(set.size());
            while(it.hasNext()) {
                lists.add(it.next());
            }
            for(int i = 0; i<lists.size();i++) {
                jedis.del(lists.get(i));
                jedis.srem(key, lists.get(i));
            }
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return s;
    }

    public static Long sDelete(String key,String value){
        Jedis jedis = null;
        Long s = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            jedis.srem(key, value);
            s =jedis.del(value);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return s;
    }

    public static void setObject(String key,Object obj,int seconds){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            String s = jedis.set(key.getBytes(), object2Bytes(obj));
            if(seconds >0) {
                jedis.expire(key.getBytes(), seconds);
            }
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    public static Long delete(String key){
        Jedis jedis = null;
        Long s = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            s = jedis.expire(key, -2);
        }catch (Exception e) {
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return s;
    }

    public static Long delete(byte[] key){
        Jedis jedis = null;
        Long s = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            s =  jedis.del(key);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return s;
    }

    public static Long deleteS(String key){
        Jedis jedis = JedisPoolUtil.getJedis();
        //long s = jedis.expire(key, -2);
        long s =  jedis.del(key);
        JedisPoolUtil.returnResource(jedis);
        return s;
    }
    public static Object getObject(String key){
        Jedis jedis = null;
        Object o = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            o =(Object)byte2Object(jedis.get(key.getBytes()));
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return o;

    }

    public static byte[] object2Bytes(Object value) {
        if (value == null){
            return null;
        }
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(arrayOutputStream);
            outputStream.writeObject(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                arrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return arrayOutputStream.toByteArray();
    }

    public static Object byte2Object(byte[] bytes) {
        if (bytes == null || bytes.length == 0){
            return null;
        }
        try {
            ObjectInputStream inputStream;
            inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object obj = inputStream.readObject();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除带有某前缀的key
     * @param keys
     */
    public static void batchDel(String keys) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Set<String> set = jedis.keys(keys + "*");
            if(set == null){
                return;
            }
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String keyStr = it.next();
                jedis.del(keyStr);
            }
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }


    public static  Long sAdd(String key,String members,int seconds){
        Jedis jedis = null;
        Long len = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            len = jedis.sadd(key, members);
            if(len > 0 ){
                jedis.expire(key, seconds);
            }
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return len;
    }



    /**
     * 把对象放入Hash中
     */
    public static void hset(String key,String field,Object o){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            jedis.hset(key,field, JSON.toJSONString(o));
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    public static void hset(String key,String field,Object o,int seconds){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            jedis.hset(key,field, JSON.toJSONString(o));
            jedis.expire(key, seconds);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    public static void hset(String key,String field,String value,int seconds){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            jedis.hset(key,field,value);
            jedis.expire(key, seconds);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    public static void hsetString(String key,String field,String value){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            jedis.hset(key,field,value);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    /**
     * 从Hash中获取对象
     */
    public static String hget(String key,String field){
        Jedis jedis = null;
        String text = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            text=jedis.hget(key,field);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return text;
    }

    /**
     * 从Hash中获取对象,转换成制定类型
     */
    public static  <T> T hget(String key,String field,Class<T> clazz){
        String text=hget(key, field);
        if (StringUtils.isEmpty(text)){
            return null;
        }
        T result=JSON.parseObject(text, clazz);
        return result;
    }
    /**
     * 从Hash中删除对象
     */
    public static void hdel(String key,String ... field){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Object result=jedis.hdel(key,field);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
    }

    /**
     * 获取keys开头的所有key
     * @param keys
     * @return
     */
    public static Set<String> batchFind(String keys){
        Jedis jedis = null;
        Set<String> set = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            set = jedis.keys(keys + "*");
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return set;
    }

    public static Map<String,String> hgetAll(String key){
        Jedis jedis = null;
        Map<String,String> map = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            map = jedis.hgetAll(key);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return map;
    }

    /**
     * 获取地理位置的坐标
     * @param key
     * @param members
     * @return
     */
    public static List<GeoCoordinate> getpos(String key, String... members){
        Jedis jedis = null;
        List<GeoCoordinate> list = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            list = jedis.geopos(key,members);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return list;
    }

    /**
     * 获取两点之间的距离
     * @param key
     * @param member1
     * @param member2
     * @param unit
     * @return
     */
    public static Double geoDist(String key,String member1,String member2,GeoUnit unit){
        Jedis jedis = null;
        Double dist = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            dist = jedis.geodist(key,member1,member2,unit);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return dist;
    }

    public static Long setnx(String key,String value,int seconds){
        Long result = null;
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            result = jedis.setnx(key,value);
            if (result == 1){
                jedis.expire(key,seconds);
            }
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return result;
    }
    public static List<String> lrange(String key, long start, long end) {
        List<String> result = null;
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            if (jedis == null){
                return result;
            }
            result = jedis.lrange(key, start, end);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return result;
    }

    public static Long rpush(String key, String string) {
        Long result = null;
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            if (jedis == null){
                return result;
            }
            result = jedis.rpush(key, string);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return result;
    }

    public static Long incr(String key) {
        Long result = null;
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            if (jedis == null){
                return result;
            }
            result = jedis.incr(key);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return result;
    }
    public static Set<String> smembers(String key) {
        Set<String> result = null;
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            if (jedis == null){
                return result;
            }
            result = jedis.smembers(key);
        }catch (Exception e){
        }finally {
            JedisPoolUtil.returnResource(jedis);
        }
        return result;
    }

}
