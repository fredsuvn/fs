package test.base.random;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.random.Rng;
import xyz.sunqian.common.collect.StreamKit;
import xyz.sunqian.test.AssertTest;
import xyz.sunqian.test.PrintTest;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
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
        testRng(Rng.newRng(new Random()));
        testRng(Rng.threadLocal());
        testRng(Rng.secure("SHA1PRNG"));

        // long Random
        //Random random = new Random(12345678987654321L);
        //testRng(Rng.newRng(random), 100000, Integer.MIN_VALUE, Integer.MIN_VALUE);

        // exceptions
        expectThrows(UnsupportedOperationException.class, () -> Rng.secure(""));
    }

    public void testRng(Rng rng) {
        testRng(rng, 8, -8, 8);
        rng.reset();
        testRng(rng, 16, -16, 16);
        rng.reset(10086);
        testRng(rng, 32, -32, 32);
        rng.reset(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08});
        testRng(rng, 64, -64, 64);
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

    private void show(String name, Object values) {
        System.out.println(name + ": " + values);
    }

    // @Test
    // public void testObjectSupplier() throws Exception {
    //     testObjectSupplier(10);
    //     testObjectSupplier(100);
    //     testObjectSupplier(1000);
    //     testObjectSupplier(10000);
    //
    //     {
    //         // always
    //         Supplier<CharSequence> supplier = RandomKit.supplier(
    //             () -> 5,
    //             Rog.probability(10, "a"),
    //             Rog.probability(10, "b")
    //         );
    //         for (int i = 0; i < 100; i++) {
    //             assertEquals(supplier.get(), "a");
    //         }
    //     }
    //
    //     {
    //         // exception
    //         Supplier<CharSequence> supplier = RandomKit.supplier(
    //             Rog.probability(10, "a")
    //         );
    //         Method getNode = supplier.getClass().getDeclaredMethod("getNode", long.class);
    //         invokeThrows(UnreachablePointException.class, getNode, supplier, -1L);
    //     }
    // }
    //
    // private void testObjectSupplier(int size) {
    //     List<CharSequence> list = new ArrayList<>(size);
    //     Supplier<CharSequence> supplier = RandomKit.supplier(
    //         Rog.probability(10, "a"),
    //         Rog.probability(30, () -> "b"),
    //         Rog.probability(60, "c")
    //     );
    //     for (int i = 0; i < size; i++) {
    //         list.add(supplier.get());
    //     }
    //     int ac = 0;
    //     int bc = 0;
    //     int cc = 0;
    //     int x = 0;
    //     for (CharSequence c : list) {
    //         if (c.equals("a")) {
    //             ac++;
    //         } else if (c.equals("b")) {
    //             bc++;
    //         } else if (c.equals("c")) {
    //             cc++;
    //         } else {
    //             x++;
    //         }
    //     }
    //     assertEquals(x, 0);
    //     showProbability(size, ac, bc, cc);
    //
    //     // exception:
    //     assertThrows(IllegalArgumentException.class, () -> Rog.probability(-1, "a"));
    // }
    //
    // private void showProbability(int size, int ac, int bc, int cc) {
    //     double total = ac + bc + cc;
    //     System.out.println(
    //         "size: " + size +
    //             "[a: " + toPercent(ac / total) +
    //             ", b: " + toPercent(bc / total) +
    //             ", c: " + toPercent(cc / total) + "]"
    //     );
    // }
    //
    // private String toPercent(double d) {
    //     return d * 100 + "%";
    // }

    // @Test
    // public void testNewRng() {
    //     class MyRng implements Rng {
    //
    //         @Override
    //         public int nextInt() {
    //             return 66;
    //         }
    //
    //         @Override
    //         public int nextInt(int startInclusive, int endExclusive) throws IllegalArgumentException {
    //             return 66;
    //         }
    //
    //         @Override
    //         public long nextLong() {
    //             return 66;
    //         }
    //
    //         @Override
    //         public long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException {
    //             return 66;
    //         }
    //
    //         @Override
    //         public double nextDouble() {
    //             return 66;
    //         }
    //
    //         @Override
    //         public double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException {
    //             return 66;
    //         }
    //
    //         @Override
    //         public @Nonnull IntSupplier intSupplier(int startInclusive, int endExclusive) throws IllegalArgumentException {
    //             return () -> 66;
    //         }
    //
    //         @Override
    //         public @Nonnull LongSupplier longSupplier(long startInclusive, long endExclusive) throws IllegalArgumentException {
    //             return () -> 66;
    //         }
    //
    //         @Override
    //         public @Nonnull DoubleSupplier doubleSupplier(double startInclusive, double endExclusive) throws IllegalArgumentException {
    //             return () -> 66;
    //         }
    //     }
    //     MyRng myRng = new MyRng();
    //     {
    //         // int
    //         int[] array = myRng.ints().limit(10).toArray();
    //         for (int anInt : array) {
    //             assertEquals(anInt, 66);
    //         }
    //         array = myRng.ints(1, 100).limit(10).toArray();
    //         for (int anInt : array) {
    //             assertEquals(anInt, 66);
    //         }
    //     }
    //     {
    //         // long
    //         long[] array = myRng.longs().limit(10).toArray();
    //         for (long anInt : array) {
    //             assertEquals(anInt, 66);
    //         }
    //         array = myRng.longs(1, 100).limit(10).toArray();
    //         for (long anInt : array) {
    //             assertEquals(anInt, 66);
    //         }
    //     }
    //     {
    //         // double
    //         double[] array = myRng.doubles().limit(10).toArray();
    //         for (double anInt : array) {
    //             assertEquals(anInt, 66);
    //         }
    //         array = myRng.doubles(1, 100).limit(10).toArray();
    //         for (double anInt : array) {
    //             assertEquals(anInt, 66);
    //         }
    //     }
    //     {
    //         // next bytes
    //         byte[] array = new byte[10];
    //         myRng.nextBytes(array, 3, 5);
    //         for (int i = 3; i < 8; i++) {
    //             assertEquals(array[i], 66);
    //         }
    //         ByteBuffer buf1 = ByteBuffer.allocate(10);
    //         myRng.nextBytes(buf1);
    //         buf1.flip();
    //         while (buf1.hasRemaining()) {
    //             assertEquals(buf1.get(), 66);
    //         }
    //         ByteBuffer buf2 = ByteBuffer.allocateDirect(10);
    //         myRng.nextBytes(buf2);
    //         buf2.flip();
    //         while (buf2.hasRemaining()) {
    //             assertEquals(buf2.get(), 66);
    //         }
    //     }
    // }
}
