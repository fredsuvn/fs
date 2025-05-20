package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;

import java.time.Duration;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

final class GateBack {

    static @Nonnull ThreadGate newThreadGate() {
        return new ThreadGateImpl();
    }

    private static final class ThreadGateImpl implements ThreadGate {

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
        public void awaitInterruptibly() throws InterruptedException {
            sync.acquireSharedInterruptibly(1);
        }

        @Override
        public boolean awaitInterruptibly(@Nonnull Duration duration) throws InterruptedException {
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
