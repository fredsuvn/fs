package xyz.sunqian.common.coll;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Static utility class for {@link Set}.
 *
 * @author sunqian
 */
public class JieSet {

    /**
     * Returns a new immutable set of which content is added from the given array. The content of the set is added in
     * array order, and duplicate data will be ignored. The behavior of this method is equivalent to:
     * <pre>{@code
     * return Collections.unmodifiableSet(linkedHashSet(array));
     * }</pre>
     *
     * @param array the given array
     * @param <T>   the component type
     * @return a new immutable set of which content is added from the given array
     */
    @Immutable
    @SafeVarargs
    public static <T> Set<@Nullable T> set(@Nullable T... array) {
        return Collections.unmodifiableSet(linkedHashSet(array));
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
    public static <T> HashSet<@Nullable T> hashSet(@Nullable T... array) {
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
}
