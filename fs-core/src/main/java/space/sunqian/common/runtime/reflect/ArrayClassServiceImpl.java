package space.sunqian.common.runtime.reflect;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

import java.lang.reflect.Type;

enum ArrayClassServiceImpl implements ArrayClassService {
    INST;

    @Override
    public @Nullable Class<?> arrayClass(@Nonnull Type componentType) {
        Class<?> componentClass = TypeKit.toRuntimeClass(componentType);
        if (componentClass == null) {
            return null;
        }
        String name = ClassKit.arrayClassName(componentClass);
        if (name == null) {
            return null;
        }
        return ClassKit.classForName(name, componentClass.getClassLoader());
    }
}
