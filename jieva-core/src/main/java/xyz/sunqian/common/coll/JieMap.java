package xyz.sunqian.common.coll;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Static utility class for {@link Map}.
 *
 * @author sunqian
 */
public class JieMap {

    /**
     * Returns a new immutable set of which content is added from the given array. The content of the set is added in
     * array order, and duplicate data will be ignored.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new immutable set of which content is added from the given array
     */
    @Immutable
    public static <K, V> Map<K, @Nullable V> set(@Nullable Object... array) {
        Map<K, V> map = JieColl.toMap(array);
    }

    /**
     * Returns a new {@link HashSet} initialing with the given array.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link HashSet} initialing with the given array
     */
    @Immutable
    @SafeVarargs
    public static <T> HashMap<@Nullable T> hashSet(@Nullable T... array) {
        return new HashSet<>(Arrays.asList(array));
    }

    /**
     * Returns a new {@link LinkedHashSet} initialing with the given array.
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new {@link LinkedHashSet} initialing with the given array
     */
    @Immutable
    @SafeVarargs
    public static <T> LinkedHashSet<@Nullable T> linkedHashSet(@Nullable T... array) {
        return new LinkedHashSet<>(Arrays.asList(array));
    }

    private static <K, V, M extends Map<K, @Nullable V>> M toMap(M map, @Nullable Object[] array) {
        int end = array.length / 2 * 2;
        int i = 0;
        while (i < end) {
            K key = Jie.as(array[i++]);
            V value = Jie.as(array[i++]);
            map.put(key, value);
        }
        if (end < array.length) {
            map.put(Jie.as(array[end]), null);
        }
        return map;
    }
}
