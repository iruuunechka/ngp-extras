package ru.ifmo.ctd.ngp.learning.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A three-dimensional map with a default value to be returned if there is no corresponding mapping.
 *
 * @author Maxim Buzdalov
 */
public final class Map3<K1, K2, K3, V> {
    private final Map2<K1, K2, Map<K3, V>> contents = new Map2<>(null);
    private final V defaultValue;

    public Map3(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    public V defaultValue() {
        return defaultValue;
    }
    
    public Set<K1> keySet1() {
        return contents.keySet1();
    }

    public Set<K2> keySet2() {
        return contents.keySet2();
    }

    public Map<K3, V> projection(K1 k1, K2 k2) {
        Map<K3, V> map = contents.get(k1, k2);
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(contents.get(k1, k2));
    }

    public V get(K1 k1, K2 k2, K3 k3) {
        Map<K3, V> map = contents.get(k1, k2);
        return map == null || !map.containsKey(k3) ? defaultValue : map.get(k3);
    }

    public void put(K1 k1, K2 k2, K3 k3, V value) {
        Map<K3, V> map = contents.get(k1, k2);
        if (map == null) {
            contents.put(k1, k2, map = new HashMap<>());
        }
        map.put(k3, value);
    }

    public void remove(K1 k1, K2 k2, K3 k3) {
        Map<K3, V> map = contents.get(k1, k2);
        if (map != null) {
            map.remove(k3);
        }
    }
    
    public void clear() {
        contents.clear();
    }
}
