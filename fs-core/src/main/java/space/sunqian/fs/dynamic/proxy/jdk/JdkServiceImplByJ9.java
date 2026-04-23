package space.sunqian.fs.dynamic.proxy.jdk;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.dynamic.DynamicClassLoader;
import space.sunqian.fs.invoke.Invocable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

enum JdkServiceImplByJ9 implements JdkService {
    INST;

    @Override
    public @Nonnull Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.unreflectSpecial(method, method.getDeclaringClass());
        return Invocable.of(methodHandle, false);
    }

    @Override
    public Class<?> getProxyClass(
        @Nonnull @RetainedParam List<@Nonnull Class<?>> interfaces,
        @Nonnull InvocationHandler invocationHandler
    ) throws IllegalArgumentException {
        Class<?>[] interfaceClasses = interfaces.toArray(new Class<?>[0]);
        return Proxy.newProxyInstance(
            new DynamicClassLoader(),
            interfaceClasses,
            invocationHandler
        ).getClass();
    }
}
