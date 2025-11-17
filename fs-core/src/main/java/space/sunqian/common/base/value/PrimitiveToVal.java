package space.sunqian.common.base.value;

import space.sunqian.annotations.Nonnull;

/**
 * This interface is used to convert {@code Val} of primitive type to {@link Val} of wrapper type, such as
 * {@link IntVal} to {@code Val<Integer>}.
 *
 * @param <T> the wrapper type
 * @author sunqian
 */
public interface PrimitiveToVal<T> {

    /**
     * Returns a {@link Val} holding the wrapper type of current primitive type.
     *
     * @return a {@link Val} holding the wrapper type of current primitive type
     */
    @Nonnull
    Val<T> toVal();
}
