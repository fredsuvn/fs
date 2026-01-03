package space.sunqian.fs.dynamic.proxy.jdk;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.FsLoader;
import space.sunqian.fs.invoke.Invocable;

import java.lang.reflect.Method;

interface DefaultMethodService {

    @Nonnull
    DefaultMethodService INST = FsLoader.loadImplByJvm(DefaultMethodService.class, 9);

    @Nonnull
    Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception;
}
