package space.sunqian.common.runtime.reflect;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.KitLoader;

import java.lang.reflect.Type;

interface ArrayClassService {

    @Nonnull
    ArrayClassService INST = KitLoader.loadImplByJvm(ArrayClassService.class, 12);

    @Nullable
    Class<?> arrayClass(@Nonnull Type componentType);
}
