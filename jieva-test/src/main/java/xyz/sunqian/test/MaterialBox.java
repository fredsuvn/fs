package xyz.sunqian.test;

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
    public static ByteBuffer copyPadding(byte[] array) {
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
    public static ByteBuffer copyDirect(byte[] array) {
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
    public static ByteBuffer copyHeap(byte[] array) {
        ByteBuffer buffer = ByteBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a buffer whose content are copied from the given buffer. The returned buffer is direct if, and only if
     * the given buffer is direct. The position of the given buffer will not be changed.
     *
     * @param buffer the given buffer
     * @return a buffer whose content are copied from the given buffer
     */
    public static ByteBuffer copyBuffer(ByteBuffer buffer) {
        ByteBuffer ret = buffer.isDirect() ? ByteBuffer.allocateDirect(buffer.remaining())
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
    public static byte[] copyBytes(ByteBuffer buffer) {
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
    public static CharBuffer copyPadding(char[] array) {
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
    public static CharBuffer copyDirect(char[] array) {
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
    public static CharBuffer copyHeap(char[] array) {
        CharBuffer buffer = CharBuffer.allocate(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a buffer whose content are copied from the given buffer. The returned buffer is direct if, and only if
     * the given buffer is direct. The position of the given buffer will not be changed.
     *
     * @param buffer the given buffer
     * @return a buffer whose content are copied from the given buffer
     */
    public static CharBuffer copyBuffer(CharBuffer buffer) {
        if (buffer.isDirect()) {
            char[] chars = copyBytes(buffer);
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
    public static char[] copyBytes(CharBuffer buffer) {
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
    public static void newFile(Path path, byte[] data) {
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
