package test;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.JieRandom;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import static org.testng.Assert.assertEquals;

public class TU {

    /**
     * Return a dangling buffer. A dangling buffer is one whose arrayOffset is not zero.
     */
    public static ByteBuffer bufferDangling(byte[] data) {
        byte[] bytes = new byte[data.length + 20];
        System.arraycopy(data, 0, bytes, 10, data.length);
        return ByteBuffer.wrap(bytes, 10, data.length).slice();
    }

    public static ByteBuffer bufferDirect(byte[] data) {
        return JieBytes.copyBuffer(data, true);
    }

    public static ByteBuffer buffer(byte[] data) {
        return JieBytes.copyBuffer(data);
    }

    /**
     * Return a dangling buffer. A dangling buffer is one whose arrayOffset is not zero.
     */
    public static CharBuffer bufferDangling(char[] data) {
        char[] bytes = new char[data.length + 20];
        System.arraycopy(data, 0, bytes, 10, data.length);
        return CharBuffer.wrap(bytes, 10, data.length).slice();
    }

    public static CharBuffer bufferDirect(char[] data) {
        return JieChars.copyBuffer(data, true);
    }

    public static CharBuffer buffer(char[] data) {
        return JieChars.copyBuffer(data);
    }

    @Test
    public void testSelf() {
        byte[] bytes = JieRandom.fill(new byte[1111]);
        ByteBuffer bb = bufferDangling(bytes);
        assertEquals(bb.arrayOffset(), 10);
        assertEquals(bb.position(), 0);
        assertEquals(bb.limit(), bytes.length);
        assertEquals(bb.capacity(), bytes.length);
        char[] chars = JieRandom.fill(new char[1111]);
        CharBuffer cb = bufferDangling(chars);
        assertEquals(cb.arrayOffset(), 10);
        assertEquals(cb.position(), 0);
        assertEquals(cb.limit(), chars.length);
        assertEquals(cb.capacity(), chars.length);
    }
}
