package space.sunqian.common;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.enums.EnumKit;
import space.sunqian.common.base.exception.UnknownTypeException;
import space.sunqian.common.base.system.JvmKit;
import space.sunqian.common.runtime.reflect.ClassKit;

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

    private FsLoader() {
    }
}
