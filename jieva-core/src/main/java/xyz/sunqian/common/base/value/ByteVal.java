package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;

/**
 * Primitive {@code byte} version of {@link Val}.
 *
 * @author sunqian
 */
@Immutable
public interface ByteVal extends PrimitiveToVal<Byte> {

    /**
     * Returns a {@link ByteVal} holding the {@code 0}.
     *
     * @return a {@link ByteVal} holding the {@code 0}
     */
    static @Nonnull ByteVal ofZero() {
        return ValImpls.OF_ZERO_BYTE;
    }

    /**
     * Returns a {@link ByteVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link ByteVal} holding the specified value
     */
    static @Nonnull ByteVal of(byte value) {
        return ValImpls.of(value);
    }

    /**
     * Returns a {@link ByteVal} holding the specified value.
     *
     * @param value the specified value
     * @return a {@link ByteVal} holding the specified value
     */
    static @Nonnull ByteVal of(int value) {
        return of((byte) value);
    }

    /**
     * Returns the held value.
     *
     * @return the held value
     */
    byte get();

    @Override
    default @Nonnull Val<Byte> toVal() {
        return Val.of(get());
    }
}
