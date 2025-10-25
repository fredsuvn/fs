package space.sunqian.common.base.process;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.Kit;
import space.sunqian.common.io.IORuntimeException;

/**
 * Utilities for process.
 *
 * @author sunqian
 */
public class ProcessKit {

    /**
     * Starts a new process with the specified command, returns the process.
     *
     * @param command the specified command
     * @return the process
     * @throws IORuntimeException if any error occurs
     */
    public static @Nonnull Process start(@Nonnull String command) throws IORuntimeException {
        return Kit.uncheck(() -> Runtime.getRuntime().exec(command), IORuntimeException::new);
    }

    /**
     * Starts a new process with the specified command and arguments, returns the process.
     *
     * @param command the specified command and arguments
     * @return the process
     * @throws IORuntimeException if any error occurs
     */
    public static @Nonnull Process start(@Nonnull String @Nonnull ... command) throws IORuntimeException {
        return Kit.uncheck(() -> Runtime.getRuntime().exec(command), IORuntimeException::new);
    }
}
