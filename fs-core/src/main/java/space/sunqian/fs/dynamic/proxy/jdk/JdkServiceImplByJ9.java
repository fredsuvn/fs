package space.sunqian.fs.dynamic.proxy.jdk;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.invoke.Invocable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

enum JdkServiceImplByJ9 implements JdkService {
    INST;

    @Override
    public @Nonnull Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.unreflectSpecial(method, method.getDeclaringClass());
        return Invocable.of(methodHandle, false);
    }
}
