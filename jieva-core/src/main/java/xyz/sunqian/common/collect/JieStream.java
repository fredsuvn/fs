package xyz.sunqian.common.collect;

import xyz.sunqian.annotations.Nonnull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
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

    public static <T> @Nonnull Supplier<T> toSupplier(@Nonnull Stream<? extends T> stream) {
        return new Supplier<T>() {

            private final @Nonnull Iterator<? extends T> iterator = stream.iterator();

            @Override
            public T get() {
                return iterator.next();
            }
        };
    }

    public static @Nonnull IntSupplier toSupplier(@Nonnull IntStream stream) {
        return new IntSupplier() {

            private final @Nonnull PrimitiveIterator.OfInt iterator = stream.iterator();

            @Override
            public int getAsInt() {
                return iterator.next();
            }
        };
    }

    public static @Nonnull LongSupplier toSupplier(@Nonnull LongStream stream) {
        return new LongSupplier() {

            private final @Nonnull PrimitiveIterator.OfLong iterator = stream.iterator();

            @Override
            public long getAsLong() {
                return iterator.next();
            }
        };
    }

    public static @Nonnull DoubleSupplier toSupplier(@Nonnull DoubleStream stream) {
        return new DoubleSupplier() {

            private final @Nonnull PrimitiveIterator.OfDouble iterator = stream.iterator();

            @Override
            public double getAsDouble() {
                return iterator.next();
            }
        };
    }
}
