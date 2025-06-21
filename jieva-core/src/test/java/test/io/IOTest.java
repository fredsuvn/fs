package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ErrorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class IOTest {

    @Test
    public void testReader() throws Exception {
        {
            // byte
            // read all
            byte[] data = JieRandom.fill(new byte[1024]);
            assertEquals(JieIO.read(new ByteArrayInputStream(data)), data);
            assertEquals(JieIO.read(new ByteArrayInputStream(data), 5), Arrays.copyOf(data, 5));
            assertEquals(
                JieIO.read(Channels.newChannel(new ByteArrayInputStream(data))),
                ByteBuffer.wrap(data)
            );
            assertEquals(
                JieIO.read(Channels.newChannel(new ByteArrayInputStream(data)), 5),
                ByteBuffer.wrap(Arrays.copyOf(data, 5))
            );
            // to stream
            BytesBuilder builder = new BytesBuilder();
            assertEquals(JieIO.readTo(new ByteArrayInputStream(data), builder), data.length);
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(builder)),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(JieIO.readTo(new ByteArrayInputStream(data), builder, data.length), data.length);
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(builder), data.length),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            // to array
            byte[] dst = new byte[data.length];
            assertEquals(JieIO.readTo(new ByteArrayInputStream(data), dst), data.length);
            assertEquals(data, dst);
            dst = new byte[data.length];
            assertEquals(JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst), data.length);
            assertEquals(data, dst);
            dst = new byte[data.length];
            assertEquals(
                JieIO.readTo(new ByteArrayInputStream(data), dst, 0, dst.length),
                data.length
            );
            assertEquals(data, dst);
            dst = new byte[data.length];
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 0, dst.length),
                data.length
            );
            assertEquals(data, dst);
            // to buffer
            ByteBuffer dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(JieIO.readTo(new ByteArrayInputStream(data), dstBuf), data.length);
            assertEquals(data, dstBuf.array());
            dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dstBuf), data.length);
            assertEquals(data, dstBuf.array());
            dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(JieIO.readTo(new ByteArrayInputStream(data), dstBuf, data.length), data.length);
            assertEquals(data, dstBuf.array());
            dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(
                JieIO.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dstBuf, data.length),
                data.length
            );
            assertEquals(data, dstBuf.array());
        }
        {
            // char
            // read all
            char[] data = JieRandom.fill(new char[1024]);
            assertEquals(JieIO.read(new CharArrayReader(data)), data);
            assertEquals(JieIO.read(new CharArrayReader(data), 5), Arrays.copyOf(data, 5));
            assertEquals(JieIO.string(new CharArrayReader(data)), new String(data));
            assertEquals(JieIO.string(new CharArrayReader(data), 5), new String(Arrays.copyOf(data, 5)));
            // to appender
            CharsBuilder builder = new CharsBuilder();
            assertEquals(JieIO.readTo(new CharArrayReader(data), builder), data.length);
            assertEquals(data, builder.toCharArray());
            builder.reset();
            assertEquals(JieIO.readTo(new CharArrayReader(data), builder, data.length), data.length);
            assertEquals(data, builder.toCharArray());
            // to array
            char[] dst = new char[data.length];
            assertEquals(JieIO.readTo(new CharArrayReader(data), dst), data.length);
            assertEquals(data, dst);
            dst = new char[data.length];
            assertEquals(
                JieIO.readTo(new CharArrayReader(data), dst, 0, dst.length),
                data.length
            );
            assertEquals(data, dst);
            // to buffer
            CharBuffer dstBuf = CharBuffer.allocate(data.length);
            assertEquals(JieIO.readTo(new CharArrayReader(data), dstBuf), data.length);
            assertEquals(data, dstBuf.array());
            dstBuf = CharBuffer.allocate(data.length);
            assertEquals(JieIO.readTo(new CharArrayReader(data), dstBuf, data.length), data.length);
            assertEquals(data, dstBuf.array());
        }
    }

    @Test
    public void testWrite() throws Exception {
        {
            // channel
            byte[] data = JieRandom.fill(new byte[1024]);
            BytesBuilder out = new BytesBuilder();
            JieIO.write(Channels.newChannel(out), ByteBuffer.wrap(data));
            assertEquals(out.toByteArray(), data);
            expectThrows(IORuntimeException.class, () ->
                JieIO.write(Channels.newChannel(new ErrorOutputStream()), ByteBuffer.wrap(data)));
        }
        {
            // appender
            char[] data = JieRandom.fill(new char[1024]);
            CharsBuilder appender1 = new CharsBuilder();
            JieIO.write(appender1, data);
            assertEquals(appender1.toCharArray(), data);
            appender1.reset();
            JieIO.write(appender1, data, 33, 99);
            assertEquals(appender1.toCharArray(), Arrays.copyOfRange(data, 33, 33 + 99));
            class Appender implements Appendable {

                private final CharsBuilder appender = new CharsBuilder();

                @Override
                public Appendable append(CharSequence csq) throws IOException {
                    return appender.append(csq);
                }

                @Override
                public Appendable append(CharSequence csq, int start, int end) throws IOException {
                    return appender.append(csq, start, end);
                }

                @Override
                public Appendable append(char c) throws IOException {
                    return appender.append(c);
                }

                public char[] toCharArray() {
                    return appender.toCharArray();
                }

                public void reset() {
                    appender.reset();
                }
            }
            Appender appender2 = new Appender();
            JieIO.write(appender2, data);
            assertEquals(appender2.toCharArray(), data);
            appender2.reset();
            JieIO.write(appender2, data, 33, 99);
            assertEquals(appender2.toCharArray(), Arrays.copyOfRange(data, 33, 33 + 99));
            expectThrows(IORuntimeException.class, () -> JieIO.write(new ErrorAppender(), data));
        }
        // {
        //     // write buffer
        //     char[] data = JieRandom.fill(new char[256]);
        //     CharsBuilder builder = new CharsBuilder();
        //     CharBuffer buffer = CharBuffer.wrap(data, 5, 100);
        //     assertEquals(buffer.position(), 5);
        //     assertEquals(buffer.limit(), 5 + 100);
        //     JieIO.write(builder, buffer);
        //     assertEquals(builder.toCharArray(), Arrays.copyOfRange(data, 5, 5 + 100));
        //     assertEquals(buffer.position(), 5 + 100);
        //     assertEquals(buffer.limit(), 5 + 100);
        // }
    }

    @Test
    public void testIORuntimeException() throws Exception {
        expectThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException();
        });
        expectThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException("");
        });
        expectThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException("", new RuntimeException());
        });
        expectThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException(new RuntimeException());
        });
        expectThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException(new IOException());
        });
    }
}
