package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This interface defines the proxy behavior for a proxied method.
 *
 * @author sunqian
 */
public interface ProxyMethodBehavior {

    /**
     * Executes the proxy behavior for the proxied method, with the specified arguments. This method will be invoked
     * when the proxy method is invoked on the proxy instance.
     *
     * @param proxy   the proxy instance
     * @param method  the proxied method
     * @param invoker the proxy method invoker associates to the specified method
     * @param args    the specified arguments
     * @return the execution result
     * @throws Throwable the bare exception thrown by the execution, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invoke(
        @Nonnull Object proxy,
        @Nonnull Method method,
        @Nonnull ProxyInvoker invoker,
        @Nullable Object @Nonnull ... args
    ) throws Throwable;
}
