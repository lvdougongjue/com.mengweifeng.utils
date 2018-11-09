package com.mengweifeng.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件操作工具类
 * 
 * @author lvdougongjue@163.com
 *
 */
public class FileUtil {
	private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 从类路径获取文件
	 * 
	 * @param resource
	 *            路径
	 * @return 文件 可能为null
	 */
	public static File getFileByClassPath(String resource) {
		URL url = FileUtil.class.getClassLoader().getResource(resource);
		File file = new File(url.getFile());
		if (!file.exists()) {
			log.warn("文件[" + resource + "]未找到");
			return null;
		}
		return file;
	}

	/**
	 * 从类路径获取输入流
	 * 
	 * @param resource
	 *            类路径
	 * @return 输入流 可能为null
	 */
	public static InputStream getInputStreamByClassPath(String resource) {
		return FileUtil.class.getClassLoader().getResourceAsStream(resource);
	}

	/**
	 * 从网络中下载文件
	 * 
	 * @param fileUrl
	 *            文件url地址
	 * @return 文件对象 可能为null
	 */
	public static File downloadFromUrl(String fileUrl) {
		String tmpdir = System.getProperty("java.io.tmpdir");
		try {
			URL url = new URL(fileUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置超时间为3秒
			conn.setConnectTimeout(3 * 1000);
			// 防止屏蔽程序抓取而返回403错误
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			// 得到输入流
			InputStream inputStream = conn.getInputStream();
			// 文件名
			String fileName;
			int i = fileUrl.lastIndexOf("/");
			if (i == fileUrl.length() - 1) {
				fileName = System.currentTimeMillis() + "";
			} else {
				fileName = fileUrl.substring(i + 1);
			}
			File tmpFile = new File(tmpdir + File.separator + fileName);
			FileOutputStream fos = new FileOutputStream(tmpFile);
			int count;
			byte[] bytes = new byte[1024];
			while ((count = inputStream.read(bytes)) != -1) {
				fos.write(bytes, 0, count);
			}
			fos.flush();
			if (fos != null) {
				fos.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			return tmpFile;
		} catch (IOException e) {
			log.debug("下载文件[" + fileUrl + "]失败", e);
			return null;
		}
	}

	public static String formetFileSize(Long fileSize) {// 转换文件大小
		if (fileSize == null || fileSize < 0) {
			return "";
		}
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileSize < 1024) {
			fileSizeString = df.format((double) fileSize) + "B";
		} else if (fileSize < 1048576) {
			fileSizeString = df.format((double) fileSize / 1024) + "K";
		} else if (fileSize < 1073741824) {
			fileSizeString = df.format((double) fileSize / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileSize / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取扩展名
	 * 
	 * @param fileName
	 *            文件名
	 * @return 扩展名
	 */
	public static String getExtendName(String fileName) {
		int l = fileName.lastIndexOf(".");
		if (l == -1) {
			return "";
		}
		return fileName.substring(l + 1, fileName.length());
	}
}
