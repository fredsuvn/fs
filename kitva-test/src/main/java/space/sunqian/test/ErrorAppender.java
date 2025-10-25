package space.sunqian.test;

import java.io.IOException;

/**
 * An appender of which all methods throw the {@link IOException}.
 *
 * @author sunqian
 */
public class ErrorAppender implements Appendable {

    @Override
    public Appendable append(CharSequence csq) throws IOException {
        throw new IOException();
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        throw new IOException();
    }

    @Override
    public Appendable append(char c) throws IOException {
        throw new IOException();
    }
}
