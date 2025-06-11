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
     *
     * @return a virtual process
     */
    public static Process virtualProcess() {
        return new VirtualProcess();
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

        @Override
        public OutputStream getOutputStream() {
            return JieIO.nullOutStream();
        }

        @Override
        public InputStream getInputStream() {
            return JieIO.emptyInStream();
        }

        @Override
        public InputStream getErrorStream() {
            return JieIO.emptyInStream();
        }

        @Override
        public int waitFor() {
            Jie.until(() -> destroyed);
            return 0;
        }

        @Override
        public int exitValue() throws IllegalThreadStateException {
            if (!destroyed) {
                throw new IllegalThreadStateException();
            }
            return 0;
        }

        @Override
        public void destroy() {
            destroyed = true;
        }
    }
}
