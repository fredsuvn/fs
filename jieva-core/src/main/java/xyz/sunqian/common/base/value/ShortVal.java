package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;

/**
 * Primitive {@code short} version of {@link Val}.
 *
 * @author sunqian
 */
@Immutable
public interface ShortVal extends PrimitiveToVal<Short> {

    /**
     * Returns a {@link ShortVal} holding the {@code 0}.
     *
     * @return a {@link ShortVal} holding the {@code 0}
     */
    static ShortVal ofZero() {
        return ValBack.OF_ZERO_SHORT;
    }

    /**
     * Returns a {@link ShortVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link ShortVal} holding the specified value
     */
    static ShortVal of(short value) {
        return ValBack.of(value);
    }

    /**
     * Returns a {@link ShortVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link ShortVal} holding the specified value
     */
    static ShortVal of(int value) {
        return of((short) value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    short get();

    @Override
    default Val<Short> toVal() {
        return Val.of(get());
    }
}
