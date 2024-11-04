package xyz.sunqian.common.file;

import xyz.sunqian.annotations.ThreadSafe;

import java.io.File;
import java.io.FileDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This interface represents File Accessor to operate file.
 * <p>
 * An instance of {@link FileAcc} is opened if {@link #open(String)} is called, and it will be closed if
 * {@link #close()} is called or other error occurs.
 * <p>
 * The file accessor can create child streams by stream methods (such as {@link #inputStream()} and
 * {@link #outputStream()}). Only one child stream present at same time, must close the previous stream before getting
 * next one. Closing the stream does not close the file accessor, only {@link #close()} can close the file accessor. And
 * if the file accessor is closed, the associated child stream will be closed at same time.
 *
 * @author fresduvn
 */
@ThreadSafe
public interface FileAcc {

    /**
     * Returns file object of this.
     *
     * @return file object of this
     */
    File getFile();

    /**
     * Returns path of this file accessor.
     *
     * @return path of this file accessor
     */
    Path getPath();

    /**
     * Returns file attributes of this file.
     *
     * @return file attributes of this file
     * @throws FileException if any error occurs
     */
    default BasicFileAttributes getAttributes() throws FileException {
        return JieFile.basicFileAttributes(getPath());
    }

    /**
     * Opens this file accessor with specified mode:
     * <ul>
     *     <li>
     *         "r": read-only mode;
     *     </li>
     *     <li>
     *         "rw": read-write mode;
     *     </li>
     *     <li>
     *         "rws": read-write mode, and every update to the file's content or metadata will be written synchronously
     *         to the underlying storage device;
     *     </li>
     *     <li>
     *         "rwd": read-write mode, and every update to the file's content will be written synchronously to the
     *         underlying storage device;
     *     </li>
     * </ul>
     *
     * @param mode specified mode
     * @throws FileException if any error occurs
     */
    void open(String mode) throws FileException;

    /**
     * Returns length of file in bytes.
     *
     * @return length of file in bytes
     */
    default long length() {
        return getFile().length();
    }

    /**
     * Sets new length of file in bytes.
     * <p>
     * This operation could truncate or extend this file if the original length of file is greater or less than the new
     * length.
     * <p>
     * This file accessor should be opened by {@link #open(String)} before calling of this method.
     *
     * @param newLength new length
     * @throws FileException if any error occurs
     */
    void setLength(long newLength) throws FileException;

    /**
     * Opens and return a channel connected to this file.
     * <p>
     * This file accessor should be opened by {@link #open(String)} before calling of this method.
     *
     * @return a channel connected to this file
     * @throws FileException if any error occurs
     */
    FileChannel getChannel() throws FileException;

    /**
     * Returns descriptor of file accessor.
     * <p>
     * This file accessor should be opened by {@link #open(String)} before calling of this method.
     *
     * @return descriptor of file
     * @throws FileException if any error occurs
     */
    FileDescriptor getDescriptor() throws FileException;

    /**
     * Returns an {@link InputStream} of this file accessor. This method is equivalent to ({@link #inputStream(long)}):
     * <pre>
     *     return inputStream(0);
     * </pre>
     *
     * @return an {@link InputStream} of this file accessor
     * @throws FileException if any error occurs
     * @see #inputStream(long)
     */
    default InputStream inputStream() throws FileException {
        return inputStream(0);
    }

    /**
     * Returns an {@link InputStream} of this file accessor.
     * <p>
     * The stream starts at specified position (0-based) in bytes. If the position is &gt;= file length, a
     * {@link FileException} will be thrown.
     * <p>
     * This file accessor should be opened by {@link #open(String)} before calling of this method.
     *
     * @param position specified position
     * @return an {@link InputStream} of this file accessor
     * @throws FileException if any error occurs
     */
    InputStream inputStream(long position) throws FileException;

    /**
     * Returns an {@link OutputStream} of this file accessor. This method is equivalent to
     * ({@link #outputStream(long)}):
     * <pre>
     *     return outputStream(0);
     * </pre>
     *
     * @return an {@link OutputStream} of this file accessor
     * @throws FileException if any error occurs
     * @see #outputStream(long)
     * @see #outputStream(long, boolean)
     */
    default OutputStream outputStream() throws FileException {
        return outputStream(0);
    }

    /**
     * Returns an {@link OutputStream} of this file accessor.
     * <p>
     * The stream starts at specified position (0-based) in bytes. If the position is &gt;= file length, the file will
     * be auto extend. This method is equivalent to ({@link #outputStream(long, boolean)}):
     * <pre>
     *     return outputStream(position, true);
     * </pre>
     *
     * @param position specified position
     * @return an {@link OutputStream} of this file accessor
     * @throws FileException if any error occurs
     * @see #outputStream(long, boolean)
     */
    default OutputStream outputStream(long position) throws FileException {
        return outputStream(position, true);
    }

    /**
     * Returns an {@link OutputStream} of this file accessor.
     * <p>
     * The stream starts at specified position (0-based) in bytes. If the position is &gt;= file length, and if the
     * {@code autoExtend} is false, a {@link FileException} will be thrown. Otherwise, the file will be auto extend.
     * <p>
     * This file accessor should be opened by {@link #open(String)} before calling of this method.
     *
     * @param position   specified position
     * @param autoExtend whether auto extends file length if length of written data out of file bounds
     * @return an {@link OutputStream} of this file accessor
     * @throws FileException if any error occurs
     */
    OutputStream outputStream(long position, boolean autoExtend) throws FileException;

    /**
     * Closes this file accessor.
     * <p>
     * If this file accessor has not been opened or has been closed, nothing will happen.
     *
     * @throws FileException if any error occurs
     */
    void close() throws FileException;
}
