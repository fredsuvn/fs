package space.sunqian.fs.base.value;

import space.sunqian.annotation.Immutable;
import space.sunqian.fs.base.exception.WrappedException;

import java.util.function.Function;

/**
 * This interface represents a wrapper to wrap a return value from a method or function or other similar ones.
 *
 * @param <T> type of the return value
 * @author sunqian
 */
@Immutable
public interface Ret<T> {

    /**
     * Returns an instance of {@link Ret} with the specified return value.
     *
     * @param value the specified return value
     * @param <T>   type of the return value
     * @return an instance of {@link Ret} with the specified return value
     */
    static <T> Ret<T> of(T value) {
        return new Ret<T>() {

            @Override
            public T get() throws WrappedException {
                return value;
            }

            @Override
            public T get(Function<Throwable, ? extends T> handler) {
                return value;
            }
        };
    }

    /**
     * Returns an instance of {@link Ret} with the specified error.
     *
     * @param error the specified error
     * @param <T>   type of the return value
     * @return an instance of {@link Ret} with the specified error
     */
    static <T> Ret<T> of(Throwable error) {
        return new Ret<T>() {

            @Override
            public T get() throws WrappedException {
                throw new WrappedException(error);
            }

            @Override
            public T get(Function<Throwable, ? extends T> handler) {
                return handler.apply(error);
            }
        };
    }

    /**
     * Returns the return value. If an error occurs during the production to the return value, throws a
     * {@link WrappedException}.
     *
     * @return the return value
     * @throws WrappedException If an error occurs during the production to the return value
     */
    T get() throws WrappedException;

    /**
     * Returns the return value. If an error occurs during the production to the return value, the {@code handler} will
     * be invoked to handle the error.
     *
     * @param handler the handler to handle the error
     * @return the return value
     */
    T get(Function<Throwable, ? extends T> handler);
}
