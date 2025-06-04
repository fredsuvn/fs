package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.JdkDependent;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.JieException;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.base.value.Var;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.reflect.BytesClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDK dynamic proxy implementation for {@link ProxyBuilder}, via {@link Proxy}.
 * <p>
 * This generator uses {@link Proxy} to generate proxy class, and that means it only supports proxy interfaces.
 *
 * @author sunqian
 */
public class JdkProxyClassGenerator implements ProxyClassGenerator {

    @Override
    public @Nonnull ProxyClass generate(
        @Nullable Class<?> proxiedClass,
        @Nonnull List<Class<?>> interfaces,
        @Nonnull ProxyMethodHandler methodHandler
    ) throws ProxyException {
        Class<?> proxyClass = Proxy.getProxyClass(
            new BytesClassLoader(),
            interfaces.toArray(new Class<?>[0])
        );
        Map<Method, Var<ProxyInvoker>> methodMap = new HashMap<>();
        for (Class<?> anInterface : interfaces) {
            Method[] methods = anInterface.getMethods();
            for (Method method : methods) {
                if (methodMap.containsKey(method)) {
                    continue;
                }
                if (methodHandler.requiresProxy(method)) {
                    methodMap.put(method, Var.of(null));
                }
            }
        }
        InvocationHandler handler = (proxy, method, args) -> {
            Var<ProxyInvoker> invokerVar = methodMap.get(method);
            if (invokerVar == null) {
                return invokeSpecial(proxy, method, args);
            }
            @Nullable ProxyInvoker invoker = invokerVar.get();
            if (invoker == null) {
                invoker = new JdkInvoker(method);
                invokerVar.set(invoker);
            }
            return methodHandler.invoke(proxy, method, invoker, args);
        };
        return new JdkProxyClass(proxyClass, handler);
    }

    private static final class JdkInvoker implements ProxyInvoker {

        private final @Nonnull Method method;
        private final @Nonnull Invocable invocable;

        private JdkInvoker(Method method) {
            this.method = method;
            this.invocable = Invocable.of(method);
        }

        @Override
        public @Nullable Object invoke(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return invocable.invokeChecked(inst, args);
        }

        @Override
        public @Nullable Object invokeSuper(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return invokeSpecial(inst, method, args);
        }
    }

    private static final class JdkProxyClass implements ProxyClass {

        private final @Nonnull Class<?> proxyClass;
        private final @Nonnull InvocationHandler handler;

        private JdkProxyClass(
            @Nonnull Class<?> proxyClass,
            @Nonnull InvocationHandler handler
        ) {
            this.proxyClass = proxyClass;
            this.handler = handler;
        }

        @Override
        public <T> @Nonnull T newInstance() throws JdkProxyException {
            return Jie.uncheck(() -> {
                Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
                return Jie.as(constructor.newInstance(handler));
            }, JdkProxyException::new);
        }

        @Override
        public @Nonnull Class<?> getProxyClass() {
            return proxyClass;
        }
    }

    /**
     * This exception is the sub-exception of {@link ProxyException} for JDK dynamic proxy implementation.
     *
     * @author sunqian
     */
    public static class JdkProxyException extends ProxyException {
        /**
         * Constructs with the cause.
         *
         * @param cause the cause
         */
        public JdkProxyException(@Nullable Throwable cause) {
            super(JieException.getMessage(cause), cause);
        }
    }

    @JdkDependent
    private static @Nullable Object invokeSpecial(
        @Nonnull Object proxy, @Nonnull Method method, @Nullable Object @Nonnull [] args
    ) throws Throwable {
        try {
            return method.invoke(proxy, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
