package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.io.Reader;
import java.nio.CharBuffer;

final class CharReaderBack {

    static long readTo(
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

    static int readTo(
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

    static int readTo(
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

    static int readTo(
        @Nonnull CharBuffer src, @Nonnull Appendable dst, long len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        if (len == 0) {
            return 0;
        }
        if (src.remaining() == 0) {
            return -1;
        }
        try {
            int actualLen = len < 0 ? src.remaining() : (int) Math.min(src.remaining(), len);
            dst.append(src, 0, actualLen);
            src.position(src.position() + actualLen);
            return actualLen;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }
}
