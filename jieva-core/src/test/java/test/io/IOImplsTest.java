package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.io.AbstractWriter;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.JieAssert;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class IOImplsTest {

    @Test
    public void testInputStream() throws Exception {
        testInputStream(64);
        testInputStream(66);
        testInputStream(256);

        // error
        expectThrows(NullPointerException.class, () -> JieIO.newInputStream(null, 2, +1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newInputStream(new byte[0], 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newInputStream(new byte[0], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newInputStream(new byte[0], 2, -1));
    }

    private void testInputStream(int dataSize) throws Exception {
        {
            // bytes
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testInputStream(JieIO.newInputStream(data), data, true);
            data = JieRandom.fill(new byte[dataSize + 12]);
            testInputStream(
                JieIO.newInputStream(data, 6, dataSize),
                Arrays.copyOfRange(data, 6, data.length - 6),
                true
            );
        }
        {
            // buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            InputStream bufferIn = JieIO.newInputStream(buffer);
            testInputStream(bufferIn, data, true);
        }
        {
            // file
            // Path path = Paths.get("src", "test", "resources", "io", "input.test");
            // RandomAccessFile raf = new FakeRandomFile(path.toFile(), "r", source);
            byte[] data = JieRandom.fill(new byte[dataSize + 6]);
            RandomAccessFile raf = new FakeFile("r", data);
            InputStream rafIn = JieIO.newInputStream(raf, 6);
            testInputStream(rafIn, Arrays.copyOfRange(data, 6, data.length), true);
            expectThrows(IORuntimeException.class, () -> rafIn.mark(66));
        }
        {
            // chars
            char[] chars = JieRandom.fill(new char[dataSize], '0', '9');
            byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
            InputStream charsIn = JieIO.newInputStream(new CharArrayReader(chars));
            testInputStream(charsIn, charBytes, false);
            expectThrows(IOException.class, charsIn::read);
            // chinese: '\u4e00' - '\u9fff'
            chars = JieRandom.fill(new char[dataSize], '\u4e00', '\u4e01');
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            charsIn = JieIO.newInputStream(new CharArrayReader(chars));
            testInputStream(charsIn, charBytes, false);
            expectThrows(IOException.class, charsIn::read);
            // emoji: "\uD83D\uDD1E"
            for (int i = 0; i < chars.length; i += 2) {
                chars[i] = '\uD83D';
                chars[i + 1] = '\uDD1E';
            }
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            charsIn = JieIO.newInputStream(new CharArrayReader(chars));
            testInputStream(charsIn, charBytes, false);
            expectThrows(IOException.class, charsIn::read);
            // error: U+DD88
            // Arrays.fill(chars, '\uDD88');
            // charsIn = JieIO.newInputStream(new CharArrayReader(chars));
            // expectThrows(IOException.class, charsIn::read);
            // error
            charsIn = JieIO.newInputStream(new CharArrayReader(chars), new ErrorCharset());
            expectThrows(IOException.class, charsIn::read);
        }
    }

    private void testInputStream(InputStream in, byte[] data, boolean available) throws Exception {
        assertEquals(in.read(new byte[10], 0, 0), 0);
        assertEquals(in.read(new byte[0]), 0);
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(-1), 0);
        expectThrows(IndexOutOfBoundsException.class, () -> in.read(new byte[10], 2, -1));
        expectThrows(IndexOutOfBoundsException.class, () -> in.read(new byte[10], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> in.read(new byte[1], 0, 2));

        {
            // mark/reset
            expectThrows(IOException.class, in::reset);
            in.mark(0);
            if (in.markSupported()) {
                in.reset();
            } else {
                expectThrows(IOException.class, in::reset);
            }
        }

        if (data.length == 0) {
            testEndInputStream(in);
            in.close();
            in.mark(0);
            return;
        }

        int hasRead = 0;

        {
            // read()
            if (available) {
                assertEquals(in.available(), data.length - hasRead);
            } else {
                assertTrue(in.available() <= data.length - hasRead && in.available() >= 0);
            }
            if (in.markSupported()) {
                in.mark(3);
            }
            assertEquals((byte) in.read(), data[0]);
            assertEquals((byte) in.read(), data[1]);
            assertEquals((byte) in.read(), data[2]);
            if (in.markSupported()) {
                in.reset();
                assertEquals((byte) in.read(), data[0]);
                assertEquals((byte) in.read(), data[1]);
                assertEquals((byte) in.read(), data[2]);
            }
            hasRead += 3;
        }
        {
            // read(byte[])
            if (available) {
                assertEquals(in.available(), data.length - hasRead);
            } else {
                assertTrue(in.available() <= data.length - hasRead && in.available() >= 0);
            }
            byte[] dst = new byte[13];
            if (in.markSupported()) {
                in.mark(dst.length);
            }
            assertEquals(in.read(dst), dst.length);
            assertEquals(dst, Arrays.copyOfRange(data, hasRead, hasRead + dst.length));
            if (in.markSupported()) {
                in.reset();
                dst = new byte[13];
                assertEquals(in.read(dst), dst.length);
                assertEquals(dst, Arrays.copyOfRange(data, hasRead, hasRead + dst.length));
            }
            hasRead += dst.length;
        }
        {
            // read(byte[], int, int)
            if (available) {
                assertEquals(in.available(), data.length - hasRead);
            } else {
                assertTrue(in.available() <= data.length - hasRead && in.available() >= 0);
            }
            int readSize = 6;
            byte[] dst = new byte[readSize + 4];
            if (in.markSupported()) {
                in.mark(readSize);
            }
            assertEquals(in.read(dst, 2, readSize), readSize);
            assertEquals(
                Arrays.copyOfRange(dst, 2, 2 + readSize),
                Arrays.copyOfRange(data, hasRead, hasRead + readSize)
            );
            if (in.markSupported()) {
                in.reset();
                dst = new byte[readSize + 4];
                assertEquals(in.read(dst, 2, readSize), readSize);
                assertEquals(
                    Arrays.copyOfRange(dst, 2, 2 + readSize),
                    Arrays.copyOfRange(data, hasRead, hasRead + readSize)
                );
            }
            hasRead += readSize;
        }
        {
            // skip
            if (available) {
                assertEquals(in.available(), data.length - hasRead);
            } else {
                assertTrue(in.available() <= data.length - hasRead && in.available() >= 0);
            }
            int skip = 11;
            if (in.markSupported()) {
                in.mark(skip);
            }
            assertEquals(in.skip(0), 0);
            assertEquals(in.skip(-1), 0);
            assertEquals(in.skip(skip), skip);
            if (in.markSupported()) {
                in.reset();
                assertEquals(in.skip(0), 0);
                assertEquals(in.skip(-1), 0);
                assertEquals(in.skip(skip), skip);
            }
            hasRead += skip;
        }
        {
            // read all
            if (available) {
                assertEquals(in.available(), data.length - hasRead);
            } else {
                assertTrue(in.available() <= data.length - hasRead && in.available() >= 0);
            }
            if (in.markSupported()) {
                in.mark(data.length - hasRead);
            }
            byte[] remaining = JieIO.read(in);
            assertEquals(
                remaining,
                Arrays.copyOfRange(data, hasRead, data.length)
            );
            if (in.markSupported()) {
                in.reset();
                byte[] remaining2 = JieIO.read(in);
                assertEquals(
                    remaining2,
                    Arrays.copyOfRange(data, hasRead, data.length)
                );
                assertEquals(
                    remaining,
                    remaining2
                );
            }
            hasRead += remaining.length;
        }
        assertEquals(hasRead, data.length);
        testEndInputStream(in);

        // close
        in.close();
    }

    private void testEndInputStream(InputStream in) throws Exception {
        assertEquals(in.available(), 0);
        assertEquals(in.read(), -1);
        assertEquals(in.read(new byte[10]), -1);
        assertEquals(in.read(new byte[10], 0, 1), -1);
        assertEquals(in.skip(999), 0);
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(-1), 0);
    }

    @Test
    public void testOutput() throws Exception {
        testOutput(1024);
        testOutput(60);
        testOutput(34);

        // error
        expectThrows(NullPointerException.class, () -> JieIO.newOutputStream(null, 2, +1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newOutputStream(new byte[0], 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newOutputStream(new byte[0], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newOutputStream(new byte[0], 2, -1));
    }

    private void testOutput(int sourceSize) throws Exception {
        byte[] source = JieRandom.fill(new byte[sourceSize]);

        // bytes
        OutputStream bytesOut = JieIO.newOutputStream(source);
        byte[] data = JieRandom.fill(new byte[source.length]);
        testOutput(bytesOut, data);
        assertEquals(data, source);
        Arrays.fill(source, (byte) 1);
        data = JieRandom.fill(new byte[source.length - 10]);
        OutputStream byteOut2 = JieIO.newOutputStream(source, 2, data.length);
        testOutput(byteOut2, data);
        assertEquals(data, Arrays.copyOfRange(source, 2, data.length + 2));

        // buffer
        OutputStream bufferOut = JieIO.newOutputStream(ByteBuffer.wrap(source));
        data = JieRandom.fill(new byte[source.length]);
        testOutput(bufferOut, data);
        assertEquals(data, source);

        // file
        Path path = Paths.get("src", "test", "resources", "io", "input.test");
        RandomAccessFile raf = new FakeRandomFile(path.toFile(), "r", source);
        OutputStream rafOut = JieIO.newOutputStream(raf, 6);
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
        OutputStream charsOut = JieIO.newOutputStream(JieIO.newWriter(dest));
        testOutput(charsOut, charBytes);
        assertEquals(chars, dest);
        OutputStream charsOut1 = charsOut;
        expectThrows(IOException.class, () -> charsOut1.write(1));
        // chinese: '\u4e00' - '\u9fff'
        chars = JieRandom.fill(new char[sourceSize], '\u4e00', '\u4e01');
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsOut = JieIO.newOutputStream(JieIO.newWriter(dest));
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
        charsOut = JieIO.newOutputStream(JieIO.newWriter(dest));
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
        charsOut = JieIO.newOutputStream(JieIO.newWriter(fakeDest), new FakeCharset(2));
        testOutput(charsOut, fakeBytes);
        assertEquals(fakeChars, fakeDest);
        OutputStream charsOut4 = charsOut;
        expectThrows(IOException.class, () -> charsOut4.write(1));
        // error: 0xC1
        byte[] errBytes = new byte[sourceSize];
        Arrays.fill(errBytes, (byte) 0xC1);
        charsOut = JieIO.newOutputStream(JieIO.newWriter(dest));
        OutputStream charsOut5 = charsOut;
        expectThrows(IOException.class, () -> charsOut5.write(errBytes));
        // StringBuilder
        StringBuilder sb = new StringBuilder();
        charsOut = JieIO.newOutputStream(sb);
        charsOut.write("中文".getBytes(JieChars.UTF_8));
        charsOut.flush();
        assertEquals(sb.toString(), "中文");
        StringBuffer sbuf = new StringBuffer();
        charsOut = JieIO.newOutputStream(sbuf);
        charsOut.write("中文".getBytes(JieChars.UTF_8));
        charsOut.flush();
        assertEquals(sbuf.toString(), "中文");
        CharBuffer cb = CharBuffer.allocate(10);
        charsOut = JieIO.newOutputStream((Appendable) cb);
        charsOut.write("中文".getBytes(JieChars.UTF_8));
        charsOut.flush();
        cb.flip();
        assertEquals(cb.toString(), "中文");
        // appender
        AutoCloseAppender aa = new AutoCloseAppender();
        charsOut = JieIO.newOutputStream(aa);
        charsOut.write(1);
        charsOut.close();
        charsOut = JieIO.newOutputStream(aa);
        aa.err = 1;
        OutputStream charsOut6 = charsOut;
        expectThrows(IOException.class, () -> charsOut6.write(1));
        expectThrows(IOException.class, charsOut::close);
        charsOut = JieIO.newOutputStream(aa);
        aa.err = 2;
        OutputStream charsOut7 = charsOut;
        expectThrows(IOException.class, () -> charsOut7.write(1));
        expectThrows(IOException.class, charsOut::close);
        OnlyAppender oa = new OnlyAppender();
        charsOut = JieIO.newOutputStream(oa);
        charsOut.close();

        // error
        FakeRandomFile.SEEK_ERR = true;
        expectThrows(IORuntimeException.class, () -> JieIO.newOutputStream(raf, 6));
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
        testReader(234);

        // error
        expectThrows(NullPointerException.class, () -> JieIO.newReader((char[]) null, 2, +1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newReader(new char[0], 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newReader(new char[0], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newReader(new char[0], 2, -1));
    }

    private void testReader(int sourceSize) throws Exception {
        char[] source = JieRandom.fill(new char[sourceSize], '0', '9');

        // chars
        IOCases.testReader(JieIO.newReader(source), source);
        IOCases.testReader(
            JieIO.newReader(source, 2, source.length - 10),
            Arrays.copyOfRange(source, 2, source.length - 8)
        );
        assertTrue(JieIO.newReader(source).ready());

        // string
        IOCases.testReader(JieIO.newReader(new String(source)), source);

        // buffer
        CharBuffer buffer = CharBuffer.wrap(source);
        Reader bufferIn = JieIO.newReader(buffer);
        IOCases.testReader(bufferIn, source);
        Class<?> bufferInClass = bufferIn.getClass();
        Method read0 = bufferInClass.getDeclaredMethod("read0");
        JieAssert.invokeThrows(IOException.class, read0, bufferIn);
        read0 = bufferInClass.getDeclaredMethod("read0", char[].class, int.class, int.class);
        JieAssert.invokeThrows(IOException.class, read0, bufferIn, null, 0, 0);
        read0 = bufferInClass.getDeclaredMethod("skip0", int.class);
        JieAssert.invokeThrows(IOException.class, read0, bufferIn, 99);

        // bytes
        char[] chars = JieRandom.fill(new char[sourceSize], '0', '9');
        byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
        Reader charsIn = JieIO.newReader(JieIO.newInputStream(charBytes));
        IOCases.testReader(charsIn, chars);
        expectThrows(IOException.class, charsIn::read);
        // chinese: '\u4e00' - '\u9fff'
        chars = JieRandom.fill(new char[sourceSize], '\u4e00', '\u4e01');
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsIn = JieIO.newReader(JieIO.newInputStream(charBytes));
        IOCases.testReader(charsIn, chars);
        expectThrows(IOException.class, charsIn::read);
        // emoji: "\uD83D\uDD1E"
        for (int i = 0; i < chars.length; i += 2) {
            chars[i] = '\uD83D';
            chars[i + 1] = '\uDD1E';
        }
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        charsIn = JieIO.newReader(JieIO.newInputStream(charBytes));
        IOCases.testReader(charsIn, chars);
        expectThrows(IOException.class, charsIn::read);
        // fake charset
        byte[] fakeBytes = JieRandom.fill(new byte[sourceSize]);
        char[] fakeChars = new char[fakeBytes.length * 3];
        for (int i = 0; i < fakeBytes.length; i++) {
            fakeChars[i * 3] = (char) fakeBytes[i];
            fakeChars[i * 3 + 1] = (char) fakeBytes[i];
            fakeChars[i * 3 + 2] = (char) fakeBytes[i];
        }
        charsIn = JieIO.newReader(JieIO.newInputStream(fakeBytes), new FakeCharset(3));
        IOCases.testReader(charsIn, fakeChars);
        expectThrows(IOException.class, charsIn::read);
        // error: 0xC1
        Arrays.fill(charBytes, (byte) 0xC1);
        charsIn = JieIO.newReader(JieIO.newInputStream(charBytes));
        expectThrows(IOException.class, charsIn::read);
        // ready
        charsIn = JieIO.newReader(new InputStream() {
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
        charsIn = JieIO.newReader(new InputStream() {
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

    @Test
    public void testWriter() throws Exception {
        testWriter(1024);
        testWriter(60);
        testWriter(34);

        // error
        expectThrows(NullPointerException.class, () -> JieIO.newWriter(null, 2, +1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newWriter(new char[0], 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newWriter(new char[0], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newWriter(new char[0], 2, -1));
        expectThrows(NullPointerException.class, () -> JieIO.newWriter(new char[100]).write((String) null, 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newWriter(new char[100]).write("", 2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newWriter(new char[100]).write("", -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> JieIO.newWriter(new char[100]).write("", 2, -1));
    }

    private void testWriter(int sourceSize) throws Exception {
        char[] source = JieRandom.fill(new char[sourceSize], '0', '9');

        // chars
        Writer charsOut = JieIO.newWriter(source);
        char[] data = JieRandom.fill(new char[source.length]);
        testWriter(charsOut, data);
        assertEquals(data, source);
        Arrays.fill(source, (char) 1);
        data = JieRandom.fill(new char[source.length - 10]);
        Writer charsOut2 = JieIO.newWriter(source, 2, data.length);
        testWriter(charsOut2, data);
        assertEquals(data, Arrays.copyOfRange(source, 2, data.length + 2));
        char[] nullChars = new char[4];
        Writer nullWriter = JieIO.newWriter(nullChars);
        nullWriter.append(null);
        nullWriter.write("null", 0, 0);
        assertEquals(nullChars, "null".toCharArray());

        // buffer
        Writer bufferOut = JieIO.newWriter(CharBuffer.wrap(source));
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
        Writer bytesOut = JieIO.newWriter(JieIO.newOutputStream(dest));
        testWriter(bytesOut, chars);
        assertEquals(charBytes, dest);
        Writer bytesOut1 = bytesOut;
        expectThrows(IOException.class, () -> bytesOut1.write(1));
        // chinese: '\u4e00' - '\u9fff'
        chars = JieRandom.fill(new char[sourceSize], '\u4e00', '\u4e01');
        charBytes = new String(chars).getBytes(JieChars.UTF_8);
        dest = new byte[charBytes.length];
        bytesOut = JieIO.newWriter(JieIO.newOutputStream(dest));
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
        bytesOut = JieIO.newWriter(JieIO.newOutputStream(dest));
        testWriter(bytesOut, chars);
        assertEquals(charBytes, dest);
        Writer bytesOut3 = bytesOut;
        expectThrows(IOException.class, () -> bytesOut3.write(1));
        // error: U+DD88
        dest = new byte[charBytes.length];
        bytesOut = JieIO.newWriter(JieIO.newOutputStream(dest));
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

    @Test
    public void testEmptyAndNull() throws Exception {
        assertEquals(JieIO.emptyInputStream().read(), -1);
        assertEquals(JieIO.emptyReader().read(), -1);
        JieIO.emptyReader().close();
        assertSame(JieIO.emptyInputStream(), JieIO.emptyInputStream());
        assertSame(JieIO.emptyReader(), JieIO.emptyReader());
        JieIO.nullOutputStream().write(JieRandom.fill(new byte[10086]));
        JieIO.nullOutputStream().close();
        JieIO.nullWriter().write(JieRandom.fill(new char[10086]));
        JieIO.nullWriter().close();
        assertSame(JieIO.nullOutputStream(), JieIO.nullOutputStream());
        assertSame(JieIO.nullWriter(), JieIO.nullWriter());
        JieIO.nullWriter().flush();
    }

    @Test
    public void testSpecial() throws Exception {
        assertEquals(JieIO.newInputStream(new byte[0]).skip(999), 0);
        assertEquals(JieIO.newInputStream(ByteBuffer.allocate(0)).skip(999), 0);
        char[] src = new char[]{9};
        char[] dst = new char[2];
        CharBuffer dstBuffer = CharBuffer.wrap(dst);
        assertEquals(JieIO.newReader(src).read(dstBuffer), 1);
        assertEquals(dst, new char[]{9, 0});
        assertEquals(JieIO.newReader(new char[0]).skip(999), 0);
    }

    private static final class FakeFile extends RandomAccessFile {

        private final byte[] data;
        private boolean closed = false;
        private ByteArrayInputStream in;

        public FakeFile(String mode, byte[] data) throws FileNotFoundException {
            super(ClassLoader.getSystemResource("io/fakeRaf.txt").getFile(), mode);
            this.data = data;
            in = new ByteArrayInputStream(data);
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return in.read(b, off, len);
        }

        @Override
        public int skipBytes(int n) throws IOException {
            return (int) in.skip(n);
        }

        @Override
        public long getFilePointer() throws IOException {
            if (closed) {
                throw new IOException();
            }
            return data.length - in.available();
        }

        @Override
        public void seek(long pos) throws IOException {
            in = new ByteArrayInputStream(data, (int) pos, data.length - (int) pos);
        }

        @Override
        public long length() throws IOException {
            return data.length;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.closed = true;
        }
    }

    private static final class ErrorCharset extends Charset {

        private ErrorCharset() {
            super("error", new String[0]);
        }

        @Override
        public boolean contains(Charset cs) {
            return false;
        }

        @Override
        public CharsetDecoder newDecoder() {
            return new CharsetDecoder(this, 1f, 1f) {
                @Override
                protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
                    return CoderResult.unmappableForLength(1);
                }
            };
        }

        @Override
        public CharsetEncoder newEncoder() {
            return new CharsetEncoder(this, 1f, 1f) {
                @Override
                protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
                    return CoderResult.unmappableForLength(1);
                }

                @Override
                public boolean isLegalReplacement(byte[] repl) {
                    return true;
                }
            };
        }
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
                in = JieIO.newInputStream(data, seek, data.length - seek);
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
                out = JieIO.newOutputStream(data, seek, data.length - seek);
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
