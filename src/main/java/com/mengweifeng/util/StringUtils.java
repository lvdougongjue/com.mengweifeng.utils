package com.mengweifeng.util;

/**
 * 字符串工具类
 * 
 * @author MengWeiFeng
 *
 */
public class StringUtils {
	private StringUtils() {
	}

	/**
	 * 判断输入字符串是否为空
	 * 
	 * @param s
	 *            待判断的字符串
	 * @return 是否为空
	 */
	public final static boolean isEmpty(String s) {
		if (s == null || s.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断输入字符串是否不为空
	 *
	 * @param s
	 *            待判断的字符串
	 * @return 是否不为空
	 */
	public final static boolean isNotEmpty(String s) {
		if (s == null || s.length() == 0) {
			return false;
		}
		return true;
	}
}
