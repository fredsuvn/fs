package test.base;

import org.testng.annotations.Test;
import test.task.TaskUtil;
import test.utils.Utils;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.SystemKit;
import xyz.sunqian.common.base.exception.UnknownArrayTypeException;
import xyz.sunqian.common.base.process.ProcessReceipt;
import xyz.sunqian.common.task.RunReceipt;
import xyz.sunqian.common.task.TaskReceipt;
import xyz.sunqian.test.AssertTest;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class JieTest implements AssertTest {

    @Test
    public void testBase() {
        assertNotNull(Jie.LIB_NAME);
        assertNotNull(Jie.LIB_VERSION);
        assertEquals(Jie.NULL_STRING, Objects.toString(null));
        String hello = "hello";
        assertSame(Jie.as(hello), hello);
        assertEquals(Jie.nonnull("123", "456"), "123");
        assertEquals(Jie.nonnull(null, "456"), "456");
        assertEquals(Jie.nonnull("123", () -> "456"), "123");
        assertEquals(Jie.nonnull(null, () -> "456"), "456");
    }

    @Test
    public void testCheckedWrapper() {
        {
            // no return
            int[] i = {0};
            assertEquals(i[0], 0);
            Jie.uncheck(() -> {
                i[0]++;
            }, RuntimeException::new);
            assertEquals(i[0], 1);
            expectThrows(RuntimeException.class, () -> Jie.uncheck(() -> {
                i[1]++;
            }, e -> {
                assertTrue(e instanceof ArrayIndexOutOfBoundsException);
                throw new RuntimeException(e);
            }));
        }
        {
            // return
            assertEquals(Jie.uncheck(() -> 1, RuntimeException::new), 1);
            Exception cause = new Exception();
            expectThrows(RuntimeException.class, () -> Jie.uncheck(() -> {
                throw cause;
            }, e -> {
                assertSame(e, cause);
                throw new RuntimeException(e);
            }));
        }
    }

    @Test
    public void testEquals() throws Exception {
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

        // unknown:
        Method equalsArray = Jie.class.getDeclaredMethod("equalsArray", Object.class, Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, equalsArray, null, "str", "str", true);
    }

    @Test
    public void testHashcode() throws Exception {
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

        // unknown:
        Method hashArray = Jie.class.getDeclaredMethod("hashArray", Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, hashArray, null, "str", true);
    }

    @Test
    public void testToString() throws Exception {
        String str = "str";
        assertEquals(Jie.toString(str), Objects.toString(str));
        Object[] strs = {"str1", "str2"};
        assertEquals(Jie.toStringAll(strs), Arrays.toString(strs));
        Object[][] strss = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(Jie.toString(strss), Arrays.deepToString(strss));
        Object[][] strss2 = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(Jie.toStringWith(strss2, true, false), Arrays.toString(strss2));
        assertEquals(Jie.toStringWith(strss2, false, false), Objects.toString(strss2));
        assertEquals(Jie.toString(new boolean[]{true, false}), Arrays.toString(new boolean[]{true, false}));
        assertEquals(Jie.toString(new byte[]{6, 66}), Arrays.toString(new byte[]{6, 66}));
        assertEquals(Jie.toString(new short[]{6, 66}), Arrays.toString(new short[]{6, 66}));
        assertEquals(Jie.toString(new char[]{6, 66}), Arrays.toString(new char[]{6, 66}));
        assertEquals(Jie.toString(new int[]{6, 66}), Arrays.toString(new int[]{6, 66}));
        assertEquals(Jie.toString(new long[]{6, 66}), Arrays.toString(new long[]{6, 66}));
        assertEquals(Jie.toString(new float[]{6, 66}), Arrays.toString(new float[]{6, 66}));
        assertEquals(Jie.toString(new double[]{6, 66}), Arrays.toString(new double[]{6, 66}));

        // null:
        assertEquals(Jie.toString(null), Objects.toString(null));
        assertEquals(Jie.toStringWith(null, false, false), Objects.toString(null));

        // unknown:
        Method toStringArray = Jie.class.getDeclaredMethod("toStringArray", Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, toStringArray, null, "str", true);
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
            assertEquals(Jie.stream(1, 2, 3).collect(Collectors.toList()), Jie.list(1, 2, 3));
            assertEquals(Jie.stream(Jie.list(1, 2, 3)).collect(Collectors.toList()), Jie.list(1, 2, 3));
        }
        {
            // thread
            Jie.sleep(1);
            Jie.sleep(Duration.ofMillis(1));
            Thread thread = new Thread(Jie::sleep);
            thread.start();
            Utils.awaitUntilExecuteTo(thread, Thread.class.getName(), "sleep");
            thread.interrupt();
            Jie.until(() -> true);
        }
        {
            // process
            {
                if (SystemKit.isWindows()) {
                    ProcessReceipt receipt = Jie.process("cmd.exe", "/c", "dir");
                    receipt.getProcess().destroyForcibly();
                } else {
                    ProcessReceipt receipt = Jie.process("ls", "-l");
                    receipt.getProcess().destroyForcibly();
                }
            }
            {
                if (SystemKit.isWindows()) {
                    ProcessReceipt receipt = Jie.process("cmd.exe /c dir");
                    receipt.getProcess().destroyForcibly();
                } else {
                    ProcessReceipt receipt = Jie.process("ls -l");
                    receipt.getProcess().destroyForcibly();
                }
            }
        }
    }

    @Test
    public void testTask() throws Exception {
        {
            int[] i = {0};
            CountDownLatch latch = new CountDownLatch(1);
            Jie.execute(() -> {
                i[0]++;
                latch.countDown();
            });
            latch.await();
            assertEquals(i[0], 1);
            TaskReceipt<Integer> receipt = Jie.execute(() -> 666);
            assertEquals(receipt.getResult(), 666);
        }
        {
            Instant now = Instant.now();
            CountDownLatch latch = new CountDownLatch(1);
            RunReceipt receipt = Jie.schedule(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await();
            receipt.await();
        }
        {
            Instant now = Instant.now();
            CountDownLatch latch = new CountDownLatch(1);
            TaskReceipt<Integer> receipt = Jie.schedule(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
                return 66;
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await();
            assertEquals(receipt.getResult(), 66);
        }
        {
            Instant now = Instant.now();
            Instant time = now.plusMillis(TaskUtil.DELAY_MILLIS);
            CountDownLatch latch = new CountDownLatch(1);
            RunReceipt receipt = Jie.scheduleAt(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
            }, time);
            latch.await();
            receipt.await();
        }
        {
            Instant now = Instant.now();
            Instant time = now.plusMillis(TaskUtil.DELAY_MILLIS);
            CountDownLatch latch = new CountDownLatch(1);
            TaskReceipt<Integer> receipt = Jie.scheduleAt(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
                return 66;
            }, time);
            latch.await();
            assertEquals(receipt.getResult(), 66);
        }
        {
            Instant now = Instant.now();
            CountDownLatch latch = new CountDownLatch(1);
            RunReceipt receipt = Jie.scheduleWithRate(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
                throw new RuntimeException();
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS), Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await();
            receipt.await();
        }
        {
            Instant now = Instant.now();
            CountDownLatch latch = new CountDownLatch(1);
            RunReceipt receipt = Jie.scheduleWithDelay(() -> {
                TaskUtil.shouldAfterNow(now, TaskUtil.DELAY_MILLIS);
                latch.countDown();
                throw new RuntimeException();
            }, Duration.ofMillis(TaskUtil.DELAY_MILLIS), Duration.ofMillis(TaskUtil.DELAY_MILLIS));
            latch.await();
            receipt.await();
        }
    }
}
