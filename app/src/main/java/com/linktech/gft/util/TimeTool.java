package com.linktech.gft.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间处理的工具类
 *
 * @author YIDA
 */
public class TimeTool {
    public static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");//年份格式
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");//日期格式
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");//时间时间
    public static final SimpleDateFormat DATE_AND_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//完整日期加时间格式

    /**
     * 查当前日期是一周中的星期几
     *
     * @return 1=Sunday,,,7=Saturday
     */
    public static long getWhicthDay(Date today) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        return cal.get(Calendar.DAY_OF_WEEK);// 1=Sunday,,,7=Saturday
    }

    /**
     * 获得符合中国习惯的星期几
     *
     * @param theDate
     * @return
     */
    public static long getChieseWhicthDay(Date theDate) {
        long week = getWhicthDay(theDate);
        if (week == Calendar.SUNDAY) return 7;
        else return week - 1;
    }

    /**
     * 获取某个日期对应的星期几
     *
     * @param theDate
     * @param week    星期数 1为星期日,类推
     * @return Date
     */
    public static Date whicthDayOfSomeDate(Date theDate, int week) {
        if (week == 1) return getSundayOfThisWeek(theDate); //如果求某个日期对应的星期日,调用星期日的逻辑
        Calendar c = Calendar.getInstance();
        if (theDate != null) c.setTime(theDate);
        c.set(Calendar.DAY_OF_WEEK, week); // 获取 周一
        if (getWhicthDay(theDate) == 1) { //输入日期为星期日
            c.add(Calendar.DATE, -7);
        }
        return c.getTime();
    }

    /**
     * 获取某个日期对应的 日期 的周日
     *
     * @param theDate 某个日期 null的话，表示当前日期
     *                获取当前日期的周7,注意 日历获取的周期是
     *                周日,1,2,3,4,5,6  不是中国传统的
     *                1,2,3,4,5,6,周日
     *                所以要加7
     * @return Date
     */
    public static Date getSundayOfThisWeek(Date theDate) {
        Calendar c = Calendar.getInstance();
        if (theDate != null) c.setTime(theDate);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);               // 获取 周日
        c.add(Calendar.DAY_OF_MONTH, 7);     // 获取 中国意义上的 周日
        if (getWhicthDay(theDate) == 1) {  //输入日期为星期日
            c.add(Calendar.DATE, c.getFirstDayOfWeek() - 8);
        }
        return c.getTime();
    }

    /**
     * 字符串转换为日期类型
     * string-->yyyy-MM-dd HH:mm:ss
     *
     * @param dateString
     * @return
     */
    public static Date stringToDate(String dateString) {
        try {
            return DATE_AND_TIME_FORMAT.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 日期类型转化为字符串
     * yyyy-MM-dd HH:mm:ss -->string
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        try {
            return DATE_AND_TIME_FORMAT.format(date);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return date.toString();
    }

    /**
     * yyyy-MM-dd HH:mm:ss --->yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static Date timeToDate(Date date) {
        String s = DATE_FORMAT.format(date);
        try {
            return DATE_FORMAT.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * yyyy-MM-dd HH:mm:ss --->HH:mm:ss
     *
     * @param date
     * @return
     */
    public static Date dateToOnlyTime(Date date) {
        String s = TIME_FORMAT.format(date);
        try {
            return TIME_FORMAT.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * yyyy-MM-dd HH:mm:ss --->HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String dateToOnlyTime(String date) {
        try {
            String s = TIME_FORMAT.format(stringToDate(date));
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * yyyy-MM-dd+HH:mm:ss = yyyy-MM-dd HH:mm:ss
     *
     * @param date yyyy-MM-dd
     * @param time HH:mm:ss
     * @return
     */
    public static Date groupDateAndTime(Date date, Date time) {
        Calendar c1 = Calendar.getInstance();//yyyy-MM-dd
        c1.setTime(date);
        Calendar c2 = Calendar.getInstance();//HH:mm:ss
        c2.setTime(time);
        Calendar group = Calendar.getInstance();//yyyy-MM-dd HH:mm:ss
        group.set(Calendar.YEAR, c1.get(Calendar.YEAR));
        group.set(Calendar.MONTH, c1.get(Calendar.MONTH));
        group.set(Calendar.DATE, c1.get(Calendar.DATE));
        group.set(Calendar.HOUR_OF_DAY, c2.get(Calendar.HOUR));
        group.set(Calendar.MINUTE, c2.get(Calendar.MINUTE));
        group.set(Calendar.SECOND, c2.get(Calendar.SECOND));
        String s = DATE_AND_TIME_FORMAT.format(group.getTime()); //经过一次转化为标准格式的处理
        try {
            return DATE_AND_TIME_FORMAT.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
