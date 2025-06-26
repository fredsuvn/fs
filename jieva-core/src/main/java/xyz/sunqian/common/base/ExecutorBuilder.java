package xyz.sunqian.common.base;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Builder for {@link ExecutorService}.
 *
 * @author fredsuvn
 */
public abstract class ExecutorBuilder implements BaseBuilder<ExecutorService, ExecutorBuilder> {

    static ExecutorBuilder newInstance() {
        return new OfJdk8();
    }

    private int corePoolSize;
    private int maxPoolSize;
    private Duration keepAliveTime;
    private BlockingQueue<Runnable> workQueue;
    private ThreadFactory threadFactory;
    private RejectedExecutionHandler rejectHandler;
    private boolean allowCoreThreadTimeOut;

    ExecutorBuilder() {
        reset();
    }

    /**
     * Sets core pool size.
     *
     * @param corePoolSize core pool size
     * @return this
     */
    public ExecutorBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * Sets max pool size.
     *
     * @param maxPoolSize max pool size
     * @return this
     */
    public ExecutorBuilder maxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    /**
     * Sets keep alive time for threads which are created exceed core threads.
     *
     * @param keepAliveTime keep alive time for threads which are created exceed core threads
     * @return this
     */
    public ExecutorBuilder keepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * Sets the queue to use for holding tasks before they are executed.
     *
     * @param workQueue the queue to use for holding tasks before they are executed
     * @return this
     */
    public ExecutorBuilder workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * Sets thread factory.
     *
     * @param threadFactory thread factory
     * @return this
     */
    public ExecutorBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * Sets the handler to use when execution is rejected because the thread bounds and queue capacities are reached.
     *
     * @param rejectHandler the handler to use when execution is rejected
     * @return this
     */
    public ExecutorBuilder rejectHandler(RejectedExecutionHandler rejectHandler) {
        this.rejectHandler = rejectHandler;
        return this;
    }

    /**
     * Sets whether core threads may time out and terminate if no tasks arrive within the keep-alive time.
     *
     * @param allowCoreThreadTimeOut whether core threads may time out and terminate if no tasks arrive within the
     *                               keep-alive time
     * @return this
     */
    public ExecutorBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    @Override
    public ExecutorBuilder reset() {
        this.corePoolSize = 0;
        this.maxPoolSize = 0;
        this.keepAliveTime = null;
        this.workQueue = null;
        this.threadFactory = null;
        this.rejectHandler = null;
        this.allowCoreThreadTimeOut = false;
        return this;
    }

    /**
     * Returns new thread pool which is configured by this.
     *
     * @return thread pool which is configured by this
     */
    public ExecutorService build() {
        Duration keepTime = Jie.nonnull(keepAliveTime, Duration.ZERO);
        BlockingQueue<Runnable> queue = Jie.nonnull(workQueue, LinkedBlockingQueue::new);
        int maxPool = Math.max(maxPoolSize, corePoolSize);
        ThreadPoolExecutor pool;
        if (threadFactory == null && rejectHandler == null) {
            pool = new ThreadPoolExecutor(
                corePoolSize,
                maxPool,
                keepTime.toNanos(),
                TimeUnit.NANOSECONDS,
                queue);
        } else if (rejectHandler == null) {
            pool = new ThreadPoolExecutor(
                corePoolSize,
                maxPool,
                keepTime.toNanos(),
                TimeUnit.NANOSECONDS,
                queue,
                threadFactory
            );
        } else if (threadFactory == null) {
            pool = new ThreadPoolExecutor(
                corePoolSize,
                maxPool,
                keepTime.toNanos(),
                TimeUnit.NANOSECONDS,
                queue,
                rejectHandler
            );
        } else {
            pool = new ThreadPoolExecutor(
                corePoolSize,
                maxPool,
                keepTime.toNanos(),
                TimeUnit.NANOSECONDS,
                queue,
                threadFactory,
                rejectHandler
            );
        }
        if (allowCoreThreadTimeOut) {
            pool.allowCoreThreadTimeOut(true);
        }
        return pool;
    }

    private static final class OfJdk8 extends ExecutorBuilder {
    }
}
