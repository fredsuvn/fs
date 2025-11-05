package space.sunqian.common;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.Kit;
import space.sunqian.common.base.exception.UnknownTypeException;
import space.sunqian.common.base.lang.EnumKit;
import space.sunqian.common.base.system.JvmKit;
import space.sunqian.common.runtime.reflect.ClassKit;

/**
 * Root class for KitVa.
 *
 * @author sunqian
 */
public class KitVa {

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
        if (highVersion > 8 && majorVersion >= highVersion) {
            className = serviceClass.getName() + "ImplByJ" + highVersion;
        } else {
            className = serviceClass.getName() + "Impl";
        }
        Class<?> cls = ClassKit.classForName(className, null);
        Enum<?> enumObj = EnumKit.findEnum(Kit.as(cls), "INST");
        if (enumObj == null) {
            throw new UnknownTypeException(className);
        }
        return Kit.as(enumObj);
    }
}
