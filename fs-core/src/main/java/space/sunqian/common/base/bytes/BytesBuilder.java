package space.sunqian.common.base.bytes;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.Checker;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.io.BufferKit;
import space.sunqian.common.io.IOKit;
import space.sunqian.common.io.IORuntimeException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * {@code BytesBuilder} is used to build byte arrays and their derived objects by appending byte data. It is similar to
 * {@link ByteArrayOutputStream}, provides compatible methods, but is not thread-safe. This class also extends the
 * {@link OutputStream}, but the {@code close()} method has no effect.
 *
 * @author sunqian
 */
public class BytesBuilder extends OutputStream {

    // Max array size.
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private final int maxSize;

    private byte @Nonnull [] buf;
    private int count;

    /**
     * Constructs with 32-bytes initial capacity.
     */
    public BytesBuilder() {
        this(32);
    }

    /**
     * Constructs with the specified initial capacity in bytes.
     *
     * @param initialCapacity the specified initial capacity in bytes
     * @throws IllegalArgumentException if size is negative
     */
    public BytesBuilder(int initialCapacity) throws IllegalArgumentException {
        this(initialCapacity, MAX_ARRAY_SIZE);
    }

    /**
     * Constructs with the specified initial capacity and the max capacity in bytes.
     *
     * @param initialCapacity the specified initial capacity in bytes
     * @param maxCapacity     the max capacity in bytes
     * @throws IllegalArgumentException if the {@code initialCapacity < 0} or {@code maxCapacity < 0} or
     *                                  {@code initialCapacity > maxCapacity}
     */
    public BytesBuilder(int initialCapacity, int maxCapacity) throws IllegalArgumentException {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Negative initial capacity: " + initialCapacity + ".");
        }
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("Negative max capacity: " + maxCapacity + ".");
        }
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException("Initial capacity must <= max capacity!");
        }
        buf = new byte[initialCapacity];
        this.maxSize = maxCapacity;
    }

    /**
     * Appends the specified byte to this builder.
     *
     * @param b the specified byte
     */
    @Override
    public void write(int b) {
        ensureCapacity(count + 1);
        buf[count] = (byte) b;
        count += 1;
    }

    /**
     * Appends all bytes from the given array.
     *
     * @param b the given array
     */
    @Override
    public void write(byte @Nonnull [] b) {
        ensureCapacity(count + b.length);
        System.arraycopy(b, 0, buf, count, b.length);
        count += b.length;
    }

    /**
     * Appends the specified number of bytes from the given array, starting at the specified offset.
     *
     * @param b   the given array
     * @param off the specified offset
     * @param len the specified number
     * @throws IndexOutOfBoundsException if the offset or number is out of bounds
     */
    @Override
    public void write(byte @Nonnull [] b, int off, int len) throws IndexOutOfBoundsException {
        Checker.checkOffLen(off, len, b.length);
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    /**
     * Writes the appended data of this builder to the specified output stream.
     *
     * @param out the specified output stream
     * @throws IORuntimeException if an I/O error occurs
     */
    public void writeTo(@Nonnull OutputStream out) throws IORuntimeException {
        try {
            out.write(buf, 0, count);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Writes the appended buffered data of this builder to the specified buffer.
     *
     * @param out the specified buffer
     * @throws IORuntimeException if an I/O error occurs
     */
    public void writeTo(@Nonnull ByteBuffer out) throws IORuntimeException {
        try {
            out.put(buf, 0, count);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Resets this builder, the appended data will be discarded.
     * <p>
     * This method doesn't guarantee releasing the allocated space for the appended data. To trim and release the unused
     * space, use {@link #trim()}.
     */
    public void reset() {
        count = 0;
    }

    /**
     * Trims and releases the allocated but unused space.
     */
    public void trim() {
        if (count < buf.length) {
            buf = Arrays.copyOf(buf, count);
        }
    }

    /**
     * Returns the size of appended data.
     *
     * @return the size of appended data
     */
    public int size() {
        return count;
    }

    /**
     * Returns a new array containing a copy of the appended data.
     *
     * @return a new array containing a copy of the appended data
     */
    public byte @Nonnull [] toByteArray() {
        return Arrays.copyOf(buf, count);
    }

    /**
     * Returns a new buffer containing a copy of the appended data.
     *
     * @return a new buffer containing a copy of the appended data
     */
    public @Nonnull ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(toByteArray());
    }

    /**
     * Returns a string decoded from the appended data using {@link CharsKit#defaultCharset()}. Note that the behavior
     * of this method is <b>different</b> from {@link ByteArrayOutputStream#toString()}
     *
     * @return a string decoded from the appended data using {@link CharsKit#defaultCharset()}
     * @see ByteArrayOutputStream#toString()
     */
    @Override
    public @Nonnull String toString() {
        return toString(CharsKit.defaultCharset());
    }

    /**
     * Returns a string decoded from the appended data using the specified charset. This is a compatible method of
     * {@link ByteArrayOutputStream#toString(String)}.
     *
     * @param charsetName name of the specified charset
     * @return a string decoded from the appended data using the specified charset
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @see ByteArrayOutputStream#toString(String)
     */
    public @Nonnull String toString(@Nonnull String charsetName) throws UnsupportedEncodingException {
        return new String(buf, 0, count, charsetName);
    }

    /**
     * Returns a string decoded from the appended data using the specified charset.
     *
     * @param charset the specified charset
     * @return a string decoded from the appended data using the specified charset
     */
    public @Nonnull String toString(@Nonnull Charset charset) {
        return new String(buf, 0, count, charset);
    }

    /**
     * No effect for this builder.
     */
    @Override
    public void close() {
    }

    /**
     * Appends the specified byte to this builder.
     *
     * @param b the specified byte
     * @return this builder
     */
    public BytesBuilder append(int b) {
        write(b);
        return this;
    }

    /**
     * Appends the specified byte to this builder.
     *
     * @param b the specified byte
     * @return this builder
     */
    public @Nonnull BytesBuilder append(byte b) {
        write(b);
        return this;
    }

    /**
     * Appends all bytes from the given array.
     *
     * @param bytes the given array
     * @return this builder
     */
    public @Nonnull BytesBuilder append(byte @Nonnull [] bytes) {
        write(bytes);
        return this;
    }

    /**
     * Appends the specified number of bytes from the given array, starting at the specified offset.
     *
     * @param bytes  the given array
     * @param offset the specified offset
     * @param length the specified number
     * @return this builder
     * @throws IndexOutOfBoundsException if the offset or number is out of bounds
     */
    public @Nonnull BytesBuilder append(byte @Nonnull [] bytes, int offset, int length) throws IndexOutOfBoundsException {
        write(bytes, offset, length);
        return this;
    }

    /**
     * Reads and appends all bytes from the given buffer.
     *
     * @param bytes the given buffer
     * @return this builder
     */
    public @Nonnull BytesBuilder append(@Nonnull ByteBuffer bytes) {
        int remaining = bytes.remaining();
        if (remaining == 0) {
            return this;
        }
        if (bytes.hasArray()) {
            write(bytes.array(), BufferKit.arrayStartIndex(bytes), bytes.remaining());
            bytes.position(bytes.position() + bytes.remaining());
        } else {
            byte[] data = new byte[remaining];
            bytes.get(data);
            write(data);
        }
        return this;
    }

    /**
     * Reads and appends all bytes from the given stream.
     *
     * @param in the given stream
     * @return this builder
     * @throws IORuntimeException if an I/O error occurs
     */
    public @Nonnull BytesBuilder append(@Nonnull InputStream in) throws IORuntimeException {
        return append(in, IOKit.bufferSize());
    }

    /**
     * Reads and appends all bytes from the given stream with the specified buffer size for each reading.
     *
     * @param in      the given stream
     * @param bufSize the specified buffer size for each reading
     * @return this builder
     * @throws IllegalArgumentException if the buffer size {@code <=0}
     * @throws IORuntimeException       if an I/O error occurs
     */
    public @Nonnull BytesBuilder append(
        @Nonnull InputStream in, int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        if (bufSize <= 0) {
            throw new IllegalArgumentException("The buffer size must > 0.");
        }
        byte[] buffer = new byte[bufSize];
        while (true) {
            try {
                int readSize = in.read(buffer);
                if (readSize < 0) {
                    return this;
                }
                write(buffer, 0, readSize);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }
    }

    /**
     * Appends all bytes from the given builder.
     *
     * @param builder the given builder
     * @return this builder
     */
    public @Nonnull BytesBuilder append(@Nonnull BytesBuilder builder) {
        write(builder.buf, 0, builder.count);
        return this;
    }

    private void ensureCapacity(int minCapacity) {
        if (buf.length < minCapacity) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        if (minCapacity < 0 || minCapacity > maxSize) {
            throw new IllegalStateException("Buffer out of size: " + minCapacity + ".");
        }
        int oldCapacity = buf.length;
        int newCapacity;
        if (oldCapacity == 0) {
            newCapacity = minCapacity;
        } else {
            newCapacity = oldCapacity * 2;
        }
        newCapacity = newCapacity(newCapacity, minCapacity);
        buf = Arrays.copyOf(buf, newCapacity);
    }

    private int newCapacity(int newCapacity, int minCapacity) {
        if (newCapacity <= 0 || newCapacity > maxSize) {
            return maxSize;
        }
        return Math.max(newCapacity, minCapacity);
    }
}
