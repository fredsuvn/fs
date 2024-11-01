package test.base;

import org.testng.annotations.Test;
import xyz.fslabo.common.base.*;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.testng.Assert.*;

public class BaseTest {

    @Test
    public void testConvenient() {
        assertEquals(Jie.array(1, 2, 3), new Integer[]{1, 2, 3});
        assertEquals(Jie.list(1, 2, 3), Arrays.asList(1, 2, 3));
        assertEquals(Jie.list(1, 2, 3).get(1), 2);
        expectThrows(UnsupportedOperationException.class, () -> Jie.list(1, 2, 3).set(1, 2));
        Integer[] is = new Integer[]{1, 2, 3};
        List<Integer> list = Jie.list(is);
        assertEquals(list, Arrays.asList(1, 2, 3));
        is[1] = 888;
        assertEquals(list, Arrays.asList(1, 888, 3));
        assertEquals(Jie.set(1, 2, 3), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        assertEquals(Jie.set(1, 2, 3, 3, 2), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Map<Integer, Integer> map2 = new LinkedHashMap<>(map);
        map2.put(3, 4);
        assertEquals(Jie.map(1, 2, 3), map);
        assertEquals(Jie.map(1, 2, 3, 4), map2);
    }

    @Test
    public void testTuple() {
        Tuple tuple = Tuple.of(0, "1", 2L);
        assertEquals(tuple.size(), 3);
        assertEquals(tuple.set(0, 0), tuple);
        assertEquals((Integer) tuple.get(0), 0);
        assertEquals(tuple.get(1), "1");
        assertEquals((Long) tuple.get(2), 2L);
        assertEquals(tuple, Tuple.of(0, "1", 2L));
        assertNotEquals(tuple, Tuple.of(0, "1", 22L));
        assertNotSame(tuple, Tuple.of(0, "1", 2L));
        tuple.set(1, "11");
        assertEquals(tuple.get(1), "11");
        assertEquals(tuple, Tuple.of(0, "11", 2L));
        tuple.set(1, new int[]{1, 2});
        tuple.set(2, Jie.arrayList("22", "33"));
        assertEquals(tuple, Tuple.of(0, new int[]{1, 2}, Jie.arrayList("22", "33")));
        assertEquals(tuple.hashCode(), Tuple.of(0, new int[]{1, 2}, Jie.arrayList("22", "33")).hashCode());
        assertEquals(Tuple.of(0, "1", 2L).toString(), Arrays.deepToString(new Object[]{0, "1", 2L}));
        assertFalse(tuple.equals(""));
        assertFalse(tuple.equals(null));
    }

    @Test
    public void testBytes() {
        {
            // bytes and buffer
            assertEquals(JieBytes.emptyBytes(), new byte[0]);
            assertEquals(JieBytes.emptyBuffer(), ByteBuffer.allocate(0));
            byte[] bytes = JieRandom.fill(new byte[10086]);
            ByteBuffer buffer = JieBytes.copyBuffer(bytes);
            assertFalse(buffer.isDirect());
            assertEquals(buffer.remaining(), bytes.length);
            ByteBuffer bufferDir = JieBytes.copyBuffer(bytes, true);
            assertTrue(bufferDir.isDirect());
            assertEquals(bufferDir.remaining(), bytes.length);
            assertEquals(buffer, bufferDir);
            ByteBuffer buffer2 = JieBytes.copyBuffer(buffer);
            assertFalse(buffer.isDirect());
            assertEquals(buffer.remaining(), bytes.length);
            ByteBuffer bufferDir2 = JieBytes.copyBuffer(bufferDir);
            assertTrue(bufferDir.isDirect());
            assertEquals(bufferDir.remaining(), bytes.length);
            assertEquals(buffer, buffer2);
            assertNotSame(buffer, buffer2);
            assertEquals(bufferDir, bufferDir2);
            assertNotSame(bufferDir, bufferDir2);
            assertEquals(buffer2, bufferDir2);
            assertNotSame(buffer2, bufferDir2);
            assertEquals(JieBytes.copyBytes(buffer), bytes);
            assertNotSame(JieBytes.copyBytes(buffer), bytes);
            assertEquals(JieBytes.copyBytes(buffer2), bytes);
            assertNotSame(JieBytes.copyBytes(buffer2), bytes);
            assertEquals(JieBytes.copyBytes(buffer), JieBytes.copyBytes(buffer2));
            assertNotSame(JieBytes.copyBytes(buffer), JieBytes.copyBytes(buffer2));
            assertEquals(JieBytes.copyBytes(bufferDir), bytes);
            assertNotSame(JieBytes.copyBytes(bufferDir), bytes);
            assertEquals(JieBytes.copyBytes(bufferDir2), bytes);
            assertNotSame(JieBytes.copyBytes(bufferDir2), bytes);
            assertEquals(JieBytes.copyBytes(bufferDir), JieBytes.copyBytes(bufferDir2));
            assertNotSame(JieBytes.copyBytes(bufferDir), JieBytes.copyBytes(bufferDir2));
            assertEquals(JieBytes.copyBytes(buffer), JieBytes.copyBytes(bufferDir));
            assertNotSame(JieBytes.copyBytes(buffer), JieBytes.copyBytes(bufferDir));
        }
    }

    @Test
    public void testChars() throws Exception {
        {
            // chars and buffer
            assertEquals(JieChars.emptyChars(), new char[0]);
            assertEquals(JieChars.emptyBuffer(), CharBuffer.allocate(0));
            char[] chars = JieRandom.fill(new char[10086]);
            CharBuffer buffer = JieChars.copyBuffer(chars);
            assertFalse(buffer.isDirect());
            assertEquals(buffer.remaining(), chars.length);
            CharBuffer bufferDir = JieChars.copyBuffer(chars, true);
            assertTrue(bufferDir.isDirect());
            assertEquals(bufferDir.remaining(), chars.length);
            assertEquals(buffer, bufferDir);
            CharBuffer buffer2 = JieChars.copyBuffer(buffer);
            assertFalse(buffer.isDirect());
            assertEquals(buffer.remaining(), chars.length);
            CharBuffer bufferDir2 = JieChars.copyBuffer(bufferDir);
            assertTrue(bufferDir.isDirect());
            assertEquals(bufferDir.remaining(), chars.length);
            assertEquals(buffer, buffer2);
            assertNotSame(buffer, buffer2);
            assertEquals(bufferDir, bufferDir2);
            assertNotSame(bufferDir, bufferDir2);
            assertEquals(buffer2, bufferDir2);
            assertNotSame(buffer2, bufferDir2);
            assertEquals(JieChars.copyChars(buffer), chars);
            assertNotSame(JieChars.copyChars(buffer), chars);
            assertEquals(JieChars.copyChars(buffer2), chars);
            assertNotSame(JieChars.copyChars(buffer2), chars);
            assertEquals(JieChars.copyChars(buffer), JieChars.copyChars(buffer2));
            assertNotSame(JieChars.copyChars(buffer), JieChars.copyChars(buffer2));
            assertEquals(JieChars.copyChars(bufferDir), chars);
            assertNotSame(JieChars.copyChars(bufferDir), chars);
            assertEquals(JieChars.copyChars(bufferDir2), chars);
            assertNotSame(JieChars.copyChars(bufferDir2), chars);
            assertEquals(JieChars.copyChars(bufferDir), JieChars.copyChars(bufferDir2));
            assertNotSame(JieChars.copyChars(bufferDir), JieChars.copyChars(bufferDir2));
            assertEquals(JieChars.copyChars(buffer), JieChars.copyChars(bufferDir));
            assertNotSame(JieChars.copyChars(buffer), JieChars.copyChars(bufferDir));
        }

        {
            // charset
            assertEquals(JieChars.defaultCharset(), StandardCharsets.UTF_8);
            assertEquals(JieChars.latinCharset(), StandardCharsets.ISO_8859_1);
            assertEquals(JieChars.jvmCharset(), JieChars.defaultCharset());
            assertEquals(JieChars.charset(StandardCharsets.UTF_8.name()), StandardCharsets.UTF_8);
            assertNull(JieChars.charset(null));
            JieChars.nativeCharset();
            String nativesClassName = JieChars.class.getName() + "$Natives";
            Class<?> nativesClass = Class.forName(nativesClassName);
            Method search = nativesClass.getDeclaredMethod("search", String[].class);
            search.setAccessible(true);
            String[] args = {new String(JieRandom.fill(new char[1024])), JieSystem.KEY_OF_FILE_ENCODING};
            search.invoke(null, (Object) args);
            String[] args2 = {new String(JieRandom.fill(new char[1024]))};
            assertNull(search.invoke(null, (Object) args2));
        }
    }
}
