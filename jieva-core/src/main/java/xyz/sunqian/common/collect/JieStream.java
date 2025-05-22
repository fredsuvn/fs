package xyz.sunqian.common.collect;

import xyz.sunqian.annotations.Nonnull;

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
     * Returns a {@link Stream} from the given elements.
     *
     * @param elements the given elements
     * @param <T>      the component type
     * @return a {@link Stream} from the given elements
     */
    @SafeVarargs
    public static <T> @Nonnull Stream<T> stream(T @Nonnull ... elements) {
        if (JieArray.isEmpty(elements)) {
            return Stream.empty();
        }
        return Arrays.stream(elements);
    }

    /**
     * Returns a {@link Stream} from the given elements.
     *
     * @param elements the given elements
     * @param <T>      the component type
     * @return a {@link Stream} from the given elements
     */
    public static <T> @Nonnull Stream<T> stream(@Nonnull Iterable<T> elements) {
        if (JieCollect.isEmpty(elements)) {
            return Stream.empty();
        }
        if (elements instanceof Collection) {
            return ((Collection<T>) elements).stream();
        }
        return StreamSupport.stream(elements.spliterator(), false);
    }
}
