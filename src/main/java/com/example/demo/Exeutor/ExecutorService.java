package com.example.demo.Exeutor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 任务执行类
 * @author: czw
 * @create: 2018-12-23 19:28
 **/
@Service
public class ExecutorService {

    @Async//声明为异步方法，该方法会自动注入到TaskExceutor
    public void executeAsyncTask(int i){
        System.out.println("执行异步任务："+i);
    }

    public void execute(int i){
        System.out.println("执行同步任务："+i);
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TaskExecutorConfig.class);
        ExecutorService executorService = context.getBean(ExecutorService.class);
        for (int i = 0; i < 10; i++){
            //同步
            executorService.execute(i);
            //异步
            executorService.executeAsyncTask(i);
        }
        context.close();
    }

}
