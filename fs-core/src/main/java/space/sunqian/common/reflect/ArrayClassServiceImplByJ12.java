package space.sunqian.common.reflect;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
enum ArrayClassServiceImplByJ12 implements ArrayClassService {
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
