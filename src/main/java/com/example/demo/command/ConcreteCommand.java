package com.example.demo.command;

import com.example.demo.command.Command;
import com.fasterxml.jackson.databind.annotation.JsonAppend;

import java.io.Serializable;

/**
 * 命令实现类
 * @author: czw
 * @create: 2018-12-12 21:47
 **/
public class ConcreteCommand implements Command, Serializable {
    private String name ;

    public ConcreteCommand(String name) {
        this.name = name;
    }

    @Override
    public void execute() {
        System.out.println(name);
    }
}
