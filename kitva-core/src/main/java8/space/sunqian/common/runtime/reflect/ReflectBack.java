package space.sunqian.common.runtime.reflect;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

import java.lang.reflect.Type;

final class ReflectBack {

    // ========  works on JDK 8 ======== {

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

    // } ========  works on JDK 8 ========

    // ========  works on JDK 12+ ======== {
    //
    // static @Nullable Class<?> arrayClass(@Nonnull Type componentType) {
    //     Class<?> componentClass = TypeKit.toRuntimeClass(componentType);
    //     if (componentClass == null) {
    //         return null;
    //     }
    //     return componentClass.arrayType();
    // }
    //
    // } ========  works on JDK 12+ ========
}
