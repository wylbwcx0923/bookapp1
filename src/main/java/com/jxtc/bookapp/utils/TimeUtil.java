package com.jxtc.bookapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 获得当前年月日的工具类
 */
public class TimeUtil {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 获取当前的年月日
     *
     * @return
     */
    public static Map<String, Integer> getTime() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Map<String, Integer> map = new HashMap<>();
        map.put("year", calendar.get(Calendar.YEAR));
        map.put("month", calendar.get(Calendar.MONTH) + 1);
        map.put("day", calendar.get(Calendar.DAY_OF_MONTH));
        return map;
    }

    /**
     * 获取当月的表的索引
     *
     * @return
     */
    public static Integer getTableIndex() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        int tableIndex = Integer.valueOf(sdf.format(date));
        return tableIndex;
    }

    /**
     * 获得当前的时间戳
     *
     * @return
     */
    public static String getTimestamp() {
        return System.currentTimeMillis() + "";
    }

    /**
     * 获取年月日
     *
     * @return
     */
    public static String getYearMonthDay(Date date) {
        if (sdf != null) {
            return sdf.format(date);
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
