package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Method proxy handle for {@link ProxyClass}.
 * <p>
 * Each proxy instance has an associated method proxy handler. When the proxy instance is created, all non-final and
 * non-static method from {@link Class#getMethods()} will be passed into {@link #proxy(Method)}, to determine whether
 * current method should be proxied. If it returns {@code true}, the method will be proxied. Then when the method is
 * invoked on the proxy instance, {@link #invoke(Object, Method, Object[], ProxyInvoker)} will be invoked.
 *
 * @author fredsuvn
 */
public interface MethodProxyHandler {

    /**
     * Returns whether specified method should be proxied. This method only invoke once for each method to be checked.
     *
     * @param method specified method
     * @return whether specified method should be proxied
     */
    boolean proxy(Method method);

    /**
     * Processes a method invocation on a proxy instance and returns the result. This method will be invoked when a
     * method is invoked on a proxy instance that it is associated with.
     *
     * @param proxy   the proxy instance
     * @param method  the proxied method
     * @param args    invocation arguments
     * @param invoker an invoker to invoke proxied method (super) on given proxy or proxied instance
     * @return the result
     * @throws Throwable the bare exceptions thrown by the proxied method, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invoke(Object proxy, Method method, Object[] args, ProxyInvoker invoker) throws Throwable;
}
