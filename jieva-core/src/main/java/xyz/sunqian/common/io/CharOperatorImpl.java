package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.JieCheck;

import java.io.Reader;
import java.nio.CharBuffer;

final class CharOperatorImpl implements CharOperator {

    private final int bufferSize;

    CharOperatorImpl(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public int bufferSize() {
        return bufferSize;
    }

    @Override
    public long readTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return readTo0(src, dst, -1, bufferSize());
    }

    @Override
    public long readTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return readTo0(src, dst, len, bufferSize());
    }

    @Override
    public int readTo(
        @Nonnull Reader src, char @Nonnull [] dst
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return readTo0(src, dst, 0, dst.length);
    }

    @Override
    public int readTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        JieCheck.checkOffsetLength(dst.length, off, len);
        return readTo0(src, dst, off, len);
    }

    @Override
    public int readTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        return readTo0(src, dst, -1);
    }

    @Override
    public int readTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return readTo0(src, dst, len);
    }

    private long readTo0(
        @Nonnull Reader src, @Nonnull Appendable dst, long len, int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            char[] buf = new char[JieIO.bufferSize(len, bufSize)];
            long count = 0;
            while (true) {
                int readSize = len < 0 ?
                    src.read(buf)
                    :
                    src.read(buf, 0, (int) Math.min(buf.length, len - count));
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                JieIO.write(dst, buf, 0, readSize);
                count += readSize;
                if (len > 0 && count >= len) {
                    return count;
                }
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    private int readTo0(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            int count = 0;
            while (count < len) {
                int readSize = src.read(dst, off + count, len - count);
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    private int readTo0(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IORuntimeException {
        if (len == 0 || dst.remaining() == 0) {
            return 0;
        }
        try {
            if (dst.hasArray()) {
                char[] buf = dst.array();
                int off = JieBuffer.arrayStartIndex(dst);
                int actualLen = len < 0 ? dst.remaining() : Math.min(dst.remaining(), len);
                int ret = readTo(src, buf, off, actualLen);
                if (ret <= 0) {
                    return ret;
                }
                dst.position(dst.position() + ret);
                return ret;
            } else {
                char[] buf = new char[len < 0 ? dst.remaining() : Math.min(dst.remaining(), len)];
                int ret = readTo(src, buf, 0, buf.length);
                if (ret <= 0) {
                    return ret;
                }
                dst.put(buf, 0, ret);
                return ret;
            }
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }
}
