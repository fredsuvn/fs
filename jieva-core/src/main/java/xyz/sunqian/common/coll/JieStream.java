package xyz.sunqian.common.coll;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Static utility class for {@link Stream}.
 *
 * @author sunqian
 */
public class JieStream {

    /**
     * Returns a {@link Stream} from the given elements, not parallel.
     *
     * @param elements the given elements
     * @param <T>      the component type
     * @return a {@link Stream} from the given elements, not parallel
     */
    public static <T> Stream<T> stream(T[] elements) {
        if (JieArray.isEmpty(elements)) {
            return Stream.empty();
        }
        return Arrays.stream(elements);
    }

    /**
     * Returns a {@link Stream} from the given elements, not parallel.
     *
     * @param elements the given elements
     * @param <T>      the component type
     * @return a {@link Stream} from the given elements, not parallel
     */
    public static <T> Stream<T> stream(Iterable<T> elements) {
        if (JieColl.isEmpty(elements)) {
            return Stream.empty();
        }
        if (elements instanceof Collection) {
            return ((Collection<T>) elements).stream();
        }
        return StreamSupport.stream(elements.spliterator(), false);
    }
}
