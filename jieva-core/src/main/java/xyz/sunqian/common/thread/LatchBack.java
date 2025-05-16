package xyz.sunqian.common.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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

        private volatile @Nonnull CountDownLatch latch = new CountDownLatch(1);
        private final @Nonnull Object lock = new Object();

        @Override
        public void await() throws InterruptedRuntimeException {
            try {
                getLatch().await();
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException(e);
            }
        }

        @Override
        public boolean await(@Nonnull Duration duration) throws InterruptedRuntimeException {
            try {
                return getLatch().await(duration.toNanos(), TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                throw new InterruptedRuntimeException(e);
            }
        }

        private @Nonnull CountDownLatch getLatch() {
            return latch;
        }

        @Override
        public @Nonnull State state() {
            return latch.getCount() <= 0 ? State.UNLATCHED : State.LATCHED;
        }

        @Override
        public void signal(@Nullable T o) {
        }

        @Override
        public void latch() {
            synchronized (lock) {
                CountDownLatch oldLatch = this.latch;
                if (oldLatch.getCount() <= 0) {
                    this.latch = new CountDownLatch(1);
                }
            }
        }

        @Override
        public void unlatch() {
            synchronized (lock) {
                latch.countDown();
            }
        }

        @Override
        public @Nonnull Waiter<T> waiter() {
            return this;
        }
    }

    private static final class NoConsumerThreadLatch<T> extends AbsThreadLatch<T> {
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

    private static final class CountLatchImpl implements CountLatch, CountLatch.Waiter {

        private final ThreadLatch<?> latch = ThreadLatch.newLatch();
        private final @Nonnull AtomicLong counter;
        private final @Nonnull LongConsumer consumer;

        public CountLatchImpl(long initialCount) {
            this.counter = new AtomicLong(initialCount);
            this.consumer = (value) -> {
                synchronized (latch) {
                    long newValue = counter.addAndGet(value);
                    afterSettingCounter(newValue);
                }
            };
        }

        @Override
        public void await() throws InterruptedRuntimeException {
            latch.await();
        }

        @Override
        public boolean await(@Nonnull Duration duration) throws InterruptedRuntimeException {
            return latch.await(duration);
        }

        @Override
        public @Nonnull State state() {
            return latch.state();
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
            return counter.get();
        }

        @Override
        public void reset(long newCount) {
            counter.set(newCount);
            afterSettingCounter(newCount);
        }

        private void afterSettingCounter(long newValue) {
            if (newValue == 0) {
                latch.unlatch();
            } else {
                latch.latch();
            }
        }

        @Override
        public @Nonnull CountLatch.Waiter waiter() {
            return this;
        }
    }
}
