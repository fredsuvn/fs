package space.sunqian.common.base.value;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;

/**
 * Primitive {@code float} version of {@link Val}.
 *
 * @author sunqian
 */
@Immutable
public interface FloatVal extends PrimitiveToVal<Float> {

    /**
     * Returns a {@link FloatVal} holding the {@code 0}.
     *
     * @return a {@link FloatVal} holding the {@code 0}
     */
    static @Nonnull FloatVal ofZero() {
        return ValBack.OF_ZERO_FLOAT;
    }

    /**
     * Returns a {@link FloatVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link FloatVal} holding the specified value
     */
    static @Nonnull FloatVal of(float value) {
        return ValBack.of(value);
    }

    /**
     * Returns a {@link FloatVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link FloatVal} holding the specified value
     */
    static @Nonnull FloatVal of(double value) {
        return of((float) value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    float get();

    @Override
    default @Nonnull Val<Float> toVal() {
        return Val.of(get());
    }
}
