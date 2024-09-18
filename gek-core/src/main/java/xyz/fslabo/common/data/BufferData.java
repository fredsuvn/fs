package xyz.fslabo.common.data;

import xyz.fslabo.common.base.JieCheck;
import xyz.fslabo.common.io.JieBuffer;
import xyz.fslabo.common.io.JieIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class BufferData implements GekData.OfBuffer {

    private final ByteBuffer buffer;

    BufferData(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public boolean isArrayData() {
        return false;
    }

    @Override
    public OfArray asArrayData() {
        throw new GekDataException("Not an array data.");
    }

    @Override
    public boolean isBufferData() {
        return true;
    }

    @Override
    public OfBuffer asBufferData() {
        return this;
    }

    @Override
    public boolean isStreamData() {
        return false;
    }

    @Override
    public OfStream asStreamData() {
        throw new GekDataException("Not a stream data.");
    }

    @Override
    public ByteBuffer buffer() {
        return buffer;
    }

    @Override
    public int write(byte[] dest, int offset, int length) {
        JieCheck.checkRangeInBounds(offset, offset + length, 0, dest.length);
        int remaining = buffer.remaining();
        if (remaining <= 0) {
            return -1;
        }
        int len = Math.min(remaining, length);
        if (len <= 0) {
            return 0;
        }
        buffer.get(dest, offset, len);
        return len;
    }

    @Override
    public int write(ByteBuffer dest, int length) {
        return JieBuffer.readTo(buffer, dest, length);
    }

    @Override
    public long write(OutputStream dest) {
        return JieIO.readTo(JieIO.toInputStream(buffer), dest);
    }

    @Override
    public long write(OutputStream dest, long length) {
        return JieIO.readTo(JieIO.toInputStream(buffer), dest, length);
    }

    @Override
    public byte[] toArray() {
        if (!buffer.hasRemaining()) {
            return null;
        }
        return JieBuffer.read(buffer);
    }

    @Override
    public InputStream asInputStream() {
        return JieIO.toInputStream(buffer);
    }
}
