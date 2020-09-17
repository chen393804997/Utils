package com.example.demo;

import com.example.demo.exception.ResultException;
import com.example.demo.exception.ReturnInfoEnum;
import javafx.concurrent.Worker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;


public class DemoApplicationTests extends AbstractQueuedSynchronizer {
    static ReentrantLock reentrantLock = new ReentrantLock(false);
    public void contextLoads(){
        reentrantLock.lock();
        System.out.println("进入！"+Thread.currentThread().getName());
        try {
            //TimeUnit.SECONDS.sleep(5);
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            reentrantLock.unlock();
            System.out.println("解锁"+Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        DemoApplicationTests tests = new DemoApplicationTests();
        for (int i = 0 ; i < 1000; i++){
           new Thread(tests::contextLoads).start();
        }

        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<Integer>();
        //synchronousQueue.offer(1);
        //synchronousQueue.put(34);
        synchronousQueue.offer(2);
        synchronousQueue.offer(3);
        System.out.println(synchronousQueue.poll());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,3,1,TimeUnit.MINUTES,new SynchronousQueue());
        //for (int i = 0; i < 10;i ++){
        //    threadPoolExecutor.execute(()->{
        //        System.out.println("进入："+Thread.currentThread().getName());
        //        try {
        //            TimeUnit.SECONDS.sleep(5);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        //    });
        //}
        //CountDownLatch countDownLatch = new CountDownLatch(4);
        //countDownLatch.countDown();
        //countDownLatch.await();

    }
    



}
