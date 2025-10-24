package space.sunqian.common.base.value;

import space.sunqian.annotations.Nonnull;

final class ValBack {

    static final @Nonnull Val<?> OF_NULL = of(null);
    static final @Nonnull BooleanVal OF_TRUE = of(true);
    static final @Nonnull BooleanVal OF_FALSE = of(false);
    static final @Nonnull ByteVal OF_ZERO_BYTE = of((byte) 0);
    static final @Nonnull ShortVal OF_ZERO_SHORT = of((short) 0);
    static final @Nonnull CharVal OF_ZERO_CHAR = of('0');
    static final @Nonnull IntVal OF_ZERO_INT = of(0);
    static final @Nonnull LongVal OF_ZERO_LONG = ValBack.of(0L);
    static final @Nonnull FloatVal OF_ZERO_FLOAT = ValBack.of(0.0f);
    static final @Nonnull DoubleVal OF_ZERO_DOUBLE = ValBack.of(0.0);

    static <T> @Nonnull Val<T> of(T value) {
        return new ValImpl<>(value);
    }

    static @Nonnull BooleanVal of(boolean value) {
        return new BooleanValImpl(value);
    }

    static @Nonnull ByteVal of(byte value) {
        return new ByteValImpl(value);
    }

    static @Nonnull ShortVal of(short value) {
        return new ShortValImpl(value);
    }

    static @Nonnull CharVal of(char value) {
        return new CharValImpl(value);
    }

    static @Nonnull IntVal of(int value) {
        return new IntValImpl(value);
    }

    static @Nonnull LongVal of(long value) {
        return new LongValImpl(value);
    }

    static @Nonnull FloatVal of(float value) {
        return new FloatValImpl(value);
    }

    static @Nonnull DoubleVal of(double value) {
        return new DoubleValImpl(value);
    }

    private static final class ValImpl<T> implements Val<T> {

        private final T value;

        private ValImpl(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }
    }

    private static final class BooleanValImpl implements BooleanVal {

        private final boolean value;

        private BooleanValImpl(boolean value) {
            this.value = value;
        }

        @Override
        public boolean get() {
            return value;
        }
    }

    private static final class ByteValImpl implements ByteVal {

        private final byte value;

        private ByteValImpl(byte value) {
            this.value = value;
        }

        @Override
        public byte get() {
            return value;
        }
    }

    private static final class ShortValImpl implements ShortVal {

        private final short value;

        private ShortValImpl(short value) {
            this.value = value;
        }

        @Override
        public short get() {
            return value;
        }
    }

    private static final class CharValImpl implements CharVal {

        private final char value;

        private CharValImpl(char value) {
            this.value = value;
        }

        @Override
        public char get() {
            return value;
        }
    }

    private static final class IntValImpl implements IntVal {

        private final int value;

        private IntValImpl(int value) {
            this.value = value;
        }

        @Override
        public int get() {
            return value;
        }
    }

    private static final class LongValImpl implements LongVal {

        private final long value;

        private LongValImpl(long value) {
            this.value = value;
        }

        @Override
        public long get() {
            return value;
        }
    }

    private static final class FloatValImpl implements FloatVal {

        private final float value;

        private FloatValImpl(float value) {
            this.value = value;
        }

        @Override
        public float get() {
            return value;
        }
    }

    private static final class DoubleValImpl implements DoubleVal {

        private final double value;

        private DoubleValImpl(double value) {
            this.value = value;
        }

        @Override
        public double get() {
            return value;
        }
    }
}
