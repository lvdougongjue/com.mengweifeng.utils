package com.mengweifeng.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
	private static final Map<String, String> CONTENTTYPES = new HashMap<String, String>();
	private static final String DEFAULT_CONTENTTYPE = "text/html;charset=UTF-8";
	static {
		CONTENTTYPES.put("html", "text/html;charset=UTF-8");
		CONTENTTYPES.put("xls", "application/x-msdownload;charset=UTF-8");
		CONTENTTYPES.put("xlsx", "application/x-msdownload;charset=UTF-8");
		CONTENTTYPES.put("csv", "application/csv;charset=UTF-8");
	}

	public static void downloadFile(HttpServletRequest request, HttpServletResponse response, String fileName, File downLoadFile) throws FileNotFoundException {
		FileInputStream is = new FileInputStream(downLoadFile);
		downloadFile(request, response, fileName, is);
	}

	public static void downloadFile(HttpServletRequest request, HttpServletResponse response, String fileName, String downLoadPath) throws FileNotFoundException {
		FileInputStream is = new FileInputStream(downLoadPath);
		downloadFile(request, response, fileName, is);
	}

	/**
	 * 下载文件
	 * 
	 * @param request
	 *            request
	 * @param response
	 *            response
	 * @param fileName
	 *            下载的文件名
	 * @param downLoadPath
	 *            文件的路径
	 */
	public static void downloadFile(HttpServletRequest request, HttpServletResponse response, String fileName, InputStream is) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			String contentType = getContentType(fileName);
			String userAgent = request.getHeader("User-Agent");
			userAgent = userAgent.toLowerCase();
			boolean isIe = false;
			if (userAgent.contains("msie") || userAgent.contains("rv:11")) {
				isIe = true;
			}
			if (isIe) {
				fileName = URLEncoder.encode(fileName, "utf-8");
			} else {
				fileName = new String(fileName.getBytes("utf-8"), "iso8859-1");
			}
			response.setContentType(contentType);
			response.setHeader("Content-disposition", "attachment; filename=" + fileName);
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			bos.flush();
		} catch (UnsupportedEncodingException e) {
			log.error("设置编码时错误", e);
		} catch (FileNotFoundException e) {
			log.error("下载文件未找到", e);
		} catch (Exception e) {
			log.error("下载文件时错误", e);
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				log.error("关闭文件流时出错", e);
			}
		}
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("X-Forwarded-For");
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		} else {
			return request.getRemoteAddr();
		}
	}

	private static String getContentType(String fileName) {
		String extendName = FileUtil.getExtendName(fileName).toLowerCase();
		String contentType = CONTENTTYPES.get(extendName);
		if (contentType == null) {
			contentType = DEFAULT_CONTENTTYPE;
		}
		return contentType;
	}

	/**
	 * 设置cookie
	 * 
	 * @param response
	 *            response
	 * @param name
	 *            cookie的名称
	 * @param value
	 *            cookie的值
	 * @param maxAge
	 *            生命周期,单位是秒
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		if (maxAge > 0) {
			cookie.setMaxAge(maxAge);
		}
		response.addCookie(cookie);
	}

	/**
	 * 获取cookie
	 * 
	 * @param request
	 *            request
	 * @param name
	 *            cookie的名称
	 * @return cookie对象(可能为null)
	 */
	public static Cookie getCookieByName(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		}
		return null;
	}

	/**
	 * 将cookie封装到Map里面
	 * 
	 * @param request
	 *            request
	 * @return cookie集合(可能为空)
	 */
	public static Map<String, Cookie> getCookieMap(HttpServletRequest request) {
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				cookieMap.put(cookie.getName(), cookie);
			}
		}
		return cookieMap;
	}

}
