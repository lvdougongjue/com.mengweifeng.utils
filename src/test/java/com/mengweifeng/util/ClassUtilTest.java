package com.mengweifeng.util;

import org.junit.jupiter.api.Assertions;

import java.io.Serializable;
import java.util.List;

/**
 * @author: weifeng.meng@ishansong.com
 * @Date: 2021/1/24 22:52
 * @Description:
 */
class ClassUtilTest {

    @org.junit.jupiter.api.Test
    void findClasses() {
        List<Class<?>> classes = ClassUtil.findClasses(Serializable.class, null, null, "com.mengweifeng.util.data");
        Assertions.assertEquals(classes.size(), 1);
        Class<?> clazz = classes.get(0);
        String clazzName = clazz.getName();
        Assertions.assertEquals(clazzName,"com.mengweifeng.util.data.SerialTest");

    }
}