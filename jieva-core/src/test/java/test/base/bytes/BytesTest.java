package test.base.bytes;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.JieBytes;

import java.nio.ByteBuffer;

import static org.testng.Assert.assertTrue;

public class BytesTest {

    @Test
    public void testBytes() {
        assertTrue(JieBytes.isEmpty(null));
        assertTrue(JieBytes.isEmpty(ByteBuffer.allocate(0)));
        assertTrue(JieBytes.isEmpty(ByteBuffer.allocateDirect(0)));
        assertTrue(JieBytes.isEmpty(ByteBuffer.wrap(new byte[10], 5, 0)));
        assertTrue(JieBytes.isEmpty(JieBytes.emptyBuffer()));
        assertTrue(JieBytes.isEmpty(ByteBuffer.wrap(JieBytes.emptyBytes())));
    }
}
