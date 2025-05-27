package test.base;


import org.testng.annotations.Test;
import test.utils.Utils;
import xyz.sunqian.common.base.Jie;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

public class JieTest {

    @Test
    public void testBase() {
        String hello = "hello";
        assertSame(Jie.as(hello), hello);
        assertEquals(Jie.nonnull("123", "456"), "123");
        assertEquals(Jie.nonnull(null, "456"), "456");
        assertEquals(Jie.nonnull("123", () -> "456"), "123");
        assertEquals(Jie.nonnull(null, () -> "456"), "456");
    }

    @Test
    public void testEquals() {
        assertTrue(Jie.equals("", ""));
        assertFalse(Jie.equals("", null));
        assertFalse(Jie.equals(null, ""));
        assertFalse(Jie.equals("1", "2"));
        assertFalse(Jie.equals(new boolean[]{true}, ""));
        assertFalse(Jie.equalsWith(new boolean[]{true}, new boolean[]{true}, false, false));
        assertTrue(Jie.equalsAll("", "", ""));
        assertFalse(Jie.equalsAll("1", "2", "3"));
        assertTrue(Jie.equalsAll());
        assertTrue(Jie.equalsAll(new Object[]{""}));
        assertTrue(Jie.equalsAll(new Object[]{"", ""}));
        assertFalse(Jie.equalsAll(new Object[]{"1", "2"}));

        // boolean
        assertTrue(Jie.equals(new boolean[]{true}, new boolean[]{true}));
        assertFalse(Jie.equals(new boolean[]{true}, new byte[]{1}));
        assertFalse(Jie.equals(new boolean[]{true}, new short[]{1}));
        assertFalse(Jie.equals(new boolean[]{true}, new char[]{1}));
        assertFalse(Jie.equals(new boolean[]{true}, new int[]{1}));
        assertFalse(Jie.equals(new boolean[]{true}, new long[]{1}));
        assertFalse(Jie.equals(new boolean[]{true}, new float[]{1}));
        assertFalse(Jie.equals(new boolean[]{true}, new double[]{1}));

        // byte
        assertFalse(Jie.equals(new byte[]{1}, new boolean[]{true}));
        assertTrue(Jie.equals(new byte[]{1}, new byte[]{1}));
        assertFalse(Jie.equals(new byte[]{1}, new short[]{1}));
        assertFalse(Jie.equals(new byte[]{1}, new char[]{1}));
        assertFalse(Jie.equals(new byte[]{1}, new int[]{1}));
        assertFalse(Jie.equals(new byte[]{1}, new long[]{1}));
        assertFalse(Jie.equals(new byte[]{1}, new float[]{1}));
        assertFalse(Jie.equals(new byte[]{1}, new double[]{1}));

        // short
        assertFalse(Jie.equals(new short[]{1}, new boolean[]{true}));
        assertFalse(Jie.equals(new short[]{1}, new byte[]{1}));
        assertTrue(Jie.equals(new short[]{1}, new short[]{1}));
        assertFalse(Jie.equals(new short[]{1}, new char[]{1}));
        assertFalse(Jie.equals(new short[]{1}, new int[]{1}));
        assertFalse(Jie.equals(new short[]{1}, new long[]{1}));
        assertFalse(Jie.equals(new short[]{1}, new float[]{1}));
        assertFalse(Jie.equals(new short[]{1}, new double[]{1}));

        // char
        assertFalse(Jie.equals(new char[]{1}, new boolean[]{true}));
        assertFalse(Jie.equals(new char[]{1}, new byte[]{1}));
        assertFalse(Jie.equals(new char[]{1}, new short[]{1}));
        assertTrue(Jie.equals(new char[]{1}, new char[]{1}));
        assertFalse(Jie.equals(new char[]{1}, new int[]{1}));
        assertFalse(Jie.equals(new char[]{1}, new long[]{1}));
        assertFalse(Jie.equals(new char[]{1}, new float[]{1}));
        assertFalse(Jie.equals(new char[]{1}, new double[]{1}));

        // int
        assertFalse(Jie.equals(new int[]{1}, new boolean[]{true}));
        assertFalse(Jie.equals(new int[]{1}, new byte[]{1}));
        assertFalse(Jie.equals(new int[]{1}, new short[]{1}));
        assertFalse(Jie.equals(new int[]{1}, new char[]{1}));
        assertTrue(Jie.equals(new int[]{1}, new int[]{1}));
        assertFalse(Jie.equals(new int[]{1}, new long[]{1}));
        assertFalse(Jie.equals(new int[]{1}, new float[]{1}));
        assertFalse(Jie.equals(new int[]{1}, new double[]{1}));

        // long
        assertFalse(Jie.equals(new long[]{1}, new boolean[]{true}));
        assertFalse(Jie.equals(new long[]{1}, new byte[]{1}));
        assertFalse(Jie.equals(new long[]{1}, new short[]{1}));
        assertFalse(Jie.equals(new long[]{1}, new char[]{1}));
        assertFalse(Jie.equals(new long[]{1}, new int[]{1}));
        assertTrue(Jie.equals(new long[]{1}, new long[]{1}));
        assertFalse(Jie.equals(new long[]{1}, new float[]{1}));
        assertFalse(Jie.equals(new long[]{1}, new double[]{1}));

        // float
        assertFalse(Jie.equals(new float[]{1}, new boolean[]{true}));
        assertFalse(Jie.equals(new float[]{1}, new byte[]{1}));
        assertFalse(Jie.equals(new float[]{1}, new short[]{1}));
        assertFalse(Jie.equals(new float[]{1}, new char[]{1}));
        assertFalse(Jie.equals(new float[]{1}, new int[]{1}));
        assertFalse(Jie.equals(new float[]{1}, new long[]{1}));
        assertTrue(Jie.equals(new float[]{1}, new float[]{1}));
        assertFalse(Jie.equals(new float[]{1}, new double[]{1}));

        // double
        assertFalse(Jie.equals(new double[]{1}, new boolean[]{true}));
        assertFalse(Jie.equals(new double[]{1}, new byte[]{1}));
        assertFalse(Jie.equals(new double[]{1}, new short[]{1}));
        assertFalse(Jie.equals(new double[]{1}, new char[]{1}));
        assertFalse(Jie.equals(new double[]{1}, new int[]{1}));
        assertFalse(Jie.equals(new double[]{1}, new long[]{1}));
        assertFalse(Jie.equals(new double[]{1}, new float[]{1}));
        assertTrue(Jie.equals(new double[]{1}, new double[]{1}));

        // object
        assertFalse(Jie.equals(new Object[]{1}, new boolean[]{true}));
        assertTrue(Jie.equalsWith(new Object[]{1}, new Object[]{1}, true, true));
        assertFalse(Jie.equalsWith(new Object[]{new Object[]{1}}, new Object[]{new Object[]{1}}, true, false));
    }

    @Test
    public void testHashcode() {
        String str = "str";
        assertEquals(Jie.hashCode(str), Objects.hashCode(str));
        Object[] strs = {"str1", "str2"};
        assertEquals(Jie.hashAll(strs), Arrays.hashCode(strs));
        Object[][] strss = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(Jie.hashCode(strss), Arrays.deepHashCode(strss));
        Object[][] strss2 = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(Jie.hashWith(strss2, true, false), Arrays.hashCode(strss2));
        assertEquals(Jie.hashWith(strss2, false, false), Objects.hashCode(strss2));
        assertEquals(Jie.hashCode(new boolean[]{true, false}), Arrays.hashCode(new boolean[]{true, false}));
        assertEquals(Jie.hashCode(new byte[]{6, 66}), Arrays.hashCode(new byte[]{6, 66}));
        assertEquals(Jie.hashCode(new short[]{6, 66}), Arrays.hashCode(new short[]{6, 66}));
        assertEquals(Jie.hashCode(new char[]{6, 66}), Arrays.hashCode(new char[]{6, 66}));
        assertEquals(Jie.hashCode(new int[]{6, 66}), Arrays.hashCode(new int[]{6, 66}));
        assertEquals(Jie.hashCode(new long[]{6, 66}), Arrays.hashCode(new long[]{6, 66}));
        assertEquals(Jie.hashCode(new float[]{6, 66}), Arrays.hashCode(new float[]{6, 66}));
        assertEquals(Jie.hashCode(new double[]{6, 66}), Arrays.hashCode(new double[]{6, 66}));
        assertEquals(Jie.hashId(str), System.identityHashCode(str));

        // null:
        assertEquals(Jie.hashCode(null), Objects.hashCode(null));
        assertEquals(Jie.hashWith(null, false, false), Objects.hashCode(null));
    }

    @Test
    public void testShortcut() throws Exception {
        {
            // collection
            Integer[] array = {1, 2, 3, 4};
            assertSame(Jie.array(array), array);
            assertEquals(Jie.list(array), Arrays.asList(array));
            assertEquals(Jie.arrayList(array), Arrays.asList(array));
            assertEquals(Jie.linkedList(array), Arrays.asList(array));
            assertEquals(Jie.set(array), new LinkedHashSet<>(Arrays.asList(array)));
            assertEquals(Jie.hashSet(array), new HashSet<>(Arrays.asList(array)));
            assertEquals(Jie.linkedHashSet(array), new LinkedHashSet<>(Arrays.asList(array)));
            Map<Integer, Integer> map = new LinkedHashMap<>();
            map.put(1, 2);
            map.put(3, 4);
            assertEquals(Jie.map(1, 2, 3, 4), map);
            assertEquals(Jie.hashMap(1, 2, 3, 4), new HashMap<>(map));
            assertEquals(Jie.linkedHashMap(1, 2, 3, 4), map);
        }
        {
            // sleep
            Jie.sleep(1);
            Jie.sleep(Duration.ofMillis(1));
            Thread thread = new Thread(Jie::sleep);
            thread.start();
            Utils.awaitUntilExecuteTo(thread, Thread.class.getName(), "sleep");
            thread.interrupt();
            Jie.until(() -> true);
            Jie.untilChecked(() -> true);
        }
        {
            // task
            Jie.run(() -> {
            });
            Jie.run(() -> 1);
            Jie.schedule(() -> {
            }, Duration.ofMillis(1));
            Jie.schedule(() -> 1, Duration.ofMillis(1));
            Jie.scheduleAt(() -> {
            }, Instant.now().plusMillis(1));
            Jie.scheduleAt(() -> 1, Instant.now().plusMillis(1));
            Jie.scheduleWithRate(() -> {
                throw new RuntimeException();
            }, Duration.ofMillis(1), Duration.ofMillis(1));
            Jie.scheduleWithDelay(() -> {
                throw new RuntimeException();
            }, Duration.ofMillis(1), Duration.ofMillis(1));
        }
    }
}
