package xyz.sunqian.common.base.process;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.task.TaskReceipt;
import xyz.sunqian.common.task.TaskState;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This interface is the implementation of {@link TaskReceipt}, represents the receipt for a submitted {@link Process},
 * to track the status of the process.
 *
 * @author sunqian
 */
public interface ProcessReceipt extends TaskReceipt<Integer> {

    /**
     * Returns the process of this receipt.
     *
     * @return the process of this receipt
     */
    @Nonnull
    Process getProcess();

    /**
     * Returns the state of the process:
     * <ul>
     *     <li>
     *         if the process is still alive, returns {@link TaskState#EXECUTING};
     *     </li>
     *     <li>
     *         if the process is canceled by {@link #cancel()}/{@link #cancel(boolean)} while it is running, returns
     *         {@link TaskState#CANCELED_EXECUTING};
     *     </li>
     *     <li>
     *         if the process is exited and the exit value is {@code 0}, returns {@link TaskState#SUCCEEDED};
     *     </li>
     *     <li>
     *         otherwise returns {@link TaskState#FAILED}.
     *     </li>
     * </ul>
     *
     * @return the state of the process
     */
    @Override
    @Nonnull
    TaskState getState();

    /**
     * Blocks the current thread until the process is terminated, returns the exit value of the process.
     *
     * @return the exit value of the process
     * @throws AwaitingException if the current thread is interrupted or an error occurs while awaiting
     */
    @Override
    default @Nonnull Integer getResult() throws AwaitingException {
        Process process = getProcess();
        Jie.uncheck(() -> process.waitFor(), AwaitingException::new);
        return process.exitValue();
    }

    /**
     * Blocks the current thread until the process is terminated, or the specified waiting time elapses. Returns the
     * exit value of the process.
     *
     * @param millis the maximum milliseconds to wait
     * @return the exit value of the process
     * @throws AwaitingException if the current thread is interrupted, or the specified waiting time elapses, or other
     *                           error occurs while awaiting
     */
    @Override
    default @Nonnull Integer getResult(long millis) throws AwaitingException {
        Process process = getProcess();
        boolean exited = Jie.uncheck(
            () -> process.waitFor(millis, TimeUnit.MILLISECONDS),
            AwaitingException::new
        );
        if (exited) {
            return process.exitValue();
        }
        throw new AwaitingException(new TimeoutException());
    }

    /**
     * Blocks the current thread until the process is terminated, or the specified waiting time elapses. Returns the
     * exit value of the process.
     *
     * @param duration the maximum time to wait
     * @return the exit value of the process
     * @throws AwaitingException if the current thread is interrupted, or the specified waiting time elapses, or other
     *                           error occurs while awaiting
     */
    @Override
    default @Nonnull Integer getResult(@Nonnull Duration duration) throws AwaitingException {
        Process process = getProcess();
        boolean exited = Jie.uncheck(
            () -> process.waitFor(duration.toNanos(), TimeUnit.NANOSECONDS),
            AwaitingException::new
        );
        if (exited) {
            return process.exitValue();
        }
        throw new AwaitingException(new TimeoutException());
    }

    /**
     * Cancels and destroys the process forcibly (equivalent to {@code cancel(true)}). This method returns immediately
     * without waiting for the process to exit. If necessary, use {@link #getResult()}/{@link #getResult(Duration)}
     * after invoking this method to ensure the termination of the process.
     * <p>
     * This method always returns {@code true}.
     *
     * @return {@code true}
     */
    @Override
    default boolean cancel() {
        return cancel(true);
    }

    /**
     * Cancels and destroys the process. This method returns immediately without waiting for the process to exit. If
     * necessary, use {@link #getResult()}/{@link #getResult(Duration)} after invoking this method to ensure the
     * termination of the process. The {@code forcibly} specifies whether destroys forcibly. If the {@code forcibly} is
     * {@code false}, this method does not guarantee successful termination of the process.
     * <p>
     * This method always returns {@code true}.
     *
     * @param forcibly to specifies whether destroys forcibly
     * @return {@code true}
     */
    @Override
    boolean cancel(boolean forcibly);

    /**
     * Returns {@code true} if the process is terminated after the invocation of
     * {@link #cancel()}/{@link #cancel(boolean)}, else {@code false}.
     *
     * @return {@code true} f the process is terminated after the invocation of
     * {@link #cancel()}/{@link #cancel(boolean)}, else {@code false}
     */
    @Override
    boolean isCancelled();

    /**
     * Returns {@code true} if the process is terminated.
     *
     * @return {@code true} if the process is terminated
     */
    @Override
    default boolean isDone() {
        return !getProcess().isAlive();
    }

    /**
     * Returns the exception thrown by the process, if any.
     *
     * @return the exception thrown by the process, if any
     */
    @Override
    default @Nullable Throwable getException() {
        return null;
    }

    /**
     * Returns the remaining delay of the process execution, or null if the process is not delayed.
     *
     * @return the remaining delay of the process execution, or null if the process is not delayed
     */
    @Override
    default @Nullable Duration getDelay() {
        return null;
    }

    /**
     * Returns the output stream of the process. This method is equivalent to the {@link Process#getOutputStream()}.
     *
     * @return the output stream of the process
     */
    default @Nonnull OutputStream getOutputStream() {
        return getProcess().getOutputStream();
    }

    /**
     * Returns the input stream of the process. This method is equivalent to the {@link Process#getInputStream()}.
     *
     * @return the input stream of the process
     */
    default @Nonnull InputStream getInputStream() {
        return getProcess().getInputStream();
    }

    /**
     * Returns the error stream of the process. This method is equivalent to the {@link Process#getErrorStream()}.
     *
     * @return the error stream of the process
     */
    default @Nonnull InputStream getErrorStream() {
        return getProcess().getErrorStream();
    }

    /**
     * Blocks the current thread until the process is terminated, returns the byte array read from the
     * {@link #getInputStream()}.
     *
     * @return the byte array read from the {@link #getInputStream()}
     * @throws AwaitingException if the current thread is interrupted or an error occurs while reading
     */
    default byte @Nonnull [] readBytes() throws AwaitingException {
        return Jie.uncheck(() -> IOKit.read(getInputStream()), AwaitingException::new);
    }

    /**
     * Blocks the current thread until the process is terminated, returns the string read from the
     * {@link #getInputStream()}, with the {@link CharsKit#localCharset()}.
     *
     * @return the string read from the {@link #getInputStream()}
     * @throws AwaitingException if the current thread is interrupted or an error occurs while reading
     */
    default @Nonnull String readString() throws AwaitingException {
        return readString(CharsKit.localCharset());
    }

    /**
     * Blocks the current thread until the process is terminated, returns the string read from the
     * {@link #getInputStream()}.
     *
     * @param charset the specified charset
     * @return the string read from the {@link #getInputStream()}
     * @throws AwaitingException if the current thread is interrupted or an error occurs while reading
     */
    default @Nonnull String readString(Charset charset) throws AwaitingException {
        return Jie.uncheck(() -> IOKit.string(getInputStream(), charset), AwaitingException::new);
    }
}
