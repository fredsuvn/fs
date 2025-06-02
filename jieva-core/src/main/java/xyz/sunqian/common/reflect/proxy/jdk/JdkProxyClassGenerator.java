package xyz.sunqian.common.reflect.proxy.jdk;

import xyz.sunqian.annotations.JdkDependent;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.BytesClassLoader;
import xyz.sunqian.common.reflect.proxy.ProxyBuilder;
import xyz.sunqian.common.reflect.proxy.ProxyClass;
import xyz.sunqian.common.reflect.proxy.ProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.ProxyException;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        Set<Method> methodSet = new LinkedHashSet<>();
        for (Class<?> anInterface : interfaces) {
            Method[] methods = anInterface.getMethods();
            for (Method method : methods) {
                if (methodSet.contains(method)) {
                    continue;
                }
                if (methodHandler.requiresProxy(method)) {
                    methodSet.add(method);
                }
            }
        }
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (!methodSet.contains(method)) {
                    return invokeMethod(proxy, method, args);
                }
                return null;
            }
        };
        return new JdkProxyClass(proxyClass, handler);
    }

    private static final class JdkInvoker implements ProxyInvoker {

        private final Method method;

        private JdkInvoker(Method method) {
            this.method = method;
        }

        @Override
        public @Nullable Object invoke(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return invokeMethod(inst, method, args);
        }

        @Override
        public @Nullable Object invokeSuper(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return null;
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

    @JdkDependent
    private static @Nullable Object invokeMethod(
        @Nonnull Object proxy,
        @Nonnull Method method,
        @Nullable Object @Nonnull [] args
    ) throws Throwable {
        try {
            return method.invoke(proxy, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
