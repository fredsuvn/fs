package space.sunqian.fs.reflect;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
enum ArrayServiceImplByJ12 implements ArrayService {
    INST;

    @Override
    public @Nullable Class<?> arrayClass(@Nonnull Type componentType) {
        Class<?> componentClass = TypeKit.toRuntimeClass(componentType);
        if (componentClass == null) {
            return null;
        }
        try {
            return componentClass.arrayType();
        } catch (Exception e) {
            return null;
        }
    }
}
