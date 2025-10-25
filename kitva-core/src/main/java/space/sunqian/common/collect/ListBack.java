package space.sunqian.common.collect;

import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.Nonnull;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

final class ListBack {

    static @Nonnull List<Boolean> asList(boolean @Nonnull [] array) {
        return new BooleanArrayList(array);
    }

    static @Nonnull List<Byte> asList(byte @Nonnull [] array) {
        return new ByteArrayList(array);
    }

    static @Nonnull List<Short> asList(short @Nonnull [] array) {
        return new ShortArrayList(array);
    }

    static @Nonnull List<Character> asList(char @Nonnull [] array) {
        return new CharArrayList(array);
    }

    static @Nonnull List<Integer> asList(int @Nonnull [] array) {
        return new IntArrayList(array);
    }

    static @Nonnull List<Long> asList(long @Nonnull [] array) {
        return new LongArrayList(array);
    }

    static @Nonnull List<Float> asList(float @Nonnull [] array) {
        return new FloatArrayList(array);
    }

    static @Nonnull List<Double> asList(double @Nonnull [] array) {
        return new DoubleArrayList(array);
    }

    static <T> @Nonnull @Immutable List<T> immutableList(T @Nonnull [] array) {
        return ArrayKit.isEmpty(array) ? Collections.emptyList() : new ImmutableList<>(array);
    }

    static @Nonnull @Immutable List<@Nonnull Boolean> immutableList(boolean @Nonnull [] array) {
        return ArrayKit.isEmpty(array) ? Collections.emptyList() : new BooleanImmutableList(array);
    }

    static @Nonnull @Immutable List<@Nonnull Byte> immutableList(byte @Nonnull [] array) {
        return ArrayKit.isEmpty(array) ? Collections.emptyList() : new ByteImmutableList(array);
    }

    static @Nonnull @Immutable List<@Nonnull Short> immutableList(short @Nonnull [] array) {
        return ArrayKit.isEmpty(array) ? Collections.emptyList() : new ShortImmutableList(array);
    }

    static @Nonnull @Immutable List<@Nonnull Character> immutableList(char @Nonnull [] array) {
        return ArrayKit.isEmpty(array) ? Collections.emptyList() : new CharImmutableList(array);
    }

    static @Nonnull @Immutable List<@Nonnull Integer> immutableList(int @Nonnull [] array) {
        return ArrayKit.isEmpty(array) ? Collections.emptyList() : new IntImmutableList(array);
    }

    static @Nonnull @Immutable List<@Nonnull Long> immutableList(long @Nonnull [] array) {
        return ArrayKit.isEmpty(array) ? Collections.emptyList() : new LongImmutableList(array);
    }

    static @Nonnull @Immutable List<@Nonnull Float> immutableList(float @Nonnull [] array) {
        return ArrayKit.isEmpty(array) ? Collections.emptyList() : new FloatImmutableList(array);
    }

    static @Nonnull @Immutable List<@Nonnull Double> immutableList(double @Nonnull [] array) {
        return ArrayKit.isEmpty(array) ? Collections.emptyList() : new DoubleImmutableList(array);
    }

    @SafeVarargs
    static <T> @Nonnull List<T> compositeList(@Nonnull List<T> @Nonnull ... lists) {
        return new CompositeList<>(lists);
    }

    private static final class BooleanArrayList
        extends AbstractList<Boolean> implements RandomAccess, Serializable {

        private final boolean[] array;

        private BooleanArrayList(boolean[] array) {
            this.array = array;
        }

        @Override
        public Boolean get(int index) {
            return array[index];
        }

        @Override
        public Boolean set(int index, Boolean element) {
            Boolean old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class ByteArrayList
        extends AbstractList<Byte> implements RandomAccess, Serializable {

        private final byte[] array;

        private ByteArrayList(byte[] array) {
            this.array = array;
        }

        @Override
        public Byte get(int index) {
            return array[index];
        }

        @Override
        public Byte set(int index, Byte element) {
            Byte old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class ShortArrayList
        extends AbstractList<Short> implements RandomAccess, Serializable {

        private final short[] array;

        private ShortArrayList(short[] array) {
            this.array = array;
        }

        @Override
        public Short get(int index) {
            return array[index];
        }

        @Override
        public Short set(int index, Short element) {
            short old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class CharArrayList
        extends AbstractList<Character> implements RandomAccess, Serializable {

        private final char[] array;

        private CharArrayList(char[] array) {
            this.array = array;
        }

        @Override
        public Character get(int index) {
            return array[index];
        }

        @Override
        public Character set(int index, Character element) {
            Character old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class IntArrayList
        extends AbstractList<Integer> implements RandomAccess, Serializable {

        private final int[] array;

        private IntArrayList(int[] array) {
            this.array = array;
        }

        @Override
        public Integer get(int index) {
            return array[index];
        }

        @Override
        public Integer set(int index, Integer element) {
            Integer old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class LongArrayList
        extends AbstractList<Long> implements RandomAccess, Serializable {

        private final long[] array;

        private LongArrayList(long[] array) {
            this.array = array;
        }

        @Override
        public Long get(int index) {
            return array[index];
        }

        @Override
        public Long set(int index, Long element) {
            Long old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class FloatArrayList
        extends AbstractList<Float> implements RandomAccess, Serializable {

        private final float[] array;

        private FloatArrayList(float[] array) {
            this.array = array;
        }

        @Override
        public Float get(int index) {
            return array[index];
        }

        @Override
        public Float set(int index, Float element) {
            Float old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class DoubleArrayList
        extends AbstractList<Double> implements RandomAccess, Serializable {

        private final double[] array;

        private DoubleArrayList(double[] array) {
            this.array = array;
        }

        @Override
        public Double get(int index) {
            return array[index];
        }

        @Override
        public Double set(int index, Double element) {
            Double old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    @Immutable
    private static final class ImmutableList<T>
        extends AbstractList<T> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final T[] array;

        private ImmutableList(T[] array) {
            this.array = array;
        }

        @Override
        public T get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    @Immutable
    private static final class BooleanImmutableList
        extends AbstractList<Boolean> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final boolean[] array;

        private BooleanImmutableList(boolean[] array) {
            this.array = array;
        }

        @Override
        public Boolean get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    @Immutable
    private static final class ByteImmutableList
        extends AbstractList<Byte> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final byte[] array;

        private ByteImmutableList(byte[] array) {
            this.array = array;
        }

        @Override
        public Byte get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    @Immutable
    private static final class ShortImmutableList
        extends AbstractList<Short> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final short[] array;

        private ShortImmutableList(short[] array) {
            this.array = array;
        }

        @Override
        public Short get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    @Immutable
    private static final class CharImmutableList
        extends AbstractList<Character> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final char[] array;

        private CharImmutableList(char[] array) {
            this.array = array;
        }

        @Override
        public Character get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    @Immutable
    private static final class IntImmutableList
        extends AbstractList<Integer> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final int[] array;

        private IntImmutableList(int[] array) {
            this.array = array;
        }

        @Override
        public Integer get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    @Immutable
    private static final class LongImmutableList
        extends AbstractList<Long> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final long[] array;

        private LongImmutableList(long[] array) {
            this.array = array;
        }

        @Override
        public Long get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    @Immutable
    private static final class FloatImmutableList
        extends AbstractList<Float> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final float[] array;

        private FloatImmutableList(float[] array) {
            this.array = array;
        }

        @Override
        public Float get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    @Immutable
    private static final class DoubleImmutableList
        extends AbstractList<Double> implements RandomAccess, Serializable {

        private static final long serialVersionUID = 0L;

        private final double[] array;

        private DoubleImmutableList(double[] array) {
            this.array = array;
        }

        @Override
        public Double get(int index) {
            return array[index];
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class CompositeList<T>
        extends AbstractList<T> implements RandomAccess, Serializable {

        private final @Nonnull List<T> @Nonnull [] lists;

        private CompositeList(@Nonnull List<T> @Nonnull [] lists) {
            this.lists = lists;
        }

        @Override
        public T get(int index) {
            long pos = getElementPos(index);
            int listIndex = (int) (pos >>> 32);
            int elementIndex = (int) (pos & 0x00000000ffffffffL);
            return lists[listIndex].get(elementIndex);
        }

        @Override
        public T set(int index, T element) {
            long pos = getElementPos(index);
            int listIndex = (int) (pos >>> 32);
            int elementIndex = (int) (pos & 0x00000000ffffffffL);
            return lists[listIndex].set(elementIndex, element);
        }

        private long getElementPos(final int index) {
            int x = index;
            for (int i = 0; i < lists.length; i++) {
                List<T> list = lists[i];
                if (x < list.size()) {
                    return ((long) i << 32) | (x & 0x00000000ffffffffL);
                } else {
                    x -= list.size();
                }
            }
            throw new IndexOutOfBoundsException("index: " + index + ".");
        }

        @Override
        public int size() {
            int size = 0;
            for (List<T> list : lists) {
                size += list.size();
            }
            return size;
        }
    }
}
