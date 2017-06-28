package com.leadingsoft.liuw.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by liuw on 2016/11/24.
 */
public class DateTimeUtil {

    /**
     * 转换为当天日期的凌晨00:00:00
     * @param date
     * @return
     */
    public final static Date toZeroTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 转换为第二天日期的凌晨00:00:00
     * @param date
     * @return
     */
    public final static Date toNextDayZeroTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 转换为当月1日的凌晨00:00:00
     * @param date
     * @return
     */
    public final static Date toZeroTimeOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * 日期偏移分钟数
     * @param date
     * @param minuteCount
     * @return
     */
    public final static Date addMinutes(Date date, int minuteCount){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minuteCount);

        return calendar.getTime();
    }

    /**
     * 日期偏移天数
     * @param date
     * @param dayCount
     * @return
     */
    public final static Date addDays(Date date, int dayCount){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, dayCount);

        return calendar.getTime();
    }

    /**
     * 日期偏移月数
     * @param date
     * @param monthCount
     * @return
     */
    public final static Date addMonths(Date date, int monthCount){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, monthCount);

        return calendar.getTime();
    }

    /**
     * 日期格式化
     * @param date
     * @return
     */
    public final static String formatDate(Date date, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
}
