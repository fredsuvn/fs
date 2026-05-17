package tests.core.collect;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.ArrayOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArrayOperatorTest {

    @Test
    public void testBooleanArrayOperator() {
        ArrayOperator operator = ArrayOperator.of(boolean[].class);
        boolean[] array = new boolean[]{true};
        assertEquals(true, operator.get(array, 0));
        operator.set(array, 0, false);
        assertEquals(false, operator.get(array, 0));
        assertEquals(1, operator.length(array));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, -1, false));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, 100));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, 100, false));
    }

    @Test
    public void testByteArrayOperator() {
        ArrayOperator operator = ArrayOperator.of(byte[].class);
        byte[] array = new byte[]{6};
        assertEquals((byte) 6, operator.get(array, 0));
        operator.set(array, 0, (byte) 9);
        assertEquals((byte) 9, operator.get(array, 0));
        assertEquals(1, operator.length(array));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, -1, (byte) 9));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, 100));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, 100, (byte) 9));
    }

    @Test
    public void testShortArrayOperator() {
        ArrayOperator operator = ArrayOperator.of(short[].class);
        short[] array = new short[]{6};
        assertEquals((short) 6, operator.get(array, 0));
        operator.set(array, 0, (short) 9);
        assertEquals((short) 9, operator.get(array, 0));
        assertEquals(1, operator.length(array));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, -1, (short) 9));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, 100));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, 100, (short) 9));
    }

    @Test
    public void testCharArrayOperator() {
        ArrayOperator operator = ArrayOperator.of(char[].class);
        char[] array = new char[]{6};
        assertEquals((char) 6, operator.get(array, 0));
        operator.set(array, 0, (char) 9);
        assertEquals((char) 9, operator.get(array, 0));
        assertEquals(1, operator.length(array));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, -1, (char) 9));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, 100));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, 100, (char) 9));
    }

    @Test
    public void testIntArrayOperator() {
        ArrayOperator operator = ArrayOperator.of(int[].class);
        int[] array = new int[]{6};
        assertEquals((int) 6, operator.get(array, 0));
        operator.set(array, 0, (int) 9);
        assertEquals((int) 9, operator.get(array, 0));
        assertEquals(1, operator.length(array));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, -1, (int) 9));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, 100));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, 100, (int) 9));
    }

    @Test
    public void testLongArrayOperator() {
        ArrayOperator operator = ArrayOperator.of(long[].class);
        long[] array = new long[]{6};
        assertEquals((long) 6, operator.get(array, 0));
        operator.set(array, 0, (long) 9);
        assertEquals((long) 9, operator.get(array, 0));
        assertEquals(1, operator.length(array));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, -1, (long) 9));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, 100));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, 100, (long) 9));
    }

    @Test
    public void testFloatArrayOperator() {
        ArrayOperator operator = ArrayOperator.of(float[].class);
        float[] array = new float[]{6};
        assertEquals((float) 6, operator.get(array, 0));
        operator.set(array, 0, (float) 9);
        assertEquals((float) 9, operator.get(array, 0));
        assertEquals(1, operator.length(array));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, -1, (float) 9));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, 100));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, 100, (float) 9));
    }

    @Test
    public void testDoubleArrayOperator() {
        ArrayOperator operator = ArrayOperator.of(double[].class);
        double[] array = new double[]{6};
        assertEquals((double) 6, operator.get(array, 0));
        operator.set(array, 0, (double) 9);
        assertEquals((double) 9, operator.get(array, 0));
        assertEquals(1, operator.length(array));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, -1, (double) 9));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, 100));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, 100, (double) 9));
    }

    @Test
    public void testObjectArrayOperator() {
        ArrayOperator operator = ArrayOperator.of(Object[].class);
        Object[] array = new Object[]{6};
        assertEquals((Object) 6, operator.get(array, 0));
        operator.set(array, 0, (Object) 9);
        assertEquals((Object) 9, operator.get(array, 0));
        assertEquals(1, operator.length(array));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, -1, (Object) 9));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.get(array, 100));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> operator.set(array, 100, (Object) 9));
    }

    @Test
    public void testInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> ArrayOperator.of(String.class));
    }
}