package tests.io;

import internal.test.DataTest;
import internal.test.ErrorAppender;
import internal.test.ReadOps;
import internal.test.TestInputStream;
import org.junit.jupiter.api.Test;
import space.sunqian.common.base.bytes.BytesBuilder;
import space.sunqian.common.base.chars.CharsBuilder;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.io.IOMode;
import space.sunqian.common.io.IOOperator;
import space.sunqian.common.io.IORuntimeException;

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
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IOTest implements DataTest {

    @Test
    public void testReader() throws Exception {
        {
            // byte
            // read all
            byte[] data = randomBytes(1024);
            assertArrayEquals(IOKit.read(new ByteArrayInputStream(data)), data);
            assertArrayEquals(IOKit.read(new ByteArrayInputStream(data), 5), Arrays.copyOf(data, 5));
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
            assertArrayEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), Channels.newChannel(builder)),
                data.length
            );
            assertArrayEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), builder),
                data.length
            );
            assertArrayEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(builder)),
                data.length
            );
            assertArrayEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), builder, data.length),
                data.length
            );
            assertArrayEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), Channels.newChannel(builder), data.length),
                data.length
            );
            assertArrayEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), builder, data.length),
                data.length
            );
            assertArrayEquals(data, builder.toByteArray());
            builder.reset();
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), Channels.newChannel(builder), data.length),
                data.length
            );
            assertArrayEquals(data, builder.toByteArray());
            builder.reset();
            // to array
            byte[] dst = new byte[data.length];
            assertEquals(IOKit.readTo(new ByteArrayInputStream(data), dst), data.length);
            assertArrayEquals(data, dst);
            dst = new byte[data.length];
            assertEquals(IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst), data.length);
            assertArrayEquals(data, dst);
            dst = new byte[data.length];
            assertEquals(
                IOKit.readTo(new ByteArrayInputStream(data), dst, 0, dst.length),
                data.length
            );
            assertArrayEquals(data, dst);
            dst = new byte[data.length];
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dst, 0, dst.length),
                data.length
            );
            assertArrayEquals(data, dst);
            // to buffer
            ByteBuffer dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(new ByteArrayInputStream(data), dstBuf), data.length);
            assertArrayEquals(data, dstBuf.array());
            dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dstBuf), data.length);
            assertArrayEquals(data, dstBuf.array());
            dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(new ByteArrayInputStream(data), dstBuf, data.length), data.length);
            assertArrayEquals(data, dstBuf.array());
            dstBuf = ByteBuffer.allocate(data.length);
            assertEquals(
                IOKit.readTo(Channels.newChannel(new ByteArrayInputStream(data)), dstBuf, data.length),
                data.length
            );
            assertArrayEquals(data, dstBuf.array());
        }
        {
            // char
            // read all
            char[] data = randomChars(1024);
            assertArrayEquals(IOKit.read(new CharArrayReader(data)), data);
            assertArrayEquals(IOKit.read(new CharArrayReader(data), 5), Arrays.copyOf(data, 5));
            assertEquals(IOKit.string(new CharArrayReader(data)), new String(data));
            assertEquals(IOKit.string(new CharArrayReader(data), 5), new String(Arrays.copyOf(data, 5)));
            // to appender
            CharsBuilder builder = new CharsBuilder();
            assertEquals(IOKit.readTo(new CharArrayReader(data), builder), data.length);
            assertArrayEquals(data, builder.toCharArray());
            builder.reset();
            assertEquals(IOKit.readTo(new CharArrayReader(data), builder, data.length), data.length);
            assertArrayEquals(data, builder.toCharArray());
            builder.reset();
            // to array
            char[] dst = new char[data.length];
            assertEquals(IOKit.readTo(new CharArrayReader(data), dst), data.length);
            assertArrayEquals(data, dst);
            dst = new char[data.length];
            assertEquals(
                IOKit.readTo(new CharArrayReader(data), dst, 0, dst.length),
                data.length
            );
            assertArrayEquals(data, dst);
            // to buffer
            CharBuffer dstBuf = CharBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(new CharArrayReader(data), dstBuf), data.length);
            assertArrayEquals(data, dstBuf.array());
            dstBuf = CharBuffer.allocate(data.length);
            assertEquals(IOKit.readTo(new CharArrayReader(data), dstBuf, data.length), data.length);
            assertArrayEquals(data, dstBuf.array());
        }
    }

    @Test
    public void testWrite() throws Exception {
        {
            // write to appender
            char[] data = randomChars(1024);
            CharsBuilder appender1 = new CharsBuilder();
            IOKit.write(appender1, data);
            assertArrayEquals(appender1.toCharArray(), data);
            appender1.reset();
            IOKit.write(appender1, data, 33, 99);
            assertArrayEquals(appender1.toCharArray(), Arrays.copyOfRange(data, 33, 33 + 99));
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
            assertArrayEquals(appender2.toCharArray(), data);
            appender2.reset();
            IOKit.write(appender2, data, 33, 99);
            assertArrayEquals(appender2.toCharArray(), Arrays.copyOfRange(data, 33, 33 + 99));
            assertThrows(IORuntimeException.class, () -> IOKit.write(new ErrorAppender(), data));
        }
        {
            // write to output stream
            String str = "hello world";
            ByteArrayOutputStream dst = new ByteArrayOutputStream();
            IOKit.write(dst, str);
            assertEquals(dst.toString("UTF-8"), str);
            OutputStream dst2 = IOKit.newOutputStream(new byte[1]);
            assertThrows(IORuntimeException.class, () -> IOKit.write(dst2, str));
        }
    }

    @Test
    public void testString() {
        char[] chars = randomChars(16, 'a', 'z');
        byte[] data = new String(chars).getBytes(CharsKit.defaultCharset());
        ReadableByteChannel tch = new ReadableByteChannel() {
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
        {
            // no charset
            // string
            InputStream in = new ByteArrayInputStream(data);
            assertEquals(IOKit.string(in), new String(chars));
            assertNull(IOKit.string(in));
            ReadableByteChannel ch = Channels.newChannel(new ByteArrayInputStream(data));
            assertEquals(IOKit.string(ch), new String(chars));
            assertNull(IOKit.string(ch));
            // available stream string
            TestInputStream tin = new TestInputStream(new ByteArrayInputStream(data));
            tin.setNextOperation(ReadOps.READ_ZERO, 99);
            assertEquals(IOKit.availableString(tin), "");
            tin.setNextOperation(ReadOps.READ_NORMAL, 99);
            assertEquals(IOKit.availableString(tin), new String(chars));
            assertNull(IOKit.availableString(tin));
            // available channel string
            assertEquals(IOKit.availableString(tch), "");
            ReadableByteChannel tch2 = Channels.newChannel(new ByteArrayInputStream(data));
            assertEquals(IOKit.availableString(tch2), new String(chars));
            assertNull(IOKit.availableString(tch2));
        }
        {
            // with charset
            // string
            InputStream in = new ByteArrayInputStream(data);
            assertEquals(IOKit.string(in, CharsKit.defaultCharset()), new String(chars));
            assertNull(IOKit.string(in, CharsKit.defaultCharset()));
            ReadableByteChannel ch = Channels.newChannel(new ByteArrayInputStream(data));
            assertEquals(IOKit.string(ch, CharsKit.defaultCharset()), new String(chars));
            assertNull(IOKit.string(ch, CharsKit.defaultCharset()));
            // available stream string
            TestInputStream tin = new TestInputStream(new ByteArrayInputStream(data));
            tin.setNextOperation(ReadOps.READ_ZERO, 99);
            assertEquals(IOKit.availableString(tin, CharsKit.defaultCharset()), "");
            tin.setNextOperation(ReadOps.READ_NORMAL, 99);
            assertEquals(IOKit.availableString(tin, CharsKit.defaultCharset()), new String(chars));
            assertNull(IOKit.availableString(tin, CharsKit.defaultCharset()));
            // available channel string
            assertEquals(IOKit.availableString(tch, CharsKit.defaultCharset()), "");
            ReadableByteChannel tch2 = Channels.newChannel(new ByteArrayInputStream(data));
            assertEquals(IOKit.availableString(tch2, CharsKit.defaultCharset()), new String(chars));
            assertNull(IOKit.availableString(tch2, CharsKit.defaultCharset()));
        }
    }

    @Test
    public void testReadBytes() {
        byte[] data = randomBytes(128);
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ReadableByteChannel channel = Channels.newChannel(in);
        assertArrayEquals(IOKit.readBytes(channel), data);
        assertNull(IOKit.readBytes(channel));
        in.reset();
        assertArrayEquals(IOKit.readBytes(channel, 64), Arrays.copyOf(data, 64));
        assertArrayEquals(IOKit.readBytes(channel, 64), Arrays.copyOfRange(data, 64, 128));
        assertNull(IOKit.readBytes(channel, 64));
        in.reset();
        assertArrayEquals(IOKit.availableBytes(channel), data);
        assertNull(IOKit.availableBytes(channel));
        in.reset();
        assertArrayEquals(IOKit.availableBytes(channel, 64), Arrays.copyOf(data, 64));
        assertArrayEquals(IOKit.availableBytes(channel, 64), Arrays.copyOfRange(data, 64, 128));
        assertNull(IOKit.availableBytes(channel, 64));
        {
            // empty
            ReadableByteChannel zch = new ReadableByteChannel() {

                @Override
                public int read(ByteBuffer dst) {
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
            assertArrayEquals(IOKit.availableBytes(zch), new byte[0]);
            assertArrayEquals(IOKit.availableBytes(zch, 1), new byte[0]);
        }
    }

    @Test
    public void testOthers() throws Exception {
        {
            // close
            IOKit.close((Closeable) () -> {
            });
            assertThrows(IOException.class, () -> {
                IOKit.close((Closeable) () -> {
                    throw new IOException();
                });
            });
            IOKit.close((AutoCloseable) () -> {
            });
            assertThrows(IOException.class, () -> {
                IOKit.close((AutoCloseable) () -> {
                    throw new IOException();
                });
            });
            assertThrows(IOException.class, () -> {
                IOKit.close((AutoCloseable) () -> {
                    throw new Exception();
                });
            });
            IOKit.close("");
        }
        {
            // flush
            IOKit.flush((Flushable) () -> {
            });
            assertThrows(IOException.class, () -> {
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
        {
            // default io operator
            assertSame(IOKit.ioOperator(), IOOperator.get(IOKit.bufferSize()));
        }
    }

    @Test
    public void testIORuntimeException() throws Exception {
        assertThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException();
        });
        assertThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException("");
        });
        assertThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException("", new RuntimeException());
        });
        assertThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException(new RuntimeException());
        });
        assertThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException(new IOException());
        });
    }
}
