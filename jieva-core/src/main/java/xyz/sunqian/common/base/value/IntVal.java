package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;

/**
 * Primitive {@code int} version of {@link Val}.
 *
 * @author sunqian
 */
@Immutable
public interface IntVal extends PrimitiveToVal<Integer> {

    /**
     * Returns a {@link IntVal} holding the {@code 0}.
     *
     * @return a {@link IntVal} holding the {@code 0}
     */
    static @Nonnull IntVal ofZero() {
        return ValImpls.OF_ZERO_INT;
    }

    /**
     * Returns a {@link IntVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link IntVal} holding the specified value
     */
    static @Nonnull IntVal of(int value) {
        return ValImpls.of(value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    int get();

    @Override
    default @Nonnull Val<Integer> toVal() {
        return Val.of(get());
    }
}
