package tests.base.random;

import org.testng.annotations.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.math.MathKit;
import space.sunqian.common.base.random.Rng;
import space.sunqian.common.collect.StreamKit;
import internal.test.AssertTest;
import internal.test.PrintTest;

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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class RngTest implements AssertTest, PrintTest {

    @Test
    public void testRng() {
        testRng(Rng.newRng());
        testRng(Rng.newRng(0x1234567812345678L));
        testRng(Rng.newRng(new Random()));
        testRng(Rng.threadLocal());
        testRng(Rng.secure("SHA1PRNG"));

        // long Random
        // Random random = new Random(12345678987654321L);
        // testRng(Rng.newRng(random), 100000, Integer.MIN_VALUE, Integer.MIN_VALUE);

        // exceptions
        expectThrows(UnsupportedOperationException.class, () -> Rng.secure(""));
    }

    public void testRng(Rng rng) {
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
        {
            // boolean
            printFor("nextBoolean", rng.nextBoolean());
        }
        {
            // float
            float start = from;
            float end = to;
            printFor("nextFloat", rng.nextFloat());
            if (end > start) {
                expectThrows(IllegalArgumentException.class, () -> rng.nextFloat(end, start));
            }
            float[] array = new float[size];
            for (int i = 0; i < size; i++) {
                array[i] = rng.nextFloat(start, end);
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextFloats", Arrays.toString(array));
        }
        {
            // int
            int start = from;
            int end = to;
            printFor("nextInt", rng.nextInt());
            printFor("getAsInt", rng.getAsInt());
            if (end > start) {
                expectThrows(IllegalArgumentException.class, () -> rng.nextInt(end, start));
            }
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = rng.nextInt(start, end);
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextInts", Arrays.toString(array));
            IntStream stream = rng.ints();
            IntSupplier supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsInt();
            }
            printFor("nextInts", Arrays.toString(array));
            assertEquals(rng.ints(size).count(), size);
            stream = rng.ints(size);
            supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsInt();
            }
            printFor("nextInts", Arrays.toString(array));
            stream = rng.ints(start, end);
            supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsInt();
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextInts", Arrays.toString(array));
            assertEquals(rng.ints(size, start, end).count(), size);
            stream = rng.ints(size, start, end);
            supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsInt();
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextInts", Arrays.toString(array));
            supplier = rng.intSupplier();
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsInt();
            }
            printFor("nextInts", Arrays.toString(array));
            supplier = rng.intSupplier(start, end);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsInt();
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextInts", Arrays.toString(array));
        }
        {
            // long
            long start = from;
            long end = to;
            printFor("nextLong", rng.nextLong());
            printFor("getAsLong", rng.getAsLong());
            if (end > start) {
                expectThrows(IllegalArgumentException.class, () -> rng.nextLong(end, start));
            }
            long[] array = new long[size];
            for (int i = 0; i < size; i++) {
                array[i] = rng.nextLong(start, end);
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextLongs", Arrays.toString(array));
            LongStream stream = rng.longs();
            LongSupplier supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsLong();
            }
            printFor("nextLongs", Arrays.toString(array));
            assertEquals(rng.longs(size).count(), size);
            stream = rng.longs(size);
            supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsLong();
            }
            printFor("nextLongs", Arrays.toString(array));
            stream = rng.longs(start, end);
            supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsLong();
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextLongs", Arrays.toString(array));
            assertEquals(rng.longs(size, start, end).count(), size);
            stream = rng.longs(size, start, end);
            supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsLong();
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextLongs", Arrays.toString(array));
            supplier = rng.longSupplier();
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsLong();
            }
            printFor("nextLongs", Arrays.toString(array));
            supplier = rng.longSupplier(start, end);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsLong();
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextLongs", Arrays.toString(array));
        }
        {
            // double
            double start = from;
            double end = to;
            printFor("nextDouble", rng.nextDouble());
            printFor("getAsDouble", rng.getAsDouble());
            if (end > start) {
                expectThrows(IllegalArgumentException.class, () -> rng.nextDouble(end, start));
            }
            double[] array = new double[size];
            for (int i = 0; i < size; i++) {
                array[i] = rng.nextDouble(start, end);
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextDoubles", Arrays.toString(array));
            DoubleStream stream = rng.doubles();
            DoubleSupplier supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsDouble();
                assertTrue(array[i] >= 0.0 && array[i] < 1.0);
            }
            printFor("nextDoubles", Arrays.toString(array));
            assertEquals(rng.doubles(size).count(), size);
            stream = rng.doubles(size);
            supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsDouble();
                assertTrue(array[i] >= 0.0 && array[i] < 1.0);
            }
            printFor("nextDoubles", Arrays.toString(array));
            stream = rng.doubles(start, end);
            supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsDouble();
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextDoubles", Arrays.toString(array));
            assertEquals(rng.doubles(size, start, end).count(), size);
            stream = rng.doubles(size, start, end);
            supplier = StreamKit.toSupplier(stream);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsDouble();
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextDoubles", Arrays.toString(array));
            supplier = rng.doubleSupplier();
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsDouble();
                assertTrue(array[i] >= 0.0 && array[i] < 1.0);
            }
            printFor("nextDoubles", Arrays.toString(array));
            supplier = rng.doubleSupplier(start, end);
            for (int i = 0; i < size; i++) {
                array[i] = supplier.getAsDouble();
                assertTrue(start == end ? (array[i] == start) : (array[i] >= start && array[i] < end));
            }
            printFor("nextDoubles", Arrays.toString(array));
        }
        {
            // bytes
            byte[] array = rng.nextBytes(size);
            printFor("nextBytes", Arrays.toString(array));
            byte[] array2 = new byte[size + 10];
            rng.nextBytes(array2, 5, size);
            printFor("nextBytes", Arrays.toString(array2));
            assertEquals(Arrays.copyOfRange(array2, 0, 5), new byte[]{0, 0, 0, 0, 0,});
            assertEquals(Arrays.copyOfRange(array2, array2.length - 5, array2.length), new byte[]{0, 0, 0, 0, 0,});
            expectThrows(IndexOutOfBoundsException.class, () -> rng.nextBytes(array, -1, size));
            expectThrows(IndexOutOfBoundsException.class, () -> rng.nextBytes(array, 0, size + 1));
            expectThrows(IndexOutOfBoundsException.class, () -> rng.nextBytes(array, 0, -1));
            ByteBuffer buffer = ByteBuffer.allocate(0);
            rng.nextBytes(buffer);
            buffer = ByteBuffer.allocateDirect(0);
            rng.nextBytes(buffer);
            buffer = ByteBuffer.allocate(size / 2);
            rng.nextBytes(buffer);
            buffer = ByteBuffer.allocateDirect(size / 2);
            rng.nextBytes(buffer);
        }
    }

    @Test
    public void testNewRng() {
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
