package xyz.sunqian.common.base;

/**
 * Base interface for {@code Starter}. The {@code Starter} is used to set then start in method chaining like following:
 * <pre>
 *     Process p = Jie.processStarter()
 *         .command(commands)
 *         .input(input)
 *         .output(output)
 *         .errorOutput(error)
 *         .start();
 * </pre>
 *
 * @param <T> type of started object
 * @param <S> type of starter
 * @author fredsuvn
 */
public interface BaseStarter<T, S extends BaseStarter<T, S>> {

    /**
     * Builds and starts a new starter.
     *
     * @return new starter
     */
    T start();

    /**
     * Resets all setting.
     *
     * @return this
     */
    S reset();
}
