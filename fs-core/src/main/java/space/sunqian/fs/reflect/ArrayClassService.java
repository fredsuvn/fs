package space.sunqian.fs.reflect;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.FsLoader;

import java.lang.reflect.Type;

interface ArrayClassService {

    @Nonnull
    ArrayClassService INST = FsLoader.loadImplByJvm(ArrayClassService.class, 12);

    @Nullable
    Class<?> arrayClass(@Nonnull Type componentType);
}
