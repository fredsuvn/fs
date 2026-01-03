package space.sunqian.fs.base.value;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

final class VarBack {

    static <T> @Nonnull Var<T> of(T value) {
        return new VarImpl<>(value);
    }

    static @Nonnull BooleanVar of(boolean value) {
        return new BooleanVarImpl(value);
    }

    static @Nonnull ByteVar of(byte value) {
        return new ByteVarImpl(value);
    }

    static @Nonnull ShortVar of(short value) {
        return new ShortVarImpl(value);
    }

    static @Nonnull CharVar of(char value) {
        return new CharVarImpl(value);
    }

    static @Nonnull IntVar of(int value) {
        return new IntVarImpl(value);
    }

    static @Nonnull LongVar of(long value) {
        return new LongVarImpl(value);
    }

    static @Nonnull FloatVar of(float value) {
        return new FloatVarImpl(value);
    }

    static @Nonnull DoubleVar of(double value) {
        return new DoubleVarImpl(value);
    }

    private static final class VarImpl<T> implements Var<T> {

        private Object value;

        private VarImpl(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return Fs.as(value);
        }

        @Override
        public @Nonnull Var<T> set(T value) {
            this.value = value;
            return this;
        }
    }

    private static final class BooleanVarImpl implements BooleanVar {

        private boolean value;

        private BooleanVarImpl(boolean value) {
            this.value = value;
        }

        @Override
        public boolean get() {
            return value;
        }

        @Override
        public @Nonnull BooleanVar set(boolean value) {
            this.value = value;
            return this;
        }

        @Override
        public @Nonnull BooleanVar toggle() {
            value = !value;
            return this;
        }

        @Override
        public boolean toggleAndGet() {
            value = !value;
            return value;
        }

        @Override
        public boolean getAndToggle() {
            return !toggleAndGet();
        }
    }

    private static final class ByteVarImpl implements ByteVar {

        private byte value;

        private ByteVarImpl(byte value) {
            this.value = value;
        }

        @Override
        public byte get() {
            return value;
        }

        @Override
        public @Nonnull ByteVar set(byte value) {
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
        public @Nonnull ByteVar add(int value) {
            this.value += (byte) value;
            return this;
        }
    }

    private static final class ShortVarImpl implements ShortVar {

        private short value;

        private ShortVarImpl(short value) {
            this.value = value;
        }

        @Override
        public short get() {
            return value;
        }

        @Override
        public @Nonnull ShortVar set(short value) {
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
        public @Nonnull ShortVar add(int value) {
            this.value += (short) value;
            return this;
        }
    }

    private static final class CharVarImpl implements CharVar {

        private char value;

        private CharVarImpl(char value) {
            this.value = value;
        }

        @Override
        public char get() {
            return value;
        }

        @Override
        public @Nonnull CharVar set(char value) {
            this.value = value;
            return this;
        }

        @Override
        public @Nonnull CharVar add(int value) {
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

    private static final class IntVarImpl implements IntVar {

        private int value;

        private IntVarImpl(int value) {
            this.value = value;
        }

        @Override
        public int get() {
            return value;
        }

        @Override
        public @Nonnull IntVar set(int value) {
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
        public @Nonnull IntVar add(int value) {
            this.value += value;
            return this;
        }
    }

    private static final class LongVarImpl implements LongVar {

        private long value;

        private LongVarImpl(long value) {
            this.value = value;
        }

        @Override
        public long get() {
            return value;
        }

        @Override
        public @Nonnull LongVar set(long value) {
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
        public @Nonnull LongVar add(long value) {
            this.value += value;
            return this;
        }
    }

    private static final class FloatVarImpl implements FloatVar {

        private float value;

        private FloatVarImpl(float value) {
            this.value = value;
        }

        @Override
        public float get() {
            return value;
        }

        @Override
        public @Nonnull FloatVar set(float value) {
            this.value = value;
            return this;
        }

        @Override
        public @Nonnull FloatVar add(float value) {
            this.value += value;
            return this;
        }
    }

    private static final class DoubleVarImpl implements DoubleVar {

        private double value;

        private DoubleVarImpl(double value) {
            this.value = value;
        }

        @Override
        public double get() {
            return value;
        }

        @Override
        public @Nonnull DoubleVar set(double value) {
            this.value = value;
            return this;
        }

        @Override
        public @Nonnull DoubleVar add(double value) {
            this.value += value;
            return this;
        }
    }

    private VarBack() {
    }
}
