package com.example.demo.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis连接池
 * @author: czw
 * @create: 2018-10-28 13:42
 **/
public class JedisPoolUtil {
    /** redis ip*/
    private static String ip = "127.0.0.1";
    /** redis端口号*/
    private static int port = 6379;
    /** redis密码*/
    private static String password = "123456";

    private static JedisPool jedisPool = null;

    private JedisPoolUtil(){}
    public static JedisPool getJedisPoolInstance(){
        if(null == jedisPool){
            synchronized (JedisPoolUtil.class){
                if(null == jedisPool){
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    poolConfig.setMinIdle(10);
                    poolConfig.setMaxTotal(100);
                    poolConfig.setMaxIdle(50);
                    poolConfig.setMaxWaitMillis(10*1000);
                    poolConfig.setTestOnReturn(true);
                    poolConfig.setTestOnBorrow(true);
                    poolConfig.setTestWhileIdle(true);
                    //表示idle object evitor两次扫描之间要sleep的毫秒数
                    poolConfig.setTimeBetweenEvictionRunsMillis(30000);
                    //表示idle object evitor每次扫描的最多的对象数
                    poolConfig.setNumTestsPerEvictionRun(10);
                    //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
                    poolConfig.setMinEvictableIdleTimeMillis(60000);
                    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
                    //poolConfig.setTestOnBorrow(true);
                    jedisPool = new JedisPool(poolConfig, ip,port,30000,password);
                }
            }
        }
        return jedisPool;
    }

    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {

        if (jedis != null) {
            jedis.close();
        }
    }

    public static synchronized Jedis getJedis(){
        JedisPool JedisPool = getJedisPoolInstance();
        try {
            Jedis jedis = JedisPool.getResource();
            return jedis;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
