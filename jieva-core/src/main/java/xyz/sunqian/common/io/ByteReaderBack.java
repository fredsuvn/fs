package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

final class ByteReaderBack {

    static long readTo(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len, int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            byte[] buf = new byte[JieIO.bufferSize(len, bufSize)];
            long count = 0;
            while (true) {
                int readSize = len < 0 ?
                    src.read(buf)
                    :
                    src.read(buf, 0, (int) Math.min(buf.length, len - count));
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                dst.write(buf, 0, readSize);
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
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
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
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IORuntimeException {
        if (len == 0 || dst.remaining() == 0) {
            return 0;
        }
        try {
            if (dst.hasArray()) {
                byte[] buf = dst.array();
                int off = JieBuffer.arrayStartIndex(dst);
                int actualLen = len < 0 ? dst.remaining() : Math.min(dst.remaining(), len);
                int ret = readTo(src, buf, off, actualLen);
                if (ret <= 0) {
                    return ret;
                }
                dst.position(dst.position() + ret);
                return ret;
            } else {
                byte[] buf = new byte[len < 0 ? dst.remaining() : Math.min(dst.remaining(), len)];
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

    static long readTo(
        @Nonnull ByteReader reader,
        @Nonnull ReadableByteChannel src,
        @Nonnull WritableByteChannel dst,
        long len,
        int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            ByteBuffer buf = ByteBuffer.allocate(JieIO.bufferSize(len, bufSize));
            long count = 0;
            while (true) {
                int limit = len < 0 ? buf.remaining() : (int) Math.min(buf.remaining(), len - count);
                buf.limit(limit);
                int readSize = src.read(buf);
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                buf.flip();
                JieBuffer.readTo(buf, dst);
                buf.clear();
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
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        if (len == 0 || dst.remaining() == 0) {
            return 0;
        }
        try {
            int oldLimit = dst.limit();
            int newLimit = len < 0 ? oldLimit : Math.min(oldLimit, dst.position() + len);
            dst.limit(newLimit);
            int pos = dst.position();
            do {
                int readSize = src.read(dst);
                if (readSize < 0) {
                    dst.limit(oldLimit);
                    int posNow = dst.position();
                    return posNow == pos ? -1 : posNow - pos;
                }
            } while (dst.remaining() != 0);
            dst.limit(oldLimit);
            return dst.position() - pos;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }
}
