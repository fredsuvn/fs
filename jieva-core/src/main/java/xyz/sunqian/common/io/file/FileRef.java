package xyz.sunqian.common.io.file;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Kit;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IORuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

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
     * Returns a new {@link FileRef} from the given path.
     *
     * @param path the given path
     * @return a new {@link FileRef} from the given path
     */
    static @Nonnull FileRef of(@Nonnull String path) {
        return of(Paths.get(path));
    }

    /**
     * Returns a new {@link FileRef} from the given path string, or a sequence of strings.
     *
     * @param first the path string or initial part of the path string
     * @param more  additional path string or initial parts of the path string
     * @return a new {@link FileRef} from the given path string, or a sequence of strings
     */
    static @Nonnull FileRef of(@Nonnull String first, @Nonnull String @Nonnull ... more) {
        return of(Paths.get(first, more));
    }

    /**
     * Returns a new {@link FileRef} from the given uri.
     *
     * @param uri the given uri
     * @return a new {@link FileRef} from the given uri
     */
    static @Nonnull FileRef of(@Nonnull URI uri) {
        return of(Paths.get(uri));
    }

    /**
     * Returns a new {@link FileRef} from the given url.
     *
     * @param url the given url
     * @return a new {@link FileRef} from the given url
     * @throws IORuntimeException if the url is unsupported
     */
    static @Nonnull FileRef of(@Nonnull URL url) throws IORuntimeException {
        try {
            return of(Paths.get(url.toURI()));
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
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
     * Returns the content type of the referenced file. The return value is in form of {@code MIME}.
     *
     * @return the content type of the referenced file, or {@code null} if the content type cannot be determined
     * @throws IORuntimeException if any error occurs
     */
    default @Nullable String getContentType() throws IORuntimeException {
        return Kit.uncheck(
            () -> Files.probeContentType(getPath()),
            IORuntimeException::new
        );
    }

    /**
     * Returns all bytes of the referenced file.
     *
     * @return a new array contains all bytes of the referenced file
     * @throws IORuntimeException if any error occurs
     */
    default byte @Nonnull [] readBytes() throws IORuntimeException {
        try {
            return Files.readAllBytes(getPath());
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Returns string from the referenced file with the {@link CharsKit#defaultCharset()}.
     *
     * @return a string from the referenced file with the {@link CharsKit#defaultCharset()}
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull String readString() throws IORuntimeException {
        return readString(CharsKit.defaultCharset());
    }

    /**
     * Returns string from the referenced file with the specified charset.
     *
     * @param charset the specified charset
     * @return a string from the referenced file with the specified charset
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull String readString(@Nonnull Charset charset) throws IORuntimeException {
        return new String(readBytes(), charset);
    }

    /**
     * Returns all lines from the referenced file with the {@link CharsKit#defaultCharset()}. This method recognizes the
     * following as line separators:
     * <ul>
     *     <li>'\r\n'</li>
     *     <li>'\n'</li>
     *     <li>'\r'</li>
     * </ul>
     *
     * @return all lines from the referenced file with the {@link CharsKit#defaultCharset()}
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull List<String> readLines() throws IORuntimeException {
        return readLines(CharsKit.defaultCharset());
    }

    /**
     * Returns all lines from the referenced file with the specified charset. This method recognizes the following as
     * line separators:
     * <ul>
     *     <li>'\r\n'</li>
     *     <li>'\n'</li>
     *     <li>'\r'</li>
     * </ul>
     *
     * @param charset the specified charset
     * @return all lines from the referenced file with the specified charset
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull List<String> readLines(@Nonnull Charset charset) throws IORuntimeException {
        try {
            return Files.readAllLines(getPath(), charset);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Write bytes to the referenced file with the open options. If no option is specified, this method creates a new
     * file or overwrites an existing file.
     *
     * @param bytes   the bytes to write
     * @param options the open options
     * @throws IORuntimeException if any error occurs
     */
    default void writeBytes(byte @Nonnull [] bytes, OpenOption @Nonnull ... options) throws IORuntimeException {
        try {
            Files.write(getPath(), bytes, options);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Write string to the referenced file with the {@link CharsKit#defaultCharset()} and open options. If no option is
     * specified, this method creates a new file or overwrites an existing file.
     *
     * @param string  the string to write
     * @param options the open options
     * @throws IORuntimeException if any error occurs
     */
    default void writeString(@Nonnull String string, OpenOption @Nonnull ... options) throws IORuntimeException {
        writeString(string, CharsKit.defaultCharset(), options);
    }

    /**
     * Write string to the referenced file with the specified charset and open options. If no option is specified, this
     * method creates a new file or overwrites an existing file.
     *
     * @param string  the string to write
     * @param charset the specified charset
     * @param options the open options
     * @throws IORuntimeException if any error occurs
     */
    default void writeString(@Nonnull String string, Charset charset, OpenOption @Nonnull ... options) throws IORuntimeException {
        writeBytes(string.getBytes(charset), options);
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
     * Opens and returns a new {@link InputStream} for reading the referenced file. If no option is specified, this
     * method opens the file for reading.
     * <p>
     * This method is equivalent to {@link Files#newInputStream(Path, OpenOption...)}.
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
     * Opens and returns a new {@link OutputStream} for writing the referenced file. If no option is specified, this
     * method opens the file for writing, creating the file if it doesn't exist, or truncating file to {@code 0} size if
     * it exists.
     * <p>
     * This method is equivalent to {@link Files#newOutputStream(Path, OpenOption...)}.
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
     * Opens and returns a new {@link FileChannel} for operating the referenced file.
     * <p>
     * This method is equivalent to {@link FileChannel#open(Path, OpenOption...)}.
     *
     * @return a new {@link FileChannel} for operating the referenced file
     * @throws IORuntimeException if any error occurs
     */
    default @Nonnull FileChannel newFileChannel(
        @Nonnull OpenOption @Nonnull ... options
    ) throws IORuntimeException {
        try {
            return FileChannel.open(getPath(), options);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
