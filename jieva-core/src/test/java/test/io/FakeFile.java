package test.io;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

final class FakeFile extends RandomAccessFile {

    private final byte[] data;
    private final OutputStream out;
    private boolean closed = false;
    private ByteArrayInputStream in;

    public FakeFile(byte[] data) throws FileNotFoundException {
        super(ClassLoader.getSystemResource("io/fakeRaf.txt").getFile(), "r");
        this.data = data;
        in = new ByteArrayInputStream(data);
        this.out = null;
    }

    public FakeFile(OutputStream out) throws FileNotFoundException {
        super(ClassLoader.getSystemResource("io/fakeRaf.txt").getFile(), "r");
        this.data = null;
        in = null;
        this.out = out;
    }

    @Override
    public int read() throws IOException {
        if (closed) {
            throw new IOException();
        }
        return in.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (closed) {
            throw new IOException();
        }
        return in.read(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        if (closed) {
            throw new IOException();
        }
        return (int) in.skip(n);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (closed) {
            throw new IOException();
        }
        out.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        if (closed) {
            throw new IOException();
        }
        out.write(b);
    }

    @Override
    public long getFilePointer() throws IOException {
        if (closed) {
            throw new IOException();
        }
        return data.length - in.available();
    }

    @Override
    public void seek(long pos) throws IOException {
        if (closed) {
            throw new IOException();
        }
        if (data != null) {
            in = new ByteArrayInputStream(data, (int) pos, data.length - (int) pos);
        }
    }

    @Override
    public long length() throws IOException {
        return data.length;
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.closed = true;
    }
}
