package test.base;

import org.testng.annotations.Test;
import test.Log;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.random.RandomKit;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.invoke.InvocationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class RandomTest {

    @Test
    public void testNextInt() {
        final int size = 10;
        final int startInclusive = 6;
        final int endExclusive = 66;

        // boolean
        Log.log(RandomKit.nextBoolean());
        boolean[] bools = RandomKit.fill(new boolean[size]);
        Log.log(JieArray.asList(bools));

        // byte
        Log.log(RandomKit.nextByte());
        byte br = RandomKit.nextByte(startInclusive, endExclusive);
        assertTrue(br >= startInclusive && br < endExclusive);
        br = RandomKit.nextByte((byte) startInclusive, (byte) endExclusive);
        assertTrue(br >= startInclusive && br < endExclusive);
        byte[] ib = RandomKit.fill(new byte[size]);
        Log.log(JieArray.asList(ib));
        ib = RandomKit.fill(new byte[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(ib));
        for (int i : ib) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }
        ib = RandomKit.fill(new byte[size], (byte) startInclusive, (byte) endExclusive);
        Log.log(JieArray.asList(ib));
        for (int i : ib) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // short
        Log.log(RandomKit.nextShort());
        short sr = RandomKit.nextShort(startInclusive, endExclusive);
        assertTrue(sr >= startInclusive && sr < endExclusive);
        sr = RandomKit.nextShort((short) startInclusive, (short) endExclusive);
        assertTrue(sr >= startInclusive && sr < endExclusive);
        short[] sa = RandomKit.fill(new short[size]);
        Log.log(JieArray.asList(sa));
        sa = RandomKit.fill(new short[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(sa));
        for (int i : sa) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }
        sa = RandomKit.fill(new short[size], (short) startInclusive, (short) endExclusive);
        Log.log(JieArray.asList(sa));
        for (int i : sa) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // char
        Log.log(RandomKit.nextChar());
        char cr = RandomKit.nextChar(startInclusive, endExclusive);
        assertTrue(cr >= startInclusive && cr < endExclusive);
        cr = RandomKit.nextChar((char) startInclusive, (char) endExclusive);
        assertTrue(cr >= startInclusive && cr < endExclusive);
        char[] ca = RandomKit.fill(new char[size]);
        Log.log(JieArray.asList(ca));
        ca = RandomKit.fill(new char[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(ca));
        for (int i : ca) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }
        ca = RandomKit.fill(new char[size], (char) startInclusive, (char) endExclusive);
        Log.log(JieArray.asList(ca));
        for (int i : ca) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // int
        Log.log(RandomKit.nextInt());
        int ir = RandomKit.nextInt(startInclusive, endExclusive);
        assertTrue(ir >= startInclusive && ir < endExclusive);
        int[] ia = RandomKit.fill(new int[size]);
        Log.log(JieArray.asList(ia));
        ia = RandomKit.fill(new int[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(ia));
        for (int i : ia) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // long
        Log.log(RandomKit.nextLong());
        long lr = RandomKit.nextLong(startInclusive, endExclusive);
        assertTrue(lr >= startInclusive && lr < endExclusive);
        long[] la = RandomKit.fill(new long[size]);
        Log.log(JieArray.asList(la));
        la = RandomKit.fill(new long[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(la));
        for (long i : la) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // float
        Log.log(RandomKit.nextFloat());
        float fr = RandomKit.nextFloat(startInclusive, endExclusive);
        assertTrue(fr >= startInclusive && fr < endExclusive);
        float[] fa = RandomKit.fill(new float[size]);
        Log.log(JieArray.asList(fa));
        fa = RandomKit.fill(new float[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(fa));
        for (float i : fa) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // double
        Log.log(RandomKit.nextDouble());
        double dr = RandomKit.nextDouble(startInclusive, endExclusive);
        assertTrue(dr >= startInclusive && dr < endExclusive);
        double[] da = RandomKit.fill(new double[size]);
        Log.log(JieArray.asList(da));
        da = RandomKit.fill(new double[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(da));
        for (double i : da) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }
    }

    @Test
    public void testSupplier() throws Exception {
        Supplier<Integer> s1 = RandomKit.supplier(
            RandomKit.score(20, 1),
            RandomKit.score(30, 2),
            RandomKit.score(30, 3),
            RandomKit.score(30, 4),
            RandomKit.score(30, 5),
            RandomKit.score(30, 6),
            RandomKit.score(30, 7),
            RandomKit.score(30, 8),
            RandomKit.score(30, 9),
            RandomKit.score(50, () -> 10)
        );
        testSupplier(s1);
        Supplier<Integer> s2 = RandomKit.supplier(Jie.list(
            RandomKit.score(20, 2),
            RandomKit.score(30, 3),
            RandomKit.score(50, () -> 5)
        ));
        testSupplier(s2);
        Supplier<Integer> s3 = RandomKit.supplier(new Random(),
            RandomKit.score(20, 2)
        );
        testSupplier(s3);
        Supplier<Integer> s4 = RandomKit.supplier(new Random(), Jie.list(
            RandomKit.score(20, 2)
        ));
        testSupplier(s4);
        expectThrows(IllegalArgumentException.class, () -> RandomKit.supplier());
        expectThrows(IllegalArgumentException.class, () -> RandomKit.supplier(Jie.list()));
        expectThrows(IllegalArgumentException.class, () -> RandomKit.supplier(new Random()));
        expectThrows(IllegalArgumentException.class, () -> RandomKit.supplier(new Random(), Jie.list()));

        // test unreadable
        Method binarySearch = s1.getClass().getDeclaredMethod("binarySearch", int.class);
        binarySearch.setAccessible(true);
        Field field = s1.getClass().getDeclaredField("totalScore");
        field.setAccessible(true);
        Method supply = s1.getClass().getDeclaredMethod("supply", int.class);
        supply.setAccessible(true);
        Invocable supplyInvocable = Invocable.of(supply);
        int totalScore = (Integer) field.get(s1);
        for (int i = 0; i < totalScore * 2; i++) {
            Object node = binarySearch.invoke(s1, i);
            if (node == null) {
                int next = i;
                expectThrows(IllegalStateException.class, () -> {
                    try {
                        supplyInvocable.invoke(s1, next);
                    } catch (InvocationException e) {
                        throw e.getCause();
                    }
                });
            }
        }
    }

    private void testSupplier(Supplier<?> supplier) {
        final int times = 10000;
        List<Object> types = new ArrayList<>();
        List<Integer> hits = new ArrayList<>();
        GET:
        for (int i = 0; i < times; i++) {
            Object result = supplier.get();
            for (int j = 0; j < types.size(); j++) {
                if (Objects.equals(types.get(j), result)) {
                    hits.set(j, hits.get(j) + 1);
                    continue GET;
                }
            }
            types.add(result);
            hits.add(1);
        }
        int total = hits.stream().mapToInt(it -> it).sum();
        assertEquals(total, times);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.size(); i++) {
            Object type = types.get(i);
            int hit = hits.get(i);
            sb.append(type).append(": ").append(hit).append(", ");
        }
        Log.log(sb);
    }
}
