package xyz.sunqian.common.ref;

import lombok.AllArgsConstructor;
import xyz.sunqian.annotations.Nullable;

final class VarImpls {

    static <T> Var<T> of(@Nullable T value) {
        return new VarImpl<>(value);
    }

    static BooleanVar ofBoolean(boolean value) {
        return new BooleanVarImpl(value);
    }

    static IntVar ofInt(int value) {
        return new IntVarImpl(value);
    }

    static LongVar ofLong(long value) {
        return new LongVarImpl(value);
    }

    static ShortVar ofShort(short value) {
        return new ShortVarImpl(value);
    }

    static FloatVar ofFloat(float value) {
        return new FloatVarImpl(value);
    }

    static DoubleVar ofDouble(double value) {
        return new DoubleVarImpl(value);
    }

    static ByteVar ofByte(byte value) {
        return new ByteVarImpl(value);
    }

    static CharVar ofChar(char value) {
        return new CharVarImpl(value);
    }

    @AllArgsConstructor
    private static final class VarImpl<T> implements Var<T> {

        private T value;

        @Override
        public @Nullable T get() {
            return value;
        }

        @Override
        public Var<T> set(@Nullable T value) {
            this.value = value;
            return this;
        }
    }

    @AllArgsConstructor
    private static final class BooleanVarImpl implements BooleanVar {

        private boolean value;

        @Override
        public boolean get() {
            return value;
        }

        @Override
        public BooleanVar set(boolean value) {
            this.value = value;
            return this;
        }

        @Override
        public boolean toggleAndGet() {
            value = !value;
            return value;
        }

        @Override
        public boolean getAndToggle() {
            value = !value;
            return !value;
        }
    }

    @AllArgsConstructor
    private static final class IntVarImpl implements IntVar {

        private int value;

        @Override
        public int get() {
            return value;
        }

        @Override
        public IntVar set(int value) {
            this.value = value;
            return this;
        }

        @Override
        public int incrementAndGet() {
            return ++value;
        }

        @Override
        public int getAndIncrement() {
            return value++;
        }

        @Override
        public IntVar add(int value) {
            this.value += value;
            return this;
        }
    }

    @AllArgsConstructor
    private static final class LongVarImpl implements LongVar {

        private long value;

        @Override
        public long get() {
            return value;
        }

        @Override
        public LongVar set(long value) {
            this.value = value;
            return this;
        }

        @Override
        public long incrementAndGet() {
            return ++value;
        }

        @Override
        public long getAndIncrement() {
            return value++;
        }

        @Override
        public LongVar add(long value) {
            this.value += value;
            return this;
        }
    }

    @AllArgsConstructor
    private static final class ShortVarImpl implements ShortVar {

        private short value;

        @Override
        public short get() {
            return value;
        }

        @Override
        public ShortVar set(short value) {
            this.value = value;
            return this;
        }

        @Override
        public short incrementAndGet() {
            return ++value;
        }

        @Override
        public short getAndIncrement() {
            return value++;
        }

        @Override
        public ShortVar add(int value) {
            this.value += (short) value;
            return this;
        }
    }

    @AllArgsConstructor
    private static final class FloatVarImpl implements FloatVar {

        private float value;

        @Override
        public float get() {
            return value;
        }

        @Override
        public FloatVar set(float value) {
            this.value = value;
            return this;
        }

        @Override
        public FloatVar add(float value) {
            this.value += value;
            return this;
        }
    }

    @AllArgsConstructor
    private static final class DoubleVarImpl implements DoubleVar {

        private double value;

        @Override
        public double get() {
            return value;
        }

        @Override
        public DoubleVar set(double value) {
            this.value = value;
            return this;
        }

        @Override
        public DoubleVar add(double value) {
            this.value += value;
            return this;
        }
    }

    @AllArgsConstructor
    private static final class ByteVarImpl implements ByteVar {

        private byte value;

        @Override
        public byte get() {
            return value;
        }

        @Override
        public ByteVar set(byte value) {
            this.value = value;
            return this;
        }

        @Override
        public byte incrementAndGet() {
            return ++value;
        }

        @Override
        public byte getAndIncrement() {
            return value++;
        }

        @Override
        public ByteVar add(int value) {
            this.value += (byte) value;
            return this;
        }
    }

    @AllArgsConstructor
    private static final class CharVarImpl implements CharVar {

        private char value;

        @Override
        public char get() {
            return value;
        }

        @Override
        public CharVar set(char value) {
            this.value = value;
            return this;
        }

        @Override
        public CharVar add(int value) {
            this.value += (char) value;
            return this;
        }

        @Override
        public char incrementAndGet() {
            return ++value;
        }

        @Override
        public char getAndIncrement() {
            return value++;
        }
    }
}
