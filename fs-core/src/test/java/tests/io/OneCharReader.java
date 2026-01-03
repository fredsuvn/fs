package tests.io;

import space.sunqian.annotation.Nonnull;

import java.io.IOException;
import java.io.Reader;

public class OneCharReader extends Reader {

    private final char[] data;
    private int pos = 0;

    public OneCharReader(char[] data) {
        this.data = data;
    }

    @Override
    public int read() throws IOException {
        if (pos >= data.length) {
            return -1;
        }
        return data[pos++] & 0x0000ffff;
    }

    @Override
    public int read(char @Nonnull [] b, int off, int len) throws IOException {
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

    @Override
    public void close() throws IOException {
    }
}
