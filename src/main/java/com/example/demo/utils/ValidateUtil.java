package com.example.demo.utils;

import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: czw
 * @create: 2018-12-30 22:58
 **/
public class ValidateUtil {

    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String ONE_STR = "1";

    public static final String ZERO_STR = "0";

    public static boolean isValidDate(String str,String simpleDateFormat) {
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(simpleDateFormat);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String value){
        try {
            Double.parseDouble(value);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean isBoolean(String value) {
        try {
            Boolean.parseBoolean(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean regex(String regex, String value) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(value);
        return m.find();
    }

    public static boolean checkMobile(String value) {
        // 手机号码 11位
        String regex = "\\d{11}";
        return regex(regex, value);
    }

    public static boolean checkEmail(String value) {
        String regex = "[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+";
        return regex(regex, value);
    }

    public static <T> boolean checkListIsNotEmpty(List<T> list) {
        if(list!=null&&!list.isEmpty()){
            return true;
        }
        return false;
    }

    public static  boolean checkMapIsNotEmpty(Map map) {
        if(map!=null&&!map.isEmpty()){
            return true;
        }
        return false;
    }

    public static <T> boolean checkSetIsNotEmpty(Set<T> set) {
        if(set!=null&&!set.isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * 获取时间差
     * @param startTime
     * @param endTime
     * @return
     */
    public static long getTimeDifference(String startTime,String endTime){
        SimpleDateFormat inputFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate=inputFormat.parse(startTime);
            endDate=inputFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        //得到两个日期对象的总毫秒数
        long firstDateMilliSeconds = startDate.getTime();
        long secondDateMilliSeconds = endDate.getTime();
        //得到秒
        long totalSeconds = ((secondDateMilliSeconds - firstDateMilliSeconds) / 1000);
        return totalSeconds >= 0 ? totalSeconds : -1;
    }

    public static long getTimeDifference(Date startTime,String endTime){
        SimpleDateFormat inputFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
        Date endDate = null;
        try {
            endDate=inputFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        //得到两个日期对象的总毫秒数
        long firstDateMilliSeconds = startTime.getTime();
        long secondDateMilliSeconds = endDate.getTime();
        //得到秒
        long totalSeconds = ((secondDateMilliSeconds - firstDateMilliSeconds) / 1000);
        return totalSeconds >= 0 ? totalSeconds : -1;
    }

    /**
     * 获取当天指定时间的String
     * @param hour
     * @param minute
     * @param second
     * @param format
     * @return
     */
    public static String getTodayTimeByHourAndMinAndSecond(int hour,int minute,int second,String format){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        long time = cal.getTimeInMillis();
        SimpleDateFormat inputFormat = new SimpleDateFormat(format);
        return inputFormat.format(new Date(time));
    }


    public static String getUUID(){
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-","");
        return uuid;
    }

    /**
     * 获取指定时间往后一天是时间
     * @param time
     * @return
     */
    public static String getAfterTimeDay(String time,String dateFormat){
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(dateFormat).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        c.setTime(date);
        int newDay = c.get(Calendar.DATE);
        c.set(Calendar.DATE, newDay + 1);
        String dayAfter = new SimpleDateFormat(dateFormat).format(c.getTime());
        return dayAfter;
    }

    /**
     * 获取指定天数后的日期
     * @param time
     * @param dateFormat
     * @param days
     * @return
     */
    public static String getAfterTimeDay(String time,String dateFormat,int days){
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(dateFormat).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        c.setTime(date);
        int newDay = c.get(Calendar.DATE);
        c.set(Calendar.DATE, newDay + days);
        String dayAfter = new SimpleDateFormat(dateFormat).format(c.getTime());
        return dayAfter;
    }

    public static String getTodayStr(String format){
        Date date = new Date();
        //设置要获取到什么样的时间
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //获取String类型的时间
        String createdate = sdf.format(date);
        return createdate;
    }

    /**
     * 获取从当天起指定天数的日期
     * @param days
     * @return
     */
    public static List<String> getDateListByDays(int days,String dateFormat) {
        if (days <= 0) {
            return null;
        }
        List<String> result = new ArrayList<String>(days);
        String timeStr = ValidateUtil.getTodayStr(dateFormat);
        result.add(timeStr);
        for (int i = 1; i < days; i++) {
            String str = ValidateUtil.getAfterTimeDay(timeStr, dateFormat, i);
            if (StringUtils.isEmpty(str)) {
                continue;
            }
            result.add(str);
        }
        return result;
    }

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

    /**
     * 是否包含中文
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
    public static String getDateFormatter(){
        Date date=new Date();
        DateFormat df=new SimpleDateFormat(YYYY_MM_DD_HH_MM);
        return df.format(date);
    }

}
