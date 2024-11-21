package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.coll.JieColl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Collections;
import java.util.List;

final class CharSourceImpl implements CharSource {

    private final Object source;
    private Object dest;
    private long readLimit = -1;
    private int blockSize = JieIO.BUFFER_SIZE;
    private boolean endOnZeroRead = false;
    private List<Encoder> encoders;

    CharSourceImpl(Reader source) {
        this.source = source;
    }

    CharSourceImpl(char[] source) {
        this.source = source;
    }

    CharSourceImpl(CharBuffer source) {
        this.source = source;
    }

    CharSourceImpl(CharSequence source) {
        this.source = source;
    }

    @Override
    public long to(Appendable dest) {
        this.dest = dest;
        return start();
    }

    @Override
    public long to(char[] dest) {
        this.dest = dest;
        return start();
    }

    @Override
    public long to(char[] dest, int offset, int length) {
        try {
            this.dest = CharBuffer.wrap(dest, offset, length);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
        return start();
    }

    @Override
    public long to(CharBuffer dest) {
        this.dest = dest;
        return start();
    }

    @Override
    public CharSource readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public CharSource blockSize(int blockSize) {
        if (blockSize <= 0) {
            throw new IORuntimeException("blockSize must > 0!");
        }
        this.blockSize = blockSize;
        return this;
    }

    @Override
    public CharSource endOnZeroRead(boolean endOnZeroRead) {
        this.endOnZeroRead = endOnZeroRead;
        return this;
    }

    @Override
    public CharSource encoder(Encoder encoder) {
        this.encoders = Collections.singletonList(encoder);
        return this;
    }

    @Override
    public CharSource encoders(Iterable<Encoder> encoders) {
        this.encoders = JieColl.toList(encoders);
        return this;
    }

    private long start() {
        if (source == null || dest == null) {
            throw new IORuntimeException("Source or dest is null!");
        }
        if (readLimit == 0) {
            return 0;
        }
        try {
            if (encoders == null) {
                if (source instanceof char[]) {
                    if (dest instanceof char[]) {
                        return charsToChars((char[]) source, (char[]) dest);
                    }
                    if (dest instanceof CharBuffer) {
                        return charsToBuffer((char[]) source, (CharBuffer) dest);
                    }
                    return charsToAppender((char[]) source, (Appendable) dest);
                } else if (source instanceof CharBuffer) {
                    if (dest instanceof char[]) {
                        return bufferToChars((CharBuffer) source, (char[]) dest);
                    }
                    return bufferToAppender((CharBuffer) source, (Appendable) dest);
                } else if (source instanceof CharSequence) {
                    if (dest instanceof char[]) {
                        return charSeqToChars((CharSequence) source, (char[]) dest);
                    }
                    return charSeqToAppender((CharSequence) source, (Appendable) dest);
                }
            }
            return start0();
        } catch (IOEncodingException e) {
            throw e;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    private long charsToChars(char[] src, char[] dst) {
        int len = getDirectLen(src.length);
        System.arraycopy(src, 0, dst, 0, len);
        return len;
    }

    private long charsToBuffer(char[] src, CharBuffer dst) {
        int len = getDirectLen(src.length);
        dst.put(src, 0, len);
        return len;
    }

    private long charsToAppender(char[] src, Appendable dst) throws IOException {
        int len = getDirectLen(src.length);
        dst.append(JieString.asChars(src, 0, len));
        return len;
    }

    private long bufferToChars(CharBuffer src, char[] dst) {
        int len = getDirectLen(src.remaining());
        src.get(dst, 0, len);
        return len;
    }

    private long bufferToAppender(CharBuffer src, Appendable dst) throws IOException {
        int len = getDirectLen(src.remaining());
        int pos = src.position();
        int newPos = pos + len;
        dst.append(src, 0, len);
        src.position(newPos);
        return len;
    }

    private long charSeqToChars(CharSequence src, char[] dst) throws IOException {
        int len = getDirectLen(src.length());
        if (src instanceof String) {
            ((String) src).getChars(0, len, dst, 0);
        } else {
            for (int i = 0; i < len; i++) {
                dst[i] = src.charAt(i);
            }
        }
        return len;
    }

    private long charSeqToAppender(CharSequence src, Appendable dst) throws IOException {
        int len = getDirectLen(src.length());
        dst.append(src, 0, len);
        return len;
    }

    private int getDirectLen(int srcSize) {
        return readLimit < 0 ? srcSize : Math.min(srcSize, (int) readLimit);
    }

    private long start0() throws Exception {
        BufferIn in = toBufferIn(source);
        BufferOut out = toBufferOut(dest);
        return readTo(in, out);
    }

    private BufferIn toBufferIn(Object src) {
        int actualBlockSize = getActualBlockSize();
        if (src instanceof Reader) {
            return new ReaderBufferIn((Reader) src, actualBlockSize, readLimit);
        }
        if (src instanceof char[]) {
            return new CharsBufferIn((char[]) src, actualBlockSize, readLimit);
        }
        if (src instanceof CharBuffer) {
            return new BufferBufferIn((CharBuffer) src, actualBlockSize, readLimit);
        }
        if (src instanceof CharSequence) {
            return new CharSeqBufferIn((CharSequence) src, actualBlockSize, readLimit);
        }
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
    }

    private BufferOut toBufferOut(Object dst) {
        if (dst instanceof char[]) {
            return new AppendableBufferOut(CharBuffer.wrap((char[]) dst));
        }
        if (dst instanceof CharBuffer) {
            return new AppendableBufferOut(JieIO.writer((CharBuffer) dst));
        }
        if (dst instanceof Appendable) {
            return new AppendableBufferOut((Appendable) dst);
        }
        throw new IORuntimeException("Unexpected destination type: " + dst.getClass());
    }

    private int getActualBlockSize() {
        if (readLimit < 0) {
            return blockSize;
        }
        return (int) Math.min(readLimit, blockSize);
    }

    private long readTo(BufferIn in, BufferOut out) throws Exception {
        long count = 0;
        while (true) {
            CharBuffer buf = in.read();
            if (buf == null || !buf.hasRemaining()) {
                if (count == 0) {
                    return buf == null ? -1 : 0;
                }
                if (encoders != null) {
                    CharBuffer encoded = encode(JieChars.emptyBuffer(), true);
                    out.write(encoded);
                }
                break;
            }
            int readSize = buf.remaining();
            count += readSize;
            if (encoders != null) {
                CharBuffer encoded;
                if (readSize < blockSize) {
                    encoded = encode(buf, true);
                    out.write(encoded);
                    break;
                } else {
                    encoded = encode(buf, false);
                    out.write(encoded);
                }
            } else {
                out.write(buf);
                if (readSize < blockSize) {
                    break;
                }
            }
        }
        return count;
    }

    private CharBuffer encode(CharBuffer buffer, boolean end) throws IOEncodingException {
        CharBuffer data = buffer;
        for (Encoder encoder : encoders) {
            CharBuffer encoded;
            try {
                encoded = encoder.encode(data, end);
            } catch (Throwable e) {
                throw new IOEncodingException(e);
            }
            if (!encoded.hasRemaining()) {
                return encoded;
            }
            data = encoded;
        }
        return data;
    }

    private interface BufferIn {
        @Nullable
        CharBuffer read() throws Exception;
    }

    private interface BufferOut {
        void write(CharBuffer buffer) throws Exception;
    }

    private final class ReaderBufferIn implements BufferIn {

        private final Reader source;
        private final char[] block;
        private final CharBuffer blockBuffer;
        private final long limit;
        private long remaining;

        private ReaderBufferIn(Reader source, int blockSize, long limit) {
            this.source = source;
            this.block = new char[limit < 0 ? blockSize : (int) Math.min(blockSize, limit)];
            this.blockBuffer = CharBuffer.wrap(block);
            this.limit = limit;
            this.remaining = limit;
        }

        @Override
        public CharBuffer read() throws IOException {
            int readSize = limit < 0 ? block.length : (int) Math.min(remaining, block.length);
            if (readSize <= 0) {
                return null;
            }
            int hasRead = 0;
            boolean zeroRead = false;
            while (hasRead < readSize) {
                int size = source.read(block, hasRead, readSize - hasRead);
                if (size < 0) {
                    break;
                }
                if (size == 0 && endOnZeroRead) {
                    zeroRead = true;
                    break;
                }
                hasRead += size;
            }
            if (hasRead == 0) {
                if (zeroRead) {
                    return JieChars.emptyBuffer();
                }
                return null;
            }
            blockBuffer.position(0);
            blockBuffer.limit(hasRead);
            if (limit > 0) {
                remaining -= hasRead;
            }
            return blockBuffer;
        }
    }

    private static final class CharsBufferIn implements BufferIn {

        private final char[] source;
        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private final long limit;
        private long remaining;

        private CharsBufferIn(char[] source, int blockSize, long limit) {
            this.source = source;
            this.sourceBuffer = CharBuffer.wrap(source);
            this.blockSize = blockSize;
            this.limit = limit;
            this.remaining = limit;
        }

        @Override
        public CharBuffer read() {
            int readSize = limit < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return null;
            }
            if (pos >= source.length) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + readSize, source.length);
            sourceBuffer.limit(newPos);
            int size = newPos - pos;
            pos = newPos;
            if (limit > 0) {
                remaining -= size;
            }
            return sourceBuffer;
        }
    }

    private static final class BufferBufferIn implements BufferIn {

        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private final long limit;
        private long remaining;
        private final int sourceRemaining;

        private BufferBufferIn(CharBuffer source, int blockSize, long limit) {
            this.sourceBuffer = source.slice();
            this.blockSize = blockSize;
            this.limit = limit;
            this.remaining = limit;
            this.sourceRemaining = source.remaining();
        }

        @Override
        public CharBuffer read() {
            int readSize = limit < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return null;
            }
            if (pos >= sourceRemaining) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + readSize, sourceRemaining);
            sourceBuffer.limit(newPos);
            int size = newPos - pos;
            pos = newPos;
            if (limit > 0) {
                remaining -= size;
            }
            return sourceBuffer;
        }
    }

    private static final class CharSeqBufferIn implements BufferIn {

        private final CharSequence source;
        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private final long limit;
        private long remaining;

        private CharSeqBufferIn(CharSequence source, int blockSize, long limit) {
            this.source = source;
            this.sourceBuffer = CharBuffer.wrap(source);
            this.blockSize = blockSize;
            this.limit = limit;
            this.remaining = limit;
        }

        @Override
        public CharBuffer read() {
            int readSize = limit < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return null;
            }
            if (pos >= source.length()) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + readSize, source.length());
            sourceBuffer.limit(newPos);
            int size = newPos - pos;
            pos = newPos;
            if (limit > 0) {
                remaining -= size;
            }
            return sourceBuffer;
        }
    }

    private static final class AppendableBufferOut implements BufferOut {

        private final Appendable dest;

        private AppendableBufferOut(Appendable dest) {
            this.dest = dest;
        }

        @Override
        public void write(CharBuffer buffer) throws IOException {
            if (dest instanceof Writer) {
                write(buffer, (Writer) dest);
                return;
            }
            if (buffer.hasArray()) {
                int remaining = buffer.remaining();
                dest.append(JieString.asChars(
                    buffer.array(),
                    JieBuffer.getArrayStartIndex(buffer),
                    JieBuffer.getArrayEndIndex(buffer)
                ));
                buffer.position(buffer.position() + remaining);
            } else {
                char[] buf = new char[buffer.remaining()];
                buffer.get(buf);
                dest.append(JieString.asChars(buf, 0, buf.length));
            }
        }

        private void write(CharBuffer buffer, Writer writer) throws IOException {
            if (buffer.hasArray()) {
                int remaining = buffer.remaining();
                writer.write(buffer.array(), JieBuffer.getArrayStartIndex(buffer), remaining);
                buffer.position(buffer.position() + remaining);
            } else {
                char[] buf = new char[buffer.remaining()];
                buffer.get(buf);
                writer.write(buf);
            }
        }
    }

    private static abstract class AbsEncoder implements CharSource.Encoder {

        protected final CharSource.Encoder encoder;
        protected char[] buf = JieChars.emptyChars();

        protected AbsEncoder(CharSource.Encoder encoder) {
            this.encoder = encoder;
        }

        protected CharBuffer totalData(CharBuffer data) {
            if (JieArray.isEmpty(buf)) {
                return data;
            }
            CharBuffer total = CharBuffer.allocate(totalSize(data));
            total.put(buf);
            total.put(data);
            total.flip();
            return total;
        }

        protected int totalSize(CharBuffer data) {
            return buf.length + data.remaining();
        }
    }

    final static class RoundEncoder extends AbsEncoder {

        private final int expectedBlockSize;

        RoundEncoder(CharSource.Encoder encoder, int expectedBlockSize) {
            super(encoder);
            this.expectedBlockSize = expectedBlockSize;
        }

        @Override
        public CharBuffer encode(CharBuffer data, boolean end) {
            if (end) {
                return encoder.encode(totalData(data), true);
            }
            int size = totalSize(data);
            if (size == expectedBlockSize) {
                CharBuffer total = totalData(data);
                buf = JieChars.emptyChars();
                return encoder.encode(total, false);
            }
            if (size < expectedBlockSize) {
                char[] newBuf = new char[size];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                data.get(newBuf, buf.length, data.remaining());
                buf = newBuf;
                return JieChars.emptyBuffer();
            }
            int remainder = size % expectedBlockSize;
            if (remainder == 0) {
                CharBuffer total = totalData(data);
                buf = JieChars.emptyChars();
                return encoder.encode(total, false);
            }
            int roundSize = size / expectedBlockSize * expectedBlockSize;
            CharBuffer round = roundData(data, roundSize);
            buf = new char[remainder];
            data.get(buf);
            return encoder.encode(round, false);
        }

        private CharBuffer roundData(CharBuffer data, int roundSize) {
            CharBuffer round = CharBuffer.allocate(roundSize);
            round.put(buf);
            int sliceSize = roundSize - buf.length;
            CharBuffer slice = JieChars.slice(data, 0, sliceSize);
            data.position(data.position() + sliceSize);
            round.put(slice);
            round.flip();
            return round;
        }
    }

    final static class BufferedEncoder extends AbsEncoder {

        BufferedEncoder(CharSource.Encoder encoder) {
            super(encoder);
        }

        @Override
        public CharBuffer encode(CharBuffer data, boolean end) {
            CharBuffer total = totalData(data);
            CharBuffer ret = encoder.encode(total, end);
            if (total.hasRemaining() && !end) {
                buf = new char[total.remaining()];
                total.get(buf);
            }
            return ret;
        }
    }
}
