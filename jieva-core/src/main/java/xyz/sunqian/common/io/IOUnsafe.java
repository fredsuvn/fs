package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.nio.ByteBuffer;

/**
 * This class provides unsafe I/O methods.
 * <p>
 * Some of the methods do not validate the passed arguments, they assume the arguments are valid. And if the wrong
 * arguments are passed in, it may cause the undefined behavior.
 *
 * @author sunqian
 */
public class IOUnsafe {

    // /**
    //  * Reads all bytes from the given input stream to the given output stream.
    //  *
    //  * @param src     the given input stream
    //  * @param dst     the given output stream
    //  * @param bufSize the buffer size for reading, expected to {@code > 0}
    //  * @return actual read number, or {@code -1} if no bytes read and reaches the end of the input stream
    //  * @throws IORuntimeException if any I/O error occurs
    //  */
    // public static long readTo(
    //     @Nonnull InputStream src, @Nonnull OutputStream dst, int bufSize
    // ) throws IORuntimeException {
    //     try {
    //         byte[] buf = new byte[bufSize];
    //         long count = 0;
    //         while (true) {
    //             int readSize = src.read(buf);
    //             if (readSize < 0) {
    //                 return count == 0 ? -1 : count;
    //             }
    //             dst.write(buf, 0, readSize);
    //             count += readSize;
    //         }
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // /**
    //  * Reads bytes of the specified length from the given input stream to the given output stream.
    //  *
    //  * @param src     the given input stream
    //  * @param dst     the given output stream
    //  * @param len     the specified length to read, expected to {@code >= 0}
    //  * @param bufSize the buffer size for reading, expected to {@code > 0}
    //  * @return actual read number, or {@code -1} if no bytes read and reaches the end of the input stream
    //  * @throws IORuntimeException if any I/O error occurs
    //  */
    // public static long readTo(
    //     @Nonnull InputStream src, @Nonnull OutputStream dst, long len, int bufSize
    // ) throws IORuntimeException {
    //     if (len == 0) {
    //         return 0;
    //     }
    //     try {
    //         byte[] buf = new byte[(int) Math.min(bufSize, len)];
    //         long count = 0;
    //         while (true) {
    //             int readSize = src.read(buf, 0, (int) Math.min(buf.length, len - count));
    //             if (readSize < 0) {
    //                 return count == 0 ? -1 : count;
    //             }
    //             dst.write(buf, 0, readSize);
    //             count += readSize;
    //             if (count >= len) {
    //                 return count;
    //             }
    //         }
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // /**
    //  * Reads all bytes from the given input stream to the given writeable channel.
    //  *
    //  * @param src     the given input stream
    //  * @param dst     the given writeable channel
    //  * @param bufSize the buffer size for reading, expected to {@code > 0}
    //  * @return actual read number, or {@code -1} if no bytes read and reaches the end of the input stream
    //  * @throws IORuntimeException if any I/O error occurs
    //  */
    // public static long readTo0(
    //     @Nonnull InputStream src, @Nonnull WritableByteChannel dst, int bufSize
    // ) throws IORuntimeException {
    //     try {
    //         byte[] arr = new byte[bufSize];
    //         ByteBuffer buf = ByteBuffer.wrap(arr);
    //         long count = 0;
    //         while (true) {
    //             int readSize = src.read(arr);
    //             if (readSize < 0) {
    //                 return count == 0 ? -1 : count;
    //             }
    //             buf.position(0);
    //             buf.limit(readSize);
    //             BufferKit.readTo(buf, dst);
    //             count += readSize;
    //             if (len > 0 && count >= len) {
    //                 return count;
    //             }
    //         }
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // static long readTo0(
    //     @Nonnull InputStream src, @Nonnull WritableByteChannel dst, long len, int bufSize
    // ) throws IORuntimeException {
    //     if (len == 0) {
    //         return 0;
    //     }
    //     try {
    //         byte[] arr = new byte[IOKit.bufferSize(len, bufSize)];
    //         ByteBuffer buf = ByteBuffer.wrap(arr);
    //         long count = 0;
    //         while (true) {
    //             int readSize = len < 0 ?
    //                 src.read(arr)
    //                 :
    //                 src.read(arr, 0, (int) Math.min(arr.length, len - count));
    //             if (readSize < 0) {
    //                 return count == 0 ? -1 : count;
    //             }
    //             buf.position(0);
    //             buf.limit(readSize);
    //             BufferKit.readTo(buf, dst);
    //             count += readSize;
    //             if (len > 0 && count >= len) {
    //                 return count;
    //             }
    //         }
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // static int readTo0(
    //     @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    // ) throws IORuntimeException {
    //     if (len == 0) {
    //         return 0;
    //     }
    //     try {
    //         int count = 0;
    //         while (count < len) {
    //             int readSize = src.read(dst, off + count, len - count);
    //             if (readSize < 0) {
    //                 return count == 0 ? -1 : count;
    //             }
    //             count += readSize;
    //         }
    //         return count;
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // static int readTo0(
    //     @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    // ) throws IORuntimeException {
    //     if (len == 0 || dst.remaining() == 0) {
    //         return 0;
    //     }
    //     try {
    //         if (dst.hasArray()) {
    //             byte[] buf = dst.array();
    //             int off = BufferKit.arrayStartIndex(dst);
    //             int actualLen = len < 0 ? dst.remaining() : Math.min(dst.remaining(), len);
    //             int ret = readTo0(src, buf, off, actualLen);
    //             if (ret <= 0) {
    //                 return ret;
    //             }
    //             dst.position(dst.position() + ret);
    //             return ret;
    //         } else {
    //             byte[] buf = new byte[len < 0 ? dst.remaining() : Math.min(dst.remaining(), len)];
    //             int ret = readTo0(src, buf, 0, buf.length);
    //             if (ret <= 0) {
    //                 return ret;
    //             }
    //             dst.put(buf, 0, ret);
    //             return ret;
    //         }
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // static long readTo0(
    //     @Nonnull ReadableByteChannel src,
    //     @Nonnull WritableByteChannel dst,
    //     long len,
    //     int bufSize
    // ) throws IORuntimeException {
    //     if (len == 0) {
    //         return 0;
    //     }
    //     try {
    //         ByteBuffer buf = ByteBuffer.allocate(IOKit.bufferSize(len, bufSize));
    //         long count = 0;
    //         while (true) {
    //             int limit = len < 0 ? buf.remaining() : (int) Math.min(buf.remaining(), len - count);
    //             buf.limit(limit);
    //             int readSize = src.read(buf);
    //             if (readSize < 0) {
    //                 return count == 0 ? -1 : count;
    //             }
    //             buf.flip();
    //             BufferKit.readTo(buf, dst);
    //             buf.clear();
    //             count += readSize;
    //             if (len > 0 && count >= len) {
    //                 return count;
    //             }
    //         }
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // static long readTo0(
    //     @Nonnull ReadableByteChannel src,
    //     @Nonnull OutputStream dst,
    //     long len,
    //     int bufSize
    // ) throws IORuntimeException {
    //     if (len == 0) {
    //         return 0;
    //     }
    //     try {
    //         ByteBuffer buf = ByteBuffer.allocate(IOKit.bufferSize(len, bufSize));
    //         long count = 0;
    //         while (true) {
    //             int limit = len < 0 ? buf.remaining() : (int) Math.min(buf.remaining(), len - count);
    //             buf.limit(limit);
    //             int readSize = src.read(buf);
    //             if (readSize < 0) {
    //                 return count == 0 ? -1 : count;
    //             }
    //             buf.flip();
    //             BufferKit.readTo(buf, dst);
    //             buf.clear();
    //             count += readSize;
    //             if (len > 0 && count >= len) {
    //                 return count;
    //             }
    //         }
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // static int readTo0(
    //     @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    // ) throws IORuntimeException {
    //     if (len == 0 || dst.remaining() == 0) {
    //         return 0;
    //     }
    //     try {
    //         int oldLimit = dst.limit();
    //         int newLimit = len < 0 ? oldLimit : Math.min(oldLimit, dst.position() + len);
    //         dst.limit(newLimit);
    //         int pos = dst.position();
    //         do {
    //             int readSize = src.read(dst);
    //             if (readSize < 0) {
    //                 dst.limit(oldLimit);
    //                 int posNow = dst.position();
    //                 return posNow == pos ? -1 : posNow - pos;
    //             }
    //         } while (dst.remaining() != 0);
    //         dst.limit(oldLimit);
    //         return dst.position() - pos;
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // /**
    //  * Reads all bytes from the given input buffer to the given writeable channel, until .
    //  *
    //  * @param src the given input buffer
    //  * @param dst the given writeable channel
    //  * @return actual read number, or {@code -1} if no bytes read and reaches the end of the input buffer
    //  * @throws IORuntimeException if any I/O error occurs
    //  */
    // public static int readTo(
    //     @Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst
    // ) throws IORuntimeException {
    //     int remaining = src.remaining();
    //     if (remaining == 0) {
    //         return -1;
    //     }
    //     try {
    //         while (src.remaining() > 0) {
    //             dst.write(src);
    //         }
    //         return remaining;
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
    //
    // /**
    //  * Reads bytes of the specified length from the given input buffer to the given writeable channel.
    //  *
    //  * @param src the given input buffer
    //  * @param dst the given writeable channel
    //  * @param len the specified length to read, expected to {@code >= 0}
    //  * @return the actual read number, or {@code -1} if no bytes read and reaches the end of the input buffer
    //  * @throws IORuntimeException if any I/O error occurs
    //  */
    // public static int readTo(
    //     @Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst, int len
    // ) throws IORuntimeException {
    //     if (len == 0) {
    //         return 0;
    //     }
    //     int remaining = src.remaining();
    //     if (remaining == 0) {
    //         return -1;
    //     }
    //     try {
    //         int actualLen = Math.min(remaining, len);
    //         int oldLimit = src.limit();
    //         src.limit(src.position() + actualLen);
    //         while (src.remaining() > 0) {
    //             dst.write(src);
    //         }
    //         src.limit(oldLimit);
    //         return actualLen;
    //     } catch (Exception e) {
    //         throw new IORuntimeException(e);
    //     }
    // }


    // /**
    //  * Reads the data from the source buffer into the specified array (starting at the specified offset and up to the
    //  * specified length), until the read number reaches the specified length or reaches the end of the source buffer,
    //  * returns the actual number of bytes read to.
    //  * <p>
    //  * If the specified length is {@code 0}, returns {@code 0} without reading. If the end of the source buffer is
    //  * reached and no data is read, returns {@code -1}.
    //  * <p>
    //  * The buffer's position increments by the actual read number.
    //  * <p>
    //  * Note this method do not validate the passed arguments, wrong arguments may cause the undefined behavior.
    //  *
    //  * @param src the source buffer
    //  * @param dst the specified array
    //  * @param off the specified offset of the array
    //  * @param len the specified length to read
    //  * @return the actual number of bytes read to, or {@code -1} if the end of the source buffer is reached and no data
    //  * is read
    //  */
    // public static int readTo(@Nonnull ByteBuffer src, byte @Nonnull [] dst, int off, int len) {
    //     if (len == 0) {
    //         return 0;
    //     }
    //     if (!src.hasRemaining()) {
    //         return -1;
    //     }
    //     int actualLen = Math.min(len, src.remaining());
    //     src.get(dst, off, actualLen);
    //     return actualLen;
    // }
}
