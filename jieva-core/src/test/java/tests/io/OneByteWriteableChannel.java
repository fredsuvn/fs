package tests.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class OneByteWriteableChannel implements WritableByteChannel {

    private final OutputStream out;

    public OneByteWriteableChannel(OutputStream out) {
        this.out = out;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        if (!src.hasRemaining()) {
            return 0;
        }
        byte b = src.get();
        out.write(b);
        return 1;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {
    }
}
