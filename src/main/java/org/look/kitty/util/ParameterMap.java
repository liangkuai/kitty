package org.look.kitty.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liangkuai
 * @date 2018/10/21
 */
public class ParameterMap<K, V> extends HashMap<K, V> {

    private static final StringManager sm = StringManager.getManager("com.server.util");

    /**
     * 锁
     */
    private boolean locked = false;


    public ParameterMap() {
        super();
    }


    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public V put(K key, V value) {
        if (locked)
            throw new IllegalStateException
                    (sm.getString("parameterMap.locked"));
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        if (locked)
            throw new IllegalStateException
                    (sm.getString("parameterMap.locked"));
        super.putAll(map);
    }

    @Override
    public V remove(Object key) {
        if (locked)
            throw new IllegalStateException
                    (sm.getString("parameterMap.locked"));
        return super.remove(key);
    }

    @Override
    public void clear() {
        if (locked)
            throw new IllegalStateException
                    (sm.getString("parameterMap.locked"));
        super.clear();
    }
}
