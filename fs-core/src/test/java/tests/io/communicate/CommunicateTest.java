package tests.io.communicate;

import internal.test.DataTest;
import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.bytes.BytesBuilder;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.io.BufferKit;
import space.sunqian.common.io.IOOperator;
import space.sunqian.common.io.IORuntimeException;
import space.sunqian.common.io.communicate.AbstractChannelContext;
import space.sunqian.common.io.communicate.ChannelContext;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommunicateTest implements DataTest {

    @Test
    public void testChannel() throws Exception {
        byte[] data = "hello world".getBytes(CharsKit.defaultCharset());
        ByteBuffer reader = ByteBuffer.wrap(data);
        BytesBuilder writer = new BytesBuilder();
        class ByteChannelImpl implements ByteChannel {

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
        }
        {
            // interface
            ByteChannel bc = new ByteChannelImpl();
            ChannelContext<ByteChannel> ic = new ChannelContext<ByteChannel>() {

                private Object attachment;

                @Override
                public @Nonnull ByteChannel channel() {
                    return bc;
                }

                @Override
                public void attach(Object attachment) {
                    this.attachment = attachment;
                }

                @Override
                public Object attachment() {
                    return attachment;
                }
            };
            testChannelContext(ic, reader, writer, data);
        }
        {
            // abstract
            ByteChannel bc = new ByteChannelImpl();
            IOOperator ioOperator = IOOperator.defaultOperator();
            ChannelContext<ByteChannel> ic = new AbstractChannelContext<ByteChannel>(bc) {

                @Override
                protected @Nonnull IOOperator ioOperator() {
                    return ioOperator;
                }
            };
            testChannelContext(ic, reader, writer, data);
        }
    }

    private void testChannelContext(
        ChannelContext<ByteChannel> ic, ByteBuffer reader, BytesBuilder writer, byte[] data
    ) throws Exception {
        reader.clear();
        writer.reset();
        Object attachment = new Object();
        ic.attach(attachment);
        assertTrue(ic.channel().isOpen());
        assertEquals("hello world", ic.availableString());
        assertTrue(ic.channel().isOpen());
        assertNull(ic.availableString(CharsKit.defaultCharset()));
        reader.clear();
        assertArrayEquals(ic.availableBytes(), data);
        assertNull(ic.availableBytes());
        reader.clear();
        assertArrayEquals(ic.availableBytes(), data);
        assertNull(ic.availableBytes());
        ic.writeString("hello world");
        assertEquals("hello world", writer.toString());
        writer.reset();
        ic.writeString("hello world", CharsKit.defaultCharset());
        assertEquals("hello world", writer.toString());
        writer.reset();
        ic.writeBytes(data);
        assertEquals("hello world", writer.toString());
        writer.reset();
        ic.writeBuffer(ByteBuffer.wrap(data));
        assertEquals("hello world", writer.toString());
        writer.reset();
        assertNull(ic.availableString());
        assertTrue(ic.channel().isOpen());
        ic.channel().close();
        assertFalse(ic.channel().isOpen());
        assertThrows(IORuntimeException.class, () -> ic.writeString("hello world"));
        assertNull(ic.availableString());
        assertSame(ic.attachment(), attachment);
    }
}
