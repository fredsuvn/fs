package test.io.communicate;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.communicate.IOChannelReader;
import xyz.sunqian.common.io.communicate.IOChannelWriter;
import xyz.sunqian.test.DataTest;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import static org.testng.Assert.assertEquals;

public class CommunicateTest implements DataTest {

    @Test
    public void testReader() throws Exception {
        char[] chars = randomChars(16, 'a', 'z');
        byte[] bytes = new String(chars).getBytes(CharsKit.defaultCharset());
        ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream(bytes));
        IOChannelReader reader = new IOChannelReader() {
            @Override
            public byte @Nullable [] nextBytes() throws IORuntimeException {
                return new byte[0];
            }

            @Override
            public @Nullable ByteBuffer nextBuffer() throws IORuntimeException {
                return null;
            }

            @Override
            public @Nullable String nextString(@Nonnull Charset charset) throws IORuntimeException {
                return IOKit.availableString(channel, charset);
            }

            @Override
            public int read(ByteBuffer dst) {
                return 0;
            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void close() {
            }
        };
        assertEquals(reader.nextString(), new String(chars));
    }

    @Test
    public void testWriter() throws Exception {
        BytesBuilder builder = new BytesBuilder();
        WritableByteChannel channel = Channels.newChannel(builder);
        IOChannelWriter writer = new IOChannelWriter() {

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void close() {
            }

            @Override
            public int write(ByteBuffer src) {
                return 0;
            }

            @Override
            public void writeBytes(byte @Nonnull [] src) throws IORuntimeException {
            }

            @Override
            public void writeBuffer(@Nonnull ByteBuffer src) throws IORuntimeException {
            }

            @Override
            public void writeString(@Nonnull String src, @Nonnull Charset charset) throws IORuntimeException {
                builder.append(src.getBytes(charset));
            }
        };
        writer.writeString("hello");
        assertEquals(builder.toByteArray(), "hello".getBytes(CharsKit.defaultCharset()));
    }
}
