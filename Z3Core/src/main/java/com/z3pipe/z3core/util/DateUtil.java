package com.z3pipe.z3core.util;

import com.z3pipe.z3core.config.DateStyle;
import com.z3pipe.z3core.config.Week;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: ${TODO}
 * Date: 2019-04-13
 * Time: 17:19
 * Copyright © 2018 ZiZhengzhuan. All rights reserved.
 * https://www.z3pipe.com
 *
 * @author zhengzhuanzi
 */
public class DateUtil {
    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL = new ThreadLocal();
    private static final Object OBJECT = new Object();

    private static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {
        SimpleDateFormat dateFormat = (SimpleDateFormat) THREAD_LOCAL.get();
        if (dateFormat == null) {
            synchronized (OBJECT) {
                dateFormat = new SimpleDateFormat(pattern);
                dateFormat.setLenient(false);
                THREAD_LOCAL.set(dateFormat);
            }
        }
        dateFormat.applyPattern(pattern);
        return dateFormat;
    }

    private static int getInteger(Date date, int dateType) {
        int num = 0;
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
            num = calendar.get(dateType);
        }
        return num;
    }

    private static String addInteger(String date, int dateType, int amount) {
        String dateString = null;
        DateStyle dateStyle = getDateStyle(date);
        if (dateStyle != null) {
            Date myDate = stringToDate(date, dateStyle);
            myDate = addInteger(myDate, dateType, amount);
            dateString = dateToString(myDate, dateStyle);
        }
        return dateString;
    }

    private static Date addInteger(Date date, int dateType, int amount) {
        Date myDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(dateType, amount);
            myDate = calendar.getTime();
        }
        return myDate;
    }

    private static Date getAccurateDate(List<Long> timestamps) {
        Date date = null;
        long timestamp = 0L;
        Map<Long, long[]> map = new HashMap();
        List<Long> absoluteValues = new ArrayList();
        if ((timestamps != null) && (timestamps.size() > 0)) {
            if (timestamps.size() > 1) {
                for (int i = 0; i < timestamps.size(); i++) {
                    for (int j = i + 1; j < timestamps.size(); j++) {
                        long absoluteValue = Math.abs(((Long) timestamps.get(i)).longValue()
                                - ((Long) timestamps.get(j)).longValue());
                        absoluteValues.add(Long.valueOf(absoluteValue));
                        long[] timestampTmp = {((Long) timestamps.get(i)).longValue(),
                                ((Long) timestamps.get(j)).longValue()};
                        map.put(Long.valueOf(absoluteValue), timestampTmp);
                    }
                }
                long minAbsoluteValue = -1L;
                if (!absoluteValues.isEmpty()) {
                    minAbsoluteValue = ((Long) absoluteValues.get(0)).longValue();
                    for (int i = 1; i < absoluteValues.size(); i++) {
                        if (minAbsoluteValue > ((Long) absoluteValues.get(i)).longValue()) {
                            minAbsoluteValue = ((Long) absoluteValues.get(i)).longValue();
                        }
                    }
                }
                if (minAbsoluteValue != -1L) {
                    long[] timestampsLastTmp = (long[]) map.get(Long.valueOf(minAbsoluteValue));

                    long dateOne = timestampsLastTmp[0];
                    long dateTwo = timestampsLastTmp[1];
                    if (absoluteValues.size() > 1) {
                        timestamp = Math.abs(dateOne) > Math.abs(dateTwo) ? dateOne : dateTwo;
                    }
                }
            } else {
                timestamp = ((Long) timestamps.get(0)).longValue();
            }
        }
        if (timestamp != 0L) {
            date = new Date(timestamp);
        }
        return date;
    }

    public static boolean isDate(String date) {
        boolean isDate = false;
        if ((date != null) && (getDateStyle(date) != null)) {
            isDate = true;
        }
        return isDate;
    }

    public static DateStyle getDateStyle(String date) {
        DateStyle dateStyle = null;
        Map<Long, DateStyle> map = new HashMap();
        List<Long> timestamps = new ArrayList();
        for (DateStyle style : DateStyle.values()) {
            if (!style.isShowOnly()) {
                Date dateTmp = null;
                if (date != null) {
                    try {
                        ParsePosition pos = new ParsePosition(0);
                        dateTmp = getDateFormat(style.getValue()).parse(date, pos);
                        if (pos.getIndex() != date.length()) {
                            dateTmp = null;
                        }
                    } catch (Exception localException) {
                    }
                }
                if (dateTmp != null) {
                    timestamps.add(Long.valueOf(dateTmp.getTime()));
                    map.put(Long.valueOf(dateTmp.getTime()), style);
                }
            }
        }
        Date accurateDate = getAccurateDate(timestamps);
        if (accurateDate != null) {
            dateStyle = (DateStyle) map.get(Long.valueOf(accurateDate.getTime()));
        }
        return dateStyle;
    }

    public static Date stringToDate(String date) {
        DateStyle dateStyle = getDateStyle(date);
        return stringToDate(date, dateStyle);
    }

    public static Date stringToDate(String date, String pattern) {
        Date myDate = null;
        if (date != null) {
            try {
                myDate = getDateFormat(pattern).parse(date);
            } catch (Exception localException) {
            }
        }
        return myDate;
    }

    public static Date stringToDate(String date, DateStyle dateStyle) {
        Date myDate = null;
        if (dateStyle != null) {
            myDate = stringToDate(date, dateStyle.getValue());
        }
        return myDate;
    }

    public static String dateToString(Date date, String pattern) {
        String dateString = null;
        if (date != null) {
            try {
                dateString = getDateFormat(pattern).format(date);
            } catch (Exception localException) {
            }
        }
        return dateString;
    }

    public static String dateToString(Date date, DateStyle dateStyle) {
        String dateString = null;
        if (dateStyle != null) {
            dateString = dateToString(date, dateStyle.getValue());
        }
        return dateString;
    }

    public static String stringToString(String date, String newPattern) {
        DateStyle oldDateStyle = getDateStyle(date);
        return stringToString(date, oldDateStyle, newPattern);
    }

    public static String stringToString(String date, DateStyle newDateStyle) {
        DateStyle oldDateStyle = getDateStyle(date);
        return stringToString(date, oldDateStyle, newDateStyle);
    }

    public static String stringToString(String date, String olddPattern, String newPattern) {
        return dateToString(stringToDate(date, olddPattern), newPattern);
    }

    public static String stringToString(String date, DateStyle olddDteStyle, String newParttern) {
        String dateString = null;
        if (olddDteStyle != null) {
            dateString = stringToString(date, olddDteStyle.getValue(), newParttern);
        }
        return dateString;
    }

    public static String stringToString(String date, String olddPattern, DateStyle newDateStyle) {
        String dateString = null;
        if (newDateStyle != null) {
            dateString = stringToString(date, olddPattern, newDateStyle.getValue());
        }
        return dateString;
    }

    public static String stringToString(String date, DateStyle olddDteStyle, DateStyle newDateStyle) {
        String dateString = null;
        if ((olddDteStyle != null) && (newDateStyle != null)) {
            dateString = stringToString(date, olddDteStyle.getValue(), newDateStyle.getValue());
        }
        return dateString;
    }

    public static String addYear(String date, int yearAmount) {
        return addInteger(date, 1, yearAmount);
    }

    public static Date addYear(Date date, int yearAmount) {
        return addInteger(date, 1, yearAmount);
    }

    public static String addMonth(String date, int monthAmount) {
        return addInteger(date, 2, monthAmount);
    }

    public static Date addMonth(Date date, int monthAmount) {
        return addInteger(date, 2, monthAmount);
    }

    public static String addDay(String date, int dayAmount) {
        return addInteger(date, 5, dayAmount);
    }

    public static Date addDay(Date date, int dayAmount) {
        return addInteger(date, 5, dayAmount);
    }

    public static String addHour(String date, int hourAmount) {
        return addInteger(date, 11, hourAmount);
    }

    public static Date addHour(Date date, int hourAmount) {
        return addInteger(date, 11, hourAmount);
    }

    public static String addMinute(String date, int minuteAmount) {
        return addInteger(date, 12, minuteAmount);
    }

    public static Date addMinute(Date date, int minuteAmount) {
        return addInteger(date, 12, minuteAmount);
    }

    public static String addSecond(String date, int secondAmount) {
        return addInteger(date, 13, secondAmount);
    }

    public static Date addSecond(Date date, int secondAmount) {
        return addInteger(date, 13, secondAmount);
    }

    public static int getYear(String date) {
        return getYear(stringToDate(date));
    }

    public static int getYear(Date date) {
        return getInteger(date, 1);
    }

    public static int getMonth(String date) {
        return getMonth(stringToDate(date));
    }

    public static int getMonth(Date date) {
        return getInteger(date, 2) + 1;
    }

    public static int getDay(String date) {
        return getDay(stringToDate(date));
    }

    public static int getDay(Date date) {
        return getInteger(date, 5);
    }

    public static int getHour(String date) {
        return getHour(stringToDate(date));
    }

    public static int getHour(Date date) {
        return getInteger(date, 11);
    }

    public static int getMinute(String date) {
        return getMinute(stringToDate(date));
    }

    public static int getMinute(Date date) {
        return getInteger(date, 12);
    }

    public static int getSecond(String date) {
        return getSecond(stringToDate(date));
    }

    public static int getSecond(Date date) {
        return getInteger(date, 13);
    }

    public static String getDate(String date) {
        return stringToString(date, DateStyle.YYYY_MM_DD);
    }

    public static String getDate(Date date) {
        return dateToString(date, DateStyle.YYYY_MM_DD);
    }

    public static String getTime(String date) {
        return stringToString(date, DateStyle.HH_MM_SS);
    }

    public static String getTime(Date date) {
        return dateToString(date, DateStyle.HH_MM_SS);
    }

    public static Week getWeek(String date) {
        Week week = null;
        DateStyle dateStyle = getDateStyle(date);
        if (dateStyle != null) {
            Date myDate = stringToDate(date, dateStyle);
            week = getWeek(myDate);
        }
        return week;
    }

    public static Week getWeek(Date date) {
        Week week = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekNumber = calendar.get(7) - 1;
        switch (weekNumber) {
            case 0:
                week = Week.SUNDAY;
                break;
            case 1:
                week = Week.MONDAY;
                break;
            case 2:
                week = Week.TUESDAY;
                break;
            case 3:
                week = Week.WEDNESDAY;
                break;
            case 4:
                week = Week.THURSDAY;
                break;
            case 5:
                week = Week.FRIDAY;
                break;
            case 6:
                week = Week.SATURDAY;
                break;
            default:
                break;
        }
        return week;
    }

    public static int getIntervalDays(String date, String otherDate) {
        return getIntervalDays(stringToDate(date), stringToDate(otherDate));
    }

    public static int getIntervalDays(Date date, Date otherDate) {
        int num = -1;
        Date dateTmp = stringToDate(getDate(date), DateStyle.YYYY_MM_DD);
        Date otherDateTmp = stringToDate(getDate(otherDate), DateStyle.YYYY_MM_DD);
        if ((dateTmp != null) && (otherDateTmp != null)) {
            long time = Math.abs(dateTmp.getTime() - otherDateTmp.getTime());
            num = (int) (time / 86400000L);
        }
        return num;
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    /**
     * 传入一个日期，获取该日期当天的零点零分零秒。
     *
     * @param date 指定日期
     * @return
     */
    public static Date toBeginOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 00);

        return calendar.getTime();
    }

    /**
     * 传入一个日期，获取该日期当天的23点59分59秒。
     *
     * @param date 指定日期
     * @return
     */
    public static Date toEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 00);

        return calendar.getTime();
    }


    /**
     * 传入月份获取天数
     *
     * @param month
     * @return
     */
    public static int getMonthDays(String month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, Integer.valueOf(month) - 1);

        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 传入年份月份获取周数
     *
     * @param month
     * @return
     */
    public static int getYeraMonthWeeks(String year, String month) {
        Calendar c = Calendar.getInstance();
        // 2010年
        c.set(Calendar.YEAR, Integer.valueOf(year));
        // 6 月
        c.set(Calendar.MONTH, Integer.valueOf(month));

        return c.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    public static List<Date> getDatesBetweenTwoDate(String beginDate, String endDate) {
        if (null == beginDate || "".equalsIgnoreCase(beginDate)) {
            beginDate = getDate(new Date());
        }
        if (null == endDate || "".equalsIgnoreCase(endDate)) {
            endDate = getDate(new Date());
        }
        List<Date> dateList = new ArrayList<>();
        //把开始时间加入集合
        dateList.add(stringToDate(beginDate, "yyyy-MM-dd"));
        if (beginDate.equals(endDate)) {
            return dateList;
        }
        Calendar cal = Calendar.getInstance();
        //使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(stringToDate(beginDate, "yyyy-MM-dd"));
        while (true) {
            //根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (stringToDate(endDate, "yyyy-MM-dd").after(cal.getTime())) {
                dateList.add(cal.getTime());
            } else {
                break;
            }
        }
        //把结束时间加入集合
        dateList.add(stringToDate(endDate, "yyyy-MM-dd"));
        return dateList;
    }
}
