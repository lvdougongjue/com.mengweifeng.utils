package com.mengweifeng.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * 系统时间工具类<br>
 * 该工具类用于替代System.currentTimeMillis()方法，提高性能
 * @author MengWeiFeng
 */
public final class SystemTime {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

    static {
        parseCurrentTime();

        new Thread(() -> {
            try {
                for (; ; ) {
                    TimeUnit.MILLISECONDS.sleep(1);
                    parseCurrentTime();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void parseCurrentTime() {
        Calendar c = Calendar.getInstance();

        long currentMillisecond = c.getTimeInMillis();
        long now = Long.parseLong(format.format(currentMillisecond));
        millisecond = currentMillisecond;
        yyyyMMdd = (int) (now / 1000000L);
        yyyyMMddHH = (int) (now / 10000L);
        year = (int) (now / 10000000000L);
        month = (int) ((now % 10000000000L) / 100000000L);
        day = (int) ((now % 100000000L) / 1000000L);
        hour = (int) ((now % 1000000L) / 10000L);
        minute = (int) ((now % 10000L) / 100L);
        second = (int) ((now % 100L));
        dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    }

    // 年月日时分秒
    private static volatile int yyyyMMdd;
    private static volatile int yyyyMMddHH;
    private static volatile int year;
    private static volatile int month;
    private static volatile int day;
    private static volatile int hour;
    private static volatile int minute;
    private static volatile int second;
    /**
     * 当前毫秒数，不精准的数据，偶尔会有几毫秒的误差
     */
    private static volatile long millisecond;

    private static volatile int dayOfYear;
    private static volatile int dayOfMonth;
    private static volatile int dayOfWeek;

    public static int getYyyyMMdd() {
        return yyyyMMdd;
    }

    public static int getYyyyMMddHH() {
        return yyyyMMddHH;
    }

    public static int getYear() {
        return year;
    }

    public static int getMonth() {
        return month;
    }

    public static int getDay() {
        return day;
    }

    public static int getHour() {
        return hour;
    }

    public static int getMinute() {
        return minute;
    }

    public static int getSecond() {
        return second;
    }

    public static long getMillisecond() {
        return millisecond;
    }

    public static int getDayOfYear() {
        return dayOfYear;
    }

    public static int getDayOfMonth() {
        return dayOfMonth;
    }

    public static int getDayOfWeek() {
        return dayOfWeek;
    }

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 100; i++) {
            System.out.println(getMillisecond() + "\t" + System.currentTimeMillis());
            Thread.sleep(2000);
        }

    }

}
