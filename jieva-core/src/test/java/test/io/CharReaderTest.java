package test.io;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.io.CharReader;
import xyz.sunqian.common.io.CharSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.ErrorAppender;
import xyz.sunqian.test.ReadOps;
import xyz.sunqian.test.TestReader;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class CharReaderTest {

    @Test
    public void testRead() {
        testRead0(0, 1);
        testRead0(1, 1);
        testRead0(32, 1);
        testRead0(32, 16);
        testRead0(32, 32);
        testRead0(32, 64);
        testRead0(128, 16);
        testRead0(128, 33);
        testRead0(128, 111);
        testRead0(128, 128);
        testRead0(128, 129);
        testRead0(128, 1024);

        // error
        {
            TestReader tr = new TestReader(new CharArrayReader(new char[10]));
            tr.setNextOperation(ReadOps.THROW, 99);
            expectThrows(IORuntimeException.class, () -> CharReader.from(tr).skip(100));
        }
    }

    private void testRead0(int dataSize, int readSize) {
        {
            // reader
            char[] data = JieRandom.fill(new char[dataSize]);
            testRead0(CharReader.from(new CharArrayReader(data)), data, readSize, false);
            testSkip0(CharReader.from(new CharArrayReader(data)), data, readSize);
            TestReader tr = new TestReader(new CharArrayReader(data));
            tr.setNextOperation(ReadOps.READ_ZERO);
            testSkip0(CharReader.from(tr), data, readSize);
        }
        {
            // char array
            char[] data = JieRandom.fill(new char[dataSize]);
            testRead0(CharReader.from(data), data, readSize, true);
            testSkip0(CharReader.from(data), data, readSize);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testRead0(
                CharReader.from(dataPadding, 33, data.length),
                Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                readSize, true
            );
            testSkip0(CharReader.from(dataPadding, 33, data.length), data, readSize);
        }
        {
            // char sequence
            char[] data = JieRandom.fill(new char[dataSize]);
            String dataStr = new String(data);
            testRead0(CharReader.from(dataStr), data, readSize, true);
            testSkip0(CharReader.from(data), data, readSize);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            String dataStrPadding = new String(dataPadding);
            testRead0(
                CharReader.from(dataStrPadding, 33, 33 + data.length),
                Arrays.copyOfRange(dataPadding, 33, 33 + data.length),
                readSize, true
            );
            testSkip0(CharReader.from(dataStrPadding, 33, 33 + data.length), data, readSize);
        }
        {
            // buffer
            char[] data = JieRandom.fill(new char[dataSize]);
            testRead0(CharReader.from(CharBuffer.wrap(data)), data, readSize, true);
            testSkip0(CharReader.from(CharBuffer.wrap(data)), data, readSize);
        }
    }

    private void testRead0(CharReader reader, char[] data, int readSize, boolean preKnown) {
        assertFalse(reader.read(0).end());
        assertFalse(reader.read(0).data().hasRemaining());
        int hasRead = 0;
        while (hasRead < data.length) {
            CharSegment segment = reader.read(readSize);
            int actualLen = Math.min(readSize, data.length - hasRead);
            assertEquals(
                JieBuffer.copyContent(segment.data()),
                Arrays.copyOfRange(data, hasRead, hasRead + actualLen)
            );
            hasRead += actualLen;
            if (hasRead >= data.length) {
                if (actualLen < readSize) {
                    assertTrue(segment.end());
                } else {
                    assertEquals(segment.end(), preKnown);
                }
            }
        }
        assertFalse(reader.read(0).end());
        assertFalse(reader.read(0).data().hasRemaining());
        assertTrue(reader.read(1).end());
        assertFalse(reader.read(1).data().hasRemaining());
    }

    private void testSkip0(CharReader reader, char[] data, int readSize) {
        assertEquals(reader.skip(0), 0);
        int hasRead = 0;
        while (hasRead < data.length) {
            long skipped = reader.skip(readSize);
            int actualLen = Math.min(readSize, data.length - hasRead);
            assertEquals(skipped, actualLen);
            hasRead += actualLen;
        }
        assertEquals(reader.skip(0), 0);
        assertEquals(reader.skip(1), 0);
    }

    @Test
    public void testReadTo() {
        testReadTo0(0);
        testReadTo0(1);
        testReadTo0(2);
        testReadTo0(3);
        testReadTo0(64);
        testReadTo0(128);
        testReadTo0(256);
        testReadTo0(512);
        testReadTo0(1024);
        testReadTo0(2048);

        {
            // special: nio
            class NioInput extends Reader {

                private int count = 1;

                @Override
                public int read() throws IOException {
                    if (count > 0) {
                        count--;
                        return 66;
                    }
                    return -1;
                }

                @Override
                public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
                    int c = 0;
                    for (int i = 0; i < len; i++) {
                        int r = read();
                        if (r < 0) {
                            return c == 0 ? -1 : c;
                        }
                        cbuf[i + off] = (char) r;
                        c++;
                    }
                    return c;
                }

                @Override
                public void close() {
                }
            }
            char[] dst = new char[2];
            assertEquals(CharReader.from(new NioInput()).readTo(dst), 1);
            assertEquals(dst[0], 66);
        }
    }

    private void testReadTo0(int dataSize) {
        char[] data = JieRandom.fill(new char[dataSize]);
        char[] dataPadding = new char[data.length + 66];
        System.arraycopy(data, 0, dataPadding, 33, data.length);
        {
            // input stream
            testReadTo0(CharReader.from(new CharArrayReader(data)), data);
        }
        {
            // char array
            testReadTo0(CharReader.from(data), data);
            testReadTo0(CharReader.from(dataPadding, 33, data.length), data);
        }
        {
            // char sequence
            testReadTo0(CharReader.from(new String(data)), data);
            testReadTo0(CharReader.from(new String(dataPadding), 33, 33 + data.length), data);
        }
        {
            // char buffer
            testReadTo0(CharReader.from(CharBuffer.wrap(data)), data);
            testReadTo0(CharReader.from(JieBuffer.directBuffer(data)), data);
        }
    }

    private void testReadTo0(CharReader reader, char[] data) {
        reader.mark();
        {
            // to writer
            CharsBuilder builder = new CharsBuilder();
            assertEquals(reader.readTo(builder), data.length == 0 ? -1 : data.length);
            assertEquals(builder.toCharArray(), data);
            assertEquals(reader.readTo(builder), -1);
            assertEquals(builder.toCharArray(), data);
            assertEquals(reader.readTo(builder, 0), 0);
            assertEquals(builder.toCharArray(), data);
            reader.reset();
            builder.reset();
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    assertEquals(reader.readTo(builder, length), Math.min(length, endIndex - startIndex));
                    assertEquals(builder.toCharArray(), Arrays.copyOfRange(data, startIndex, endIndex));
                    builder.reset();
                    if (endIndex >= data.length) {
                        break;
                    }
                    length *= 2;
                    startIndex = endIndex;
                }
            }
            reader.reset();
            if (data.length > 0) {
                expectThrows(IORuntimeException.class, () -> reader.readTo(new ErrorAppender()));
                reader.reset();
                expectThrows(IORuntimeException.class, () -> reader.readTo(new ErrorAppender(), 5));
                reader.reset();
                expectThrows(IllegalArgumentException.class, () -> reader.readTo(builder, -5));
                reader.reset();
            }
        }
        {
            // to char array
            char[] dst = new char[data.length];
            assertEquals(reader.readTo(dst), data.length);
            assertEquals(dst, data);
            assertEquals(reader.readTo(dst), dst.length == 0 ? 0 : -1);
            assertEquals(dst, data);
            reader.reset();
            assertEquals(reader.readTo(new char[0]), 0);
            assertEquals(reader.readTo(dst, 0, 0), 0);
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                dst = new char[data.length];
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    assertEquals(
                        reader.readTo(dst, startIndex, Math.min(length, endIndex - startIndex)),
                        Math.min(length, endIndex - startIndex)
                    );
                    if (endIndex >= data.length) {
                        break;
                    }
                    length *= 2;
                    startIndex = endIndex;
                }
                assertEquals(dst, data);
            }
            reader.reset();
        }
        {
            // to heap buffer
            CharBuffer dst = CharBuffer.allocate(data.length);
            assertEquals(reader.readTo(dst), data.length);
            dst.flip();
            assertEquals(JieBuffer.copyContent(dst), data);
            reader.reset();
            dst = CharBuffer.allocate(data.length);
            assertEquals(reader.readTo(dst), data.length);
            assertEquals(reader.readTo(dst), dst.remaining() == 0 ? 0 : -1);
            assertEquals(reader.readTo(CharBuffer.allocate(1)), -1);
            dst.flip();
            assertEquals(JieBuffer.copyContent(dst), data);
            reader.reset();
            assertEquals(reader.readTo(CharBuffer.allocate(0)), 0);
            assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
            int length = 1;
            int startIndex = 0;
            if (data.length > 0) {
                dst = CharBuffer.allocate(data.length);
                while (true) {
                    int endIndex = Math.min(data.length, startIndex + length);
                    int actualLen = Math.min(length, endIndex - startIndex);
                    CharBuffer slice = JieBuffer.slice(dst, actualLen);
                    assertEquals(
                        reader.readTo(slice),
                        Math.min(length, endIndex - startIndex)
                    );
                    dst.position(dst.position() + actualLen);
                    if (endIndex >= data.length) {
                        break;
                    }
                    length *= 2;
                    startIndex = endIndex;
                }
                dst.flip();
                assertEquals(JieBuffer.read(dst), data);
            }
            reader.reset();
            // error
            if (data.length > 0) {
                expectThrows(IndexOutOfBoundsException.class, () -> reader.readTo(new char[0], 0, -1));
                expectThrows(IndexOutOfBoundsException.class, () -> reader.readTo(new char[0], 0, 1));
                reader.reset();
            }
        }
        // {
        //     // to direct buffer
        //     CharBuffer dst = JieBuffer.directBuffer(data.length * 2);
        //     assertEquals(reader.readTo(dst), data.length);
        //     dst.flip();
        //     assertEquals(JieBuffer.read(dst), data);
        //     reader.reset();
        //     dst = JieBuffer.directBuffer(data.length * 2);
        //     assertEquals(reader.readTo(dst), data.length);
        //     assertEquals(reader.readTo(dst), dst.remaining() == 0 ? 0 : -1);
        //     assertEquals(reader.readTo(JieBuffer.directBuffer(2)), -1);
        //     dst.flip();
        //     assertEquals(JieBuffer.read(dst), data);
        //     reader.reset();
        //     assertEquals(reader.readTo(CharBuffer.allocate(0)), 0);
        //     assertEquals(reader.readTo(CharBuffer.allocate(1), 0), 0);
        //     int length = 1;
        //     int startIndex = 0;
        //     if (data.length > 0) {
        //         dst = JieBuffer.directBuffer(data.length * 2);
        //         while (true) {
        //             int endIndex = Math.min(data.length, startIndex + length);
        //             int actualLen = Math.min(length, endIndex - startIndex);
        //             CharBuffer slice = JieBuffer.slice(dst, actualLen);
        //             assertEquals(
        //                 reader.readTo(slice),
        //                 Math.min(length, endIndex - startIndex)
        //             );
        //             dst.position(dst.position() + actualLen);
        //             if (endIndex >= data.length) {
        //                 break;
        //             }
        //             length *= 2;
        //             startIndex = endIndex;
        //         }
        //         dst.flip();
        //         assertEquals(JieBuffer.read(dst), data);
        //     }
        //     reader.reset();
        //     if (data.length > 0) {
        //         expectThrows(IllegalArgumentException.class, () -> reader.readTo(CharBuffer.allocate(0), -1));
        //         reader.reset();
        //     }
        // }
    }

    @Test
    public void testShare() {
        int dataSize = 1024;
        {
            // reader
            char[] data = JieRandom.fill(new char[dataSize]);
            testShare(CharReader.from(new CharArrayReader(data)), CharBuffer.wrap(data), false, false);
        }
        {
            // char array
            char[] data = JieRandom.fill(new char[dataSize]);
            testShare(CharReader.from(data), CharBuffer.wrap(data), true, true);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            testShare(
                CharReader.from(dataPadding, 33, data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                true, true
            );
        }
        {
            // char sequence
            char[] data = JieRandom.fill(new char[dataSize]);
            CharSequence dataStr = JieString.asChars(data);
            testShare(CharReader.from(dataStr), CharBuffer.wrap(data), false, true);
            char[] dataPadding = new char[data.length + 66];
            System.arraycopy(data, 0, dataPadding, 33, data.length);
            CharSequence dataStrPadding = JieString.asChars(dataPadding);
            testShare(
                CharReader.from(dataStrPadding, 33, 33 + data.length),
                CharBuffer.wrap(dataPadding, 33, data.length),
                false, true
            );
        }
        {
            // char buffer
            char[] data = JieRandom.fill(new char[dataSize]);
            testShare(CharReader.from(CharBuffer.wrap(data)), CharBuffer.wrap(data), true, true);
            CharBuffer direct = JieBuffer.directBuffer(data);
            testShare(CharReader.from(direct), direct.slice(), true, true);
        }
    }

    private void testShare(
        CharReader reader, CharBuffer data, boolean sharedReaderToData, boolean sharedDataToReader
    ) {
        CharSegment segment = reader.read(data.remaining() * 2);
        CharBuffer readBuf = segment.data();
        assertEquals(readBuf, data);
        assertTrue(segment.end());
        if (sharedReaderToData) {
            for (int i = 0; i < readBuf.remaining(); i++) {
                readBuf.put(i, (char) (readBuf.get(i) + 1));
            }
            assertEquals(readBuf, data);
        }
        if (sharedDataToReader) {
            for (int i = 0; i < data.remaining(); i++) {
                data.put(i, (char) (data.get(i) + 100));
            }
            assertEquals(data, readBuf);
        }
    }

    @Test
    public void testSegment() throws Exception {
        char[] chars = JieRandom.fill(new char[64]);
        CharReader reader = CharReader.from(chars);
        CharSegment segment = reader.read(chars.length * 2);
        assertSame(segment.data().array(), chars);
        assertTrue(segment.end());
        CharSegment segmentCopy = segment.clone();
        assertEquals(segmentCopy.data(), CharBuffer.wrap(chars));
        assertTrue(segmentCopy.end());
        assertNotSame(segmentCopy.data().array(), chars);
        assertEquals(segment.copyCharArray(), chars);
        assertNotSame(segment.copyCharArray(), chars);
        assertEquals(segment.toCharArray(), chars);
        assertEquals(segment.toCharArray(), new char[0]);
    }

    // @Test
    // public void testSpecial() throws Exception {
    //     char[] chars = JieArray.fill(new char[64], Constants.FILL_CHAR);
    //     CharArrayReader in = new CharArrayReader(chars);
    //     TestReader testIn = new TestReader(in);
    //     {
    //         // NIO tests
    //         CharReader reader = CharReader.from(testIn);
    //         testIn.setNextOperation(ReadOps.READ_ZERO, 10);
    //         CharSegment s0 = reader.read(chars.length);
    //         assertEquals(s0.data(), CharBuffer.wrap(chars));
    //         assertFalse(s0.end());
    //         s0 = reader.read(1);
    //         assertTrue(s0.end());
    //         assertEquals(reader.skip(66), 0);
    //         in.reset();
    //         CharReader reader2 = CharReader.from(testIn);
    //         testIn.setNextOperation(ReadOps.READ_ZERO);
    //         assertEquals(reader2.skip(66), chars.length);
    //         // TestInputStream testIn2 = new TestInputStream(new CharArrayInputStream(new char[2]));
    //         // testIn2.setNextOperation(ReadOps.READ_ZERO);
    //         // assertEquals(CharReader.from(testIn2).skip(66), 2);
    //     }
    //     {
    //         // Exception tests
    //         CharReader reader = CharReader.from(testIn);
    //         in.reset();
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, () -> reader.read(66));
    //         expectThrows(IllegalArgumentException.class, () -> reader.read(-66));
    //         // expectThrows(IllegalArgumentException.class, () -> reader.withReadLimit(-66));
    //         expectThrows(IllegalArgumentException.class, () -> reader.skip(-66));
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, () -> reader.readTo(CharBuffer.allocate(1)));
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, () -> reader.readTo(new char[1]));
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, () -> reader.skip(66));
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, reader::mark);
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, reader::reset);
    //         testIn.setNextOperation(ReadOps.THROW);
    //         expectThrows(IORuntimeException.class, reader::close);
    //     }
    //     {
    //         // for segment
    //         char[] charsCopy = Arrays.copyOf(chars, chars.length);
    //         CharReader reader = CharReader.from(charsCopy);
    //         CharSegment segment = reader.read(charsCopy.length * 2);
    //         assertSame(segment.data().array(), charsCopy);
    //         assertTrue(segment.end());
    //         CharSegment segmentCopy = segment.clone();
    //         assertEquals(segmentCopy.data(), CharBuffer.wrap(charsCopy));
    //         assertTrue(segmentCopy.end());
    //         assertNotSame(segmentCopy.data().array(), charsCopy);
    //     }
    //     {
    //         // special mark/reset
    //         TestReader tin = new TestReader(new CharArrayReader(new char[2]));
    //         CharReader reader = CharReader.from(tin);//.withReadLimit(1);
    //         assertTrue(reader.markSupported());
    //         tin.markSupported(false);
    //         assertFalse(reader.markSupported());
    //         reader.mark();
    //         reader.reset();
    //     }
    //     {
    //         // close
    //         CharReader.from(JieIO.newReader(new char[0])).close();
    //         CharReader.from(new char[0]).close();
    //         CharReader.from(CharBuffer.allocate(0)).close();
    //         CharReader.from("").close();
    //     }
    // }
}
