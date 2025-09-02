package tests.io.communicate;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.communicate.AbstractChannelContext;
import xyz.sunqian.common.io.communicate.ChannelContext;
import xyz.sunqian.test.DataTest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class CommunicateTest implements DataTest {

    @Test
    public void testChannel() throws Exception {
        byte[] data = "hello world".getBytes(CharsKit.defaultCharset());
        ByteBuffer reader = ByteBuffer.wrap(data);
        BytesBuilder writer = new BytesBuilder();
        ByteChannel bc = new ByteChannel() {

            private boolean closed = false;

            @Override
            public int read(ByteBuffer dst) {
                return BufferKit.readTo(reader, dst);
            }

            @Override
            public int write(ByteBuffer src) throws IOException {
                if (closed) {
                    throw new ClosedChannelException();
                }
                int remaining = src.remaining();
                writer.append(src);
                return remaining;
            }

            @Override
            public boolean isOpen() {
                return !closed;
            }

            @Override
            public void close() {
                closed = true;
            }
        };
        ChannelContext<ByteChannel> ic = new AbstractChannelContext<ByteChannel>(bc, 1024) {

            private Object attachment;

            @Override
            public void attach(Object attachment) {
                this.attachment = attachment;
            }

            @Override
            public Object attachment() {
                return attachment;
            }
        };
        Object attachment = new Object();
        ic.attach(attachment);
        assertTrue(ic.channel().isOpen());
        assertEquals(ic.availableString(), "hello world");
        assertTrue(ic.channel().isOpen());
        assertNull(ic.availableString());
        reader.clear();
        assertEquals(ic.availableBytes(), data);
        assertNull(ic.availableBytes());
        reader.clear();
        assertEquals(ic.availableBuffer(), ByteBuffer.wrap(data));
        assertNull(ic.availableBytes());
        ic.writeString("hello world");
        assertEquals(writer.toString(), "hello world");
        assertNull(ic.availableString());
        assertTrue(ic.channel().isOpen());
        ic.channel().close();
        assertFalse(ic.channel().isOpen());
        expectThrows(IORuntimeException.class, () -> ic.writeString("hello world"));
        assertNull(ic.availableString());
        assertSame(ic.attachment(), attachment);
    }
}
