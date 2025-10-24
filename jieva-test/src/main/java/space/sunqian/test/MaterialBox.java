package space.sunqian.test;

import space.sunqian.annotations.Nonnull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.file.Path;

/**
 * This class provides methods for generating material data.
 *
 * @author sunqian
 */
public class MaterialBox {

    /**
     * Returns a buffer backed by the middle of an extended array. The extended array is longer than and copied from the
     * given array, with the copied data positioned in the middle (not occupying the start or end of the extended
     * array). The returned buffer contains the portion of the extended array that holds the copied data. The buffer's
     * position is 0, and its limit and capacity is length of the given array.
     *
     * @param array the given array
     * @return a buffer backed by the middle of an extended array
     */
    public static @Nonnull ByteBuffer copyPadding(byte @Nonnull [] array) {
        byte[] back = new byte[array.length + 20];
        System.arraycopy(array, 0, back, 10, array.length);
        return ByteBuffer.wrap(back, 10, array.length).slice();
    }

    /**
     * Returns a direct buffer whose content are copied from the given array.
     *
     * @param array the given array
     * @return a direct buffer whose content are copied from the given array
     */
    public static @Nonnull ByteBuffer copyDirect(byte @Nonnull [] array) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a heap buffer whose content are copied from the given array.
     *
     * @param array the given array
     * @return a heap buffer whose content are copied from the given array
     */
    public static @Nonnull ByteBuffer copyHeap(byte @Nonnull [] array) {
        ByteBuffer buffer = ByteBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a direct buffer whose content are copied from the given buffer.
     *
     * @param buffer the given buffer
     * @return a direct buffer whose content are copied from the given buffer
     */
    public static @Nonnull ByteBuffer copyDirect(@Nonnull ByteBuffer buffer) {
        return copyBuffer(buffer, true);
    }

    /**
     * Returns a heap buffer whose content are copied from the given buffer.
     *
     * @param buffer the given buffer
     * @return a heap buffer whose content are copied from the given buffer
     */
    public static @Nonnull ByteBuffer copyHeap(@Nonnull ByteBuffer buffer) {
        return copyBuffer(buffer, false);
    }

    /**
     * Returns a buffer whose content are copied from the given buffer. The returned buffer is direct if, and only if
     * the given buffer is direct. The position of the given buffer will not be changed.
     *
     * @param buffer the given buffer
     * @return a buffer whose content are copied from the given buffer
     */
    public static @Nonnull ByteBuffer copyBuffer(@Nonnull ByteBuffer buffer) {
        return copyBuffer(buffer, buffer.isDirect());
    }

    private static @Nonnull ByteBuffer copyBuffer(@Nonnull ByteBuffer buffer, boolean direct) {
        ByteBuffer ret = direct ? ByteBuffer.allocateDirect(buffer.remaining())
            : ByteBuffer.allocate(buffer.remaining());
        int pos = buffer.position();
        ret.put(buffer);
        buffer.position(pos);
        ret.flip();
        return ret;
    }

    /**
     * Returns a new array whose content are copied from the given buffer. The position of the given buffer will not be
     * changed.
     *
     * @param buffer the given buffer
     * @return a new array whose content are copied from the given buffer
     */
    public static byte @Nonnull [] copyBytes(@Nonnull ByteBuffer buffer) {
        byte[] array = new byte[buffer.remaining()];
        int pos = buffer.position();
        buffer.get(array);
        buffer.position(pos);
        return array;
    }

    /**
     * Returns a buffer backed by the middle of an extended array. The extended array is longer than and copied from the
     * given array, with the copied data positioned in the middle (not occupying the start or end of the extended
     * array). The returned buffer contains the portion of the extended array that holds the copied data. The buffer's
     * position is 0, and its limit and capacity is length of the given array.
     *
     * @param array the given array
     * @return a buffer backed by the middle of an extended array
     */
    public static @Nonnull CharBuffer copyPadding(char @Nonnull [] array) {
        char[] back = new char[array.length + 20];
        System.arraycopy(array, 0, back, 10, array.length);
        return CharBuffer.wrap(back, 10, array.length).slice();
    }

    /**
     * Returns a direct buffer whose content are copied from the given array.
     *
     * @param array the given array
     * @return a direct buffer whose content are copied from the given array
     */
    public static @Nonnull CharBuffer copyDirect(char @Nonnull [] array) {
        CharBuffer buffer =
            ByteBuffer.allocateDirect(array.length * 2).order(ByteOrder.BIG_ENDIAN).asCharBuffer();
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a heap buffer whose content are copied from the given array.
     *
     * @param array the given array
     * @return a heap buffer whose content are copied from the given array
     */
    public static @Nonnull CharBuffer copyHeap(char @Nonnull [] array) {
        CharBuffer buffer = CharBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a direct buffer whose content are copied from the given buffer.
     *
     * @param buffer the given buffer
     * @return a direct buffer whose content are copied from the given buffer
     */
    public static @Nonnull CharBuffer copyDirect(@Nonnull CharBuffer buffer) {
        return copyBuffer(buffer, true);
    }

    /**
     * Returns a heap buffer whose content are copied from the given buffer.
     *
     * @param buffer the given buffer
     * @return a heap buffer whose content are copied from the given buffer
     */
    public static @Nonnull CharBuffer copyHeap(@Nonnull CharBuffer buffer) {
        return copyBuffer(buffer, false);
    }

    /**
     * Returns a buffer whose content are copied from the given buffer. The returned buffer is direct if, and only if
     * the given buffer is direct. The position of the given buffer will not be changed.
     *
     * @param buffer the given buffer
     * @return a buffer whose content are copied from the given buffer
     */
    public static @Nonnull CharBuffer copyBuffer(@Nonnull CharBuffer buffer) {
        return copyBuffer(buffer, buffer.isDirect());
    }

    private static @Nonnull CharBuffer copyBuffer(@Nonnull CharBuffer buffer, boolean direct) {
        if (direct) {
            char[] chars = copyChars(buffer);
            ByteBuffer bb = ByteBuffer.allocateDirect(chars.length * 2);
            CharBuffer ret = bb.asCharBuffer();
            ret.put(chars);
            ret.flip();
            return ret;
        }
        CharBuffer ret = CharBuffer.allocate(buffer.remaining());
        int pos = buffer.position();
        ret.put(buffer);
        buffer.position(pos);
        ret.flip();
        return ret;
    }

    /**
     * Returns a new array whose content are copied from the given buffer. The position of the given buffer will not be
     * changed.
     *
     * @param buffer the given buffer
     * @return a new array whose content are copied from the given buffer
     */
    public static char @Nonnull [] copyChars(@Nonnull CharBuffer buffer) {
        char[] array = new char[buffer.remaining()];
        int pos = buffer.position();
        buffer.get(array);
        buffer.position(pos);
        return array;
    }

    /**
     * Creates a new file with the specified path and data.
     *
     * @param path the specified path
     * @param data the specified data
     */
    public static void newFile(@Nonnull Path path, byte @Nonnull [] data) {
        try {
            File file = path.toFile();
            if (file.createNewFile()) {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data);
                outputStream.close();
            } else {
                throw new IOException("File is existed.");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
