package space.sunqian.common.io;

import space.sunqian.annotations.ThreadSafe;

/**
 * This interface provides I/O operations.
 *
 * @author sunqian
 */
@ThreadSafe
public interface IOOperator extends ByteIOOperator, CharIOOperator {

    /**
     * Returns the default {@link IOOperator} instance of which buffer size of {@link IOKit#bufferSize()}.
     *
     * @return the default {@link IOOperator} instance of which buffer size of {@link IOKit#bufferSize()}
     */
    static IOOperator defaultOperator() {
        return IOKit.io;
    }

    /**
     * Returns a {@link IOOperator} instance with the given buffer size. If the buffer size equals to the
     * {@link IOKit#bufferSize()}, returns the default {@link IOOperator} instance, otherwise returns a new one by
     * {@link #newOperator(int)}.
     *
     * @param bufSize the given buffer size, must {@code > 0}
     * @return a {@link IOOperator} instance with the given buffer size
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static IOOperator get(int bufSize) throws IllegalArgumentException {
        IOOperator io = IOKit.io;
        return bufSize == io.bufferSize() ? io : newOperator(bufSize);
    }

    /**
     * Returns a new {@link IOOperator} instance with the given buffer size.
     *
     * @param bufSize the given buffer size, must {@code > 0}
     * @return a new {@link IOOperator} instance with the given buffer size
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static IOOperator newOperator(int bufSize) throws IllegalArgumentException {
        IOChecker.checkBufSize(bufSize);
        return () -> bufSize;
    }
}
