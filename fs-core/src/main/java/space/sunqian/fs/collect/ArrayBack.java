package space.sunqian.fs.collect;

import space.sunqian.annotation.Nonnull;

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
        public Object get(@Nonnull Object array, int index) throws ArrayIndexOutOfBoundsException {
            return ((boolean[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) throws ArrayIndexOutOfBoundsException {
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
        public Object get(@Nonnull Object array, int index) throws ArrayIndexOutOfBoundsException {
            return ((byte[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) throws ArrayIndexOutOfBoundsException {
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
        public Object get(@Nonnull Object array, int index) throws ArrayIndexOutOfBoundsException {
            return ((short[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) throws ArrayIndexOutOfBoundsException {
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
        public Object get(@Nonnull Object array, int index) throws ArrayIndexOutOfBoundsException {
            return ((char[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) throws ArrayIndexOutOfBoundsException {
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
        public Object get(@Nonnull Object array, int index) throws ArrayIndexOutOfBoundsException {
            return ((int[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) throws ArrayIndexOutOfBoundsException {
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
        public Object get(@Nonnull Object array, int index) throws ArrayIndexOutOfBoundsException {
            return ((long[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) throws ArrayIndexOutOfBoundsException {
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
        public Object get(@Nonnull Object array, int index) throws ArrayIndexOutOfBoundsException {
            return ((float[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) throws ArrayIndexOutOfBoundsException {
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
        public Object get(@Nonnull Object array, int index) throws ArrayIndexOutOfBoundsException {
            return ((double[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) throws ArrayIndexOutOfBoundsException {
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
        public Object get(@Nonnull Object array, int index) throws ArrayIndexOutOfBoundsException {
            return ((Object[]) array)[index];
        }

        @Override
        public void set(@Nonnull Object array, int index, Object value) throws ArrayIndexOutOfBoundsException {
            ((Object[]) array)[index] = value;
        }

        @Override
        public int size(@Nonnull Object array) {
            return ((Object[]) array).length;
        }
    }

    private ArrayBack() {
    }
}
