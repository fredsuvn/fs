package space.sunqian.fs.dynamic.proxy.jdk;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.lang.FsLoader;
import space.sunqian.fs.invoke.Invocable;

import java.lang.reflect.Method;

interface JdkService {

    @Nonnull
    JdkService INST = FsLoader.loadImplByJvm(JdkService.class, 9);

    @Nonnull
    Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception;
}
