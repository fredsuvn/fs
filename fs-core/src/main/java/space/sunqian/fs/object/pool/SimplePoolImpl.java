package space.sunqian.fs.object.pool;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class SimplePoolImpl<T> implements SimplePool<T> {

    private final int coreSize;
    private final int maxSize;
    private final long idleTimeoutMillis;
    private final @Nonnull Supplier<? extends @Nonnull T> supplier;
    private final @Nonnull Predicate<? super @Nonnull T> validator;
    private final @Nonnull Consumer<? super @Nonnull T> discarder;

    // objects
    private final @Nonnull Map<@Nonnull T, @Nonnull Status> idleMap = new IdentityHashMap<>();
    private final @Nonnull Map<@Nonnull T, @Nonnull Status> activeMap = new IdentityHashMap<>();
    private volatile int totalSize;
    // close state
    private volatile boolean closed = false;

    SimplePoolImpl(
        int coreSize, int maxSize, long idleTimeoutMillis,
        @Nonnull Supplier<? extends @Nonnull T> supplier,
        @Nonnull Predicate<? super @Nonnull T> validator,
        @Nonnull Consumer<? super @Nonnull T> discarder
    ) throws ObjectPoolException {

        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.idleTimeoutMillis = idleTimeoutMillis;
        this.supplier = supplier;
        this.validator = validator;
        this.discarder = discarder;

        // initialize core objects
        try {
            for (int i = 0; i < coreSize; i++) {
                T obj = supplier.get();
                Status status = new Status();
                idleMap.put(obj, status);
            }
            this.totalSize = idleMap.size();
        } catch (Exception e) {
            close();
        }
    }

    @Override
    public synchronized @Nullable T get() throws ObjectPoolException {

        checkClosed();

        try {
            // get one
            Iterator<Map.Entry<T, Status>> idleIt = idleMap.entrySet().iterator();
            while (idleIt.hasNext()) {
                Map.Entry<T, Status> entry = idleIt.next();
                T obj = entry.getKey();
                if (!validator.test(obj)) {
                    discarder.accept(obj);
                    idleIt.remove();
                    totalSize--;
                } else {
                    Status status = entry.getValue();
                    status.active();
                    activeMap.put(obj, status);
                    idleIt.remove();
                    return obj;
                }
            }

            // no idle objects available, try to create a new one
            if (totalSize < maxSize) {
                T obj = supplier.get();
                Status newStatus = new Status();
                activeMap.put(obj, newStatus);
                totalSize++;
                return obj;
            }
        } catch (Exception e) {
            close();
            throw new ObjectPoolException("Failed to get object from pool.", e);
        }

        // cannot create new object (reached max size), return null
        return null;
    }

    @Override
    public synchronized boolean release(@Nonnull T obj) throws ObjectPoolException {

        checkClosed();

        try {
            Status status = activeMap.remove(obj);
            if (status == null) {
                return false;
            }
            if (validator.test(obj)) {
                status.idle();
                idleMap.put(obj, status);
            } else {
                discarder.accept(obj);
                totalSize--;
            }
            return true;
        } catch (Exception e) {
            close();
            throw new ObjectPoolException("Failed to release object to pool.", e);
        }
    }

    @Override
    public synchronized void clean() throws ObjectPoolException {

        checkClosed();

        try {
            Iterator<Map.Entry<T, Status>> idleIt = idleMap.entrySet().iterator();
            while (idleIt.hasNext()) {
                Map.Entry<T, Status> entry = idleIt.next();
                T obj = entry.getKey();
                if (!validator.test(obj)) {
                    discarder.accept(obj);
                    idleIt.remove();
                    totalSize--;
                    continue;
                }
                if (totalSize > coreSize) {
                    Status status = entry.getValue();
                    if (status.isIdleTimeout()) {
                        discarder.accept(obj);
                        idleIt.remove();
                        totalSize--;
                    }
                }
            }
            if (totalSize < coreSize) {
                int newSize = coreSize - totalSize;
                for (int i = 0; i < newSize; i++) {
                    T obj = supplier.get();
                    Status newStatus = new Status();
                    idleMap.put(obj, newStatus);
                    totalSize++;
                }
            }
        } catch (Exception e) {
            close();
            throw new ObjectPoolException("Failed to clean pool.", e);
        }
    }

    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }

        try {
            Iterator<Map.Entry<T, Status>> idleIt = idleMap.entrySet().iterator();
            while (idleIt.hasNext()) {
                Map.Entry<T, Status> entry = idleIt.next();
                T obj = entry.getKey();
                try {
                    discarder.accept(obj);
                    idleIt.remove();
                    totalSize--;
                } catch (Exception e) {
                    // do nothing
                }
            }
        } finally {
            totalSize = 0;
            closed = true;
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public synchronized @Nonnull List<T> unreleasedObjects() {
        if (!closed) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>(totalSize);
        list.addAll(idleMap.keySet());
        list.addAll(activeMap.keySet());
        return list;
    }

    @Override
    public int size() {
        return totalSize;
    }

    @Override
    public synchronized int idleSize() {
        return idleMap.size();
    }

    @Override
    public synchronized int activeSize() {
        return activeMap.size();
    }

    private void checkClosed() throws ObjectPoolException {
        if (closed) {
            throw new ObjectPoolException("Pool is closed.");
        }
    }

    private final class Status {

        private volatile long lastReleaseTime = System.currentTimeMillis();

        public void active() {
            lastReleaseTime = -1;
        }

        public void idle() {
            lastReleaseTime = System.currentTimeMillis();
        }

        public boolean isIdleTimeout() {
            return lastReleaseTime + idleTimeoutMillis < System.currentTimeMillis();
        }
    }
}
