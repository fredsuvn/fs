package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

final class WorkBack {

    private static abstract class AbsWork<T> implements Work<T> {

        volatile WorkState state = WorkState.WAITING;

        @Override
        public T doWork() throws Exception {
            return null;
        }

        protected abstract void doWork0() throws Exception;
    }

    private static final class FutureReceipt<T> implements WorkReceipt<T> {

        private final @Nonnull Future<T> future;

        private FutureReceipt(@Nonnull Future<T> future) {
            this.future = future;
        }

        @Override
        public @Nonnull WorkState getState() {
            future.isDone();
            return null;
        }

        @Override
        public T await() throws AwaitingException {
            try {
                return future.get();
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

        @Override
        public boolean cancel(boolean interrupt) {
            return future.cancel(interrupt);
        }
    }
}
