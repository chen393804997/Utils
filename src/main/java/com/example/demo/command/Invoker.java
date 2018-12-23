package com.example.demo.command;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令执行类
 * @author: czw
 * @create: 2018-12-12 21:48
 **/
public class Invoker {
    //存放命令
    public static Map<String, Command> map = new HashMap<>();
    private Command command;

    //设置需要执行的命令
    public void setCommand(Command command){
        this.command = command;
        map.put("a",command);
    }

    public void action(){
        command.execute();
    }

    public static void main(String[] args) {
        Command command = new ConcreteCommand("张三");
        Invoker invoker = new Invoker();
        invoker.setCommand(command);
        invoker.action();
        List<Command> list = new ArrayList<>();
        list.add(command);
        String json = JSON.toJSON(list).toString();
        System.out.println(json);

        invoker.setCommand(Invoker.map.get("a"));
        invoker.action();
    }
}
