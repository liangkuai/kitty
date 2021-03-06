package org.look.kitty.util;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 错误消息的国际化处理
 *
 * TODO: 单例模式
 *
 * @author liangkuai
 * @date 2018/9/2
 */
public class StringManager {

    /**
     * 下面的同步方法 getManager() 控制了多线程互斥访问，
     * 再使用 volatile 保证 managers 在各个线程中的可见性
     */
    private static volatile Map<String, StringManager> managers = new HashMap<>();

    private ResourceBundle bundle;

    private StringManager(String packageName) {
        String bundleName = packageName + ".LocalStrings";
        bundle = ResourceBundle.getBundle(bundleName);
    }


    public String getString(String key) {
        if (key == null || key.isEmpty()) {
            String msg = "key is null";
            throw new NullPointerException(msg);
        }

        String str;
        try {
            str = bundle.getString(key);
        } catch (MissingResourceException mre) {
            str = "Cannot find message associated with key '" + key + "'";
        }
        return str;
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