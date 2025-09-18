package xyz.sunqian.common.runtime.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The ProxyHandler is an interface used to handle proxy behavior, including determining whether a method needs to be
 * proxied ({@link #needsProxy(Method)}) and the specific behavior of the proxy method
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
     * Typically, the {@link ProxyMaker} invokes this method for each proxiable method of the proxied class and
     * interfaces, only once. If there are methods with the same name and JVM descriptor, {@link ProxyMaker} only passes
     * the first one encountered. Note some implementations may not have this guarantee.
     *
     * @param method the given method
     * @return whether the given method should be proxied
     */
    boolean needsProxy(@Nonnull Method method);

    /**
     * Defines the proxy behavior for the proxied method. When a proxied method is invoked on a proxy instance, this
     * method will be called to handle the invocation.
     *
     * @param proxy   the proxy instance on which the method was invoked
     * @param method  the proxied method being invoked
     * @param invoker the invoker that can be used to invoke the original behavior (call {@code super})
     * @param args    the arguments for the method invocation
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
