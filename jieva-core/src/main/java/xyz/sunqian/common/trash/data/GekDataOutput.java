package xyz.sunqian.common.trash.data;

import xyz.sunqian.annotations.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Data output interface, to write data into different types of destinations such as byte array, byte buffer and output
 * stream.
 */
public interface GekDataOutput {

    /**
     * Writes data into dest array as possible. The writing operation is limited by readable length of this and writable
     * space of dest. Returns actual written number (maybe 0) or -1 if end of the data is reached.
     *
     * @param dest dest array
     * @return actual written number (maybe 0) or -1 if end of the data is reached
     */
    default int write(byte[] dest) {
        return write(dest, 0);
    }

    /**
     * Writes data into dest array from given offset to end as possible. The writing operation is limited by readable
     * length of this and writable space of dest. Returns actual written number (maybe 0) or -1 if end of the data is
     * reached.
     *
     * @param dest   dest array
     * @param offset given offset
     * @return actual written number (maybe 0) or -1 if end of the data is reached
     */
    default int write(byte[] dest, int offset) {
        return write(dest, 0, dest.length);
    }

    /**
     * Writes data of specified length into dest array from given offset to end as possible. The writing operation is
     * limited by readable length of this and writable space of dest. Returns actual written number (maybe 0) or -1 if
     * end of the data is reached.
     *
     * @param dest   dest array
     * @param offset given offset
     * @param length specified length
     * @return actual written number (maybe 0) or -1 if end of the data is reached
     */
    int write(byte[] dest, int offset, int length);

    /**
     * Writes data into dest buffer as possible. The writing operation is limited by readable length of this and
     * writable space of dest. Returns actual written number (maybe 0) or -1 if end of the data is reached.
     *
     * @param dest dest buffer
     * @return actual written number (maybe 0) or -1 if end of the data is reached
     */
    default int write(ByteBuffer dest) {
        return write(dest, dest.remaining());
    }

    /**
     * Writes data of specified length into dest buffer as possible. The writing operation is limited by readable length
     * of this and writable space of dest. Returns actual written number (maybe 0) or -1 if end of the data is reached.
     *
     * @param dest   dest buffer
     * @param length specified length
     * @return actual written number (maybe 0) or -1 if end of the data is reached
     */
    int write(ByteBuffer dest, int length);

    /**
     * Writes data into dest stream as possible. The writing operation is limited by readable length of this and
     * writable space of dest. Returns actual written number (maybe 0) or -1 if end of the data is reached.
     *
     * @param dest dest stream
     * @return actual written number (maybe 0) or -1 if end of the data is reached
     */
    long write(OutputStream dest);

    /**
     * Writes data of specified length into dest stream as possible. The writing operation is limited by readable length
     * of this and writable space of dest. Returns actual written number (maybe 0) or -1 if end of the data is reached.
     *
     * @param dest   dest stream
     * @param length specified length
     * @return actual written number (maybe 0) or -1 if end of the data is reached
     */
    long write(OutputStream dest, long length);

    /**
     * Writes all data into an array and returns, return null if end of the data is reached.
     *
     * @return the array which was written into all data, or null if end of the data is reached
     */
    @Nullable
    byte[] toArray();

    /**
     * Writes all data into a buffer and returns, return null if end of the data is reached.
     *
     * @return The buffer which was written into all data, or null if end of the data is reached. The position is 0,
     * limit and capacity is length of data
     */
    @Nullable
    default ByteBuffer toBuffer() {
        byte[] array = toArray();
        if (array == null) {
            return null;
        }
        return ByteBuffer.wrap(array);
    }

    /**
     * Returns this as {@link InputStream}. This method is lazy, it does read operation if and only if returned stream
     * does read operation.
     *
     * @return this as {@link InputStream}
     */
    InputStream asInputStream();
}
