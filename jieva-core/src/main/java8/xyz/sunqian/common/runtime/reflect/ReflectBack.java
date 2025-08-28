package xyz.sunqian.common.runtime.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.Type;

final class ReflectBack {

    static @Nullable Class<?> arrayClass(@Nonnull Type componentType) {
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
