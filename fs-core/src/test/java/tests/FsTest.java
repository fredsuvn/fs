package tests;

import internal.test.AssertTest;
import internal.test.PrintTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.common.Fs;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.base.exception.AwaitingException;
import space.sunqian.common.base.exception.UnknownArrayTypeException;
import space.sunqian.common.base.exception.UnreachablePointException;
import space.sunqian.common.base.system.OSKit;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.object.convert.ObjectConverter;
import space.sunqian.common.runtime.reflect.TypeRef;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FsTest implements AssertTest, PrintTest {

    @Test
    public void testBase() {
        assertNotNull(Fs.LIB_NAME);
        assertNotNull(Fs.LIB_VERSION);
        assertEquals(Fs.NULL_STRING, Objects.toString(null));
        String hello = "hello";
        assertSame(hello, Fs.as(hello));
        assertSame(hello, Fs.asNonnull(hello));
        assertEquals("123", Fs.nonnull("123", "456"));
        assertEquals("456", Fs.nonnull(null, "456"));
        assertEquals("123", Fs.nonnull("123", () -> "456"));
        assertEquals("456", Fs.nonnull(null, () -> "456"));
    }

    @Test
    public void testUnchecked() {
        {
            // uncheck no return
            int[] i = {0};
            assertEquals(0, i[0]);
            Fs.uncheck(() -> {
                i[0]++;
            }, RuntimeException::new);
            assertEquals(1, i[0]);
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> Fs.uncheck(() -> {
                i[1]++;
            }, e -> {
                assertInstanceOf(ArrayIndexOutOfBoundsException.class, e);
                return (ArrayIndexOutOfBoundsException) e;
            }));
        }
        {
            // uncheck return
            assertEquals(1, Fs.uncheck(() -> 1, RuntimeException::new));
            Exception cause = new Exception();
            assertThrows(UnreachablePointException.class, () -> Fs.uncheck(() -> {
                throw cause;
            }, e -> {
                assertSame(e, cause);
                return new UnreachablePointException(e);
            }));
        }
        {
            // call
            Exception cause = new Exception();
            assertEquals(1, Fs.call(() -> 1, 2));
            assertEquals(2, Fs.call(() -> throwEx(cause), 2));
            assertEquals(1, Fs.callUncheck(() -> 1, e -> 2));
            assertEquals(2, Fs.callUncheck(() -> throwEx(cause), e -> {
                assertSame(e, cause);
                return 2;
            }));
        }
        {
            // ignore
            Fs.uncheck(() -> {
                throw new Exception();
            });
        }
    }

    private int throwEx(Exception cause) throws Exception {
        throw cause;
    }

    @Test
    public void testUntil() {
        int[] i = {0};
        Fs.until(() -> i[0]++ >= 10);
        assertEquals(11, i[0]);
        RuntimeException cause = new RuntimeException();
        try {
            Fs.until(() -> {
                throw cause;
            });
        } catch (AwaitingException e) {
            assertSame(e.getCause(), cause);
        }
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(Fs.equals("", ""));
        assertFalse(Fs.equals("", null));
        assertFalse(Fs.equals(null, ""));
        assertFalse(Fs.equals("1", "2"));
        assertFalse(Fs.equals(new boolean[]{true}, ""));
        assertFalse(Fs.equalsWith(new boolean[]{true}, new boolean[]{true}, false, false));
        assertTrue(Fs.equalsAll("", "", ""));
        assertFalse(Fs.equalsAll("1", "2", "3"));
        assertTrue(Fs.equalsAll());
        assertTrue(Fs.equalsAll(new Object[]{""}));
        assertTrue(Fs.equalsAll(new Object[]{"", ""}));
        assertFalse(Fs.equalsAll(new Object[]{"1", "2"}));

        // boolean
        assertTrue(Fs.equals(new boolean[]{true}, new boolean[]{true}));
        assertFalse(Fs.equals(new boolean[]{true}, new byte[]{1}));
        assertFalse(Fs.equals(new boolean[]{true}, new short[]{1}));
        assertFalse(Fs.equals(new boolean[]{true}, new char[]{1}));
        assertFalse(Fs.equals(new boolean[]{true}, new int[]{1}));
        assertFalse(Fs.equals(new boolean[]{true}, new long[]{1}));
        assertFalse(Fs.equals(new boolean[]{true}, new float[]{1}));
        assertFalse(Fs.equals(new boolean[]{true}, new double[]{1}));

        // byte
        assertFalse(Fs.equals(new byte[]{1}, new boolean[]{true}));
        assertTrue(Fs.equals(new byte[]{1}, new byte[]{1}));
        assertFalse(Fs.equals(new byte[]{1}, new short[]{1}));
        assertFalse(Fs.equals(new byte[]{1}, new char[]{1}));
        assertFalse(Fs.equals(new byte[]{1}, new int[]{1}));
        assertFalse(Fs.equals(new byte[]{1}, new long[]{1}));
        assertFalse(Fs.equals(new byte[]{1}, new float[]{1}));
        assertFalse(Fs.equals(new byte[]{1}, new double[]{1}));

        // short
        assertFalse(Fs.equals(new short[]{1}, new boolean[]{true}));
        assertFalse(Fs.equals(new short[]{1}, new byte[]{1}));
        assertTrue(Fs.equals(new short[]{1}, new short[]{1}));
        assertFalse(Fs.equals(new short[]{1}, new char[]{1}));
        assertFalse(Fs.equals(new short[]{1}, new int[]{1}));
        assertFalse(Fs.equals(new short[]{1}, new long[]{1}));
        assertFalse(Fs.equals(new short[]{1}, new float[]{1}));
        assertFalse(Fs.equals(new short[]{1}, new double[]{1}));

        // char
        assertFalse(Fs.equals(new char[]{1}, new boolean[]{true}));
        assertFalse(Fs.equals(new char[]{1}, new byte[]{1}));
        assertFalse(Fs.equals(new char[]{1}, new short[]{1}));
        assertTrue(Fs.equals(new char[]{1}, new char[]{1}));
        assertFalse(Fs.equals(new char[]{1}, new int[]{1}));
        assertFalse(Fs.equals(new char[]{1}, new long[]{1}));
        assertFalse(Fs.equals(new char[]{1}, new float[]{1}));
        assertFalse(Fs.equals(new char[]{1}, new double[]{1}));

        // int
        assertFalse(Fs.equals(new int[]{1}, new boolean[]{true}));
        assertFalse(Fs.equals(new int[]{1}, new byte[]{1}));
        assertFalse(Fs.equals(new int[]{1}, new short[]{1}));
        assertFalse(Fs.equals(new int[]{1}, new char[]{1}));
        assertTrue(Fs.equals(new int[]{1}, new int[]{1}));
        assertFalse(Fs.equals(new int[]{1}, new long[]{1}));
        assertFalse(Fs.equals(new int[]{1}, new float[]{1}));
        assertFalse(Fs.equals(new int[]{1}, new double[]{1}));

        // long
        assertFalse(Fs.equals(new long[]{1}, new boolean[]{true}));
        assertFalse(Fs.equals(new long[]{1}, new byte[]{1}));
        assertFalse(Fs.equals(new long[]{1}, new short[]{1}));
        assertFalse(Fs.equals(new long[]{1}, new char[]{1}));
        assertFalse(Fs.equals(new long[]{1}, new int[]{1}));
        assertTrue(Fs.equals(new long[]{1}, new long[]{1}));
        assertFalse(Fs.equals(new long[]{1}, new float[]{1}));
        assertFalse(Fs.equals(new long[]{1}, new double[]{1}));

        // float
        assertFalse(Fs.equals(new float[]{1}, new boolean[]{true}));
        assertFalse(Fs.equals(new float[]{1}, new byte[]{1}));
        assertFalse(Fs.equals(new float[]{1}, new short[]{1}));
        assertFalse(Fs.equals(new float[]{1}, new char[]{1}));
        assertFalse(Fs.equals(new float[]{1}, new int[]{1}));
        assertFalse(Fs.equals(new float[]{1}, new long[]{1}));
        assertTrue(Fs.equals(new float[]{1}, new float[]{1}));
        assertFalse(Fs.equals(new float[]{1}, new double[]{1}));

        // double
        assertFalse(Fs.equals(new double[]{1}, new boolean[]{true}));
        assertFalse(Fs.equals(new double[]{1}, new byte[]{1}));
        assertFalse(Fs.equals(new double[]{1}, new short[]{1}));
        assertFalse(Fs.equals(new double[]{1}, new char[]{1}));
        assertFalse(Fs.equals(new double[]{1}, new int[]{1}));
        assertFalse(Fs.equals(new double[]{1}, new long[]{1}));
        assertFalse(Fs.equals(new double[]{1}, new float[]{1}));
        assertTrue(Fs.equals(new double[]{1}, new double[]{1}));

        // object
        assertFalse(Fs.equals(new Object[]{1}, new boolean[]{true}));
        assertTrue(Fs.equalsWith(new Object[]{1}, new Object[]{1}, true, true));
        assertFalse(Fs.equalsWith(new Object[]{new Object[]{1}}, new Object[]{new Object[]{1}}, true, false));

        // unknown:
        Method equalsArray = Fs.class.getDeclaredMethod("equalsArray", Object.class, Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, equalsArray, null, "str", "str", true);
    }

    @Test
    public void testHashcode() throws Exception {
        String str = "str";
        assertEquals(Fs.hashCode(str), Objects.hashCode(str));
        Object[] strs = {"str1", "str2"};
        assertEquals(Fs.hashAll(strs), Arrays.hashCode(strs));
        Object[][] strss = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(Fs.hashCode(strss), Arrays.deepHashCode(strss));
        Object[][] strss2 = {{"str1", "str2"}, {"str3", "str4"}};
        assertEquals(Fs.hashWith(strss2, true, false), Arrays.hashCode(strss2));
        assertEquals(Fs.hashWith(strss2, false, false), Objects.hashCode(strss2));
        assertEquals(Fs.hashCode(new boolean[]{true, false}), Arrays.hashCode(new boolean[]{true, false}));
        assertEquals(Fs.hashCode(new byte[]{6, 66}), Arrays.hashCode(new byte[]{6, 66}));
        assertEquals(Fs.hashCode(new short[]{6, 66}), Arrays.hashCode(new short[]{6, 66}));
        assertEquals(Fs.hashCode(new char[]{6, 66}), Arrays.hashCode(new char[]{6, 66}));
        assertEquals(Fs.hashCode(new int[]{6, 66}), Arrays.hashCode(new int[]{6, 66}));
        assertEquals(Fs.hashCode(new long[]{6, 66}), Arrays.hashCode(new long[]{6, 66}));
        assertEquals(Fs.hashCode(new float[]{6, 66}), Arrays.hashCode(new float[]{6, 66}));
        assertEquals(Fs.hashCode(new double[]{6, 66}), Arrays.hashCode(new double[]{6, 66}));
        assertEquals(Fs.hashId(str), System.identityHashCode(str));

        // null:
        assertEquals(Fs.hashCode(null), Objects.hashCode(null));
        assertEquals(Fs.hashWith(null, false, false), Objects.hashCode(null));

        // unknown:
        Method hashArray = Fs.class.getDeclaredMethod("hashArray", Object.class, boolean.class);
        invokeThrows(UnknownArrayTypeException.class, hashArray, null, "str", true);
    }

    @Test
    public void testShortcut() throws Exception {
        {
            // collection
            Integer[] array = {1, 2, 3, 4};
            assertSame(Fs.array(array), array);
            assertEquals(Fs.list(array), Arrays.asList(array));
            assertEquals(Fs.arrayList(array), Arrays.asList(array));
            assertEquals(Fs.linkedList(array), Arrays.asList(array));
            assertEquals(Fs.set(array), new LinkedHashSet<>(Arrays.asList(array)));
            assertEquals(Fs.hashSet(array), new HashSet<>(Arrays.asList(array)));
            assertEquals(Fs.linkedHashSet(array), new LinkedHashSet<>(Arrays.asList(array)));
            Map<Integer, Integer> map = new LinkedHashMap<>();
            map.put(1, 2);
            map.put(3, 4);
            assertEquals(Fs.map(1, 2, 3, 4), map);
            assertEquals(Fs.hashMap(1, 2, 3, 4), new HashMap<>(map));
            assertEquals(Fs.linkedHashMap(1, 2, 3, 4), map);
            assertEquals(Fs.stream(1, 2, 3).collect(Collectors.toList()), Fs.list(1, 2, 3));
            assertEquals(Fs.stream(Fs.list(1, 2, 3)).collect(Collectors.toList()), Fs.list(1, 2, 3));
        }
        {
            // thread
            Fs.sleep(1);
            Fs.sleep(Duration.ofMillis(1));
            // Kit.sleep() will be tested in ThreadTest
        }
        {
            // process
            {
                Process process;
                if (OSKit.isWindows()) {
                    process = Fs.process("cmd.exe", "/c", "dir");
                } else {
                    process = Fs.process("ls", "-l");
                }
                printProcess("split cmd", process);
                process.destroyForcibly();
            }
            {
                Process process;
                if (OSKit.isWindows()) {
                    process = Fs.process("cmd.exe /c dir");
                } else {
                    process = Fs.process("ls -l");
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
        Fs.copyProperties(sp, ip1);
        assertEquals(new IntProps(1, 2, 3), ip1);
        IntProps ip2 = new IntProps();
        Fs.copyProperties(sp, sp.getClass(), ip2, ip2.getClass());
        assertEquals(new IntProps(1, 2, 3), ip2);
        IntProps ip3 = new IntProps();
        Fs.copyProperties(sp, sp.getClass(), ip3, ip3.getClass(), ObjectConverter.defaultConverter());
        assertEquals(new IntProps(1, 2, 3), ip3);
        IntProps ip4 = new IntProps();
        Fs.copyProperties(sp, ip4, ObjectConverter.defaultConverter());
        assertEquals(new IntProps(1, 2, 3), ip4);
    }

    @Test
    public void testObjectConversion() {
        StringProps sp = new StringProps("1", "2", "3");
        assertEquals(new IntProps(1, 2, 3), Fs.convert(sp, IntProps.class));
        assertEquals(new IntProps(1, 2, 3), Fs.convert(sp, sp.getClass(), IntProps.class));
        assertEquals(new IntProps(1, 2, 3), Fs.convert(sp, new TypeRef<IntProps>() {}));
        assertEquals(new IntProps(1, 2, 3), Fs.convert(sp, sp.getClass(), new TypeRef<IntProps>() {}));
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
