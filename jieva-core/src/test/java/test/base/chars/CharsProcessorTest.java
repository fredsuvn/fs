package test.base.chars;

import org.jetbrains.annotations.NotNull;
import org.testng.TestException;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieMath;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.chars.CharsBuilder;
import xyz.sunqian.common.base.chars.CharsProcessor;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;

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
import static xyz.sunqian.test.JieTest.reflectThrows;
import static xyz.sunqian.test.MaterialBox.heapBuffer;
import static xyz.sunqian.test.MaterialBox.paddedBuffer;

public class CharsProcessorTest {

    @Test
    public void testProcessing() throws Exception {
        // readTo()
        testProcessing(666, JieIO.BUFFER_SIZE, -1);
        testProcessing(666, 67, -1);
        testProcessing(666, 1, -1);
        testProcessing(100, 10, -1);
        testProcessing(666, JieIO.BUFFER_SIZE, -1);
        testProcessing(666, 67, 667);
        testProcessing(666, 1, 667);
        testProcessing(100, 10, 101);
        testProcessing(222, 33, 55);
        testProcessing(100, 10, 0);
        testProcessing(100, 10, 100);
        testProcessing(6666, 99, 77777);

        {
            // empty
            CharsBuilder bb = new CharsBuilder();
            long c;
            c = JieChars.process(new char[0]).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = JieChars.process(new char[0]).writeTo(new char[0]);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = JieChars.process(new char[0]).writeTo(CharBuffer.allocate(0));
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = JieChars.process(JieChars.emptyBuffer()).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = JieChars.process(JieChars.emptyBuffer()).writeTo(new char[0]);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = JieChars.process(JieChars.emptyBuffer()).writeTo(CharBuffer.allocate(0));
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = JieChars.process(new CharArrayReader(new char[0])).writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = JieChars.process("").writeTo(new char[0]);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = JieChars.process("").writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
        }

        {
            // endOnZeroRead
            CharsBuilder bb = new CharsBuilder();
            long c;
            c = JieChars.process(new NioReader()).endOnZeroRead(true)
                .encoder((data, end) -> data)
                .writeTo(bb);
            assertEquals(c, 0);
            assertEquals(bb.toCharArray(), new char[0]);
            c = JieChars.process(new NioReader(new CharArrayReader(new char[0]))).endOnZeroRead(false)
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
            JieChars.process(src).readBlockSize(3).encoder(((data, end) -> {
                assertFalse(data.isReadOnly());
                while (data.hasRemaining()) {
                    data.put((char) 2);
                }
                return data;
            })).writeTo();
            assertEquals(src, target);
            Arrays.fill(src, (char) 1);
            assertNotEquals(src, target);
            JieChars.process(CharBuffer.wrap(src)).readBlockSize(3).encoder(((data, end) -> {
                assertFalse(data.isReadOnly());
                while (data.hasRemaining()) {
                    data.put((char) 2);
                }
                return data;
            })).writeTo();
            assertEquals(src, target);
            JieChars.process(new CharArrayReader(src)).readBlockSize(3).encoder(((data, end) -> {
                assertTrue(data.isReadOnly());
                return data;
            })).writeTo();
            JieChars.process(new String(src)).readBlockSize(3).encoder(((data, end) -> {
                assertTrue(data.isReadOnly());
                return data;
            })).writeTo();
        }

        {
            // writeTo
            String str = "1234567890qwertyuiop[]中文";
            char[] strChars = str.toCharArray();
            assertEquals(JieChars.process(str).toCharArray(), strChars);
            assertEquals(JieChars.process(str).toCharBuffer(), CharBuffer.wrap(strChars));
            assertEquals(JieChars.process(str).toString(), str);
        }

        // error
        expectThrows(IllegalArgumentException.class, () -> testProcessing(666, 0, 0));
        expectThrows(IORuntimeException.class, () -> JieChars.process((Reader) null).writeTo((Appendable) null));
        expectThrows(IndexOutOfBoundsException.class, () -> JieChars.process(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> JieChars.process(new char[0]).writeTo(new char[0], 0, 100));
        expectThrows(IORuntimeException.class, () -> JieChars.process(new char[0]).writeTo((Appendable) null));
        expectThrows(IORuntimeException.class, () -> JieChars.process((Reader) null).writeTo(new char[0]));
        Method method = JieChars.process(new char[0]).getClass().getDeclaredMethod("toBufferIn", Object.class);
        reflectThrows(IORuntimeException.class, method, JieChars.process(new char[0]), 1);
        method = JieChars.process(new char[0]).getClass().getDeclaredMethod("toBufferOut", Object.class);
        reflectThrows(IORuntimeException.class, method, JieChars.process(new char[0]), "");
        expectThrows(IORuntimeException.class, () -> JieChars.process(new ThrowReader(0)).writeTo(new char[0]));
        expectThrows(IORuntimeException.class, () -> JieChars.process(new ThrowReader(1)).writeTo(new char[0]));
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
        CharBuffer dirBuffer = JieChars.copyBuffer(chars, true);

        {
            // stream -> stream
            CharArrayReader in = new CharArrayReader(chars);
            CharsBuilder out = new CharsBuilder();
            long readNum = JieChars.process(in).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // string -> stream
            CharsBuilder out = new CharsBuilder();
            long readNum = JieChars.process(str).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // direct -> stream
            CharBuffer dirInBuffer = JieChars.copyBuffer(dirBuffer);
            CharsBuilder outBuilder = new CharsBuilder();
            long readNum = JieChars.process(dirInBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuilder);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = JieChars.copyBuffer(dirBuffer);
            outBuilder.reset();
            readNum = JieChars.process(dirInBuffer).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> JieChars.copyBuffer(s)).writeTo(outBuilder);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), outBuilder.toString());
            dirInBuffer = JieChars.copyBuffer(dirBuffer);
            StringWriter sw = new StringWriter();
            readNum = JieChars.process(dirInBuffer).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> JieChars.copyBuffer(s)).writeTo(sw);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(str.substring(0, getLength(chars.length, readLimit)), sw.toString());
        }

        {
            // stream -> char[]
            char[] outChars = new char[chars.length];
            CharArrayReader in = new CharArrayReader(chars);
            in.mark(0);
            long readNum = JieChars.process(in).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length * 2];
            in.reset();
            readNum = JieChars.process(in).readBlockSize(blockSize).writeTo(outChars, offset, chars.length);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(Arrays.copyOfRange(outChars, offset, offset + chars.length)));
        }

        {
            // stream -> buffer
            CharBuffer outBuffer = JieChars.copyBuffer(dirBuffer);
            CharArrayReader in = new CharArrayReader(chars);
            long readNum = JieChars.process(in).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outChars = JieChars.getChars(outBuffer);
            assertEquals(str, new String(outChars));
        }

        // char[] -> stream
        {
            CharsBuilder out = new CharsBuilder();
            long readNum = JieChars.process(chars).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            out.reset();
            readNum = JieChars.process(chars).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // char[] -> char[]
            char[] outChars = new char[chars.length];
            long readNum = JieChars.process(chars).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            readNum = JieChars.process(chars).readBlockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
            char[] inChars = new char[chars.length * 2];
            outChars = new char[chars.length];
            System.arraycopy(chars, 0, inChars, offset, chars.length);
            readNum = JieChars.process(inChars, offset, chars.length).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = JieChars.process(chars, 0, chars.length)
                .readBlockSize(blockSize).writeTo(outChars, 0, outChars.length);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = JieChars.process(chars, 0, chars.length - 1)
                .readBlockSize(blockSize).writeTo(outChars, 0, outChars.length - 1);
            assertEquals(readNum, chars.length - 1);
            assertEquals(str.substring(0, str.length() - 1),
                new String(Arrays.copyOfRange(outChars, 0, outChars.length - 1)));
        }

        {
            // char[] -> buffer
            CharBuffer outBuffer = JieChars.copyBuffer(chars, true);
            long readNum = JieChars.process(chars).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            assertEquals(str, new String(JieChars.getChars(outBuffer)));
            outBuffer = JieChars.copyBuffer(chars, true);
            readNum = JieChars.process(chars).readBlockSize(blockSize).readLimit(readLimit).writeTo(outBuffer);
            assertEquals(readNum, getLength(chars.length, readLimit));
            outBuffer.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieChars.getChars(outBuffer), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // char[] -> appender
            CharsBuilder appender = new CharsBuilder();
            long readNum = JieChars.process(chars).readBlockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.reset();
            readNum = JieChars.process(chars).readBlockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }

        {
            // buffer -> stream
            CharBuffer inBuffer = heapBuffer(chars);
            inBuffer.mark();
            CharsBuilder out = new CharsBuilder();
            long readNum = JieChars.process(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            inBuffer.reset();
            out.reset();
            readNum = JieChars.process(inBuffer).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).writeTo(out);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(out.toCharArray(), 0, getLength(chars.length, readLimit))
            );
            CharBuffer arrayIn = paddedBuffer(chars);
            CharBuffer arrayOut = paddedBuffer(new char[chars.length]);
            readNum = JieChars.process(arrayIn).readBlockSize(blockSize).readLimit(readLimit).writeTo(arrayOut);
            assertEquals(readNum, getLength(chars.length, readLimit));
            arrayOut.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieChars.getChars(arrayOut), 0, getLength(chars.length, readLimit))
            );
            arrayIn.flip();
            arrayOut.flip();
            readNum = JieChars.process(arrayIn).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).writeTo(arrayOut);
            assertEquals(readNum, getLength(chars.length, readLimit));
            arrayOut.flip();
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(JieChars.getChars(arrayOut), 0, getLength(chars.length, readLimit))
            );
        }

        {
            // buffer -> char[]
            CharBuffer inBuffer = heapBuffer(chars);
            inBuffer.mark();
            char[] outChars = new char[chars.length];
            long readNum = JieChars.process(inBuffer).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            inBuffer.reset();
            outChars = new char[chars.length];
            readNum = JieChars.process(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
        }

        {
            // buffer -> appender
            CharBuffer inBuffer = paddedBuffer(chars);
            inBuffer.mark();
            CharsBuilder appender = new CharsBuilder();
            long readNum = JieChars.process(inBuffer).readBlockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            inBuffer.reset();
            appender.reset();
            readNum = JieChars.process(inBuffer).readBlockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }

        {
            // buffer -> buffer
            CharBuffer inBuffer = paddedBuffer(chars);
            inBuffer.mark();
            CharBuffer outBuffer = JieChars.copyBuffer(dirBuffer);
            long readNum = JieChars.process(inBuffer).readBlockSize(blockSize).writeTo(outBuffer);
            assertEquals(readNum, chars.length);
            outBuffer.flip();
            char[] outBytes = JieChars.getChars(outBuffer);
            assertEquals(str, new String(outBytes));
        }

        {
            // charSeq -> char[]
            char[] outChars = new char[chars.length];
            long readNum = JieChars.process(str).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = JieChars.process(str).readBlockSize(blockSize).readLimit(readLimit).writeTo(outChars);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                new String(outChars, 0, getLength(chars.length, readLimit))
            );
            outChars = new char[chars.length];
            readNum = JieChars.process(JieString.asChars(str.toCharArray())).readBlockSize(blockSize).writeTo(outChars);
            assertEquals(readNum, chars.length);
            assertEquals(str, new String(outChars));
            outChars = new char[chars.length];
            readNum = JieChars.process(JieString.asChars(str.toCharArray()))
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
            long readNum = JieChars.process(str).readBlockSize(blockSize).writeTo(appender);
            assertEquals(readNum, chars.length);
            assertEquals(str, appender.toString());
            appender.reset();
            readNum = JieChars.process(str).readBlockSize(blockSize).readLimit(readLimit).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
            appender.reset();
            readNum = JieChars.process(str).readBlockSize(blockSize).readLimit(readLimit)
                .encoder((s, e) -> CharBuffer.wrap(JieChars.getChars(s))).writeTo(appender);
            assertEquals(readNum, getLength(chars.length, readLimit));
            assertEquals(
                str.substring(0, getLength(chars.length, readLimit)),
                appender.toString()
            );
        }

        {
            // any -> null
            long[] counter = {0};
            long readNum = JieChars.process(new char[totalSize])
                .readBlockSize(blockSize)
                .readLimit(readLimit)
                .encoder(((data, end) -> {
                    counter[0] += data.remaining();
                    return data;
                }))
                .writeTo();
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
    public void testEncoder() {
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
                JieChars.process(new char[100]).encoder((data, end) -> {
                    throw new TestException("haha");
                }).writeTo(new char[100]);
            } catch (ProcessingException e) {
                ts[0] = e;
            }
            assertEquals(ts[0].getCause().getClass(), TestException.class);
            assertEquals(ts[0].getCause().getMessage(), "haha");
        }
    }

    private void testEncoder(int totalSize, int blockSize) {
        {
            // simple
            char[] src = JieRandom.fill(new char[totalSize]);
            int portion = JieMath.leastPortion(totalSize, blockSize);
            CharsBuilder bb = new CharsBuilder();
            int start = 0;
            for (int i = 0; i < portion; i++) {
                int end = Math.min(start + blockSize, totalSize);
                bb.append(Arrays.copyOfRange(src, start, end));
                bb.append(Arrays.copyOfRange(src, start, end));
                bb.append(Arrays.copyOfRange(src, start, end));
                bb.append(Arrays.copyOfRange(src, start, end));
                start += blockSize;
            }
            char[] expectDst = bb.toCharArray();
            bb.reset();
            CharsProcessor.Encoder encoder = (data, end) -> {
                char[] chars = JieChars.getChars(data);
                char[] ret = new char[chars.length * 2];
                System.arraycopy(chars, 0, ret, 0, chars.length);
                System.arraycopy(chars, 0, ret, chars.length, chars.length);
                return CharBuffer.wrap(ret);
            };
            long count = JieChars.process(src).readBlockSize(blockSize).encoder(encoder).encoder(encoder).writeTo(bb);
            assertEquals(count, totalSize);
            assertEquals(bb.toCharArray(), expectDst);
        }
        {
            // complex
            char[] src = JieRandom.fill(new char[totalSize]);
            CharsBuilder bb = new CharsBuilder();
            for (int i = 0, j = 0; i < src.length; i++) {
                if (j == 2) {
                    j = 0;
                    continue;
                }
                bb.append(src[i]);
                j++;
            }
            char[] proc = bb.toCharArray();
            bb.reset();
            for (int i = 0, j = 0; i < proc.length; i++) {
                bb.append(proc[i]);
                if (j == 9) {
                    j = 0;
                    bb.append('\r');
                } else {
                    j++;
                }
            }
            proc = bb.toCharArray();
            bb.reset();
            boolean[] buffer = {true};
            long count = JieChars.process(src).readBlockSize(blockSize)
                .roundEncoder(3, (data, end) -> {
                    CharsBuilder ret = new CharsBuilder();
                    int j = 0;
                    while (data.hasRemaining()) {
                        char b = data.get();
                        if (j == 2) {
                            j = 0;
                            continue;
                        }
                        ret.append(b);
                        j++;
                    }
                    return CharBuffer.wrap(ret.toString());
                })
                .bufferedEncoder((data, end) -> {
                    if (end) {
                        return data;
                    }
                    CharBuffer ret;
                    if (buffer[0]) {
                        ret = JieChars.emptyBuffer();
                    } else {
                        ret = data;
                    }
                    buffer[0] = !buffer[0];
                    return ret;
                })
                .encoder(10, (data, end) -> {
                    if (data.remaining() == 10) {
                        char[] ret = new char[11];
                        data.get(ret, 0, 10);
                        ret[10] = '\r';
                        return CharBuffer.wrap(ret);
                    } else {
                        return data;
                    }
                })
                .writeTo(bb);
            assertEquals(count, totalSize);
            assertEquals(bb.toCharArray(), proc);
        }
        {
            // null
            char[] src = JieRandom.fill(new char[totalSize]);
            char[] dst = new char[src.length];
            int[] pos = {0};
            CharsBuilder dst0 = new CharsBuilder();
            long c = JieChars.process(src)
                .encoder((data, end) -> {
                    int len = data.remaining();
                    data.get(dst, pos[0], len);
                    pos[0] += len;
                    return null;
                }).encoder((data, end) -> data)
                .writeTo(dst0);
            assertEquals(c, totalSize);
            assertEquals(dst, src);
            assertEquals(dst0.size(), 0);
            char[] dst1 = new char[src.length];
            boolean[] buffer = {true};
            c = JieChars.process(src)
                .bufferedEncoder((data, end) -> {
                    boolean b = buffer[0];
                    buffer[0] = !b;
                    return b ? data : null;
                }).encoder((data, end) -> data)
                .writeTo(dst1);
            assertEquals(c, totalSize);
            assertEquals(dst1, src);
            char[] dst2 = new char[src.length];
            boolean[] hit = {false};
            c = JieChars.process(src)
                .encoder((data, end) -> null)
                .encoder((data, end) -> {
                    hit[0] = true;
                    return data;
                })
                .writeTo(dst0);
            assertEquals(c, totalSize);
            assertEquals(dst2, new char[src.length]);
            assertEquals(dst0.size(), 0);
            assertFalse(hit[0]);
        }
    }

    @Test
    public void testRoundEncoder() {
        testRoundEncoder(100, 5, 6);
        testRoundEncoder(100, 200, 60);
        testRoundEncoder(10086, 11, 333);
        testRoundEncoder(10086, 333, 11);
        testRoundEncoder(10086, 22, 22);
        testRoundEncoder(10086, 222, 1);
        testRoundEncoder(222, 10086, 1);
        testRoundEncoder(223, 2233, 2);
    }

    private void testRoundEncoder(int totalSize, int blockSize, int expectedBlockSize) {
        {
            char[] src = JieRandom.fill(new char[totalSize]);
            char[] dst = new char[src.length * 2];
            for (int i = 0; i < src.length; i++) {
                dst[i * 2] = src[i];
                dst[i * 2 + 1] = (char) expectedBlockSize;
            }
            char[] dst2 = new char[src.length * 2];
            long len = JieChars.process(src).readBlockSize(blockSize)
                .roundEncoder(expectedBlockSize, (data, end) -> {
                    if (!end) {
                        assertTrue(data.remaining() >= expectedBlockSize);
                        if (blockSize < expectedBlockSize) {
                            assertEquals(data.remaining(), expectedBlockSize);
                        } else {
                            assertTrue(data.remaining() <= (blockSize / expectedBlockSize + 1) * expectedBlockSize);
                            assertTrue(data.remaining() >= (blockSize / expectedBlockSize) * expectedBlockSize);
                        }
                    }
                    CharBuffer bb = CharBuffer.allocate(data.remaining() * 2);
                    while (data.hasRemaining()) {
                        bb.put(data.get());
                        bb.put((char) expectedBlockSize);
                    }
                    bb.flip();
                    return bb;
                })
                .writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
            len = JieChars.process(src).readBlockSize(blockSize)
                .roundEncoder(expectedBlockSize, (data, end) -> {
                    if (!end) {
                        assertTrue(data.remaining() >= expectedBlockSize);
                        if (blockSize < expectedBlockSize) {
                            assertEquals(data.remaining(), expectedBlockSize);
                        } else {
                            assertTrue(data.remaining() <= (blockSize / expectedBlockSize + 1) * expectedBlockSize);
                            assertTrue(data.remaining() >= (blockSize / expectedBlockSize) * expectedBlockSize);
                        }
                    }
                    CharBuffer bb = CharBuffer.allocate(data.remaining() * 2);
                    while (data.hasRemaining()) {
                        bb.put(data.get());
                        bb.put((char) expectedBlockSize);
                    }
                    bb.flip();
                    return bb;
                })
                .writeTo(dst2);
            assertEquals(dst2, dst);
            assertEquals(len, src.length);
        }
        {
            // null
            char[] src = JieRandom.fill(new char[totalSize]);
            CharsBuilder builder = new CharsBuilder();
            JieChars.process(src).readBlockSize(blockSize)
                .roundEncoder(expectedBlockSize, (data, end) -> null)
                .writeTo(builder);
            assertEquals(builder.size(), 0);
        }
    }

    @Test
    public void testBufferedEncoder() {
        testBufferedEncoder(100, 5, 6);
        testBufferedEncoder(100, 200, 60);
        testBufferedEncoder(10086, 11, 333);
        testBufferedEncoder(10086, 333, 11);
        testBufferedEncoder(10086, 22, 22);
        testBufferedEncoder(10086, 333, 1);
        testBufferedEncoder(333, 10086, 1);
        testBufferedEncoder(233, 2333, 2);
    }

    private void testBufferedEncoder(int size, int blockSize, int eatNum) {
        {
            char[] src = JieRandom.fill(new char[size]);
            char[] dst = new char[src.length];
            boolean[] buffer = {true};
            long len = JieChars.process(src).readBlockSize(blockSize).
                bufferedEncoder((data, end) -> {
                    if (end) {
                        return data;
                    }
                    CharBuffer ret;
                    if (buffer[0]) {
                        char[] bb = new char[Math.min(data.remaining(), eatNum)];
                        data.get(bb);
                        ret = CharBuffer.wrap(bb);
                    } else {
                        ret = data;
                    }
                    buffer[0] = !buffer[0];
                    return ret;
                })
                .writeTo(dst);
            assertEquals(dst, src);
            assertEquals(len, src.length);
        }
        {
            // null
            char[] src = JieRandom.fill(new char[size]);
            CharsBuilder builder = new CharsBuilder();
            JieChars.process(src).readBlockSize(blockSize)
                .bufferedEncoder((data, end) -> null)
                .writeTo(builder);
            assertEquals(builder.size(), 0);
        }
    }

    @Test
    public void testFixedSizeEncoder() {
        testFixedSizeEncoder(100, 5, 6);
        testFixedSizeEncoder(100, 200, 60);
        testFixedSizeEncoder(10086, 11, 333);
        testFixedSizeEncoder(10086, 333, 11);
        testFixedSizeEncoder(10086, 22, 22);
        testFixedSizeEncoder(10086, 333, 1);
        testFixedSizeEncoder(333, 10086, 1);
        testFixedSizeEncoder(10086, 20, 19);
        testFixedSizeEncoder(20, 40, 19);
    }

    private void testFixedSizeEncoder(int totalSize, int blockSize, int fixedSize) {
        {
            char[] src = JieRandom.fill(new char[totalSize]);
            int times = totalSize / fixedSize;
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
            int portion = JieMath.leastPortion(totalSize, fixedSize);
            char[] dst = new char[src.length + portion * 2];
            long len = JieChars.process(src).readBlockSize(blockSize).
                encoder(fixedSize, (data, end) -> {
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
        }
        {
            // null
            char[] src = JieRandom.fill(new char[totalSize]);
            CharsBuilder builder = new CharsBuilder();
            JieChars.process(src).readBlockSize(blockSize)
                .encoder(fixedSize, (data, end) -> null)
                .writeTo(builder);
            assertEquals(builder.size(), 0);
        }
    }

    @Test
    public void testToReader() throws Exception {
        testToReader(100, 5);
        testToReader(10086, 11);
        testToReader(10086, 333);
        testToReader(10086, 22);
        testToReader(333, 10086);
        testToReader(20, 10086);
        testToReader(20, 40);
        {
            Reader in = JieChars.process(new char[0]).toReader();
            assertEquals(in.read(), -1);
            assertEquals(in.read(), -1);
            assertEquals(in.read(new char[1], 0, 0), 0);
            assertEquals(in.skip(-1), 0);
            assertEquals(in.skip(0), 0);
            in.close();
            in.close();
            expectThrows(IOException.class, () -> in.read());
            Reader nio = JieChars.process(new NioReader()).endOnZeroRead(true).toReader();
            assertEquals(nio.read(), -1);
            Reader empty = JieChars.process(new char[]{'9'}).encoder(((data, end) -> {
                CharsBuilder builder = new CharsBuilder();
                builder.append(data);
                if (end) {
                    builder.append(new char[]{'1', '2', '3'});
                }
                return CharBuffer.wrap(builder.toString());
            })).toReader();
            assertEquals(JieIO.string(empty).toCharArray(), new char[]{'9', '1', '2', '3'});
            assertEquals(empty.read(), -1);
            Reader err1 = JieChars.process(new CharsProcessorTest.ThrowReader(0)).toReader();
            expectThrows(IOException.class, () -> err1.close());
            Reader err2 = JieChars.process(new CharsProcessorTest.ThrowReader(2)).toReader();
            expectThrows(IOException.class, () -> err2.close());
            Reader err3 = JieChars.process(new CharsProcessorTest.ThrowReader(3)).toReader();
            expectThrows(IOException.class, () -> err3.read());
        }
        {
            boolean[] flag = {true};
            Reader in = JieChars.process(new char[1024]).readBlockSize(1).encoder(((data, end) -> {
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

    private void testToReader(int totalSize, int blockSize) throws Exception {
        char[] src = JieRandom.fill(new char[totalSize]);
        int times = totalSize / blockSize;
        CharsBuilder bb = new CharsBuilder();
        int pos = 0;
        for (int i = 0; i < times; i++) {
            bb.append(Arrays.copyOfRange(src, pos, pos + blockSize));
            bb.append('\r');
            pos += blockSize;
        }
        if (pos < totalSize) {
            bb.append(Arrays.copyOfRange(src, pos, totalSize));
            bb.append('\r');
        }
        char[] encoded = bb.toCharArray();
        {
            Reader in = JieChars.process(src).readBlockSize(blockSize).encoder(((data, end) -> {
                if (!data.hasRemaining()) {
                    return data;
                }
                CharsBuilder b = new CharsBuilder();
                b.append(data);
                b.append('\r');
                return CharBuffer.wrap(b.toString());
            })).toReader();
            assertEquals(JieIO.string(in).toCharArray(), encoded);
            assertEquals(in.read(), -1);
        }
        {
            Reader in = JieChars.process(src).readBlockSize(blockSize).encoder(((data, end) -> {
                if (!data.hasRemaining()) {
                    return data;
                }
                CharsBuilder b = new CharsBuilder();
                b.append(data);
                b.append('\r');
                return CharBuffer.wrap(b.toString());
            })).toReader();
            CharsBuilder builder = new CharsBuilder();
            while (true) {
                int b = in.read();
                if (b == -1) {
                    break;
                }
                builder.append((char) b);
            }
            assertEquals(builder.toCharArray(), encoded);
        }
        {
            Reader in = JieChars.process(src).readBlockSize(blockSize).encoder(((data, end) -> {
                if (!data.hasRemaining()) {
                    return data;
                }
                CharsBuilder b = new CharsBuilder();
                b.append(data);
                b.append('\r');
                return CharBuffer.wrap(b.toString());
            })).toReader();
            assertEquals(in.skip(666), Math.min(666, encoded.length));
            assertEquals(in.skip(1666), Math.min(1666, Math.max(encoded.length - 666, 0)));
        }
        {
            Reader in = JieChars.process(src).readBlockSize(blockSize).toReader();
            assertEquals(JieIO.string(in).toCharArray(), src);
            assertEquals(in.read(), -1);
        }
    }

    @Test
    public void testToByteProcessor() {
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
                JieChars.process(str).readBlockSize(blockSize).toByteProcessor(JieChars.defaultCharset()).toInputStream()
            );
            assertEquals(converted, bytes);
        }
        {
            char[] str = JieRandom.fill(new char[totalSize], '\u4e00', '\u9fff');
            byte[] bytes = new String(str).getBytes(JieChars.defaultCharset());
            byte[] converted = JieIO.read(
                JieChars.process(str).readBlockSize(blockSize).toByteProcessor(JieChars.defaultCharset()).toInputStream()
            );
            assertEquals(converted, bytes);
        }
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
}
