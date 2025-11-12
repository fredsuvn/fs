package space.sunqian.common.runtime.reflect;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.FsLoader;

import java.lang.reflect.Type;

interface ArrayClassService {

    @Nonnull
    ArrayClassService INST = FsLoader.loadImplByJvm(ArrayClassService.class, 12);

    @Nullable
    Class<?> arrayClass(@Nonnull Type componentType);
}
