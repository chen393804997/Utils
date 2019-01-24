package com.example.demo.redis;
/**
 * 缓存没命中的回调
 * @author andy
 * @param <T>
 */
@SuppressWarnings("all")
public interface CacheLoadBack<T> {
    /**
     * 回调函数
     * @param <T>
     * @return
     */
    public <T>T load();

}
