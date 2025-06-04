package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.JdkDependent;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.JieException;
import xyz.sunqian.common.base.value.Var;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.reflect.BytesClassLoader;
import xyz.sunqian.common.reflect.JieClass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    private static final Object[] EMPTY_ARGS = {};

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
        Map<Method, Var<Invocable>> unproxiedMethods = new HashMap<>();
        Map<Method, Var<ProxyInvoker>> proxiedMethods = new HashMap<>();
        for (Class<?> anInterface : interfaces) {
            Method[] methods = anInterface.getMethods();
            for (Method method : methods) {
                if (JieClass.isStatic(method)) {
                    continue;
                }
                if (!proxiedMethods.containsKey(method) && methodHandler.requiresProxy(method)) {
                    proxiedMethods.put(method, Var.of(null));
                } else {
                    unproxiedMethods.put(method, Var.of(null));
                }
            }
        }
        InvocationHandler handler = getInvocationHandler(
            methodHandler,
            new HashMap<>(proxiedMethods),
            new HashMap<>(unproxiedMethods)
        );
        return new JdkProxyClass(proxyClass, handler);
    }

    private static @Nonnull InvocationHandler getInvocationHandler(
        @Nonnull ProxyMethodHandler methodHandler,
        @Nonnull Map<@Nonnull Method, @Nonnull Var<ProxyInvoker>> proxiedMap,
        @Nonnull Map<@Nonnull Method, @Nonnull Var<Invocable>> unproxiedMap
    ) {
        return (proxy, method, args) -> {
            Object[] nonnullArgs = Jie.nonnull(args, EMPTY_ARGS);
            Var<ProxyInvoker> invokerVar = proxiedMap.get(method);
            if (invokerVar == null) {
                @Nonnull Var<Invocable> invocableVar = unproxiedMap.get(method);
                Invocable invocable = invocableVar.get();
                if (invocable == null) {
                    invocable = buildSuperInvoker(method);
                    invocableVar.set(invocable);
                }
                return invocable.invokeChecked(proxy, nonnullArgs);
            }
            @Nullable ProxyInvoker invoker = invokerVar.get();
            if (invoker == null) {
                invoker = new JdkInvoker(method);
                invokerVar.set(invoker);
            }
            return methodHandler.invoke(proxy, method, invoker, nonnullArgs);
        };
    }

    private static final class JdkInvoker implements ProxyInvoker {

        private final @Nonnull Invocable virtualInvoker;
        private final @Nonnull Invocable superInvoker;

        private JdkInvoker(Method method) throws Throwable {
            this.virtualInvoker = Invocable.of(method);
            // if (method.is)
            this.superInvoker = buildSuperInvoker(method);
        }

        @Override
        public @Nullable Object invoke(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return virtualInvoker.invokeChecked(inst, args);
        }

        @Override
        public @Nullable Object invokeSuper(@Nonnull Object inst, @Nullable Object @Nonnull ... args) throws Throwable {
            return superInvoker.invokeChecked(inst, args);
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

    private static @Nonnull Invocable buildSuperInvoker(@Nonnull Method method) throws Throwable {
        if (Modifier.isAbstract(method.getModifiers())) {
            return (inst, args) -> {
                throw new AbstractMethodError(method.toString());
            };
        }
        MethodHandle methodHandle = getMethodHandleForDefaultMethod(method);
        return Invocable.of(methodHandle, false);
    }

    @JdkDependent
    private static @Nonnull MethodHandle getMethodHandleForDefaultMethod(@Nonnull Method method) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        // works on JDK 8:
        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
            .getDeclaredConstructor(Class.class);
        constructor.setAccessible(true);
        MethodHandles.Lookup lookup = constructor.newInstance(declaringClass);
        return lookup.unreflectSpecial(method, declaringClass);
        // works on JDK 9+:
        // MethodHandle methodHandle = MethodHandles.lookup().unreflectSpecial(method, declaringClass);
    }
}
