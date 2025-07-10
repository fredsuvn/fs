package test.base.random;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.base.random.RandomKit;
import xyz.sunqian.common.base.random.Rng;
import xyz.sunqian.common.base.random.Rog;
import xyz.sunqian.test.JieAssert;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

public class RandomTest {

    @Test
    public void testNext() {
        testNext(8, 0, 100);
        testNext(16, 11, 122);
        testNext(24, -25, 111);
        testNext(32, -88, -8);

        {
            // next bytes
            byte[] array = new byte[10];
            RandomKit.nextBytes(array, 1, 8);
            assertEquals(array[0], 0);
            assertEquals(array[9], 0);
            show("nextBytes", Arrays.toString(array));
            ByteBuffer buf1 = ByteBuffer.allocate(10);
            RandomKit.nextBytes(buf1);
            buf1.flip();
            show("nextBytes", buf1.toString());
            ByteBuffer buf2 = ByteBuffer.allocateDirect(10);
            RandomKit.nextBytes(buf2);
            buf2.flip();
            show("nextBytes", buf2.toString());
        }
    }

    private void testNext(int size, int fromInt, int toInt) {
        {
            // boolean
            boolean next = RandomKit.nextBoolean();
            show("boolean.next", next);
            boolean[] array = RandomKit.fill(new boolean[size]);
            show("booleans", Arrays.toString(array));
        }
        {
            // byte
            byte from = (byte) fromInt;
            byte to = (byte) toInt;
            byte next = RandomKit.nextByte();
            show("byte.next", next);
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextByte(from, to);
                assertTrue(next >= from && next < to);
            }
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextByte(from, from);
                assertEquals(from, next);
            }
            byte[] array = RandomKit.fill(new byte[size]);
            show("bytes.fill", Arrays.toString(array));
            array = RandomKit.fill(new byte[size], from, to);
            for (byte e : array) {
                assertTrue(e >= from && e < to);
            }
            show("bytes.fill range", Arrays.toString(array));
            array = RandomKit.fill(new byte[size], from, from);
            for (byte e : array) {
                assertEquals(e, from);
            }
            show("bytes.fill fix", Arrays.toString(array));

        }
        {
            // short
            short from = (short) fromInt;
            short to = (short) toInt;
            short next = RandomKit.nextShort();
            show("short.next", next);
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextShort(from, to);
                assertTrue(next >= from && next < to);
            }
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextShort(from, from);
                assertEquals(from, next);
            }
            short[] array = RandomKit.fill(new short[size]);
            show("shorts.fill", Arrays.toString(array));
            array = RandomKit.fill(new short[size], from, to);
            for (short e : array) {
                assertTrue(e >= from && e < to);
            }
            show("shorts.fill range", Arrays.toString(array));
            array = RandomKit.fill(new short[size], from, from);
            for (short e : array) {
                assertEquals(e, from);
            }
            show("shorts.fill fix", Arrays.toString(array));
        }
        {
            // char
            char from = (char) Math.min(Math.abs(fromInt), Math.abs(toInt));
            char to = (char) Math.max(Math.abs(fromInt), Math.abs(toInt));
            char next = RandomKit.nextChar();
            show("char.next", next);
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextChar(from, to);
                assertTrue(next >= from && next < to);
            }
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextChar(from, from);
                assertEquals(from, next);
            }
            char[] array = RandomKit.fill(new char[size]);
            show("chars.fill", Arrays.toString(array));
            array = RandomKit.fill(new char[size], from, to);
            for (char e : array) {
                assertTrue(e >= from && e < to);
            }
            show("chars.fill range", Arrays.toString(array));
            array = RandomKit.fill(new char[size], from, from);
            for (char e : array) {
                assertEquals(e, from);
            }
            show("chars.fill fix", Arrays.toString(array));
        }
        {
            // int
            int from = fromInt;
            int to = toInt;
            int next = RandomKit.nextInt();
            show("int.next", next);
            next = Rng.getDefault().getAsInt();
            show("int.getAsInt", next);
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextInt(from, to);
                assertTrue(next >= from && next < to);
            }
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextInt(from, from);
                assertEquals(from, next);
            }
            int[] array = RandomKit.fill(new int[size]);
            show("ints.fill", Arrays.toString(array));
            array = RandomKit.fill(new int[size], from, to);
            for (int e : array) {
                assertTrue(e >= from && e < to);
            }
            show("ints.fill range", Arrays.toString(array));
            array = RandomKit.fill(new int[size], from, from);
            for (int e : array) {
                assertEquals(e, from);
            }
            show("ints.fill fix", Arrays.toString(array));
        }
        {
            // long
            long from = fromInt;
            long to = toInt;
            long next = RandomKit.nextLong();
            show("long.next", next);
            next = Rng.getDefault().getAsLong();
            show("int.getAsLong", next);
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextLong(from, to);
                assertTrue(next >= from && next < to);
            }
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextLong(from, from);
                assertEquals(from, next);
            }
            long[] array = RandomKit.fill(new long[size]);
            show("longs.fill", Arrays.toString(array));
            array = RandomKit.fill(new long[size], from, to);
            for (long e : array) {
                assertTrue(e >= from && e < to);
            }
            show("longs.fill range", Arrays.toString(array));
            array = RandomKit.fill(new long[size], from, from);
            for (long e : array) {
                assertEquals(e, from);
            }
            show("longs.fill fix", Arrays.toString(array));
        }
        {
            // float
            float from = fromInt;
            float to = toInt;
            float next = RandomKit.nextFloat();
            show("float.next", next);
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextFloat();
                assertTrue(next >= 0 && next < 1);
            }
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextFloat(from, to);
                assertTrue(next >= from && next < to);
            }
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextFloat(from, from);
                assertEquals(from, next);
            }
            float[] array = RandomKit.fill(new float[size]);
            show("floats.fill", Arrays.toString(array));
            array = RandomKit.fill(new float[size], from, to);
            for (float e : array) {
                assertTrue(e >= from && e < to);
            }
            show("floats.fill range", Arrays.toString(array));
            array = RandomKit.fill(new float[size], from, from);
            for (float e : array) {
                assertEquals(e, from);
            }
            show("floats.fill fix", Arrays.toString(array));
        }
        {
            // double
            double from = fromInt;
            double to = toInt;
            double next = RandomKit.nextDouble();
            assertTrue(next >= 0 && next < 1);
            show("double.next", next);
            next = Rng.getDefault().getAsDouble();
            assertTrue(next >= 0 && next < 1);
            show("int.getAsDouble", next);
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextFloat();
                assertTrue(next >= 0 && next < 1);
            }
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextDouble(from, to);
                assertTrue(next >= from && next < to);
            }
            for (int i = 0; i < size; i++) {
                next = RandomKit.nextDouble(from, from);
                assertEquals(from, next);
            }
            double[] array = RandomKit.fill(new double[size]);
            show("doubles.fill", Arrays.toString(array));
            array = RandomKit.fill(new double[size], from, to);
            for (double e : array) {
                assertTrue(e >= from && e < to);
            }
            show("doubles.fill range", Arrays.toString(array));
            array = RandomKit.fill(new double[size], from, from);
            for (double e : array) {
                assertEquals(e, from);
            }
            show("doubles.fill fix", Arrays.toString(array));
        }
    }

    @Test
    public void testPrimitiveSupplier() {
        testPrimitiveSupplier(8, 0, 100);
        testPrimitiveSupplier(16, 11, 122);
        testPrimitiveSupplier(24, -25, 111);
        testPrimitiveSupplier(32, -88, -8);
    }

    private void testPrimitiveSupplier(int size, int fromInt, int toInt) {
        List<Object> list = new ArrayList<>();
        {
            // int
            IntStream stream = RandomKit.ints();
            PrimitiveIterator.OfInt iterator = stream.iterator();
            for (int i = 0; i < size; i++) {
                list.add(iterator.nextInt());
            }
            show("ints", list);
            list.clear();
            stream = RandomKit.ints(fromInt, toInt);
            iterator = stream.iterator();
            for (int i = 0; i < size; i++) {
                int next = iterator.nextInt();
                assertTrue(next >= fromInt && next < toInt);
                list.add(next);
            }
            show("ints.range", list);
            list.clear();
            stream = RandomKit.ints(fromInt, fromInt);
            iterator = stream.iterator();
            for (int i = 0; i < size; i++) {
                int next = iterator.nextInt();
                assertEquals(fromInt, next);
                list.add(next);
            }
            show("ints.fix", list);
            list.clear();
            IntSupplier supplier = RandomKit.intSupplier();
            for (int i = 0; i < size; i++) {
                list.add(supplier.getAsInt());
            }
            show("ints.supplier", list);
            list.clear();
            supplier = RandomKit.intSupplier(fromInt, toInt);
            for (int i = 0; i < size; i++) {
                int next = supplier.getAsInt();
                assertTrue(next >= fromInt && next < toInt);
                list.add(next);
            }
            show("ints.supplier range", list);
            list.clear();
            supplier = RandomKit.intSupplier(fromInt, fromInt);
            for (int i = 0; i < size; i++) {
                int next = supplier.getAsInt();
                assertEquals(fromInt, next);
                list.add(next);
            }
            show("ints.supplier fix", list);
        }
        {
            // long
            LongStream stream = RandomKit.longs();
            PrimitiveIterator.OfLong iterator = stream.iterator();
            for (long i = 0; i < size; i++) {
                list.add(iterator.nextLong());
            }
            show("longs", list);
            list.clear();
            stream = RandomKit.longs(fromInt, toInt);
            iterator = stream.iterator();
            for (long i = 0; i < size; i++) {
                long next = iterator.nextLong();
                assertTrue(next >= fromInt && next < toInt);
                list.add(next);
            }
            show("longs.range", list);
            list.clear();
            stream = RandomKit.longs(fromInt, fromInt);
            iterator = stream.iterator();
            for (long i = 0; i < size; i++) {
                long next = iterator.nextLong();
                assertEquals(fromInt, next);
                list.add(next);
            }
            show("longs.fix", list);
            list.clear();
            LongSupplier supplier = RandomKit.longSupplier();
            for (long i = 0; i < size; i++) {
                list.add(supplier.getAsLong());
            }
            show("longs.supplier", list);
            list.clear();
            supplier = RandomKit.longSupplier(fromInt, toInt);
            for (long i = 0; i < size; i++) {
                long next = supplier.getAsLong();
                assertTrue(next >= fromInt && next < toInt);
                list.add(next);
            }
            show("longs.supplier range", list);
            list.clear();
            supplier = RandomKit.longSupplier(fromInt, fromInt);
            for (long i = 0; i < size; i++) {
                long next = supplier.getAsLong();
                assertEquals(fromInt, next);
                list.add(next);
            }
            show("longs.supplier fix", list);
        }
        {
            // double
            DoubleStream stream = RandomKit.doubles();
            PrimitiveIterator.OfDouble iterator = stream.iterator();
            for (double i = 0; i < size; i++) {
                list.add(iterator.nextDouble());
            }
            show("doubles", list);
            list.clear();
            stream = RandomKit.doubles(fromInt, toInt);
            iterator = stream.iterator();
            for (double i = 0; i < size; i++) {
                double next = iterator.nextDouble();
                assertTrue(next >= fromInt && next < toInt);
                list.add(next);
            }
            show("doubles.range", list);
            list.clear();
            stream = RandomKit.doubles(fromInt, fromInt);
            iterator = stream.iterator();
            for (double i = 0; i < size; i++) {
                double next = iterator.nextDouble();
                assertEquals(fromInt, next);
                list.add(next);
            }
            show("doubles.fix", list);
            list.clear();
            DoubleSupplier supplier = RandomKit.doubleSupplier();
            for (double i = 0; i < size; i++) {
                list.add(supplier.getAsDouble());
            }
            show("doubles.supplier", list);
            list.clear();
            supplier = RandomKit.doubleSupplier(fromInt, toInt);
            for (double i = 0; i < size; i++) {
                double next = supplier.getAsDouble();
                assertTrue(next >= fromInt && next < toInt);
                list.add(next);
            }
            show("doubles.supplier range", list);
            list.clear();
            supplier = RandomKit.doubleSupplier(fromInt, fromInt);
            for (double i = 0; i < size; i++) {
                double next = supplier.getAsDouble();
                assertEquals(fromInt, next);
                list.add(next);
            }
            show("doubles.supplier fix", list);
        }
    }

    private void show(String name, Object values) {
        System.out.println(name + ": " + values);
    }

    @Test
    public void testObjectSupplier() throws Exception {
        testObjectSupplier(10);
        testObjectSupplier(100);
        testObjectSupplier(1000);
        testObjectSupplier(10000);

        {
            // always
            Supplier<CharSequence> supplier = RandomKit.supplier(
                () -> 5,
                Rog.probability(10, "a"),
                Rog.probability(10, "b")
            );
            for (int i = 0; i < 100; i++) {
                assertEquals(supplier.get(), "a");
            }
        }

        {
            // exception
            Supplier<CharSequence> supplier = RandomKit.supplier(
                Rog.probability(10, "a")
            );
            Method getNode = supplier.getClass().getDeclaredMethod("getNode", long.class);
            JieAssert.invokeThrows(UnreachablePointException.class, getNode, supplier, -1L);
        }
    }

    private void testObjectSupplier(int size) {
        List<CharSequence> list = new ArrayList<>(size);
        Supplier<CharSequence> supplier = RandomKit.supplier(
            Rog.probability(10, "a"),
            Rog.probability(30, () -> "b"),
            Rog.probability(60, "c")
        );
        for (int i = 0; i < size; i++) {
            list.add(supplier.get());
        }
        int ac = 0;
        int bc = 0;
        int cc = 0;
        int x = 0;
        for (CharSequence c : list) {
            if (c.equals("a")) {
                ac++;
            } else if (c.equals("b")) {
                bc++;
            } else if (c.equals("c")) {
                cc++;
            } else {
                x++;
            }
        }
        assertEquals(x, 0);
        showProbability(size, ac, bc, cc);

        // exception:
        assertThrows(IllegalArgumentException.class, () -> Rog.probability(-1, "a"));
    }

    private void showProbability(int size, int ac, int bc, int cc) {
        double total = ac + bc + cc;
        System.out.println(
            "size: " + size +
                "[a: " + toPercent(ac / total) +
                ", b: " + toPercent(bc / total) +
                ", c: " + toPercent(cc / total) + "]"
        );
    }

    private String toPercent(double d) {
        return d * 100 + "%";
    }

    @Test
    public void testNewRng() {
        class MyRng implements Rng {

            @Override
            public int nextInt() {
                return 66;
            }

            @Override
            public int nextInt(int startInclusive, int endExclusive) throws IllegalArgumentException {
                return 66;
            }

            @Override
            public long nextLong() {
                return 66;
            }

            @Override
            public long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException {
                return 66;
            }

            @Override
            public double nextDouble() {
                return 66;
            }

            @Override
            public double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException {
                return 66;
            }

            @Override
            public @Nonnull IntSupplier intSupplier(int startInclusive, int endExclusive) throws IllegalArgumentException {
                return () -> 66;
            }

            @Override
            public @Nonnull LongSupplier longSupplier(long startInclusive, long endExclusive) throws IllegalArgumentException {
                return () -> 66;
            }

            @Override
            public @Nonnull DoubleSupplier doubleSupplier(double startInclusive, double endExclusive) throws IllegalArgumentException {
                return () -> 66;
            }
        }
        MyRng myRng = new MyRng();
        {
            // int
            int[] array = myRng.ints().limit(10).toArray();
            for (int anInt : array) {
                assertEquals(anInt, 66);
            }
            array = myRng.ints(1, 100).limit(10).toArray();
            for (int anInt : array) {
                assertEquals(anInt, 66);
            }
        }
        {
            // long
            long[] array = myRng.longs().limit(10).toArray();
            for (long anInt : array) {
                assertEquals(anInt, 66);
            }
            array = myRng.longs(1, 100).limit(10).toArray();
            for (long anInt : array) {
                assertEquals(anInt, 66);
            }
        }
        {
            // double
            double[] array = myRng.doubles().limit(10).toArray();
            for (double anInt : array) {
                assertEquals(anInt, 66);
            }
            array = myRng.doubles(1, 100).limit(10).toArray();
            for (double anInt : array) {
                assertEquals(anInt, 66);
            }
        }
        {
            // next bytes
            byte[] array = new byte[10];
            myRng.nextBytes(array, 3, 5);
            for (int i = 3; i < 8; i++) {
                assertEquals(array[i], 66);
            }
            ByteBuffer buf1 = ByteBuffer.allocate(10);
            myRng.nextBytes(buf1);
            buf1.flip();
            while (buf1.hasRemaining()) {
                assertEquals(buf1.get(), 66);
            }
            ByteBuffer buf2 = ByteBuffer.allocateDirect(10);
            myRng.nextBytes(buf2);
            buf2.flip();
            while (buf2.hasRemaining()) {
                assertEquals(buf2.get(), 66);
            }
        }
    }
}
