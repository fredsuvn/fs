package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Nonnull;

/**
 * This interface is used to convert {@code Var} of primitive type to {@link Var} of wrapper type, such as
 * {@link IntVar} to {@code Var<Integer>}.
 *
 * @param <T> the wrapper type
 * @author sunqian
 */
public interface PrimitiveToVar<T> extends PrimitiveToVal<T> {

    /**
     * Returns a {@link Var} initialized with the wrapper type of current primitive type.
     *
     * @return a {@link Var} initialized with the wrapper type of current primitive type
     */
    @Nonnull
    Var<T> toVar();
}
