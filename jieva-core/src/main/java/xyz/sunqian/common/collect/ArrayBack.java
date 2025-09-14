package xyz.sunqian.common.collect;

import xyz.sunqian.annotations.Nonnull;

final class ArrayBack {

    static @Nonnull ArrayOperator operator(@Nonnull Class<?> arrayType) throws IllegalArgumentException {
        if (!arrayType.isArray()) {
            throw new IllegalArgumentException("Not an array type: " + arrayType.getTypeName() + ".");
        }
        if (boolean[].class.equals(arrayType)) {
            return BooleanArrayOperator.INST;
        }
        if (byte[].class.equals(arrayType)) {
            return ByteArrayOperator.INST;
        }
        if (short[].class.equals(arrayType)) {
            return ShortArrayOperator.INST;
        }
        if (char[].class.equals(arrayType)) {
            return CharArrayOperator.INST;
        }
        if (int[].class.equals(arrayType)) {
            return IntArrayOperator.INST;
        }
        if (long[].class.equals(arrayType)) {
            return LongArrayOperator.INST;
        }
        if (float[].class.equals(arrayType)) {
            return FloatArrayOperator.INST;
        }
        if (double[].class.equals(arrayType)) {
            return DoubleArrayOperator.INST;
        }
        return ObjectArrayOperator.INST;
    }

    enum BooleanArrayOperator implements ArrayOperator {

        INST;

        @Override
        public Object get(@Nonnull Object array, int index) {
            return ((boolean[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) {
            ((boolean[]) array)[index] = (boolean) value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((boolean[]) array).length;
        }
    }

    enum ByteArrayOperator implements ArrayOperator {

        INST;

        @Override
        public Object get(@Nonnull Object array, int index) {
            return ((byte[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) {
            ((byte[]) array)[index] = (byte) value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((byte[]) array).length;
        }
    }

    enum ShortArrayOperator implements ArrayOperator {

        INST;

        @Override
        public Object get(@Nonnull Object array, int index) {
            return ((short[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) {
            ((short[]) array)[index] = (short) value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((short[]) array).length;
        }
    }

    enum CharArrayOperator implements ArrayOperator {

        INST;

        @Override
        public Object get(@Nonnull Object array, int index) {
            return ((char[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) {
            ((char[]) array)[index] = (char) value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((char[]) array).length;
        }
    }

    enum IntArrayOperator implements ArrayOperator {

        INST;

        @Override
        public Object get(@Nonnull Object array, int index) {
            return ((int[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) {
            ((int[]) array)[index] = (int) value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((int[]) array).length;
        }
    }

    enum LongArrayOperator implements ArrayOperator {

        INST;

        @Override
        public Object get(@Nonnull Object array, int index) {
            return ((long[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) {
            ((long[]) array)[index] = (long) value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((long[]) array).length;
        }
    }

    enum FloatArrayOperator implements ArrayOperator {

        INST;

        @Override
        public Object get(@Nonnull Object array, int index) {
            return ((float[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) {
            ((float[]) array)[index] = (float) value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((float[]) array).length;
        }
    }

    enum DoubleArrayOperator implements ArrayOperator {

        INST;

        @Override
        public Object get(@Nonnull Object array, int index) {
            return ((double[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) {
            ((double[]) array)[index] = (double) value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((double[]) array).length;
        }
    }

    enum ObjectArrayOperator implements ArrayOperator {

        INST;

        @Override
        public Object get(@Nonnull Object array, int index) {
            return ((Object[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) {
            ((Object[]) array)[index] = value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((Object[]) array).length;
        }
    }
}
