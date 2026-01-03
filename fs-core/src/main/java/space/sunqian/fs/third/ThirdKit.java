package space.sunqian.fs.third;

import space.sunqian.annotation.Nonnull;

/**
 * Third-party utilities.
 *
 * @author sunqian
 */
public class ThirdKit {

    /**
     * Returns the fully qualified name of third-party utilities.
     *
     * @param subPackage      the sub-package name of third-party utilities
     * @param simpleClassName the simple class name of third-party utilities
     * @return the fully qualified name of third-party utilities
     */
    public static @Nonnull String thirdClassName(@Nonnull String subPackage, @Nonnull String simpleClassName) {
        return ThirdKit.class.getPackage().getName() + "." + subPackage + "." + simpleClassName;
    }

    private ThirdKit() {
    }
}
