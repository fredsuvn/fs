package xyz.sunqian.common.base.process;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.task.TaskState;

import java.io.File;

/**
 * Static utility class for process.
 *
 * @author sunqian
 */
public class JieProcess {

    /**
     * Starts a process with the specified command and its arguments.
     *
     * @param command the specified command and its arguments
     * @throws IORuntimeException if an I/O error occurs
     */
    public static ProcessReceipt start(@Nonnull String @Nonnull ... command) throws IORuntimeException {
        return Jie.uncheck(() -> {
            Process process = Runtime.getRuntime().exec(command);
            return new ProcessReceiptImpl(process);
        }, IORuntimeException::new);
    }

    /**
     * Starts a process with the specified command and its arguments,  environment and working directory.
     *
     * @param command the specified command and its arguments
     * @param envp    array of strings, each element of which has environment variable settings in the format
     *                {@code name=value} , or {@code null} if the process uses the default environment.
     * @param dir     the working directory of the process, or {@code null} if the process uses the default working
     *                directory.
     * @throws IORuntimeException if an I/O error occurs
     */
    public static ProcessReceipt start(
        @Nonnull String @Nonnull @RetainedParam [] command,
        @Nonnull String @Nullable [] envp,
        @Nullable File dir
    ) throws IORuntimeException {
        return Jie.uncheck(() -> {
            Process process = Runtime.getRuntime().exec(command, envp, dir);
            return new ProcessReceiptImpl(process);
        }, IORuntimeException::new);
    }

    static final class ProcessReceiptImpl implements ProcessReceipt {

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
}
