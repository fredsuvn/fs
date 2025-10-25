package space.sunqian.common.runtime.proxy;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.system.JvmKit;
import space.sunqian.common.runtime.invoke.Invocable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

final class ProxyBack {

    // ========  works on JDK 8 ======== {

    /**
     * An {@link Invocable} instance that does not support default method of java interface.
     */
    private static final @Nonnull Invocable UNSUPPORTED_DEFAULT_METHOD_INVOCABLE = (inst, args) -> {
        throw new JdkProxyMaker.JdkProxyException(new UnsupportedOperationException(
            "Current Java Runtime Environment does not support obtaining MethodHandle of default method: " +
                JvmKit.jvmDescription()
        ));
    };

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static final @Nonnull Optional<Constructor<MethodHandles.Lookup>> csOpt =
        Optional.ofNullable(getLookUpConstructor());

    private static @Nullable Constructor<MethodHandles.Lookup> getLookUpConstructor() {
        return Kit.call(() -> {
            Constructor<MethodHandles.Lookup> c = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class);
            c.setAccessible(true);
            return c;
        }, null);
    }

    private static @Nonnull Optional<MethodHandles.Lookup> getLookUp(@Nonnull Method method) {
        return csOpt.map(c ->
            Kit.call(() -> c.newInstance(method.getDeclaringClass()), null)
        );
    }

    static @Nonnull Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception {
        Optional<MethodHandles.Lookup> lookupOpt = ProxyBack.getLookUp(method);
        return lookupOpt
            .map(lookup ->
                Kit.call(() -> lookup.unreflectSpecial(method, method.getDeclaringClass()), null)
            )
            .map(methodHandle -> Invocable.of(methodHandle, false))
            .orElse(UNSUPPORTED_DEFAULT_METHOD_INVOCABLE);
    }

    // } ========  works on JDK 8 ========

    // ========  works on JDK 9+ ======== {
    //
    // private static Optional<MethodHandles.Lookup> getLookUp(@Nonnull Method method) {
    //     return Optional.of(MethodHandles.lookup());
    // }
    //
    // } ========  works on JDK 9+ ========
}
