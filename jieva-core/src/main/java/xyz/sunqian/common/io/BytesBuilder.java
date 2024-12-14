package xyz.sunqian.common.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * This class is used to build bytes, provides an API compatible with {@link ByteArrayOutputStream}, but with no
 * guarantee of synchronization. It is {@code byte} version of {@link StringBuilder} and an implementation of
 * {@link OutputStream}.
 * <p>
 * Like {@link ByteArrayOutputStream}, this class also has a buffer space to store the bytes, and close method has no
 * effect. The methods in this class can be called after the stream has been closed without exception.
 *
 * @author sunqian
 */
public class BytesBuilder extends OutputStream {

    /**
     * Max buffer size.
     */
    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private final int maxSize;

    private byte[] buf;
    private int count;

    /**
     * Constructs with 32 bytes of buffer capacity, it is equivalent to:
     * <pre>
     *     BytesBuilder(32)
     * </pre>
     *
     * @see #BytesBuilder(int)
     * @see ByteArrayOutputStream#ByteArrayOutputStream()
     */
    public BytesBuilder() {
        this(32);
    }

    /**
     * Constructs with specified initial size of buffer capacity in bytes.
     *
     * @param initialSize the initial size
     * @throws IllegalArgumentException if size is negative
     * @see ByteArrayOutputStream#ByteArrayOutputStream(int)
     */
    public BytesBuilder(int initialSize) {
        this(initialSize, MAX_ARRAY_SIZE);
    }

    /**
     * Constructs with specified initial size and max size of buffer capacity in bytes.
     *
     * @param initialSize the initial size
     * @param maxSize     max size
     * @throws IllegalArgumentException if initial size is negative or max size &lt;= 0 or initial size &gt; max size
     */
    public BytesBuilder(int initialSize, int maxSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative initialSize: " + initialSize + ".");
        }
        if (maxSize < 0) {
            throw new IllegalArgumentException("Negative maxSize: " + maxSize + ".");
        }
        if (initialSize > maxSize) {
            throw new IllegalArgumentException("The initialSize must <= maxSize.");
        }
        buf = new byte[initialSize];
        this.maxSize = maxSize;
    }

    /**
     * Writes the specified byte to this builder.
     *
     * @param b the byte to be written
     * @see ByteArrayOutputStream#write(int)
     */
    public void write(int b) {
        ensureCapacity(count + 1);
        buf[count] = (byte) b;
        count += 1;
    }

    /**
     * Writes specified length of bytes from the specified byte array starting at specified offset to this builder.
     *
     * @param b   specified byte array
     * @param off specified offset
     * @param len specified length
     * @see ByteArrayOutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) {
        IOMisc.checkReadBounds(b, off, len);
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    /**
     * Writes contents of this builder to specified output stream.
     *
     * @param out specified output stream
     * @throws IORuntimeException if an I/O error occurs
     * @see ByteArrayOutputStream#writeTo(OutputStream)
     */
    public void writeTo(OutputStream out) throws IORuntimeException {
        try {
            out.write(buf, 0, count);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Writes contents of this builder to specified byte buffer.
     *
     * @param out specified byte buffer
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
     * Resets the <code>byte count</code> of buffer of this builder to zero, so that all currently accumulated bytes in
     * this builder is discarded. This builder can be used again, reusing the already allocated buffer space.
     * <p>
     * To trim and release discarded allocated buffer space, use {@link #trimBuffer()}.
     *
     * @see ByteArrayOutputStream#reset()
     */
    public void reset() {
        count = 0;
    }

    /**
     * Trims and releases the allocated but discarded buffer space. This method is similar to
     * {@link StringBuilder#trimToSize()}.
     *
     * @see StringBuilder#trimToSize()
     */
    public void trimBuffer() {
        if (count < buf.length) {
            buf = Arrays.copyOf(buf, count);
        }
    }

    /**
     * Returns the current size of the buffer (value of the <code>byte count</code>).
     *
     * @return the value of the <code>byte count</code>, which is the number of valid bytes in this builder.
     * @see ByteArrayOutputStream#size()
     */
    public int size() {
        return count;
    }

    /**
     * Creates and returns a newly allocated byte array. Its size is the current size of this builder and the valid
     * contents of the buffer have been copied into it.
     *
     * @return copy of the current contents of this builder, as a byte array.
     * @see ByteArrayOutputStream#toByteArray()
     */
    public byte[] toByteArray() {
        return Arrays.copyOf(buf, count);
    }

    /**
     * Creates and returns a newly allocated byte buffer. Its size is the current size of this builder and the valid
     * contents of the buffer have been copied into it.
     *
     * @return copy of the current contents of this builder, as a byte buffer.
     */
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(toByteArray());
    }

    /**
     * Converts the buffer's contents into a string using the platform's default charset.
     *
     * @return String decoded from the buffer's contents using the platform's default charset
     * @see ByteArrayOutputStream#toString()
     */
    public String toString() {
        return new String(buf, 0, count);
    }

    /**
     * Converts the buffer's contents into a string using specified charset.
     *
     * @param charsetName name of specified charset
     * @return String decoded from the buffer's contents using specified charset
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @see ByteArrayOutputStream#toString(String)
     */
    public String toString(String charsetName) throws UnsupportedEncodingException {
        return new String(buf, 0, count, charsetName);
    }

    /**
     * Converts current contents of this builder into a string with specified charset.
     *
     * @param charset specified charset
     * @return String decoded from the buffer's contents using specified charset
     */
    public String toString(Charset charset) {
        return new String(buf, 0, count, charset);
    }

    /**
     * No effect for this builder.
     *
     * @see ByteArrayOutputStream#close()
     */
    public void close() {
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

    /**
     * Appends a byte into this builder.
     *
     * @param b a byte
     * @return this builder
     */
    public BytesBuilder append(byte b) {
        write(b);
        return this;
    }

    /**
     * Appends given bytes into this builder.
     *
     * @param bytes given bytes
     * @return this builder
     */
    public BytesBuilder append(byte[] bytes) {
        write(bytes, 0, bytes.length);
        return this;
    }

    /**
     * Appends given bytes from specified offset up to specified length into this builder.
     *
     * @param bytes  given bytes
     * @param offset specified offset
     * @param length specified length
     * @return this builder
     */
    public BytesBuilder append(byte[] bytes, int offset, int length) {
        write(bytes, offset, length);
        return this;
    }

    /**
     * Reads and appends given byte buffer into this builder.
     *
     * @param bytes given byte buffer
     * @return this builder
     */
    public BytesBuilder append(ByteBuffer bytes) {
        if (!bytes.hasRemaining()) {
            return this;
        }
        if (bytes.hasArray()) {
            write(bytes.array(), JieBuffer.getArrayStartIndex(bytes), bytes.remaining());
            bytes.position(bytes.position() + bytes.remaining());
        } else {
            byte[] remaining = JieBuffer.read(bytes);
            write(remaining, 0, remaining.length);
        }
        return this;
    }

    /**
     * Reads and appends given input stream into this builder.
     *
     * @param in given input stream
     * @return this builder
     * @throws IORuntimeException if any IO problem occurs
     */
    public BytesBuilder append(InputStream in) throws IORuntimeException {
        return append(in, JieIO.BUFFER_SIZE);
    }

    /**
     * Reads and appends given input stream into this builder with specified buffer size for per reading.
     *
     * @param in         given input stream
     * @param bufferSize specified buffer size
     * @return this builder
     * @throws IORuntimeException if any IO problem occurs
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
     * Appends contents of given bytes builder into this builder.
     *
     * @param builder given bytes builder
     * @return this builder
     */
    public BytesBuilder append(BytesBuilder builder) {
        write(builder.buf, 0, builder.count);
        return this;
    }
}
