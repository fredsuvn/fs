package xyz.sunqian.common.net.data;

import xyz.sunqian.common.base.CheckKit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

final class ArrayData implements GekData.OfArray {

    private final byte[] array;
    private final int arrayOffset;
    private final int arrayLength;

    ArrayData(byte[] array, int arrayOffset, int arrayLength) {
        this.array = array;
        this.arrayOffset = arrayOffset;
        this.arrayLength = arrayLength;
    }

    @Override
    public boolean isArrayData() {
        return true;
    }

    @Override
    public OfArray asArrayData() {
        return this;
    }

    @Override
    public boolean isBufferData() {
        return false;
    }

    @Override
    public OfBuffer asBufferData() {
        throw new GekDataException("Not a buffer data.");
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
    public byte[] array() {
        return array;
    }

    @Override
    public int arrayOffset() {
        return arrayOffset;
    }

    @Override
    public int length() {
        return arrayLength;
    }

    @Override
    public int write(byte[] dest, int offset, int length) {
        CheckKit.checkRangeInBounds(offset, offset + length, 0, dest.length);
        if (arrayLength == 0) {
            return -1;
        }
        int len = Math.min(length(), length);
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(array, arrayOffset, dest, offset, len);
        return len;
    }

    @Override
    public int write(ByteBuffer dest, int length) {
        if (arrayLength == 0) {
            return -1;
        }
        int len = Math.min(dest.remaining(), length);
        len = Math.min(len, length());
        if (len <= 0) {
            return 0;
        }
        dest.put(array, arrayOffset, len);
        return len;
    }

    @Override
    public long write(OutputStream dest) {
        if (arrayLength == 0) {
            return -1;
        }
        try {
            dest.write(array, arrayOffset, arrayLength);
            return arrayLength;
        } catch (IOException e) {
            throw new GekDataException(e);
        }
    }

    @Override
    public long write(OutputStream dest, long length) {
        if (arrayLength == 0) {
            return -1;
        }
        int len = (int) Math.min(length, length());
        if (len <= 0) {
            return 0;
        }
        try {
            dest.write(array, arrayOffset, len);
            return len;
        } catch (IOException e) {
            throw new GekDataException(e);
        }
    }

    @Override
    public byte[] toArray() {
        if (arrayLength == 0) {
            return null;
        }
        return Arrays.copyOfRange(array, arrayOffset, arrayOffset + arrayLength);
    }

    @Override
    public InputStream asInputStream() {
        return new ByteArrayInputStream(array, arrayOffset, arrayLength);
    }
}
