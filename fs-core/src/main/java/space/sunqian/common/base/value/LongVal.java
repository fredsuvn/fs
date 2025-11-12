package space.sunqian.common.base.value;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;

/**
 * Primitive {@code long} version of {@link Val}.
 *
 * @author sunqian
 */
@Immutable
public interface LongVal extends PrimitiveToVal<Long> {

    /**
     * Returns a {@link LongVal} holding the {@code 0}.
     *
     * @return a {@link LongVal} holding the {@code 0}
     */
    static @Nonnull LongVal ofZero() {
        return ValBack.OF_ZERO_LONG;
    }

    /**
     * Returns a {@link LongVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link LongVal} holding the specified value
     */
    static @Nonnull LongVal of(long value) {
        return ValBack.of(value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    long get();

    @Override
    default @Nonnull Val<Long> toVal() {
        return Val.of(get());
    }
}
