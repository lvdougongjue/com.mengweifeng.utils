package com.mengweifeng.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5工具类，线程安全
 * 
 * @author mwf
 *
 */
public final class MD5Util {
	private static ThreadLocal<MessageDigest> threadLocal = new ThreadLocal<MessageDigest>() {

		@Override
		protected MessageDigest initialValue() {
			try {
				MessageDigest mdInst = MessageDigest.getInstance("MD5");
				return mdInst;
			} catch (NoSuchAlgorithmException e) {
			}
			return null;
		}

	};

	/**
	 * 私有化构造函数
	 */
	private MD5Util() {
	}

	/**
	 * 将输入字符串进行md5编码
	 * 
	 * @param s
	 *            待编码的字符串
	 * @return md5编码后的字符串，可能为null
	 */
	public final static String md5(String s) {
		if (s == null || s.trim().length() == 0) {
			return null;
		}
		MessageDigest mdInst = threadLocal.get();
		byte[] btInput = s.getBytes();
		mdInst.update(btInput);
		byte[] md = mdInst.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < md.length; i++) {
			int val = ((int) md[i]) & 0xff;
			if (val < 16)
				sb.append("0");
			sb.append(Integer.toHexString(val));

		}
		return sb.toString();
	}

}
