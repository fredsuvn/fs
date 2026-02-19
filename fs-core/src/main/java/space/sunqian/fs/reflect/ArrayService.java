package space.sunqian.fs.reflect;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.lang.FsLoader;

import java.lang.reflect.Type;

interface ArrayService {

    @Nonnull
    ArrayService INST = FsLoader.loadImplByJvm(ArrayService.class, 12);

    @Nullable
    Class<?> arrayClass(@Nonnull Type componentType);
}
