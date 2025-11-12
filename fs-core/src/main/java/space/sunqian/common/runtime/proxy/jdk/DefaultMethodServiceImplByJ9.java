package space.sunqian.common.runtime.proxy.jdk;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.runtime.invoke.Invocable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

enum DefaultMethodServiceImplByJ9 implements DefaultMethodService {
    INST;

    @Override
    public @Nonnull Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle methodHandle = lookup.unreflectSpecial(method, method.getDeclaringClass());
        return Invocable.of(methodHandle, false);
    }
}
