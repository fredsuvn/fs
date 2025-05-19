package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.thread.JieThread;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
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

final class WorkBack {

    static @Nonnull WorkExecutor newExecutor(boolean scheduled) {
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

    static @Nonnull WorkExecutor newExecutor(int coreThreadSize, int maxThreadSize, int maxQueueSize) {
        ThreadPoolExecutor service = new ThreadPoolExecutor(
            coreThreadSize,
            maxThreadSize,
            60L,
            TimeUnit.SECONDS,
            maxQueueSize <= 0 ? new LinkedBlockingQueue<>() : new LinkedBlockingQueue<>(maxQueueSize)
        );
        return newExecutor(service);
    }

    static @Nonnull WorkExecutor newExecutor(@Nonnull ExecutorService service) {
        return new WorkExecutorImpl(service);
    }

    private static final class WorkExecutorImpl implements WorkExecutor {

        private static final Duration AWAITING_UNIT = Duration.ofSeconds(1);

        private final @Nonnull ExecutorService service;

        private WorkExecutorImpl(@Nonnull ExecutorService service) {
            this.service = service;
        }

        @Override
        public void run(@Nonnull Runnable work) throws SubmissionException {
            try {
                service.execute(work);
            } catch (Exception e) {
                throw new SubmissionException(e);
            }
        }

        @Override
        public @Nonnull RunReceipt submit(@Nonnull Runnable work) throws SubmissionException {
            try {
                RunnableWork absWork = new RunnableWork(Objects.requireNonNull(work));
                Future<?> future = service.submit(absWork.asRunnable());
                return new RunReceiptImpl(future, absWork);
            } catch (Exception e) {
                throw new SubmissionException(e);
            }
        }

        @Override
        public @Nonnull <T> WorkReceipt<T> submit(@Nonnull Callable<? extends T> work) throws SubmissionException {
            try {
                CallableWork<T> absWork = new CallableWork<>(Objects.requireNonNull(work));
                Future<T> future = service.submit(absWork.asCallable());
                return new WorkReceiptImpl<>(future, absWork);
            } catch (Exception e) {
                throw new SubmissionException(e);
            }
        }

        @Override
        public @Nonnull <T> List<@Nonnull WorkReceipt<T>> executeAll(
            @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> works
        ) throws AwaitingException {
            try {
                List<@Nonnull AbsWork<T>> workList = callablesToAbsWorks(works);
                List<@Nonnull Future<T>> futureList = service.invokeAll(workList);
                return futuresToReceipts(futureList, workList);
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }

        @Override
        public @Nonnull <T> List<@Nonnull WorkReceipt<T>> executeAll(
            @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> works,
            @Nonnull Duration duration
        ) throws AwaitingException {
            try {
                List<@Nonnull AbsWork<T>> workList = callablesToAbsWorks(works);
                List<@Nonnull Future<T>> futureList = service.invokeAll(workList, duration.toNanos(), TimeUnit.NANOSECONDS);
                return futuresToReceipts(futureList, workList);
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }

        @Override
        public <T> T executeAny(
            @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> works
        ) throws AwaitingException {
            try {
                List<@Nonnull AbsWork<T>> workList = callablesToAbsWorks(works);
                return service.invokeAny(workList);
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }

        @Override
        public <T> T executeAny(
            @RetainedParam @Nonnull Collection<? extends @Nonnull Callable<? extends T>> works,
            @Nonnull Duration duration
        ) throws AwaitingException {
            try {
                List<@Nonnull AbsWork<T>> workList = callablesToAbsWorks(works);
                return service.invokeAny(workList, duration.toNanos(), TimeUnit.NANOSECONDS);
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }

        private <T> @Nonnull List<@Nonnull AbsWork<T>> callablesToAbsWorks(
            @Nonnull Collection<? extends @Nonnull Callable<? extends T>> works
        ) {
            Stream<@Nonnull AbsWork<T>> stream = works.stream()
                .map(it -> new CallableWork<>(Objects.requireNonNull(it)));
            return stream.collect(Collectors.toList());
        }

        private <T> @Nonnull List<@Nonnull WorkReceipt<T>> futuresToReceipts(
            @Nonnull List<@Nonnull Future<T>> futureList,
            @Nonnull List<@Nonnull AbsWork<T>> workList
        ) {
            List<@Nonnull WorkReceipt<T>> result = new ArrayList<>(futureList.size());
            int c = 0;
            for (@Nonnull Future<T> future : futureList) {
                result.add(new WorkReceiptImpl<>(future, workList.get(c++)));
            }
            return result;
        }

        @Override
        public void close() {
            service.shutdown();
        }

        @Override
        public List<Runnable> closeNow() {
            return service.shutdownNow();
        }

        @Override
        public boolean isClosed() {
            return service.isShutdown();
        }

        @Override
        public void await() throws AwaitingException {
            JieThread.until(() -> await(AWAITING_UNIT));
        }

        @Override
        public boolean await(@Nonnull Duration duration) throws AwaitingException {
            try {
                return service.awaitTermination(duration.toNanos(), TimeUnit.NANOSECONDS);
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
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
            @Nonnull Runnable work, @Nonnull Duration delay
        ) throws SubmissionException {
            try {
                ScheduledExecutorService scheduledService = getScheduledService();
                RunnableWork absWork = new RunnableWork(Objects.requireNonNull(work));
                ScheduledFuture<?> future = scheduledService.schedule(
                    (Runnable) absWork, delay.toNanos(), TimeUnit.NANOSECONDS);
                return new RunReceiptImpl(future, absWork);
            } catch (Exception e) {
                throw new SubmissionException(e);
            }
        }

        @Override
        public @Nonnull <T> WorkReceipt<T> schedule(
            @Nonnull Callable<? extends T> work, @Nonnull Duration delay
        ) throws SubmissionException {
            try {
                ScheduledExecutorService scheduledService = getScheduledService();
                CallableWork<T> absWork = new CallableWork<>(Objects.requireNonNull(work));
                ScheduledFuture<T> future = scheduledService.schedule(
                    (Callable<T>) absWork, delay.toNanos(), TimeUnit.NANOSECONDS);
                return new WorkReceiptImpl<>(future, absWork);
            } catch (Exception e) {
                throw new SubmissionException(e);
            }
        }

        @Override
        public @Nonnull RunReceipt scheduleWithRate(
            @Nonnull Runnable work, @Nonnull Duration initialDelay, @Nonnull Duration period
        ) throws SubmissionException {
            try {
                ScheduledExecutorService scheduledService = getScheduledService();
                RunnableWork absWork = new RunnableWork(Objects.requireNonNull(work));
                ScheduledFuture<?> future = scheduledService.scheduleAtFixedRate(
                    absWork, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS);
                return new RunReceiptImpl(future, absWork);
            } catch (Exception e) {
                throw new SubmissionException(e);
            }
        }

        @Override
        public @Nonnull RunReceipt scheduleWithDelay(
            @Nonnull Runnable work, @Nonnull Duration initialDelay, @Nonnull Duration delay
        ) throws SubmissionException {
            try {
                ScheduledExecutorService scheduledService = getScheduledService();
                RunnableWork absWork = new RunnableWork(Objects.requireNonNull(work));
                ScheduledFuture<?> future = scheduledService.scheduleWithFixedDelay(
                    absWork, initialDelay.toNanos(), delay.toNanos(), TimeUnit.NANOSECONDS);
                return new RunReceiptImpl(future, absWork);
            } catch (Exception e) {
                throw new SubmissionException(e);
            }
        }

        private @Nonnull ScheduledExecutorService getScheduledService() {
            try {
                return (ScheduledExecutorService) service;
            } catch (Exception e) {
                throw new UnsupportedOperationException("Current executor does not support Scheduling.");
            }
        }
    }

    private static abstract class AbsWork<T> implements Work<T> {

        volatile @Nonnull WorkState state = WorkState.WAITING;
        volatile @Nullable Exception exception;

        @Override
        public T doWork() throws Exception {
            state = WorkState.EXECUTING;
            try {
                T result = runWork();
                state = WorkState.SUCCEEDED;
                return result;
            } catch (Exception e) {
                exception = e;
                state = WorkState.FAILED;
                throw e;
            }
        }

        protected abstract T runWork() throws Exception;
    }

    private static final class RunnableWork extends AbsWork<Object> {

        private final @Nonnull Runnable runnable;

        private RunnableWork(@Nonnull Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected Object runWork() {
            runnable.run();
            return null;
        }
    }

    private static final class CallableWork<T> extends AbsWork<T> {

        private final @Nonnull Callable<? extends T> callable;

        private CallableWork(@Nonnull Callable<? extends T> callable) {
            this.callable = callable;
        }

        @Override
        protected T runWork() throws Exception {
            return callable.call();
        }
    }

    private static abstract class AbsReceipt implements BaseWorkReceipt {

        private final @Nonnull Future<?> future;
        private final @Nonnull AbsWork<?> work;

        private AbsReceipt(@Nonnull Future<?> future, AbsWork<?> work) {
            this.future = future;
            this.work = work;
        }

        @Override
        public @Nonnull WorkState getState() {
            if (future.isDone()) {
                WorkState state = work.state;
                if (Objects.equals(state, WorkState.WAITING)) {
                    work.state = WorkState.CANCELED;
                    return WorkState.CANCELED;
                }
                if (future.isCancelled()) {
                    work.state = WorkState.CANCELED_DURING;
                    return WorkState.CANCELED_DURING;
                }
                return state;
            }
            return work.state;
        }

        @Override
        public boolean cancel(boolean interrupt) {
            return future.cancel(interrupt);
        }

        @Override
        public @Nullable Throwable getException() {
            return work.exception;
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

    private static final class WorkReceiptImpl<T> extends AbsReceipt implements WorkReceipt<T> {

        private WorkReceiptImpl(@Nonnull Future<T> future, AbsWork<T> work) {
            super(future, work);
        }

        @Override
        public T await() throws AwaitingException {
            try {
                Future<T> future = getFuture();
                return future.get();
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }

        @Override
        public T await(@Nonnull Duration duration) throws AwaitingException {
            try {
                Future<T> future = getFuture();
                return future.get(duration.toNanos(), TimeUnit.NANOSECONDS);
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }
    }

    private static final class RunReceiptImpl extends AbsReceipt implements RunReceipt {

        private RunReceiptImpl(@Nonnull Future<?> future, AbsWork<?> work) {
            super(future, work);
        }

        @Override
        public void await() throws AwaitingException {
            try {
                Future<?> future = getFuture();
                future.get();
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }

        @Override
        public void await(@Nonnull Duration duration) throws AwaitingException {
            try {
                Future<?> future = getFuture();
                future.get(duration.toNanos(), TimeUnit.NANOSECONDS);
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }
    }
}
