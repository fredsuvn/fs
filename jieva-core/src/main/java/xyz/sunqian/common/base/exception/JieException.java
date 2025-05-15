package xyz.sunqian.common.base.exception;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.util.Objects;

/**
 * Static utility class for exception.
 *
 * @author sunqian
 */
public class JieException {

    /**
     * If the given throwable is an instance of the target type, returns the result of {@link Throwable#getCause()},
     * otherwise returns the given throwable.
     *
     * @param throwable the given throwable
     * @param type      the target type
     * @return the resolved cause
     */
    public static @Nullable Throwable getCauseIfTypeMatches(@Nonnull Throwable throwable, @Nonnull Class<?> type) {
        return Objects.equals(throwable.getClass(), type) ? throwable.getCause() : throwable;
    }
}
