package xyz.sunqian.common.file;

import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.common.io.JieIn;
import xyz.sunqian.common.io.JieOut;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * File utilities.
 *
 * @author sunq62
 */
public class JieFile {

    /**
     * Using {@link RandomAccessFile} to read all bytes of given path.
     *
     * @param path given path
     * @return read bytes
     */
    public static byte[] readBytes(Path path) {
        return readBytes(path, 0, -1);
    }

    /**
     * Using {@link RandomAccessFile} to read given length bytes of given path from offset position, the given length
     * may be set to -1 to read to end of file.
     *
     * @param path   given path
     * @param offset offset position
     * @param length given length, maybe -1 to read to end of file
     * @return read bytes
     */
    public static byte[] readBytes(Path path, long offset, long length) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "r")) {
            return JieIO.read(JieIn.wrap(random, offset));
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    /**
     * Using {@link RandomAccessFile} to read all bytes of given path. The read bytes will be encoded to String with
     * {@link JieChars#defaultCharset()}.
     *
     * @param path given path
     * @return read string
     */
    public static String readString(Path path) {
        return readString(path, JieChars.defaultCharset());
    }

    /**
     * Using {@link RandomAccessFile} to read all bytes of given path. The read bytes will be encoded to String with
     * given charset.
     *
     * @param path    given path
     * @param charset given charset
     * @return read string
     */
    public static String readString(Path path, Charset charset) {
        return readString(path, 0, -1, charset);
    }

    /**
     * Using {@link RandomAccessFile} to read given length bytes of given path from offset position, the given length
     * may be set to -1 to read to end of file. The read bytes will be encoded to String with
     * {@link JieChars#defaultCharset()}.
     *
     * @param path   given path
     * @param offset offset position
     * @param length given length, maybe -1 to read to end of file
     * @return read string
     */
    public static String readString(Path path, long offset, long length) {
        return readString(path, offset, length, JieChars.defaultCharset());
    }

    /**
     * Using {@link RandomAccessFile} to read given length bytes of given path from offset position, the given length
     * may be set to -1 to read to end of file. The read bytes will be encoded to String with given charset.
     *
     * @param path    given path
     * @param offset  offset position
     * @param length  given length, maybe -1 to read to end of file
     * @param charset given charset
     * @return read string
     */
    public static String readString(Path path, long offset, long length, Charset charset) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "r")) {
            return JieIO.readString(JieIn.wrap(random, offset), charset);
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    /**
     * Using {@link RandomAccessFile} to write  bytes into given path.
     *
     * @param path given path
     * @param data given data
     */
    public static void writeBytes(Path path, InputStream data) {
        writeBytes(path, 0, -1, data);
    }

    /**
     * Using {@link RandomAccessFile} to write given length bytes into given path from offset position, the given length
     * may be set to -1 to write unlimitedly.
     *
     * @param path   given path
     * @param offset offset position
     * @param length given length, maybe -1 to write unlimitedly
     * @param data   given data
     */
    public static void writeBytes(Path path, long offset, long length, InputStream data) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "rw")) {
            OutputStream dest = JieOut.wrap(random, offset);
            JieIO.transfer(data, dest);
            dest.flush();
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    /**
     * Using {@link RandomAccessFile} to write given data into given file. The written bytes will be decoded from given
     * data with {@link JieChars#defaultCharset()}.
     *
     * @param path given path
     * @param data given data
     */
    public static void writeString(Path path, CharSequence data) {
        writeString(path, data, JieChars.defaultCharset());
    }

    /**
     * Using {@link RandomAccessFile} to write given data into given file. The written bytes will be decoded from given
     * data with given charset.
     *
     * @param path    given path
     * @param data    given data
     * @param charset given charset
     */
    public static void writeString(Path path, CharSequence data, Charset charset) {
        writeString(path, 0, -1, data, charset);
    }

    /**
     * Using {@link RandomAccessFile} to write given data into given path from offset position, the given length may be
     * set to -1 to write unlimitedly. The written bytes will be decoded from given data with
     * {@link JieChars#defaultCharset()}.
     *
     * @param path   given path
     * @param offset offset position
     * @param length given length, maybe -1 to write unlimitedly
     * @param data   given data
     */
    public static void writeString(Path path, long offset, long length, CharSequence data) {
        writeString(path, offset, length, data, JieChars.defaultCharset());
    }

    /**
     * Using {@link RandomAccessFile} to write given data into given path from offset position, the given length may be
     * set to -1 to write unlimitedly. The written bytes will be decoded from given data with given charset.
     *
     * @param path    given path
     * @param offset  offset position
     * @param length  given length, maybe -1 to write unlimitedly
     * @param data    given data
     * @param charset given charset
     */
    public static void writeString(Path path, long offset, long length, CharSequence data, Charset charset) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "rw")) {
            Writer writer = JieOut.wrap(JieOut.wrap(random, offset), charset);
            writer.append(data);
            writer.flush();
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    /**
     * Returns basic file attributes of given path.
     *
     * @param path given path
     * @return basic file attributes of file of given path
     */
    public static BasicFileAttributes basicFileAttributes(Path path) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class);
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    /**
     * Returns a new {@link FileAcc} with specified file.
     *
     * @param file specified file
     * @return a new {@link FileAcc} with specified file
     */
    public static FileAcc fileAcc(File file) {
        return new FileAccImpl(file);
    }

    /**
     * Returns a new {@link FileAcc} with specified path.
     *
     * @param path specified path
     * @return a new {@link FileAcc} with specified path
     */
    public static FileAcc fileAcc(Path path) {
        return new FileAccImpl(path.toFile());
    }
}
