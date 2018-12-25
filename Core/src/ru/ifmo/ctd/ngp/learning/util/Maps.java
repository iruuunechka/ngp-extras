package ru.ifmo.ctd.ngp.learning.util;

import ru.ifmo.ctd.ngp.util.FastRandom;
import ru.ifmo.ctd.ngp.util.Static;

import java.util.*;

/**
 * Utilities for {@link ru.ifmo.ctd.ngp.learning.util.Map2}
 * and {@link ru.ifmo.ctd.ngp.learning.util.Map3}.
 *
 * @author Maxim Buzdalov
 */
public final class Maps {
    private Maps() {
        Static.doNotCreateInstancesOf(Maps.class);
    }

    public static <
            K1, K2, V
    > String format(Map2<K1, K2, V> map, List<? extends K2> columnOrder, PrintFormatter<? super V> valueFormat) {
        StringBuilder builder = new StringBuilder();
        for (K1 k1 : map.keySet1()) {
            builder.append(k1);
            builder.append(" ");
            for (K2 k2 : columnOrder) {
                builder.append(valueFormat.format(map.get(k1, k2)));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static <
            K1, K2, K3, V
    > String format(Map3<K1, K2, K3, V> map, List<? extends K2> columnOrder, PrintFormatter<? super V> valueFormat) {
        StringBuilder builder = new StringBuilder();

        for (K1 k1 : map.keySet1()) {
            builder.append(k1);
            builder.append(" ");
            for (K2 k2 : columnOrder) {
                builder.append(String.format("[%s|", k2));
                for (K3 k3 : map.projection(k1, k2).keySet()) {
                    builder.append(String.format(" %s : %s", k3, valueFormat.format(map.get(k1, k2, k3))));
                }
                builder.append("]");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static <K1, K2, V extends Comparable<? super V>> V max(Map2<K1, K2, V> map, K1 k1, List<K2> possibleK2s) {
        V result = null;
        int count = 0;
        Map2.LinkedEntry<K1, K2, V> e = map.firstKeyLinks.get(k1);

        while (e != null && !e.next.isRoot()) {
            ++count;
            e = e.next;
            V v = e.v;
            if (result == null) {
                result = v;
            } else {
                result = result.compareTo(v) > 0 ? result : v;
            }
        }
        V defV = map.defaultValue();
        if (result == null) {
            return defV;
        } else {
            if (count < possibleK2s.size() && defV.compareTo(result) > 0) {
                return defV;
            } else {
                return result;
            }
        }
    }
    
    public static <K1, K2, V extends Comparable<? super V>> K2 argMax(Map2<K1, K2, V> map, K1 k1, List<K2> possibleK2s) {
        if (possibleK2s.isEmpty()) {
            throw new IllegalArgumentException("possibleK2s are empty");
        }
        V result = max(map, k1, possibleK2s);
        List<K2> candidates = new ArrayList<>();
        for (K2 k2 : possibleK2s) {
            if (map.get(k1, k2).compareTo(result) == 0) {
                candidates.add(k2);
            }
        }
        Random r = FastRandom.threadLocal();
        if (candidates.isEmpty()) {
            throw new AssertionError("Should not happen ever");
        }
        return candidates.get(r.nextInt(candidates.size()));
    }
}
