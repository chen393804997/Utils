package com.example.demo.map2object;

import java.util.List;
import java.util.Map;

/**
 * @author: czw
 * @Date: 2018-12-03 09:39
 **/
public class Test {

    private String name;

    private Integer ageAge;

    private List<String> list;

    private Map<String,String> map;


    public Test() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAgeAge() {
        return ageAge;
    }

    public void setAgeAge(Integer ageAge) {
        this.ageAge = ageAge;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "Test{" +
                "name='" + name + '\'' +
                ", age=" + ageAge +
                ", list=" + list +
                ", map=" + map +
                '}';
    }
}
