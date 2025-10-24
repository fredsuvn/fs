package xyz.sunqian.common.base.thread;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Kit;
import xyz.sunqian.common.base.exception.AwaitingException;

import java.time.Duration;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

final class ThreadGateImpl implements ThreadGate {

    static @Nonnull ThreadGate newThreadGate() {
        return new ThreadGateImpl();
    }

    private static final int OPENED = 1;
    private static final int CLOSED = 0;

    private final @Nonnull Sync sync = new Sync();

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
        Kit.uncheck(
            () -> sync.acquireSharedInterruptibly(1),
            AwaitingException::new
        );
    }

    @Override
    public boolean await(long millis) throws AwaitingException {
        return Kit.uncheck(
            () -> sync.tryAcquireSharedNanos(1, millis * 1000000L),
            AwaitingException::new
        );
    }

    @Override
    public boolean await(@Nonnull Duration duration) throws AwaitingException {
        return Kit.uncheck(
            () -> sync.tryAcquireSharedNanos(1, duration.toNanos()),
            AwaitingException::new
        );
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
