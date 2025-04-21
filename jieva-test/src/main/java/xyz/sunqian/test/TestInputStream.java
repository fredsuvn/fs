package xyz.sunqian.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * This is a testing input stream. It wraps a normal stream, then provides the {@link #setNextReadOption(ReadOps)} to
 * set behavior for next read operation.
 *
 * @author sunqian
 */
public class TestInputStream extends InputStream {

    private final InputStream in;
    private ReadOps readOps = ReadOps.READ_NORMAL;

    /**
     * Constructs with the specified wrapped input stream.
     *
     * @param in the specified wrapped input stream
     */
    public TestInputStream(InputStream in) {
        this.in = in;
    }

    /**
     * Sets the behavior for next read operation, including:
     * <ul>
     *     <li>{@link ReadOps#READ_NORMAL}</li>
     *     <li>{@link ReadOps#READ_ZERO}</li>
     *     <li>{@link ReadOps#REACH_END}</li>
     *     <li>{@link ReadOps#THROW}</li>
     * </ul>
     * The read operation includes read, skip, and available methods.
     * <p>
     * For the {@link #read()} method, {@link ReadOps#READ_ZERO} and {@link ReadOps#REACH_END} have the same effect.
     *
     * @param readOps the behavior for next read operation
     */
    public void setNextReadOption(ReadOps readOps) {
        this.readOps = readOps;
    }

    @Override
    public int read() throws IOException {
        switch (readOps) {
            case READ_NORMAL:
                return in.read();
            case READ_ZERO:
            case REACH_END: {
                readOps = ReadOps.READ_NORMAL;
                return -1;
            }
            default: {
                readOps = ReadOps.READ_NORMAL;
                throw new IOException();
            }
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        switch (readOps) {
            case READ_NORMAL:
                return in.read(b, off, len);
            case READ_ZERO: {
                readOps = ReadOps.READ_NORMAL;
                return 0;
            }
            case REACH_END: {
                readOps = ReadOps.READ_NORMAL;
                return -1;
            }
            default: {
                readOps = ReadOps.READ_NORMAL;
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
                readOps = ReadOps.READ_NORMAL;
                return 0;
            }
            default: {
                readOps = ReadOps.READ_NORMAL;
                throw new IOException();
            }
        }
    }

    @Override
    public int available() throws IOException {
        switch (readOps) {
            case READ_NORMAL:
                return in.available();
            case READ_ZERO:
            case REACH_END: {
                readOps = ReadOps.READ_NORMAL;
                return 0;
            }
            default: {
                readOps = ReadOps.READ_NORMAL;
                throw new IOException();
            }
        }
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    @Override
    public synchronized void mark(int readlimit) {
        if (Objects.equals(readOps, ReadOps.THROW)) {
            readOps = ReadOps.READ_NORMAL;
            throw new TestIOException();
        } else {
            in.mark(readlimit);
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (Objects.equals(readOps, ReadOps.THROW)) {
            readOps = ReadOps.READ_NORMAL;
            throw new IOException();
        } else {
            in.reset();
        }
    }

    @Override
    public void close() throws IOException {
        if (Objects.equals(readOps, ReadOps.THROW)) {
            readOps = ReadOps.READ_NORMAL;
            throw new IOException();
        } else {
            in.close();
        }
    }
}
