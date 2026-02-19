package space.sunqian.fs.base.chars;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.lang.Checker;
import space.sunqian.fs.io.BufferKit;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.CharArrayWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.Arrays;

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

    private char @Nonnull [] buf;
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
    public CharsBuilder(int initialCapacity) throws IllegalArgumentException {
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
    public CharsBuilder(int initialCapacity, int maxCapacity) throws IllegalArgumentException {
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
     * Appends all chars from the given array.
     *
     * @param cbuf the given array
     */
    public void write(char @Nonnull [] cbuf) {
        ensureCapacity(count + cbuf.length);
        System.arraycopy(cbuf, 0, buf, count, cbuf.length);
        count += cbuf.length;
    }

    /**
     * Appends the specified number of chars from the given array, starting at the specified offset.
     *
     * @param cbuf the given array
     * @param off  the specified offset
     * @param len  the specified number
     * @throws IndexOutOfBoundsException if the offset or number is out of bounds
     */
    public void write(char @Nonnull [] cbuf, int off, int len) throws IndexOutOfBoundsException {
        Checker.checkOffLen(off, len, cbuf.length);
        ensureCapacity(count + len);
        System.arraycopy(cbuf, off, buf, count, len);
        count += len;
    }

    /**
     * Writes the appended data of this builder to the specified writer.
     *
     * @param out the specified writer
     * @throws IORuntimeException if an I/O error occurs
     */
    public void writeTo(@Nonnull Writer out) throws IORuntimeException {
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
    public void writeTo(@Nonnull CharBuffer out) throws IORuntimeException {
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
    public char @Nonnull [] toCharArray() {
        return Arrays.copyOf(buf, count);
    }

    /**
     * Returns a new buffer containing a copy of the appended data.
     *
     * @return a new buffer containing a copy of the appended data
     */
    public @Nonnull CharBuffer toCharBuffer() {
        return CharBuffer.wrap(toCharArray());
    }

    /**
     * Returns a string from a copy of the appended data.
     *
     * @return a string from a copy of the appended data
     */
    public @Nonnull String toString() {
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
     * @param c the specified char
     * @return this builder
     */
    public @Nonnull CharsBuilder append(int c) {
        write(c);
        return this;
    }

    /**
     * Appends the specified char to this builder.
     *
     * @param c the specified char
     * @return this builder
     */
    public @Nonnull CharsBuilder append(char c) {
        write(c);
        return this;
    }

    /**
     * Appends all chars from the given array.
     *
     * @param chars the given array
     * @return this builder
     */
    public @Nonnull CharsBuilder append(char @Nonnull [] chars) {
        write(chars);
        return this;
    }

    /**
     * Appends the specified number of chars from the given array, starting at the specified offset.
     *
     * @param chars  the given array
     * @param offset the specified offset
     * @param length the specified number
     * @return this builder
     * @throws IndexOutOfBoundsException if the offset or number is out of bounds
     */
    public @Nonnull CharsBuilder append(
        char @Nonnull [] chars, int offset, int length
    ) throws IndexOutOfBoundsException {
        write(chars, offset, length);
        return this;
    }

    /**
     * Reads and appends all chars from the given buffer.
     *
     * @param chars the given buffer
     * @return this builder
     */
    public @Nonnull CharsBuilder append(@Nonnull CharBuffer chars) {
        int remaining = chars.remaining();
        if (remaining == 0) {
            return this;
        }
        if (chars.hasArray()) {
            write(chars.array(), BufferKit.arrayStartIndex(chars), chars.remaining());
            chars.position(chars.position() + chars.remaining());
        } else {
            char[] data = new char[remaining];
            chars.get(data);
            write(data);
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
    public @Nonnull CharsBuilder append(@Nonnull Reader reader) throws IORuntimeException {
        return append(reader, IOKit.bufferSize());
    }

    /**
     * Reads and appends all chars from the given reader with the specified buffer size for each reading.
     *
     * @param reader  the given reader
     * @param bufSize the specified buffer size for each reading
     * @return this builder
     * @throws IllegalArgumentException if the buffer size {@code <= 0}
     * @throws IORuntimeException       if an I/O error occurs
     */
    public @Nonnull CharsBuilder append(
        @Nonnull Reader reader, int bufSize
    ) throws IllegalArgumentException, IORuntimeException {
        if (bufSize <= 0) {
            throw new IllegalArgumentException("The buffer size must > 0.");
        }
        char[] buffer = new char[bufSize];
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
    public @Nonnull CharsBuilder append(@Nonnull CharsBuilder builder) {
        write(builder.buf, 0, builder.count);
        return this;
    }

    @Override
    public int length() {
        return count;
    }

    @Override
    public char charAt(int index) throws IndexOutOfBoundsException {
        if (index >= count) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index + ".");
        }
        return buf[index];
    }

    @Override
    public @Nonnull CharSequence subSequence(int start, int end) {
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

    private int newCapacity(int newCapacity, int minCapacity) {
        if (newCapacity <= 0 || newCapacity > maxSize) {
            return maxSize;
        }
        return Math.max(newCapacity, minCapacity);
    }
}
