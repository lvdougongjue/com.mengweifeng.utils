package com.mengweifeng.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期工具类
 * 
 * @author mwf
 *
 */
public class DateUtil {
	private static final Logger log = LoggerFactory.getLogger(DateUtil.class);
	private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMdd");
		}
	};

	// 计时间隔
	private static final int TICKMILLIS = 1000;

	// 毫秒时间
	private static volatile long time;

	// GMT时间
	private static volatile String gmt;

	private static volatile String nextHourCookieGmt;

	// 年月日时分秒
	private static volatile int yyyyMMdd;
	private static volatile int year;
	private static volatile int month;
	private static volatile int date;
	private static volatile int hour;
	private static volatile int minute;
	private static volatile int second;

	private static volatile int dayofyear;

	// 星期(0 - 6表示星期一到日)
	private static volatile int week;

	// 当前小时还剩下多少秒
	private static volatile int hourLeftSeconds;

	// 今天还剩下多少秒
	private static volatile int dayLeftSeconds;

	// 计时器
	private static Timer timer = new HashWheelTimer();
	static {
		init();
	}

	// 初始化
	private static void init() {

		// 日期格式化器
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

		final SimpleDateFormat gmtFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
		gmtFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));

		final SimpleDateFormat cookieGmtFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.ENGLISH);
		cookieGmtFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));

		// 初始化时间
		time = System.currentTimeMillis();
		long now = Long.parseLong(format.format(time));
		gmt = gmtFormat.format(time);
		nextHourCookieGmt = cookieGmtFormat.format(time + 3600000);
		yyyyMMdd = (int) (now / 1000000L);
		year = (int) (now / 10000000000L);
		month = (int) ((now % 10000000000L) / 100000000L);
		date = (int) ((now % 100000000L) / 1000000L);
		hour = (int) ((now % 1000000L) / 10000L);
		minute = (int) ((now % 10000L) / 100L);
		second = (int) ((now % 100L));
		hourLeftSeconds = 60 * (59 - minute) + 60 - second;
		dayLeftSeconds = 3600 * (23 - hour) + hourLeftSeconds;

		// 初始化week
		Calendar c = Calendar.getInstance();
		dayofyear = c.get(Calendar.DAY_OF_YEAR);
		c.setTimeInMillis(time);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

		// dayOfWeek用1-7表示周日到周六, week用0-6表示周一到周日
		week = dayOfWeek - 2;
		if (week < 0)
			week = 6;

		// 用计时器每秒更新时间
		timer.timing(new TimerTask() {

			// 间隔,触发方式,任务类型
			public long delayOrIntervalMillis() {
				return TICKMILLIS;
			}

			public boolean isTriggerIndependently() {
				return false;
			}

			public Type type() {
				return Type.INTERVAL;
			}

			/**
			 * (non-Javadoc)
			 *
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				time = System.currentTimeMillis();
				long now = Long.parseLong(format.format(time));
				gmt = gmtFormat.format(time);
				nextHourCookieGmt = cookieGmtFormat.format(time + 3600000);
				yyyyMMdd = (int) (now / 1000000L);
				int t = (int) (now / 10000000000L);
				if (t != year) {
					dayofyear = 1;
				}
				year = t;
				month = (int) ((now % 10000000000L) / 100000000L);
				t = (int) ((now % 100000000L) / 1000000L);
				if (t != date) {

					// 日期发生了变化,更新week
					week = week == 6 ? 0 : week + 1;
				}
				date = t;
				hour = (int) ((now % 1000000L) / 10000L);
				minute = (int) ((now % 10000L) / 100L);
				second = (int) ((now % 100L));
				hourLeftSeconds = 60 * (59 - minute) + 60 - second;
				dayLeftSeconds = 3600 * (23 - hour) + hourLeftSeconds;
			}
		});
	}

	/**
	 * @return the time
	 */
	public static long getTime() {
		return time;
	}

	/**
	 * @return the yyyyMMdd
	 */
	public static int getYyyyMMdd() {
		return yyyyMMdd;
	}

	/**
	 * 获取当前时间戳的年
	 * 
	 * @return the year
	 */
	public static int getYear() {
		return year;
	}

	/**
	 * 获取当前时间戳的月
	 * 
	 * @return the month
	 */
	public static int getMonth() {
		return month;
	}

	/**
	 * 获取当前时间戳的日
	 * 
	 * @return the date
	 */
	public static int getDate() {
		return date;
	}

	/**
	 * 获取当前时间戳的小时
	 * 
	 * @return the hour
	 */
	public static int getHour() {
		return hour;
	}

	/**
	 * 获取当前时间戳的分钟
	 * 
	 * @return the minute
	 */
	public static int getMinute() {
		return minute;
	}

	/**
	 * 获取当前时间戳的秒
	 * 
	 * @return
	 */
	public static int getSecond() {
		return second;
	}

	/**
	 * 获取当前小时剩余的秒数
	 * 
	 * @return the hourLeftSeconds
	 */
	public static int getHourLeftSeconds() {
		return hourLeftSeconds;
	}

	/**
	 * 获取当天剩余的秒数
	 * 
	 * @return
	 */
	public static int getDayLeftSeconds() {
		return dayLeftSeconds;
	}

	/**
	 * @return the week
	 */
	public static int getWeek() {
		return week;
	}

	/**
	 * @return the gmt
	 */
	public static String getGmt() {
		return gmt;
	}

	public static int getDayofyear() {
		return dayofyear;
	}

	/**
	 * @return the nextHourGmt
	 */
	public static String getNextHourCookieGmt() {
		return nextHourCookieGmt;
	}

	/**
	 * 获取时分秒时间字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getHhMsSs(Date date) {
		if (null == date) {
			date = new Date();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String hms = sdf.format(date);
		return hms;
	}

	/**
	 * 将日期字符串转为int
	 * 
	 * @param dateStr
	 * @return
	 */
	public static int getDateInt(String dateStr) {
		return Integer.parseInt(dateStr);
	}

	/**
	 * 将日期转为int
	 * 
	 * @param date
	 * @return
	 */
	public static int getDateInt(Date date) {
		String dateStr = getDateStr(date);
		return getDateInt(dateStr);
	}

	/**
	 * 获取两个时间相差的天数
	 * 
	 * @param dateStr1
	 * @param dateStr2
	 * @return
	 */
	public static int getDiffDays(String dateStr1, String dateStr2) {
		Date date1 = getDate(dateStr1);
		Date date2 = getDate(dateStr2);
		int diffDays = getDiffDays(date1, date2);
		return diffDays;
	}

	/**
	 * 获取两个时间相差的天数 比较第二个日期比第一个日期大多少天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getDiffDays(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new RuntimeException("输入参数为null");
		}
		Long day = null;
		day = (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);

		return Integer.parseInt(day.toString());
	}

	/**
	 * 时间字符串转为事件对象
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date getDate(String dateStr) {
		SimpleDateFormat sdf = threadLocal.get();
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			log.warn("解析时间字符串[" + dateStr + "]失败");
		}
		return date;
	}

	/**
	 * 时间对象转为时间字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDateStr(Date date) {
		SimpleDateFormat sdf = threadLocal.get();
		return sdf.format(date);
	}

	/**
	 * 将date增加或减少一定天数
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextDate(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		return calendar.getTime();
	}

	/**
	 * 获取日期所在月份的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int maxDays = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DAY_OF_MONTH, maxDays);
		return calendar.getTime();
	}

	/**
	 * 获取日期所在月份的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * 获取日期所在周的周一
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return calendar.getTime();
	}

	/**
	 * 获取日期所在周的周日
	 * 
	 * @param date
	 * @return
	 */
	public static Date getSunday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return calendar.getTime();
	}

	/**
	 * 获取是周几 <br>
	 * 周一的值是2，周二的值是3，以此类推，周日的值是1 <br>
	 * 日期类一周开始的日期是周日
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 是否是周一
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isMonday(Date date) {
		int dayOfWeek = getDayOfWeek(date);
		return dayOfWeek == Calendar.MONDAY;
	}

	/**
	 * 是否是周日
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isSunday(Date date) {
		int dayOfWeek = getDayOfWeek(date);
		return dayOfWeek == Calendar.SUNDAY;
	}

	/**
	 * 获取是本月的第几天
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取是本年的第几天
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * 获取上个月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getPreviousMonthFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * 获取上个月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getPreviousMonthLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int month = calendar.get(Calendar.MONTH);
		calendar.set(Calendar.MONTH, month - 1);
		int maxDays = calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DAY_OF_MONTH, maxDays);
		return calendar.getTime();
	}

	/**
	 * 获取日期所在月份 <br>
	 * 月份从0开始， 0表示一月，11表示十二月
	 * 
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH);
	}

	/**
	 * 判断是否是某个月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isMonthFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return day == 1;
	}

	/**
	 * 判断是否是某个月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isMonthLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int maxDays = calendar.getActualMaximum(Calendar.DATE);
		return day == maxDays;
	}

	/**
	 * 是否是今天
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isToday(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar today = Calendar.getInstance();
		return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
				&& today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
	}

	public static String getTodayStr() {
		return yyyyMMdd + "";
	}

	/**
	 * 返回当天的数字格式
	 * 
	 * @return
	 */
	public static Integer getTodayInt() {
		return yyyyMMdd;
	}

	/**
	 * 获取当天0时0分0秒的时间戳
	 * 
	 * @return
	 */
	public static Date getToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 得到时
	 * 
	 * @return
	 */
	public static int getHourByDay() {
		return hour;
	}

	/**
	 * 获取星期几
	 * 
	 * @return
	 */
	public static int getWeekDayInt() {
		return week + 1;
	}

	public static int getWeekDayInt(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int index = calendar.get(Calendar.DAY_OF_WEEK);
		index = index - 1;
		if (index == 0) {
			index = 7;
		}
		return index;
	}

	public static int getMonthDayInt(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int index = calendar.get(Calendar.DAY_OF_MONTH);
		return index;
	}

	public static Date getYestoday() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_YEAR, -1);
		return now.getTime();
	}

	public static String getYestodayStr() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_YEAR, -1);
		SimpleDateFormat sdf = threadLocal.get();
		return sdf.format(now.getTime());
	}

	/**
	 * 获取当天剩余多少秒 <br>
	 * 
	 * @return
	 */
	public static Integer getTodayRemainingSecond() {
		return dayLeftSeconds;
	}

}
