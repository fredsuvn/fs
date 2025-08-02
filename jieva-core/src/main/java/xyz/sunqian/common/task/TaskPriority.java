package xyz.sunqian.common.task;

/**
 * Represents the priority of a task.
 *
 * @author sunqian
 */
public enum TaskPriority {

    HIGH(100),
    MEDIUM(0.5),
    LOW(0.1);

    final double value;

    TaskPriority(double value) {
        this.value = value;
    }
}
