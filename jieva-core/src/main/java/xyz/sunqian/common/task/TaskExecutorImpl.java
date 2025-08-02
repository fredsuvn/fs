package xyz.sunqian.common.task;

import xyz.sunqian.annotations.JdkDependent;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.exception.WrappedException;
import xyz.sunqian.common.base.function.VoidCallable;
import xyz.sunqian.common.base.thread.ThreadKit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class TaskExecutorImpl implements TaskExecutor {

    static final @Nonnull TaskExecutor DEFAULT_EXECUTOR = newExecutor(false);
    static final @Nonnull TaskExecutor DEFAULT_SCHEDULER = newExecutor(true);

    @JdkDependent
    @SuppressWarnings("ScheduledThreadPoolExecutorWithZeroCoreThreads")
    static @Nonnull TaskExecutor newExecutor(boolean scheduled) {
        ExecutorService service = scheduled ?
            new ScheduledThreadPoolExecutor(0) :
            new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>()
            );
        return newExecutor(service);
    }

    static @Nonnull TaskExecutor newExecutor(
        int coreThreadSize, int maxThreadSize, int maxQueueSize
    ) throws IllegalArgumentException {
        ThreadPoolExecutor service = new ThreadPoolExecutor(
            coreThreadSize,
            maxThreadSize,
            60L,
            TimeUnit.SECONDS,
            maxQueueSize <= 0 ? new LinkedBlockingQueue<>() : new LinkedBlockingQueue<>(maxQueueSize)
        );
        return newExecutor(service);
    }

    static @Nonnull TaskExecutor newExecutor(@Nonnull ExecutorService service) {
        return new TaskExecutorImpl(service);
    }

    private final @Nonnull ExecutorService service;

    private TaskExecutorImpl(@Nonnull ExecutorService service) {
        this.service = service;
    }

    @Override
    public void run(@Nonnull Runnable task) throws TaskSubmissionException {
        try {
            service.execute(task);
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

    @Override
    public @Nonnull RunReceipt submit(@Nonnull Runnable task) throws TaskSubmissionException {
        try {
            RunnableTask absWork = new RunnableTask(Objects.requireNonNull(task));
            Future<?> future = service.submit(absWork.asRunnable());
            return new RunReceiptImpl(future, absWork);
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

    @Override
    public @Nonnull <T> CallReceipt<T> submit(@Nonnull Callable<? extends T> task) throws TaskSubmissionException {
        try {
            CallableTask<T> absWork = new CallableTask<>(Objects.requireNonNull(task));
            Future<T> future = service.submit(absWork.asCallable());
            return new CallReceiptImpl<>(future, absWork);
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

    @Override
    public @Nonnull <T> List<@Nonnull CallReceipt<T>> executeAll(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> tasks
    ) throws AwaitingException {
        try {
            List<@Nonnull Task<T>> taskList = callablesToAbsWorks(tasks);
            List<@Nonnull Future<T>> futureList = service.invokeAll(taskList);
            return futuresToReceipts(futureList, taskList);
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    @Override
    public @Nonnull <T> List<@Nonnull CallReceipt<T>> executeAll(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> tasks,
        @Nonnull Duration duration
    ) throws AwaitingException {
        try {
            List<@Nonnull Task<T>> taskList = callablesToAbsWorks(tasks);
            List<@Nonnull Future<T>> futureList = service.invokeAll(taskList, duration.toNanos(), TimeUnit.NANOSECONDS);
            return futuresToReceipts(futureList, taskList);
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    @Override
    public <T> T executeAny(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> tasks
    ) throws AwaitingException {
        try {
            List<@Nonnull Task<T>> taskList = callablesToAbsWorks(tasks);
            return service.invokeAny(taskList);
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    @Override
    public <T> T executeAny(
        @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> tasks,
        @Nonnull Duration duration
    ) throws AwaitingException {
        try {
            List<@Nonnull Task<T>> taskList = callablesToAbsWorks(tasks);
            return service.invokeAny(taskList, duration.toNanos(), TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            throw new AwaitingException(e);
        }
    }

    private <T> @Nonnull List<@Nonnull Task<T>> callablesToAbsWorks(
        @Nonnull Collection<? extends @Nonnull Callable<? extends T>> tasks
    ) {
        Stream<@Nonnull Task<T>> stream = tasks.stream()
            .map(it -> new CallableTask<>(Objects.requireNonNull(it)));
        return stream.collect(Collectors.toList());
    }

    private <T> @Nonnull List<@Nonnull CallReceipt<T>> futuresToReceipts(
        @Nonnull List<@Nonnull Future<T>> futureList,
        @Nonnull List<@Nonnull Task<T>> taskList
    ) {
        List<@Nonnull CallReceipt<T>> result = new ArrayList<>(futureList.size());
        int c = 0;
        for (@Nonnull Future<T> future : futureList) {
            result.add(new CallReceiptImpl<>(future, taskList.get(c++)));
        }
        return result;
    }

    @Override
    public void shutdown() {
        service.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return service.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return service.isShutdown();
    }

    @Override
    public void await() throws AwaitingException {
        Jie.uncheck(
            () -> ThreadKit.until(
                () -> awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)
            ),
            AwaitingException::new
        );
    }

    @Override
    public boolean await(long millis) throws AwaitingException {
        return Jie.uncheck(
            () -> awaitTermination(millis, TimeUnit.MILLISECONDS),
            AwaitingException::new
        );
    }

    @Override
    public boolean await(@Nonnull Duration duration) throws AwaitingException {
        return Jie.uncheck(
            () -> awaitTermination(duration.toNanos(), TimeUnit.NANOSECONDS),
            AwaitingException::new
        );
    }

    private boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return service.awaitTermination(timeout, unit);
    }

    @Override
    public boolean isTerminated() {
        return service.isTerminated();
    }

    @Override
    public boolean isScheduled() {
        return service instanceof ScheduledExecutorService;
    }

    @Override
    public @Nonnull RunReceipt schedule(
        @Nonnull Runnable task, @Nonnull Duration delay
    ) throws TaskSubmissionException {
        try {
            ScheduledExecutorService scheduledService = getScheduledService();
            RunnableTask absWork = new RunnableTask(Objects.requireNonNull(task));
            ScheduledFuture<?> future = scheduledService.schedule(
                (Runnable) absWork, delay.toNanos(), TimeUnit.NANOSECONDS);
            return new RunReceiptImpl(future, absWork);
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

    @Override
    public @Nonnull <T> CallReceipt<T> schedule(
        @Nonnull Callable<? extends T> task, @Nonnull Duration delay
    ) throws TaskSubmissionException {
        try {
            ScheduledExecutorService scheduledService = getScheduledService();
            CallableTask<T> absWork = new CallableTask<>(Objects.requireNonNull(task));
            ScheduledFuture<T> future = scheduledService.schedule(
                (Callable<T>) absWork, delay.toNanos(), TimeUnit.NANOSECONDS);
            return new CallReceiptImpl<>(future, absWork);
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

    @Override
    public @Nonnull RunReceipt scheduleWithRate(
        @Nonnull Runnable task, @Nonnull Duration initialDelay, @Nonnull Duration period
    ) throws TaskSubmissionException {
        try {
            ScheduledExecutorService scheduledService = getScheduledService();
            RunnableTask absWork = new RunnableTask(Objects.requireNonNull(task));
            ScheduledFuture<?> future = scheduledService.scheduleAtFixedRate(
                absWork, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS);
            return new RunReceiptImpl(future, absWork);
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

    @Override
    public @Nonnull RunReceipt scheduleWithDelay(
        @Nonnull Runnable task, @Nonnull Duration initialDelay, @Nonnull Duration delay
    ) throws TaskSubmissionException {
        try {
            ScheduledExecutorService scheduledService = getScheduledService();
            RunnableTask absWork = new RunnableTask(Objects.requireNonNull(task));
            ScheduledFuture<?> future = scheduledService.scheduleWithFixedDelay(
                absWork, initialDelay.toNanos(), delay.toNanos(), TimeUnit.NANOSECONDS);
            return new RunReceiptImpl(future, absWork);
        } catch (Exception e) {
            throw new TaskSubmissionException(e);
        }
    }

    @Override
    public @Nonnull ExecutorService asExecutorService() {
        return service;
    }

    @Override
    public @Nonnull ScheduledExecutorService asScheduledExecutorService() throws UnsupportedOperationException {
        return getScheduledService();
    }

    private @Nonnull ScheduledExecutorService getScheduledService() throws UnsupportedOperationException {
        try {
            return (ScheduledExecutorService) service;
        } catch (Exception e) {
            throw new UnsupportedOperationException("Current executor does not support Scheduling.");
        }
    }

    private static abstract class Task<T> implements Runnable, Callable<T> {

        static final int WAITING = 0;
        static final int EXECUTING = 1;
        static final int SUCCEEDED = 2;
        static final int FAILED = 3;

        volatile @Nonnull int state = WAITING;
        volatile @Nullable Exception exception;

        public T execute() throws Exception {
            state = EXECUTING;
            try {
                T result = doExecute();
                state = SUCCEEDED;
                return result;
            } catch (Exception e) {
                exception = e;
                state = FAILED;
                throw e;
            }
        }

        protected abstract T doExecute() throws Exception;

        @Override
        public void run() {
            Jie.uncheck(this::execute, WrappedException::new);
        }

        @Override
        public T call() throws Exception {
            return execute();
        }

        public Runnable asRunnable() {
            return this;
        }

        public Callable<T> asCallable() {
            return this;
        }
    }

    private static final class RunnableTask extends Task<Object> {

        private final @Nonnull Runnable runnable;

        private RunnableTask(@Nonnull Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected Object doExecute() {
            runnable.run();
            return null;
        }
    }

    private static final class CallableTask<T> extends Task<T> {

        private final @Nonnull Callable<? extends T> callable;

        private CallableTask(@Nonnull Callable<? extends T> callable) {
            this.callable = callable;
        }

        @Override
        protected T doExecute() throws Exception {
            return callable.call();
        }
    }

    private static abstract class AbsReceipt implements BaseReceipt {

        private final @Nonnull Future<?> future;
        private final @Nonnull Task<?> task;

        private AbsReceipt(@Nonnull Future<?> future, Task<?> task) {
            this.future = future;
            this.task = task;
        }

        @Override
        public @Nonnull TaskState getState() {
            if (future.isDone()) {
                int state = task.state;
                if (future.isCancelled()) {
                    if (state == Task.WAITING) {
                        return TaskState.CANCELED;
                    }
                    return TaskState.CANCELED_EXECUTING;
                }
                if (state == Task.SUCCEEDED) {
                    return TaskState.SUCCEEDED;
                }
                return TaskState.FAILED;
            }
            if (task.state == Task.WAITING) {
                return TaskState.WAITING;
            }
            return TaskState.EXECUTING;
        }

        @Override
        public boolean cancel(boolean interrupt) {
            return future.cancel(interrupt);
        }

        @Override
        public @Nullable Throwable getException() {
            return task.exception;
        }

        public @Nullable Duration getDelay() {
            if (!(future instanceof ScheduledFuture)) {
                return null;
            }
            ScheduledFuture<?> scheduledFuture = (ScheduledFuture<?>) future;
            long nanos = scheduledFuture.getDelay(TimeUnit.NANOSECONDS);
            return Duration.ofNanos(nanos);
        }

        protected <T> @Nonnull Future<T> getFuture() {
            return Jie.as(future);
        }
    }

    private static final class RunReceiptImpl extends AbsReceipt implements RunReceipt {

        private RunReceiptImpl(@Nonnull Future<?> future, Task<?> task) {
            super(future, task);
        }

        @Override
        public void await() throws AwaitingException {
            doAwait(() -> {
                Future<?> future = getFuture();
                future.get();
            });
        }

        @Override
        public void await(long millis) throws AwaitingException {
            doAwait(() -> {
                Future<?> future = getFuture();
                future.get(millis, TimeUnit.MILLISECONDS);
            });
        }

        @Override
        public void await(@Nonnull Duration duration) throws AwaitingException {
            doAwait(() -> {
                Future<?> future = getFuture();
                future.get(duration.toNanos(), TimeUnit.NANOSECONDS);
            });
        }

        private void doAwait(VoidCallable callable) throws AwaitingException {
            Jie.uncheck(
                () -> {
                    try {
                        callable.call();
                    } catch (ExecutionException | CancellationException e) {
                        // do nothing
                    }
                },
                AwaitingException::new
            );
        }
    }

    private static final class CallReceiptImpl<T> extends AbsReceipt implements CallReceipt<T> {

        private CallReceiptImpl(@Nonnull Future<T> future, Task<T> task) {
            super(future, task);
        }

        @Override
        public @Nullable T await() throws AwaitingException {
            return doAwait(() -> {
                Future<T> future = getFuture();
                return future.get();
            });
        }

        @Override
        public @Nullable T await(long millis) throws AwaitingException {
            return doAwait(() -> {
                Future<T> future = getFuture();
                return future.get(millis, TimeUnit.MILLISECONDS);
            });
        }

        @Override
        public @Nullable T await(@Nonnull Duration duration) throws AwaitingException {
            return doAwait(() -> {
                Future<T> future = getFuture();
                return future.get(duration.toNanos(), TimeUnit.NANOSECONDS);
            });
        }

        private @Nullable T doAwait(Callable<T> callable) throws AwaitingException {
            return Jie.uncheck(
                () -> {
                    try {
                        return callable.call();
                    } catch (ExecutionException | CancellationException e) {
                        return null;
                    }
                },
                AwaitingException::new
            );
        }
    }
}
