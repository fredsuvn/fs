package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.function.BiConsumer;
import java.util.function.LongConsumer;

final class LatchBack {

    static <T> @Nonnull ThreadLatch<T> newLatch() {
        return new NoConsumerThreadLatch<>();
    }

    static <T> @Nonnull ThreadLatch<T> newLatch(
        @Nonnull BiConsumer<ThreadLatch<T>, ? super @Nullable T> signalConsumer
    ) {
        return new ConsumerThreadLatch<>(signalConsumer);
    }

    static @Nonnull CountLatch newCountLatch(long initialCount) {
        return new CountLatchImpl(initialCount);
    }

    private static abstract class AbsThreadLatch<T> implements ThreadLatch<T>, ThreadLatch.Waiter<T> {

        protected final @Nonnull LatchSync sync = new LatchSync();

        @Override
        public void await() throws InterruptedRuntimeException {
            try {
                sync.acquireSharedInterruptibly(1);
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException(e);
            }
        }

        @Override
        public boolean await(@Nonnull Duration duration) throws InterruptedRuntimeException {
            try {
                return sync.tryAcquireSharedNanos(1, duration.toNanos());
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException(e);
            }
        }

        @Override
        public @Nonnull State state() {
            return (sync.getCount() == State.LATCHED.value) ? State.LATCHED : State.UNLATCHED;
        }

        @Override
        public void signal(@Nullable T o) {
        }

        @Override
        public void latch() {
            sync.releaseShared(State.LATCHED.value);
        }

        @Override
        public void unlatch() {
            sync.releaseShared(State.UNLATCHED.value);
        }

        @Override
        public @Nonnull Waiter<T> waiter() {
            return this;
        }

        private static final class LatchSync extends AbstractQueuedSynchronizer {

            private LatchSync() {
                setState(ThreadLatch.State.LATCHED.value);
            }

            private int getCount() {
                return getState();
            }

            protected int tryAcquireShared(int acquires) {
                return (getState() == ThreadLatch.State.LATCHED.value) ? -1 : 1;
            }

            protected boolean tryReleaseShared(int releases) {
                setState(releases);
                return releases == ThreadLatch.State.UNLATCHED.value;
            }
        }
    }

    private static final class NoConsumerThreadLatch<T> extends AbsThreadLatch<T> {

        private NoConsumerThreadLatch() {
        }
    }

    private static final class ConsumerThreadLatch<T> extends AbsThreadLatch<T> {

        private final @Nonnull BiConsumer<ThreadLatch<T>, ? super @Nullable T> consumer;

        private ConsumerThreadLatch(@Nonnull BiConsumer<ThreadLatch<T>, ? super @Nullable T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void signal(@Nullable T o) {
            consumer.accept(this, o);
        }
    }

    private static final class CountLatchImpl
        extends AbsThreadLatch<Long>
        implements CountLatch, CountLatch.Waiter {

        private long count;
        private final @Nonnull LongConsumer consumer;

        public CountLatchImpl(long initialCount) {
            this.count = initialCount;
            this.consumer = (value) -> {
                synchronized (this) {
                    long newValue = count + value;
                    count = newValue;
                    doLatch(newValue);
                }
            };
            doLatch(initialCount);
        }

        @Override
        public void signal(@Nullable Long o) {
            CountLatch.super.signal(o);
        }

        @Override
        public void signal(long i) {
            consumer.accept(i);
        }

        @Override
        public void countDown() {
            CountLatch.super.countDown();
        }

        @Override
        public void countUp() {
            CountLatch.super.countUp();
        }

        @Override
        public long count() {
            return count;
        }

        @Override
        public void reset(long newCount) {
            synchronized (this) {
                count = newCount;
                doLatch(newCount);
            }
        }

        @Override
        public void latch() {
            CountLatch.super.latch();
        }

        @Override
        public void unlatch() {
            CountLatch.super.unlatch();
        }

        private void doLatch(long newValue) {
            if (newValue == 0) {
                sync.releaseShared(State.UNLATCHED.value);
            } else {
                sync.releaseShared(State.LATCHED.value);
            }
        }

        @Override
        public @Nonnull CountLatch.Waiter waiter() {
            return this;
        }
    }
}
