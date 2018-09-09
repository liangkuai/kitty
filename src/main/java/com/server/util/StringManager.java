package com.server.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 错误消息的国际化处理
 *
 * 单例模式
 *
 * @author liangkuai
 * @date 2018/9/2
 */
public class StringManager {

    private static Map<String, StringManager> managers = new HashMap<>();

    private ResourceBundle bundle;

    private StringManager(String packageName) {
        String bundleName = packageName + ".LocalStrings";
        bundle = ResourceBundle.getBundle(bundleName);
    }


    public synchronized static StringManager getManager(String packageName) {
        StringManager sm = managers.get(packageName);
        if (sm == null) {
            sm = new StringManager(packageName);
            managers.put(packageName, sm);
        }
        return sm;
    }
}
