package space.sunqian.fs.reflect;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

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
