package xyz.sunqian.common.base.function;

/**
 * This is a variant of {@link Runnable} that returns a {@code boolean} result. It's a functional interface whose
 * functional method is {@link #run()}.
 *
 * @author sunqian
 */
@FunctionalInterface
public interface BooleanRunnable {

    /**
     * Runs this function and returns a {@link boolean} result.
     *
     * @return the {@link boolean} result
     */
    boolean run();
}
