package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;

/**
 * Primitive {@code boolean} version of {@link Val}.
 *
 * @author sunqian
 */
@Immutable
public interface BooleanVal extends PrimitiveToVal<Boolean> {

    /**
     * Returns a {@link BooleanVal} holding the {@code true}.
     *
     * @return a {@link BooleanVal} holding the {@code true}
     */
    static BooleanVal ofTrue() {
        return ValBack.OF_TRUE;
    }

    /**
     * Returns a {@link BooleanVal} holding the {@code true}.
     *
     * @return a {@link BooleanVal} holding the {@code true}
     */
    static BooleanVal ofFalse() {
        return ValBack.OF_FALSE;
    }

    /**
     * Returns a {@link BooleanVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link BooleanVal} holding the specified value
     */
    static BooleanVal of(boolean value) {
        return ValBack.of(value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    boolean get();

    @Override
    default Val<Boolean> toVal() {
        return Val.of(get());
    }
}
