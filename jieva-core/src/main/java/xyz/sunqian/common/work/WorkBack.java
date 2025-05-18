package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

final class WorkBack {

    private static final class AbsWorkExecutor implements WorkExecutor {

        protected final @Nonnull ExecutorService service;

        private AbsWorkExecutor(@Nonnull ExecutorService service) {
            this.service = service;
        }

        @Override
        public void run(@Nonnull Runnable work) throws SubmissionException {
            service.execute(work);
        }

        @Override
        public @Nonnull RunReceipt submit(@Nonnull Runnable work) throws SubmissionException {
            return null;
        }

        @Override
        public @Nonnull <T> WorkReceipt<T> submit(@Nonnull Callable<? extends T> work) throws SubmissionException {
            return null;
        }

        @Override
        public @Nonnull <T> WorkReceipt<T> submit(@Nonnull Work<? extends T> work) throws SubmissionException {
            return null;
        }

        @Override
        public @Nonnull <T> List<@Nonnull WorkReceipt<T>> executeAll(@Nonnull Collection<? extends @Nonnull Work<? extends T>> works) throws AwaitingException {
            return Collections.emptyList();
        }

        @Override
        public @Nonnull <T> List<@Nonnull WorkReceipt<T>> executeAll(@Nonnull Collection<? extends @Nonnull Work<? extends T>> works, @Nonnull Duration timeout) throws AwaitingException {
            return Collections.emptyList();
        }

        @Override
        public <T> T executeAny(@Nonnull Collection<? extends @Nonnull Work<? extends T>> works) throws AwaitingException {
            return null;
        }

        @Override
        public <T> T executeAny(@Nonnull Collection<? extends @Nonnull Work<? extends T>> works, @Nonnull Duration duration) throws AwaitingException {
            return null;
        }

        @Override
        public @Nonnull RunReceipt schedule(@Nonnull Runnable work, @Nonnull Duration delay) throws SubmissionException {
            return null;
        }

        @Override
        public @Nonnull <T> WorkReceipt<T> schedule(@Nonnull Work<? extends T> work, @Nonnull Duration delay) throws SubmissionException {
            return null;
        }

        @Override
        public @Nonnull RunReceipt scheduleWithRate(@Nonnull Runnable work, @Nonnull Duration initialDelay, @Nonnull Duration period) throws SubmissionException {
            return null;
        }

        @Override
        public @Nonnull RunReceipt scheduleWithDelay(@Nonnull Runnable work, @Nonnull Duration initialDelay, @Nonnull Duration delay) throws SubmissionException {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public List<Runnable> closeNow() {
            return Collections.emptyList();
        }

        @Override
        public boolean isClosed() {
            return false;
        }

        @Override
        public void await() throws AwaitingException {

        }

        @Override
        public boolean await(@Nonnull Duration duration) throws AwaitingException {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }
    }

    private static abstract class AbsWork<T> implements Work<T> {

        volatile @Nullable WorkState state = null;
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

    private static final class RunnableWork<T> extends AbsWork<T> {

        private final @Nonnull Runnable runnable;

        private RunnableWork(@Nonnull Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected T runWork() {
            runnable.run();
            return null;
        }
    }

    private static final class CallableWork<T> extends AbsWork<T> {

        private final @Nonnull Callable<T> callable;

        private CallableWork(@Nonnull Callable<T> callable) {
            this.callable = callable;
        }

        @Override
        protected T runWork() throws Exception {
            return callable.call();
        }
    }

    private static final class WorkWrapper<T> extends AbsWork<T> {

        private final @Nonnull Work<T> work;

        private WorkWrapper(@Nonnull Work<T> work) {
            this.work = work;
        }

        @Override
        protected T runWork() throws Exception {
            return work.doWork();
        }
    }

    private static abstract class AbsReceipt implements BaseWorkReceipt {

        protected final @Nonnull Future<?> future;
        private final @Nonnull AbsWork<?> work;

        private AbsReceipt(@Nonnull Future<?> future, AbsWork<?> work) {
            this.future = future;
            this.work = work;
        }

        @Override
        public @Nonnull WorkState getState() {
            WorkState state = work.state;
            if (state == null) {
                return WorkState.WAITING;
            }
            if (state.isTerminal()) {
                return state;
            }
            if (future.isDone()) {
                state = work.state;
                if (state == null) {
                    work.state = WorkState.CANCELED;
                    return WorkState.CANCELED;
                }
                if (future.isCancelled()) {
                    work.state = WorkState.CANCELED_DURING;
                    return WorkState.CANCELED_DURING;
                }
                return state;
            }
            return WorkState.EXECUTING;
        }

        @Override
        public boolean cancel(boolean interrupt) {
            return future.cancel(interrupt);
        }

        @Override
        public @Nullable Throwable getException() {
            return work.exception;
        }
    }

    private static abstract class AbsWorkReceipt<T> extends AbsReceipt implements WorkReceipt<T>{

        private AbsWorkReceipt(@Nonnull Future<T> future, AbsWork<T> work) {
            super(future, work);
        }

        @Override
        public T await() throws AwaitingException {
            try {
                return ((Future<T>)future).get();
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }

        @Override
        public T await(@Nonnull Duration duration) throws AwaitingException {
            try {
                return future.get(duration.toNanos(), TimeUnit.NANOSECONDS);
            } catch (Exception e) {
                throw new AwaitingException(e);
            }
        }

    }

    private static final class FutureReceipt<T> extends AbsFutureReceipt<T> {

        private FutureReceipt(@Nonnull Future<T> future, AbsWork<T> work) {
            super(future, work);
        }

        @Override
        public @Nullable Duration getDelay() {
            return null;
        }
    }

    private static final class ScheduledFutureReceipt<T> extends AbsFutureReceipt<T> {

        private ScheduledFutureReceipt(@Nonnull ScheduledFuture<T> future, AbsWork<T> work) {
            super(future, work);
        }

        @Override
        public @Nonnull Duration getDelay() {
            long nanos = ((ScheduledFuture<T>) future).getDelay(TimeUnit.NANOSECONDS);
            return Duration.ofNanos(nanos);
        }
    }
}
