package xyz.sunqian.test;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;

/**
 * This is a testing reader. It wraps a normal reader, then provides the {@link #setNextOperation(ReadOps)} to set
 * behavior for next read operation.
 *
 * @author sunqian
 */
public class TestReader extends Reader {

    private final Reader in;
    private ReadOps readOps = ReadOps.READ_NORMAL;
    private int times = 0;
    private Boolean markSupported = null;

    /**
     * Constructs with the specified wrapped reader.
     *
     * @param in the specified wrapped reader
     */
    public TestReader(Reader in) {
        this.in = in;
    }

    /**
     * Sets the behavior for the next I/O operation. This method is equivalent to:
     * <pre>{@code
     *     setNextOperations(readOps, 1);
     * }</pre>
     *
     * @param readOps the behavior for the next I/O operation
     * @see #setNextOperations(ReadOps, int)
     */
    public void setNextOperation(ReadOps readOps) {
        setNextOperations(readOps, 1);
    }

    /**
     * Set the behaviors for the next specified number of I/O operations. After executing the specified number of times,
     * the behaviors will be reset to normal.
     *
     * @param readOps the behaviors for the next specified number of I/O operations
     * @param times   the number of I/O operations
     */
    public void setNextOperations(ReadOps readOps, int times) {
        this.readOps = readOps;
        this.times = times;
    }

    @Override
    public int read() throws IOException {
        switch (readOps) {
            case READ_NORMAL:
                return in.read();
            case READ_ZERO:
            case REACH_END: {
                reduceTimes();
                return -1;
            }
            default: {
                reduceTimes();
                throw new IOException();
            }
        }
    }

    @Override
    public int read(char[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(char[] b, int off, int len) throws IOException {
        switch (readOps) {
            case READ_NORMAL:
                return in.read(b, off, len);
            case READ_ZERO: {
                reduceTimes();
                return 0;
            }
            case REACH_END: {
                reduceTimes();
                return -1;
            }
            default: {
                reduceTimes();
                throw new IOException();
            }
        }
    }

    @Override
    public long skip(long n) throws IOException {
        switch (readOps) {
            case READ_NORMAL:
                return in.skip(n);
            case READ_ZERO:
            case REACH_END: {
                reduceTimes();
                return 0;
            }
            default: {
                reduceTimes();
                throw new IOException();
            }
        }
    }

    /**
     * Sets the mark-supported for this {@link Reader}. If the {@code markSupported} is null, this {@link Reader} will
     * directly use the mark-supported flag of wrapped source.
     *
     * @param markSupported the mark-supported flag, can be null
     */
    public void markSupported(Boolean markSupported) {
        this.markSupported = markSupported;
    }

    @Override
    public boolean markSupported() {
        if (markSupported == null) {
            return in.markSupported();
        }
        return markSupported;
    }

    @Override
    public synchronized void mark(int readlimit) throws IOException {
        if (Objects.equals(readOps, ReadOps.THROW)) {
            reduceTimes();
            throw new IOException();
        } else {
            in.mark(readlimit);
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (Objects.equals(readOps, ReadOps.THROW)) {
            reduceTimes();
            throw new IOException();
        } else {
            in.reset();
        }
    }

    @Override
    public void close() throws IOException {
        if (Objects.equals(readOps, ReadOps.THROW)) {
            reduceTimes();
            throw new IOException();
        } else {
            in.close();
        }
    }

    private void reduceTimes() {
        times--;
        if (times <= 0) {
            times = 0;
            readOps = ReadOps.READ_NORMAL;
        }
    }
}
