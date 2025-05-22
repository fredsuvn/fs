package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieConfig;
import xyz.sunqian.common.base.JieSystem;
import xyz.sunqian.common.collect.JieCollect;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.invoke.InvocationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDK dynamic proxy ({@link Proxy}) implementation for {@link ProxyBuilder}.
 * <p>
 * This generator only supports interfaces inherited by subclass, and {@link ProxyClass#getProxyClass()} is
 * unsupported.
 * <p>
 * This generator doesn't support {@link #superClass(Class)}, {@link #isFinal(boolean)} and {@link #className(String)}.
 *
 * @author fredsuvn
 */
public class JdkDynamicProxyBuilder implements ProxyBuilder {

    private static final Object[] EMPTY_ARGS = {};

    private List<Class<?>> interfaces;
    private MethodProxyHandler handler;
    private ClassLoader classLoader;

    @Override
    public ProxyBuilder superClass(Class<?> superClass) {
        return this;
    }

    @Override
    public ProxyBuilder interfaces(List<Class<?>> interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    @Override
    public ProxyBuilder proxyHandler(MethodProxyHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public ProxyBuilder isFinal(boolean isFinal) {
        return this;
    }

    @Override
    public ProxyBuilder className(String className) {
        return this;
    }

    @Override
    public ProxyBuilder classLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    @Override
    public ProxyClass build() throws ProxyException {
        if (JieCollect.isEmpty(interfaces)) {
            throw new ProxyException("No interface to proxy.");
        }
        if (handler == null) {
            throw new ProxyException("Need method proxy handler.");
        }
        ClassLoader classLoader = this.classLoader;
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        return new JavaProxyClass(classLoader, interfaces.toArray(new Class[0]), handler);
    }

    private static final class JavaProxyClass implements ProxyClass {

        private final ClassLoader classLoader;
        private final Class<?>[] interfaces;
        private final MethodProxyHandler handler;

        private JavaProxyClass(ClassLoader classLoader, Class<?>[] interfaces, MethodProxyHandler handler) {
            this.classLoader = classLoader;
            this.interfaces = interfaces;
            this.handler = handler;
        }

        @Override
        public <T> T newInstance() throws ProxyException {
            try {
                InvocationHandler invocationHandler = new InvocationHandlerImpl(interfaces, handler);
                Object proxy = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
                return Jie.as(proxy);
            } catch (Exception e) {
                throw new ProxyException(e);
            }
        }

        @Override
        public @Nullable Class<?> getProxyClass() {
            return null;
        }
    }

    private static final class InvocationHandlerImpl implements InvocationHandler {

        private final MethodProxyHandler handler;
        private final Map<Method, JdkProxyInvoker> invokerMap = new HashMap<>();

        private InvocationHandlerImpl(Class<?>[] uppers, MethodProxyHandler handler) {
            this.handler = handler;
            for (Class<?> upper : uppers) {
                Method[] methods = upper.getMethods();
                for (Method method : methods) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        continue;
                    }
                    if (handler.needsProxy(method)) {
                        Invocable invocable = Invocable.of(method);
                        JdkProxyInvoker jdkInvoker = new JdkProxyInvoker(method, invocable);
                        invokerMap.put(method, jdkInvoker);
                    }
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
            JdkProxyInvoker invoker = invokerMap.get(method);
            if (invoker == null) {
                return invokeSuper0(method, proxy, args);
            }
            Object[] actualArgs = args == null ? EMPTY_ARGS : args;
            return handler.invoke(proxy, method, actualArgs, invoker);
        }
    }

    private static final class JdkProxyInvoker implements ProxyInvoker {

        private final Method method;
        private final Invocable invocable;

        private JdkProxyInvoker(Method method, Invocable invocable) {
            this.method = method;
            this.invocable = invocable;
        }

        @Override
        public @Nullable Object invoke(Object inst, Object[] args) throws Throwable {
            try {
                return invocable.invoke(inst, args);
            } catch (InvocationException e) {
                throw e.getCause();
            }
        }

        @Override
        public @Nullable Object invokeSuper(Object inst, Object[] args) throws Throwable {
            return invokeSuper0(method, inst, args);
        }
    }

    private static Object invokeSuper0(Method method, Object inst, Object[] args) throws Throwable {
        return invokeSuper0JdkVer8(method, inst, args);
    }

    private static Object invokeSuper0JdkVer8(Method method, Object inst, Object[] args) throws Throwable {
        if (method.isDefault()) {
            throw new ProxyException("Cannot invoke default method ("
                + method + ") in current versions: [jieva: "
                + JieConfig.version() + ", java: "
                + JieSystem.getJavaVersion() + "].");
        }
        throw new AbstractMethodError(method.toString());
    }
}
