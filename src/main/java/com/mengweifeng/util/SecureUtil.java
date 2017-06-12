package com.mengweifeng.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全工具类，线程安全
 * 
 * @author mwf
 *
 */
public final class SecureUtil {
	private static final Logger log = LoggerFactory.getLogger(SecureUtil.class);
	private static ThreadLocal<MessageDigest> md5ThreadLocal = new ThreadLocal<MessageDigest>() {

		@Override
		protected MessageDigest initialValue() {
			try {
				MessageDigest mdInst = MessageDigest.getInstance("MD5");
				return mdInst;
			} catch (NoSuchAlgorithmException e) {
				log.error(e.getMessage(), e);
			}
			return null;
		}
	};
	private static ThreadLocal<MessageDigest> sha1ThreadLocal = new ThreadLocal<MessageDigest>() {

		@Override
		protected MessageDigest initialValue() {
			try {
				MessageDigest mdInst = MessageDigest.getInstance("SHA-1");
				return mdInst;
			} catch (NoSuchAlgorithmException e) {
				log.error(e.getMessage(), e);
			}
			return null;
		}

	};

	/**
	 * 私有化构造函数
	 */
	private SecureUtil() {
	}

	/**
	 * 进行SHA-1编码
	 * 
	 * @param s
	 *            待编码的字符串
	 * @return 编码后的字符串，可能为null
	 */
	public final static String sha1(String s) {
		MessageDigest mdInst = sha1ThreadLocal.get();
		return digest(mdInst, s);
	}

	/**
	 * 将输入字符串进行md5编码
	 * 
	 * @param s
	 *            待编码的字符串
	 * @return md5编码后的字符串，可能为null
	 */
	public final static String md5(String s) {
		MessageDigest mdInst = md5ThreadLocal.get();
		return digest(mdInst, s);
	}

	private static String digest(MessageDigest mdInst, String s) {
		if (s == null || s.trim().length() == 0) {
			return null;
		}
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
