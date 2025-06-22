package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
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
            builder.reset();
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
            // read buffer
            assertEquals(
                JieIO.readTo(ByteBuffer.wrap(data), builder),
                data.length
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                JieIO.readTo(ByteBuffer.wrap(data), builder, 5),
                5
            );
            assertEquals(builder.toByteArray(), Arrays.copyOf(data, 5));
            builder.reset();
            assertEquals(
                JieIO.readTo(ByteBuffer.wrap(data), Channels.newChannel(builder)),
                data.length
            );
            assertEquals(builder.toByteArray(), data);
            builder.reset();
            assertEquals(
                JieIO.readTo(ByteBuffer.wrap(data), Channels.newChannel(builder), 5),
                5
            );
            assertEquals(builder.toByteArray(), Arrays.copyOf(data, 5));
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
            builder.reset();
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
            // read buffer
            assertEquals(
                JieIO.readTo(CharBuffer.wrap(data), builder),
                data.length
            );
            assertEquals(builder.toCharArray(), data);
            builder.reset();
            assertEquals(
                JieIO.readTo(CharBuffer.wrap(data), builder, 5),
                5
            );
            assertEquals(builder.toCharArray(), Arrays.copyOf(data, 5));
        }
    }

    @Test
    public void testWrite() throws Exception {
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

    @Test
    public void testAvailable() throws Exception {
        byte[] data = JieRandom.fill(new byte[10]);
        class In extends InputStream {

            private final int avai;
            private final int read;

            In(int avai, int read) {
                this.avai = avai;
                this.read = read;
            }

            @Override
            public int available() throws IOException {
                return avai;
            }

            @Override
            public int read() throws IOException {
                return -1;
            }

            @Override
            public int read(@NotNull byte[] b, int off, int len) throws IOException {
                if (read < 0) {
                    return read;
                }
                System.arraycopy(data, 0, b, off, len);
                return read;
            }
        }
        assertEquals(JieIO.available(new In(5, 5)), Arrays.copyOf(data, 5));
        assertEquals(JieIO.available(new In(6, 5)), Arrays.copyOf(data, 5));
        assertEquals(JieIO.available(new In(-1, 5)), Arrays.copyOf(data, 0));
        assertEquals(JieIO.available(new In(0, 5)), Arrays.copyOf(data, 0));
        assertEquals(JieIO.available(new In(5, -5)), Arrays.copyOf(data, 0));
        TestInputStream tin = new TestInputStream(new ByteArrayInputStream(data));
        tin.setNextOperation(ReadOps.THROW);
        expectThrows(IORuntimeException.class, () -> JieIO.available(tin));
    }

    @Test
    public void testOthers() throws Exception {
        {
            // string
            String hello = "hello";
            assertEquals(JieIO.string(new ByteArrayInputStream(hello.getBytes(JieChars.defaultCharset()))), hello);
            assertEquals(
                JieIO.string(new ByteArrayInputStream(hello.getBytes(JieChars.defaultCharset())), JieChars.defaultCharset()),
                hello
            );
            assertNull(JieIO.string(new ByteArrayInputStream(new byte[0])));
        }
        {
            // close
            JieIO.close((Closeable) () -> {});
            expectThrows(IOException.class, () -> {
                JieIO.close((Closeable) () -> {
                    throw new IOException();
                });
            });
            JieIO.close((AutoCloseable) () -> {});
            expectThrows(IOException.class, () -> {
                JieIO.close((AutoCloseable) () -> {
                    throw new IOException();
                });
            });
            expectThrows(IOException.class, () -> {
                JieIO.close((AutoCloseable) () -> {
                    throw new Exception();
                });
            });
            JieIO.close("");
        }
        {
            // flush
            JieIO.flush((Flushable) () -> {});
            expectThrows(IOException.class, () -> {
                JieIO.flush((Flushable) () -> {
                    throw new IOException();
                });
            });
            JieIO.flush("");
        }
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
