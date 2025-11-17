package space.sunqian.common.base.value;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;

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
    static @Nonnull BooleanVal ofTrue() {
        return ValBack.OF_TRUE;
    }

    /**
     * Returns a {@link BooleanVal} holding the {@code true}.
     *
     * @return a {@link BooleanVal} holding the {@code true}
     */
    static @Nonnull BooleanVal ofFalse() {
        return ValBack.OF_FALSE;
    }

    /**
     * Returns a {@link BooleanVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link BooleanVal} holding the specified value
     */
    static @Nonnull BooleanVal of(boolean value) {
        return ValBack.of(value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    boolean get();

    @Override
    default @Nonnull Val<Boolean> toVal() {
        return Val.of(get());
    }
}
