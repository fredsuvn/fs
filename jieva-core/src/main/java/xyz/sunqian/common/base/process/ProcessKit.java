package xyz.sunqian.common.base.process;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.task.TaskState;

/**
 * Static utility class for process.
 *
 * @author sunqian
 */
public class ProcessKit {

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

    private static final class ProcessReceiptImpl implements ProcessReceipt {

        private final @Nonnull Process process;
        private int canceled = 0;

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
                (canceled == 1 ?
                    TaskState.CANCELED_EXECUTING :
                    (process.exitValue() == 0 ? TaskState.SUCCEEDED : TaskState.FAILED)
                );
        }

        @Override
        public boolean cancel(boolean forcibly) {
            canceled = 1;
            if (forcibly) {
                process.destroyForcibly();
            } else {
                process.destroy();
            }
            return true;
        }

        @Override
        public boolean isCancelled() {
            return canceled == 1 && !process.isAlive();
        }
    }
}
