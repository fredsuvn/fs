package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This interfaces represents an invoker to help invoking method directly. Each instance associates a method to be
 * proxied.
 *
 * @author fredsuvn
 */
public interface ProxyInvoker {

    /**
     * Invokes method associated to this invoker with specified instance and arguments. The invocation behavior of this
     * method is equivalent to {@code INVOKEVIRTUAL} and {@code INVOKEINTERFACE} instructions. Therefore, this method
     * does not expect a proxy instance to be passed in, which is the first argument of
     * {@link MethodProxyHandler#invoke(Object, Method, Object[], ProxyInvoker)}). Passing a proxy instance may lead to
     * recursive invocations, eventually resulting in a stack overflow.
     *
     * @param inst given instance
     * @param args the arguments
     * @return result of invocation
     * @throws Throwable the bare exceptions thrown by the proxied method, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invoke(Object inst, Object[] args) throws Throwable;

    /**
     * Invokes proxied ({@code super}) method associated to this invoker with specified instance and arguments. The
     * invocation behavior of this method is equivalent to {@code INVOKESPECIAL}. Therefore, this method expects a proxy
     * instance to be passed in.
     *
     * @param inst given instance
     * @param args the arguments
     * @return result of invocation
     * @throws Throwable the bare exceptions thrown by the proxied method, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invokeSuper(Object inst, Object[] args) throws Throwable;
}
