package internal.test;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

import java.io.PrintStream;

/**
 * This interface provides methods for printing test info. The default printer is {@link System#out}.
 *
 * @author sunqian
 */
public interface PrintTest {

    /**
     * Sets the printer for current thread. The printer may be {@code null}, int this case the printer will be set to
     * {@link System#out}, and it is the default printer.
     *
     * @param printer the printer
     */
    default void setPrinter(@Nullable PrintStream printer) {
        if (printer == null) {
            ThreadLocals.remove(ThreadLocals.Key.PRINTER);
        } else {
            ThreadLocals.set(ThreadLocals.Key.PRINTER, printer);
        }
    }

    /**
     * Prints the given message. Each element of the message array will be concatenated as one to print.
     *
     * @param message the given message
     */
    default void print(@Nullable Object @Nonnull ... message) {
        StringBuilder sb = new StringBuilder();
        for (Object o : message) {
            sb.append(o);
        }
        PrintStream printer = ThreadLocals.get(ThreadLocals.Key.PRINTER);
        printer = printer == null ? System.out : printer;
        printer.print(sb);
        printer.flush();
    }

    /**
     * Prints the given message and adds a line-separator at the tail. Each element of the message array will be
     * concatenated as one to print.
     *
     * @param message the given message
     */
    default void println(@Nullable Object @Nonnull ... message) {
        StringBuilder sb = new StringBuilder();
        for (Object o : message) {
            sb.append(o);
        }
        PrintStream printer = ThreadLocals.get(ThreadLocals.Key.PRINTER);
        printer = printer == null ? System.out : printer;
        printer.println(sb);
        printer.flush();
    }

    /**
     * Prints the content in this format: {@code title: message}, and adds a line-separator at the tail. Each element of
     * the message array will be concatenated as one to print.
     *
     * @param title   the given title
     * @param message the given message
     */
    default void printFor(@Nonnull String title, @Nullable Object @Nonnull ... message) {
        Object[] msg = new Object[message.length + 1];
        msg[0] = title + ": ";
        System.arraycopy(message, 0, msg, 1, message.length);
        println(msg);
    }
}
