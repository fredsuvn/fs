package tests.base.bytes;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.bytes.BytesKit;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BytesTest {

    @Test
    public void testEmpty() {
        assertTrue(BytesKit.isEmpty(null));
        assertTrue(BytesKit.isEmpty(ByteBuffer.allocate(0)));
        assertFalse(BytesKit.isEmpty(ByteBuffer.allocate(1)));
        assertTrue(BytesKit.isEmpty(ByteBuffer.allocate(0)));
        assertTrue(BytesKit.isEmpty(ByteBuffer.allocateDirect(0)));
        assertTrue(BytesKit.isEmpty(ByteBuffer.wrap(new byte[10], 5, 0)));
        assertTrue(BytesKit.isEmpty(BytesKit.emptyBuffer()));
        assertTrue(BytesKit.isEmpty(ByteBuffer.wrap(BytesKit.empty())));
    }

    @Test
    public void testBytesToPrimitives() {
        {
            // int
            assertEquals(0x01020304, BytesKit.bytesToInt(new byte[]{0x01, 0x02, 0x03, 0x04}));
            assertEquals(0x010203, BytesKit.bytesToInt(new byte[]{0x01, 0x02, 0x03}));
            assertEquals(0x02030405, BytesKit.bytesToInt(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05}));
            assertEquals(0x01, BytesKit.bytesToInt(new byte[]{0x01}));
            assertArrayEquals(
                new byte[]{0x01, 0x02, 0x03, 0x04},
                BytesKit.intToBytes(0x01020304)
            );
        }
        {
            // long
            assertEquals(0x0102030405060708L, BytesKit.bytesToLong(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}));
            assertEquals(0x01020304050607L, BytesKit.bytesToLong(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07}));
            assertEquals(0x0203040506070809L, BytesKit.bytesToLong(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09}));
            assertEquals(0x01L, BytesKit.bytesToLong(new byte[]{0x01}));
            assertArrayEquals(
                new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08},
                BytesKit.longToBytes(0x0102030405060708L)
            );
        }
    }
}
