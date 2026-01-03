package space.sunqian.fs.concurrent;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.exception.AwaitingException;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Utilities for {@link Future}.
 *
 * @author sunqian
 */
public class FutureKit {

    /**
     * Gets the result of the future by {@link Future#get()}.
     *
     * @param future the future object
     * @param <T>    the type of the result
     * @return the result of the future object
     * @throws AwaitingException to wrap the exception thrown by {@link Future#get()}
     */
    public static <T> T get(@Nonnull Future<T> future) throws AwaitingException {
        try {
            return future.get();
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    /**
     * Gets the result of the future by {@link Future#get(long, TimeUnit)}.
     *
     * @param future the future object
     * @param millis the maximum milliseconds to wait
     * @param <T>    the type of the result
     * @return the result of the future object
     * @throws AwaitingException to wrap the exception thrown by {@link Future#get(long, TimeUnit)}
     */
    public static <T> T get(@Nonnull Future<T> future, long millis) throws AwaitingException {
        try {
            return future.get(millis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    /**
     * Gets the result of the future by {@link Future#get(long, TimeUnit)}.
     *
     * @param future  the future object
     * @param timeout the maximum time to wait
     * @param <T>     the type of the result
     * @return the result of the future object
     * @throws AwaitingException to wrap the exception thrown by {@link Future#get(long, TimeUnit)}
     */
    public static <T> T get(@Nonnull Future<T> future, @Nonnull Duration timeout) throws AwaitingException {
        try {
            return future.get(timeout.toNanos(), TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    /**
     * Gets the result of the future by {@link Future#get()}, and if the operation fails, returns the default value.
     *
     * @param future       the future object
     * @param defaultValue the default value
     * @param <T>          the type of the result
     * @return the result of the future object
     */
    public static <T> T get(@Nonnull Future<T> future, T defaultValue) {
        try {
            return future.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gets the result of the future by {@link Future#get(long, TimeUnit)}, and if the operation fails, returns the
     * default value.
     *
     * @param future       the future object
     * @param millis       the maximum milliseconds to wait
     * @param defaultValue the default value
     * @param <T>          the type of the result
     * @return the result of the future object
     */
    public static <T> T get(@Nonnull Future<T> future, long millis, T defaultValue) {
        try {
            return future.get(millis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Gets the result of the future by {@link Future#get(long, TimeUnit)}, and if the operation fails, returns the
     * default value.
     *
     * @param future       the future object
     * @param timeout      the maximum time to wait
     * @param defaultValue the default value
     * @param <T>          the type of the result
     * @return the result of the future object
     */
    public static <T> T get(@Nonnull Future<T> future, @Nonnull Duration timeout, T defaultValue) {
        try {
            return future.get(timeout.toNanos(), TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private FutureKit() {
    }
}
