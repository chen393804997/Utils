package com.example.demo.BloomFilter;

import com.example.demo.utils.JedisPoolUtil;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * redis实现BloomFilter
 */
public class RedisBloomFilter {

    /** 预计插入量*/
    private long expectedInsertions = 10000000;
    /** 可接受的错误率*/
    private double fpp = 0.001F;
    /** 布隆过滤器的键在Redis中的前缀 利用它可以统计过滤器对Redis的使用情况*/
    private String redisKeyPrefix = "bloom_filter:";
    /** bit数组长度*/
    private long numBits = optimalNumOfBits(expectedInsertions, fpp);
    /**hash函数数量*/
    private int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);

    /** 计算hash函数个数 方法来自guava */
    private int optimalNumOfHashFunctions(long n, long m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }

    /** 计算bit数组长度 方法来自guava */
    private long optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (long) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 判断keys是否存在于集合key中 不存在就将值放进去
     */
    public boolean isExist(String key, String value) throws Exception {
        long[] indexs = getIndexs(value);
        boolean result;
        //这里使用了Redis管道来降低过滤器运行当中访问Redis次数 降低Redis并发量
        Jedis jedis = JedisPoolUtil.getJedis();
        Pipeline pipeline = jedis.pipelined();
        try {
            for (long index : indexs) {
                pipeline.getbit(getRedisKey(key), index);
            }
            result = !pipeline.syncAndReturnAll().contains(false);
        } finally {
            try {
                pipeline.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jedis.close();

        }
        if (!result) {
            //不存在将值放进去
            put(key, value);
        }
        return result;
    }

    /**
     * 将key存入redis bitmap
     */
    private void put(String key, String value) throws Exception {
        long[] indexs = getIndexs(value);
        //这里使用了Redis管道来降低过滤器运行当中访问Redis次数 降低Redis并发量
        Jedis jedis = JedisPoolUtil.getJedis();
        Pipeline pipeline = jedis.pipelined();
        try {
            for (long index : indexs) {
                pipeline.setbit(getRedisKey(key), index, true);
            }
            pipeline.sync();
        } finally {
            try {
                pipeline.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jedis.close();

        }
    }

    /**
     * 根据key获取bitmap下标 方法来自guava
     */
    private long[] getIndexs(String key) {
        long hash1 = hash(key);
        long hash2 = hash1 >>> 16;
        long[] result = new long[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            long combinedHash = hash1 + i * hash2;
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            result[i] = combinedHash % numBits;
        }
        return result;
    }

    /**
     * 获取一个hash值 方法来自guava
     */
    private long hash(String key) {
        Charset charset = Charset.forName("UTF-8");
        return Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(charset)).asLong();
    }

    private String getRedisKey(String where) {
        return redisKeyPrefix + where;
    }


}
