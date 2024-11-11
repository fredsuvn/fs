package xyz.sunqian.common.file;

import xyz.sunqian.common.io.JieIn;
import xyz.sunqian.common.io.JieOut;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

final class FileAccImpl implements FileAcc {

    private final File file;
    private volatile RandomAccessFile raf;
    private volatile boolean hasStream = false;
    private volatile boolean close = false;

    FileAccImpl(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public Path getPath() {
        return file.toPath();
    }

    @Override
    public synchronized void open(String mode) throws FileException {
        if (raf != null) {
            throw new FileException("FileAcc has been opened.");
        }
        try {
            raf = new RandomAccessFile(file, mode);
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    @Override
    public synchronized void setLength(long newLength) throws FileException {
        checkOpened();
        try {
            raf.setLength(newLength);
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    @Override
    public synchronized FileChannel getChannel() throws FileException {
        checkOpened();
        try {
            return raf.getChannel();
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    @Override
    public synchronized FileDescriptor getDescriptor() throws FileException {
        checkOpened();
        try {
            return raf.getFD();
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    @Override
    public synchronized InputStream inputStream(long position) throws FileException {
        checkOpened();
        checkStatus();
        hasStream = true;
        InputStream ris = JieIn.wrap(raf, position);
        return new BindedInputStream(ris);
    }

    @Override
    public synchronized OutputStream outputStream(long position, boolean autoExtend) throws FileException {
        checkOpened();
        checkStatus();
        hasStream = true;
        OutputStream ros = JieOut.wrap(raf, position);
        return new BindedOutputStream(ros);
    }

    @Override
    public synchronized void close() throws FileException {
        try {
            close = true;
            if (raf != null) {
                raf.close();
            }
        } catch (Exception e) {
            throw new FileException(e);
        }
    }

    @Override
    public String toString() {
        return "FileAcc[" + getPath().toAbsolutePath() + "]";
    }

    private void checkOpened() {
        if (raf == null) {
            throw new FileException("FileAcc has not been opened.");
        }
    }

    private void checkStatus() {
        if (close || hasStream) {
            throw new FileException("FileAcc has been closed or last stream has not been closed.");
        }
    }

    private final class BindedInputStream extends InputStream {

        private final InputStream source;
        private volatile boolean close = false;

        private BindedInputStream(InputStream source) {
            this.source = source;
        }

        @Override
        public int read() throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                return source.read();
            }
        }

        @Override
        public int read(byte[] b) throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                return source.read(b);
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                return source.read(b, off, len);
            }
        }

        @Override
        public long skip(long n) throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                return source.skip(n);
            }
        }

        @Override
        public int available() throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                return source.available();
            }
        }

        @Override
        public void close() throws IOException {
            synchronized (FileAccImpl.this) {
                close = true;
                FileAccImpl.this.hasStream = false;
            }
        }

        @Override
        public void mark(int readlimit) {
            synchronized (FileAccImpl.this) {
                checkStatus();
                source.mark(readlimit);
            }
        }

        @Override
        public void reset() throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                source.reset();
            }
        }

        @Override
        public boolean markSupported() {
            synchronized (FileAccImpl.this) {
                checkStatus();
                return source.markSupported();
            }
        }

        private void checkStatus() {
            if (close || FileAccImpl.this.close) {
                throw new FileException("FileAcc or stream has been closed.");
            }
        }
    }

    private final class BindedOutputStream extends OutputStream {

        private final OutputStream source;
        private volatile boolean close = false;

        private BindedOutputStream(OutputStream source) {
            this.source = source;
        }

        @Override
        public void write(int b) throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                source.write(b);
            }
        }

        @Override
        public void write(byte[] b) throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                source.write(b);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                source.write(b, off, len);
            }
        }

        @Override
        public void flush() throws IOException {
            synchronized (FileAccImpl.this) {
                checkStatus();
                source.flush();
            }
        }

        @Override
        public void close() throws IOException {
            synchronized (FileAccImpl.this) {
                close = true;
                FileAccImpl.this.hasStream = false;
            }
        }

        private void checkStatus() {
            if (close || FileAccImpl.this.close) {
                throw new FileException("FileAcc or stream has been closed.");
            }
        }
    }
}
