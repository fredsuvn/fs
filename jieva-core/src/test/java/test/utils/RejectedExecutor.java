package test.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RejectedExecutor implements ScheduledExecutorService {

    @NotNull
    @Override
    public ScheduledFuture<?> schedule(@NotNull Runnable command, long delay, @NotNull TimeUnit unit) {
        throw new RejectedExecutionException();
    }

    @NotNull
    @Override
    public <V> ScheduledFuture<V> schedule(@NotNull Callable<V> callable, long delay, @NotNull TimeUnit unit) {
        throw new RejectedExecutionException();
    }

    @NotNull
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(@NotNull Runnable command, long initialDelay, long period, @NotNull TimeUnit unit) {
        throw new RejectedExecutionException();
    }

    @NotNull
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(@NotNull Runnable command, long initialDelay, long delay, @NotNull TimeUnit unit) {
        throw new RejectedExecutionException();
    }

    @Override
    public void shutdown() {
    }

    @NotNull
    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        throw new RejectedExecutionException();
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Runnable task, T result) {
        throw new RejectedExecutionException();
    }

    @NotNull
    @Override
    public Future<?> submit(@NotNull Runnable task) {
        throw new RejectedExecutionException();
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new RejectedExecutionException();
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        throw new RejectedExecutionException();
    }

    @NotNull
    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new RejectedExecutionException();
    }

    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new RejectedExecutionException();
    }

    @Override
    public void execute(@NotNull Runnable command) {
        throw new RejectedExecutionException();
    }
}
