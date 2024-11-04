package xyz.sunqian.common.base;

import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.common.io.JieInput;
import xyz.sunqian.common.io.JieOutput;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This interface represents starter to build and start a {@link Process}.
 *
 * @author fredsuvn
 */
public abstract class ProcessStarter implements BaseStarter<Process, ProcessStarter> {

    static ProcessStarter newInstance() {
        return new OfJdk8();
    }

    private List<String> command;
    private Object input;
    private Object output;
    private Object errorOutput;
    private File directory;
    private Consumer<Map<String, String>> envConfigurator;

    ProcessStarter() {
        reset();
    }

    /**
     * Sets command. This method will split given command with space chars:
     * <pre>
     *     command.split(" +")
     * </pre>
     *
     * @param command command
     * @return this
     */
    public ProcessStarter command(String command) {
        return command(Jie.list(command.split(" +")));
    }

    /**
     * Sets command.
     *
     * @param command command
     * @return this
     */
    public ProcessStarter command(String... command) {
        return command(Jie.list(command));
    }

    /**
     * Sets command.
     *
     * @param command command
     * @return this
     */
    public ProcessStarter command(List<String> command) {
        this.command = command;
        return this;
    }

    /**
     * Sets input to given array.
     *
     * @param array given array
     * @return this
     */
    public ProcessStarter input(byte[] array) {
        this.input = array;
        return this;
    }

    /**
     * Sets input to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    public ProcessStarter input(ByteBuffer buffer) {
        this.input = buffer;
        return this;
    }

    /**
     * Sets input to given input stream.
     *
     * @param in given input stream
     * @return this
     */
    public ProcessStarter input(InputStream in) {
        this.input = in;
        return this;
    }

    /**
     * Sets input to given file.
     *
     * @param file given file
     * @return this
     */
    public ProcessStarter input(File file) {
        this.input = file;
        return this;
    }

    /**
     * Sets input to given file.
     *
     * @param file given file
     * @return this
     */
    public ProcessStarter input(Path file) {
        this.input = file;
        return this;
    }

    /**
     * Sets input to given redirect.
     *
     * @param redirect given redirect
     * @return this
     */
    public ProcessStarter input(ProcessBuilder.Redirect redirect) {
        this.input = redirect;
        return this;
    }

    /**
     * Sets output to given array.
     *
     * @param array given array
     * @return this
     */
    public ProcessStarter output(byte[] array) {
        this.output = array;
        return this;
    }

    /**
     * Sets output to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    public ProcessStarter output(ByteBuffer buffer) {
        this.output = buffer;
        return this;
    }

    /**
     * Sets output to given out put stream.
     *
     * @param out given out put stream
     * @return this
     */
    public ProcessStarter output(OutputStream out) {
        this.output = out;
        return this;
    }

    /**
     * Sets output to given file.
     *
     * @param file given file
     * @return this
     */
    public ProcessStarter output(File file) {
        this.output = file;
        return this;
    }

    /**
     * Sets output to given file.
     *
     * @param file given file
     * @return this
     */
    public ProcessStarter output(Path file) {
        this.output = file;
        return this;
    }

    /**
     * Sets output to given redirect.
     *
     * @param redirect given redirect
     * @return this
     */
    public ProcessStarter output(ProcessBuilder.Redirect redirect) {
        this.output = redirect;
        return this;
    }

    /**
     * Sets error output to given array. The error output can be same with output, in this case both output will be
     * written into same destination.
     *
     * @param array given array
     * @return this
     */
    public ProcessStarter errorOutput(byte[] array) {
        this.errorOutput = array;
        return this;
    }

    /**
     * Sets error output to given buffer. The error output can be same with output, in this case both output will be
     * written into same destination.
     *
     * @param buffer given buffer
     * @return this
     */
    public ProcessStarter errorOutput(ByteBuffer buffer) {
        this.errorOutput = buffer;
        return this;
    }

    /**
     * Sets error output to given output stream. The error output can be same with output, in this case both output will
     * be written into same destination.
     *
     * @param out given output stream
     * @return this
     */
    public ProcessStarter errorOutput(OutputStream out) {
        this.errorOutput = out;
        return this;
    }

    /**
     * Sets error output to given file. The error output can be same with output, in this case both output will be
     * written into same destination.
     *
     * @param file given file
     * @return this
     */
    public ProcessStarter errorOutput(File file) {
        this.errorOutput = file;
        return this;
    }

    /**
     * Sets error output to given file. The error output can be same with output, in this case both output will be
     * written into same destination.
     *
     * @param file given file
     * @return this
     */
    public ProcessStarter errorOutput(Path file) {
        this.errorOutput = file;
        return this;
    }

    /**
     * Sets error output to given redirect. The error output can be same with output, in this case both output will be
     * written into same destination.
     *
     * @param redirect given redirect
     * @return this
     */
    public ProcessStarter errorOutput(ProcessBuilder.Redirect redirect) {
        this.errorOutput = redirect;
        return this;
    }

    /**
     * Sets directory where the process works.
     *
     * @param directory directory where the process works
     * @return this
     */
    public ProcessStarter directory(String directory) {
        return directory(new File(directory));
    }

    /**
     * Sets directory where the process works.
     *
     * @param directory directory where the process works
     * @return this
     */
    public ProcessStarter directory(Path directory) {
        return directory(directory.toFile());
    }

    /**
     * Sets directory where the process works.
     *
     * @param directory directory where the process works
     * @return this
     */
    public ProcessStarter directory(File directory) {
        this.directory = directory;
        return this;
    }

    /**
     * Sets environment by specified environment configurator - {@code envConfigurator}.
     * <p>
     * The {@code envConfigurator} will be passed into initial environment of current system as a {@link Map}, then the
     * {@code envConfigurator} can modify the {@link Map} before the process execution unit starts.
     *
     * @param envConfigurator specified environment configurator
     * @return this
     */
    public ProcessStarter environment(Consumer<Map<String, String>> envConfigurator) {
        this.envConfigurator = envConfigurator;
        return this;
    }

    @Override
    public ProcessStarter reset() {
        this.input = null;
        this.output = null;
        this.errorOutput = null;
        this.command = null;
        this.directory = null;
        this.envConfigurator = null;
        return this;
    }

    /**
     * Builds and starts a new {@link Process}.
     *
     * @return a new {@link Process}
     */
    @Override
    public Process start() {
        if (JieColl.isEmpty(command)) {
            throw new IllegalArgumentException("Command is empty.");
        }
        try {
            return startWithProcessBuilder();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Process startWithProcessBuilder() throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        if (directory != null) {
            builder.directory(directory);
        }
        InputStream in = null;
        if (input != null) {
            if (input instanceof ProcessBuilder.Redirect) {
                builder.redirectInput((ProcessBuilder.Redirect) input);
            } else if (input instanceof File) {
                builder.redirectInput((File) input);
            } else if (input instanceof Path) {
                builder.redirectInput(((Path) input).toFile());
            } else {
                in = inputToStream(input);
                builder.redirectInput(ProcessBuilder.Redirect.PIPE);
            }
        }
        OutputStream out = null;
        if (output != null) {
            if (output instanceof ProcessBuilder.Redirect) {
                builder.redirectOutput((ProcessBuilder.Redirect) output);
            } else if (output instanceof File) {
                builder.redirectOutput((File) output);
            } else if (output instanceof Path) {
                builder.redirectOutput(((Path) output).toFile());
            } else {
                out = outputToStream(output);
                builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            }
        }
        OutputStream err = null;
        if (errorOutput != null) {
            if (Objects.equals(errorOutput, out)) {
                builder.redirectErrorStream(true);
            }
            if (errorOutput instanceof ProcessBuilder.Redirect) {
                builder.redirectError((ProcessBuilder.Redirect) errorOutput);
            } else if (errorOutput instanceof File) {
                builder.redirectError((File) errorOutput);
            } else if (errorOutput instanceof Path) {
                builder.redirectError(((Path) errorOutput).toFile());
            } else {
                err = outputToStream(errorOutput);
                builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            }
        }
        if (envConfigurator != null) {
            envConfigurator.accept(builder.environment());
        }
        Process process = builder.start();
        if (in != null) {
            JieIO.transfer(in, process.getOutputStream());
        }
        if (out != null) {
            JieIO.transfer(process.getInputStream(), out);
        }
        if (err != null) {
            JieIO.transfer(process.getErrorStream(), out);
        }
        return process;
    }

    private static InputStream inputToStream(Object in) {
        if (in instanceof byte[]) {
            return JieInput.wrap((byte[]) in);
        }
        if (in instanceof ByteBuffer) {
            return JieInput.wrap((ByteBuffer) in);
        }
        if (in instanceof InputStream) {
            return (InputStream) in;
        }
        throw new IllegalArgumentException("Error input type: " + in.getClass());
    }

    private static OutputStream outputToStream(Object out) {
        if (out instanceof byte[]) {
            return JieOutput.wrap((byte[]) out);
        }
        if (out instanceof ByteBuffer) {
            return JieOutput.wrap((ByteBuffer) out);
        }
        if (out instanceof OutputStream) {
            return (OutputStream) out;
        }
        throw new IllegalArgumentException("Error output type: " + out.getClass());
    }

    private static final class OfJdk8 extends ProcessStarter {
    }
}
