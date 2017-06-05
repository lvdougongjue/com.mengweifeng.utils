package com.mengweifeng.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author mwf
 * 
 *
 */
public class ClassUtil {

	/**
	 * 获取目标类的所有属性
	 * 
	 * @param clazz
	 *            目标类定义
	 * @param recursion
	 *            是否递归获取父类的的属性
	 * @return 属性集合
	 */
	public static Map<String, Field> getDeclaredFieldsMap(Class<?> clazz, Boolean recursion) {
		Map<String, Field> fieldsMap = new HashMap<String, Field>();
		Field[] fields = getDeclaredFields(clazz, recursion);
		for (Field field : fields) {
			String fieldName = field.getName();
			Field existField = fieldsMap.get(fieldName);
			if (existField == null) {
				fieldsMap.put(fieldName, field);
			} else {
				// 有重名的属性，保留子类的，覆盖父类的
				Class<?> declaringClass = field.getDeclaringClass();
				Class<?> existDeclaringClass = existField.getDeclaringClass();
				if (existDeclaringClass.isAssignableFrom(declaringClass)) {
					// 已存在的是父类，覆盖
					fieldsMap.put(fieldName, field);
				}
			}
		}
		return fieldsMap;
	}

	/**
	 * 获取目标定义的属性
	 * 
	 * @param clazz
	 *            目标类定义
	 * @return 属性数组
	 */
	public static Field[] getDeclaredFields(Class<?> clazz) {
		return getDeclaredFields(clazz, false);
	}

	/**
	 * 获取目标类定义的属性
	 * 
	 * @param clazz
	 *            目标类定义
	 * @param recursion
	 *            是否递归获取父类的的属性
	 * @return 属性数组
	 */
	public static Field[] getDeclaredFields(Class<?> clazz, Boolean recursion) {
		if (clazz == null) {
			return new Field[0];
		}
		if (recursion == null) {
			recursion = false;
		}
		Field[] fields = clazz.getDeclaredFields();
		if (recursion) {
			Class<?> superClass = clazz.getSuperclass();
			Field[] superFields = getDeclaredFields(superClass, recursion);
			fields = addAll(fields, superFields);
		}
		return fields;
	}

	/**
	 * 获取类定义的命名(小驼峰式)
	 * 
	 * @param clazz
	 *            class对象
	 * @return 类定义的命名(小驼峰式)
	 */
	public static String getHumpName(Class<?> clazz) {
		String className = clazz.getName();
		int i = className.lastIndexOf(".");
		className = className.substring(i + 1, i + 2).toLowerCase() + className.substring(i + 2);
		return className;
	}

	/**
	 * 获取指定包下某个接口的所有实现类
	 * 
	 * @param <T>
	 *            泛型
	 * @param c
	 *            接口定义
	 * @param packageName
	 *            包路径，如果为空，则获取接口所在的包路径
	 * @return 实现该接口的类定义集合
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<Class<T>> getAllClassByInterface(Class<T> c, String packageName) {
		// 判断是不是接口,不是接口不作处理
		if (!c.isInterface()) {
			return Collections.emptyList();
		}
		List<Class<T>> returnClassList = new ArrayList<Class<T>>();
		if (StringUtils.isEmpty(packageName)) {
			// 未指定包名，获得当前包名
			packageName = c.getPackage().getName();
		}
		try {
			List<Class<?>> allClass = getClasses(packageName);// 获得当前包以及子包下的所有类
			// 判断是否是一个接口
			for (int i = 0; i < allClass.size(); i++) {
				if (c.isAssignableFrom(allClass.get(i))) {
					if (!c.equals(allClass.get(i))) {
						returnClassList.add((Class<T>) allClass.get(i));
					}
				}
			}
		} catch (Exception e) {
			return returnClassList;
		}
		return returnClassList;
	}

	/**
	 * 获取加载指定注解的类
	 * 
	 * @param annotationClass
	 *            注解
	 * @param packageName
	 *            指定包路径，如果为空，则获取注解所在的包路径
	 * @return 类集合
	 */
	public static List<Class<?>> getAllClassByAnnotation(Class<? extends Annotation> annotationClass, String packageName) {
		List<Class<?>> returnClassList = new ArrayList<Class<?>>();
		if (StringUtils.isEmpty(packageName)) {
			packageName = annotationClass.getPackage().getName(); // 获得当前包名
		}
		try {
			List<Class<?>> allClass = getClasses(packageName);// 获得当前包以及子包下的所有类
			for (int i = 0; i < allClass.size(); i++) {
				if (allClass.get(i).isAnnotationPresent((Class<? extends Annotation>) annotationClass)) {
					returnClassList.add(allClass.get(i));
				}
			}
			return returnClassList;
		} catch (Exception e) {
			return returnClassList;
		}
	}

	private static Field[] addAll(Field[] fields, Field[] superFields) {
		Field[] newArray = new Field[fields.length + superFields.length];
		for (int i = 0; i < fields.length; i++) {
			newArray[i] = fields[i];
		}
		for (int i = 0; i < superFields.length; i++) {
			newArray[fields.length + i] = superFields[i];
		}
		return newArray;
	}

	/**
	 * 
	 * 根据包名获得该包以及子包下的所有类，不查找jar包中的
	 * 
	 * @param pageName
	 *            包名
	 * @return List<Class> 包下所有类
	 */
	private static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace(".", "/");
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			String newPath = resource.getFile().replace("%20", " ");
			dirs.add(new File(newPath));
		}
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClass(directory, packageName));
		}
		return classes;
	}

	private static List<Class<?>> findClass(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClass(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public static void main(String[] args) {
		System.out.println(getHumpName(String.class));
	}
}
