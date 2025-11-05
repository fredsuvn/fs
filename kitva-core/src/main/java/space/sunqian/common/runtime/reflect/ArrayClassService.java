package space.sunqian.common.runtime.reflect;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.KitVa;

import java.lang.reflect.Type;

interface ArrayClassService {

    @Nonnull ArrayClassService INST = KitVa.loadServiceByJvm(ArrayClassService.class, 12);

    @Nullable
    Class<?> arrayClass(@Nonnull Type componentType);
}
