package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This interface represents an invoker of a proxy method. It provides direct invocation methods, including
 * {@link #invoke(Object, Object...)} and {@link #invokeSuper(Object, Object[])}, for the proxy method.
 *
 * @author sunqian
 */
public interface ProxyInvoker {

    /**
     * Invokes the method with the specified instance and arguments.
     * <p>
     * The invocation behavior of this method is equivalent to the JVM instructions: {@code INVOKEVIRTUAL} and
     * {@code INVOKEINTERFACE}. Therefore, this method does not expect a proxy instance to be passed in, which is the
     * first argument of {@link MethodProxyHandler#invoke(Object, Method, Object[], ProxyInvoker)}). Passing a proxy
     * instance may cause recursive invocations to the proxy method itself, eventually leading to stack overflow.
     *
     * @param inst the specified instance
     * @param args the specified arguments
     * @return the invocation result
     * @throws Throwable the bare exception thrown by the invocation, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invoke(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable;

    /**
     * Invokes the {@code super} method with the specified instance and arguments.
     * <p>
     * The invocation behavior of this method is equivalent to the JVM instruction: {@code INVOKESPECIAL}. Therefore,
     * this method expects a proxy instance to be passed in.
     *
     * @param inst the specified instance
     * @param args the specified arguments
     * @return the invocation result
     * @throws Throwable the bare exception thrown by the invocation, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invokeSuper(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable;
}
