package tests.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.testng.annotations.Test;
import tests.utils.Utils;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.base.exception.UnknownArrayTypeException;
import space.sunqian.common.base.system.OSKit;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.object.convert.ObjectConverter;
import space.sunqian.common.runtime.reflect.TypeRef;
import space.sunqian.test.AssertTest;
import space.sunqian.test.PrintTest;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class KitTest implements AssertTest, PrintTest {

    @Test
    public void testBase() {
        assertNotNull(Kit.LIB_NAME);
        assertNotNull(Kit.LIB_VERSION);
        assertEquals(Kit.NULL_STRING, Objects.toString(null));
        String hello = "hello";
        assertSame(Kit.as(hello), hello);
        assertSame(Kit.asNonnull(hello), hello);
        assertEquals(Kit.nonnull("123", "456"), "123");
        assertEquals(Kit.nonnull(null, "456"), "456");
        assertEquals(Kit.nonnull("123", () -> "456"), "123");
        assertEquals(Kit.nonnull(null, () -> "456"), "456");
    }

    @Test
    public void testUnchecked() {
        {
            // uncheck no return
            int[] i = {0};
            assertEquals(i[0], 0);
            Kit.uncheck(() -> {
                i[0]++;
            }, RuntimeException::new);
            assertEquals(i[0], 1);
            expectThrows(RuntimeException.class, () -> Kit.uncheck(() -> {
                i[1]++;
            }, e -> {
                assertTrue(e instanceof ArrayIndexOutOfBoundsException);
                throw new RuntimeException(e);
            }));
        }
        {
            // uncheck return
            assertEquals(Kit.uncheck(() -> 1, RuntimeException::new), 1);
            Exception cause = new Exception();
            expectThrows(RuntimeException.class, () -> Kit.uncheck(() -> {
                throw cause;
            }, e -> {
                assertSame(e, cause);
                throw new RuntimeException(e);
            }));
        }
        {
            // call
            Exception cause = new Exception();
            assertEquals(Kit.call(() -> 1, 2), 1);
            assertEquals(Kit.call(() -> throwEx(cause), 2), 2);
            assertEquals(Kit.callUncheck(() -> 1, e -> 2), 1);
            assertEquals(Kit.callUncheck(() -> throwEx(cause), e -> {
                assertSame(e, cause);
                return 2;
            }), 2);
        }
        {
            // ignore
            Kit.ignoreException(() -> {
                throw new Exception();
            });
        }
    }

    private int throwEx(Exception cause) throws Exception {
        throw cause;
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(Kit.equals("", ""));
        assertFalse(Kit.equals("", null));
        assertFalse(Kit.equals(null, ""));
        assertFalse(Kit.equals("1", "2"));
        assertFalse(Kit.equals(new boolean[]{true}, ""));
        assertFalse(Kit.equalsWith(new boolean[]{true}, new boolean[]{true}, false, false));
        assertTrue(Kit.equalsAll("", "", ""));
        assertFalse(Kit.equalsAll("1", "2", "3"));
        assertTrue(Kit.equalsAll());
        assertTrue(Kit.equalsAll(new Object[]{""}));
        assertTrue(Kit.equalsAll(new Object[]{"", ""}));
        assertFalse(Kit.equalsAll(new Object[]{"1", "2"}));

        // boolean
        assertTrue(Kit.equals(new boolean[]{true}, new boolean[]{true}));
        assertFalse(Kit.equals(new boolean[]{true}, new byte[]{1}));
        assertFalse(Kit.equals(new boolean[]{true}, new short[]{1}));
        assertFalse(Kit.equals(new boolean[]{true}, new char[]{1}));
        assertFalse(Kit.equals(new boolean[]{true}, new int[]{1}));
        assertFalse(Kit.equals(new boolean[]{true}, new long[]{1}));
        assertFalse(Kit.equals(new boolean[]{true}, new float[]{1}));
        assertFalse(Kit.equals(new boolean[]{true}, new double[]{1}));

        // byte
        assertFalse(Kit.equals(new byte[]{1}, new boolean[]{true}));
        assertTrue(Kit.equals(new byte[]{1}, new byte[]{1}));
        assertFalse(Kit.equals(new byte[]{1}, new short[]{1}));
        assertFalse(Kit.equals(new byte[]{1}, new char[]{1}));
        assertFalse(Kit.equals(new byte[]{1}, new int[]{1}));
        assertFalse(Kit.equals(new byte[]{1}, new long[]{1}));
        assertFalse(Kit.equals(new byte[]{1}, new float[]{1}));
        assertFalse(Kit.equals(new byte[]{1}, new double[]{1}));

        // short
        assertFalse(Kit.equals(new short[]{1}, new boolean[]{true}));
        assertFalse(Kit.equals(new short[]{1}, new byte[]{1}));
        assertTrue(Kit.equals(new short[]{1}, new short[]{1}));
        assertFalse(Kit.equals(new short[]{1}, new char[]{1}));
        assertFalse(Kit.equals(new short[]{1}, new int[]{1}));
        assertFalse(Kit.equals(new short[]{1}, new long[]{1}));
        assertFalse(Kit.equals(new short[]{1}, new float[]{1}));
        assertFalse(Kit.equals(new short[]{1}, new double[]{1}));

        // char
        assertFalse(Kit.equals(new char[]{1}, new boolean[]{true}));
        assertFalse(Kit.equals(new char[]{1}, new byte[]{1}));
        assertFalse(Kit.equals(new char[]{1}, new short[]{1}));
        assertTrue(Kit.equals(new char[]{1}, new char[]{1}));
        assertFalse(Kit.equals(new char[]{1}, new int[]{1}));
        assertFalse(Kit.equals(new char[]{1}, new long[]{1}));
        assertFalse(Kit.equals(new char[]{1}, new float[]{1}));
        assertFalse(Kit.equals(new char[]{1}, new double[]{1}));

        // int
        assertFalse(Kit.equals(new int[]{1}, new boolean[]{true}));
        assertFalse(Kit.equals(new int[]{1}, new byte[]{1}));
        assertFalse(Kit.equals(new int[]{1}, new short[]{1}));
        assertFalse(Kit.equals(new int[]{1}, new char[]{1}));
        assertTrue(Kit.equals(new int[]{1}, new int[]{1}));
        assertFalse(Kit.equals(new int[]{1}, new long[]{1}));
        assertFalse(Kit.equals(new int[]{1}, new float[]{1}));
        assertFalse(Kit.equals(new int[]{1}, new double[]{1}));

        // long
        assertFalse(Kit.equals(new long[]{1}, new boolean[]{true}));
        assertFalse(Kit.equals(new long[]{1}, new byte[]{1}));
        assertFalse(Kit.equals(new long[]{1}, new short[]{1}));
        assertFalse(Kit.equals(new long[]{1}, new char[]{1}));
        assertFalse(Kit.equals(new long[]{1}, new int[]{1}));
        assertTrue(Kit.equals(new long[]{1}, new long[]{1}));
        assertFalse(Kit.equals(new long[]{1}, new float[]{1}));
        assertFalse(Kit.equals(new long[]{1}, new double[]{1}));

        // float
        assertFalse(Kit.equals(new float[]{1}, new boolean[]{true}));
        assertFalse(Kit.equals(new float[]{1}, new byte[]{1}));
        assertFalse(Kit.equals(new float[]{1}, new short[]{1}));
        assertFalse(Kit.equals(new float[]{1}, new char[]{1}));
        assertFalse(Kit.equals(new float[]{1}, new int[]{1}));
        assertFalse(Kit.equals(new float[]{1}, new long[]{1}));
        assertTrue(Kit.equals(new float[]{1}, new float[]{1}));
        assertFalse(Kit.equals(new float[]{1}, new double[]{1}));

        // double
        assertFalse(Kit.equals(new double[]{1}, new boolean[]{true}));
        assertFalse(Kit.equals(new double[]{1}, new byte[]{1}));
        assertFalse(Kit.equals(new double[]{1}, new short[]{1}));
        assertFalse(Kit.equals(new double[]{1}, new char[]{1}));
        assertFalse(Kit.equals(new double[]{1}, new int[]{1}));
        assertFalse(Kit.equals(new double[]{1}, new long[]{1}));
        assertFalse(Kit.equals(new double[]{1}, new float[]{1}));
        assertTrue(Kit.equals(new double[]{1}, new double[]{1}));

        // object
        assertFalse(Kit.equals(new Object[]{1}, new boolean[]{true}));
        assertTrue(Kit.equalsWith(new Object[]{1}, new Object[]{1}, true, true));
        assertFalse(Kit.equalsWith(new Object[]{new Object[]{1}}, new Object[]{new Object[]{1}}, true, false));

        // unknown:
        Method equalsArray = Kit.class.getDeclaredMethod("equalsArray", Object.class, Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, equalsArray, null, "str", "str", true);
    }

    @Test
    public void testHashcode() throws Exception {
        String str = "str";
        assertEquals(Kit.hashCode(str), Objects.hashCode(str));
        Object[] strs = {"str1", "str2"};
        assertEquals(Kit.hashAll(strs), Arrays.hashCode(strs));
        Object[][] strss = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(Kit.hashCode(strss), Arrays.deepHashCode(strss));
        Object[][] strss2 = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(Kit.hashWith(strss2, true, false), Arrays.hashCode(strss2));
        assertEquals(Kit.hashWith(strss2, false, false), Objects.hashCode(strss2));
        assertEquals(Kit.hashCode(new boolean[]{true, false}), Arrays.hashCode(new boolean[]{true, false}));
        assertEquals(Kit.hashCode(new byte[]{6, 66}), Arrays.hashCode(new byte[]{6, 66}));
        assertEquals(Kit.hashCode(new short[]{6, 66}), Arrays.hashCode(new short[]{6, 66}));
        assertEquals(Kit.hashCode(new char[]{6, 66}), Arrays.hashCode(new char[]{6, 66}));
        assertEquals(Kit.hashCode(new int[]{6, 66}), Arrays.hashCode(new int[]{6, 66}));
        assertEquals(Kit.hashCode(new long[]{6, 66}), Arrays.hashCode(new long[]{6, 66}));
        assertEquals(Kit.hashCode(new float[]{6, 66}), Arrays.hashCode(new float[]{6, 66}));
        assertEquals(Kit.hashCode(new double[]{6, 66}), Arrays.hashCode(new double[]{6, 66}));
        assertEquals(Kit.hashId(str), System.identityHashCode(str));

        // null:
        assertEquals(Kit.hashCode(null), Objects.hashCode(null));
        assertEquals(Kit.hashWith(null, false, false), Objects.hashCode(null));

        // unknown:
        Method hashArray = Kit.class.getDeclaredMethod("hashArray", Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, hashArray, null, "str", true);
    }

    @Test
    public void testShortcut() throws Exception {
        {
            // collection
            Integer[] array = {1, 2, 3, 4};
            assertSame(Kit.array(array), array);
            assertEquals(Kit.list(array), Arrays.asList(array));
            assertEquals(Kit.arrayList(array), Arrays.asList(array));
            assertEquals(Kit.linkedList(array), Arrays.asList(array));
            assertEquals(Kit.set(array), new LinkedHashSet<>(Arrays.asList(array)));
            assertEquals(Kit.hashSet(array), new HashSet<>(Arrays.asList(array)));
            assertEquals(Kit.linkedHashSet(array), new LinkedHashSet<>(Arrays.asList(array)));
            Map<Integer, Integer> map = new LinkedHashMap<>();
            map.put(1, 2);
            map.put(3, 4);
            assertEquals(Kit.map(1, 2, 3, 4), map);
            assertEquals(Kit.hashMap(1, 2, 3, 4), new HashMap<>(map));
            assertEquals(Kit.linkedHashMap(1, 2, 3, 4), map);
            assertEquals(Kit.stream(1, 2, 3).collect(Collectors.toList()), Kit.list(1, 2, 3));
            assertEquals(Kit.stream(Kit.list(1, 2, 3)).collect(Collectors.toList()), Kit.list(1, 2, 3));
        }
        {
            // thread
            Kit.sleep(1);
            Kit.sleep(Duration.ofMillis(1));
            Thread thread = new Thread(Kit::sleep);
            thread.start();
            Utils.awaitUntilExecuteTo(thread, Thread.class.getName(), "sleep");
            thread.interrupt();
            Kit.until(() -> true);
        }
        {
            // process
            {
                Process process;
                if (OSKit.isWindows()) {
                    process = Kit.process("cmd.exe", "/c", "dir");
                } else {
                    process = Kit.process("ls", "-l");
                }
                printProcess("split cmd", process);
                process.destroyForcibly();
            }
            {
                Process process;
                if (OSKit.isWindows()) {
                    process = Kit.process("cmd.exe /c dir");
                } else {
                    process = Kit.process("ls -l");
                }
                printProcess("one cmd", process);
                process.destroyForcibly();
            }
        }
    }

    private void printProcess(String title, Process process) {
        InputStream in = process.getInputStream();
        printFor(title, IOKit.string(in, CharsKit.localCharset()));
    }

    @Test
    public void testCopyProperties() {
        StringProps sp = new StringProps("1", "2", "3");
        IntProps ip1 = new IntProps();
        Kit.copyProperties(sp, ip1);
        assertEquals(ip1, new IntProps(1, 2, 3));
        IntProps ip2 = new IntProps();
        Kit.copyProperties(sp, sp.getClass(), ip2, ip2.getClass());
        assertEquals(ip2, new IntProps(1, 2, 3));
        IntProps ip3 = new IntProps();
        Kit.copyProperties(sp, sp.getClass(), ip3, ip3.getClass(), ObjectConverter.defaultConverter());
        assertEquals(ip3, new IntProps(1, 2, 3));
        IntProps ip4 = new IntProps();
        Kit.copyProperties(sp, ip4, ObjectConverter.defaultConverter());
        assertEquals(ip4, new IntProps(1, 2, 3));
    }

    @Test
    public void testObjectConversion() {
        StringProps sp = new StringProps("1", "2", "3");
        assertEquals(Kit.convert(sp, IntProps.class), new IntProps(1, 2, 3));
        assertEquals(Kit.convert(sp, sp.getClass(), IntProps.class), new IntProps(1, 2, 3));
        assertEquals(Kit.convert(sp, new TypeRef<IntProps>() {}), new IntProps(1, 2, 3));
        assertEquals(Kit.convert(sp, sp.getClass(), new TypeRef<IntProps>() {}), new IntProps(1, 2, 3));
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StringProps {
        private String first;
        private String second;
        private String third;
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IntProps {
        private Integer first;
        private Integer second;
        private Integer third;
    }
}
