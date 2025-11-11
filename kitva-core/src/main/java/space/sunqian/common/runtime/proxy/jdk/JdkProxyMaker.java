package space.sunqian.common.runtime.proxy.jdk;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.RetainedParam;
import space.sunqian.annotations.ThreadSafe;
import space.sunqian.common.Kit;
import space.sunqian.common.base.value.Var;
import space.sunqian.common.runtime.invoke.Invocable;
import space.sunqian.common.runtime.proxy.ProxyException;
import space.sunqian.common.runtime.proxy.ProxyHandler;
import space.sunqian.common.runtime.proxy.ProxyInvoker;
import space.sunqian.common.runtime.proxy.ProxyMaker;
import space.sunqian.common.runtime.proxy.ProxySpec;
import space.sunqian.common.runtime.reflect.BytesClassLoader;
import space.sunqian.common.runtime.reflect.ClassKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDK dynamic proxy implementation for {@link ProxyMaker}.
 * <p>
 * This implementation uses {@link Proxy} to implement proxy, and that means it only supports proxy interfaces.
 *
 * @author sunqian
 */
@ThreadSafe
public class JdkProxyMaker implements ProxyMaker {

    private static final @Nonnull Object @Nonnull [] EMPTY_ARGS = {};

    @Override
    public @Nonnull ProxySpec make(
        @Nullable Class<?> proxiedClass,
        @Nonnull @RetainedParam List<@Nonnull Class<?>> interfaces,
        @Nonnull ProxyHandler proxyHandler
    ) throws ProxyException {
        Class<?> proxyClass = Proxy.getProxyClass(
            new BytesClassLoader(),
            interfaces.toArray(new Class<?>[0])
        );
        Map<Method, Var<ProxyInvoker>> proxiedMethods = new HashMap<>();
        Map<Method, Var<Invocable>> unproxiedMethods = new HashMap<>();
        for (Class<?> anInterface : interfaces) {
            Method[] methods = anInterface.getMethods();
            for (Method method : methods) {
                if (ClassKit.isStatic(method)) {
                    continue;
                }
                if (!proxiedMethods.containsKey(method) && proxyHandler.needsProxy(method)) {
                    proxiedMethods.put(method, Var.of(null));
                } else {
                    unproxiedMethods.put(method, Var.of(null));
                }
            }
        }
        InvocationHandler invocationHandler = makeInvocationHandler(
            proxyHandler,
            new HashMap<>(proxiedMethods),
            new HashMap<>(unproxiedMethods)
        );
        return new JdkProxySpec(proxyClass, interfaces, proxyHandler, invocationHandler);
    }

    private static @Nonnull InvocationHandler makeInvocationHandler(
        @Nonnull ProxyHandler methodHandler,
        @Nonnull Map<@Nonnull Method, @Nonnull Var<ProxyInvoker>> proxiedMap,
        @Nonnull Map<@Nonnull Method, @Nonnull Var<Invocable>> unproxiedMap
    ) {
        return (proxy, method, args) -> {
            Object[] nonnullArgs = Kit.nonnull(args, EMPTY_ARGS);
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

    private static final class JdkProxySpec implements ProxySpec {

        private final @Nonnull Class<?> proxyClass;
        private final @Nonnull List<@Nonnull Class<?>> proxiedInterfaces;
        private final @Nonnull ProxyHandler proxyHandler;
        private final @Nonnull InvocationHandler invocationHandler;

        private JdkProxySpec(
            @Nonnull Class<?> proxyClass,
            @Nonnull List<@Nonnull Class<?>> proxiedInterfaces,
            @Nonnull ProxyHandler proxyHandler,
            @Nonnull InvocationHandler invocationHandler
        ) {
            this.proxyClass = proxyClass;
            this.proxiedInterfaces = proxiedInterfaces;
            this.proxyHandler = proxyHandler;
            this.invocationHandler = invocationHandler;
        }

        @Override
        public <T> @Nonnull T newInstance() throws JdkProxyException {
            return Kit.uncheck(() -> {
                Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
                return Kit.as(constructor.newInstance(invocationHandler));
            }, JdkProxyException::new);
        }

        @Override
        public @Nonnull Class<?> proxyClass() {
            return proxyClass;
        }

        @Override
        public @Nonnull Class<?> proxiedClass() {
            return Object.class;
        }

        @Override
        public @Nonnull List<@Nonnull Class<?>> proxiedInterfaces() {
            return proxiedInterfaces;
        }

        @Override
        public @Nonnull ProxyHandler proxyHandler() {
            return proxyHandler;
        }
    }

    private static @Nonnull Invocable buildSuperInvoker(@Nonnull Method method) throws Exception {
        if (Modifier.isAbstract(method.getModifiers())) {
            return (inst, args) -> {
                throw new AbstractMethodError(method.toString());
            };
        }
        return DefaultMethodService.INST.getDefaultMethodInvocable(method);
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
}
