package test.base.random;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.random.RandomKit;

import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RandomTest {

    @Test
    public void testNext() {
        int size = 20;
        int from = -86;
        int to = 110;
        {
            // boolean
            boolean[] array = RandomKit.fill(new boolean[size]);
            show("booleans", Arrays.toString(array));
        }
        {
            // byte
            show("byte.next", String.valueOf(RandomKit.nextByte()));
            byte r = RandomKit.nextByte((byte) from, (byte) to);
            assertTrue(r >= from && r < to);
            show("byte.next range", String.valueOf(r));
            byte[] array = RandomKit.fill(new byte[size]);
            show("bytes.fill", Arrays.toString(array));
            for (int i = 0; i < array.length; i++) {
                array[i] = RandomKit.nextByte((byte) from, (byte) to);
            }
            for (byte e : array) {
                assertTrue(e >= from && e < to);
            }
            show("bytes.next range", Arrays.toString(array));
            array = RandomKit.fill(new byte[size], (byte) from, (byte) to);
            for (byte e : array) {
                assertTrue(e >= from && e < to);
            }
            show("bytes.fill range", Arrays.toString(array));
            array = RandomKit.fill(new byte[size], (byte) from, (byte) from);
            for (byte e : array) {
                assertEquals(e, from);
            }
            show("bytes.fill fix", Arrays.toString(array));
        }
        {
            // short
            show("short.next", String.valueOf(RandomKit.nextShort()));
            short r = RandomKit.nextShort((short) from, (short) to);
            assertTrue(r >= from && r < to);
            show("short.next range", String.valueOf(r));
            short[] array = RandomKit.fill(new short[size]);
            show("shorts.fill", Arrays.toString(array));
            for (int i = 0; i < array.length; i++) {
                array[i] = RandomKit.nextShort((short) from, (short) to);
            }
            for (short e : array) {
                assertTrue(e >= from && e < to);
            }
            show("shorts.next range", Arrays.toString(array));
            array = RandomKit.fill(new short[size], (short) from, (short) to);
            for (short e : array) {
                assertTrue(e >= from && e < to);
            }
            show("shorts.fill range", Arrays.toString(array));
            array = RandomKit.fill(new short[size], (short) from, (short) from);
            for (short e : array) {
                assertEquals(e, from);
            }
            show("shorts.fill fix", Arrays.toString(array));
        }
        {
            // char
            int cFrom = 66;
            int cTo = 111;
            show("char.next", String.valueOf(RandomKit.nextChar()));
            char r = RandomKit.nextChar((char) cFrom, (char) cTo);
            assertTrue(r >= cFrom && r < cTo);
            show("char.next range", String.valueOf(r));
            char[] array = RandomKit.fill(new char[size]);
            show("chars.fill", Arrays.toString(array));
            for (int i = 0; i < array.length; i++) {
                array[i] = RandomKit.nextChar((char) cFrom, (char) cTo);
            }
            for (char e : array) {
                assertTrue(e >= cFrom && e < cTo);
            }
            show("chars.next range", Arrays.toString(array));
            array = RandomKit.fill(new char[size], (char) cFrom, (char) cTo);
            for (char e : array) {
                assertTrue(e >= cFrom && e < cTo);
            }
            show("chars.fill range", Arrays.toString(array));
            array = RandomKit.fill(new char[size], (char) cFrom, (char) cFrom);
            for (char e : array) {
                assertEquals(e, cFrom);
            }
            show("chars.fill fix", Arrays.toString(array));
        }
        {
            // int
            show("int.next", String.valueOf(RandomKit.nextInt()));
            int r = RandomKit.nextInt((int) from, (int) to);
            assertTrue(r >= from && r < to);
            show("int.next range", String.valueOf(r));
            int[] array = RandomKit.fill(new int[size]);
            show("ints.fill", Arrays.toString(array));
            for (int i = 0; i < array.length; i++) {
                array[i] = RandomKit.nextInt((int) from, (int) to);
            }
            for (int e : array) {
                assertTrue(e >= from && e < to);
            }
            show("ints.next range", Arrays.toString(array));
            array = RandomKit.fill(new int[size], (int) from, (int) to);
            for (int e : array) {
                assertTrue(e >= from && e < to);
            }
            show("ints.fill range", Arrays.toString(array));
            array = RandomKit.fill(new int[size], (int) from, (int) from);
            for (int e : array) {
                assertEquals(e, from);
            }
            show("ints.fill fix", Arrays.toString(array));
        }
        {
            // long
            show("long.next", String.valueOf(RandomKit.nextLong()));
            long r = RandomKit.nextLong((long) from, (long) to);
            assertTrue(r >= from && r < to);
            show("long.next range", String.valueOf(r));
            long[] array = RandomKit.fill(new long[size]);
            show("longs.fill", Arrays.toString(array));
            for (int i = 0; i < array.length; i++) {
                array[i] = RandomKit.nextLong((long) from, (long) to);
            }
            for (long e : array) {
                assertTrue(e >= from && e < to);
            }
            show("longs.next range", Arrays.toString(array));
            array = RandomKit.fill(new long[size], (long) from, (long) to);
            for (long e : array) {
                assertTrue(e >= from && e < to);
            }
            show("longs.fill range", Arrays.toString(array));
            array = RandomKit.fill(new long[size], (long) from, (long) from);
            for (long e : array) {
                assertEquals(e, from);
            }
            show("longs.fill fix", Arrays.toString(array));
        }
        {
            // float
            show("float.next", String.valueOf(RandomKit.nextFloat()));
            float r = RandomKit.nextFloat((float) from, (float) to);
            assertTrue(r >= from && r < to);
            show("float.next range", String.valueOf(r));
            float[] array = RandomKit.fill(new float[size]);
            show("floats.fill", Arrays.toString(array));
            for (int i = 0; i < array.length; i++) {
                array[i] = RandomKit.nextFloat((float) from, (float) to);
            }
            for (float e : array) {
                assertTrue(e >= from && e < to);
            }
            show("floats.next range", Arrays.toString(array));
            array = RandomKit.fill(new float[size], (float) from, (float) to);
            for (float e : array) {
                assertTrue(e >= from && e < to);
            }
            show("floats.fill range", Arrays.toString(array));
            array = RandomKit.fill(new float[size], (float) from, (float) from);
            for (float e : array) {
                assertEquals(e, from);
            }
            show("floats.fill fix", Arrays.toString(array));
        }
        {
            // double
            show("double.next", String.valueOf(RandomKit.nextDouble()));
            double r = RandomKit.nextDouble((double) from, (double) to);
            assertTrue(r >= from && r < to);
            show("double.next range", String.valueOf(r));
            double[] array = RandomKit.fill(new double[size]);
            show("doubles.fill", Arrays.toString(array));
            for (int i = 0; i < array.length; i++) {
                array[i] = RandomKit.nextDouble((double) from, (double) to);
            }
            for (double e : array) {
                assertTrue(e >= from && e < to);
            }
            show("doubles.next range", Arrays.toString(array));
            array = RandomKit.fill(new double[size], (double) from, (double) to);
            for (double e : array) {
                assertTrue(e >= from && e < to);
            }
            show("doubles.fill range", Arrays.toString(array));
            array = RandomKit.fill(new double[size], (double) from, (double) from);
            for (double e : array) {
                assertEquals(e, from);
            }
            show("doubles.fill fix", Arrays.toString(array));
        }
    }

    private void show(String name, String values) {
        System.out.println(name + ": " + values);
    }

    // @Test
    // public void testNextInt() {
    //     final int size = 10;
    //     final int startInclusive = 6;
    //     final int endExclusive = 66;
    //
    //     // boolean
    //     Log.log(RandomKit.nextBoolean());
    //     boolean[] bools = RandomKit.fill(new boolean[size]);
    //     Log.log(JieArray.asList(bools));
    //
    //     // byte
    //     Log.log(RandomKit.nextByte());
    //     byte br = RandomKit.nextByte(startInclusive, endExclusive);
    //     assertTrue(br >= startInclusive && br < endExclusive);
    //     br = RandomKit.nextByte((byte) startInclusive, (byte) endExclusive);
    //     assertTrue(br >= startInclusive && br < endExclusive);
    //     byte[] ib = RandomKit.fill(new byte[size]);
    //     Log.log(JieArray.asList(ib));
    //     ib = RandomKit.fill(new byte[size], startInclusive, endExclusive);
    //     Log.log(JieArray.asList(ib));
    //     for (int i : ib) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    //     ib = RandomKit.fill(new byte[size], (byte) startInclusive, (byte) endExclusive);
    //     Log.log(JieArray.asList(ib));
    //     for (int i : ib) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    //
    //     // short
    //     Log.log(RandomKit.nextShort());
    //     short sr = RandomKit.nextShort(startInclusive, endExclusive);
    //     assertTrue(sr >= startInclusive && sr < endExclusive);
    //     sr = RandomKit.nextShort((short) startInclusive, (short) endExclusive);
    //     assertTrue(sr >= startInclusive && sr < endExclusive);
    //     short[] sa = RandomKit.fill(new short[size]);
    //     Log.log(JieArray.asList(sa));
    //     sa = RandomKit.fill(new short[size], startInclusive, endExclusive);
    //     Log.log(JieArray.asList(sa));
    //     for (int i : sa) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    //     sa = RandomKit.fill(new short[size], (short) startInclusive, (short) endExclusive);
    //     Log.log(JieArray.asList(sa));
    //     for (int i : sa) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    //
    //     // char
    //     Log.log(RandomKit.nextChar());
    //     char cr = RandomKit.nextChar(startInclusive, endExclusive);
    //     assertTrue(cr >= startInclusive && cr < endExclusive);
    //     cr = RandomKit.nextChar((char) startInclusive, (char) endExclusive);
    //     assertTrue(cr >= startInclusive && cr < endExclusive);
    //     char[] ca = RandomKit.fill(new char[size]);
    //     Log.log(JieArray.asList(ca));
    //     ca = RandomKit.fill(new char[size], startInclusive, endExclusive);
    //     Log.log(JieArray.asList(ca));
    //     for (int i : ca) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    //     ca = RandomKit.fill(new char[size], (char) startInclusive, (char) endExclusive);
    //     Log.log(JieArray.asList(ca));
    //     for (int i : ca) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    //
    //     // int
    //     Log.log(RandomKit.nextInt());
    //     int ir = RandomKit.nextInt(startInclusive, endExclusive);
    //     assertTrue(ir >= startInclusive && ir < endExclusive);
    //     int[] ia = RandomKit.fill(new int[size]);
    //     Log.log(JieArray.asList(ia));
    //     ia = RandomKit.fill(new int[size], startInclusive, endExclusive);
    //     Log.log(JieArray.asList(ia));
    //     for (int i : ia) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    //
    //     // long
    //     Log.log(RandomKit.nextLong());
    //     long lr = RandomKit.nextLong(startInclusive, endExclusive);
    //     assertTrue(lr >= startInclusive && lr < endExclusive);
    //     long[] la = RandomKit.fill(new long[size]);
    //     Log.log(JieArray.asList(la));
    //     la = RandomKit.fill(new long[size], startInclusive, endExclusive);
    //     Log.log(JieArray.asList(la));
    //     for (long i : la) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    //
    //     // float
    //     Log.log(RandomKit.nextFloat());
    //     float fr = RandomKit.nextFloat(startInclusive, endExclusive);
    //     assertTrue(fr >= startInclusive && fr < endExclusive);
    //     float[] fa = RandomKit.fill(new float[size]);
    //     Log.log(JieArray.asList(fa));
    //     fa = RandomKit.fill(new float[size], startInclusive, endExclusive);
    //     Log.log(JieArray.asList(fa));
    //     for (float i : fa) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    //
    //     // double
    //     Log.log(RandomKit.nextDouble());
    //     double dr = RandomKit.nextDouble(startInclusive, endExclusive);
    //     assertTrue(dr >= startInclusive && dr < endExclusive);
    //     double[] da = RandomKit.fill(new double[size]);
    //     Log.log(JieArray.asList(da));
    //     da = RandomKit.fill(new double[size], startInclusive, endExclusive);
    //     Log.log(JieArray.asList(da));
    //     for (double i : da) {
    //         assertTrue(i >= startInclusive && i < endExclusive);
    //     }
    // }

    // @Test
    // public void testSupplier() throws Exception {
    //     Supplier<Integer> s1 = RandomKit.supplier(
    //         RandomKit.score(20, 1),
    //         RandomKit.score(30, 2),
    //         RandomKit.score(30, 3),
    //         RandomKit.score(30, 4),
    //         RandomKit.score(30, 5),
    //         RandomKit.score(30, 6),
    //         RandomKit.score(30, 7),
    //         RandomKit.score(30, 8),
    //         RandomKit.score(30, 9),
    //         RandomKit.score(50, () -> 10)
    //     );
    //     testSupplier(s1);
    //     Supplier<Integer> s2 = RandomKit.supplier(Jie.list(
    //         RandomKit.score(20, 2),
    //         RandomKit.score(30, 3),
    //         RandomKit.score(50, () -> 5)
    //     ));
    //     testSupplier(s2);
    //     Supplier<Integer> s3 = RandomKit.supplier(new Random(),
    //         RandomKit.score(20, 2)
    //     );
    //     testSupplier(s3);
    //     Supplier<Integer> s4 = RandomKit.supplier(new Random(), Jie.list(
    //         RandomKit.score(20, 2)
    //     ));
    //     testSupplier(s4);
    //     expectThrows(IllegalArgumentException.class, () -> RandomKit.supplier());
    //     expectThrows(IllegalArgumentException.class, () -> RandomKit.supplier(Jie.list()));
    //     expectThrows(IllegalArgumentException.class, () -> RandomKit.supplier(new Random()));
    //     expectThrows(IllegalArgumentException.class, () -> RandomKit.supplier(new Random(), Jie.list()));
    //
    //     // test unreadable
    //     Method binarySearch = s1.getClass().getDeclaredMethod("binarySearch", int.class);
    //     binarySearch.setAccessible(true);
    //     Field field = s1.getClass().getDeclaredField("totalScore");
    //     field.setAccessible(true);
    //     Method supply = s1.getClass().getDeclaredMethod("supply", int.class);
    //     supply.setAccessible(true);
    //     Invocable supplyInvocable = Invocable.of(supply);
    //     int totalScore = (Integer) field.get(s1);
    //     for (int i = 0; i < totalScore * 2; i++) {
    //         Object node = binarySearch.invoke(s1, i);
    //         if (node == null) {
    //             int next = i;
    //             expectThrows(IllegalStateException.class, () -> {
    //                 try {
    //                     supplyInvocable.invoke(s1, next);
    //                 } catch (InvocationException e) {
    //                     throw e.getCause();
    //                 }
    //             });
    //         }
    //     }
    // }
    //
    // private void testSupplier(Supplier<?> supplier) {
    //     final int times = 10000;
    //     List<Object> types = new ArrayList<>();
    //     List<Integer> hits = new ArrayList<>();
    //     GET:
    //     for (int i = 0; i < times; i++) {
    //         Object result = supplier.get();
    //         for (int j = 0; j < types.size(); j++) {
    //             if (Objects.equals(types.get(j), result)) {
    //                 hits.set(j, hits.get(j) + 1);
    //                 continue GET;
    //             }
    //         }
    //         types.add(result);
    //         hits.add(1);
    //     }
    //     int total = hits.stream().mapToInt(it -> it).sum();
    //     assertEquals(total, times);
    //     StringBuilder sb = new StringBuilder();
    //     for (int i = 0; i < types.size(); i++) {
    //         Object type = types.get(i);
    //         int hit = hits.get(i);
    //         sb.append(type).append(": ").append(hit).append(", ");
    //     }
    //     Log.log(sb);
    // }
}
