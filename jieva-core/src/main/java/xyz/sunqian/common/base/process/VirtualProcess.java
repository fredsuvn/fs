package xyz.sunqian.common.base.process;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IOKit;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A virtual process used for simulation. It has 3 flags:
 * <ul>
 *     <li>{@code alive}: specifies whether this process is alive, initial is {@code true};</li>
 *     <li>
 *         {@code normal}: specifies whether this process ends normally, the {@link #exitValue()} returns {@code 0} if
 *         normally, otherwise {@code 1}; initial is {@code true};
 *     </li>
 * </ul>
 *
 * @author sunqian
 */
public class VirtualProcess extends Process {

    private volatile boolean alive = true;
    private volatile boolean normal = true;

    private final @Nonnull InputStream input;
    private final @Nonnull InputStream error;
    private final @Nonnull OutputStream output;

    /**
     * Constructs a new virtual process. The result of {@link #getInputStream()} and {@link #getErrorStream()} are
     * {@link IOKit#emptyInputStream()}, and the result of {@link #getOutputStream()} is
     * {@link IOKit#nullOutputStream()}.
     */
    public VirtualProcess() {
        this(IOKit.emptyInputStream(), IOKit.nullOutputStream());
    }

    /**
     * Constructs with the specified input stream and output stream. These streams are the result of
     * {@link #getInputStream()} and {@link #getOutputStream()}. The {@link #getErrorStream()} will be mered into the
     * {@link #getInputStream()}.
     *
     * @param input  the specified input and error stream
     * @param output the specified output stream
     */
    public VirtualProcess(
        @Nonnull InputStream input,
        @Nonnull OutputStream output
    ) {
        this(input, input, output);
    }

    /**
     * Constructs with the specified input stream, error stream and output stream. These streams are the result of
     * {@link #getInputStream()}, {@link #getErrorStream()} and {@link #getOutputStream()}.
     *
     * @param input  the specified input stream
     * @param error  the specified error stream
     * @param output the specified output stream
     */
    public VirtualProcess(
        @Nonnull InputStream input,
        @Nonnull InputStream error,
        @Nonnull OutputStream output
    ) {
        this.input = input;
        this.error = error;
        this.output = output;
    }

    /**
     * Sets the value of alive flag.
     *
     * @param alive the value of alive flag
     * @return this
     */
    public VirtualProcess alive(boolean alive) {
        this.alive = alive;
        return this;
    }

    /**
     * Sets the value of normal flag.
     *
     * @param normal the value of normal flag
     * @return this
     */
    public VirtualProcess normal(boolean normal) {
        this.normal = normal;
        return this;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public @Nonnull OutputStream getOutputStream() {
        return output;
    }

    @Override
    public @Nonnull InputStream getInputStream() {
        return input;
    }

    @Override
    public @Nonnull InputStream getErrorStream() {
        return error;
    }

    @Override
    public int waitFor() {
        Jie.until(() -> !alive);
        return exitValue();
    }

    @Override
    public int exitValue() throws IllegalThreadStateException {
        if (alive) {
            throw new IllegalThreadStateException();
        }
        return normal ? 0 : 1;
    }

    @Override
    public void destroy() {
        alive = false;
    }

    @Override
    public @Nonnull Process destroyForcibly() {
        destroy();
        return this;
    }
}