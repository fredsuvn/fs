package xyz.sunqian.test;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An output stream of which all methods throw the {@link IOException}.
 *
 * @author sunqian
 */
public class ErrorOutputStream extends OutputStream {

    @Override
    public void write(int b) throws IOException {
        throw new IOException();
    }

    @Override
    public void flush() throws IOException {
        throw new IOException();
    }

    @Override
    public void close() throws IOException {
        throw new IOException();
    }
}
