package xyz.sunqian.common.base.exception;

import xyz.sunqian.annotations.Nullable;

/**
 * Utility class for exception.
 *
 * @author sunqian
 */
public class ExceptionKit {

    /**
     * Returns the message of the given throwable.
     *
     * @param throwable the given throwable
     * @return the message of the given throwable
     */
    public static @Nullable String getMessage(@Nullable Throwable throwable) {
        return throwable == null ? null : throwable.getMessage();
    }
}
