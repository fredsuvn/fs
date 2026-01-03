package space.sunqian.fs.third;

import space.sunqian.annotation.Nonnull;

/**
 * Third-party utilities.
 *
 * @author sunqian
 */
public class ThirdKit {

    /**
     * Returns the package name of third-party utilities.
     *
     * @return the package name of third-party utilities
     */
    public static @Nonnull String thirdPackageName() {
        return ThirdKit.class.getPackage().getName();
    }

    private ThirdKit() {
    }
}
