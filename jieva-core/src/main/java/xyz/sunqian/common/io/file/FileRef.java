package xyz.sunqian.common.io.file;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.IORuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This interface represents a file reference used to retrieve information and manipulate the file.
 *
 * @author sunqian
 */
public interface FileRef {

    /**
     * Returns a new {@link FileRef} from the given path.
     *
     * @param path the given path
     * @return a new {@link FileRef} from the given path
     */
    static @Nonnull FileRef of(@Nonnull Path path) {
        return new FileRef() {

            private File file;

            @Override
            public @Nonnull File getFile() {
                if (file == null) {
                    file = path.toFile();
                }
                return file;
            }

            @Override
            public @Nonnull Path getPath() {
                return path;
            }
        };
    }

    /**
     * Returns a new {@link FileRef} from the given file.
     *
     * @param file the given file
     * @return a new {@link FileRef} from the given file
     */
    static @Nonnull FileRef of(@Nonnull File file) {
        return new FileRef() {

            private Path path;

            @Override
            public @Nonnull File getFile() {
                return file;
            }

            @Override
            public @Nonnull Path getPath() {
                if (path == null) {
                    path = file.toPath();
                }
                return path;
            }
        };
    }

    /**
     * Returns the {@link File} object related to this file reference.
     *
     * @return the {@link File} object related to this file reference
     */
    @Nonnull
    File getFile();

    /**
     * Returns the {@link Path} object related to this file reference.
     *
     * @return the {@link Path} object related to this file reference
     */
    @Nonnull
    Path getPath();

    /**
     * Returns the basic file attributes of the referenced file.
     *
     * @param linkOptions options indicating how symbolic links are handled, can be empty
     * @return the basic file attributes of the referenced file
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull BasicFileAttributes getBasicFileAttributes(
        @Nonnull LinkOption @Nonnull ... linkOptions
    ) throws IORuntimeException {
        try {
            return Files.readAttributes(getPath(), BasicFileAttributes.class, linkOptions);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Returns a new {@link FileInputStream} for reading the referenced file.
     *
     * @return a new {@link FileInputStream} for reading the referenced file
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull FileInputStream fileInputStream() throws IORuntimeException {
        try {
            return new FileInputStream(getFile());
        } catch (FileNotFoundException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Returns a new {@link FileOutputStream} for writing the referenced file in overwrite mode. In overwrite mode, the
     * file's content will be overwritten starting from the beginning, and the file's length will be truncated or
     * extended to the number of bytes actually written.
     *
     * @return a new {@link FileOutputStream} for writing the referenced file in overwrite mode
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull FileOutputStream fileOutputStream() throws IORuntimeException {
        return fileOutputStream(false);
    }

    /**
     * Returns a new {@link FileOutputStream} for writing the referenced file in the specified mode.
     * <p>
     * In overwrite mode, the file's content will be overwritten starting from the beginning, and the file's length will
     * be truncated or extended to the number of bytes actually written. Otherwise in append mode, new bytes will be
     * written starting at the end of the file, and the file's length will be extended by the number of bytes actually
     * written.
     *
     * @param append {@code true} for append mode, {@code false} for overwrite mode
     * @return a new {@link FileOutputStream} for writing the referenced file in the specified mode
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull FileOutputStream fileOutputStream(boolean append) throws IORuntimeException {
        try {
            return new FileOutputStream(getFile(), append);
        } catch (FileNotFoundException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Opens and returns a new {@link InputStream} for reading the referenced file. This method is equivalent to
     * {@link Files#newInputStream(Path, OpenOption...)}.
     *
     * @param options options specifying how the file is opened
     * @return a new {@link InputStream} for reading the referenced file
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull InputStream newInputStream(
        @Nonnull OpenOption @Nonnull ... options
    ) throws IORuntimeException {
        try {
            return Files.newInputStream(getPath(), options);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Opens and returns a new {@link OutputStream} for writing the referenced file. This method is equivalent to
     * {@link Files#newOutputStream(Path, OpenOption...)}.
     *
     * @param options options specifying how the file is opened
     * @return a new {@link OutputStream} for writing the referenced file
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull OutputStream newOutputStream(
        @Nonnull OpenOption @Nonnull ... options
    ) throws IORuntimeException {
        try {
            return Files.newOutputStream(getPath(), options);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Opens and returns a new {@link FileChannel} for operating the referenced file. This method is equivalent to
     * {@link FileChannel#open(Path, OpenOption...)}.
     *
     * @return a new {@link FileChannel} for operating the referenced file
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull FileChannel getFileChannel(
        @Nonnull OpenOption @Nonnull ... options
    ) throws IORuntimeException {
        try {
            return FileChannel.open(getPath(), options);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
