package space.sunqian.fs.base.exception;

import space.sunqian.annotation.Nonnull;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utilities for {@link Throwable}.
 *
 * @author sunqian
 */
public class ThrowKit {

    /**
     * Returns the stack trace info of the given throwable as string. The content of the trace info equals to the
     * content of {@link Throwable#printStackTrace()}.
     *
     * @param throwable the given throwable
     * @return the stack trace info of the given throwable as string
     */
    public static @Nonnull String toString(@Nonnull Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private ThrowKit() {
    }
}
