package tests.core.collect;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.BooleanArraySlice;
import space.sunqian.fs.collect.ByteArraySlice;
import space.sunqian.fs.collect.CharArraySlice;
import space.sunqian.fs.collect.DoubleArraySlice;
import space.sunqian.fs.collect.FloatArraySlice;
import space.sunqian.fs.collect.IntArraySlice;
import space.sunqian.fs.collect.LongArraySlice;
import space.sunqian.fs.collect.ObjectArraySlice;
import space.sunqian.fs.collect.ShortArraySlice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArraySliceTest {

    @Test
    public void testBooleanArraySlice() {
        boolean[] array = new boolean[]{true, false, true, false};
        BooleanArraySlice slice = BooleanArraySlice.of(array, 1, 3);

        assertEquals(array, slice.array());
        assertEquals(1, slice.startIndex());
        assertEquals(3, slice.endIndex());
        assertEquals(2, slice.length());

        assertEquals(false, slice.get(0));
        assertEquals(true, slice.get(1));

        slice.set(0, true);
        slice.set(1, false);
        assertEquals(true, array[1]);
        assertEquals(false, array[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(-1, true));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(2, true));
        assertThrows(IndexOutOfBoundsException.class, () -> BooleanArraySlice.of(array, 3, 2));
    }

    @Test
    public void testByteArraySlice() {
        byte[] array = new byte[]{1, 2, 3, 4, 5};
        ByteArraySlice slice = ByteArraySlice.of(array, 1, 4);

        assertEquals(array, slice.array());
        assertEquals(1, slice.startIndex());
        assertEquals(4, slice.endIndex());
        assertEquals(3, slice.length());

        assertEquals((byte) 2, slice.get(0));
        assertEquals((byte) 3, slice.get(1));
        assertEquals((byte) 4, slice.get(2));

        slice.set(0, (byte) 9);
        slice.set(1, (byte) 8);
        assertEquals((byte) 9, array[1]);
        assertEquals((byte) 8, array[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(-1, (byte) 0));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(3, (byte) 0));
        assertThrows(IndexOutOfBoundsException.class, () -> ByteArraySlice.of(array, 3, 2));
    }

    @Test
    public void testCharArraySlice() {
        char[] array = new char[]{'a', 'b', 'c', 'd', 'e'};
        CharArraySlice slice = CharArraySlice.of(array, 1, 4);

        assertEquals(array, slice.array());
        assertEquals(1, slice.startIndex());
        assertEquals(4, slice.endIndex());
        assertEquals(3, slice.length());

        assertEquals('b', slice.get(0));
        assertEquals('c', slice.get(1));
        assertEquals('d', slice.get(2));

        slice.set(0, 'x');
        slice.set(1, 'y');
        assertEquals('x', array[1]);
        assertEquals('y', array[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(-1, ' '));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(3, ' '));
        assertThrows(IndexOutOfBoundsException.class, () -> CharArraySlice.of(array, 3, 2));
    }

    @Test
    public void testShortArraySlice() {
        short[] array = new short[]{1, 2, 3, 4, 5};
        ShortArraySlice slice = ShortArraySlice.of(array, 1, 4);

        assertEquals(array, slice.array());
        assertEquals(1, slice.startIndex());
        assertEquals(4, slice.endIndex());
        assertEquals(3, slice.length());

        assertEquals((short) 2, slice.get(0));
        assertEquals((short) 3, slice.get(1));
        assertEquals((short) 4, slice.get(2));

        slice.set(0, (short) 9);
        slice.set(1, (short) 8);
        assertEquals((short) 9, array[1]);
        assertEquals((short) 8, array[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(-1, (short) 0));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(3, (short) 0));
        assertThrows(IndexOutOfBoundsException.class, () -> ShortArraySlice.of(array, 3, 2));
    }

    @Test
    public void testIntArraySlice() {
        int[] array = new int[]{1, 2, 3, 4, 5};
        IntArraySlice slice = IntArraySlice.of(array, 1, 4);

        assertEquals(array, slice.array());
        assertEquals(1, slice.startIndex());
        assertEquals(4, slice.endIndex());
        assertEquals(3, slice.length());

        assertEquals(2, slice.get(0));
        assertEquals(3, slice.get(1));
        assertEquals(4, slice.get(2));

        slice.set(0, 9);
        slice.set(1, 8);
        assertEquals(9, array[1]);
        assertEquals(8, array[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> IntArraySlice.of(array, 3, 2));
    }

    @Test
    public void testLongArraySlice() {
        long[] array = new long[]{1, 2, 3, 4, 5};
        LongArraySlice slice = LongArraySlice.of(array, 1, 4);

        assertEquals(array, slice.array());
        assertEquals(1, slice.startIndex());
        assertEquals(4, slice.endIndex());
        assertEquals(3, slice.length());

        assertEquals(2L, slice.get(0));
        assertEquals(3L, slice.get(1));
        assertEquals(4L, slice.get(2));

        slice.set(0, 9L);
        slice.set(1, 8L);
        assertEquals(9L, array[1]);
        assertEquals(8L, array[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(-1, 0L));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(3, 0L));
        assertThrows(IndexOutOfBoundsException.class, () -> LongArraySlice.of(array, 3, 2));
    }

    @Test
    public void testFloatArraySlice() {
        float[] array = new float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        FloatArraySlice slice = FloatArraySlice.of(array, 1, 4);

        assertEquals(array, slice.array());
        assertEquals(1, slice.startIndex());
        assertEquals(4, slice.endIndex());
        assertEquals(3, slice.length());

        assertEquals(2.0f, slice.get(0));
        assertEquals(3.0f, slice.get(1));
        assertEquals(4.0f, slice.get(2));

        slice.set(0, 9.0f);
        slice.set(1, 8.0f);
        assertEquals(9.0f, array[1]);
        assertEquals(8.0f, array[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(-1, 0.0f));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(3, 0.0f));
        assertThrows(IndexOutOfBoundsException.class, () -> FloatArraySlice.of(array, 3, 2));
    }

    @Test
    public void testDoubleArraySlice() {
        double[] array = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
        DoubleArraySlice slice = DoubleArraySlice.of(array, 1, 4);

        assertEquals(array, slice.array());
        assertEquals(1, slice.startIndex());
        assertEquals(4, slice.endIndex());
        assertEquals(3, slice.length());

        assertEquals(2.0, slice.get(0));
        assertEquals(3.0, slice.get(1));
        assertEquals(4.0, slice.get(2));

        slice.set(0, 9.0);
        slice.set(1, 8.0);
        assertEquals(9.0, array[1]);
        assertEquals(8.0, array[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(-1, 0.0));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(3, 0.0));
        assertThrows(IndexOutOfBoundsException.class, () -> DoubleArraySlice.of(array, 3, 2));
    }

    @Test
    public void testObjectArraySlice() {
        String[] array = new String[]{"a", "b", "c", "d", "e"};
        ObjectArraySlice<String> slice = ObjectArraySlice.of(array, 1, 4);

        assertEquals(array, slice.array());
        assertEquals(1, slice.startIndex());
        assertEquals(4, slice.endIndex());
        assertEquals(3, slice.length());

        assertEquals("b", slice.get(0));
        assertEquals("c", slice.get(1));
        assertEquals("d", slice.get(2));

        slice.set(0, "x");
        slice.set(1, "y");
        assertEquals("x", array[1]);
        assertEquals("y", array[2]);

        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(-1, ""));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> slice.set(3, ""));
        assertThrows(IndexOutOfBoundsException.class, () -> ObjectArraySlice.of(array, 3, 2));
    }
}