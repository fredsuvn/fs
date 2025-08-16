package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The ProxyHandler is an interface used to handle proxy behavior, including determining whether a method needs to be
 * proxied ({@link #shouldProxyMethod(Method)}) and the specific behavior of the proxy method
 * ({@link #invoke(Object, Method, ProxyInvoker, Object...)}).
 * <p>
 * This interface is typically used for {@link ProxyMaker#make(Class, List, ProxyHandler)}.
 *
 * @author sunqian
 */
public interface ProxyHandler {

    /**
     * Returns whether the given method should be proxied.
     * <p>
     * Typically, the {@link ProxyMaker} invokes this method for each overrideable method of the superclass and
     * interfaces, only once. If there are methods with the same name and parameter types, {@link ProxyMaker} will only
     * pass the first one encountered.
     *
     * @param method the given method
     * @return whether the given method should be proxied
     */
    boolean shouldProxyMethod(@Nonnull Method method);

    /**
     * Defines the proxy behavior for the proxied method. When a proxied method is invoked on a proxy instance, it is
     * actually this method that gets invoked.
     *
     * @param proxy   the proxy instance
     * @param method  the proxied method
     * @param invoker the invoker to invoke the proxy method
     * @param args    the arguments of the invocation
     * @return the invocation result
     * @throws Throwable the bare exception thrown by the invocation, without any wrapping
     */
    @Nullable
    Object invoke(
        @Nonnull Object proxy,
        @Nonnull Method method,
        @Nonnull ProxyInvoker invoker,
        @Nullable Object @Nonnull ... args
    ) throws Throwable;
}
