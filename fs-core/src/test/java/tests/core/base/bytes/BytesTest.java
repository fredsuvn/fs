package tests.core.base.bytes;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.bytes.BytesKit;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BytesTest {

    @Test
    public void testIsEmpty() {
        // Test null input
        assertTrue(BytesKit.isEmpty(null));

        // Test empty byte buffers
        assertTrue(BytesKit.isEmpty(ByteBuffer.allocate(0)));
        assertTrue(BytesKit.isEmpty(ByteBuffer.allocateDirect(0)));
        assertTrue(BytesKit.isEmpty(ByteBuffer.wrap(new byte[10], 5, 0)));
        assertTrue(BytesKit.isEmpty(BytesKit.emptyBuffer()));
        assertTrue(BytesKit.isEmpty(ByteBuffer.wrap(BytesKit.empty())));

        // Test non-empty byte buffer
        assertFalse(BytesKit.isEmpty(ByteBuffer.allocate(1)));
    }

    @Test
    public void testBytesToInt() {
        // Test with 4 bytes
        assertEquals(0x01020304, BytesKit.bytesToInt(new byte[]{0x01, 0x02, 0x03, 0x04}));

        // Test with less than 4 bytes
        assertEquals(0x010203, BytesKit.bytesToInt(new byte[]{0x01, 0x02, 0x03}));
        assertEquals(0x01, BytesKit.bytesToInt(new byte[]{0x01}));

        // Test with more than 4 bytes
        assertEquals(0x02030405, BytesKit.bytesToInt(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05}));
    }

    @Test
    public void testIntToBytes() {
        assertArrayEquals(
            new byte[]{0x01, 0x02, 0x03, 0x04},
            BytesKit.intToBytes(0x01020304)
        );
    }

    @Test
    public void testBytesToLong() {
        // Test with 8 bytes
        assertEquals(0x0102030405060708L, BytesKit.bytesToLong(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}));

        // Test with less than 8 bytes
        assertEquals(0x01020304050607L, BytesKit.bytesToLong(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07}));
        assertEquals(0x01L, BytesKit.bytesToLong(new byte[]{0x01}));

        // Test with more than 8 bytes
        assertEquals(0x0203040506070809L, BytesKit.bytesToLong(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09}));
    }

    @Test
    public void testLongToBytes() {
        assertArrayEquals(
            new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08},
            BytesKit.longToBytes(0x0102030405060708L)
        );
    }
}
