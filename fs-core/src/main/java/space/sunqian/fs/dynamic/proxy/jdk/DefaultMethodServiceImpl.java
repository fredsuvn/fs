package space.sunqian.fs.dynamic.proxy.jdk;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.invoke.Invocable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

enum DefaultMethodServiceImpl implements DefaultMethodService {
    INST;

    private Constructor<MethodHandles.Lookup> constructor;

    @Override
    public @Nonnull Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception {
        if (constructor == null) {
            Constructor<MethodHandles.Lookup> c = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class);
            c.setAccessible(true);
            constructor = c;
        }
        MethodHandles.Lookup lookup = constructor.newInstance(method.getDeclaringClass());
        MethodHandle methodHandle = lookup.unreflectSpecial(method, method.getDeclaringClass());
        return Invocable.of(methodHandle, false);
    }
}
