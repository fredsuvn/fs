package tests.core.io;

import internal.utils.DataGen;
import internal.utils.ErrorAppender;
import internal.utils.ErrorOutputStream;
import internal.utils.ReadOps;
import internal.utils.TestInputStream;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.bytes.BytesBuilder;
import space.sunqian.fs.base.chars.CharsBuilder;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IOMode;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.io.IORuntimeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IOKitTest implements DataGen {

    @Test
    public void testReader() throws Exception {
        // byte
        testReaderForBytes();
        // char
        testReaderForChars();
    }

    private void testReaderForBytes() throws Exception {
        byte[] data = randomBytes(1024);
        // read all
        testReaderForBytesReadAll(data);
        // to stream/channel
        testReaderForBytesToStreamOrChannel(data);
        // to array
        testReaderForBytesToArray(data);
        // to buffer
        testReaderForBytesToBuffer(data);
    }

    private void testReaderForBytesReadAll(byte[] data) throws Exception {
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
    }

    private void testReaderForBytesToStreamOrChannel(byte[] data) throws Exception {
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
    }

    private void testReaderForBytesToArray(byte[] data) throws Exception {
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
    }

    private void testReaderForBytesToBuffer(byte[] data) throws Exception {
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

    private void testReaderForChars() throws Exception {
        char[] data = randomChars(1024);
        // read all
        testReaderForCharsReadAll(data);
        // to appender
        testReaderForCharsToAppender(data);
        // to array
        testReaderForCharsToArray(data);
        // to buffer
        testReaderForCharsToBuffer(data);
    }

    private void testReaderForCharsReadAll(char[] data) throws Exception {
        assertArrayEquals(IOKit.read(new CharArrayReader(data)), data);
        assertArrayEquals(IOKit.read(new CharArrayReader(data), 5), Arrays.copyOf(data, 5));
        assertEquals(IOKit.string(new CharArrayReader(data)), new String(data));
        assertEquals(IOKit.string(new CharArrayReader(data), 5), new String(Arrays.copyOf(data, 5)));
    }

    private void testReaderForCharsToAppender(char[] data) throws Exception {
        CharsBuilder builder = new CharsBuilder();
        assertEquals(IOKit.readTo(new CharArrayReader(data), builder), data.length);
        assertArrayEquals(data, builder.toCharArray());
        builder.reset();
        assertEquals(IOKit.readTo(new CharArrayReader(data), builder, data.length), data.length);
        assertArrayEquals(data, builder.toCharArray());
        builder.reset();
    }

    private void testReaderForCharsToArray(char[] data) throws Exception {
        char[] dst = new char[data.length];
        assertEquals(IOKit.readTo(new CharArrayReader(data), dst), data.length);
        assertArrayEquals(data, dst);
        dst = new char[data.length];
        assertEquals(
            IOKit.readTo(new CharArrayReader(data), dst, 0, dst.length),
            data.length
        );
        assertArrayEquals(data, dst);
    }

    private void testReaderForCharsToBuffer(char[] data) throws Exception {
        CharBuffer dstBuf = CharBuffer.allocate(data.length);
        assertEquals(IOKit.readTo(new CharArrayReader(data), dstBuf), data.length);
        assertArrayEquals(data, dstBuf.array());
        dstBuf = CharBuffer.allocate(data.length);
        assertEquals(IOKit.readTo(new CharArrayReader(data), dstBuf, data.length), data.length);
        assertArrayEquals(data, dstBuf.array());
    }

    @Test
    public void testWrite() throws Exception {
        // write bytes
        testWriteBytes();
        // write to appender
        testWriteToAppender();
        // write string
        testWriteString();
    }

    private void testWriteBytes() throws Exception {
        ByteArrayOutputStream dst = new ByteArrayOutputStream();
        byte[] bytes = randomBytes(16);
        IOKit.write(dst, bytes);
        assertArrayEquals(bytes, dst.toByteArray());
        dst.reset();
        bytes = randomBytes(16);
        IOKit.write(dst, bytes, 2, 10);
        assertArrayEquals(Arrays.copyOfRange(bytes, 2, 12), dst.toByteArray());
        dst.reset();
        bytes = randomBytes(16);
        IOKit.write(dst, ByteBuffer.wrap(bytes));
        assertArrayEquals(bytes, dst.toByteArray());
        dst.reset();
        bytes = randomBytes(16);
        IOKit.write(Channels.newChannel(dst), bytes);
        assertArrayEquals(bytes, dst.toByteArray());
        dst.reset();
        bytes = randomBytes(16);
        IOKit.write(Channels.newChannel(dst), bytes, 2, 10);
        assertArrayEquals(Arrays.copyOfRange(bytes, 2, 12), dst.toByteArray());
        dst.reset();
        bytes = randomBytes(16);
        IOKit.write(Channels.newChannel(dst), ByteBuffer.wrap(bytes));
        assertArrayEquals(bytes, dst.toByteArray());
        dst.reset();
        OutputStream err = new ErrorOutputStream();
        assertThrows(IORuntimeException.class, () -> IOKit.write(err, new byte[10]));
        assertThrows(IORuntimeException.class, () -> IOKit.write(err, new byte[10], 0, 1));
        assertThrows(IORuntimeException.class, () -> IOKit.write(err, ByteBuffer.allocate(10)));
        assertThrows(IORuntimeException.class, () -> IOKit.write(Channels.newChannel(err), new byte[10]));
        assertThrows(IORuntimeException.class, () -> IOKit.write(Channels.newChannel(err), new byte[10], 0, 1));
        assertThrows(IORuntimeException.class, () -> IOKit.write(Channels.newChannel(err), ByteBuffer.allocate(10)));
    }

    private void testWriteToAppender() throws Exception {
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

    private void testWriteString() throws Exception {
        String str = "hello world";
        byte[] strBytes = str.getBytes(CharsKit.defaultCharset());
        ByteArrayOutputStream dst = new ByteArrayOutputStream();
        IOKit.write(dst, str);
        assertArrayEquals(strBytes, dst.toByteArray());
        dst.reset();
        IOKit.write(dst, str, CharsKit.defaultCharset());
        assertArrayEquals(strBytes, dst.toByteArray());
        dst.reset();
        IOKit.write(Channels.newChannel(dst), str);
        assertArrayEquals(strBytes, dst.toByteArray());
        dst.reset();
        IOKit.write(Channels.newChannel(dst), str, CharsKit.defaultCharset());
        assertArrayEquals(strBytes, dst.toByteArray());
        dst.reset();
        OutputStream err = new ErrorOutputStream();
        assertThrows(IORuntimeException.class, () -> IOKit.write(err, str));
        assertThrows(IORuntimeException.class, () -> IOKit.write(err, str, CharsKit.defaultCharset()));
        assertThrows(IORuntimeException.class, () -> IOKit.write(Channels.newChannel(err), str));
        assertThrows(IORuntimeException.class, () -> IOKit.write(Channels.newChannel(err), str, CharsKit.defaultCharset()));
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
        // no charset
        testStringNoCharset(data, chars, tch);
        // with charset
        testStringWithCharset(data, chars, tch);
    }

    private void testStringNoCharset(byte[] data, char[] chars, ReadableByteChannel tch) {
        // string
        testStringNoCharsetString(data, chars);
        // available stream string
        testStringNoCharsetAvailableStreamString(data, chars);
        // available channel string
        testStringNoCharsetAvailableChannelString(data, chars, tch);
    }

    private void testStringNoCharsetString(byte[] data, char[] chars) {
        InputStream in = new ByteArrayInputStream(data);
        assertEquals(IOKit.string(in), new String(chars));
        assertNull(IOKit.string(in));
        ReadableByteChannel ch = Channels.newChannel(new ByteArrayInputStream(data));
        assertEquals(IOKit.string(ch), new String(chars));
        assertNull(IOKit.string(ch));
    }

    private void testStringNoCharsetAvailableStreamString(byte[] data, char[] chars) {
        TestInputStream tin = new TestInputStream(new ByteArrayInputStream(data));
        tin.setNextOperation(ReadOps.READ_ZERO, 99);
        assertEquals("", IOKit.availableString(tin));
        tin.setNextOperation(ReadOps.READ_NORMAL, 99);
        assertEquals(IOKit.availableString(tin), new String(chars));
        assertNull(IOKit.availableString(tin));
    }

    private void testStringNoCharsetAvailableChannelString(byte[] data, char[] chars, ReadableByteChannel tch) {
        assertEquals("", IOKit.availableString(tch));
        ReadableByteChannel tch2 = Channels.newChannel(new ByteArrayInputStream(data));
        assertEquals(IOKit.availableString(tch2), new String(chars));
        assertNull(IOKit.availableString(tch2));
    }

    private void testStringWithCharset(byte[] data, char[] chars, ReadableByteChannel tch) {
        // string
        testStringWithCharsetString(data, chars);
        // available stream string
        testStringWithCharsetAvailableStreamString(data, chars);
        // available channel string
        testStringWithCharsetAvailableChannelString(data, chars, tch);
    }

    private void testStringWithCharsetString(byte[] data, char[] chars) {
        InputStream in = new ByteArrayInputStream(data);
        assertEquals(IOKit.string(in, CharsKit.defaultCharset()), new String(chars));
        assertNull(IOKit.string(in, CharsKit.defaultCharset()));
        ReadableByteChannel ch = Channels.newChannel(new ByteArrayInputStream(data));
        assertEquals(IOKit.string(ch, CharsKit.defaultCharset()), new String(chars));
        assertNull(IOKit.string(ch, CharsKit.defaultCharset()));
    }

    private void testStringWithCharsetAvailableStreamString(byte[] data, char[] chars) {
        TestInputStream tin = new TestInputStream(new ByteArrayInputStream(data));
        tin.setNextOperation(ReadOps.READ_ZERO, 99);
        assertEquals("", IOKit.availableString(tin, CharsKit.defaultCharset()));
        tin.setNextOperation(ReadOps.READ_NORMAL, 99);
        assertEquals(IOKit.availableString(tin, CharsKit.defaultCharset()), new String(chars));
        assertNull(IOKit.availableString(tin, CharsKit.defaultCharset()));
    }

    private void testStringWithCharsetAvailableChannelString(byte[] data, char[] chars, ReadableByteChannel tch) {
        assertEquals("", IOKit.availableString(tch, CharsKit.defaultCharset()));
        ReadableByteChannel tch2 = Channels.newChannel(new ByteArrayInputStream(data));
        assertEquals(IOKit.availableString(tch2, CharsKit.defaultCharset()), new String(chars));
        assertNull(IOKit.availableString(tch2, CharsKit.defaultCharset()));
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
            assertArrayEquals(new byte[0], IOKit.availableBytes(zch));
            assertArrayEquals(new byte[0], IOKit.availableBytes(zch, 1));
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
            assertEquals(IOMode.BLOCKING, IOMode.valueOf("BLOCKING"));
            assertEquals(IOMode.NON_BLOCKING, IOMode.valueOf("NON_BLOCKING"));
        }
        {
            // default io operator
            assertSame(IOKit.ioOperator(), IOOperator.get(IOKit.bufferSize()));
        }
        {
            // writer
            StringBuilder sb = new StringBuilder();
            Writer writer = IOKit.wrapWriter(sb);
            writer.close();
            writer.flush();
            writer.write("abc");
            assertNotSame(writer, sb);
            assertEquals("abc", sb.toString());
            IOKit.wrapWriter(writer).write("abc");
            assertSame(writer, IOKit.wrapWriter(writer));
            assertEquals("abcabc", sb.toString());
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

    @Test
    public void testReadToWithBuffer() throws Exception {
        // test bytes
        testReadToWithBufferForBytes(128, 11);
        testReadToWithBufferForBytes(128, 128);
        testReadToWithBufferForBytes(129, 1111);
        // test chars
        testReadToWithBufferForChars(128, 11);
        testReadToWithBufferForChars(128, 128);
        testReadToWithBufferForChars(129, 1111);
        // test read 0
        testReadToWithBufferRead0();
    }

    private void testReadToWithBufferRead0() throws Exception {
        // test bytes
        testReadToWithBufferRead0ForBytes();
        // test chars
        testReadToWithBufferRead0ForChars();
    }

    private void testReadToWithBufferRead0ForBytes() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1];
        // stream
        testReadToWithBufferRead0ForBytesStream(in, out, buf);
        // channel
        testReadToWithBufferRead0ForBytesChannel(in, out, buf);
    }

    private void testReadToWithBufferRead0ForBytesStream(ByteArrayInputStream in, ByteArrayOutputStream out, byte[] buf) throws Exception {
        assertEquals(
            -1,
            IOKit.readTo(in, out, buf)
        );
        assertEquals(
            0,
            IOKit.readTo(in, out, 0, buf)
        );
        assertEquals(
            -1,
            IOKit.readTo(in, Channels.newChannel(out), buf)
        );
        assertEquals(
            0,
            IOKit.readTo(in, Channels.newChannel(out), 0, buf)
        );
        assertEquals(
            -1,
            IOKit.availableTo(in, out, buf)
        );
        assertEquals(
            0,
            IOKit.availableTo(in, out, 0, buf)
        );
        assertEquals(
            -1,
            IOKit.availableTo(in, Channels.newChannel(out), buf)
        );
        assertEquals(
            0,
            IOKit.availableTo(in, Channels.newChannel(out), 0, buf)
        );
    }

    private void testReadToWithBufferRead0ForBytesChannel(ByteArrayInputStream in, ByteArrayOutputStream out, byte[] buf) throws Exception {
        assertEquals(
            -1,
            IOKit.readTo(Channels.newChannel(in), out, buf)
        );
        assertEquals(
            0,
            IOKit.readTo(Channels.newChannel(in), out, 0, buf)
        );
        assertEquals(
            -1,
            IOKit.readTo(Channels.newChannel(in), Channels.newChannel(out), buf)
        );
        assertEquals(
            0,
            IOKit.readTo(Channels.newChannel(in), Channels.newChannel(out), 0, buf)
        );
        assertEquals(
            -1,
            IOKit.availableTo(Channels.newChannel(in), out, buf)
        );
        assertEquals(
            0,
            IOKit.availableTo(Channels.newChannel(in), out, 0, buf)
        );
        assertEquals(
            -1,
            IOKit.availableTo(Channels.newChannel(in), Channels.newChannel(out), buf)
        );
        assertEquals(
            0,
            IOKit.availableTo(Channels.newChannel(in), Channels.newChannel(out), 0, buf)
        );
    }

    private void testReadToWithBufferRead0ForChars() throws Exception {
        CharArrayReader in = new CharArrayReader(new char[0]);
        CharArrayWriter out = new CharArrayWriter();
        char[] buf = new char[1];
        assertEquals(
            -1,
            IOKit.readTo(in, out, buf)
        );
        in.reset();
        out.reset();
        assertEquals(
            0,
            IOKit.readTo(in, out, 0, buf)
        );
        in.reset();
        out.reset();
        assertEquals(
            -1,
            IOKit.availableTo(in, out, buf)
        );
        in.reset();
        out.reset();
        assertEquals(
            0,
            IOKit.availableTo(in, out, 0, buf)
        );
        in.reset();
        out.reset();
    }

    private void testReadToWithBufferForBytes(int dataSize, int bufSize) throws Exception {
        byte[] data = randomBytes(dataSize);
        byte[] buf = new byte[bufSize];
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        {
            // stream
            assertEquals(
                dataSize,
                IOKit.readTo(in, out, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize / 2,
                IOKit.readTo(in, out, dataSize / 2, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize,
                IOKit.readTo(in, Channels.newChannel(out), buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize / 2,
                IOKit.readTo(in, Channels.newChannel(out), dataSize / 2, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize,
                IOKit.availableTo(in, out, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize / 2,
                IOKit.availableTo(in, out, dataSize / 2, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize,
                IOKit.availableTo(in, Channels.newChannel(out), buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize / 2,
                IOKit.availableTo(in, Channels.newChannel(out), dataSize / 2, buf)
            );
            in.reset();
            out.reset();
        }
        {
            // channel
            assertEquals(
                dataSize,
                IOKit.readTo(Channels.newChannel(in), out, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize / 2,
                IOKit.readTo(Channels.newChannel(in), out, dataSize / 2, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize,
                IOKit.readTo(Channels.newChannel(in), Channels.newChannel(out), buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize / 2,
                IOKit.readTo(Channels.newChannel(in), Channels.newChannel(out), dataSize / 2, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize,
                IOKit.availableTo(Channels.newChannel(in), out, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize / 2,
                IOKit.availableTo(Channels.newChannel(in), out, dataSize / 2, buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize,
                IOKit.availableTo(Channels.newChannel(in), Channels.newChannel(out), buf)
            );
            in.reset();
            out.reset();
            assertEquals(
                dataSize / 2,
                IOKit.availableTo(Channels.newChannel(in), Channels.newChannel(out), dataSize / 2, buf)
            );
            in.reset();
            out.reset();
        }
    }

    private void testReadToWithBufferForChars(int dataSize, int bufSize) throws Exception {
        char[] data = randomChars(dataSize);
        char[] buf = new char[bufSize];
        CharArrayReader in = new CharArrayReader(data);
        CharArrayWriter out = new CharArrayWriter();
        assertEquals(
            dataSize,
            IOKit.readTo(in, out, buf)
        );
        in.reset();
        out.reset();
        assertEquals(
            dataSize / 2,
            IOKit.readTo(in, out, dataSize / 2, buf)
        );
        in.reset();
        out.reset();
        assertEquals(
            dataSize,
            IOKit.availableTo(in, out, buf)
        );
        in.reset();
        out.reset();
        assertEquals(
            dataSize / 2,
            IOKit.availableTo(in, out, dataSize / 2, buf)
        );
        in.reset();
        out.reset();
    }
}
