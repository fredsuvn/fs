package space.sunqian.common.base.logging;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.date.DateKit;
import space.sunqian.common.base.thread.TraceKit;

import java.lang.reflect.Method;

final class SimpleLoggerImpl extends AbstractSimpleLogger {

    static @Nonnull SimpleLogger SYSTEM = new SimpleLoggerImpl(SimpleLogger.Level.INFO, System.out);

    private final @Nonnull Appendable appendable;

    SimpleLoggerImpl(@Nonnull Level level, @Nonnull Appendable appendable) {
        super(level);
        this.appendable = appendable;
    }

    @Override
    protected void log(@Nonnull SimpleLog log, @Nonnull Method method) {
        try {
            StackTraceElement caller = getCallerTrace(method, log.stackTrace());
            appendable.append(DateKit.format(log.timestamp()))
                .append("[")
                .append(log.level().name())
                .append("]");
            appendable.append("@")
                .append(caller.getClassName())
                .append(".")
                .append(caller.getMethodName())
                .append("(")
                .append(String.valueOf(caller.getLineNumber()))
                .append(")");
            appendable.append("-")
                .append("[")
                .append(log.thread().getName())
                .append("]: ");
            for (Object o : log.message()) {
                appendable.append(String.valueOf(o));
            }
            appendable.append(System.lineSeparator());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private @Nonnull StackTraceElement getCallerTrace(
        @Nonnull Method method,
        @Nonnull StackTraceElement[] stackElements
    ) {
        int i = -1;
        for (StackTraceElement element : stackElements) {
            i++;
            if (
                method.getDeclaringClass().getName().equals(element.getClassName())
                    && method.getName().equals(element.getMethodName())
            ) {
                break;
            }
        }
        if (i < 0 || i + 1 >= stackElements.length) {
            return TraceKit.EMPTY_FRAME;
        }
        return stackElements[i + 1];
    }
}
