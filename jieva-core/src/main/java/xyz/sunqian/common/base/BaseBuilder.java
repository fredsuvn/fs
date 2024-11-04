package xyz.sunqian.common.base;

/**
 * Base interface for {@code Builder}. The {@code Builder} is used to set then build in method chaining like following:
 * <pre>
 *     ExecutorService s = Jie.executorBuilder()
 *         .corePoolSize(1)
 *         .maxPoolSize(10)
 *         .keepAliveTime(time)
 *         .build();
 * </pre>
 *
 * @param <T> type of built object
 * @param <B> type of builder
 * @author fredsuvn
 */
public interface BaseBuilder<T, B extends BaseBuilder<T, B>> {

    /**
     * Builds a new instance.
     *
     * @return new instance
     */
    T build();

    /**
     * Resets all setting.
     *
     * @return this
     */
    B reset();
}
