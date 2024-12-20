package xyz.sunqian.common.io;

import xyz.sunqian.common.base.JieChars;

import java.io.CharArrayWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Arrays;

/**
 * This class is used to build chars, provides an API compatible with {@link CharArrayWriter}, but with no guarantee of
 * synchronization. It is {@code char} version of {@link BytesBuilder} and an implementation of {@link Writer} and
 * {@link CharSequence}.
 * <p>
 * Like {@link CharArrayWriter}, this class also has a buffer space to store the bytes, and close method has no effect.
 * The methods in this class can be called after the builder has been closed without exception.
 *
 * @author sunqian
 */
public class CharsBuilder extends Writer implements CharSequence {

    /**
     * Max buffer size.
     */
    public static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private final int maxSize;

    private char[] buf;
    private int count;

    /**
     * Constructs with 32 chars of buffer capacity, it is equivalent to:
     * <pre>
     *     CharsBuilder(32)
     * </pre>
     *
     * @see #CharsBuilder(int)
     * @see CharArrayWriter#CharArrayWriter()
     */
    public CharsBuilder() {
        this(32);
    }

    /**
     * Constructs with specified initial size of buffer capacity in chars.
     *
     * @param initialSize the initial size
     * @throws IllegalArgumentException if size is negative
     * @see CharArrayWriter#CharArrayWriter(int)
     */
    public CharsBuilder(int initialSize) {
        this(initialSize, MAX_ARRAY_SIZE);
    }

    /**
     * Constructs with specified initial size and max size of buffer capacity in chars.
     *
     * @param initialSize the initial size
     * @param maxSize     max size
     * @throws IllegalArgumentException if initial size is negative or max size &lt;= 0 or initial size &gt; max size
     */
    public CharsBuilder(int initialSize, int maxSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative initialSize: " + initialSize + ".");
        }
        if (maxSize < 0) {
            throw new IllegalArgumentException("Negative maxSize: " + maxSize + ".");
        }
        if (initialSize > maxSize) {
            throw new IllegalArgumentException("The initialSize must <= maxSize.");
        }
        buf = new char[initialSize];
        this.maxSize = maxSize;
    }

    /**
     * Writes the specified char to this builder.
     *
     * @param b the char to be written
     * @see CharArrayWriter#write(int)
     */
    public void write(int b) {
        ensureCapacity(count + 1);
        buf[count] = (char) b;
        count += 1;
    }

    /**
     * Writes specified length of chars from the specified char array starting at specified offset to this builder.
     *
     * @param b   specified char array
     * @param off specified offset
     * @param len specified length
     * @see CharArrayWriter#write(char[], int, int)
     */
    public void write(char[] b, int off, int len) {
        IOMisc.checkReadBounds(b, off, len);
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    /**
     * Writes contents of this builder to specified writer.
     *
     * @param out specified writer
     * @throws IORuntimeException if an I/O error occurs
     * @see CharArrayWriter#writeTo(Writer)
     */
    public void writeTo(Writer out) throws IORuntimeException {
        try {
            out.write(buf, 0, count);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Writes contents of this builder to specified char buffer.
     *
     * @param out specified char buffer
     * @throws IORuntimeException if an I/O error occurs
     */
    public void writeTo(CharBuffer out) throws IORuntimeException {
        try {
            out.put(buf, 0, count);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Resets the <code>char count</code> of buffer of this builder to zero, so that all currently accumulated chars in
     * this builder is discarded. This builder can be used again, reusing the already allocated buffer space.
     * <p>
     * To trim and release discarded allocated buffer space, use {@link #trimBuffer()}.
     *
     * @see CharArrayWriter#reset()
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
     * Returns the current size of the buffer (value of the <code>char count</code>).
     *
     * @return the value of the <code>char count</code>, which is the number of valid chars in this builder.
     * @see CharArrayWriter#size()
     */
    public int size() {
        return count;
    }

    /**
     * Creates and returns a newly allocated char array. Its size is the current size of this builder and the valid
     * contents of the buffer have been copied into it.
     *
     * @return copy of the current contents of this builder, as a char array.
     * @see CharArrayWriter#toCharArray()
     */
    public char[] toCharArray() {
        return Arrays.copyOf(buf, count);
    }

    /**
     * Creates and returns a newly allocated char buffer. Its size is the current size of this builder and the valid
     * contents of the buffer have been copied into it.
     *
     * @return copy of the current contents of this builder, as a char buffer.
     */
    public CharBuffer toCharBuffer() {
        return CharBuffer.wrap(toCharArray());
    }

    /**
     * Converts input data to a string.
     *
     * @return the string
     * @see CharArrayWriter#toString()
     */
    public String toString() {
        return new String(buf, 0, count);
    }

    /**
     * No effect for this builder.
     */
    @Override
    public void flush() {
    }

    /**
     * No effect for this builder.
     */
    @Override
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
     * Appends a char into this builder.
     *
     * @param b a char
     * @return this builder
     */
    public CharsBuilder append(char b) {
        write(b);
        return this;
    }

    /**
     * Appends given chars into this builder.
     *
     * @param chars given chars
     * @return this builder
     */
    public CharsBuilder append(char[] chars) {
        write(chars, 0, chars.length);
        return this;
    }

    /**
     * Appends given chars from specified offset up to specified length into this builder.
     *
     * @param chars  given chars
     * @param offset specified offset
     * @param length specified length
     * @return this builder
     */
    public CharsBuilder append(char[] chars, int offset, int length) {
        write(chars, offset, length);
        return this;
    }

    /**
     * Reads and appends given char buffer into this builder.
     *
     * @param chars given char buffer
     * @return this builder
     */
    public CharsBuilder append(CharBuffer chars) {
        if (!chars.hasRemaining()) {
            return this;
        }
        if (chars.hasArray()) {
            write(chars.array(), JieBuffer.getArrayStartIndex(chars), chars.remaining());
            chars.position(chars.position() + chars.remaining());
        } else {
            char[] remaining = JieChars.getChars(chars);
            write(remaining, 0, remaining.length);
        }
        return this;
    }

    /**
     * Reads and appends given reader into this builder.
     *
     * @param in given reader
     * @return this builder
     * @throws IORuntimeException if any IO problem occurs
     */
    public CharsBuilder append(Reader in) throws IORuntimeException {
        return append(in, JieIO.BUFFER_SIZE);
    }

    /**
     * Reads and appends given reader into this builder with specified buffer size for per reading.
     *
     * @param in         given reader
     * @param bufferSize specified buffer size
     * @return this builder
     * @throws IORuntimeException if any IO problem occurs
     */
    public CharsBuilder append(Reader in, int bufferSize) throws IORuntimeException {
        char[] buffer = new char[bufferSize];
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
     * Appends contents of given chars builder into this builder.
     *
     * @param builder given chars builder
     * @return this builder
     */
    public CharsBuilder append(CharsBuilder builder) {
        write(builder.buf, 0, builder.count);
        return this;
    }

    @Override
    public int length() {
        return count;
    }

    @Override
    public char charAt(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Illegal index, must >= 0: " + index + ".");
        }
        if (index >= count) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index + ".");
        }
        return buf[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }
}
