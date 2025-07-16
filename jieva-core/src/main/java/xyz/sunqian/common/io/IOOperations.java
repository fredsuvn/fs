package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

final class IOOperations {

    static final @Nonnull IOOperator DEFAULT_OPERATOR = IOOperator.newOperator(IOHelper.DEFAULT_BUFFER_SIZE);

    static long readTo0(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len, int bufSize
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            byte[] buf = new byte[(int) Math.min(len, bufSize)];
            long count = 0;
            while (count < len) {
                int readSize = src.read(buf, 0, (int) Math.min(buf.length, len - count));
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                dst.write(buf, 0, readSize);
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull InputStream src, @Nonnull WritableByteChannel dst, long len, int bufSize
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            byte[] arr = new byte[(int) Math.min(len, bufSize)];
            ByteBuffer buf = ByteBuffer.wrap(arr);
            long count = 0;
            while (count < len) {
                int readSize = src.read(arr, 0, (int) Math.min(arr.length, len - count));
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                buf.position(0);
                buf.limit(readSize);
                BufferKit.readTo(buf, dst);
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    ) throws IORuntimeException {
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

    static int readTo0(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IORuntimeException {
        if (len == 0 || dst.remaining() == 0) {
            return 0;
        }
        return readTo0WithActualLen(src, dst, Math.min(dst.remaining(), len));
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull OutputStream dst,
        long len,
        int bufSize
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            int actualBufSize = (int) Math.min(len, bufSize);
            ByteBuffer buf = ByteBuffer.allocate(actualBufSize);
            long count = 0;
            while (count < len) {
                int actualSize = (int) Math.min(actualBufSize, len - count);
                buf.limit(actualSize);
                int readSize = src.read(buf);
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                buf.flip();
                BufferKit.readTo(buf, dst);
                count += readSize;
                buf.clear();
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static long readTo0(
        @Nonnull ReadableByteChannel src,
        @Nonnull WritableByteChannel dst,
        long len,
        int bufSize
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            int actualBufSize = (int) Math.min(len, bufSize);
            ByteBuffer buf = ByteBuffer.allocate(actualBufSize);
            long count = 0;
            while (count < len) {
                int actualSize = (int) Math.min(actualBufSize, len - count);
                buf.limit(actualSize);
                int readSize = src.read(buf);
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                buf.flip();
                BufferKit.readTo(buf, dst);
                count += readSize;
                buf.clear();
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst
    ) throws IORuntimeException {
        if (dst.remaining() == 0) {
            return 0;
        }
        try {
            int count = 0;
            while (dst.hasRemaining()) {
                int readSize = src.read(dst);
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

    static int readTo0(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        int remaining = dst.remaining();
        if (remaining == 0) {
            return 0;
        }
        int pos = dst.position();
        int oldLimit = dst.limit();
        int actualLen = Math.min(remaining, len);
        dst.limit(pos + actualLen);
        int ret = readTo0(src, dst);
        if (ret <= 0) {
            return ret;
        }
        dst.position(pos + ret);
        dst.limit(oldLimit);
        return ret;
    }

    static int readTo0WithActualLen(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int actualLen
    ) throws IORuntimeException {
        try {
            if (dst.hasArray()) {
                int pos = dst.position();
                int ret = readTo0(src, dst.array(), BufferKit.arrayStartIndex(dst), actualLen);
                if (ret <= 0) {
                    return ret;
                }
                dst.position(pos + ret);
                return ret;
            } else {
                byte[] buf = new byte[actualLen];
                int ret = readTo0(src, buf, 0, buf.length);
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

    static long readTo0(
        @Nonnull Reader src, @Nonnull Appendable dst, long len, int bufSize
    ) throws IORuntimeException {
        if (len == 0) {
            return 0;
        }
        try {
            char[] buf = new char[(int) Math.min(len, bufSize)];
            long count = 0;
            while (count < len) {
                int readSize = src.read(buf, 0, (int) Math.min(buf.length, len - count));
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                IOKit.write(dst, buf, 0, readSize);
                count += readSize;
            }
            return count;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    static int readTo0(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IORuntimeException {
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

    static int readTo0(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IORuntimeException {
        if (len == 0 || dst.remaining() == 0) {
            return 0;
        }
        return readTo0WithActualLen(src, dst, Math.min(dst.remaining(), len));
    }

    static int readTo0WithActualLen(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int actualLen
    ) throws IORuntimeException {
        try {
            if (dst.hasArray()) {
                int pos = dst.position();
                int ret = readTo0(src, dst.array(), BufferKit.arrayStartIndex(dst), actualLen);
                if (ret <= 0) {
                    return ret;
                }
                dst.position(pos + ret);
                return ret;
            } else {
                char[] buf = new char[actualLen];
                int ret = readTo0(src, buf, 0, buf.length);
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
