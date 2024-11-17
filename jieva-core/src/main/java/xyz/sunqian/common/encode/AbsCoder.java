package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.ByteStream;
import xyz.sunqian.common.io.JieBuffer;

import java.nio.ByteBuffer;
import java.util.Arrays;

abstract class AbsCoder implements ByteCoder, ByteStream.Encoder {

    protected abstract int getOutputSize(int length, boolean end);

    protected abstract void checkCodingRemaining(int srcRemaining, int dstRemaining);

    protected abstract int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean end);

    protected byte[] doCode(byte[] source, boolean end) throws EncodingException {
        int outputSize = getOutputSize(source.length, end);
        byte[] dst = new byte[outputSize];
        int len = doCode(source, 0, source.length, dst, 0, end);
        if (len == dst.length) {
            return dst;
        }
        return Arrays.copyOf(dst, len);
    }

    protected ByteBuffer doCode(ByteBuffer source, boolean end) throws EncodingException {
        int outputSize = getOutputSize(source.remaining(), end);
        byte[] dst = new byte[outputSize];
        int len;
        if (source.hasArray()) {
            len = doCode(
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
            len = doCode(s, 0, s.length, dst, 0, end);
        }
        if (len == dst.length) {
            return ByteBuffer.wrap(dst);
        }
        return ByteBuffer.wrap(Arrays.copyOf(dst, len));
    }

    protected int doCode(byte[] source, byte[] dest, boolean end) throws EncodingException {
        int outputSize = getOutputSize(source.length, end);
        checkCodingRemaining(outputSize, dest.length);
        return doCode(source, 0, source.length, dest, 0, end);
    }

    protected int doCode(ByteBuffer source, ByteBuffer dest, boolean end) throws EncodingException {
        int outputSize = getOutputSize(source.remaining(), end);
        checkCodingRemaining(outputSize, dest.remaining());
        if (source.hasArray() && dest.hasArray()) {
            doCode(
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
            ByteBuffer dst = doCode(source, end);
            dest.put(dst);
        }
        return outputSize;
    }

    @Override
    public int getOutputSize(int inputSize) throws CodingException {
        return getOutputSize(inputSize, true);
    }

    @Override
    public ByteStream.Encoder streamEncoder() {
        return this;
    }

    @Override
    public ByteBuffer encode(ByteBuffer data, boolean end) {
        return doCode(data, end);
    }

    abstract static class En extends AbsCoder implements ByteEncoder {

        @Override
        public byte[] encode(byte[] source) throws EncodingException {
            return super.doCode(source, true);
        }

        @Override
        public ByteBuffer encode(ByteBuffer source) throws EncodingException {
            return super.doCode(source, true);
        }

        @Override
        public int encode(byte[] source, byte[] dest) throws EncodingException {
            return super.doCode(source, dest, true);
        }

        @Override
        public int encode(ByteBuffer source, ByteBuffer dest) throws EncodingException {
            return super.doCode(source, dest, true);
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
            return super.doCode(data, true);
        }

        @Override
        public ByteBuffer decode(ByteBuffer data) throws DecodingException {
            return super.doCode(data, true);
        }

        @Override
        public int decode(byte[] data, byte[] dest) throws DecodingException {
            return super.doCode(data, dest, true);
        }

        @Override
        public int decode(ByteBuffer data, ByteBuffer dest) throws DecodingException {
            return super.doCode(data, dest, true);
        }

        protected void checkCodingRemaining(int srcRemaining, int dstRemaining) {
            if (srcRemaining > dstRemaining) {
                throw new DecodingException("Remaining space of destination for decoding is not enough.");
            }
        }
    }
}