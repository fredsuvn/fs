package xyz.sunqian.common.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * This class is used to build bytes, provides an API compatible with {@link ByteArrayOutputStream}, but with no
 * guarantee of synchronization. It is {@code byte} version of {@link StringBuilder} and an implementation of
 * {@link OutputStream}.
 * <p>
 * Like {@link ByteArrayOutputStream}, this class also has a buffer space to store the bytes, and close method has no
 * effect. The methods in this class can be called after the stream has been closed without generating an IOException.
 *
 * @author sunqian
 */
public class BytesBuilder extends OutputStream {

    /**
     * Max buffer size of bytes builder.
     */
    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private byte[] buf;
    private int count;
    private final int maxSize;

    /**
     * Constructs with 32 initial size of buffer capacity.
     *
     * @see ByteArrayOutputStream#ByteArrayOutputStream()
     */
    public BytesBuilder() {
        this(32);
    }

    /**
     * Constructs with specified initial size of buffer capacity in bytes.
     *
     * @param initialSize the initial size.
     * @throws IllegalArgumentException if size is negative.
     * @see ByteArrayOutputStream#ByteArrayOutputStream(int)
     */
    public BytesBuilder(int initialSize) {
        this(initialSize, MAX_ARRAY_SIZE);
    }

    /**
     * Constructs with specified initial size and max size of buffer capacity in bytes.
     *
     * @param initialSize the initial size.
     * @param maxSize     max size
     * @throws IllegalArgumentException if initial size is negative or max size &lt;= 0 or initial size &gt; max size.
     * @see ByteArrayOutputStream#ByteArrayOutputStream(int)
     */
    public BytesBuilder(int initialSize, int maxSize) {
        if (initialSize < 0 || maxSize <= 0 || initialSize > maxSize) {
            throw new IllegalArgumentException("Negative initialSize: "
                + initialSize + ", maxSize: " + maxSize + ".");
        }
        buf = new byte[initialSize];
        this.maxSize = maxSize;
    }

    /**
     * Writes the specified byte to this builder.
     *
     * @param b the byte to be written.
     * @see ByteArrayOutputStream#write(int)
     */
    public void write(int b) {
        ensureCapacity(count + 1);
        buf[count] = (byte) b;
        count += 1;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array starting at offset <code>off</code> to this builder.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @see ByteArrayOutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) {
        IOMisc.checkReadBounds(b, off, len);
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    /**
     * Writes the complete contents of this builder to the specified output stream argument, as if by calling the output
     * stream's write method using <code>out.write(buf, 0, count)</code>.
     *
     * @param out the output stream to which to write the data.
     * @throws IOException if an I/O error occurs.
     * @see ByteArrayOutputStream#writeTo(OutputStream)
     */
    public void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }

    /**
     * Writes the complete contents of this builder to the specified byte buffer argument, as if by calling the buffer's
     * write method using <code>out.put(buf, 0, count)</code>.
     *
     * @param out the byte buffer to which to write the data.
     * @throws IOException if an I/O error occurs.
     */
    public void writeTo(ByteBuffer out) throws IOException {
        out.put(buf, 0, count);
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
     * Trims the allocated but unused buffer space.
     */
    public void trimBuffer() {
        buf = Arrays.copyOf(buf, count);
    }

    /**
     * Returns the current size of the buffer.
     *
     * @return the value of the <code>byte count</code>, which is the number of valid bytes in this builder.
     * @see ByteArrayOutputStream#size()
     */
    public int size() {
        return count;
    }

    /**
     * Creates a newly allocated byte array. Its size is the current size of this builder and the valid contents of the
     * buffer have been copied into it.
     *
     * @return the current contents of this builder, as a byte array.
     * @see ByteArrayOutputStream#toByteArray()
     */
    public byte[] toByteArray() {
        return Arrays.copyOf(buf, count);
    }

    /**
     * Creates a newly allocated byte buffer, and its contents are copied from this builder.
     *
     * @return the current contents of this builder, as a byte buffer.
     */
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(toByteArray());
    }

    /**
     * Converts the buffer's contents into a string decoding bytes using the platform's default character set. The
     * length of the new <tt>String</tt> is a function of the character set, and hence may not be equal to the size of
     * the buffer.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with the default replacement string for the platform's default character set. The
     * {@linkplain java.nio.charset.CharsetDecoder} class should be used when more control over the decoding process is
     * required.
     *
     * @return String decoded from the buffer's contents.
     * @see ByteArrayOutputStream#toString()
     */
    public String toString() {
        return new String(buf, 0, count);
    }

    /**
     * Converts the buffer's contents into a string by decoding the bytes using the named
     * {@link java.nio.charset.Charset charset}. The length of the new
     * <tt>String</tt> is a function of the charset, and hence may not be equal
     * to the length of the byte array.
     *
     * <p> This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement string. The {@link java.nio.charset.CharsetDecoder} class
     * should be used when more control over the decoding process is required.
     *
     * @param charsetName the name of a supported {@link java.nio.charset.Charset charset}
     * @return String decoded from the buffer's contents.
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @see ByteArrayOutputStream#toString(String)
     */
    public String toString(String charsetName)
        throws UnsupportedEncodingException {
        return new String(buf, 0, count, charsetName);
    }

    /**
     * Converts current contents of this builder into a string with specified charset.
     *
     * @param charset specified charset
     * @return String decoded from the current contents of this builder
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
            throw new IllegalStateException("Buffer out of size: " + minCapacity);
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
        if (newCapacity < minCapacity) {
            return minCapacity;
        }
        return newCapacity;
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
            } catch (IOException e) {
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
