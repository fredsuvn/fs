package space.sunqian.common.runtime.proxy.jdk;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.KitVa;
import space.sunqian.common.runtime.invoke.Invocable;

import java.lang.reflect.Method;

interface DefaultMethodService {

    @Nonnull
    DefaultMethodService INST = KitVa.loadImplByJvm(DefaultMethodService.class, 9);

    @Nonnull
    Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception;
}
