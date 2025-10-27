package tests.base.bytes;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.bytes.BytesKit;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BytesTest {

    @Test
    public void testEmpty() {
        assertTrue(BytesKit.isEmpty(null));
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
            assertEquals(BytesKit.bytesToInt(new byte[]{0x01, 0x02, 0x03, 0x04}), 0x01020304);
            assertEquals(BytesKit.bytesToInt(new byte[]{0x01, 0x02, 0x03}), 0x010203);
            assertEquals(BytesKit.bytesToInt(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05}), 0x02030405);
            assertEquals(BytesKit.bytesToInt(new byte[]{0x01}), 0x01);
            assertArrayEquals(
                BytesKit.intToBytes(0x01020304),
                new byte[]{0x01, 0x02, 0x03, 0x04}
            );
        }
        {
            // long
            assertEquals(BytesKit.bytesToLong(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}), 0x0102030405060708L);
            assertEquals(BytesKit.bytesToLong(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07}), 0x01020304050607L);
            assertEquals(BytesKit.bytesToLong(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09}), 0x0203040506070809L);
            assertEquals(BytesKit.bytesToLong(new byte[]{0x01}), 0x01L);
            assertArrayEquals(
                BytesKit.longToBytes(0x0102030405060708L),
                new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}
            );
        }
    }
}
