package xyz.sunqian.common.base.process;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
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
     * Wraps the given process to a new {@link ProcessReceipt}.
     *
     * @param process the given process
     * @return a new {@link ProcessReceipt} wraps the given process
     */
    public static ProcessReceipt receipt(Process process) {
        return new ProcessReceiptImpl(process);
    }

    /**
     * Returns a virtual process.
     * <p>
     * For returned virtual process: {@link Process#destroy()} terminates itself normally, but the
     * {@link Process#destroyForcibly()} occurs an abnormal termination; {@link Process#getInputStream()},
     * {@link Process#getErrorStream()} and {@link Process#getOutputStream()} are {@link JieIO#emptyInStream()},
     * {@link JieIO#emptyInStream()} and {@link JieIO#nullOutStream()}.
     *
     * @return a virtual process
     */
    public static Process virtualProcess() {
        return virtualProcess(JieIO.emptyInStream(), JieIO.emptyInStream(), JieIO.nullOutStream());
    }

    /**
     * Returns a virtual process
     * <p>
     * For returned virtual process: {@link Process#destroy()} terminates itself normally, but the
     * {@link Process#destroyForcibly()} occurs an abnormal termination; {@link Process#getInputStream()},
     * {@link Process#getErrorStream()} and {@link Process#getOutputStream()} are specified streams.
     *
     * @param input  the input stream of {@link Process#getErrorStream()}
     * @param error  the error stream of {@link Process#getErrorStream()}
     * @param output the output stream of {@link Process#getOutputStream()}
     * @return a virtual process
     */
    public static Process virtualProcess(
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
        public OutputStream getOutputStream() {
            return output;
        }

        @Override
        public InputStream getInputStream() {
            return input;
        }

        @Override
        public InputStream getErrorStream() {
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
        public Process destroyForcibly() {
            normal = false;
            destroy();
            return this;
        }
    }
}
