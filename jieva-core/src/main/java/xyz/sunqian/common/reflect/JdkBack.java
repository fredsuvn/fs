package xyz.sunqian.common.reflect;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.Type;

final class JdkBack {

    static @Nullable Class<?> arrayClass(@Nonnull Type componentType) {
        Class<?> componentClass = JieType.toRuntimeClass(componentType);
        if (componentClass == null) {
            return null;
        }
        String name = JieClass.arrayClassName(componentClass);
        if (name == null) {
            return null;
        }
        return JieClass.classForName(name, componentClass.getClassLoader());
    }
}
