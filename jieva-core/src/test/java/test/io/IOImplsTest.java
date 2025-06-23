package test.io;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.io.DoReadReader;
import xyz.sunqian.common.io.DoReadStream;
import xyz.sunqian.common.io.DoWriteStream;
import xyz.sunqian.common.io.DoWriteWriter;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class IOImplsTest {

    @Test
    public void testInputStream() throws Exception {
        testInputStream(64);
        testInputStream(66);
        testInputStream(256);

        // empty
        testInputStream(JieIO.emptyInputStream(), new byte[0], true, false);

        // error
        RandomAccessFile raf = new FakeFile(new byte[0]);
        raf.close();
        expectThrows(IORuntimeException.class, () -> JieIO.newInputStream(raf, 0));
    }

    private void testInputStream(int dataSize) throws Exception {
        {
            // bytes
            byte[] data = JieRandom.fill(new byte[dataSize]);
            testInputStream(JieIO.newInputStream(data), data, true, false);
            data = JieRandom.fill(new byte[dataSize + 12]);
            testInputStream(
                JieIO.newInputStream(data, 6, dataSize),
                Arrays.copyOfRange(data, 6, data.length - 6),
                true, false
            );
        }
        {
            // buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            ByteBuffer buffer = ByteBuffer.wrap(data);
            InputStream bufferIn = JieIO.newInputStream(buffer);
            testInputStream(bufferIn, data, true, false);
        }
        {
            // file
            byte[] data = JieRandom.fill(new byte[dataSize + 6]);
            RandomAccessFile raf = new FakeFile(data);
            InputStream rafIn = JieIO.newInputStream(raf, 6);
            testInputStream(rafIn, Arrays.copyOfRange(data, 6, data.length), true, true);
            expectThrows(IORuntimeException.class, () -> rafIn.mark(66));
        }
        {
            // chars
            char[] chars = JieRandom.fill(new char[dataSize], '0', '9');
            byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
            InputStream charsIn = JieIO.newInputStream(new CharArrayReader(chars));
            testInputStream(charsIn, charBytes, false, false);
            expectThrows(IOException.class, charsIn::read);
            // chinese: '\u4e00' - '\u9fff'
            chars = JieRandom.fill(new char[dataSize], '\u4e00', '\u4e01');
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            charsIn = JieIO.newInputStream(new CharArrayReader(chars));
            testInputStream(charsIn, charBytes, false, false);
            expectThrows(IOException.class, charsIn::read);
            // emoji: "\uD83D\uDD1E"
            for (int i = 0; i < chars.length; i += 2) {
                chars[i] = '\uD83D';
                chars[i + 1] = '\uDD1E';
            }
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            charsIn = JieIO.newInputStream(new CharArrayReader(chars));
            testInputStream(charsIn, charBytes, false, false);
            expectThrows(IOException.class, charsIn::read);
            // error
            charsIn = JieIO.newInputStream(new CharArrayReader(chars), new ErrorCharset());
            expectThrows(IOException.class, charsIn::read);
            // error: U+DD88
            // Arrays.fill(chars, '\uDD88');
            // charsIn = JieIO.newInputStream(new CharArrayReader(chars));
            // expectThrows(IOException.class, charsIn::read);
        }
    }

    private void testInputStream(InputStream in, byte[] data, boolean available, boolean close) throws Exception {

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
            if (in.markSupported()) {
                in.mark(0);
            }
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
        in.close();
        if (close) {
            expectThrows(IOException.class, () -> in.read());
            expectThrows(IOException.class, () -> in.read(new byte[1]));
            expectThrows(IOException.class, () -> in.read(new byte[1], 0, 1));
            in.close();
        }
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
    public void testReader() throws Exception {
        testReader(128);
        testReader(256);
        testReader(1024);

        // empty
        testReader(JieIO.emptyReader(), new char[0], true, false);
    }

    private void testReader(int dataSize) throws Exception {
        {
            // chars
            char[] data = JieRandom.fill(new char[dataSize]);
            testReader(JieIO.newReader(data), data, true, false);
            data = JieRandom.fill(new char[dataSize + 12]);
            testReader(
                JieIO.newReader(data, 6, dataSize),
                Arrays.copyOfRange(data, 6, data.length - 6),
                true, false
            );
        }
        {
            // string
            char[] data = JieRandom.fill(new char[dataSize]);
            testReader(JieIO.newReader(new String(data)), data, true, false);
            data = JieRandom.fill(new char[dataSize + 12]);
            testReader(
                JieIO.newReader(new String(data), 6, data.length - 6),
                Arrays.copyOfRange(data, 6, data.length - 6),
                true, false
            );
        }
        {
            // buffer
            char[] data = JieRandom.fill(new char[dataSize]);
            CharBuffer buffer = CharBuffer.wrap(data);
            Reader bufferIn = JieIO.newReader(buffer);
            testReader(bufferIn, data, true, false);
        }
        {
            // bytes
            char[] chars = JieRandom.fill(new char[dataSize], '0', '9');
            byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
            Reader charsIn = JieIO.newReader(JieIO.newInputStream(charBytes));
            testReader(charsIn, chars, false, false);
            expectThrows(IOException.class, charsIn::read);
            // chinese: '\u4e00' - '\u9fff'
            chars = JieRandom.fill(new char[dataSize], '\u4e00', '\u4e01');
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            charsIn = JieIO.newReader(JieIO.newInputStream(charBytes));
            testReader(charsIn, chars, false, false);
            expectThrows(IOException.class, charsIn::read);
            // emoji: "\uD83D\uDD1E"
            for (int i = 0; i < chars.length; i += 2) {
                chars[i] = '\uD83D';
                chars[i + 1] = '\uDD1E';
            }
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            charsIn = JieIO.newReader(JieIO.newInputStream(charBytes));
            testReader(charsIn, chars, false, false);
            expectThrows(IOException.class, charsIn::read);
            // error: 0xC1
            charsIn = JieIO.newReader(JieIO.newInputStream(charBytes), new ErrorCharset());
            expectThrows(IOException.class, charsIn::read);
            // 1 byte -> 3 char
            byte[] fakeBytes = JieRandom.fill(new byte[dataSize]);
            char[] fakeChars = new char[fakeBytes.length * 3];
            for (int i = 0; i < fakeBytes.length; i++) {
                fakeChars[i * 3] = (char) fakeBytes[i];
                fakeChars[i * 3 + 1] = (char) fakeBytes[i];
                fakeChars[i * 3 + 2] = (char) fakeBytes[i];
            }
            charsIn = JieIO.newReader(JieIO.newInputStream(fakeBytes), new ByteToNCharCharset(3));
            testReader(charsIn, fakeChars, false, false);
            expectThrows(IOException.class, charsIn::read);
        }
    }

    private void testReader(Reader in, char[] data, boolean ready, boolean close) throws Exception {

        assertEquals(in.read(new char[10], 0, 0), 0);
        assertEquals(in.read(new char[0]), 0);
        if (data.length > 0) {
            assertEquals(in.read(CharBuffer.allocate(0)), 0);
        }
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(-1), 0);
        expectThrows(IndexOutOfBoundsException.class, () -> in.read(new char[10], 2, -1));
        expectThrows(IndexOutOfBoundsException.class, () -> in.read(new char[10], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> in.read(new char[1], 0, 2));

        if (ready) {
            assertTrue(in.ready());
        } else {
            in.ready();
        }

        {
            // mark/reset
            expectThrows(IOException.class, in::reset);
            if (in.markSupported()) {
                in.mark(0);
                in.reset();
            } else {
                expectThrows(IOException.class, in::reset);
            }
        }

        if (data.length == 0) {
            testEndReader(in);
            in.close();
            if (in.markSupported()) {
                in.mark(0);
            }
            return;
        }

        int hasRead = 0;

        {
            // read()
            if (in.markSupported()) {
                in.mark(3);
            }
            assertEquals((char) in.read(), data[0]);
            assertEquals((char) in.read(), data[1]);
            assertEquals((char) in.read(), data[2]);
            if (in.markSupported()) {
                in.reset();
                assertEquals((char) in.read(), data[0]);
                assertEquals((char) in.read(), data[1]);
                assertEquals((char) in.read(), data[2]);
            }
            hasRead += 3;
        }
        {
            // read(byte[])
            char[] dst = new char[13];
            if (in.markSupported()) {
                in.mark(dst.length);
            }
            assertEquals(in.read(dst), dst.length);
            assertEquals(dst, Arrays.copyOfRange(data, hasRead, hasRead + dst.length));
            if (in.markSupported()) {
                in.reset();
                dst = new char[13];
                assertEquals(in.read(dst), dst.length);
                assertEquals(dst, Arrays.copyOfRange(data, hasRead, hasRead + dst.length));
            }
            hasRead += dst.length;
        }
        {
            // read(byte[], int, int)
            int readSize = 6;
            char[] dst = new char[readSize + 4];
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
                dst = new char[readSize + 4];
                assertEquals(in.read(dst, 2, readSize), readSize);
                assertEquals(
                    Arrays.copyOfRange(dst, 2, 2 + readSize),
                    Arrays.copyOfRange(data, hasRead, hasRead + readSize)
                );
            }
            hasRead += readSize;
        }
        {
            // read(CharBuffer)
            CharBuffer dst = CharBuffer.allocate(6);
            if (in.markSupported()) {
                in.mark(dst.capacity());
            }
            assertEquals(in.read(dst), dst.capacity());
            assertEquals(dst.position(), dst.capacity());
            assertEquals(dst.array(), Arrays.copyOfRange(data, hasRead, hasRead + dst.capacity()));
            if (in.markSupported()) {
                in.reset();
                dst = CharBuffer.allocate(6);
                assertEquals(in.read(dst), dst.capacity());
                assertEquals(dst.position(), dst.capacity());
                assertEquals(dst.array(), Arrays.copyOfRange(data, hasRead, hasRead + dst.capacity()));
            }
            hasRead += dst.capacity();
        }
        {
            // skip
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
            if (in.markSupported()) {
                in.mark(data.length - hasRead);
            }
            char[] remaining = JieIO.read(in);
            assertEquals(
                remaining,
                Arrays.copyOfRange(data, hasRead, data.length)
            );
            if (in.markSupported()) {
                in.reset();
                char[] remaining2 = JieIO.read(in);
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
        testEndReader(in);

        // close
        in.close();
        in.close();
        if (close) {
            expectThrows(IOException.class, () -> in.read());
            expectThrows(IOException.class, () -> in.read(new char[1]));
            expectThrows(IOException.class, () -> in.read(new char[1], 0, 1));
            expectThrows(IOException.class, () -> in.read(CharBuffer.allocate(1)));
            in.close();
        }
    }

    private void testEndReader(Reader in) throws Exception {
        assertEquals(in.read(), -1);
        assertEquals(in.read(new char[10]), -1);
        assertEquals(in.read(new char[10], 0, 1), -1);
        assertEquals(in.read(CharBuffer.allocate(1)), -1);
        assertEquals(in.skip(999), 0);
        assertEquals(in.skip(0), 0);
        assertEquals(in.skip(-1), 0);
    }

    @Test
    public void testOutputStream() throws Exception {
        testOutputStream(64);
        testOutputStream(128);
        testOutputStream(256);

        // null
        testOutputStream(JieIO.nullOutputStream(), new byte[1024], false, false);

        // error
        RandomAccessFile raf = new FakeFile(new byte[0]);
        raf.close();
        expectThrows(IORuntimeException.class, () -> JieIO.newOutputStream(raf, 0));
    }

    private void testOutputStream(int dataSize) throws Exception {
        {
            // bytes
            byte[] data = JieRandom.fill(new byte[dataSize]);
            byte[] dst = new byte[data.length];
            OutputStream out = JieIO.newOutputStream(dst);
            testOutputStream(out, data, true, false);
            assertEquals(dst, data);
            dst = new byte[data.length + 10];
            out = JieIO.newOutputStream(dst, 5, data.length);
            testOutputStream(out, data, true, false);
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length - 5), data);
        }
        {
            // buffer
            byte[] data = JieRandom.fill(new byte[dataSize]);
            ByteBuffer dst = ByteBuffer.allocate(data.length);
            OutputStream out = JieIO.newOutputStream(dst);
            testOutputStream(out, data, true, false);
            assertEquals(dst.flip(), ByteBuffer.wrap(data));
        }
        {
            // file
            byte[] data = JieRandom.fill(new byte[dataSize]);
            BytesBuilder builder = new BytesBuilder();
            RandomAccessFile raf = new FakeFile(builder);
            OutputStream out = JieIO.newOutputStream(raf, 6);
            testOutputStream(out, data, false, true);
            assertEquals(builder.toByteArray(), data);
        }
        {
            // chars
            CharsBuilder builder = new CharsBuilder();
            char[] chars = JieRandom.fill(new char[dataSize], '0', '9');
            byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
            OutputStream out = JieIO.newOutputStream(builder);
            testOutputStream(out, charBytes, false, true);
            assertEquals(builder.toCharArray(), chars);
            // chinese: '\u4e00' - '\u9fff'
            builder.reset();
            chars = JieRandom.fill(new char[dataSize], '\u4e00', '\u4e01');
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            out = JieIO.newOutputStream(builder);
            testOutputStream(out, charBytes, false, true);
            assertEquals(builder.toCharArray(), chars);
            // emoji: "\uD83D\uDD1E"
            builder.reset();
            for (int i = 0; i < chars.length; i += 2) {
                chars[i] = '\uD83D';
                chars[i + 1] = '\uDD1E';
            }
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            out = JieIO.newOutputStream(builder);
            testOutputStream(out, charBytes, false, true);
            assertEquals(builder.toCharArray(), chars);
            // fake charset
            builder.reset();
            byte[] fakeBytes = JieRandom.fill(new byte[dataSize]);
            char[] fakeChars = new char[fakeBytes.length * 2];
            for (int i = 0; i < fakeBytes.length; i++) {
                fakeChars[i * 2] = (char) fakeBytes[i];
                fakeChars[i * 2 + 1] = (char) fakeBytes[i];
            }
            out = JieIO.newOutputStream(builder, new ByteToNCharCharset(2));
            testOutputStream(out, fakeBytes, false, true);
            assertEquals(builder.toCharArray(), fakeChars);
            // error: 0xC1
            builder.reset();
            OutputStream errOut = JieIO.newOutputStream(builder, new ErrorCharset());
            expectThrows(IOException.class, () -> errOut.write(new byte[10]));
        }
    }

    private void testOutputStream(OutputStream out, byte[] data, boolean limited, boolean close) throws Exception {

        expectThrows(IndexOutOfBoundsException.class, () -> out.write(new byte[10], 2, -1));
        expectThrows(IndexOutOfBoundsException.class, () -> out.write(new byte[10], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> out.write(new byte[1], 0, 2));

        int hasWritten = 0;

        {
            // write
            out.write(data[0]);
            out.write(data[1]);
            out.write(data[2]);
            out.flush();
            hasWritten += 3;
        }
        {
            // write(byte[])
            out.write(new byte[0]);
            out.write(Arrays.copyOfRange(data, hasWritten, hasWritten + 13));
            out.flush();
            hasWritten += 13;
        }
        {
            // write(byte[], int, int)
            out.write(new byte[1], 0, 0);
            byte[] subData = Arrays.copyOfRange(data, hasWritten - 5, data.length);
            out.write(subData, 5, data.length - hasWritten);
            out.flush();
        }

        if (limited) {
            expectThrows(IOException.class, () -> out.write(1));
            expectThrows(IOException.class, () -> out.write(new byte[1]));
            expectThrows(IOException.class, () -> out.write(new byte[1], 0, 1));
        }

        out.close();
        out.close();

        if (close) {
            expectThrows(IOException.class, () -> out.write(1));
            expectThrows(IOException.class, () -> out.write(new byte[1]));
            expectThrows(IOException.class, () -> out.write(new byte[1], 0, 1));
            expectThrows(IOException.class, out::flush);
            out.close();
        }
    }

    @Test
    public void testWriter() throws Exception {
        testWriter(64);
        testWriter(128);
        testWriter(256);

        // null
        testWriter(JieIO.nullWriter(), new char[1024], false, false);
    }

    private void testWriter(int dataSize) throws Exception {
        {
            // chars
            char[] data = JieRandom.fill(new char[dataSize]);
            char[] dst = new char[data.length];
            Writer out = JieIO.newWriter(dst);
            testWriter(out, data, true, false);
            assertEquals(dst, data);
            dst = new char[data.length + 10];
            out = JieIO.newWriter(dst, 5, data.length);
            testWriter(out, data, true, false);
            assertEquals(Arrays.copyOfRange(dst, 5, dst.length - 5), data);
        }
        {
            // buffer
            char[] data = JieRandom.fill(new char[dataSize]);
            CharBuffer dst = CharBuffer.allocate(data.length);
            Writer out = JieIO.newWriter(dst);
            testWriter(out, data, true, false);
            assertEquals(dst.flip(), CharBuffer.wrap(data));
        }
        {
            // bytes
            BytesBuilder builder = new BytesBuilder();
            char[] chars = JieRandom.fill(new char[dataSize], '0', '9');
            byte[] charBytes = new String(chars).getBytes(JieChars.UTF_8);
            Writer out = JieIO.newWriter(builder);
            testWriter(out, chars, false, true);
            assertEquals(builder.toByteArray(), charBytes);
            // chinese: '\u4e00' - '\u9fff'
            builder.reset();
            chars = JieRandom.fill(new char[dataSize], '\u4e00', '\u4e01');
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            out = JieIO.newWriter(builder);
            testWriter(out, chars, false, true);
            assertEquals(builder.toByteArray(), charBytes);
            // emoji: "\uD83D\uDD1E"
            builder.reset();
            for (int i = 0; i < chars.length; i += 2) {
                chars[i] = '\uD83D';
                chars[i + 1] = '\uDD1E';
            }
            charBytes = new String(chars).getBytes(JieChars.UTF_8);
            out = JieIO.newWriter(builder);
            testWriter(out, chars, false, true);
            assertEquals(builder.toByteArray(), charBytes);
            // error: U+DD88
            builder.reset();
            Writer errOut = JieIO.newWriter(builder, new ErrorCharset());
            expectThrows(IOException.class, () -> errOut.write(new char[10]));
        }
    }

    private void testWriter(Writer out, char[] data, boolean limited, boolean close) throws Exception {

        expectThrows(IndexOutOfBoundsException.class, () -> out.write(new char[10], 2, -1));
        expectThrows(IndexOutOfBoundsException.class, () -> out.write(new char[10], -2, 1));
        expectThrows(IndexOutOfBoundsException.class, () -> out.write(new char[1], 0, 2));

        int hasWritten = 0;

        {
            // write
            out.write(data[0]);
            out.write(data[1]);
            out.append(data[2]);
            out.append(data[3]);
            out.flush();
            hasWritten += 4;
        }
        {
            // write(char[])
            out.write(new char[0]);
            out.write(Arrays.copyOfRange(data, hasWritten, hasWritten + 13));
            out.flush();
            hasWritten += 13;
        }
        {
            // write(char[], int, int)
            out.write(new char[1], 0, 0);
            char[] subData = Arrays.copyOfRange(data, hasWritten - 5, hasWritten + 5);
            out.write(subData, 5, 5);
            out.flush();
            hasWritten += 5;
        }
        {
            // write(String)
            out.write("");
            out.write(new String(Arrays.copyOfRange(data, hasWritten, hasWritten + 3)));
            out.flush();
            hasWritten += 3;
        }
        {
            // write(String, int, int)
            out.write("", 0, 0);
            char[] subData = Arrays.copyOfRange(data, hasWritten - 5, hasWritten + 3);
            out.write(new String(subData), 5, 3);
            out.flush();
            hasWritten += 3;
        }
        {
            // append(CharSequence)
            out.append("");
            out.append(new String(Arrays.copyOfRange(data, hasWritten, hasWritten + 3)));
            out.flush();
            hasWritten += 3;
        }
        {
            // append(CharSequence, int, int)
            out.append("", 0, 0);
            char[] subData = Arrays.copyOfRange(data, hasWritten - 5, data.length);
            out.append(new String(subData), 5, subData.length);
            out.flush();
        }

        if (limited) {
            expectThrows(IOException.class, () -> out.write(1));
            expectThrows(IOException.class, () -> out.write(new char[1]));
            expectThrows(IOException.class, () -> out.write(new char[1], 0, 1));
            expectThrows(IOException.class, () -> out.append('1'));
            expectThrows(IOException.class, () -> out.append("1"));
            expectThrows(IOException.class, () -> out.append("1", 0, 1));
        }

        out.close();
        out.close();

        if (close) {
            expectThrows(IOException.class, () -> out.write(1));
            expectThrows(IOException.class, () -> out.write(new char[1]));
            expectThrows(IOException.class, () -> out.write(new char[1], 0, 1));
            expectThrows(IOException.class, () -> out.append('1'));
            expectThrows(IOException.class, () -> out.append("1"));
            expectThrows(IOException.class, () -> out.append("1", 0, 1));
            expectThrows(IOException.class, out::flush);
            out.close();
        }
    }

    @Test
    public void testSpecial() throws Exception {
        assertSame(JieIO.emptyInputStream(), JieIO.emptyInputStream());
        assertSame(JieIO.emptyReader(), JieIO.emptyReader());
        assertSame(JieIO.nullOutputStream(), JieIO.nullOutputStream());
        assertSame(JieIO.nullWriter(), JieIO.nullWriter());
    }

    @Test
    public void testDoImpls() throws Exception {
        {
            class In extends DoReadStream {

                @Override
                protected int doRead(byte @Nonnull [] b, int off, int len) {
                    return 8;
                }

                @Override
                public int read() {
                    return 0;
                }
            }
            assertEquals(new In().read(new byte[1]), 8);
            assertEquals(new In().read(new byte[1], 0, 1), 8);
            expectThrows(IndexOutOfBoundsException.class, () -> new In().read(new byte[1], 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () -> new In().read(new byte[1], -1, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> new In().read(new byte[1], 0, 2));
        }
        {
            class In extends DoReadReader {

                @Override
                protected int doRead(char @Nonnull [] b, int off, int len) {
                    return 8;
                }

                @Override
                public int read() {
                    return 0;
                }

                @Override
                public void close() {
                }
            }
            assertEquals(new In().read(new char[1]), 8);
            assertEquals(new In().read(new char[1], 0, 1), 8);
            expectThrows(IndexOutOfBoundsException.class, () -> new In().read(new char[1], 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () -> new In().read(new char[1], -1, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> new In().read(new char[1], 0, 2));
        }
        {
            IntVar v = IntVar.of(0);
            class Out extends DoWriteStream {

                @Override
                protected void doWrite(byte @Nonnull [] b, int off, int len) {
                    v.set(8);
                }

                @Override
                public void write(int b) {
                }
            }
            new Out().write(new byte[1]);
            assertEquals(v.get(), 8);
            v.set(0);
            new Out().write(new byte[1], 0, 1);
            assertEquals(v.get(), 8);
            expectThrows(IndexOutOfBoundsException.class, () -> new Out().write(new byte[1], 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () -> new Out().write(new byte[1], -1, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> new Out().write(new byte[1], 0, 2));
        }
        {
            IntVar v = IntVar.of(0);
            class Out extends DoWriteWriter {

                @Override
                protected void doWrite(char @Nonnull [] b, int off, int len) {
                    v.set(8);
                }

                @Override
                protected void doWrite(@Nonnull String str, int off, int len) {
                    v.set(8);
                }

                @Override
                public void write(int b) {
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() {
                }
            }
            new Out().write(new char[1]);
            assertEquals(v.get(), 8);
            v.set(0);
            new Out().write(new char[1], 0, 1);
            assertEquals(v.get(), 8);
            expectThrows(IndexOutOfBoundsException.class, () -> new Out().write(new char[1], 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () -> new Out().write(new char[1], -1, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> new Out().write(new char[1], 0, 2));
            v.set(0);
            new Out().write("1");
            assertEquals(v.get(), 8);
            v.set(0);
            new Out().write("1", 0, 1);
            assertEquals(v.get(), 8);
            expectThrows(IndexOutOfBoundsException.class, () -> new Out().write("1", 0, -1));
            expectThrows(IndexOutOfBoundsException.class, () -> new Out().write("1", -1, 1));
            expectThrows(IndexOutOfBoundsException.class, () -> new Out().write("1", 0, 2));
        }
    }

    private static final class FakeFile extends RandomAccessFile {

        private final byte[] data;
        private final OutputStream out;
        private boolean closed = false;
        private ByteArrayInputStream in;

        public FakeFile(byte[] data) throws FileNotFoundException {
            super(ClassLoader.getSystemResource("io/fakeRaf.txt").getFile(), "r");
            this.data = data;
            in = new ByteArrayInputStream(data);
            this.out = null;
        }

        public FakeFile(OutputStream out) throws FileNotFoundException {
            super(ClassLoader.getSystemResource("io/fakeRaf.txt").getFile(), "r");
            this.data = null;
            in = null;
            this.out = out;
        }

        @Override
        public int read() throws IOException {
            if (closed) {
                throw new IOException();
            }
            return in.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (closed) {
                throw new IOException();
            }
            return in.read(b, off, len);
        }

        @Override
        public int skipBytes(int n) throws IOException {
            if (closed) {
                throw new IOException();
            }
            return (int) in.skip(n);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (closed) {
                throw new IOException();
            }
            out.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            if (closed) {
                throw new IOException();
            }
            out.write(b);
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
            if (closed) {
                throw new IOException();
            }
            if (data != null) {
                in = new ByteArrayInputStream(data, (int) pos, data.length - (int) pos);
            }
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

    private static final class ByteToNCharCharset extends Charset {

        private final int byteToNChar;

        private ByteToNCharCharset(int byteToNChar) {
            super("ByteToNChar", new String[0]);
            this.byteToNChar = byteToNChar;
        }

        @Override
        public boolean contains(Charset cs) {
            return false;
        }

        @Override
        public CharsetDecoder newDecoder() {
            return new CharsetDecoder(
                this, 1f, 1f
            ) {
                @Override
                protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
                    while (in.hasRemaining()) {
                        if (out.remaining() >= byteToNChar) {
                            byte b = in.get();
                            for (int i = 0; i < byteToNChar; i++) {
                                out.put((char) b);
                            }
                        } else {
                            return CoderResult.OVERFLOW;
                        }
                    }
                    return CoderResult.UNDERFLOW;
                }
            };
        }

        @Override
        public CharsetEncoder newEncoder() {
            return new CharsetEncoder(this, 1f, 1f) {
                @Override
                protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
                    while (in.remaining() >= byteToNChar) {
                        if (out.hasRemaining()) {
                            char c = in.get();
                            for (int i = 0; i < byteToNChar - 1; i++) {
                                in.get();
                            }
                            out.put((byte) c);
                        } else {
                            return CoderResult.OVERFLOW;
                        }
                    }
                    return CoderResult.UNDERFLOW;
                }

                @Override
                public boolean isLegalReplacement(byte[] repl) {
                    return true;
                }
            };
        }
    }
}
