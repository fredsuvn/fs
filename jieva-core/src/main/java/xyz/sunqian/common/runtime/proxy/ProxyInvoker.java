package xyz.sunqian.common.runtime.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * This interface is used to directly invoke the proxy method by. An instance of this interface binds a proxy method,
 * and can directly invoke the method by{@link #invoke(Object, Object...)} and {@link #invokeSuper(Object, Object...)}.
 *
 * @author sunqian
 */
public interface ProxyInvoker {

    /**
     * Invokes the bound proxy method with the specified instance and arguments.
     * <p>
     * The invocation behavior of this method is equivalent to the JVM instructions: {@code INVOKEVIRTUAL} and
     * {@code INVOKEINTERFACE}. Therefore, this method does not expect a proxy instance to be passed in, which is the
     * first argument of {@link ProxyHandler#invoke(Object, Method, ProxyInvoker, Object...)}. Passing a proxy instance
     * may cause recursive invocations to the proxy method itself, eventually leading to stack overflow.
     *
     * @param inst the specified instance
     * @param args the specified arguments
     * @return the invocation result
     * @throws Throwable the bare exception thrown by the invocation, without any wrapping
     */
    @Nullable
    Object invoke(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable;

    /**
     * Invokes the {@code super} of the bound proxy method, which actually is the proxied method, with the specified
     * instance and arguments.
     * <p>
     * The invocation behavior of this method is equivalent to the JVM instruction: {@code INVOKESPECIAL}. Therefore,
     * this method expects a proxy instance to be passed in.
     *
     * @param inst the specified instance
     * @param args the specified arguments
     * @return the invocation result
     * @throws Throwable the bare exception thrown by the invocation, without any wrapping
     */
    @Nullable
    Object invokeSuper(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable;
}
