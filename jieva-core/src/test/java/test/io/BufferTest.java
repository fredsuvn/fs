package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.test.MaterialBox;

import java.nio.ByteBuffer;

import static org.testng.Assert.assertEquals;

public class BufferTest {

    @Test
    public void tesIndex() {
        byte[] bytes = new byte[100];
        ByteBuffer buffer = MaterialBox.copyPadding(bytes);
        buffer.get();
        assertEquals(buffer.arrayOffset(), 10);
        assertEquals(buffer.position(), 1);
        assertEquals(buffer.remaining(), 99);
        assertEquals(JieBuffer.arrayStartIndex(buffer), 10 + 1);
        assertEquals(JieBuffer.arrayEndIndex(buffer), 10 + 1 + 99);
    }
}
