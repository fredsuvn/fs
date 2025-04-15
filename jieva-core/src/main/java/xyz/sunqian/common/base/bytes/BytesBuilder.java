package xyz.sunqian.common.base.bytes;

import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;

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

    private byte[] buf;
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
    public BytesBuilder(int initialCapacity) {
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
    public BytesBuilder(int initialCapacity, int maxCapacity) {
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
     * Appends the specified number of bytes from the given array, starting at the specified offset.
     *
     * @param b   the given array
     * @param off the specified offset
     * @param len the specified number
     */
    @Override
    public void write(byte[] b, int off, int len) {
        JieCheck.checkOffsetLength(b, off, len);
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
    public void writeTo(OutputStream out) throws IORuntimeException {
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
    public void writeTo(ByteBuffer out) throws IORuntimeException {
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
    public byte[] toByteArray() {
        return Arrays.copyOf(buf, count);
    }

    /**
     * Returns a new buffer containing a copy of the appended data.
     *
     * @return a new buffer containing a copy of the appended data
     */
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(toByteArray());
    }

    /**
     * Returns a string decoded from the appended data using the <b>platform's default charset</b>. This is a compatible
     * method of {@link ByteArrayOutputStream#toString()}.
     *
     * @return a string decoded from the appended data using the <b>platform's default charset</b>
     * @see ByteArrayOutputStream#toString()
     */
    @Override
    public String toString() {
        return new String(buf, 0, count);
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
    public String toString(String charsetName) throws UnsupportedEncodingException {
        return new String(buf, 0, count, charsetName);
    }

    /**
     * Returns a string decoded from the appended data using the specified charset.
     *
     * @param charset the specified charset
     * @return a string decoded from the appended data using the specified charset
     */
    public String toString(Charset charset) {
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
    public BytesBuilder append(byte b) {
        write(b);
        return this;
    }

    /**
     * Appends all bytes from the given array.
     *
     * @param bytes the given array
     * @return this builder
     */
    public BytesBuilder append(byte[] bytes) {
        write(bytes, 0, bytes.length);
        return this;
    }

    /**
     * Appends the specified number of bytes from the given array, starting at the specified offset.
     *
     * @param bytes  the given array
     * @param offset the specified offset
     * @param length the specified number
     * @return this builder
     */
    public BytesBuilder append(byte[] bytes, int offset, int length) {
        write(bytes, offset, length);
        return this;
    }

    /**
     * Appends all bytes from the given buffer.
     *
     * @param bytes the given buffer
     * @return this builder
     */
    public BytesBuilder append(ByteBuffer bytes) {
        if (!bytes.hasRemaining()) {
            return this;
        }
        if (bytes.hasArray()) {
            write(bytes.array(), JieBuffer.arrayStartIndex(bytes), bytes.remaining());
            bytes.position(bytes.position() + bytes.remaining());
        } else {
            byte[] remaining = JieBytes.getBytes(bytes);
            write(remaining, 0, remaining.length);
        }
        return this;
    }

    /**
     * Appends all bytes from the given stream.
     *
     * @param in the given stream
     * @return this builder
     * @throws IORuntimeException if an I/O error occurs
     */
    public BytesBuilder append(InputStream in) throws IORuntimeException {
        return append(in, JieIO.BUFFER_SIZE);
    }

    /**
     * Appends all bytes from the given stream with the specified buffer size for each reading.
     *
     * @param in         the given stream
     * @param bufferSize the specified buffer size for each reading
     * @return this builder
     * @throws IORuntimeException if an I/O error occurs
     */
    public BytesBuilder append(InputStream in, int bufferSize) throws IORuntimeException {
        byte[] buffer = new byte[bufferSize];
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
    public BytesBuilder append(BytesBuilder builder) {
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

    private int newCapacity(final int newCapacity, int minCapacity) {
        if (newCapacity <= 0 || newCapacity > maxSize) {
            return maxSize;
        }
        return Math.max(newCapacity, minCapacity);
    }
}
