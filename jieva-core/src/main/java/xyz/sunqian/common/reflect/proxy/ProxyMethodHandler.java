package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This interface is a handler that defines the proxy behavior for the proxied methods. It is typically used for
 * generating the proxy class.
 *
 * @author sunqian
 */
public interface ProxyMethodHandler {

    /**
     * Returns whether the given method should be proxied.
     * <p>
     * This method will be invoked once for each given method when generating the proxy class, and the given methods
     * typically come from {@link Class#getMethods()} but do not include static, final, or bridged.
     *
     * @param method the given method
     * @return whether the given method should be proxied
     */
    boolean requiresProxy(Method method);

    /**
     * Defines the proxy behavior for the proxied method. When a proxy method is invoked on a proxy instance, it is
     * actually this method that gets invoked.
     *
     * @param proxy   the proxy instance
     * @param method  the proxied method
     * @param invoker the invoker to invoke the proxy method
     * @param args    the arguments of invocation
     * @return the invocation result
     * @throws Throwable the bare exception thrown by the invocation, without any wrapping such as
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
