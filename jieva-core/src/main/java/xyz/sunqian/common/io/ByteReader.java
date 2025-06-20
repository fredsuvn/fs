package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.JieCheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * This interface is used to read bytes for {@link InputStream} and {@link ReadableByteChannel}.
 *
 * @author sunqian
 */
public interface ByteReader {

    /**
     * Returns the buffer size of current instance.
     *
     * @return the buffer size of current instance
     */
    int bufferSize();

    /**
     * Reads all data from the source stream into a new array, continuing until reaches the end of the stream, and
     * returns the array.
     * <p>
     * Note the data in the stream cannot exceed the maximum limit of the array.
     *
     * @param src the source stream
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    byte @Nonnull [] read(@Nonnull InputStream src) throws IORuntimeException;

    /**
     * Reads the data of the specified length from the source stream into a new array, and returns the array. If the
     * specified length {@code = 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the stream.
     * <p>
     * Note the length cannot exceed the maximum limit of the array.
     *
     * @param src the source stream
     * @param len the specified read length, must {@code >= 0}
     * @return the array containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    byte @Nonnull [] read(@Nonnull InputStream src, int len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Reads all data from the source channel into a new buffer, continuing until reaches the end of the channel, and
     * returns the buffer. The buffer's position is {@code 0}, limit equals to capacity, and it has a backing array of
     * which offset is {@code 0}.
     * <p>
     * Note the data in the channel cannot exceed the maximum limit of the buffer.
     *
     * @param src the source channel
     * @return the buffer containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    @Nonnull
    ByteBuffer read(@Nonnull ReadableByteChannel src) throws IORuntimeException;

    /**
     * Reads the data of the specified length from the source channel into a new buffer, and returns the buffer. If the
     * specified length {@code length = 0}, returns an empty buffer without reading. Otherwise, this method keeps
     * reading until the read number reaches the specified length or reaches the end of the channel.
     * <p>
     * The buffer's position is {@code 0}, limit equals to capacity, and it has a backing array of which offset is
     * {@code 0}. And note the length cannot exceed the maximum limit of the buffer.
     *
     * @param src the source channel
     * @param len the specified read length, must {@code >= 0}
     * @return the buffer containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    @Nonnull
    ByteBuffer read(@Nonnull ReadableByteChannel src, int len) throws IllegalArgumentException, IORuntimeException;

    /**
     * Reads all from the source stream into the specified output stream, until the read number reaches the specified
     * length or reaches the end of the source stream, returns the actual number of bytes read to.
     * <p>
     * If the end of the source stream has already been reached, returns {@code -1}.
     * <p>
     * This method never invoke the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param src the source stream
     * @param dst the specified output stream
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    long read(@Nonnull InputStream src, @Nonnull OutputStream dst) throws IORuntimeException;

    /**
     * Reads the data of the specified length from the source stream into the specified output stream, until the read
     * number reaches the specified length or reaches the end of the source stream, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     * <p>
     * This method never invoke the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param src the source stream
     * @param dst the specified output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return readTo(src, dst, len, bufferSize());
    }

    /**
     * Reads the data of the specified length from the source stream into the specified output stream, until the read
     * number reaches the specified length or reaches the end of the source stream, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     * <p>
     * This method never invoke the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param src     the source stream
     * @param dst     the specified output stream
     * @param len     the specified length, must {@code >= 0}
     * @param bufSize specifies the buffer size for reading, must {@code > 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified length or buffer size is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len, int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return readTo0(src, dst, len, bufSize);
    }

    private static long readTo0(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len, int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(bufSize > 0, "bufSize must > 0.");
        if (len == 0) {
            return 0;
        }
        try {
            byte[] buf = new byte[bufferSize(len, bufSize)];
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
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads the data from the source stream into the specified array, until the read number reaches the array's length
     * or reaches the end of the source stream, returns the actual number of bytes read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has already
     * been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified array
     * @return the actual number of bytes read
     * @throws IndexOutOfBoundsException if the array arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int readTo(
        @Nonnull InputStream src, byte @Nonnull [] dst
    ) throws IndexOutOfBoundsException, IORuntimeException {
        if (dst.length == 0) {
            return 0;
        }
        return readTo0(src, dst, 0, dst.length);
    }

    /**
     * Reads the data from the source stream into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source stream,
     * returns the actual number of bytes read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has
     * already been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read
     * @throws IndexOutOfBoundsException if the array arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int readTo(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        JieCheck.checkOffsetLength(dst.length, off, len);
        if (len == 0) {
            return 0;
        }
        return readTo0(src, dst, off, len);
    }

    private static int readTo0(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
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
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads the data from the source stream into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source stream, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source stream
     * @param dst the specified buffer
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull InputStream src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return readTo(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source stream into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source stream, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length {@code < 0}, this method performs as {@link #readTo(InputStream, ByteBuffer)}; if the
     * specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source
     * stream has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source stream
     * @param dst the specified buffer
     * @param len the specified length
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IORuntimeException {
        if (len == 0 || dst.remaining() == 0) {
            return 0;
        }
        try {
            if (dst.hasArray()) {
                byte[] buf = dst.array();
                int off = dst.arrayOffset() + dst.position();
                int actualLen = len < 0 ? dst.remaining() : Math.min(dst.remaining(), len);
                int ret = readTo0(src, buf, off, actualLen);
                if (ret <= 0) {
                    return ret;
                }
                dst.position(dst.position() + ret);
                return ret;
            } else {
                byte[] buf = new byte[len < 0 ? dst.remaining() : Math.min(dst.remaining(), len)];
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

    /**
     * Reads all from the source channel into the specified output channel, until the read number reaches the specified
     * length or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the end of the source channel has already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified output channel
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readAllTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst
    ) throws IORuntimeException {
        return readTo0(src, dst, -1, bufferSize());
    }

    /**
     * Reads all from the source channel into the specified output channel, until the read number reaches the specified
     * length or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the end of the source channel has already been reached, returns {@code -1}.
     *
     * @param src     the source channel
     * @param dst     the specified output channel
     * @param bufSize specifies the buffer size for reading, must {@code > 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified buffer size is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readAllTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        return readTo0(src, dst, -1, bufSize);
    }

    /**
     * Reads the data of the specified length from the source channel into the specified output channel, until the read
     * number reaches the specified length or reaches the end of the source channel, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code < 0}, this method reads all data; if the specified length {@code = 0}, returns
     * {@code 0} without reading; if the end of the source channel has already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified output channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return readTo(src, dst, len, bufferSize());
    }

    /**
     * Reads the data of the specified length from the source channel into the specified output channel, until the read
     * number reaches the specified length or reaches the end of the source channel, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code < 0}, this method reads all data; if the specified length {@code = 0}, returns
     * {@code 0} without reading; if the end of the source channel has already been reached, returns {@code -1}.
     *
     * @param src     the source channel
     * @param dst     the specified output channel
     * @param len     the specified length, must {@code >= 0}
     * @param bufSize specifies the buffer size for reading, must {@code > 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified length or buffer size is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len, int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return readTo0(src, dst, len, bufSize);
    }

    private static long readTo0(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len, int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(bufSize > 0, "bufSize must > 0.");
        if (len == 0) {
            return 0;
        }
        try {
            ByteBuffer buf = ByteBuffer.allocate(bufferSize(len, bufSize));
            long count = 0;
            while (true) {
                int limit = len < 0 ? buf.remaining() : (int) Math.min(buf.remaining(), len - count);
                buf.limit(limit);
                int readSize = src.read(buf);
                if (readSize < 0) {
                    return count == 0 ? -1 : count;
                }
                buf.flip();
                write(dst, buf);
                buf.clear();
                count += readSize;
                if (len > 0 && count >= len) {
                    return count;
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads the data from the source channel into the specified array, until the read number reaches the array's length
     * or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source channel has
     * already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified array
     * @return the actual number of bytes read
     * @throws IndexOutOfBoundsException if the array arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ReadableByteChannel src, byte @Nonnull [] dst
    ) throws IndexOutOfBoundsException, IORuntimeException {
        if (dst.length == 0) {
            return 0;
        }
        return readTo0(src, ByteBuffer.wrap(dst), -1);
    }

    /**
     * Reads the data from the source channel into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source channel,
     * returns the actual number of bytes read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source channel has
     * already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read
     * @throws IndexOutOfBoundsException if the array arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ReadableByteChannel src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        JieCheck.checkOffsetLength(dst.length, off, len);
        if (len == 0) {
            return 0;
        }
        return readTo0(src, ByteBuffer.wrap(dst, off, len), len);
    }

    /**
     * Reads the data from the source channel into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source channel has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the specified buffer
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return readTo(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source channel into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source channel, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length {@code < 0}, this method performs as {@link #readTo(ReadableByteChannel, ByteBuffer)}; if
     * the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source channel has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the specified buffer
     * @param len the specified length
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IORuntimeException {
        if (len == 0 || dst.remaining() == 0) {
            return 0;
        }
        return readTo0(src, dst, len);
    }

    private static int readTo0(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
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
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    // static class A {
    //     /**
    //      * Returns the recommended IO buffer size, typically is 1024 * 8 = 8192.
    //      *
    //      * @return the recommended IO buffer size
    //      */
    //     public static int bufferSize() {
    //         return BUFFER_SIZE;
    //     }
    //
    //     /**
    //      * Determines the recommended buffer size based on the given length and the given buffer size, and its logic is as
    //      * follows:
    //      * <pre>{@code
    //      * return len < 0 ? bufSize : (int) Math.min(len, bufSize);
    //      * }</pre>
    //      *
    //      * @param len     the given length
    //      * @param bufSize the given buffer size
    //      * @return the recommended buffer size based on the given length and the given buffer size
    //      */
    //     public static int bufferSize(long len, int bufSize) {
    //         return len < 0 ? bufSize : (int) Math.min(len, bufSize);
    //     }
    //
    //     /**
    //      * Reads all data from the source stream into a new array, continuing until reaches the end of the stream, and
    //      * returns the array.
    //      * <p>
    //      * Note the data in the stream cannot exceed the maximum limit of the array.
    //      *
    //      * @param src the source stream
    //      * @return the array containing the data
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     @SuppressWarnings("resource")
    //     public static byte @Nonnull [] read(@Nonnull InputStream src) throws IORuntimeException {
    //         try {
    //             int available = src.available();
    //             byte[] buf = new byte[available > 0 ? available : bufferSize()];
    //             BytesBuilder builder = null;
    //             int off = 0;
    //             while (true) {
    //                 int readSize = src.read(buf, off, buf.length - off);
    //                 if (readSize < 0) {
    //                     if (builder != null) {
    //                         builder.append(buf, 0, off);
    //                         return builder.toByteArray();
    //                     }
    //                     return Arrays.copyOfRange(buf, 0, off);
    //                 }
    //                 off += readSize;
    //                 if (off == buf.length) {
    //                     if (builder == null) {
    //                         int r = src.read();
    //                         if (r == -1) {
    //                             return buf;
    //                         }
    //                         builder = new BytesBuilder(buf.length + 1);
    //                         builder.append(buf);
    //                         builder.append(r);
    //                     } else {
    //                         builder.append(buf);
    //                     }
    //                     off = 0;
    //                 }
    //             }
    //         } catch (IOException e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     /**
    //      * Reads the data of the specified length from the source stream into a new array, and returns the array. If the
    //      * specified length {@code < 0}, this method performs as {@link #read(InputStream)}. If the specified length
    //      * {@code = 0}, returns an empty array without reading. Otherwise, this method keeps reading until the read number
    //      * reaches the specified length or reaches the end of the stream.
    //      * <p>
    //      * Note the length cannot exceed the maximum limit of the array.
    //      *
    //      * @param src the source stream
    //      * @param len the specified read length
    //      * @return the array containing the data
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     public static byte @Nonnull [] read(@Nonnull InputStream src, int len) throws IORuntimeException {
    //         if (len < 0) {
    //             return read(src);
    //         }
    //         if (len == 0) {
    //             return new byte[0];
    //         }
    //         try {
    //             byte[] buf = new byte[len];
    //             int off = 0;
    //             while (off < len) {
    //                 int readSize = src.read(buf, off, buf.length - off);
    //                 if (readSize < 0) {
    //                     return Arrays.copyOfRange(buf, 0, off);
    //                 }
    //                 off += readSize;
    //             }
    //             return buf;
    //         } catch (IOException e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     /**
    //      * Reads all data from the source channel into a new buffer, continuing until reaches the end of the channel, and
    //      * returns the buffer. The buffer's position is {@code 0}, limit equals to capacity, and it has a backing array of
    //      * which offset is {@code 0}.
    //      * <p>
    //      * Note the data in the channel cannot exceed the maximum limit of the buffer.
    //      *
    //      * @param src the source channel
    //      * @return the buffer containing the data
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     @SuppressWarnings("resource")
    //     public static @Nonnull ByteBuffer read(@Nonnull ReadableByteChannel src) throws IORuntimeException {
    //         try {
    //             BytesBuilder builder = null;
    //             ByteBuffer buf = ByteBuffer.allocate(bufferSize());
    //             while (true) {
    //                 int readSize = src.read(buf);
    //                 if (readSize < 0) {
    //                     break;
    //                 }
    //                 if (buf.remaining() == 0) {
    //                     if (builder == null) {
    //                         int lastIndex = buf.capacity() - 1;
    //                         byte b = buf.get(lastIndex);
    //                         buf.position(lastIndex);
    //                         int r = src.read(buf);
    //                         buf.position(0);
    //                         if (r < 0) {
    //                             return buf;
    //                         }
    //                         builder = new BytesBuilder(buf.capacity() + 1);
    //                         buf.limit(lastIndex);
    //                         builder.append(buf);
    //                         builder.append(b);
    //                         buf.limit(buf.capacity());
    //                         builder.append(buf);
    //                     } else {
    //                         buf.flip();
    //                         builder.append(buf);
    //                     }
    //                     buf.flip();
    //                 }
    //             }
    //             if (builder == null) {
    //                 return ByteBuffer.wrap(Arrays.copyOfRange(buf.array(), 0, buf.position()));
    //             } else {
    //                 if (buf.position() > 0) {
    //                     buf.flip();
    //                     builder.append(buf);
    //                 }
    //                 return builder.toByteBuffer();
    //             }
    //         } catch (IOException e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     /**
    //      * Reads the data of the specified length from the source channel into a new buffer, and returns the buffer. If the
    //      * specified length {@code < 0}, this method performs as {@link #read(ReadableByteChannel)}. If the specified length
    //      * {@code length = 0}, returns an empty buffer without reading. Otherwise, this method keeps reading until the read
    //      * number reaches the specified length or reaches the end of the channel.
    //      * <p>
    //      * The buffer's position is {@code 0}, limit equals to capacity, and it has a backing array of which offset is
    //      * {@code 0}. And note the length cannot exceed the maximum limit of the buffer.
    //      *
    //      * @param src the source channel
    //      * @param len the specified read length
    //      * @return the buffer containing the data
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     public static @Nonnull ByteBuffer read(@Nonnull ReadableByteChannel src, int len) throws IORuntimeException {
    //         if (len < 0) {
    //             return read(src);
    //         }
    //         if (len == 0) {
    //             return ByteBuffer.allocate(0);
    //         }
    //         try {
    //             ByteBuffer dst = ByteBuffer.allocate(len);
    //             while (dst.remaining() > 0) {
    //                 int readSize = src.read(dst);
    //                 if (readSize < 0) {
    //                     return ByteBuffer.wrap(Arrays.copyOfRange(dst.array(), 0, dst.position()));
    //                 }
    //             }
    //             dst.flip();
    //             return dst;
    //         } catch (IOException e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     /**
    //      * Reads all from the source stream into the specified output stream, until the read number reaches the specified
    //      * length or reaches the end of the source stream, returns the actual number of bytes read to.
    //      * <p>
    //      * If the end of the source stream has already been reached, returns {@code -1}.
    //      * <p>
    //      * This method never invoke the {@link OutputStream#flush()} to force the backing buffer.
    //      *
    //      * @param src the source stream
    //      * @param dst the specified output stream
    //      * @return the actual number of bytes read
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     public static long readAllTo(
    //         @Nonnull InputStream src, @Nonnull OutputStream dst
    //     ) throws IORuntimeException {
    //         return readTo0(src, dst, -1, bufferSize());
    //     }
    //
    //     /**
    //      * Reads all from the source stream into the specified output stream, until the read number reaches the specified
    //      * length or reaches the end of the source stream, returns the actual number of bytes read to.
    //      * <p>
    //      * If the end of the source stream has already been reached, returns {@code -1}.
    //      * <p>
    //      * This method never invoke the {@link OutputStream#flush()} to force the backing buffer.
    //      *
    //      * @param src     the source stream
    //      * @param dst     the specified output stream
    //      * @param bufSize specifies the buffer size for reading, must {@code > 0}
    //      * @return the actual number of bytes read
    //      * @throws IllegalArgumentException if the specified buffer size is illegal
    //      * @throws IORuntimeException       if an I/O error occurs
    //      */
    //     public static long readAllTo(
    //         @Nonnull InputStream src, @Nonnull OutputStream dst, int bufSize
    //     ) throws IllegalArgumentException, IORuntimeException {
    //         return readTo0(src, dst, -1, bufSize);
    //     }
    //
    //     /**
    //      * Reads the data of the specified length from the source stream into the specified output stream, until the read
    //      * number reaches the specified length or reaches the end of the source stream, returns the actual number of bytes
    //      * read to.
    //      * <p>
    //      * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
    //      * already been reached, returns {@code -1}.
    //      * <p>
    //      * This method never invoke the {@link OutputStream#flush()} to force the backing buffer.
    //      *
    //      * @param src the source stream
    //      * @param dst the specified output stream
    //      * @param len the specified length, must {@code >= 0}
    //      * @return the actual number of bytes read
    //      * @throws IllegalArgumentException if the specified length is illegal
    //      * @throws IORuntimeException       if an I/O error occurs
    //      */
    //     public static long readTo(
    //         @Nonnull InputStream src, @Nonnull OutputStream dst, long len
    //     ) throws IllegalArgumentException, IORuntimeException {
    //         return readTo(src, dst, len, bufferSize());
    //     }
    //
    //     /**
    //      * Reads the data of the specified length from the source stream into the specified output stream, until the read
    //      * number reaches the specified length or reaches the end of the source stream, returns the actual number of bytes
    //      * read to.
    //      * <p>
    //      * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
    //      * already been reached, returns {@code -1}.
    //      * <p>
    //      * This method never invoke the {@link OutputStream#flush()} to force the backing buffer.
    //      *
    //      * @param src     the source stream
    //      * @param dst     the specified output stream
    //      * @param len     the specified length, must {@code >= 0}
    //      * @param bufSize specifies the buffer size for reading, must {@code > 0}
    //      * @return the actual number of bytes read
    //      * @throws IllegalArgumentException if the specified length or buffer size is illegal
    //      * @throws IORuntimeException       if an I/O error occurs
    //      */
    //     public static long readTo(
    //         @Nonnull InputStream src, @Nonnull OutputStream dst, long len, int bufSize
    //     ) throws IllegalArgumentException, IORuntimeException {
    //         JieCheck.checkArgument(len >= 0, "len must >= 0.");
    //         return readTo0(src, dst, len, bufSize);
    //     }
    //
    //     private static long readTo0(
    //         @Nonnull InputStream src, @Nonnull OutputStream dst, long len, int bufSize
    //     ) throws IllegalArgumentException, IORuntimeException {
    //         JieCheck.checkArgument(bufSize > 0, "bufSize must > 0.");
    //         if (len == 0) {
    //             return 0;
    //         }
    //         try {
    //             byte[] buf = new byte[bufferSize(len, bufSize)];
    //             long count = 0;
    //             while (true) {
    //                 int readSize = len < 0 ?
    //                     src.read(buf)
    //                     :
    //                     src.read(buf, 0, (int) Math.min(buf.length, len - count));
    //                 if (readSize < 0) {
    //                     return count == 0 ? -1 : count;
    //                 }
    //                 dst.write(buf, 0, readSize);
    //                 count += readSize;
    //                 if (len > 0 && count >= len) {
    //                     return count;
    //                 }
    //             }
    //         } catch (IOException e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     /**
    //      * Reads the data from the source stream into the specified array, until the read number reaches the array's length
    //      * or reaches the end of the source stream, returns the actual number of bytes read to.
    //      * <p>
    //      * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has already
    //      * been reached, returns {@code -1}.
    //      *
    //      * @param src the source stream
    //      * @param dst the specified array
    //      * @return the actual number of bytes read
    //      * @throws IndexOutOfBoundsException if the array arguments are out of bounds
    //      * @throws IORuntimeException        if an I/O error occurs
    //      */
    //     public static int readTo(
    //         @Nonnull InputStream src, byte @Nonnull [] dst
    //     ) throws IndexOutOfBoundsException, IORuntimeException {
    //         if (dst.length == 0) {
    //             return 0;
    //         }
    //         return readTo0(src, dst, 0, dst.length);
    //     }
    //
    //     /**
    //      * Reads the data from the source stream into the specified array (starting at the specified offset and up to the
    //      * specified length), until the read number reaches the specified length or reaches the end of the source stream,
    //      * returns the actual number of bytes read to.
    //      * <p>
    //      * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has
    //      * already been reached, returns {@code -1}.
    //      *
    //      * @param src the source stream
    //      * @param dst the specified array
    //      * @param off the specified offset of the array
    //      * @param len the specified length to read
    //      * @return the actual number of bytes read
    //      * @throws IndexOutOfBoundsException if the array arguments are out of bounds
    //      * @throws IORuntimeException        if an I/O error occurs
    //      */
    //     public static int readTo(
    //         @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    //     ) throws IndexOutOfBoundsException, IORuntimeException {
    //         JieCheck.checkOffsetLength(dst.length, off, len);
    //         if (len == 0) {
    //             return 0;
    //         }
    //         return readTo0(src, dst, off, len);
    //     }
    //
    //     private static int readTo0(
    //         @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    //     ) throws IndexOutOfBoundsException, IORuntimeException {
    //         try {
    //             int count = 0;
    //             while (count < len) {
    //                 int readSize = src.read(dst, off + count, len - count);
    //                 if (readSize < 0) {
    //                     return count == 0 ? -1 : count;
    //                 }
    //                 count += readSize;
    //             }
    //             return count;
    //         } catch (IOException e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     /**
    //      * Reads the data from the source stream into the specified buffer, until the read number reaches the buffer's
    //      * remaining or reaches the end of the source stream, returns the actual number of bytes read to.
    //      * <p>
    //      * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
    //      * already been reached, returns {@code -1}.
    //      * <p>
    //      * The buffer's position increments by the actual read number.
    //      *
    //      * @param src the source stream
    //      * @param dst the specified buffer
    //      * @return the actual number of bytes read
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     public static int readTo(@Nonnull InputStream src, @Nonnull ByteBuffer dst) throws IORuntimeException {
    //         return readTo(src, dst, -1);
    //     }
    //
    //     /**
    //      * Reads the data of the specified length from the source stream into the specified buffer, until the read number
    //      * reaches the buffer's remaining or reaches the end of the source stream, returns the actual number of bytes read
    //      * to.
    //      * <p>
    //      * If the specified length {@code < 0}, this method performs as {@link #readTo(InputStream, ByteBuffer)}; if the
    //      * specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source
    //      * stream has already been reached, returns {@code -1}.
    //      * <p>
    //      * The buffer's position increments by the actual read number.
    //      *
    //      * @param src the source stream
    //      * @param dst the specified buffer
    //      * @param len the specified length
    //      * @return the actual number of bytes read
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     public static int readTo(
    //         @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    //     ) throws IORuntimeException {
    //         if (len == 0 || dst.remaining() == 0) {
    //             return 0;
    //         }
    //         try {
    //             if (dst.hasArray()) {
    //                 byte[] buf = dst.array();
    //                 int off = dst.arrayOffset() + dst.position();
    //                 int actualLen = len < 0 ? dst.remaining() : Math.min(dst.remaining(), len);
    //                 int ret = readTo0(src, buf, off, actualLen);
    //                 if (ret <= 0) {
    //                     return ret;
    //                 }
    //                 dst.position(dst.position() + ret);
    //                 return ret;
    //             } else {
    //                 byte[] buf = new byte[len < 0 ? dst.remaining() : Math.min(dst.remaining(), len)];
    //                 int ret = readTo0(src, buf, 0, buf.length);
    //                 if (ret <= 0) {
    //                     return ret;
    //                 }
    //                 dst.put(buf, 0, ret);
    //                 return ret;
    //             }
    //         } catch (Exception e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     /**
    //      * Reads all from the source channel into the specified output channel, until the read number reaches the specified
    //      * length or reaches the end of the source channel, returns the actual number of bytes read to.
    //      * <p>
    //      * If the end of the source channel has already been reached, returns {@code -1}.
    //      *
    //      * @param src the source channel
    //      * @param dst the specified output channel
    //      * @return the actual number of bytes read
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     public static long readAllTo(
    //         @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst
    //     ) throws IORuntimeException {
    //         return readTo0(src, dst, -1, bufferSize());
    //     }
    //
    //     /**
    //      * Reads all from the source channel into the specified output channel, until the read number reaches the specified
    //      * length or reaches the end of the source channel, returns the actual number of bytes read to.
    //      * <p>
    //      * If the end of the source channel has already been reached, returns {@code -1}.
    //      *
    //      * @param src     the source channel
    //      * @param dst     the specified output channel
    //      * @param bufSize specifies the buffer size for reading, must {@code > 0}
    //      * @return the actual number of bytes read
    //      * @throws IllegalArgumentException if the specified buffer size is illegal
    //      * @throws IORuntimeException       if an I/O error occurs
    //      */
    //     public static long readAllTo(
    //         @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, int bufSize
    //     ) throws IllegalArgumentException, IORuntimeException {
    //         return readTo0(src, dst, -1, bufSize);
    //     }
    //
    //     /**
    //      * Reads the data of the specified length from the source channel into the specified output channel, until the read
    //      * number reaches the specified length or reaches the end of the source channel, returns the actual number of bytes
    //      * read to.
    //      * <p>
    //      * If the specified length {@code < 0}, this method reads all data; if the specified length {@code = 0}, returns
    //      * {@code 0} without reading; if the end of the source channel has already been reached, returns {@code -1}.
    //      *
    //      * @param src the source channel
    //      * @param dst the specified output channel
    //      * @param len the specified length, must {@code >= 0}
    //      * @return the actual number of bytes read
    //      * @throws IllegalArgumentException if the specified length is illegal
    //      * @throws IORuntimeException       if an I/O error occurs
    //      */
    //     public static long readTo(
    //         @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len
    //     ) throws IllegalArgumentException, IORuntimeException {
    //         return readTo(src, dst, len, bufferSize());
    //     }
    //
    //     /**
    //      * Reads the data of the specified length from the source channel into the specified output channel, until the read
    //      * number reaches the specified length or reaches the end of the source channel, returns the actual number of bytes
    //      * read to.
    //      * <p>
    //      * If the specified length {@code < 0}, this method reads all data; if the specified length {@code = 0}, returns
    //      * {@code 0} without reading; if the end of the source channel has already been reached, returns {@code -1}.
    //      *
    //      * @param src     the source channel
    //      * @param dst     the specified output channel
    //      * @param len     the specified length, must {@code >= 0}
    //      * @param bufSize specifies the buffer size for reading, must {@code > 0}
    //      * @return the actual number of bytes read
    //      * @throws IllegalArgumentException if the specified length or buffer size is illegal
    //      * @throws IORuntimeException       if an I/O error occurs
    //      */
    //     public static long readTo(
    //         @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len, int bufSize
    //     ) throws IllegalArgumentException, IORuntimeException {
    //         JieCheck.checkArgument(len >= 0, "len must >= 0.");
    //         return readTo0(src, dst, len, bufSize);
    //     }
    //
    //     private static long readTo0(
    //         @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len, int bufSize
    //     ) throws IllegalArgumentException, IORuntimeException {
    //         JieCheck.checkArgument(bufSize > 0, "bufSize must > 0.");
    //         if (len == 0) {
    //             return 0;
    //         }
    //         try {
    //             ByteBuffer buf = ByteBuffer.allocate(bufferSize(len, bufSize));
    //             long count = 0;
    //             while (true) {
    //                 int limit = len < 0 ? buf.remaining() : (int) Math.min(buf.remaining(), len - count);
    //                 buf.limit(limit);
    //                 int readSize = src.read(buf);
    //                 if (readSize < 0) {
    //                     return count == 0 ? -1 : count;
    //                 }
    //                 buf.flip();
    //                 write(dst, buf);
    //                 buf.clear();
    //                 count += readSize;
    //                 if (len > 0 && count >= len) {
    //                     return count;
    //                 }
    //             }
    //         } catch (IOException e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     /**
    //      * Reads the data from the source channel into the specified array, until the read number reaches the array's length
    //      * or reaches the end of the source channel, returns the actual number of bytes read to.
    //      * <p>
    //      * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source channel has
    //      * already been reached, returns {@code -1}.
    //      *
    //      * @param src the source channel
    //      * @param dst the specified array
    //      * @return the actual number of bytes read
    //      * @throws IndexOutOfBoundsException if the array arguments are out of bounds
    //      * @throws IORuntimeException        if an I/O error occurs
    //      */
    //     public static int readTo(
    //         @Nonnull ReadableByteChannel src, byte @Nonnull [] dst
    //     ) throws IndexOutOfBoundsException, IORuntimeException {
    //         if (dst.length == 0) {
    //             return 0;
    //         }
    //         return readTo0(src, ByteBuffer.wrap(dst), -1);
    //     }
    //
    //     /**
    //      * Reads the data from the source channel into the specified array (starting at the specified offset and up to the
    //      * specified length), until the read number reaches the specified length or reaches the end of the source channel,
    //      * returns the actual number of bytes read to.
    //      * <p>
    //      * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source channel has
    //      * already been reached, returns {@code -1}.
    //      *
    //      * @param src the source channel
    //      * @param dst the specified array
    //      * @param off the specified offset of the array
    //      * @param len the specified length to read
    //      * @return the actual number of bytes read
    //      * @throws IndexOutOfBoundsException if the array arguments are out of bounds
    //      * @throws IORuntimeException        if an I/O error occurs
    //      */
    //     public static int readTo(
    //         @Nonnull ReadableByteChannel src, byte @Nonnull [] dst, int off, int len
    //     ) throws IndexOutOfBoundsException, IORuntimeException {
    //         JieCheck.checkOffsetLength(dst.length, off, len);
    //         if (len == 0) {
    //             return 0;
    //         }
    //         return readTo0(src, ByteBuffer.wrap(dst, off, len), len);
    //     }
    //
    //     /**
    //      * Reads the data from the source channel into the specified buffer, until the read number reaches the buffer's
    //      * remaining or reaches the end of the source channel, returns the actual number of bytes read to.
    //      * <p>
    //      * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source channel has
    //      * already been reached, returns {@code -1}.
    //      * <p>
    //      * The buffer's position increments by the actual read number.
    //      *
    //      * @param src the source channel
    //      * @param dst the specified buffer
    //      * @return the actual number of bytes read
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     public static int readTo(@Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst) throws IORuntimeException {
    //         return readTo(src, dst, -1);
    //     }
    //
    //     /**
    //      * Reads the data of the specified length from the source channel into the specified buffer, until the read number
    //      * reaches the buffer's remaining or reaches the end of the source channel, returns the actual number of bytes read
    //      * to.
    //      * <p>
    //      * If the specified length {@code < 0}, this method performs as {@link #readTo(ReadableByteChannel, ByteBuffer)}; if
    //      * the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
    //      * source channel has already been reached, returns {@code -1}.
    //      * <p>
    //      * The buffer's position increments by the actual read number.
    //      *
    //      * @param src the source channel
    //      * @param dst the specified buffer
    //      * @param len the specified length
    //      * @return the actual number of bytes read
    //      * @throws IORuntimeException if an I/O error occurs
    //      */
    //     public static int readTo(
    //         @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    //     ) throws IORuntimeException {
    //         if (len == 0 || dst.remaining() == 0) {
    //             return 0;
    //         }
    //         return readTo0(src, dst, len);
    //     }
    //
    //     private static int readTo0(
    //         @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    //     ) throws IndexOutOfBoundsException, IORuntimeException {
    //         try {
    //             int oldLimit = dst.limit();
    //             int newLimit = len < 0 ? oldLimit : Math.min(oldLimit, dst.position() + len);
    //             dst.limit(newLimit);
    //             int pos = dst.position();
    //             do {
    //                 int readSize = src.read(dst);
    //                 if (readSize < 0) {
    //                     dst.limit(oldLimit);
    //                     int posNow = dst.position();
    //                     return posNow == pos ? -1 : posNow - pos;
    //                 }
    //             } while (dst.remaining() != 0);
    //             dst.limit(oldLimit);
    //             return dst.position() - pos;
    //         } catch (IOException e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    // }
}
