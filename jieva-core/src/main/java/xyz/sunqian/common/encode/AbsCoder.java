package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.ByteProcessor;
import xyz.sunqian.common.io.JieBuffer;

import java.nio.ByteBuffer;
import java.util.Arrays;

abstract class AbsCoder implements ByteCoder {

    protected abstract int getOutputSize(int length, boolean end);

    protected abstract void checkCodingRemaining(int srcRemaining, int dstRemaining);

    protected abstract int doCode(
        long startPos,
        byte[] src,
        int srcOff,
        int srcEnd,
        byte[] dst,
        int dstOff,
        boolean end
    );

    protected byte[] doCode(long startPos, byte[] source, boolean end) throws EncodingException {
        int outputSize = getOutputSize(source.length, end);
        byte[] dst = new byte[outputSize];
        int len = doCode(startPos, source, 0, source.length, dst, 0, end);
        if (len == dst.length) {
            return dst;
        }
        return Arrays.copyOf(dst, len);
    }

    protected ByteBuffer doCode(long startPos, ByteBuffer source, boolean end) throws EncodingException {
        int outputSize = getOutputSize(source.remaining(), end);
        byte[] dst = new byte[outputSize];
        int len;
        if (source.hasArray()) {
            len = doCode(
                startPos,
                source.array(),
                JieBuffer.getArrayStartIndex(source),
                JieBuffer.getArrayEndIndex(source),
                dst,
                0,
                end
            );
            source.position(source.limit());
        } else {
            byte[] s = new byte[source.remaining()];
            source.get(s);
            len = doCode(startPos, s, 0, s.length, dst, 0, end);
        }
        if (len == dst.length) {
            return ByteBuffer.wrap(dst);
        }
        return ByteBuffer.wrap(Arrays.copyOf(dst, len));
    }

    protected int doCode(long startPos, byte[] source, byte[] dest, boolean end) throws EncodingException {
        int outputSize = getOutputSize(source.length, end);
        checkCodingRemaining(outputSize, dest.length);
        return doCode(startPos, source, 0, source.length, dest, 0, end);
    }

    protected int doCode(long startPos, ByteBuffer source, ByteBuffer dest, boolean end) throws EncodingException {
        int outputSize = getOutputSize(source.remaining(), end);
        checkCodingRemaining(outputSize, dest.remaining());
        if (source.hasArray() && dest.hasArray()) {
            doCode(
                startPos,
                source.array(),
                JieBuffer.getArrayStartIndex(source),
                JieBuffer.getArrayEndIndex(source),
                dest.array(),
                JieBuffer.getArrayStartIndex(dest),
                end
            );
            source.position(source.limit());
            dest.position(dest.position() + outputSize);
        } else {
            ByteBuffer dst = doCode(startPos, source, end);
            dest.put(dst);
        }
        return outputSize;
    }

    @Override
    public int getOutputSize(int inputSize) throws CodingException {
        return getOutputSize(inputSize, true);
    }

    @Override
    public ByteProcessor.Encoder streamEncoder() {
        ByteProcessor.Encoder encoder = new ByteProcessor.Encoder() {

            private long startPos = 0;

            @Override
            public ByteBuffer encode(ByteBuffer data, boolean end) {
                int pos = data.position();
                ByteBuffer ret = doCode(startPos, data, end);
                startPos += (data.position() - pos);
                return ret;
            }
        };
        return ByteProcessor.roundEncoder(encoder, getBlockSize());
    }

    abstract static class En extends AbsCoder implements ByteEncoder {

        @Override
        public byte[] encode(byte[] source) throws EncodingException {
            return super.doCode(0, source, true);
        }

        @Override
        public ByteBuffer encode(ByteBuffer source) throws EncodingException {
            return super.doCode(0, source, true);
        }

        @Override
        public int encode(byte[] source, byte[] dest) throws EncodingException {
            return super.doCode(0, source, dest, true);
        }

        @Override
        public int encode(ByteBuffer source, ByteBuffer dest) throws EncodingException {
            return super.doCode(0, source, dest, true);
        }

        protected void checkCodingRemaining(int srcRemaining, int dstRemaining) {
            if (srcRemaining > dstRemaining) {
                throw new EncodingException("Remaining space of destination for encoding is not enough.");
            }
        }
    }

    abstract static class De extends AbsCoder implements ByteDecoder {

        @Override
        public byte[] decode(byte[] data) throws DecodingException {
            return super.doCode(0, data, true);
        }

        @Override
        public ByteBuffer decode(ByteBuffer data) throws DecodingException {
            return super.doCode(0, data, true);
        }

        @Override
        public int decode(byte[] data, byte[] dest) throws DecodingException {
            return super.doCode(0, data, dest, true);
        }

        @Override
        public int decode(ByteBuffer data, ByteBuffer dest) throws DecodingException {
            return super.doCode(0, data, dest, true);
        }

        protected void checkCodingRemaining(int srcRemaining, int dstRemaining) {
            if (srcRemaining > dstRemaining) {
                throw new DecodingException("Remaining space of destination for decoding is not enough.");
            }
        }
    }
}