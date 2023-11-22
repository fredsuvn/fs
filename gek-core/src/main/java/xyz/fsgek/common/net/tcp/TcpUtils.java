package xyz.fsgek.common.net.tcp;

import xyz.fsgek.common.collect.GekArray;
import xyz.fsgek.common.io.GekIO;

import java.nio.ByteBuffer;
import java.util.function.IntFunction;

final class TcpUtils {

    static ByteBuffer compact(ByteBuffer buffer, IntFunction<ByteBuffer> generator) {
        if (buffer.position() == 0) {
            return buffer;
        }
        if (buffer.remaining() <= 0) {
            return GekIO.emptyBuffer();
        }
        ByteBuffer newBuffer = generator.apply(buffer.remaining());
        newBuffer.put(buffer);
        newBuffer.flip();
        return newBuffer.asReadOnlyBuffer();
    }

    static ByteBuffer compact(ByteBuffer buffer, byte[] newBytes, IntFunction<ByteBuffer> generator) {
        if (buffer.remaining() <= 0) {
            if (GekArray.isEmpty(newBytes)) {
                return GekIO.emptyBuffer();
            }
            ByteBuffer newBuffer = generator.apply(newBytes.length);
            newBuffer.put(newBytes);
            newBuffer.flip();
            return newBuffer.asReadOnlyBuffer();
        }
        int newCapacity = buffer.remaining() + newBytes.length;
        ByteBuffer newBuffer = generator.apply(newCapacity);
        newBuffer.put(buffer);
        newBuffer.put(newBytes);
        newBuffer.flip();
        return newBuffer.asReadOnlyBuffer();
    }
}
