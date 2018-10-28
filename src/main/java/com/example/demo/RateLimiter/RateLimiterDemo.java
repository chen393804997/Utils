package com.example.demo.RateLimiter;

import com.google.common.util.concurrent.RateLimiter;

import javax.annotation.security.RunAs;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: czw
 * @create: 2018-10-28 16:48
 **/
public class RateLimiterDemo {
    /** 表示每秒产生1个令牌*/
    public static RateLimiter rateLimiter = RateLimiter.create(1);

    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    /** 超时时间*/
    public static int timeout = 100;

    public static void rateLimiterTest() throws InterruptedException {
        List<Runnable> list = new ArrayList<Runnable>();
        for (int i = 0; i < 1000; i++){
            list.add(new UserRequest(i));
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (Runnable runnable : list){
            //拿指定个数的令牌，会阻塞直到拿到位置
            //RateLimiterDemo.rateLimiter.acquire(1);
            if (RateLimiterDemo.rateLimiter.tryAcquire(RateLimiterDemo.timeout,TimeUnit.MILLISECONDS)){
                atomicInteger.incrementAndGet();
                System.out.println("抢到令牌:"+atomicInteger.get());
                Thread.sleep(500);
            }else {
                System.out.println("没有抢到令牌");
                Thread.sleep(10);
            }
            executorService.execute(runnable);
        }
        executorService.shutdown();
    }


    private static class UserRequest implements Runnable{
        private int id;

        public UserRequest(int id){
            this.id = id;
        }

        @Override
        public void run() {
            System.out.println(id);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiterDemo.rateLimiterTest();
    }
}
