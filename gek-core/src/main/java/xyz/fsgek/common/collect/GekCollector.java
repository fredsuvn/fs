package xyz.fsgek.common.collect;

import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.GekConfigurer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;
import java.util.stream.StreamSupport;

/**
 * This class is used to configure and build collection in method chaining:
 * <pre>
 *     collector.initialSize(100).initialFunction(i->random()).toList();
 * </pre>
 * Its instance is reusable, re-set and re-build are permitted.
 *
 * @author fredsuvn
 */
public abstract class GekCollector implements GekConfigurer<GekCollector> {

    /**
     * Returns a new pair of specified key and value.
     *
     * @param key   specified key
     * @param value specified value
     * @param <K>   key type
     * @param <V>   value type
     * @return a new pair of specified key and value
     */
    public static <K, V> Pair<K, V> pair(K key, V value) {
        return new Pair<K, V>() {
            @Override
            public K key() {
                return key;
            }

            @Override
            public V value() {
                return value;
            }
        };
    }

    static GekCollector newInstance() {
        return new OfJdk8();
    }

    private int initialCapacity;
    private int initialSize;
    private Object initialElements;
    private IntFunction<?> initialFunction;
    private boolean immutable;

    GekCollector() {
        reset();
    }

    /**
     * Sets initial capacity.
     *
     * @param initialCapacity initial capacity
     * @return this
     */
    public GekCollector initialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    /**
     * Sets initial size.
     *
     * @param initialSize initial size
     * @return this
     */
    public GekCollector initialSize(int initialSize) {
        this.initialSize = initialSize;
        return this;
    }

    /**
     * Sets initial elements:
     * <ul>
     *     <li>
     *         To build a collection, each element will be added;
     *     </li>
     *     <li>
     *         To build a map, the first element will be put as key1, second as value1,
     *         third as key2, fourth as value2 and so on.
     *         If last key{@code n} is not followed by a value{@code n}, it will be ignored;
     *     </li>
     * </ul>
     *
     * @param initialElements initial elements
     * @return this
     */
    public GekCollector initialElements(Iterable<?> initialElements) {
        this.initialElements = initialElements;
        return this;
    }

    /**
     * Sets initial elements:
     * <ul>
     *     <li>
     *         To build a collection, each element will be added;
     *     </li>
     *     <li>
     *         To build a map, the first element will be put as key1, second as value1,
     *         third as key2, fourth as value2 and so on.
     *         If last key{@code n} is not followed by a value{@code n}, it will be ignored;
     *     </li>
     * </ul>
     *
     * @param initialElements initial elements
     * @return this
     */
    public GekCollector initialElements(Object... initialElements) {
        this.initialElements = initialElements;
        return this;
    }

    /**
     * Sets initial function:
     * <ul>
     *     <li>
     *         To build a collection, the function will be passed to the index and return an element at the index;
     *     </li>
     *     <li>
     *         To build a map, the function will be passed to the index and return
     *         an array of {@link Pair} at the index;
     *     </li>
     * </ul>
     * If the {@link #initialElements} is set, this configuration will be ignored.
     *
     * @param initialFunction initial function
     * @return this
     */
    public GekCollector initialFunction(IntFunction<?> initialFunction) {
        this.initialFunction = initialFunction;
        return this;
    }

    /**
     * Sets whether built collection is immutable.
     *
     * @param immutable whether built collection is immutable
     * @return this
     */
    public GekCollector immutable(boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    /**
     * Builds and returns {@link ArrayList}.
     *
     * @param <T> type of element
     * @return {@link ArrayList}
     */
    public <T> ArrayList<T> toArrayList() {
        ArrayList<T> result = initialCapacity <= 0 ? new ArrayList<>() : new ArrayList<>(initialCapacity);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link LinkedList}.
     *
     * @param <T> type of element
     * @return {@link ArrayList}
     */
    public <T> LinkedList<T> toLinkedList() {
        LinkedList<T> result = new LinkedList<>();
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link HashSet}.
     *
     * @param <T> type of element
     * @return {@link HashSet}
     */
    public <T> HashSet<T> toHashSet() {
        HashSet<T> result = initialCapacity <= 0 ? new HashSet<>() : new HashSet<>(initialCapacity);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link HashSet}.
     *
     * @param <T> type of element
     * @return {@link HashSet}
     */
    public <T> LinkedHashSet<T> toLinkedHashSet() {
        LinkedHashSet<T> result = initialCapacity <= 0 ? new LinkedHashSet<>() : new LinkedHashSet<>(initialCapacity);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link TreeSet}.
     *
     * @param <T> type of element
     * @return {@link TreeSet}
     */
    public <T> TreeSet<T> toTreeSet() {
        TreeSet<T> result = new TreeSet<>();
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link TreeSet}.
     *
     * @param comparator the comparator
     * @param <T>        type of element
     * @return {@link TreeSet}
     */
    public <T> TreeSet<T> toTreeSet(Comparator<? super T> comparator) {
        TreeSet<T> result = new TreeSet<>(comparator);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link ConcurrentHashMap.KeySetView}.
     *
     * @param <T> type of key
     * @return {@link ConcurrentHashMap.KeySetView}
     */
    public <T> ConcurrentHashMap.KeySetView<T, Boolean> toConcurrentSet() {
        ConcurrentHashMap.KeySetView<T, Boolean> result =
            initialCapacity <= 0 ? ConcurrentHashMap.newKeySet() : ConcurrentHashMap.newKeySet(initialCapacity);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link HashMap}.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return {@link HashMap}
     */
    public <K, V> HashMap<K, V> toHashMap() {
        HashMap<K, V> result = initialSize <= 0 ? new HashMap<>() : new HashMap<>(initialCapacity);
        fillMap(result);
        return result;
    }

    /**
     * Builds and returns {@link LinkedHashMap}.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return {@link LinkedHashMap}
     */
    public <K, V> LinkedHashMap<K, V> toLinkedHashMap() {
        LinkedHashMap<K, V> result = initialSize <= 0 ? new LinkedHashMap<>() : new LinkedHashMap<>(initialCapacity);
        fillMap(result);
        return result;
    }

    /**
     * Builds and returns {@link ConcurrentHashMap}.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return {@link ConcurrentHashMap}
     */
    public <K, V> ConcurrentHashMap<K, V> toConcurrentHashMap() {
        ConcurrentHashMap<K, V> result =
            initialSize <= 0 ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(initialCapacity);
        fillMap(result);
        return result;
    }

    private void fillCollection(Collection<?> collection) {
        if (initialElements != null) {
            if (initialElements instanceof Collection) {
                collection.addAll(Gek.as(initialElements));
                return;
            }
            if (initialElements instanceof Object[]) {
                collection.addAll(new GekColl.ImmutableList<>((Object[]) initialElements));
                return;
            }
            if (initialElements instanceof Iterable) {
                ((Iterable<?>) initialElements).forEach(it -> collection.add(Gek.as(it)));
                return;
            }
            throw new IllegalArgumentException("Initial elements must be iterable or array.");
        } else if (initialSize > 0 && initialFunction != null) {
            for (int i = 0; i < initialSize; i++) {
                Object v = initialFunction.apply(i);
                collection.add(Gek.as(v));
            }
        }
    }

    private void fillMap(Map<?, ?> map) {
        if (initialElements != null) {
            if (initialElements instanceof Object[]) {
                Object[] array = (Object[]) initialElements;
                GekColl.collect(map, array);
                return;
            }
            if (initialElements instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) initialElements;
                GekColl.collect(map, iterable);
                return;
            }
            throw new IllegalArgumentException("Initial elements must be iterable or array.");
        } else if (initialSize > 0 && initialFunction != null) {
            for (int i = 0; i < initialSize; i++) {
                Pair<?, ?> p = Gek.as(initialFunction.apply(i));
                map.put(Gek.as(p.key()), Gek.as(p.value()));
            }
        }
    }

    /**
     * Builds and returns list.
     *
     * @param <T> type of element
     * @return list
     */
    public <T> List<T> toList() {
        if (!immutable) {
            return toArrayList();
        }
        if (initialElements != null) {
            Object[] array = toArray(initialElements, false);
            return new GekColl.ImmutableList<>(array);
        } else if (initialSize > 0 && initialFunction != null) {
            Object[] array = new Object[initialSize];
            for (int i = 0; i < array.length; i++) {
                array[i] = initialFunction.apply(i);
            }
            return new GekColl.ImmutableList<>(array);
        }
        return Collections.emptyList();
    }

    /**
     * Builds and returns set.
     *
     * @param <T> type of element
     * @return set
     */
    public <T> Set<T> toSet() {
        if (!immutable) {
            return toLinkedHashSet();
        }
        if (initialElements != null) {
            Object[] array = toArray(initialElements, true);
            return new GekColl.ImmutableSet<>(array);
        } else if (initialSize > 0 && initialFunction != null) {
            Object[] array = new Object[initialSize];
            for (int i = 0; i < array.length; i++) {
                array[i] = initialFunction.apply(i);
            }
            return new GekColl.ImmutableSet<>(array);
        }
        return Collections.emptySet();
    }

    /**
     * Builds and returns map.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return map
     */
    public <K, V> Map<K, V> toMap() {
        if (!immutable) {
            return toLinkedHashMap();
        }
        if (initialElements != null) {
            Object[] array = toArray(initialElements, false);
            return new GekColl.ImmutableMap<>(array);
        } else if (initialSize > 0 && initialFunction != null) {
            Pair<K, V>[] array = new Pair[initialSize];
            for (int i = 0; i < initialSize; i++) {
                array[i] = Gek.as(initialFunction.apply(i));
            }
            return new GekColl.ImmutableMap<>(array);
        }
        return Collections.emptyMap();
    }

    private static Object[] toArray(Object elements, boolean distinct) {
        if (elements instanceof Collection) {
            if (distinct) {
                return ((Collection<?>) elements).stream().distinct().toArray();
            }
            return ((Collection<?>) elements).toArray();
        }
        if (elements instanceof Object[]) {
            if (distinct) {
                return Arrays.stream((Object[]) elements).distinct().toArray();
            }
            return (Object[]) elements;
        }
        if (elements instanceof Iterable) {
            if (distinct) {
                return StreamSupport.stream(((Iterable<?>) elements).spliterator(), false).distinct().toArray();
            }
            return GekColl.toArray((Iterable<?>) elements);
        }
        throw new IllegalArgumentException("Initial elements must be iterable or array.");
    }

    @Override
    public GekCollector reset() {
        this.initialSize = 0;
        this.initialCapacity = 0;
        this.initialElements = null;
        this.initialFunction = null;
        this.immutable = false;
        return this;
    }

    private static final class OfJdk8 extends GekCollector {
    }

    /**
     * Structure contains a key and a value.
     *
     * @param <K> key type
     * @param <V> value type
     */
    public interface Pair<K, V> {

        /**
         * Returns key.
         *
         * @return key
         */
        K key();

        /**
         * Returns value.
         *
         * @return value
         */
        V value();
    }
}
