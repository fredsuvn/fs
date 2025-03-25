package xyz.sunqian.common.base.value;

/**
 * Primitive {@code long} version of {@link Val}.
 *
 * @author sunqian
 */
public interface LongVal extends PrimitiveToVal<Long> {

    /**
     * Returns a {@link LongVal} holding the {@code 0}.
     *
     * @return a {@link LongVal} holding the {@code 0}
     */
    static LongVal ofZero() {
        return ValBack.OF_ZERO_LONG;
    }

    /**
     * Returns a {@link LongVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link LongVal} holding the specified value
     */
    static LongVal of(long value) {
        return ValBack.of(value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    long get();

    @Override
    default Val<Long> toVal() {
        return Val.of(get());
    }
}
