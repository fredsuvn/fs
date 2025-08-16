package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.JdkDependent;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.system.JvmKit;
import xyz.sunqian.common.base.value.Var;
import xyz.sunqian.common.invoke.Invocable;
import xyz.sunqian.common.reflect.BytesClassLoader;
import xyz.sunqian.common.reflect.ClassKit;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JDK dynamic proxy implementation for {@link ProxyMaker}.
 * <p>
 * This implementation uses {@link Proxy} to implement proxy, and that means it only supports proxy interfaces.
 *
 * @author sunqian
 */
@ThreadSafe
public class JdkProxyMaker implements ProxyMaker {

    /**
     * An {@link Invocable} instance that does not support default method of java interface.
     */
    public static final @Nonnull Invocable UNSUPPORTED_DEFAULT_METHOD_INVOCABLE = (inst, args) -> {
        throw new JdkProxyException(new UnsupportedOperationException(
            "Current Java Runtime Environment does not support obtaining MethodHandle of default method: " +
                JvmKit.jvmDescription()
        ));
    };

    private static final @Nonnull Object @Nonnull [] EMPTY_ARGS = {};

    @Override
    public @Nonnull ProxyFactory make(
        @Nullable Class<?> proxiedClass,
        @Nonnull List<@Nonnull Class<?>> interfaces,
        @Nonnull ProxyHandler proxyHandler
    ) throws ProxyException {
        Class<?> proxyClass = Proxy.getProxyClass(
            new BytesClassLoader(),
            interfaces.toArray(new Class<?>[0])
        );
        Map<Class<?>, List<Method>> proxiableMethods = ProxyKit.getProxiableMethods(
            null,
            interfaces,
            proxyHandler
        );
        Map<Method, Var<ProxyInvoker>> proxiedMethods = new HashMap<>();
        Map<Method, Var<Invocable>> unproxiedMethods = new HashMap<>();
        for (Class<?> anInterface : interfaces) {
            Method[] methods = anInterface.getMethods();
            for (Method method : methods) {
                if (ClassKit.isStatic(method)) {
                    continue;
                }
                if (!proxiedMethods.containsKey(method) && proxyHandler.shouldProxyMethod(method)) {
                    proxiedMethods.put(method, Var.of(null));
                } else {
                    unproxiedMethods.put(method, Var.of(null));
                }
            }
        }
        InvocationHandler handler = makeInvocationHandler(
            proxyHandler,
            new HashMap<>(proxiedMethods),
            new HashMap<>(unproxiedMethods)
        );
        return new JdkProxyFactory(proxyClass, handler);
    }

    private static @Nonnull InvocationHandler makeInvocationHandler(
        @Nonnull ProxyHandler methodHandler,
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

        private JdkInvoker(Method method) throws Exception {
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

    private static final class JdkProxyFactory implements ProxyFactory {

        private final @Nonnull Class<?> proxyClass;
        private final @Nonnull InvocationHandler handler;

        private JdkProxyFactory(
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
        public @Nonnull Class<?> proxyClass() {
            return proxyClass;
        }
    }

    private static @Nonnull Invocable buildSuperInvoker(@Nonnull Method method) throws Exception {
        if (Modifier.isAbstract(method.getModifiers())) {
            return (inst, args) -> {
                throw new AbstractMethodError(method.toString());
            };
        }
        return getDefaultMethodInvocable(method);
    }

    private static @Nonnull Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception {
        Optional<MethodHandles.Lookup> lookupOpt = LookUp.getLookUp(method);
        return lookupOpt
            .map(lookup ->
                Jie.call(() -> lookup.unreflectSpecial(method, method.getDeclaringClass()), null)
            )
            .map(methodHandle -> Invocable.of(methodHandle, false))
            .orElse(UNSUPPORTED_DEFAULT_METHOD_INVOCABLE);
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
            super(cause);
        }
    }

    @JdkDependent
    private static final class LookUp {

        // ========  works on JDK 8 ======== {

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private static final @Nonnull Optional<Constructor<MethodHandles.Lookup>> csOpt =
            Optional.ofNullable(getLookUpConstructor());

        private static @Nullable Constructor<MethodHandles.Lookup> getLookUpConstructor() {
            return Jie.call(() -> {
                Constructor<MethodHandles.Lookup> c = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class);
                c.setAccessible(true);
                return c;
            }, null);
        }

        private static @Nonnull Optional<MethodHandles.Lookup> getLookUp(@Nonnull Method method) {
            return csOpt.map(c ->
                Jie.call(() -> c.newInstance(method.getDeclaringClass()), null)
            );
        }

        // } ========  works on JDK 8 ========

        // ========  works on JDK 9+ ======== {
        //
        // private static Optional<MethodHandles.Lookup> getLookUp(@Nonnull Method method) {
        //     return Optional.of(MethodHandles.lookup());
        // }

        // } ========  works on JDK 9+ ========
    }
}
