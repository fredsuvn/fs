package space.sunqian.fs.object.pool;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Simple object pool interface, provides methods for acquiring, releasing objects. If the pool is closed due to some
 * exception, the {@link #unreleasedObjects()} can be still invoked to get the list of unreleased objects.
 *
 * @param <T> the type of objects in the pool
 * @author sunqian
 */
public interface SimplePool<T> {

    /**
     * Returns a builder for {@link SimplePool}.
     *
     * @param <T> the type of objects in the pool
     * @return a builder for {@link SimplePool}
     */
    static <T> @Nonnull Builder<T> newBuilder() {
        return new Builder<>();
    }

    /**
     * Acquires an object from the pool, or {@code null} if no object is available.
     * <p>
     * If any exception occurs during the acquisition process, {@link #close()} will be invoked to close this pool and
     * the {@link #unreleasedObjects()} will return the list of unreleased objects, including idle objects and active
     * objects.
     *
     * @return the acquired object, or {@code null} if no object is available
     * @throws ObjectPoolException if failed to acquire object
     */
    @Nullable
    T get() throws ObjectPoolException;

    /**
     * Releases the given object to the pool. If the object is not acquired from this pool, this method will do
     * nothing.
     * <p>
     * If any exception occurs during the release process, {@link #close()} will be invoked to close this pool and the
     * {@link #unreleasedObjects()} will return the list of unreleased objects, including idle objects and active
     * objects.
     *
     * @param obj the given object to release
     * @throws ObjectPoolException if failed to release object
     */
    void release(@Nonnull T obj) throws ObjectPoolException;

    /**
     * Cleans the pool, removing idle objects that idle timeout or invalidated by validator, or over the core size,
     * adding new objects up to the core size if necessary. The active objects will not be cleaned.
     * <p>
     * If any exception occurs during the clean process, {@link #close()} will be invoked to close this pool and the
     * {@link #unreleasedObjects()} will return the list of unreleased objects, including idle objects and active
     * objects.
     *
     * @throws ObjectPoolException if any exception occurs during the clean process
     */
    void clean() throws ObjectPoolException;

    /**
     * Closes the pool. The idle objects will be discarded by configured discarder, and the active objects will not be
     * discarded. If this process is failed, the pool will be in a closed state, and the {@link #unreleasedObjects()}
     * will return the list of unreleased objects, including idle objects and active objects.
     */
    void close();

    /**
     * Returns whether the pool is closed.
     *
     * @return {@code true} if the pool is closed, {@code false} otherwise
     */
    boolean isClosed();

    /**
     * Returns the list of unreleased objects after the pool is closed, including idle objects and active objects. If
     * the pool is not closed, this method will return an empty list.
     *
     * @return the list of unreleased objects after the pool is closed, or an empty list if the pool is not closed
     */
    @Nonnull
    @Immutable
    List<@Nonnull T> unreleasedObjects();

    /**
     * Returns the number of objects in the pool.
     *
     * @return the number of objects in the pool
     */
    int size();

    /**
     * Returns the number of idle objects in the pool.
     *
     * @return the number of idle objects in the pool
     */
    int idleSize();

    /**
     * Returns the number of active objects in the pool.
     *
     * @return the number of active objects in the pool
     */
    int activeSize();

    /**
     * Builder class for {@link SimplePool}.
     *
     * @param <T> the type of objects in the pool
     */
    class Builder<T> {

        // size:

        private int coreSize = 1;
        private int maxSize = -1;
        private long idleTimeoutMillis = 60000;

        // actions:

        private Supplier<? extends @Nonnull T> supplier;
        private @Nonnull Predicate<? super @Nonnull T> validator = t -> true;
        private @Nonnull Consumer<? super @Nonnull T> discarder = t -> {};

        /**
         * Sets the supplier for creating new objects.
         *
         * @param supplier the supplier for creating new objects
         * @return this builder
         */
        public @Nonnull Builder<T> supplier(@Nonnull Supplier<? extends @Nonnull T> supplier) {
            this.supplier = supplier;
            return this;
        }

        /**
         * Sets the validator for checking object validity.
         *
         * @param validator the validator for checking object validity
         * @return this builder
         */
        public @Nonnull Builder<T> validator(@Nonnull Predicate<? super @Nonnull T> validator) {
            this.validator = validator;
            return this;
        }

        /**
         * Sets the discarder for destroying objects.
         *
         * @param discarder the discarder for destroying objects
         * @return this builder
         */
        public @Nonnull Builder<T> discarder(@Nonnull Consumer<? super @Nonnull T> discarder) {
            this.discarder = discarder;
            return this;
        }

        /**
         * Sets the core size of the pool.
         *
         * @param coreSize the core size of the pool
         * @return this builder
         * @throws IllegalArgumentException if coreSize is less than or equal to 0
         */
        public @Nonnull Builder<T> coreSize(int coreSize) throws IllegalArgumentException {
            if (coreSize <= 0) {
                throw new IllegalArgumentException("coreSize must be greater than 0.");
            }
            this.coreSize = coreSize;
            return this;
        }

        /**
         * Sets the max size of the pool.
         *
         * @param maxSize the max size of the pool
         * @return this builder
         * @throws IllegalArgumentException if maxSize is less than coreSize
         */
        public @Nonnull Builder<T> maxSize(int maxSize) throws IllegalArgumentException {
            if (maxSize < coreSize) {
                throw new IllegalArgumentException("maxSize must be greater than or equal to coreSize.");
            }
            this.maxSize = maxSize;
            return this;
        }

        /**
         * Sets the idle timeout in milliseconds of the pool.
         *
         * @param idleTimeoutMillis the idle timeout in milliseconds of the pool
         * @return this builder
         * @throws IllegalArgumentException if idleTimeout is less than or equal to 0
         */
        public @Nonnull Builder<T> idleTimeout(long idleTimeoutMillis) throws IllegalArgumentException {
            if (idleTimeoutMillis <= 0) {
                throw new IllegalArgumentException("idleTimeout must be greater than 0.");
            }
            this.idleTimeoutMillis = idleTimeoutMillis;
            return this;
        }

        /**
         * Sets the idle timeout in milliseconds of the pool.
         *
         * @param idleTimeout the idle timeout in milliseconds of the pool
         * @return this builder
         * @throws IllegalArgumentException if idleTimeout is less than or equal to 0
         */
        public @Nonnull Builder<T> idleTimeout(@Nonnull Duration idleTimeout) throws IllegalArgumentException {
            if (idleTimeout.isNegative()) {
                throw new IllegalArgumentException("idleTimeout must be greater than 0.");
            }
            this.idleTimeoutMillis = idleTimeout.toMillis();
            return this;
        }

        /**
         * Builds a {@link SimplePool} instance. If some exception occurs during the initialization, a closed pool with
         * unreleased objects (if any) will be returned.
         *
         * @param <T1> the type of objects in the pool, it is used to pass the generic type in method-chaining
         * @return the built {@link SimplePool} instance
         * @throws IllegalArgumentException if supplier is not set
         */
        public <T1 extends T> @Nonnull SimplePool<T1> build() throws IllegalArgumentException {
            if (supplier == null) {
                throw new IllegalArgumentException("Supplier must be set.");
            }
            return Fs.as(new SimplePoolImpl<>(
                coreSize, Math.max(coreSize, maxSize), idleTimeoutMillis, supplier, validator, discarder
            ));
        }
    }
}