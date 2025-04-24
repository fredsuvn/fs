package test.base.chars;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieMath;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.chars.CharEncoder;
import xyz.sunqian.common.base.chars.CharProcessor;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.base.value.BooleanVar;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.JieTestException;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;
import static xyz.sunqian.common.base.chars.CharEncoder.withBuffering;
import static xyz.sunqian.common.base.chars.CharEncoder.withRounding;
import static xyz.sunqian.test.JieTest.reflectThrows;
import static xyz.sunqian.test.MaterialBox.copyBuffer;
import static xyz.sunqian.test.MaterialBox.copyDirect;
import static xyz.sunqian.test.MaterialBox.copyHeap;
import static xyz.sunqian.test.MaterialBox.copyPadding;

public class CharProcessorTest {

    @Test
    public void testProcessing() throws Exception {
        testProcessing(0, JieIO.BUFFER_SIZE, -1);
        testProcessing(666, JieIO.BUFFER_SIZE, -1);
        testProcessing(0, 67, -1);
        testProcessing(666, 67, -1);
        testProcessing(666, 1, -1);
        testProcessing(100, 10, -1);
        testProcessing(666, JieIO.BUFFER_SIZE, -1);
        testProcessing(0, 67, 667);
        testProcessing(666, 67, 667);
        testProcessing(666, 1, 667);
        testProcessing(100, 10, 101);
        testProcessing(222, 33, 55);
        testProcessing(100, 10, 0);
        testProcessing(100, 10, 100);
        testProcessing(6666, 99, 77777);
        testProcessing(0, 99, 77777);
    }

    private void testProcessing(int totalSize, int blockSize, int readLimit) throws Exception {
        int offset = 22;
        String str = new String(JieRandom.fill(new char[totalSize], 'a', 'z'));
        char[] chars = str.toCharArray();

        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            bytes[i * 2] = 0;
            bytes[i * 2 + 1] = (byte) chars[i];
        }
        CharBuffer dirBuffer = copyDirect(chars);

        {
            // stream -> stream
            CharArrayReader in = new CharArrayReader(chars);
            CharsBuilder out = new CharsBuilder();
            long readNum = CharProcessor.from(in).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // string -> stream
            CharsBuilder out = new CharsBuilder();
            long readNum = CharProcessor.from(str).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // direct -> stream
            CharBuffer dirInBuffer = copyBuffer(dirBuffer);
            CharsBuilder outBuilder = new CharsBuilder();
            long readNum = CharProcessor.from(dirInBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuilder);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = copyBuffer(dirBuffer);
            outBuilder.reset();
            readNum = CharProcessor.from(dirInBuffer).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> copyBuffer(s)).writeTo(outBuilder);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = copyBuffer(dirBuffer);
            StringWriter sw = new StringWriter();
            readNum = CharProcessor.from(dirInBuffer).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> copyBuffer(s)).writeTo(sw);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), sw.toString());
        }

        {
            // stream -> char[]
            char[] outChars = new char[chars.length];
            CharArrayReader in = new CharArrayReader(chars);
            in.mark(0);
            long readNum = CharProcessor.from(in).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length + offset * 2];
            in.reset();
            readNum = CharProcessor.from(in).readBlockSize(blockSize).writeTo(outChars, offset, chars.length);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(Arrays.copyOfRange(outChars, offset, offset + chars.length)));
        }

        {
            // stream -> buffer
            CharBuffer outBuffer = copyBuffer(dirBuffer);
            CharArrayReader in = new CharArrayReader(chars);
            long readNum = CharProcessor.from(in).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outChars = JieBuffer.read(outBuffer);
            assertEquals(str, new String(outChars));
        }

        {
            // char[] -> stream
            CharsBuilder out = new CharsBuilder();
            long readNum = CharProcessor.from(chars).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            out.reset();
            readNum = CharProcessor.from(chars).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieBuffer.read(s))).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // char[] -> char[]
            char[] outChars = new char[chars.length];
            long readNum = CharProcessor.from(chars).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            readNum = CharProcessor.from(chars).readBlockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
            char[] inChars = new char[chars.length + offset * 2];
            outChars = new char[chars.length];
            System.arraycopy(chars, 0, inChars, offset, chars.length);
            readNum = CharProcessor.from(inChars, offset, chars.length).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharProcessor.from(chars, 0, chars.length)
                .readBlockSize(blockSize).writeTo(outChars, 0, outChars.length);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            if (bytes.length > 0) {
                readNum = CharProcessor.from(chars, 0, chars.length - 1)
                    .readBlockSize(blockSize).writeTo(outChars, 0, outChars.length - 1);
                assertEquals(readNum, chars.length - 1);
                assertEquals(str.substring(0, str.length() - 1),
                    new String(Arrays.copyOfRange(outChars, 0, outChars.length - 1)));
            }
        }

        {
            // char[] -> buffer
            CharBuffer outBuffer = copyDirect(chars);
            long readNum = CharProcessor.from(chars).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            assertEquals(str, new String(JieBuffer.read(outBuffer)));
            outBuffer = copyDirect(chars);
            readNum = CharProcessor.from(chars).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(chars.length, readLimit));
            outBuffer.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieBuffer.read(outBuffer), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // char[] -> appender
            CharsBuilder appender = new CharsBuilder();
            long readNum = CharProcessor.from(chars).readBlockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.reset();
            readNum = CharProcessor.from(chars).readBlockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }

        {
            // char[] -> non-writer-appender
            NonWriterAppender appender = new NonWriterAppender();
            long readNum = CharProcessor.from(chars).readBlockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.reset();
            readNum = CharProcessor.from(chars).readBlockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }

        {
            // buffer -> stream
            CharBuffer inBuffer = copyHeap(chars);
            inBuffer.mark();
            CharsBuilder out = new CharsBuilder();
            long readNum = CharProcessor.from(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            inBuffer.reset();
            out.reset();
            readNum = CharProcessor.from(inBuffer).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieBuffer.read(s))).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            CharBuffer arrayIn = copyPadding(chars);
            CharBuffer arrayOut = copyPadding(new char[chars.length]);
            readNum = CharProcessor.from(arrayIn).readBlockSize(blockSize).readLimit(readLimit).writeTo(arrayOut);
            assertEquals(readNum, getLength(chars.length, readLimit));
            arrayOut.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieBuffer.read(arrayOut), 0, getLength(chars.length, readLimit))
            );
            arrayIn.flip();
            arrayOut.flip();
            readNum = CharProcessor.from(arrayIn).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieBuffer.read(s))).writeTo(arrayOut);
            assertEquals(readNum, getLength(chars.length, readLimit));
            arrayOut.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieBuffer.read(arrayOut), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // buffer -> char[]
            CharBuffer inBuffer = copyHeap(chars);
            inBuffer.mark();
            char[] outChars = new char[chars.length];
            long readNum = CharProcessor.from(inBuffer).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            inBuffer.reset();
            outChars = new char[chars.length];
            readNum = CharProcessor.from(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
        }

        {
            // buffer -> appender
            CharBuffer inBuffer = copyPadding(chars);
            inBuffer.mark();
            CharsBuilder appender = new CharsBuilder();
            long readNum = CharProcessor.from(inBuffer).readBlockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            inBuffer.reset();
            appender.reset();
            readNum = CharProcessor.from(copyDirect(inBuffer)).readBlockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }

        {
            // buffer -> buffer
            CharBuffer inBuffer = copyPadding(chars);
            inBuffer.mark();
            CharBuffer outBuffer = copyBuffer(dirBuffer);
            long readNum = CharProcessor.from(inBuffer).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outBytes = JieBuffer.read(outBuffer);
            assertEquals(str, new String(outBytes));
        }

        {
            // charSeq -> char[]
            char[] outChars = new char[chars.length];
            long readNum = CharProcessor.from(str).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharProcessor.from(str).readBlockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
            outChars = new char[chars.length];
            readNum = CharProcessor.from(JieString.asChars(str.toCharArray())).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = CharProcessor.from(JieString.asChars(str.toCharArray()))
                .readBlockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
        }

        {
            // charSeq -> appender
            CharsBuilder appender = new CharsBuilder();
            long readNum = CharProcessor.from(str).readBlockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.reset();
            readNum = CharProcessor.from(str).readBlockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
            appender.reset();
            readNum = CharProcessor.from(str).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieBuffer.read(s))).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }

        {
            // any -> null
            long[] counter = {0};
            long readNum = CharProcessor.from(new char[totalSize])
                .readBlockSize(blockSize)
                .readLimit(readLimit)
                .encoder(((data, end) -> {
                    counter[0] += data.remaining();
                    return data;
                }))
                .process();
            assertEquals(readNum, getLength(totalSize, readLimit));
            assertEquals(counter[0], getLength(totalSize, readLimit));
        }
    }

    private int getLength(int length, int readLimit) {
        if (readLimit < 0) {
            return length;
        }
        return Math.min(length, readLimit);
    }

    @Test
    public void testEncoder() throws Exception {
        testEncoder(0, 1);
        testEncoder(1, 1);
        testEncoder(1, 10);
        testEncoder(99, 9);
        testEncoder(99, 990);
        testEncoder(1024, 77);
        testEncoder(1024 * 1024, 777);
        testEncoder(1024 * 1024, 1024);

        {
            // error
            Throwable[] ts = new Throwable[1];
            try {
                CharProcessor.from(new char[100]).encoder((data, end) -> {
                    throw new JieTestException("haha");
                }).writeTo(new char[100]);
            } catch (ProcessingException e) {
                ts[0] = e;
            }
            assertEquals(ts[0].getCause().getClass(), JieTestException.class);
            assertEquals(ts[0].getCause().getMessage(), "haha");
        }
        {
            // special
            expectThrows(IllegalArgumentException.class, () ->
                CharProcessor.from(new char[0]).encoder(-1, CharEncoder.emptyEncoder()));
            expectThrows(IllegalArgumentException.class, () ->
                CharProcessor.from(new char[0]).encoder(0, CharEncoder.emptyEncoder()));
        }
    }

    private void testEncoder(int dataSize, int readBlockSize) throws Exception {
        {
            // from char array
            char[] src = JieArray.fill(new char[dataSize], (char) 6);
            CharsBuilder dst = new CharsBuilder();
            IntVar ec = IntVar.of(0);
            CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder((d, e) -> {
                    CharsBuilder dst0 = new CharsBuilder();
                    while (d.hasRemaining()) {
                        char b = d.get();
                        dst0.append(b);
                        dst0.append(b);
                    }
                    d.flip();
                    while (d.hasRemaining()) {
                        d.put((char) 9);
                    }
                    if (e) {
                        ec.incrementAndGet();
                    }
                    return dst0.toCharBuffer();
                })
                .encoder((d, e) -> d)
                .writeTo(dst);
            assertEquals(dst.toCharArray(), JieArray.fill(new char[dataSize * 2], (char) 6));
            assertEquals(src, JieArray.fill(new char[dataSize], (char) 9));
            assertEquals(ec.get(), 1);
        }
        {
            // from heap char buffer
            char[] srcChars = JieArray.fill(new char[dataSize], (char) 6);
            CharBuffer src = CharBuffer.wrap(srcChars);
            CharsBuilder dst = new CharsBuilder();
            IntVar ec = IntVar.of(0);
            CharProcessor.from(src).readBlockSize(readBlockSize).encoder((d, e) -> {
                CharsBuilder dst0 = new CharsBuilder();
                while (d.hasRemaining()) {
                    char b = d.get();
                    dst0.append(b);
                    dst0.append(b);
                }
                d.flip();
                while (d.hasRemaining()) {
                    d.put((char) 9);
                }
                if (e) {
                    ec.incrementAndGet();
                }
                return dst0.toCharBuffer();
            }).writeTo(dst);
            assertEquals(dst.toCharArray(), JieArray.fill(new char[dataSize * 2], (char) 6));
            src.flip();
            assertEquals(JieBuffer.read(src), JieArray.fill(new char[dataSize], (char) 9));
            assertEquals(ec.get(), 1);
        }
        {
            // from direct char buffer to non-writer-appender
            char[] srcChars = JieArray.fill(new char[dataSize], (char) 6);
            CharBuffer src = CharBuffer.wrap(srcChars);
            NonWriterAppender dst = new NonWriterAppender();
            IntVar ec = IntVar.of(0);
            CharProcessor.from(src).readBlockSize(readBlockSize).encoder((d, e) -> {
                CharsBuilder dst0 = new CharsBuilder();
                while (d.hasRemaining()) {
                    char b = d.get();
                    dst0.append(b);
                    dst0.append(b);
                }
                d.flip();
                while (d.hasRemaining()) {
                    d.put((char) 9);
                }
                if (e) {
                    ec.incrementAndGet();
                }
                return copyDirect(dst0.toCharBuffer());
            }).writeTo(dst);
            assertEquals(dst.toCharArray(), JieArray.fill(new char[dataSize * 2], (char) 6));
            src.flip();
            assertEquals(JieBuffer.read(src), JieArray.fill(new char[dataSize], (char) 9));
            assertEquals(ec.get(), 1);
        }
        {
            // from stream
            char[] srcChars = JieArray.fill(new char[dataSize], (char) 6);
            CharArrayReader src = new CharArrayReader(srcChars);
            CharsBuilder dst = new CharsBuilder();
            IntVar ec = IntVar.of(0);
            CharProcessor.from(src).readBlockSize(readBlockSize).encoder((d, e) -> {
                CharsBuilder dst0 = new CharsBuilder();
                while (d.hasRemaining()) {
                    char b = d.get();
                    dst0.append(b);
                    dst0.append(b);
                }
                d.flip();
                while (d.hasRemaining()) {
                    d.put((char) 9);
                }
                if (e) {
                    ec.incrementAndGet();
                }
                return dst0.toCharBuffer();
            }).writeTo(dst);
            assertEquals(dst.toCharArray(), JieArray.fill(new char[dataSize * 2], (char) 6));
            src.reset();
            assertEquals(JieIO.read(src), JieArray.fill(new char[dataSize], (char) 6));
            assertEquals(ec.get(), 1);
        }
        {
            // from char sequence
            char[] srcChars = JieArray.fill(new char[dataSize], (char) 6);
            CharSequence src = new String(srcChars);
            CharsBuilder dst = new CharsBuilder();
            IntVar ec = IntVar.of(0);
            CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder((d, e) -> {
                    CharsBuilder dst0 = new CharsBuilder();
                    while (d.hasRemaining()) {
                        char b = d.get();
                        dst0.append(b);
                        dst0.append(b);
                    }
                    if (e) {
                        ec.incrementAndGet();
                    }
                    return dst0.toCharBuffer();
                })
                .encoder((d, e) -> d)
                .writeTo(dst);
            assertEquals(dst.toCharArray(), JieArray.fill(new char[dataSize * 2], (char) 6));
            assertEquals(srcChars, JieArray.fill(new char[dataSize], (char) 6));
            assertEquals(ec.get(), 1);
        }
        {
            // test null
            char[] src = JieArray.fill(new char[dataSize], (char) 6);
            CharsBuilder dst = new CharsBuilder();
            IntVar ec = IntVar.of(0);
            CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder((d, e) -> {
                    CharsBuilder dst0 = new CharsBuilder();
                    while (d.hasRemaining()) {
                        char b = d.get();
                        dst0.append(b);
                        dst0.append(b);
                    }
                    d.flip();
                    while (d.hasRemaining()) {
                        d.put((char) 9);
                    }
                    if (e) {
                        ec.incrementAndGet();
                    }
                    return dst0.toCharBuffer();
                })
                .encoder((d, e) -> null)
                .writeTo(dst);
            assertEquals(dst.toCharArray(), new char[0]);
            assertEquals(src, JieArray.fill(new char[dataSize], (char) 9));
            assertEquals(ec.get(), 1);
            Arrays.fill(src, (char) 6);
            CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder((d, e) -> null)
                .encoder((d, e) -> {
                    CharsBuilder dst0 = new CharsBuilder();
                    while (d.hasRemaining()) {
                        char b = d.get();
                        dst0.append(b);
                        dst0.append(b);
                    }
                    d.flip();
                    while (d.hasRemaining()) {
                        d.put((char) 9);
                    }
                    if (e) {
                        ec.incrementAndGet();
                    }
                    return dst0.toCharBuffer();
                })
                .writeTo(dst);
            assertEquals(dst.toCharArray(), new char[0]);
            assertEquals(src, JieArray.fill(new char[dataSize], (char) 6));
            assertEquals(ec.get(), 1);
        }
    }

    @Test
    public void testFixedSizeEncoder() {
        testFixedSizeEncoder(0, 5, 6);
        testFixedSizeEncoder(0, 6, 5);
        testFixedSizeEncoder(100, 5, 6);
        testFixedSizeEncoder(100, 200, 60);
        testFixedSizeEncoder(10086, 11, 333);
        testFixedSizeEncoder(10086, 333, 11);
        testFixedSizeEncoder(10086, 22, 22);
        testFixedSizeEncoder(10086, 333, 1);
        testFixedSizeEncoder(333, 10086, 1);
        testFixedSizeEncoder(10086, 20, 19);
        testFixedSizeEncoder(20, 40, 19);
        testFixedSizeEncoder(10240, 1024, 512);
        testFixedSizeEncoder(1024, 1024, 1024);
    }

    private void testFixedSizeEncoder(int dataSize, int readBlockSize, int fixedSize) {
        {
            // test read
            char[] src = JieRandom.fill(new char[dataSize]);
            int times = dataSize / fixedSize;
            CharsBuilder charsBuilder = new CharsBuilder();
            int pos = 0;
            for (int i = 0; i < times; i++) {
                charsBuilder.append(Arrays.copyOfRange(src, pos, pos + fixedSize));
                charsBuilder.append('\r');
                charsBuilder.append('\n');
                pos += fixedSize;
            }
            if (src.length > pos) {
                charsBuilder.append(Arrays.copyOfRange(src, pos, src.length));
                charsBuilder.append('\r');
                charsBuilder.append('\n');
            }
            int portion = JieMath.leastPortion(dataSize, fixedSize);
            char[] dst = new char[src.length + portion * 2];
            IntVar ec = IntVar.of(0);
            long len = CharProcessor.from(src).readBlockSize(readBlockSize).
                encoder(fixedSize, (data, end) -> {
                    if (end) {
                        ec.incrementAndGet();
                    }
                    int remaining = data.remaining();
                    if (remaining == 0) {
                        return JieChars.emptyBuffer();
                    }
                    char[] bb = new char[remaining + 2];
                    data.get(bb, 0, remaining);
                    bb[remaining] = '\r';
                    bb[remaining + 1] = '\n';
                    return CharBuffer.wrap(bb);
                })
                .writeTo(dst);
            assertEquals(dst, charsBuilder.toCharArray());
            assertEquals(len, src.length);
            assertEquals(ec.get(), 1);
        }
        {
            // test write 6
            char[] src = JieRandom.fill(new char[dataSize]);
            CharsBuilder charsBuilder = new CharsBuilder();
            int totalRemaining = dataSize;
            int pos = 0;
            int bufferPos = -1;
            while (totalRemaining > 0) {
                int readSize = Math.min(totalRemaining, readBlockSize);
                if (bufferPos < 0) {
                    if (readSize >= fixedSize) {
                        int fillSize = readSize / fixedSize * fixedSize;
                        charsBuilder.append(JieArray.fill(new char[fillSize], (char) 6));
                        if (fillSize < readSize) {
                            bufferPos = pos + fillSize;
                        }
                    } else {
                        bufferPos = pos;
                    }
                } else {
                    int bufferedSize = pos - bufferPos;
                    int nowRemaining = bufferedSize + readSize;
                    if (nowRemaining >= fixedSize) {
                        charsBuilder.append(Arrays.copyOfRange(src, bufferPos, bufferPos + fixedSize));
                        int restReadSize = nowRemaining - fixedSize;
                        int fillSize = restReadSize / fixedSize * fixedSize;
                        charsBuilder.append(JieArray.fill(new char[fillSize], (char) 6));
                        if (fillSize < restReadSize) {
                            bufferPos = bufferPos + fixedSize + fillSize;
                        } else {
                            bufferPos = -1;
                        }
                    }
                }
                pos += readSize;
                totalRemaining -= readSize;
            }
            if (bufferPos >= 0) {
                charsBuilder.append(Arrays.copyOfRange(src, bufferPos, src.length));
            }
            if (readBlockSize % fixedSize == 0 && dataSize % fixedSize == 0) {
                assertEquals(charsBuilder.toCharArray(), JieArray.fill(new char[dataSize], (char) 6));
            }
            char[] b6ret = CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder(fixedSize, (data, end) -> {
                    char[] b6 = JieArray.fill(new char[data.remaining()], (char) 6);
                    data.put(b6);
                    return CharBuffer.wrap(b6);
                }).toCharArray();
            assertEquals(b6ret, JieArray.fill(new char[dataSize], (char) 6));
            assertEquals(src, charsBuilder.toCharArray());
        }
        {
            // null
            char[] src = JieRandom.fill(new char[dataSize]);
            CharsBuilder builder = new CharsBuilder();
            IntVar ec = IntVar.of(0);
            CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder(fixedSize, (data, end) -> null)
                .encoder((data, end) -> {
                    if (end) {
                        ec.incrementAndGet();
                    }
                    return null;
                })
                .writeTo(builder);
            assertEquals(builder.size(), 0);
            assertEquals(ec.get(), 0);
        }
    }

    @Test
    public void testRoundingEncoder() {
        testRoundingEncoder(0, 5, 6);
        testRoundingEncoder(0, 6, 5);
        testRoundingEncoder(100, 5, 6);
        testRoundingEncoder(100, 200, 60);
        testRoundingEncoder(10086, 11, 333);
        testRoundingEncoder(10086, 333, 11);
        testRoundingEncoder(10086, 22, 22);
        testRoundingEncoder(10086, 222, 1);
        testRoundingEncoder(222, 10086, 1);
        testRoundingEncoder(223, 2233, 2);
    }

    private void testRoundingEncoder(int dataSize, int readBlockSize, int roundingSize) {
        {
            // test read
            char[] src = JieRandom.fill(new char[dataSize]);
            char[] dst = new char[src.length * 2];
            for (int i = 0; i < src.length; i++) {
                dst[i * 2] = src[i];
                dst[i * 2 + 1] = (char) roundingSize;
            }
            char[] dst2 = new char[src.length * 2];
            IntVar ec = IntVar.of(0);
            long len = CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder(withRounding(roundingSize, (data, end) -> {
                    if (!end) {
                        assertTrue(
                            (data.remaining() >= roundingSize)
                                && (data.remaining() % roundingSize == 0)
                        );
                    } else {
                        ec.incrementAndGet();
                        if (roundingSize > 1) {
                            assertTrue(data.remaining() <= roundingSize);
                        }
                    }
                    CharBuffer bb = CharBuffer.allocate(data.remaining() * 2);
                    while (data.hasRemaining()) {
                        bb.put(data.get());
                        bb.put((char) roundingSize);
                    }
                    bb.flip();
                    return bb;
                }))
                .writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
            assertEquals(ec.get(), 1);
        }
        {
            // test write 6
            char[] src = JieRandom.fill(new char[dataSize]);
            CharsBuilder charsBuilder = new CharsBuilder();
            int totalRemaining = dataSize;
            int pos = 0;
            int bufferPos = -1;
            while (totalRemaining > 0) {
                int readSize = Math.min(totalRemaining, readBlockSize);
                if (bufferPos < 0) {
                    if (readSize >= roundingSize) {
                        int fillSize = readSize / roundingSize * roundingSize;
                        charsBuilder.append(JieArray.fill(new char[fillSize], (char) 6));
                        if (fillSize < readSize) {
                            bufferPos = pos + fillSize;
                        }
                    } else {
                        bufferPos = pos;
                    }
                } else {
                    int bufferedSize = pos - bufferPos;
                    int nowRemaining = bufferedSize + readSize;
                    if (nowRemaining >= roundingSize) {
                        charsBuilder.append(Arrays.copyOfRange(src, bufferPos, bufferPos + roundingSize));
                        int restReadSize = nowRemaining - roundingSize;
                        int fillSize = restReadSize / roundingSize * roundingSize;
                        charsBuilder.append(JieArray.fill(new char[fillSize], (char) 6));
                        if (fillSize < restReadSize) {
                            bufferPos = bufferPos + roundingSize + fillSize;
                        } else {
                            bufferPos = -1;
                        }
                    }
                }
                pos += readSize;
                totalRemaining -= readSize;
            }
            if (bufferPos >= 0) {
                charsBuilder.append(Arrays.copyOfRange(src, bufferPos, src.length));
            }
            if (readBlockSize % roundingSize == 0 && dataSize % roundingSize == 0) {
                assertEquals(charsBuilder.toCharArray(), JieArray.fill(new char[dataSize], (char) 6));
            }
            char[] b6ret = CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder(roundingSize, (data, end) -> {
                    char[] b6 = JieArray.fill(new char[data.remaining()], (char) 6);
                    data.put(b6);
                    return CharBuffer.wrap(b6);
                }).toCharArray();
            assertEquals(b6ret, JieArray.fill(new char[dataSize], (char) 6));
            assertEquals(src, charsBuilder.toCharArray());
        }
        {
            // null
            char[] src = JieRandom.fill(new char[dataSize]);
            CharsBuilder builder = new CharsBuilder();
            IntVar ec = IntVar.of(0);
            CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder(withRounding(roundingSize, (data, end) -> null))
                .encoder((data, end) -> {
                    if (end) {
                        ec.incrementAndGet();
                    }
                    return null;
                })
                .writeTo(builder);
            assertEquals(builder.size(), 0);
            assertEquals(ec.get(), 0);
        }
    }

    @Test
    public void testBufferingEncoder() {
        testBufferingEncoder(0, 5);
        testBufferingEncoder(100, 5);
        testBufferingEncoder(100, 200);
        testBufferingEncoder(10086, 11);
        testBufferingEncoder(10086, 333);
        testBufferingEncoder(10086, 22);
        testBufferingEncoder(10086, 333);
        testBufferingEncoder(333, 10086);
        testBufferingEncoder(233, 2333);
    }

    private void testBufferingEncoder(int dataSize, int readBlockSize) {
        {
            char[] src = JieRandom.fill(new char[dataSize]);
            char[] dst = new char[src.length];
            BooleanVar bf = BooleanVar.of(false);
            IntVar ec = IntVar.of(0);
            long len = CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder(withBuffering((data, end) -> {
                    if (end) {
                        ec.incrementAndGet();
                        return data;
                    }
                    boolean bfv = bf.getAndToggle();
                    if (bfv) {
                        int size = data.remaining() / 2;
                        CharBuffer ret = CharBuffer.allocate(size);
                        while (ret.hasRemaining()) {
                            ret.put(data.get());
                        }
                        ret.flip();
                        return ret;
                    } else {
                        return CharBuffer.wrap(JieBuffer.read(data));
                    }
                }))
                .writeTo(dst);
            assertEquals(dst, src);
            assertEquals(len, src.length);
            assertEquals(ec.get(), 1);
        }
        {
            // null
            char[] src = JieRandom.fill(new char[dataSize]);
            CharsBuilder builder = new CharsBuilder();
            IntVar ec = IntVar.of(0);
            CharProcessor.from(src).readBlockSize(readBlockSize)
                .encoder(withBuffering((data, end) -> null))
                .encoder((data, end) -> {
                    if (end) {
                        ec.incrementAndGet();
                    }
                    return null;
                })
                .writeTo(builder);
            assertEquals(builder.size(), 0);
            assertEquals(ec.get(), 0);
        }
    }

    @Test
    public void testToReader() throws Exception {
        testToReader(0, 5);
        testToReader(100, 5);
        testToReader(10086, 11);
        testToReader(10086, 333);
        testToReader(10086, 22);
        testToReader(333, 10086);
        testToReader(20, 10086);
        testToReader(20, 40);
        {
            // non-processor reader
            assertEquals(
                CharProcessor.from(new CharArrayReader(new char[0])).toReader().getClass(),
                CharArrayReader.class
            );
            assertEquals(
                CharProcessor.from(new char[0]).toReader().getClass(),
                JieIO.reader(new char[0]).getClass()
            );
            assertEquals(
                CharProcessor.from(CharBuffer.allocate(0)).toReader().getClass(),
                JieIO.reader(CharBuffer.allocate(0)).getClass()
            );
            assertEquals(
                CharProcessor.from("").toReader().getClass(),
                JieIO.reader("").getClass()
            );
            CharProcessor inst = CharProcessor.from(new char[0]);
            reflectThrows(
                IORuntimeException.class,
                inst.getClass().getDeclaredMethod("toReader", Object.class),
                inst,
                5
            );
        }
        {
            // special with encoder
            Reader in = CharProcessor.from(new char[0])
                .encoder((d, e) -> d)
                .toReader();
            assertEquals(in.read(), -1);
            assertEquals(in.read(), -1);
            assertEquals(in.read(new char[1]), -1);
            assertEquals(in.read(new char[1], 0, 0), 0);
            assertEquals(in.skip(-1), 0);
            assertEquals(in.skip(0), 0);
            in.close();
            in.close();
            expectThrows(IOException.class, () -> in.read());
            Reader nio = CharProcessor.from(new NioReader()).endOnZeroRead(true)
                .encoder((d, e) -> d)
                .toReader();
            assertEquals(nio.read(), -1);
            Reader empty = CharProcessor.from(new char[]{'9'})
                .encoder(((data, end) -> {
                    CharsBuilder builder = new CharsBuilder();
                    builder.append(data);
                    if (end) {
                        builder.append(new char[]{'1', '2', '3'});
                    }
                    return CharBuffer.wrap(builder.toString());
                })).toReader();
            assertEquals(JieIO.string(empty).toCharArray(), new char[]{'9', '1', '2', '3'});
            assertEquals(empty.read(), -1);
            Reader err1 = CharProcessor.from(new CharProcessorTest.ThrowReader(0))
                .encoder((d, e) -> d)
                .toReader();
            expectThrows(IOException.class, () -> err1.close());
            Reader err2 = CharProcessor.from(new CharProcessorTest.ThrowReader(2))
                .encoder((d, e) -> d)
                .toReader();
            expectThrows(IOException.class, () -> err2.close());
            Reader err3 = CharProcessor.from(new CharProcessorTest.ThrowReader(3))
                .encoder((d, e) -> d)
                .toReader();
            expectThrows(IOException.class, () -> err3.read());
        }
        {
            boolean[] flag = {true};
            Reader in = CharProcessor.from(new char[1024]).readBlockSize(1)
                .encoder(((data, end) -> {
                    CharBuffer ret = flag[0] ? data : JieChars.emptyBuffer();
                    flag[0] = !flag[0];
                    return ret;
                })).toReader();
            CharsBuilder builder = new CharsBuilder();
            while (true) {
                int b = in.read();
                if (b == -1) {
                    break;
                }
                builder.append((char) b);
            }
            assertEquals(builder.toCharArray().length, 1024 / 2);
        }
    }

    private void testToReader(int dataSize, int readBlockSize) throws Exception {
        char[] src = JieRandom.fill(new char[dataSize]);
        int times = dataSize / readBlockSize;
        CharsBuilder bb = new CharsBuilder();
        int pos = 0;
        for (int i = 0; i < times; i++) {
            bb.append(Arrays.copyOfRange(src, pos, pos + readBlockSize));
            bb.append('\r');
            pos += readBlockSize;
        }
        if (pos < dataSize) {
            bb.append(Arrays.copyOfRange(src, pos, dataSize));
            bb.append('\r');
        }
        char[] encoded = bb.toCharArray();
        {
            IntVar ec = IntVar.of(0);
            Reader in = toReader(src, readBlockSize, ec);
            assertEquals(JieIO.string(in).toCharArray(), encoded);
            assertEquals(in.read(), -1);
            assertEquals(ec.get(), 1);
        }
        {
            IntVar ec = IntVar.of(0);
            Reader in = toReader(src, readBlockSize, ec);
            CharsBuilder builder = new CharsBuilder();
            while (true) {
                int b = in.read();
                if (b == -1) {
                    break;
                }
                builder.append((char) b);
            }
            assertEquals(builder.toCharArray(), encoded);
            assertEquals(ec.get(), 1);
        }
        {
            IntVar ec = IntVar.of(0);
            Reader in = toReader(src, readBlockSize, ec);
            assertEquals(in.skip(666), Math.min(666, encoded.length));
            assertEquals(in.skip(1666), Math.min(1666, Math.max(encoded.length - 666, 0)));
            assertEquals(ec.get(), 666 + 1666 >= encoded.length ? 1 : 0);
        }
        {
            Reader in = CharProcessor.from(src).readBlockSize(readBlockSize).toReader();
            assertEquals(JieIO.string(in).toCharArray(), src);
            assertEquals(in.read(), -1);
        }
    }

    private Reader toReader(char[] src, int readBlockSize, IntVar ec) {
        return CharProcessor.from(src).readBlockSize(readBlockSize)
            .encoder(((data, end) -> {
                if (end) {
                    ec.incrementAndGet();
                }
                if (!data.hasRemaining()) {
                    return data;
                }
                CharsBuilder b = new CharsBuilder();
                b.append(data);
                b.append((byte) '\r');
                return b.toCharBuffer();
            })).toReader();
    }

    @Test
    public void testToByteProcessor() {
        testToByteProcessor(0, 5);
        testToByteProcessor(100, 5);
        testToByteProcessor(10086, 11);
        testToByteProcessor(10086, 333);
        testToByteProcessor(10086, 22);
        testToByteProcessor(10086, 333);
        testToByteProcessor(10086, 20);
        testToByteProcessor(20, 40);
        testToByteProcessor(10086, 1);
    }

    private void testToByteProcessor(int totalSize, int blockSize) {
        {
            char[] str = JieRandom.fill(new char[totalSize], 'a', 'z');
            byte[] bytes = new String(str).getBytes(JieChars.defaultCharset());
            byte[] converted = JieIO.read(
                CharProcessor.from(str).readBlockSize(blockSize).toByteProcessor(JieChars.defaultCharset()).toInputStream()
            );
            assertEquals(converted, bytes);
        }
        {
            char[] str = JieRandom.fill(new char[totalSize], '\u4e00', '\u9fff');
            byte[] bytes = new String(str).getBytes(JieChars.defaultCharset());
            byte[] converted = JieIO.read(
                CharProcessor.from(str).readBlockSize(blockSize).toByteProcessor(JieChars.defaultCharset()).toInputStream()
            );
            assertEquals(converted, bytes);
        }
    }

    @Test
    public void testSpecial() throws Exception {

        {
            // empty
            CharsBuilder bb = new CharsBuilder();
            long c;
            c = CharProcessor.from(new char[0]).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = CharProcessor.from(new char[0]).writeTo(new char[0]);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = CharProcessor.from(new char[0]).writeTo(CharBuffer.allocate(0));
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = CharProcessor.from(JieChars.emptyBuffer()).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = CharProcessor.from(JieChars.emptyBuffer()).writeTo(new char[0]);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = CharProcessor.from(JieChars.emptyBuffer()).writeTo(CharBuffer.allocate(0));
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = CharProcessor.from(new CharArrayReader(new char[0])).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = CharProcessor.from("").writeTo(new char[0]);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = CharProcessor.from("").writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
        }

        {
            // endOnZeroRead
            CharsBuilder bb = new CharsBuilder();
            long c;
            c = CharProcessor.from(new NioReader()).endOnZeroRead(true)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = CharProcessor.from(new NioReader(new CharArrayReader(new char[0]))).endOnZeroRead(false)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
        }

        {
            // writeable
            char[] src = new char[1024];
            char[] target = new char[1024];
            Arrays.fill(src, (char) 1);
            Arrays.fill(target, (char) 2);
            assertNotEquals(src, target);
            CharProcessor.from(src).readBlockSize(3).encoder(((data, end) -> {
                assertFalse(data.isReadOnly());
                while (data.hasRemaining()) {
                    data.put((char) 2);
                }
                return data;
            })).process();
            assertEquals(src, target);
            Arrays.fill(src, (char) 1);
            assertNotEquals(src, target);
            CharProcessor.from(CharBuffer.wrap(src)).readBlockSize(3).encoder(((data, end) -> {
                assertFalse(data.isReadOnly());
                while (data.hasRemaining()) {
                    data.put((char) 2);
                }
                return data;
            })).process();
            assertEquals(src, target);
        }

        {
            // writeTo
            String str = "1234567890qwertyuiop[]中文";
            char[] strChars = str.toCharArray();
            assertEquals(CharProcessor.from(str).toCharArray(), strChars);
            assertEquals(CharProcessor.from(str).toCharBuffer(), CharBuffer.wrap(strChars));
            assertEquals(CharProcessor.from(str).toString(), str);
        }

        // error
        expectThrows(IllegalArgumentException.class, () -> testProcessing(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> CharProcessor.from((Reader) null).writeTo((Appendable) null));
        expectThrows(IndexOutOfBoundsException.class, () -> CharProcessor.from(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> CharProcessor.from(new char[0]).writeTo(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> CharProcessor.from(new char[0]).writeTo((Appendable) null));
        expectThrows(IORuntimeException.class, () -> CharProcessor.from((Reader) null).writeTo(new char[0]));
        Method method = CharProcessor.from(new char[0]).getClass().getDeclaredMethod("toCharReader", Object.class);
        reflectThrows(IORuntimeException.class, method, CharProcessor.from(new char[0]), 1);
        method = CharProcessor.from(new char[0]).getClass().getDeclaredMethod("toBufferOut", Object.class);
        reflectThrows(IORuntimeException.class, method, CharProcessor.from(new char[0]), "");
        expectThrows(IORuntimeException.class, () -> CharProcessor.from(new ThrowReader(0)).writeTo(new char[0]));
        expectThrows(IORuntimeException.class, () -> CharProcessor.from(new ThrowReader(1)).writeTo(new char[0]));
    }

    private static final class NioReader extends Reader {

        private int i = 0;
        private final Reader in;

        public NioReader() {
            this(null);
        }

        public NioReader(Reader in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(@NotNull char[] b, int off, int len) throws IOException {
            if (i++ < 3) {
                return 0;
            }
            int actualLen = len <= 1 ? len : len / 2;
            if (in != null) {
                return in.read(b, off, actualLen);
            } else {
                Arrays.fill(b, off, off + actualLen, (char) 1);
                return actualLen;
            }
        }

        public void reset() {
            i = 0;
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static final class ThrowReader extends Reader {

        private final int e;

        private ThrowReader(int e) {
            this.e = e;
        }

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public int read(@NotNull char[] cbuf, int off, int len) throws IOException {
            if (e == 0) {
                throw new IOException("e == 0");
            }
            throw new IllegalArgumentException("e = " + e);
        }

        @Override
        public void close() throws IOException {
            if (e == 0) {
                throw new IOException("e == 0");
            }
            throw new IllegalArgumentException("e = " + e);
        }
    }

    private static final class NonWriterAppender implements Appendable {

        private final StringBuilder sb = new StringBuilder();

        @Override
        public Appendable append(CharSequence csq) {
            sb.append(csq);
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) {
            sb.append(csq, start, end);
            return this;
        }

        @Override
        public Appendable append(char c) {
            sb.append(c);
            return this;
        }

        public void reset() {
            sb.delete(0, sb.length());
        }

        public String toString() {
            return sb.toString();
        }

        public char[] toCharArray() {
            return sb.toString().toCharArray();
        }
    }
}
