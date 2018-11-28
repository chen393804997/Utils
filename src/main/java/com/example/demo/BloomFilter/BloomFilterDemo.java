package com.example.demo.BloomFilter;

/**
 * 自己实现的布隆过滤器
 * @author: czw
 * @Date: 2018-11-28 14:01
 **/
public class BloomFilterDemo {

    /** 数组的长度*/
    private int arraySize;
    /** 数组*/
    private int[] arrays;

    public BloomFilterDemo(int arraySize) {
        this.arraySize = arraySize;
        arrays = new int[arraySize];
    }

    /**
     * 添加值
     * @param key
     */
    public void add(String key){
        int first = hashcode_1(key);
        int second = hashcode_2(key);
        int third = hashcode_3(key);

        arrays[first % arraySize] = 1;
        arrays[second % arraySize] = 1;
        arrays[third % arraySize] = 1;
    }

    private int hashcode_1(String key){
        int hash = 0;
        for (int i = 0; i < key.length();++i){
            hash = 33 * hash + key.charAt(i);
        }
        return Math.abs(hash);
    }

    private int hashcode_2(String key){
        final int p = 1677619;
        int hash = (int)2166136261L;
        for (int i = 0; i < key.length(); i++){
            hash = (hash ^ key.charAt(i) * p);
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return Math.abs(hash);
    }

    private int hashcode_3(String key){
        int hash ,i;
        for (hash = 0, i = 0; i < key.length();++i){
            hash += key.charAt(i);
            hash += (hash << 10);
            hash ^= (hash >> 6);
        }
        hash += (hash << 3);
        hash ^= (hash >> 11);
        hash += (hash << 15);
        return Math.abs(hash);
    }

    /**
     * 判断值是否存在
     * @param key
     * @return 存在：true 不存在：false
     */
    public boolean check(String key){
        int first = hashcode_1(key);
        int second = hashcode_2(key);
        int third = hashcode_3(key);
        int firstIndex = arrays[first % arraySize];
        if (firstIndex == 0){
            return false;
        }
        int secondIndex = arrays[second % arraySize];
        if (secondIndex == 0){
            return false;
        }
        int thirdIndex = arrays[third % arraySize];
        if (thirdIndex == 0){
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        int index = 10000000;
        BloomFilterDemo bfd = new BloomFilterDemo(index);
        for (int i = 0; i < index; i++){
            bfd.add("czw"+i);
        }
        boolean isExistence = bfd.check("czw10312");
        System.out.println(isExistence);

    }

}
