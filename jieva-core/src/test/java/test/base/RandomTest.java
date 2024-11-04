package test.base;

import org.testng.annotations.Test;
import test.Log;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.invoke.InvocationException;
import xyz.sunqian.common.invoke.Invoker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

import static org.testng.Assert.*;

public class RandomTest {

    @Test
    public void testNextInt() {
        final int size = 10;
        final int startInclusive = 6;
        final int endExclusive = 66;

        // boolean
        Log.log(JieRandom.nextBoolean());
        boolean[] bools = JieRandom.fill(new boolean[size]);
        Log.log(JieArray.asList(bools));

        // byte
        Log.log(JieRandom.nextByte());
        byte br = JieRandom.nextByte(startInclusive, endExclusive);
        assertTrue(br >= startInclusive && br < endExclusive);
        br = JieRandom.nextByte((byte) startInclusive, (byte) endExclusive);
        assertTrue(br >= startInclusive && br < endExclusive);
        byte[] ib = JieRandom.fill(new byte[size]);
        Log.log(JieArray.asList(ib));
        ib = JieRandom.fill(new byte[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(ib));
        for (int i : ib) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }
        ib = JieRandom.fill(new byte[size], (byte) startInclusive, (byte) endExclusive);
        Log.log(JieArray.asList(ib));
        for (int i : ib) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // short
        Log.log(JieRandom.nextShort());
        short sr = JieRandom.nextShort(startInclusive, endExclusive);
        assertTrue(sr >= startInclusive && sr < endExclusive);
        sr = JieRandom.nextShort((short) startInclusive, (short) endExclusive);
        assertTrue(sr >= startInclusive && sr < endExclusive);
        short[] sa = JieRandom.fill(new short[size]);
        Log.log(JieArray.asList(sa));
        sa = JieRandom.fill(new short[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(sa));
        for (int i : sa) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }
        sa = JieRandom.fill(new short[size], (short) startInclusive, (short) endExclusive);
        Log.log(JieArray.asList(sa));
        for (int i : sa) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // char
        Log.log(JieRandom.nextChar());
        char cr = JieRandom.nextChar(startInclusive, endExclusive);
        assertTrue(cr >= startInclusive && cr < endExclusive);
        cr = JieRandom.nextChar((char) startInclusive, (char) endExclusive);
        assertTrue(cr >= startInclusive && cr < endExclusive);
        char[] ca = JieRandom.fill(new char[size]);
        Log.log(JieArray.asList(ca));
        ca = JieRandom.fill(new char[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(ca));
        for (int i : ca) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }
        ca = JieRandom.fill(new char[size], (char) startInclusive, (char) endExclusive);
        Log.log(JieArray.asList(ca));
        for (int i : ca) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // int
        Log.log(JieRandom.nextInt());
        int ir = JieRandom.nextInt(startInclusive, endExclusive);
        assertTrue(ir >= startInclusive && ir < endExclusive);
        int[] ia = JieRandom.fill(new int[size]);
        Log.log(JieArray.asList(ia));
        ia = JieRandom.fill(new int[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(ia));
        for (int i : ia) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // long
        Log.log(JieRandom.nextLong());
        long lr = JieRandom.nextLong(startInclusive, endExclusive);
        assertTrue(lr >= startInclusive && lr < endExclusive);
        long[] la = JieRandom.fill(new long[size]);
        Log.log(JieArray.asList(la));
        la = JieRandom.fill(new long[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(la));
        for (long i : la) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // float
        Log.log(JieRandom.nextFloat());
        float fr = JieRandom.nextFloat(startInclusive, endExclusive);
        assertTrue(fr >= startInclusive && fr < endExclusive);
        float[] fa = JieRandom.fill(new float[size]);
        Log.log(JieArray.asList(fa));
        fa = JieRandom.fill(new float[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(fa));
        for (float i : fa) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }

        // double
        Log.log(JieRandom.nextDouble());
        double dr = JieRandom.nextDouble(startInclusive, endExclusive);
        assertTrue(dr >= startInclusive && dr < endExclusive);
        double[] da = JieRandom.fill(new double[size]);
        Log.log(JieArray.asList(da));
        da = JieRandom.fill(new double[size], startInclusive, endExclusive);
        Log.log(JieArray.asList(da));
        for (double i : da) {
            assertTrue(i >= startInclusive && i < endExclusive);
        }
    }

    @Test
    public void testSupplier() throws Exception {
        Supplier<Integer> s1 = JieRandom.supplier(
            JieRandom.score(20, 1),
            JieRandom.score(30, 2),
            JieRandom.score(30, 3),
            JieRandom.score(30, 4),
            JieRandom.score(30, 5),
            JieRandom.score(30, 6),
            JieRandom.score(30, 7),
            JieRandom.score(30, 8),
            JieRandom.score(30, 9),
            JieRandom.score(50, () -> 10)
        );
        testSupplier(s1);
        Supplier<Integer> s2 = JieRandom.supplier(Jie.list(
            JieRandom.score(20, 2),
            JieRandom.score(30, 3),
            JieRandom.score(50, () -> 5)
        ));
        testSupplier(s2);
        Supplier<Integer> s3 = JieRandom.supplier(new Random(),
            JieRandom.score(20, 2)
        );
        testSupplier(s3);
        Supplier<Integer> s4 = JieRandom.supplier(new Random(), Jie.list(
            JieRandom.score(20, 2)
        ));
        testSupplier(s4);
        expectThrows(IllegalArgumentException.class, () -> JieRandom.supplier());
        expectThrows(IllegalArgumentException.class, () -> JieRandom.supplier(Jie.list()));
        expectThrows(IllegalArgumentException.class, () -> JieRandom.supplier(new Random()));
        expectThrows(IllegalArgumentException.class, () -> JieRandom.supplier(new Random(), Jie.list()));

        // test unreadable
        Method binarySearch = s1.getClass().getDeclaredMethod("binarySearch", int.class);
        binarySearch.setAccessible(true);
        Field field = s1.getClass().getDeclaredField("totalScore");
        field.setAccessible(true);
        Method supply = s1.getClass().getDeclaredMethod("supply", int.class);
        supply.setAccessible(true);
        Invoker supplyInvoker = Invoker.reflect(supply);
        int totalScore = (Integer) field.get(s1);
        for (int i = 0; i < totalScore * 2; i++) {
            Object node = binarySearch.invoke(s1, i);
            if (node == null) {
                int next = i;
                expectThrows(IllegalStateException.class, () -> {
                    try {
                        supplyInvoker.invoke(s1, next);
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
