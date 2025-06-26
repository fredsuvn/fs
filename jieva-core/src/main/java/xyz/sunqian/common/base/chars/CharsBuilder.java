package xyz.sunqian.common.base.chars;

import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IORuntimeException;

import java.io.CharArrayWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Arrays;

import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;

/**
 * {@code CharsBuilder} is used to build char arrays and their derived objects by appending char data. It is similar to
 * {@link CharArrayWriter}, provides compatible methods, but is not thread-safe. This class is also the subtype of the
 * {@link Writer} and {@link CharSequence}, but the {@code close()} method has no effect.
 *
 * @author sunqian
 */
public class CharsBuilder extends Writer implements CharSequence {

    // Max array size.
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private final int maxSize;

    private char[] buf;
    private int count;

    /**
     * Constructs with 32-chars initial capacity.
     */
    public CharsBuilder() {
        this(32);
    }

    /**
     * Constructs with the specified initial capacity in chars.
     *
     * @param initialCapacity the specified initial capacity in chars
     * @throws IllegalArgumentException if size is negative
     */
    public CharsBuilder(int initialCapacity) {
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
    public CharsBuilder(int initialCapacity, int maxCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Negative initial capacity: " + initialCapacity + ".");
        }
        if (maxCapacity < 0) {
            throw new IllegalArgumentException("Negative max capacity: " + maxCapacity + ".");
        }
        if (initialCapacity > maxCapacity) {
            throw new IllegalArgumentException("Initial capacity must <= max capacity!");
        }
        buf = new char[initialCapacity];
        this.maxSize = maxCapacity;
    }

    /**
     * Appends the specified char to this builder.
     *
     * @param b the specified char
     */
    public void write(int b) {
        ensureCapacity(count + 1);
        buf[count] = (char) b;
        count += 1;
    }

    /**
     * Appends the specified number of chars from the given array, starting at the specified offset.
     *
     * @param b   the given array
     * @param off the specified offset
     * @param len the specified number
     */
    public void write(char[] b, int off, int len) {
        checkOffsetLength(b.length, off, len);
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    /**
     * Writes the appended data of this builder to the specified writer.
     *
     * @param out the specified writer
     * @throws IORuntimeException if an I/O error occurs
     */
    public void writeTo(Writer out) throws IORuntimeException {
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
    public void writeTo(CharBuffer out) throws IORuntimeException {
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
    public char[] toCharArray() {
        return Arrays.copyOf(buf, count);
    }

    /**
     * Returns a new buffer containing a copy of the appended data.
     *
     * @return a new buffer containing a copy of the appended data
     */
    public CharBuffer toCharBuffer() {
        return CharBuffer.wrap(toCharArray());
    }

    /**
     * Returns a string from a copy of the appended data.
     *
     * @return a string from a copy of the appended data
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

    /**
     * Appends the specified char to this builder.
     *
     * @param b the specified char
     * @return this builder
     */
    public CharsBuilder append(int b) {
        write(b);
        return this;
    }

    /**
     * Appends the specified char to this builder.
     *
     * @param b the specified char
     * @return this builder
     */
    public CharsBuilder append(char b) {
        write(b);
        return this;
    }

    /**
     * Appends all chars from the given array.
     *
     * @param chars the given array
     * @return this builder
     */
    public CharsBuilder append(char[] chars) {
        write(chars, 0, chars.length);
        return this;
    }

    /**
     * Appends the specified number of chars from the given array, starting at the specified offset.
     *
     * @param chars  the given array
     * @param offset the specified offset
     * @param length the specified number
     * @return this builder
     */
    public CharsBuilder append(char[] chars, int offset, int length) {
        write(chars, offset, length);
        return this;
    }

    /**
     * Reads and appends all chars from the given buffer.
     *
     * @param chars the given buffer
     * @return this builder
     */
    public CharsBuilder append(CharBuffer chars) {
        if (!chars.hasRemaining()) {
            return this;
        }
        if (chars.hasArray()) {
            write(chars.array(), BufferKit.arrayStartIndex(chars), chars.remaining());
            chars.position(chars.position() + chars.remaining());
        } else {
            char[] remaining = BufferKit.read(chars);
            write(remaining, 0, remaining.length);
        }
        return this;
    }

    /**
     * Reads and appends all chars from the given reader.
     *
     * @param reader the given reader
     * @return this builder
     * @throws IORuntimeException if an I/O error occurs
     */
    public CharsBuilder append(Reader reader) throws IORuntimeException {
        return append(reader, IOKit.bufferSize());
    }

    /**
     * Reads and appends all chars from the given reader with the specified buffer size for each reading.
     *
     * @param reader     the given reader
     * @param bufferSize the specified buffer size for each reading
     * @return this builder
     * @throws IORuntimeException if an I/O error occurs
     */
    public CharsBuilder append(Reader reader, int bufferSize) throws IORuntimeException {
        char[] buffer = new char[bufferSize];
        while (true) {
            try {
                int readSize = reader.read(buffer);
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
     * Appends all chars from the given builder.
     *
     * @param builder the given reader
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
