package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.ByteStream;
import xyz.sunqian.common.io.JieBuffer;

import java.nio.ByteBuffer;
import java.util.Arrays;

abstract class AbsCoder implements ByteCoder, ByteStream.Encoder {

    private byte[] doCode(byte[] source) throws EncodingException {
        int outputSize = getOutputSize(source.length);
        byte[] dst = new byte[outputSize];
        int len = doCode(source, 0, source.length, dst, 0);
        if (len == dst.length) {
            return dst;
        }
        return Arrays.copyOf(dst, len);
    }

    private ByteBuffer doCode(ByteBuffer source) throws EncodingException {
        int outputSize = getOutputSize(source.remaining());
        byte[] dst = new byte[outputSize];
        int len;
        if (source.hasArray()) {
            len = doCode(
                source.array(),
                JieBuffer.getArrayStartIndex(source),
                JieBuffer.getArrayEndIndex(source),
                dst,
                0
            );
            source.position(source.limit());
        } else {
            byte[] s = new byte[source.remaining()];
            source.get(s);
            len = doCode(s, 0, s.length, dst, 0);
        }
        if (len == dst.length) {
            return ByteBuffer.wrap(dst);
        }
        return ByteBuffer.wrap(Arrays.copyOf(dst, len));
    }

    private int doCode(byte[] source, byte[] dest) throws EncodingException {
        int outputSize = getOutputSize(source.length);
        checkCodingRemaining(outputSize, dest.length);
        return doCode(source, 0, source.length, dest, 0);
    }

    private int doCode(ByteBuffer source, ByteBuffer dest) throws EncodingException {
        int outputSize = getOutputSize(source.remaining());
        checkCodingRemaining(outputSize, dest.remaining());
        if (source.hasArray() && dest.hasArray()) {
            doCode(
                source.array(),
                JieBuffer.getArrayStartIndex(source),
                JieBuffer.getArrayEndIndex(source),
                dest.array(),
                JieBuffer.getArrayStartIndex(dest)
            );
            source.position(source.limit());
            dest.position(dest.position() + outputSize);
        } else {
            ByteBuffer dst = doCode(source);
            dest.put(dst);
        }
        return outputSize;
    }

    @Override
    public ByteStream.Encoder toStreamEncoder() {
        return this;
    }

    @Override
    public ByteBuffer encode(ByteBuffer data, boolean end) {
        return doCode(data);
    }

    protected abstract int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff);

    protected abstract void checkCodingRemaining(int srcRemaining, int dstRemaining);

    abstract static class En extends AbsCoder implements ByteEncoder {

        @Override
        public byte[] encode(byte[] source) throws EncodingException {
            return super.doCode(source);
        }

        @Override
        public ByteBuffer encode(ByteBuffer source) throws EncodingException {
            return super.doCode(source);
        }

        @Override
        public int encode(byte[] source, byte[] dest) throws EncodingException {
            return super.doCode(source, dest);
        }

        @Override
        public int encode(ByteBuffer source, ByteBuffer dest) throws EncodingException {
            return super.doCode(source, dest);
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
            return super.doCode(data);
        }

        @Override
        public ByteBuffer decode(ByteBuffer data) throws DecodingException {
            return super.doCode(data);
        }

        @Override
        public int decode(byte[] data, byte[] dest) throws DecodingException {
            return super.doCode(data, dest);
        }

        @Override
        public int decode(ByteBuffer data, ByteBuffer dest) throws DecodingException {
            return super.doCode(data, dest);
        }

        protected void checkCodingRemaining(int srcRemaining, int dstRemaining) {
            if (srcRemaining > dstRemaining) {
                throw new DecodingException("Remaining space of destination for decoding is not enough.");
            }
        }
    }
}