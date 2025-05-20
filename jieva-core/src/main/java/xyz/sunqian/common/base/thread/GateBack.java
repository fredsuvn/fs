package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

final class GateBack {

    static @Nonnull ThreadGate newThreadGate() {
        return new ThreadGateImpl();
    }

    private static final class ThreadGateImpl implements ThreadGate, AwaitingAdaptor {

        private static final int OPENED = 1;
        private static final int CLOSED = 0;

        private final Sync sync = new Sync();

        @Override
        public boolean isOpened() {
            return sync.currentState() == OPENED;
        }

        @Override
        public boolean isClosed() {
            return sync.currentState() == CLOSED;
        }

        @Override
        public void open() {
            sync.releaseShared(OPENED);
        }

        @Override
        public void close() {
            sync.releaseShared(CLOSED);
        }

        @Override
        public void await() throws AwaitingException {
            AwaitingAdaptor.super.await();
        }

        @Override
        public boolean await(long millis) throws AwaitingException {
            return AwaitingAdaptor.super.await(millis);
        }

        @Override
        public boolean await(@Nonnull Duration duration) throws AwaitingException {
            return AwaitingAdaptor.super.await(duration);
        }

        @Override
        public void awaitInterruptibly() throws Exception {
            sync.acquireSharedInterruptibly(1);
        }

        @Override
        public boolean awaitInterruptibly(long millis) throws Exception {
            return sync.tryAcquireSharedNanos(1, millis * 1000000L);
        }

        @Override
        public boolean awaitInterruptibly(@Nonnull Duration duration) throws Exception {
            return sync.tryAcquireSharedNanos(1, duration.toNanos());
        }

        private static final class Sync extends AbstractQueuedSynchronizer {

            private Sync() {
                setState(CLOSED);
            }

            private int currentState() {
                return getState();
            }

            protected int tryAcquireShared(int acquires) {
                return (getState() == OPENED) ? 1 : -1;
            }

            protected boolean tryReleaseShared(int releases) {
                setState(releases);
                return releases == OPENED;
            }
        }
    }
}
