package tests.io.communicate;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.value.BooleanVar;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.communicate.IOChannel;
import xyz.sunqian.test.DataTest;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class CommunicateTest implements DataTest {

    @Test
    public void testChannel() throws Exception {
        byte[] data = "hello world".getBytes(CharsKit.defaultCharset());
        ByteBuffer reader = ByteBuffer.wrap(data);
        BytesBuilder writer = new BytesBuilder();
        {
            // test end-able
            BooleanVar open = BooleanVar.of(true);
            ByteChannel bc = new ByteChannel() {

                @Override
                public int read(ByteBuffer dst) {
                    return BufferKit.readTo(reader, dst);
                }

                @Override
                public int write(ByteBuffer src) {
                    int remaining = src.remaining();
                    writer.append(src);
                    return remaining;
                }

                @Override
                public boolean isOpen() {
                    return open.get();
                }

                @Override
                public void close() {
                    open.set(false);
                }
            };
            IOChannel ic = IOChannel.newChannel(bc);
            assertTrue(ic.isOpen());
            assertEquals(ic.availableString(), "hello world");
            assertFalse(ic.isOpen());
            assertNull(ic.availableString());
            reader.clear();
            open.set(true);
            assertEquals(ic.availableBytes(), data);
            assertNull(ic.availableBytes());
            reader.clear();
            open.set(true);
            assertEquals(ic.availableBuffer(), ByteBuffer.wrap(data));
            assertNull(ic.availableBytes());
            open.set(true);
            ic.writeString("hello world");
            assertEquals(writer.toString(), "hello world");
            open.set(false);
            assertNull(ic.availableString());
            expectThrows(IORuntimeException.class, () -> ic.writeString("hello world"));
            open.set(true);
            assertTrue(ic.isOpen());
            ic.close();
            assertFalse(ic.isOpen());
        }
        {
            // test available
            ByteChannel bc = new ByteChannel() {
                @Override
                public int read(ByteBuffer dst) {
                    return 0;
                }

                @Override
                public int write(ByteBuffer src) {
                    return 0;
                }

                @Override
                public boolean isOpen() {
                    return true;
                }

                @Override
                public void close() {
                }
            };
            IOChannel ic = IOChannel.newChannel(bc);
            assertEquals(ic.availableString(), "");
            assertEquals(ic.availableBytes(), new byte[0]);
            assertEquals(ic.availableBuffer(), ByteBuffer.allocate(0));
        }
    }
}
