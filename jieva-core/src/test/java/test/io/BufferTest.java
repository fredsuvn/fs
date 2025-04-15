package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.io.JieBuffer;

import java.nio.ByteBuffer;

import static org.testng.Assert.assertEquals;
import static xyz.sunqian.test.MaterialBox.paddedBuffer;

public class BufferTest {

    @Test
    public void tesIndex() {
        byte[] bytes = new byte[100];
        ByteBuffer buffer = paddedBuffer(bytes);
        buffer.get();
        assertEquals(buffer.arrayOffset(), 10);
        assertEquals(buffer.position(), 1);
        assertEquals(buffer.remaining(), 99);
        assertEquals(JieBuffer.arrayStartIndex(buffer), 10 + 1);
        assertEquals(JieBuffer.arrayEndIndex(buffer), 10 + 1 + 99);
    }
}
