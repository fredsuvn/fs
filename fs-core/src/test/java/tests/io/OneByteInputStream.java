package tests.io;

import space.sunqian.annotations.Nonnull;

import java.io.IOException;
import java.io.InputStream;

public class OneByteInputStream extends InputStream {

    private final byte[] data;
    private int pos = 0;

    public OneByteInputStream(byte[] data) {
        this.data = data;
    }

    @Override
    public int read() throws IOException {
        if (pos >= data.length) {
            return -1;
        }
        return data[pos++] & 0x000000ff;
    }

    @Override
    public int read(byte @Nonnull [] b, int off, int len) throws IOException {
        if (pos >= data.length) {
            return -1;
        }
        b[off] = data[pos++];
        return 1;
    }

    @Override
    public long skip(long n) {
        if (n <= 0) {
            return 0;
        }
        if (pos >= data.length) {
            return 0;
        }
        pos++;
        return 1;
    }
}
