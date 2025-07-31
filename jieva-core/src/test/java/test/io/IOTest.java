package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IOMode;
import xyz.sunqian.common.io.IORuntimeCloseable;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.test.DataTest;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.expectThrows;

public class IOTest implements DataTest {

    @Test
    public void testReader() throws Exception {
        {
            // byte
            // read all
            byte[] data = randomBytes(1024);
            assertEquals(IOKit.read(new ByteArrayInputStream(data)), data);
            assertEquals(IOKit.read(new ByteArrayInputStream(data), 5), Arrays.copyOf(data, 5));
            assertEquals(
                IOKit.read(Channels.newChannel(new ByteArrayInputStream(data))),
                ByteBuffer.wrap(data)
            );
            assertEquals(
                IOKit.read(Channels.newChannel(new ByteArrayInputStream(data)), 5),
                ByteBuffer.wrap(Arrays.copyOf(data, 5))
            );
            // to stream/channel
            BytesBuilder builder = new BytesBuilder();
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), builder),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), Channels.newChannel(builder)),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), builder),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(builder)),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), builder, data.length),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), Channels.newChannel(builder), data.length),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), builder, data.length),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(builder), data.length),
                data.length
            );
            assertEquals(data, builder.toByteArray());
            builder.reset();
            // to array
            byte[] dst = new byte[data.length];
            assertEquals(IOKit.readTo(new ByteArrayInputStream(data), dst), data.length);
            assertEquals(data, dst);
            dst = new byte[data.length];
            assertEquals(IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst), data.length);
            assertEquals(data, dst);
            dst = new byte[data.length];
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst, 0, dst.length),
                data.length
            );
            assertEquals(data, dst);
            dst = new byte[data.length];
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 0, dst.length),
                data.length
            );
            assertEquals(data, dst);
            // to buffer
            ByteBuffer dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(new ByteArrayInputStream(data), dstBuf), data.length);
            assertEquals(data, dstBuf.array());
            dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dstBuf), data.length);
            assertEquals(data, dstBuf.array());
            dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(new ByteArrayInputStream(data), dstBuf, data.length), data.length);
            assertEquals(data, dstBuf.array());
            dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dstBuf, data.length),
                data.length
            );
            assertEquals(data, dstBuf.array());
        }
        {
            // char
            // read all
            char[] data = randomChars(1024);
            assertEquals(IOKit.read(new CharArrayReader(data)), data);
            assertEquals(IOKit.read(new CharArrayReader(data), 5), Arrays.copyOf(data, 5));
            assertEquals(IOKit.string(new CharArrayReader(data)), new String(data));
            assertEquals(IOKit.string(new CharArrayReader(data), 5), new String(Arrays.copyOf(data, 5)));
            // to appender
            CharsBuilder builder = new CharsBuilder();
            assertEquals(IOKit.readTo(new CharArrayReader(data), builder), data.length);
            assertEquals(data, builder.toCharArray());
            builder.reset();
            assertEquals(IOKit.readTo(new CharArrayReader(data), builder, data.length), data.length);
            assertEquals(data, builder.toCharArray());
            builder.reset();
            // to array
            char[] dst = new char[data.length];
            assertEquals(IOKit.readTo(new CharArrayReader(data), dst), data.length);
            assertEquals(data, dst);
            dst = new char[data.length];
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst, 0, dst.length),
                data.length
            );
            assertEquals(data, dst);
            // to buffer
            CharBuffer dstBuf = CharBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(new CharArrayReader(data), dstBuf), data.length);
            assertEquals(data, dstBuf.array());
            dstBuf = CharBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(new CharArrayReader(data), dstBuf, data.length), data.length);
            assertEquals(data, dstBuf.array());
        }
    }

    @Test
    public void testWrite() throws Exception {
        {
            // write to appender
            char[] data = randomChars(1024);
            CharsBuilder appender1 = new CharsBuilder();
            IOKit.write(appender1, data);
            assertEquals(appender1.toCharArray(), data);
            appender1.reset();
            IOKit.write(appender1, data, 33, 99);
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
            IOKit.write(appender2, data);
            assertEquals(appender2.toCharArray(), data);
            appender2.reset();
            IOKit.write(appender2, data, 33, 99);
            assertEquals(appender2.toCharArray(), Arrays.copyOfRange(data, 33, 33 + 99));
            expectThrows(IORuntimeException.class, () -> IOKit.write(new ErrorAppender(), data));
        }
        {
            // write to output stream
            String str = "hello world";
            ByteArrayOutputStream dst = new ByteArrayOutputStream();
            IOKit.write(dst, str);
            assertEquals(dst.toString("UTF-8"), str);
            OutputStream dst2 = IOKit.newOutputStream(new byte[1]);
            expectThrows(IORuntimeException.class, () -> IOKit.write(dst2, str));
        }
    }

    @Test
    public void testAvailable() throws Exception {
        byte[] data = randomBytes(10);
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
        assertEquals(IOKit.available(new In(5, 5)), Arrays.copyOf(data, 5));
        assertEquals(IOKit.available(new In(6, 5)), Arrays.copyOf(data, 5));
        assertEquals(IOKit.available(new In(-1, 5)), Arrays.copyOf(data, 0));
        assertEquals(IOKit.available(new In(0, 5)), Arrays.copyOf(data, 0));
        assertEquals(IOKit.available(new In(5, -5)), Arrays.copyOf(data, 0));
        TestInputStream tin = new TestInputStream(new ByteArrayInputStream(data));
        tin.setNextOperation(ReadOps.THROW);
        expectThrows(IORuntimeException.class, () -> IOKit.available(tin));
    }

    @Test
    public void testOthers() throws Exception {
        {
            // string
            String hello = "hello";
            assertEquals(IOKit.string(new ByteArrayInputStream(hello.getBytes(CharsKit.defaultCharset()))), hello);
            assertEquals(
                IOKit.string(new ByteArrayInputStream(hello.getBytes(CharsKit.defaultCharset())), CharsKit.defaultCharset()),
                hello
            );
            assertNull(IOKit.string(new ByteArrayInputStream(new byte[0])));
        }
        {
            // close
            IOKit.close((Closeable) () -> {
            });
            expectThrows(IOException.class, () -> {
                IOKit.close((Closeable) () -> {
                    throw new IOException();
                });
            });
            IOKit.close((AutoCloseable) () -> {
            });
            expectThrows(IOException.class, () -> {
                IOKit.close((AutoCloseable) () -> {
                    throw new IOException();
                });
            });
            expectThrows(IOException.class, () -> {
                IOKit.close((AutoCloseable) () -> {
                    throw new Exception();
                });
            });
            IOKit.close((IORuntimeCloseable) () -> {
            });
            expectThrows(IOException.class, () -> {
                IOKit.close((IORuntimeCloseable) () -> {
                    throw new IORuntimeException();
                });
            });
            IOKit.close("");
        }
        {
            // flush
            IOKit.flush((Flushable) () -> {
            });
            expectThrows(IOException.class, () -> {
                IOKit.flush((Flushable) () -> {
                    throw new IOException();
                });
            });
            IOKit.flush("");
        }
        {
            // mode
            assertEquals(IOMode.valueOf("BLOCKING"), IOMode.BLOCKING);
            assertEquals(IOMode.valueOf("NON_BLOCKING"), IOMode.NON_BLOCKING);
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
