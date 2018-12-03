package com.example.demo.map2object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: czw
 * @Date: 2018-12-03 09:41
 **/
public class ChangeUtil {

    /**
     * map转obj
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) {
        if (map == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = beanClass.newInstance();
            org.apache.commons.beanutils.BeanUtils.populate(obj, map);
        } catch (Exception e){
            e.printStackTrace();
            return obj;
        }
        return obj;
    }

    /**
     * obj转map
     * @param obj
     * @return
     */
    public static Map<?, ?> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return new org.apache.commons.beanutils.BeanMap(obj);
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.setAgeAge(1);
        test.setName("张三");
        List<String> list = new ArrayList<>();
        list.add("啊");
        list.add("f");
        Map<String,Object> map = (Map<String, Object>) ChangeUtil.objectToMap(test);
        for (Object s : map.keySet()){
            System.out.println(s);
        }
        map.put("ageAge","2");
        map.put("list",list);
        Test test2 = (Test) ChangeUtil.mapToObject(map,Test.class);
        System.out.println(test2);

    }

}
