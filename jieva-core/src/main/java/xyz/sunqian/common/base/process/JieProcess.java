package xyz.sunqian.common.base.process;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.common.task.TaskState;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Static utility class for process.
 *
 * @author sunqian
 */
public class JieProcess {

    /**
     * Starts a new process with the specified command, returns the receipt of the process.
     *
     * @param command the specified command
     * @return the receipt of the process
     * @throws IORuntimeException if any I/O error occurs
     */
    public static @Nonnull ProcessReceipt start(@Nonnull String command) throws IORuntimeException {
        return Jie.uncheck(() -> receipt(Runtime.getRuntime().exec(command)), IORuntimeException::new);
    }

    /**
     * Starts a new process with the specified command and arguments, returns the receipt of the process.
     *
     * @param command the specified command and arguments
     * @return the receipt of the process
     * @throws IORuntimeException if any I/O error occurs
     */
    public static @Nonnull ProcessReceipt start(@Nonnull String @Nonnull ... command) throws IORuntimeException {
        return Jie.uncheck(() -> receipt(Runtime.getRuntime().exec(command)), IORuntimeException::new);
    }

    /**
     * Wraps the given process to a new {@link ProcessReceipt}.
     *
     * @param process the given process
     * @return a new {@link ProcessReceipt} wraps the given process
     */
    public static @Nonnull ProcessReceipt receipt(Process process) {
        return new ProcessReceiptImpl(process);
    }

    /**
     * Returns a virtual process.
     * <p>
     * For the returned process: {@link Process#destroy()} terminates itself normally, but the
     * {@link Process#destroyForcibly()} occurs an abnormal termination; {@link Process#getInputStream()},
     * {@link Process#getErrorStream()} and {@link Process#getOutputStream()} are {@link JieIO#emptyInStream()},
     * {@link JieIO#emptyInStream()} and {@link JieIO#nullOutStream()}.
     *
     * @return a virtual process
     */
    public static @Nonnull Process virtualProcess() {
        return virtualProcess(JieIO.emptyInStream(), JieIO.nullOutStream());
    }

    /**
     * Returns a virtual process.
     * <p>
     * For the returned process: {@link Process#destroy()} terminates itself normally, but the
     * {@link Process#destroyForcibly()} occurs an abnormal termination; {@link Process#getInputStream()} and
     * {@link Process#getErrorStream()} are merged into the specified input stream, {@link Process#getOutputStream()} is
     * the specified output stream.
     *
     * @param input  the specified input stream for {@link Process#getInputStream()} and
     *               {@link Process#getErrorStream()}
     * @param output the specified output stream for {@link Process#getOutputStream()}
     * @return a virtual process
     */
    public static @Nonnull Process virtualProcess(
        @Nonnull InputStream input,
        @Nonnull OutputStream output
    ) {
        return virtualProcess(input, input, output);
    }

    /**
     * Returns a virtual process.
     * <p>
     * For the returned process: {@link Process#destroy()} terminates itself normally, but the
     * {@link Process#destroyForcibly()} occurs an abnormal termination; {@link Process#getInputStream()} is the
     * specified input stream, {@link Process#getErrorStream()} is the specified error stream,
     * {@link Process#getOutputStream()} is the specified output stream.
     *
     * @param input  the specified input stream for {@link Process#getInputStream()}
     * @param error  the specified error stream for {@link Process#getErrorStream()}
     * @param output the specified output stream for {@link Process#getOutputStream()}
     * @return a virtual process
     */
    public static @Nonnull Process virtualProcess(
        @Nonnull InputStream input,
        @Nonnull InputStream error,
        @Nonnull OutputStream output
    ) {
        return new VirtualProcess(input, error, output);
    }

    private static final class ProcessReceiptImpl implements ProcessReceipt {

        private final @Nonnull Process process;
        private int cancal = 0;

        ProcessReceiptImpl(@Nonnull Process process) {
            this.process = process;
        }

        @Override
        public @Nonnull Process getProcess() {
            return process;
        }

        @Override
        public @Nonnull TaskState getState() {
            return process.isAlive() ? TaskState.EXECUTING :
                (cancal == 1 ?
                    TaskState.CANCELED_EXECUTING :
                    (process.exitValue() == 0 ? TaskState.SUCCEEDED : TaskState.FAILED)
                );
        }

        @Override
        public boolean cancel(boolean forcibly) {
            cancal = 1;
            if (forcibly) {
                process.destroyForcibly();
            } else {
                process.destroy();
            }
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancal == 1 && !process.isAlive();
        }
    }

    private static final class VirtualProcess extends Process {

        private volatile boolean destroyed = false;
        private volatile boolean normal = true;
        private final @Nonnull InputStream input;
        private final @Nonnull InputStream error;
        private final @Nonnull OutputStream output;

        private VirtualProcess(
            @Nonnull InputStream input,
            @Nonnull InputStream error,
            @Nonnull OutputStream output
        ) {
            this.input = input;
            this.error = error;
            this.output = output;
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
            Jie.until(() -> destroyed);
            return exitValue();
        }

        @Override
        public int exitValue() throws IllegalThreadStateException {
            if (!destroyed) {
                throw new IllegalThreadStateException();
            }
            return normal ? 0 : 1;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }

        @Override
        public @Nonnull Process destroyForcibly() {
            normal = false;
            destroy();
            return this;
        }
    }
}
