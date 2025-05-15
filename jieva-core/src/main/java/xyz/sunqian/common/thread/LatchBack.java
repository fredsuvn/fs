package xyz.sunqian.common.thread;

import xyz.sunqian.annotations.Nonnull;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

final class LatchBack {

    static ThreadLatch newLatch() {
        return new NoConsumerThreadLatch();
    }

    static ThreadLatch newLatch(Consumer<Object> signalConsumer) {
        return new ConsumerThreadLatch(signalConsumer);
    }

    private static abstract class AbsThreadLatch implements ThreadLatch {

        private volatile @Nonnull CountDownLatch latch = new CountDownLatch(1);
        private final Object lock = new Object();

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

        private CountDownLatch getLatch() {
            return latch;
        }

        @Override
        public void lock() {
            synchronized (lock) {
                CountDownLatch oldLatch = this.latch;
                if (oldLatch.getCount() <= 0) {
                    this.latch = new CountDownLatch(1);
                }
            }
        }

        @Override
        public void unlock() {
            synchronized (lock) {
                latch.countDown();
            }
        }

        @Override
        public @Nonnull State state() {
            return latch.getCount() <= 0 ? State.UNLOCKED : State.LOCKED;
        }
    }

    private static final class NoConsumerThreadLatch extends AbsThreadLatch {

        @Override
        public Waiter waiter() {
            return new NoConsumerWaiter(this);
        }
    }

    private static final class ConsumerThreadLatch extends AbsThreadLatch {

        private final Consumer<Object> consumer;

        private ConsumerThreadLatch(Consumer<Object> consumer) {
            this.consumer = consumer;
        }

        @Override
        public Waiter waiter() {
            return new ConsumerWaiter(this);
        }
    }

    private static abstract class AbsWaiter<T extends ThreadLatch> implements ThreadLatch.Waiter {

        protected final T latch;

        protected AbsWaiter(T latch) {
            this.latch = latch;
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
        public ThreadLatch.@Nonnull State state() {
            return latch.state();
        }
    }

    private static final class NoConsumerWaiter extends AbsWaiter<NoConsumerThreadLatch> {

        NoConsumerWaiter(NoConsumerThreadLatch latch) {
            super(latch);
        }

        @Override
        public void signal(Object o) {
        }
    }

    private static final class ConsumerWaiter extends AbsWaiter<ConsumerThreadLatch> {

        ConsumerWaiter(ConsumerThreadLatch latch) {
            super(latch);
        }

        @Override
        public void signal(Object o) {
            latch.consumer.accept(o);
        }
    }
}
