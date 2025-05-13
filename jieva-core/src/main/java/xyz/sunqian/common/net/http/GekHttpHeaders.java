package xyz.sunqian.common.net.http;

import lombok.EqualsAndHashCode;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collection.JieCollection;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Http header info.
 *
 * @author fredsuvn
 */
@EqualsAndHashCode
public class GekHttpHeaders {

    /**
     * Returns headers of which content from given map.
     * <p>
     * If a value is instance of {@link Collection}, it will be considered as repeated header and converts to
     * {@link List}&lt;{@link String}&gt; by {@link String#valueOf(Object)} for each element, then add into the
     * associated key. This method is equivalent to:
     * <pre>
     *     GekHttpHeaders headers = new GekHttpHeaders();
     *     headers.addHeaders(map);
     *     return headers;
     * </pre>
     * See {@link #addHeader(String, Object)}.
     *
     * @param map given map
     * @return headers of which content from given map
     * @see #addHeaders(Map)
     */
    public static GekHttpHeaders from(Map<String, ?> map) {
        GekHttpHeaders headers = new GekHttpHeaders();
        headers.addHeaders(map);
        return headers;
    }

    /**
     * Returns headers of which content from given key-values. The 1st is key, 2nd is value, 3rd is key, 4th is value,
     * and so on.
     *
     * @param keyValues given key values
     * @return headers of which content from given key-values
     */
    public static GekHttpHeaders of(String... keyValues) {
        GekHttpHeaders headers = new GekHttpHeaders();
        headers.addHeaders(Jie.linkedHashMap(keyValues));
        return headers;
    }

    private final Map<String, List<String>> headerMap = new LinkedHashMap<>();

    /**
     * Constructs an empty headers.
     */
    public GekHttpHeaders() {
    }

    /**
     * Adds a header. HTTP allows repeated header keys. Therefore, if there is already an existing key, it will add
     * another one.
     *
     * @param key   header key
     * @param value header value
     * @return this headers
     */
    public GekHttpHeaders addHeader(String key, String value) {
        List<String> list = headerMap.computeIfAbsent(key, k -> new LinkedList<>());
        list.add(value);
        return this;
    }

    /**
     * Clears and sets a header. HTTP allows repeated header keys. Using this method will clear old value of same keys.
     *
     * @param key   header key
     * @param value header value
     * @return this headers
     */
    public GekHttpHeaders setHeader(String key, String value) {
        List<String> list = headerMap.computeIfAbsent(key, k -> new LinkedList<>());
        list.clear();
        list.add(value);
        return this;
    }

    /**
     * Returns value of header key. If there exists repeated keys, return first one.
     *
     * @param key header key
     * @return value of header key
     */
    @Nullable
    public String getHeaderFirst(String key) {
        List<String> list = headerMap.get(key);
        return JieCollection.isEmpty(list) ? null : list.get(0);
    }

    /**
     * Returns values of header key.
     *
     * @param key header key
     * @return values of header key
     */
    @Nullable
    public List<String> getHeader(String key) {
        return headerMap.get(key);
    }

    /**
     * Adds header.
     * <p>
     * If the value is instance of {@link Collection}, it will be considered as repeated header and converts to
     * {@link List}&lt;{@link String}&gt; by {@link String#valueOf(Object)} for each element, then add into the
     * associated key.
     *
     * @param key   header key
     * @param value header value
     * @return this headers
     */
    public GekHttpHeaders addHeader(String key, Object value) {
        List<String> values = headerMap.computeIfAbsent(key, k -> new LinkedList<>());
        if (value instanceof Collection) {
            Collection<Object> c = Jie.as(value);
            values.addAll(c.stream().map(String::valueOf).collect(Collectors.toList()));
        } else {
            values.add(String.valueOf(value));
        }
        return this;
    }

    /**
     * Adds headers.
     * <p>
     * If a value is instance of {@link Collection}, it will be considered as repeated header and converts to
     * {@link List}&lt;{@link String}&gt; by {@link String#valueOf(Object)} for each element, then add into the
     * associated key. This method is equivalent to:
     * <pre>
     *     headers.forEach(this::addHeader);
     * </pre>
     * See {@link #addHeader(String, Object)}.
     *
     * @param headers headers to be added
     * @return this headers
     * @see #addHeader(String, Object)
     */
    public GekHttpHeaders addHeaders(Map<String, ?> headers) {
        headers.forEach(this::addHeader);
        return this;
    }

    /**
     * Returns backed map of this headers object. The map and this object share the data, so any operation will reflect
     * each other.
     *
     * @return backed map of this headers object
     */
    public Map<String, List<String>> asMap() {
        return headerMap;
    }

    /**
     * Removes header.
     *
     * @param key header key
     * @return this headers
     */
    public GekHttpHeaders removeHeader(String key) {
        headerMap.remove(key);
        return this;
    }

    /**
     * Clears all headers.
     *
     * @return this headers
     */
    public GekHttpHeaders clearHeaders() {
        headerMap.clear();
        return this;
    }

    @Override
    public String toString() {
        return headerMap.toString();
    }
}
