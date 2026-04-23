package tests.core.base.random;

import internal.annotations.J17Also;
import internal.utils.Asserter;
import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.math.MathKit;
import space.sunqian.fs.base.random.Rng;
import space.sunqian.fs.collect.StreamKit;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@J17Also
public class RngTest implements Asserter, TestPrint {

    @Test
    public void testRngCreation() {
        // Test different Rng creation methods
        testRng(Rng.newRng());
        testRng(Rng.newRng(0x1234567812345678L));
        testRng(Rng.newRng(new Random()));
        testRng(Rng.threadLocal());
        testRng(Rng.secure("SHA1PRNG"));

        // Test exception case
        assertThrows(UnsupportedOperationException.class, () -> Rng.secure(""));
    }

    public void testRng(Rng rng) {
        // Test with different parameters
        testRng(rng, 8, -8, 8);
        rng.reset();
        testRng(rng, 17, -16, 16);
        rng.reset(10086);
        testRng(rng, 32, Integer.MIN_VALUE, Integer.MAX_VALUE);
        rng.reset(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08});
        testRng(rng, 57, -64, 64);
        testRng(rng, 63, -64, 64);
        testRng(rng, 64, 1, 1);
    }

    private void testRng(Rng rng, int size, int from, int to) {
        // Test boolean generation
        testBooleanGeneration(rng);

        // Test float generation
        testFloatGeneration(rng, size, from, to);

        // Test int generation
        testIntGeneration(rng, size, from, to);

        // Test long generation
        testLongGeneration(rng, size, from, to);

        // Test double generation
        testDoubleGeneration(rng, size, from, to);

        // Test bytes generation
        testBytesGeneration(rng, size);
    }

    private void testBooleanGeneration(Rng rng) {
        printFor("nextBoolean", rng.nextBoolean());
    }

    private void testFloatGeneration(Rng rng, int size, int from, int to) {
        float start = from;
        float end = to;
        printFor("nextFloat", rng.nextFloat());

        // Test exception case
        if (end > start) {
            assertThrows(IllegalArgumentException.class, () -> rng.nextFloat(end, start));
        }

        // Test float array generation
        float[] array = new float[size];
        for (int i = 0; i < size; i++) {
            array[i] = rng.nextFloat(start, end);
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextFloats", Arrays.toString(array));
    }

    private void testIntGeneration(Rng rng, int size, int from, int to) {
        int start = from;
        int end = to;
        printFor("nextInt", rng.nextInt());
        printFor("getAsInt", rng.getAsInt());

        // Test exception case
        if (end > start) {
            assertThrows(IllegalArgumentException.class, () -> rng.nextInt(end, start));
        }

        // Test int array generation
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = rng.nextInt(start, end);
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextInts", Arrays.toString(array));

        // Test int stream generation
        testIntStreamGeneration(rng, size, start, end, array);

        // Test int supplier generation
        testIntSupplierGeneration(rng, size, start, end, array);
    }

    private void testIntStreamGeneration(Rng rng, int size, int start, int end, int[] array) {
        // Test int stream without bounds
        IntStream stream = rng.ints();
        IntSupplier supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsInt();
        }
        printFor("nextInts (stream)", Arrays.toString(array));

        // Test int stream with size
        assertEquals(rng.ints(size).count(), size);
        stream = rng.ints(size);
        supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsInt();
        }
        printFor("nextInts (stream with size)", Arrays.toString(array));

        // Test int stream with bounds
        stream = rng.ints(start, end);
        supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsInt();
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextInts (stream with bounds)", Arrays.toString(array));

        // Test int stream with size and bounds
        assertEquals(rng.ints(size, start, end).count(), size);
        stream = rng.ints(size, start, end);
        supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsInt();
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextInts (stream with size and bounds)", Arrays.toString(array));
    }

    private void testIntSupplierGeneration(Rng rng, int size, int start, int end, int[] array) {
        // Test int supplier without bounds
        IntSupplier supplier = rng.intSupplier();
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsInt();
        }
        printFor("nextInts (supplier)", Arrays.toString(array));

        // Test int supplier with bounds
        supplier = rng.intSupplier(start, end);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsInt();
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextInts (supplier with bounds)", Arrays.toString(array));
    }

    private void testLongGeneration(Rng rng, int size, int from, int to) {
        long start = from;
        long end = to;
        printFor("nextLong", rng.nextLong());
        printFor("getAsLong", rng.getAsLong());

        // Test exception case
        if (end > start) {
            assertThrows(IllegalArgumentException.class, () -> rng.nextLong(end, start));
        }

        // Test long array generation
        long[] array = new long[size];
        for (int i = 0; i < size; i++) {
            array[i] = rng.nextLong(start, end);
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextLongs", Arrays.toString(array));

        // Test long stream generation
        testLongStreamGeneration(rng, size, start, end, array);

        // Test long supplier generation
        testLongSupplierGeneration(rng, size, start, end, array);
    }

    private void testLongStreamGeneration(Rng rng, int size, long start, long end, long[] array) {
        // Test long stream without bounds
        LongStream stream = rng.longs();
        LongSupplier supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsLong();
        }
        printFor("nextLongs (stream)", Arrays.toString(array));

        // Test long stream with size
        assertEquals(rng.longs(size).count(), size);
        stream = rng.longs(size);
        supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsLong();
        }
        printFor("nextLongs (stream with size)", Arrays.toString(array));

        // Test long stream with bounds
        stream = rng.longs(start, end);
        supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsLong();
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextLongs (stream with bounds)", Arrays.toString(array));

        // Test long stream with size and bounds
        assertEquals(rng.longs(size, start, end).count(), size);
        stream = rng.longs(size, start, end);
        supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsLong();
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextLongs (stream with size and bounds)", Arrays.toString(array));
    }

    private void testLongSupplierGeneration(Rng rng, int size, long start, long end, long[] array) {
        // Test long supplier without bounds
        LongSupplier supplier = rng.longSupplier();
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsLong();
        }
        printFor("nextLongs (supplier)", Arrays.toString(array));

        // Test long supplier with bounds
        supplier = rng.longSupplier(start, end);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsLong();
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextLongs (supplier with bounds)", Arrays.toString(array));
    }

    private void testDoubleGeneration(Rng rng, int size, int from, int to) {
        double start = from;
        double end = to;
        printFor("nextDouble", rng.nextDouble());
        printFor("getAsDouble", rng.getAsDouble());

        // Test exception case
        if (end > start) {
            assertThrows(IllegalArgumentException.class, () -> rng.nextDouble(end, start));
        }

        // Test double array generation
        double[] array = new double[size];
        for (int i = 0; i < size; i++) {
            array[i] = rng.nextDouble(start, end);
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextDoubles", Arrays.toString(array));

        // Test double stream generation
        testDoubleStreamGeneration(rng, size, start, end, array);

        // Test double supplier generation
        testDoubleSupplierGeneration(rng, size, start, end, array);
    }

    private void testDoubleStreamGeneration(Rng rng, int size, double start, double end, double[] array) {
        // Test double stream without bounds
        DoubleStream stream = rng.doubles();
        DoubleSupplier supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsDouble();
            assertTrue(array[i] >= 0.0 && array[i] < 1.0);
        }
        printFor("nextDoubles (stream)", Arrays.toString(array));

        // Test double stream with size
        assertEquals(rng.doubles(size).count(), size);
        stream = rng.doubles(size);
        supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsDouble();
            assertTrue(array[i] >= 0.0 && array[i] < 1.0);
        }
        printFor("nextDoubles (stream with size)", Arrays.toString(array));

        // Test double stream with bounds
        stream = rng.doubles(start, end);
        supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsDouble();
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextDoubles (stream with bounds)", Arrays.toString(array));

        // Test double stream with size and bounds
        assertEquals(rng.doubles(size, start, end).count(), size);
        stream = rng.doubles(size, start, end);
        supplier = StreamKit.toSupplier(stream);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsDouble();
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextDoubles (stream with size and bounds)", Arrays.toString(array));
    }

    private void testDoubleSupplierGeneration(Rng rng, int size, double start, double end, double[] array) {
        // Test double supplier without bounds
        DoubleSupplier supplier = rng.doubleSupplier();
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsDouble();
            assertTrue(array[i] >= 0.0 && array[i] < 1.0);
        }
        printFor("nextDoubles (supplier)", Arrays.toString(array));

        // Test double supplier with bounds
        supplier = rng.doubleSupplier(start, end);
        for (int i = 0; i < size; i++) {
            array[i] = supplier.getAsDouble();
            assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
        }
        printFor("nextDoubles (supplier with bounds)", Arrays.toString(array));
    }

    private void testBytesGeneration(Rng rng, int size) {
        // Test bytes array generation
        byte[] array = rng.nextBytes(size);
        printFor("nextBytes", Arrays.toString(array));

        // Test bytes array with offset and length
        byte[] array2 = new byte[size + 10];
        rng.nextBytes(array2, 5, size);
        printFor("nextBytes (with offset)", Arrays.toString(array2));
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0,}, Arrays.copyOfRange(array2, 0, 5));
        assertArrayEquals(new byte[]{0, 0, 0, 0, 0,}, Arrays.copyOfRange(array2, array2.length - 5, array2.length));

        // Test exception cases
        assertThrows(IndexOutOfBoundsException.class, () -> rng.nextBytes(array, -1, size));
        assertThrows(IndexOutOfBoundsException.class, () -> rng.nextBytes(array, 0, size + 1));
        assertThrows(IndexOutOfBoundsException.class, () -> rng.nextBytes(array, 0, -1));

        // Test ByteBuffer generation
        ByteBuffer buffer = ByteBuffer.allocate(0);
        rng.nextBytes(buffer);
        buffer = ByteBuffer.allocateDirect(0);
        rng.nextBytes(buffer);
        buffer = ByteBuffer.allocate(size / 2);
        rng.nextBytes(buffer);
        buffer = ByteBuffer.allocateDirect(size / 2);
        rng.nextBytes(buffer);
    }

    @Test
    public void testCustomRngImplementation() {
        class MyRng implements Rng {

            @Override
            public void reset(long seed) {
            }

            @Override
            public void reset(byte @Nonnull [] seed) {
            }

            @Override
            public int nextInt() {
                return ThreadLocalRandom.current().nextInt();
            }

            @Override
            public int nextInt(int startInclusive, int endExclusive) throws IllegalArgumentException {
                return intSupplier(startInclusive, endExclusive).getAsInt();
            }

            @Override
            public long nextLong() {
                return ThreadLocalRandom.current().nextLong();
            }

            @Override
            public long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException {
                return longSupplier(startInclusive, endExclusive).getAsLong();
            }

            @Override
            public float nextFloat() {
                double value = nextDouble();
                return MathKit.makeIn((float) value, 0.0f, 1.0f);
            }

            @Override
            public float nextFloat(float startInclusive, float endExclusive) throws IllegalArgumentException {
                double value = nextDouble(startInclusive, endExclusive);
                return MathKit.makeIn((float) value, startInclusive, endExclusive);
            }

            @Override
            public double nextDouble() {
                return ThreadLocalRandom.current().nextDouble();
            }

            @Override
            public double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException {
                return doubleSupplier(startInclusive, endExclusive).getAsDouble();
            }

            @Override
            public @Nonnull IntSupplier intSupplier(int startInclusive, int endExclusive) throws IllegalArgumentException {
                if (startInclusive == endExclusive) {
                    return () -> startInclusive;
                }
                return StreamKit.toSupplier(ThreadLocalRandom.current().ints(startInclusive, endExclusive));
            }

            @Override
            public LongSupplier longSupplier(long startInclusive, long endExclusive) throws IllegalArgumentException {
                if (startInclusive == endExclusive) {
                    return () -> startInclusive;
                }
                return StreamKit.toSupplier(ThreadLocalRandom.current().longs(startInclusive, endExclusive));
            }

            @Override
            public @Nonnull DoubleSupplier doubleSupplier(double startInclusive, double endExclusive) throws IllegalArgumentException {
                if (startInclusive == endExclusive) {
                    return () -> startInclusive;
                }
                return StreamKit.toSupplier(ThreadLocalRandom.current().doubles(startInclusive, endExclusive));
            }
        }
        testRng(new MyRng());
    }
}
