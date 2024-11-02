package test.io;

import org.testng.annotations.Test;
import xyz.fslabo.common.base.JieChars;
import xyz.fslabo.common.base.JieRandom;
import xyz.fslabo.common.base.JieString;
import xyz.fslabo.common.io.AbstractWriter;
import xyz.fslabo.common.io.IORuntimeException;
import xyz.fslabo.common.io.JieInput;
import xyz.fslabo.common.io.JieOutput;
import xyz.fslabo.test.JieTest;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.testng.Assert.*;

public class ImplsTest {

    @Test
    public void testInput() throws Exception {
        testInput(1024);
        testInput(60);
        testInput(34);

        // error
        expectThrows(NullPointerException.class, () -> JieInput.wrap((byte[]) null, 2, +1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieInput.wrap(new byte[0], 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieInput.wrap(new byte[0], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieInput.wrap(new byte[0], 2, -1));
    }

    private void testInput(int sourceSize) throws Exception {
        byte[] source = new byte[sourceSize];
        JieRandom.fill(source);

        // bytes
        testInput(JieInput.wrap(source), source, true);
        testInput(
            JieInput.wrap(source, 2, source.length - 10),
            Arrays.copyOfRange(source, 2, source.length - 8),
            true
        );

        // buffer
        ByteBuffer buffer = ByteBuffer.wrap(source);
        InputStream bufferIn = JieInput.wrap(buffer);
        testInput(bufferIn, source, true);
        Class<?> bufferInClass = bufferIn.getClass();
        Method read0 = bufferInClass.getDeclaredMethod("read0");
        JieTest.testThrow(IOException.class, read0, bufferIn);
        read0 = bufferInClass.getDeclaredMethod("read0", byte[].class, int.class, int.class);
        JieTest.testThrow(IOException.class, read0, bufferIn, null, 0, 0);
        read0 = bufferInClass.getDeclaredMethod("skip0", int.class);
        JieTest.testThrow(IOException.class, read0, bufferIn, 99);

        // file
        Path path = Paths.get("src", "test", "resources", "io", "input.test");
        RandomAccessFile raf = new FakeRandomFile(path.toFile(), "r", source);
        InputStream rafIn = JieInput.wrap(raf, 6);
        testInput(rafIn, Arrays.copyOfRange(source, 6, source.length), true);
        expectThrows(IORuntimeException.class, () -> rafIn.mark(66));

        // chars
        char[] chars = JieRandom.fill(new char[sourceSize], '0', '9');
        byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
        InputStream charsIn = JieInput.wrap(new CharArrayReader(chars));
        testInput(charsIn, charBytes, false);
        expectThrows(IOException.class, charsIn::read);
        // chinese: '\u4e00' - '\u9fff'
        chars = JieRandom.fill(new char[sourceSize], '\u4e00', '\u4e01');
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsIn = JieInput.wrap(new CharArrayReader(chars));
        testInput(charsIn, charBytes, false);
        expectThrows(IOException.class, charsIn::read);
        // emoji: "\uD83D\uDD1E"
        for (int i = 0; i < chars.length; i += 2) {
            chars[i] = '\uD83D';
            chars[i + 1] = '\uDD1E';
        }
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsIn = JieInput.wrap(new CharArrayReader(chars));
        testInput(charsIn, charBytes, false);
        expectThrows(IOException.class, charsIn::read);
        // error: U+DD88
        Arrays.fill(chars, '\uDD88');
        charsIn = JieInput.wrap(new CharArrayReader(chars));
        expectThrows(IOException.class, charsIn::read);

        // error
        FakeRandomFile.SEEK_ERR = true;
        expectThrows(IORuntimeException.class, () -> JieInput.wrap(raf, 6));
        FakeRandomFile.SEEK_ERR = false;
    }

    private void testInput(InputStream in, byte[] source, boolean available) throws Exception {
        byte[] dest = new byte[source.length];
        assertEquals(in.read(dest, 0, 0), 0);
        assertEquals((byte) in.read(), source[0]);
        assertEquals((byte) in.read(), source[1]);
        assertEquals((byte) in.read(), source[2]);
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 3, 13));
        if (available) {
            assertEquals(in.available(), source.length - 13);
        } else {
            assertTrue(in.available() <= source.length - 13 && in.available() >= 0);
        }
        if (in.markSupported()) {
            expectThrows(IOException.class, in::reset);
            in.mark(999);
            assertEquals(in.read(dest, 3, 10), 10);
            assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
            in.reset();
        }
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(source.length), source.length - 23);
        assertEquals(in.read(), -1);
        assertEquals(in.read(dest, 3, 10), -1);
        assertEquals(in.skip(source.length), 0);

        in.close();
    }

    @Test
    public void testOutput() throws Exception {
        testOutput(1024);
        testOutput(60);
        testOutput(34);

        // error
        expectThrows(NullPointerException.class, () -> JieOutput.wrap((byte[]) null, 2, +1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieOutput.wrap(new byte[0], 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieOutput.wrap(new byte[0], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieOutput.wrap(new byte[0], 2, -1));
    }

    private void testOutput(int sourceSize) throws Exception {
        byte[] source = new byte[sourceSize];
        Arrays.fill(source, (byte) 1);

        // bytes
        OutputStream bytesOut = JieOutput.wrap(source);
        byte[] data = JieRandom.fill(new byte[source.length]);
        testOutput(bytesOut, data);
        assertEquals(data, source);
        Arrays.fill(source, (byte) 1);
        data = JieRandom.fill(new byte[source.length - 10]);
        OutputStream byteOut2 = JieOutput.wrap(source, 2, data.length);
        testOutput(byteOut2, data);
        assertEquals(data, Arrays.copyOfRange(source, 2, data.length + 2));

        // buffer
        OutputStream bufferOut = JieOutput.wrap(ByteBuffer.wrap(source));
        data = JieRandom.fill(new byte[source.length]);
        testOutput(bufferOut, data);
        assertEquals(data, source);

        // file
        Path path = Paths.get("src", "test", "resources", "io", "input.test");
        RandomAccessFile raf = new FakeRandomFile(path.toFile(), "r", source);
        OutputStream rafOut = JieOutput.wrap(raf, 6);
        data = JieRandom.fill(new byte[source.length - 6]);
        testOutput(rafOut, data);
        assertEquals(data, Arrays.copyOfRange(source, 6, data.length + 6));
        rafOut.flush();
        rafOut.close();
        expectThrows(IOException.class, () -> rafOut.write(1));

        // chars
        char[] dest = new char[sourceSize];
        char[] chars = JieRandom.fill(new char[sourceSize], '0', '9');
        byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
        OutputStream charsOut = JieOutput.wrap(JieOutput.wrap(dest));
        testOutput(charsOut, charBytes);
        assertEquals(chars, dest);
        OutputStream charsOut1 = charsOut;
        expectThrows(IOException.class, () -> charsOut1.write(1));
        // chinese: '\u4e00' - '\u9fff'
        chars = JieRandom.fill(new char[sourceSize], '\u4e00', '\u4e01');
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsOut = JieOutput.wrap(JieOutput.wrap(dest));
        testOutput(charsOut, charBytes);
        assertEquals(chars, dest);
        OutputStream charsOut2 = charsOut;
        expectThrows(IOException.class, () -> charsOut2.write(1));
        // emoji: "\uD83D\uDD1E"
        for (int i = 0; i < chars.length; i += 2) {
            chars[i] = '\uD83D';
            chars[i + 1] = '\uDD1E';
        }
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsOut = JieOutput.wrap(JieOutput.wrap(dest));
        testOutput(charsOut, charBytes);
        assertEquals(chars, dest);
        OutputStream charsOut3 = charsOut;
        expectThrows(IOException.class, () -> charsOut3.write(1));
        // fake charset
        byte[] fakeBytes = JieRandom.fill(new byte[sourceSize]);
        char[] fakeChars = new char[fakeBytes.length * 2];
        for (int i = 0; i < fakeBytes.length; i++) {
            fakeChars[i * 2] = (char) fakeBytes[i];
            fakeChars[i * 2 + 1] = (char) fakeBytes[i];
        }
        char[] fakeDest = new char[fakeBytes.length * 2];
        charsOut = JieOutput.wrap(JieOutput.wrap(fakeDest), new FakeCharset(2));
        testOutput(charsOut, fakeBytes);
        assertEquals(fakeChars, fakeDest);
        OutputStream charsOut4 = charsOut;
        expectThrows(IOException.class, () -> charsOut4.write(1));
        // error: 0xC1
        byte[] errBytes = new byte[sourceSize];
        Arrays.fill(errBytes, (byte) 0xC1);
        charsOut = JieOutput.wrap(JieOutput.wrap(dest));
        OutputStream charsOut5 = charsOut;
        expectThrows(IOException.class, () -> charsOut5.write(errBytes));
        // StringBuilder
        StringBuilder sb = new StringBuilder();
        charsOut = JieOutput.wrap(sb);
        charsOut.write("中文".getBytes(JieChars.UTF_8));
        charsOut.flush();
        assertEquals(sb.toString(), "中文");
        StringBuffer sbuf = new StringBuffer();
        charsOut = JieOutput.wrap(sbuf);
        charsOut.write("中文".getBytes(JieChars.UTF_8));
        charsOut.flush();
        assertEquals(sbuf.toString(), "中文");
        CharBuffer cb = CharBuffer.allocate(10);
        charsOut = JieOutput.wrap((Appendable) cb);
        charsOut.write("中文".getBytes(JieChars.UTF_8));
        charsOut.flush();
        cb.flip();
        assertEquals(cb.toString(), "中文");
        // appender
        AutoCloseAppender aa = new AutoCloseAppender();
        charsOut = JieOutput.wrap(aa);
        charsOut.write(1);
        charsOut.close();
        charsOut = JieOutput.wrap(aa);
        aa.err = 1;
        OutputStream charsOut6 = charsOut;
        expectThrows(IOException.class, () -> charsOut6.write(1));
        expectThrows(IOException.class, charsOut::close);
        charsOut = JieOutput.wrap(aa);
        aa.err = 2;
        OutputStream charsOut7 = charsOut;
        expectThrows(IOException.class, () -> charsOut7.write(1));
        expectThrows(IOException.class, charsOut::close);
        OnlyAppender oa = new OnlyAppender();
        charsOut = JieOutput.wrap(oa);
        charsOut.close();

        // error
        FakeRandomFile.SEEK_ERR = true;
        expectThrows(IORuntimeException.class, () -> JieOutput.wrap(raf, 6));
        FakeRandomFile.SEEK_ERR = false;
    }

    private void testOutput(OutputStream out, byte[] data) throws Exception {
        out.write(data[0]);
        out.write(data[1]);
        out.write(Arrays.copyOfRange(data, 2, data.length));
        out.write(Arrays.copyOfRange(data, 2, 2));
        out.flush();

        expectThrows(IOException.class, () -> out.write(1));
        expectThrows(IOException.class, () -> out.write(new byte[10]));

        out.flush();
        out.close();
        out.close();
    }

    @Test
    public void testReader() throws Exception {
        testReader(1024);
        testReader(60);
        testReader(34);

        // error
        expectThrows(NullPointerException.class, () -> JieInput.wrap((char[]) null, 2, +1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieInput.wrap(new char[0], 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieInput.wrap(new char[0], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieInput.wrap(new char[0], 2, -1));
    }

    private void testReader(int sourceSize) throws Exception {
        char[] source = new char[sourceSize];
        JieRandom.fill(source);

        // chars
        testReader(JieInput.wrap(source), source);
        testReader(
            JieInput.wrap(source, 2, source.length - 10),
            Arrays.copyOfRange(source, 2, source.length - 8)
        );
        assertTrue(JieInput.wrap(source).ready());

        // string
        testReader(JieInput.wrap(new String(source)), source);

        // buffer
        CharBuffer buffer = CharBuffer.wrap(source);
        Reader bufferIn = JieInput.wrap(buffer);
        testReader(bufferIn, source);
        Class<?> bufferInClass = bufferIn.getClass();
        Method read0 = bufferInClass.getDeclaredMethod("read0");
        JieTest.testThrow(IOException.class, read0, bufferIn);
        read0 = bufferInClass.getDeclaredMethod("read0", char[].class, int.class, int.class);
        JieTest.testThrow(IOException.class, read0, bufferIn, null, 0, 0);
        read0 = bufferInClass.getDeclaredMethod("skip0", int.class);
        JieTest.testThrow(IOException.class, read0, bufferIn, 99);

        // bytes
        char[] chars = JieRandom.fill(new char[sourceSize], '0', '9');
        byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
        Reader charsIn = JieInput.wrap(JieInput.wrap(charBytes));
        testReader(charsIn, chars);
        expectThrows(IOException.class, charsIn::read);
        // chinese: '\u4e00' - '\u9fff'
        chars = JieRandom.fill(new char[sourceSize], '\u4e00', '\u4e01');
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsIn = JieInput.wrap(JieInput.wrap(charBytes));
        testReader(charsIn, chars);
        expectThrows(IOException.class, charsIn::read);
        // emoji: "\uD83D\uDD1E"
        for (int i = 0; i < chars.length; i += 2) {
            chars[i] = '\uD83D';
            chars[i + 1] = '\uDD1E';
        }
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsIn = JieInput.wrap(JieInput.wrap(charBytes));
        testReader(charsIn, chars);
        expectThrows(IOException.class, charsIn::read);
        // fake charset
        byte[] fakeBytes = JieRandom.fill(new byte[sourceSize]);
        char[] fakeChars = new char[fakeBytes.length * 3];
        for (int i = 0; i < fakeBytes.length; i++) {
            fakeChars[i * 3] = (char) fakeBytes[i];
            fakeChars[i * 3 + 1] = (char) fakeBytes[i];
            fakeChars[i * 3 + 2] = (char) fakeBytes[i];
        }
        charsIn = JieInput.wrap(JieInput.wrap(fakeBytes), new FakeCharset(3));
        testReader(charsIn, fakeChars);
        expectThrows(IOException.class, charsIn::read);
        // error: 0xC1
        Arrays.fill(charBytes, (byte) 0xC1);
        charsIn = JieInput.wrap(JieInput.wrap(charBytes));
        expectThrows(IOException.class, charsIn::read);
        // ready
        charsIn = JieInput.wrap(new InputStream() {
            @Override
            public int read() {
                return 0;
            }

            @Override
            public int available() {
                return 1;
            }
        });
        assertTrue(charsIn.ready());
        charsIn = JieInput.wrap(new InputStream() {
            @Override
            public int read() {
                return 0;
            }

            @Override
            public int available() {
                return 0;
            }
        });
        assertFalse(charsIn.ready());
    }

    private void testReader(Reader in, char[] source) throws Exception {
        char[] dest = new char[source.length];
        assertEquals(in.read(dest, 0, 0), 0);
        assertEquals((char) in.read(), source[0]);
        assertEquals((char) in.read(), source[1]);
        CharBuffer target = CharBuffer.allocate(1);
        assertEquals(in.read(target), 1);
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 3, 13));
        if (in.markSupported()) {
            expectThrows(IOException.class, in::reset);
            in.mark(999);
            assertEquals(in.read(dest, 3, 10), 10);
            assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
            in.reset();
        }
        assertEquals(in.read(dest, 3, 10), 10);
        assertEquals(Arrays.copyOfRange(dest, 3, 13), Arrays.copyOfRange(source, 13, 23));
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(source.length), source.length - 23);
        assertEquals(in.read(), -1);
        assertEquals(in.read(dest, 3, 10), -1);
        assertEquals(in.skip(source.length), 0);

        in.close();
    }

    @Test
    public void testWriter() throws Exception {
        testWriter(1024);
        testWriter(60);
        testWriter(34);

        // error
        expectThrows(NullPointerException.class, () -> JieOutput.wrap((char[]) null, 2, +1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieOutput.wrap(new char[0], 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieOutput.wrap(new char[0], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieOutput.wrap(new char[0], 2, -1));
        expectThrows(NullPointerException.class, () -> JieOutput.wrap(new char[100]).write((String) null, 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieOutput.wrap(new char[100]).write("", 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieOutput.wrap(new char[100]).write("", -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieOutput.wrap(new char[100]).write("", 2, -1));
    }

    private void testWriter(int sourceSize) throws Exception {
        char[] source = new char[sourceSize];
        Arrays.fill(source, (char) 1);

        // chars
        Writer charsOut = JieOutput.wrap(source);
        char[] data = JieRandom.fill(new char[source.length]);
        testWriter(charsOut, data);
        assertEquals(data, source);
        Arrays.fill(source, (char) 1);
        data = JieRandom.fill(new char[source.length - 10]);
        Writer charsOut2 = JieOutput.wrap(source, 2, data.length);
        testWriter(charsOut2, data);
        assertEquals(data, Arrays.copyOfRange(source, 2, data.length + 2));
        char[] nullChars = new char[4];
        Writer nullWriter = JieOutput.wrap(nullChars);
        nullWriter.append(null);
        nullWriter.write("null", 0, 0);
        assertEquals(nullChars, "null".toCharArray());

        // buffer
        Writer bufferOut = JieOutput.wrap(CharBuffer.wrap(source));
        data = JieRandom.fill(new char[source.length]);
        testWriter(bufferOut, data);
        assertEquals(data, source);

        // abstract
        Writer absOut = new AbstractWriter() {

            @Override
            protected void doWrite(char c) {
            }

            @Override
            protected void doWrite(char[] cbuf, int off, int len) {
            }

            @Override
            protected void doWrite(String str, int off, int len) {
                throw new IllegalStateException();
            }

            @Override
            protected void doAppend(CharSequence csq, int start, int end) {
                throw new IllegalStateException();
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
            }
        };
        expectThrows(IOException.class, () -> absOut.append("123"));
        expectThrows(IOException.class, () -> absOut.write("123"));

        // bytes
        char[] chars = JieRandom.fill(new char[sourceSize], '0', '9');
        byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
        byte[] dest = new byte[charBytes.length];
        Writer bytesOut = JieOutput.wrap(JieOutput.wrap(dest));
        testWriter(bytesOut, chars);
        assertEquals(charBytes, dest);
        Writer bytesOut1 = bytesOut;
        expectThrows(IOException.class, () -> bytesOut1.write(1));
        // chinese: '\u4e00' - '\u9fff'
        chars = JieRandom.fill(new char[sourceSize], '\u4e00', '\u4e01');
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        dest = new byte[charBytes.length];
        bytesOut = JieOutput.wrap(JieOutput.wrap(dest));
        testWriter(bytesOut, chars);
        assertEquals(charBytes, dest);
        Writer bytesOut2 = bytesOut;
        expectThrows(IOException.class, () -> bytesOut2.write(1));
        // emoji: "\uD83D\uDD1E"
        for (int i = 0; i < chars.length; i += 2) {
            chars[i] = '\uD83D';
            chars[i + 1] = '\uDD1E';
        }
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        dest = new byte[charBytes.length];
        bytesOut = JieOutput.wrap(JieOutput.wrap(dest));
        testWriter(bytesOut, chars);
        assertEquals(charBytes, dest);
        Writer bytesOut3 = bytesOut;
        expectThrows(IOException.class, () -> bytesOut3.write(1));
        // error: U+DD88
        dest = new byte[charBytes.length];
        bytesOut = JieOutput.wrap(JieOutput.wrap(dest));
        Writer bytesOut4 = bytesOut;
        expectThrows(IOException.class, () -> bytesOut4.write('\uDD88'));
        // close
        bytesOut.close();
        expectThrows(IOException.class, () -> bytesOut4.write(1));
        bytesOut.close();
    }

    private void testWriter(Writer out, char[] data) throws Exception {
        out.write(data[0]);
        out.append(data[1]);
        out.write(new String(data, 2, 1));
        out.write(Arrays.copyOfRange(data, 3, 6));
        out.append(JieString.asChars(data, 6, 10));
        out.append(new String(data, 10, 10));
        out.write(Arrays.copyOfRange(data, 20, data.length));
        out.write(new char[0]);
        out.append("");

        expectThrows(IOException.class, () -> out.write(1));
        expectThrows(IOException.class, () -> out.write(new char[10]));

        out.flush();
        out.close();
    }

    private static final class FakeRandomFile extends RandomAccessFile {

        private static boolean SEEK_ERR = false;

        private int seek = 0;
        private final byte[] data;
        private volatile InputStream in;
        private volatile OutputStream out;
        private boolean close = false;

        public FakeRandomFile(File name, String mode, byte[] data) throws IOException {
            super(name, mode);
            this.data = data;
        }

        private InputStream getIn() {
            if (in == null) {
                in = JieInput.wrap(data, seek, data.length - seek);
            }
            return in;
        }

        @Override
        public int read() throws IOException {
            checkClose();
            int result = getIn().read();
            if (result >= 0) {
                seek++;
            }
            return result;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            checkClose();
            int result = getIn().read(b, off, len);
            if (result >= 0) {
                seek += result;
            }
            return result;
        }

        @Override
        public long getFilePointer() throws IOException {
            checkClose();
            return seek;
        }

        @Override
        public void seek(long pos) throws IOException {
            if (SEEK_ERR) {
                throw new IOException();
            }
            checkClose();
            this.seek = (int) pos;
            this.in = null;
            this.out = null;
        }

        @Override
        public long length() throws IOException {
            checkClose();
            return data.length;
        }

        private OutputStream getOut() {
            if (out == null) {
                out = JieOutput.wrap(data, seek, data.length - seek);
            }
            return out;
        }

        @Override
        public void write(int b) throws IOException {
            checkClose();
            getOut().write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            checkClose();
            getOut().write(b, off, len);
        }

        @Override
        public void close() throws IOException {
            close = true;
        }

        private void checkClose() throws IOException {
            if (close) {
                throw new IOException("Stream closed.");
            }
        }
    }

    private static final class FakeCharset extends Charset {

        private final int num;

        private FakeCharset(int num) {
            super("fake", new String[0]);
            this.num = num;
        }

        @Override
        public boolean contains(Charset cs) {
            return false;
        }

        @Override
        public CharsetDecoder newDecoder() {
            return new FakeCharsetDecoder(this, 1f, 1f);
        }

        @Override
        public CharsetEncoder newEncoder() {
            return null;
        }

        private final class FakeCharsetDecoder extends CharsetDecoder {

            private FakeCharsetDecoder(Charset cs, float averageCharsPerByte, float maxCharsPerByte) {
                super(cs, averageCharsPerByte, maxCharsPerByte);
            }

            @Override
            protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
                while (in.hasRemaining()) {
                    if (out.remaining() >= 2) {
                        byte b = in.get();
                        for (int i = 0; i < num; i++) {
                            out.put((char) b);
                        }
                    } else {
                        return CoderResult.OVERFLOW;
                    }
                }
                return CoderResult.UNDERFLOW;
            }
        }
    }

    private static final class AutoCloseAppender implements Appendable, AutoCloseable {

        private int err = 0;

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            return null;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            return null;
        }

        @Override
        public Appendable append(char c) throws IOException {
            switch (err) {
                case 1:
                    throw new IOException();
                case 2:
                    throw new IllegalStateException();
                default:
                    return null;
            }
        }

        @Override
        public void close() throws Exception {
            switch (err) {
                case 1:
                    throw new IOException();
                case 2:
                    throw new IllegalStateException();
                default:
            }
        }
    }

    private static final class OnlyAppender implements Appendable {

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            return null;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            return null;
        }

        @Override
        public Appendable append(char c) throws IOException {
            return null;
        }
    }
}
