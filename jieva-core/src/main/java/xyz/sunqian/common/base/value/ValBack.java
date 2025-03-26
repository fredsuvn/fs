package xyz.sunqian.common.base.value;

import xyz.sunqian.annotations.Nullable;

final class ValBack {

    static Val<?> OF_NULL = of(null);
    static BooleanVal OF_TRUE = of(true);
    static BooleanVal OF_FALSE = of(false);
    static ByteVal OF_ZERO_BYTE = of((byte) 0);
    static ShortVal OF_ZERO_SHORT = of((short) 0);
    static CharVal OF_ZERO_CHAR = of('0');
    static IntVal OF_ZERO_INT = of(0);
    static LongVal OF_ZERO_LONG = ValBack.of(0L);
    static FloatVal OF_ZERO_FLOAT = ValBack.of(0.0f);
    static DoubleVal OF_ZERO_DOUBLE = ValBack.of(0.0);

    static <T> Val<T> of(@Nullable T value) {
        return new ValImpl<>(value);
    }

    static BooleanVal of(boolean value) {
        return new BooleanValImpl(value);
    }

    static ByteVal of(byte value) {
        return new ByteValImpl(value);
    }

    static ShortVal of(short value) {
        return new ShortValImpl(value);
    }

    static CharVal of(char value) {
        return new CharValImpl(value);
    }

    static IntVal of(int value) {
        return new IntValImpl(value);
    }

    static LongVal of(long value) {
        return new LongValImpl(value);
    }

    static FloatVal of(float value) {
        return new FloatValImpl(value);
    }

    static DoubleVal of(double value) {
        return new DoubleValImpl(value);
    }

    private static final class ValImpl<T> implements Val<T> {

        private final T value;

        private ValImpl(@Nullable T value) {
            this.value = value;
        }

        @Override
        public @Nullable T get() {
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
