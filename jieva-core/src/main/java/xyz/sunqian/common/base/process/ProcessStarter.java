package xyz.sunqian.common.base.process;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IORuntimeException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This interface represents the starter to start a sub-process of the current process. It becomes invalid after the
 * {@link #start()} is invoked.
 *
 * @author sunqian
 */
public abstract class ProcessStarter {

    /**
     * Returns a new {@link ProcessStarter} with the specified command and its arguments.
     *
     * @param command the specified command and its arguments
     * @return a new {@link ProcessStarter} with the specified command and its arguments
     */
    public static ProcessStarter from(@Nonnull String @Nonnull ... command) {
        return new Impl(command);
    }

    private final @Nonnull String @Nonnull [] command;
    private @Nullable File processInput;
    private @Nullable File processOutput;
    private @Nullable File errorOutput;
    private boolean mergeErrorOutput;
    private @Nullable File directory;
    private @Nullable Consumer<Map<String, String>> envConfig;

    /**
     * Constructs with the specified command and its arguments.
     *
     * @param command the specified command and its arguments
     */
    protected ProcessStarter(@Nonnull String @Nonnull [] command) {
        this.command = command;
    }

    /**
     * Sets the process standard input to the specified file.
     *
     * @param file the specified file
     * @return this
     */
    public ProcessStarter processInput(@Nonnull File file) {
        this.processInput = file;
        return this;
    }

    /**
     * Sets the process standard output to the specified file.
     *
     * @param file the specified file
     * @return this
     */
    public ProcessStarter processOutput(@Nonnull File file) {
        this.processOutput = file;
        return this;
    }

    /**
     * Sets the process standard error output to the specified file.
     *
     * @param file the specified file
     * @return this
     */
    public ProcessStarter errorOutput(@Nonnull File file) {
        this.errorOutput = file;
        return this;
    }

    /**
     * Sets whether merge the error output into the output, then the {@link ProcessReceipt#getInputStream()} can get
     * both process output and error output.
     *
     * @return this
     */
    public ProcessStarter mergeErrorOutput() {
        this.mergeErrorOutput = true;
        return this;
    }

    /**
     * Sets the working directory of the process.
     *
     * @param directory the working directory of the process
     * @return this
     */
    public ProcessStarter directory(@Nonnull File directory) {
        this.directory = directory;
        return this;
    }

    /**
     * Sets the environment configuration of the process. When the process is built, a modifiable {@link Map} contains
     * the default environment variables will be passed to the environment configuration, and the
     * {@link Consumer#accept(Object)} will be invoked to set the configured environment variables.
     *
     * @param envConfig the environment configuration of the process
     * @return this
     */
    public ProcessStarter environment(
        @Nonnull Consumer<@Nonnull Map<@Nonnull String, @Nonnull String>> envConfig
    ) {
        this.envConfig = envConfig;
        return this;
    }

    /**
     * Builds and starts a new process, returns the receipt of the process.
     *
     * @return the receipt of the started process
     * @throws IORuntimeException if any I/O error occurs
     */
    public ProcessReceipt start() throws IORuntimeException {
        return Jie.uncheck(() -> {
            Process process = buildProcess();
            return JieProcess.receipt(process);
        }, IORuntimeException::new);
    }

    private Process buildProcess() throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        if (directory != null) {
            builder.directory(directory);
        }
        if (envConfig != null) {
            envConfig.accept(builder.environment());
        }
        if (processInput != null) {
            builder.redirectInput(processInput);
        }
        if (processOutput != null) {
            builder.redirectOutput(processOutput);
        }
        if (errorOutput != null) {
            builder.redirectError(errorOutput);
        }
        if (mergeErrorOutput) {
            builder.redirectErrorStream(true);
        }
        return builder.start();
    }

    private static final class Impl extends ProcessStarter {
        Impl(@Nonnull String @Nonnull [] command) {
            super(command);
        }
    }
}
