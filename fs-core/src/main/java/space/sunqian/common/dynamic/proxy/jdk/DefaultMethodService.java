package space.sunqian.common.dynamic.proxy.jdk;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.FsLoader;
import space.sunqian.common.invoke.Invocable;

import java.lang.reflect.Method;

interface DefaultMethodService {

    @Nonnull
    DefaultMethodService INST = FsLoader.loadImplByJvm(DefaultMethodService.class, 9);

    @Nonnull
    Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception;
}
