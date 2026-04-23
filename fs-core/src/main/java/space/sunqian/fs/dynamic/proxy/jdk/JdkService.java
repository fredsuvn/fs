package space.sunqian.fs.dynamic.proxy.jdk;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.FsLoader;
import space.sunqian.fs.invoke.Invocable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

interface JdkService {

    @Nonnull
    JdkService INST = FsLoader.loadImplByJvm(JdkService.class, 9);

    @Nonnull
    Invocable getDefaultMethodInvocable(@Nonnull Method method) throws Exception;

    Class<?> getProxyClass(
        @Nonnull @RetainedParam List<@Nonnull Class<?>> interfaces,
        @Nonnull InvocationHandler invocationHandler
    ) throws IllegalArgumentException;
}
