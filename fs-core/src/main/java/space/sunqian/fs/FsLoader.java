package space.sunqian.fs;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.UnknownTypeException;
import space.sunqian.fs.base.lang.EnumKit;
import space.sunqian.fs.base.system.JvmKit;
import space.sunqian.fs.reflect.ClassKit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loader for implementations of this lib.
 *
 * @author sunqian
 */
public class FsLoader {

    /**
     * This method is used to load the corresponding version of service implementation based on the currently running
     * JVM platform for this lib.
     * <p>
     * This is a lib-internal method.
     *
     * @param serviceClass the service class
     * @param highVersion  the high version
     * @param <T>          the service type
     * @return the corresponding version of service implementation
     */
    public static <T> @Nonnull T loadImplByJvm(
        @Nonnull Class<T> serviceClass, int highVersion
    ) throws UnknownTypeException {
        int majorVersion = JvmKit.javaMajorVersion();
        String className;
        if (majorVersion > 8) {
            className = serviceClass.getName() + "ImplByJ" + highVersion;
            T ret = loadImplByJvm(className);
            if (ret != null) {
                return ret;
            }
        }
        className = serviceClass.getName() + "Impl";
        T ret = loadImplByJvm(className);
        if (ret != null) {
            return ret;
        }
        throw new UnknownTypeException(className);
    }

    private static <T> @Nullable T loadImplByJvm(String classImplName) {
        Class<?> cls = ClassKit.classForName(classImplName);
        Enum<?> enumObj = EnumKit.findEnum(Fs.as(cls), "INST");
        return Fs.as(enumObj);
    }

    /**
     * This method is used to load a class that depends on another class.
     * <p>
     * This is a lib-internal method.
     *
     * @param className          the name of the class that needs to be loaded
     * @param dependentClassName the name of the dependent class
     * @return the loaded class
     */
    public static @Nullable Class<?> loadClassByDependent(
        @Nonnull String className, @Nonnull String dependentClassName
    ) {
        Class<?> dependentClass = ClassKit.classForName(dependentClassName);
        if (dependentClass == null) {
            return null;
        }
        return ClassKit.classForName(className);
    }

    /**
     * This method is used to load the given objects. If the object is {@link Class}, loads it to an instance, else
     * loads the object itself. The {@code null} elements and {@code null} instanced (if loading fails) will be
     * ignored.
     * <p>
     * This is a lib-internal method.
     *
     * @param classesOrInstances the objects array that need to be loaded
     * @param <T>                the type of the instances
     * @return the instances
     */
    public static <T> @Nonnull List<@Nonnull T> loadInstances(@Nullable Object @Nonnull ... classesOrInstances) {
        ArrayList<T> list = new ArrayList<>(classesOrInstances.length);
        for (Object obj : classesOrInstances) {
            if (obj == null) {
                continue;
            }
            if (obj instanceof Class<?>) {
                Class<?> cls = (Class<?>) obj;
                Object o = ClassKit.newInstance(cls);
                if (o != null) {
                    list.add(Fs.as(o));
                }
            } else {
                list.add(Fs.as(obj));
            }
        }
        list.trimToSize();
        return Collections.unmodifiableList(list);
    }

    private FsLoader() {
    }
}
