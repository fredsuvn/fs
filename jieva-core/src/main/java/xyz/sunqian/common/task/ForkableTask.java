package xyz.sunqian.common.task;

/**
 * The forkable task, which is a type of task that can divide itself into several subtasks, each subtask can be executed
 * in parallel on different threads, and finally results of all subtasks will be collected and merged in one final
 * result. It is typically executed in {@link ForkJoinExecutor}.
 *
 * @param <T> the result type of the task
 * @author sunqian
 */
public interface ForkableTask<T> {

    /**
     * Asynchronously execute this task in the executor this task is running in. This method is typically used for
     * subtasks that are divided from a task.
     */
    void fork();

    /**
     * Blocks current thread until this task is completed, returns the result of this task. This method is typically
     * used after {@link #fork()}.
     *
     * @return the result of this task
     */
    T join();
}
