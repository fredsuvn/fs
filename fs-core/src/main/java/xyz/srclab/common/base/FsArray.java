package xyz.srclab.common.base;

import xyz.srclab.build.annotations.FsMethod;
import xyz.srclab.build.annotations.FsMethods;

import java.lang.reflect.Array;
import java.util.function.Function;

/**
 * Array utilities.
 *
 * @author fredsuvn
 */
@FsMethods
public class FsArray {

    /**
     * Returns given arguments as an array.
     *
     * @param args given arguments
     */
    @SafeVarargs
    public static <T> T[] array(T... args) {
        return args;
    }

    /**
     * Maps source array of type T[] to dest array of typeR[].
     * If the dest array's length equals to source array, the mapped elements will be put into the dest array,
     * else create and put into a new array.
     *
     * @param <T>    component type of source array
     * @param <R>    component type of dest array
     * @param source the source array
     * @param dest   the dest array
     * @param mapper given mapper
     */
    @FsMethod(name = "mapArray")
    public static <T, R> R[] map(T[] source, R[] dest, Function<T, R> mapper) {
        R[] result;
        if (dest.length == source.length) {
            result = dest;
        } else {
            result = newArray(dest.getClass().getComponentType(), source.length);
        }
        for (int i = 0; i < source.length; i++) {
            result[i] = mapper.apply(source[i]);
        }
        return result;
    }

    /**
     * Creates a new array of given component type and length.
     *
     * @param componentType given component type
     * @param length        given length
     */
    public static <T> T[] newArray(Class<?> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }
}
