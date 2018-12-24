package com.example.demo.BloomFilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;

/**
 * Guava实现布隆过滤器
 * @author: czw
 * @Date: 2018-11-28 14:26
 **/
public class GuavaBloomFilter {

    /** 存放长度*/
    public static final int EXPECTEDINSERTIONS = 10000000;
    /** 误差率*/
    public static final double FPP = 0.1;
    /**  存放integer*/
    public static BloomFilter<Integer> bloomFilterInt = BloomFilter.create(Funnels.integerFunnel(),EXPECTEDINSERTIONS,FPP);
    /** 存放string*/
    public static BloomFilter<CharSequence> bloomFilterString = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), EXPECTEDINSERTIONS,FPP);

    public static void putInteger(Integer value){
        bloomFilterInt.put(value);
    }
    public static void putString(String value){
        bloomFilterString.put(value);
    }

    public static boolean checkInteger(Integer key){
        return bloomFilterInt.mightContain(key);
    }

    public static boolean checkString(String key){
        return bloomFilterString.mightContain(key);
    }
}
