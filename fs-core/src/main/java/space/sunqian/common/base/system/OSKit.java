package space.sunqian.common.base.system;

import space.sunqian.annotations.Nonnull;

/**
 * Utilities for OS.
 *
 * @author sunqian
 */
public class OSKit {

    /**
     * Returns whether the current OS is {@code Windows}.
     *
     * @return whether the current OS is {@code Windows}
     */
    public static boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    /**
     * Returns whether the current OS is {@code Linux}.
     *
     * @return whether the current OS is {@code Linux}
     */
    public static boolean isLinux() {
        return getOsName().toLowerCase().contains("linux");
    }

    /**
     * Returns whether the current OS is {@code macOS}.
     *
     * @return whether the current OS is {@code macOS}
     */
    public static boolean isMac() {
        return getOsName().toLowerCase().startsWith("mac");
    }

    /**
     * Returns whether the current OS is {@code AIX}.
     *
     * @return whether the current OS is {@code AIX}
     */
    public static boolean isAix() {
        return getOsName().contains("AIX");
    }

    /**
     * Returns whether the current OS is {@code HP-UX}.
     *
     * @return whether the current OS is {@code HP-UX}
     */
    public static boolean isHpUx() {
        return getOsName().contains("HP-UX");
    }

    /**
     * Returns whether the current OS is {@code Solaris}.
     *
     * @return whether the current OS is {@code Solaris}
     */
    public static boolean isSolaris() {
        return getOsName().contains("SunOS");
    }

    /**
     * Returns whether the current OS is {@code FreeBSD}.
     *
     * @return whether the current OS is {@code FreeBSD}
     */
    public static boolean isFreeBSD() {
        return getOsName().contains("FreeBSD");
    }

    /**
     * Returns whether the current OS is {@code OpenBSD}.
     *
     * @return whether the current OS is {@code OpenBSD}
     */
    public static boolean isOpenBSD() {
        return getOsName().contains("OpenBSD");
    }

    /**
     * Returns whether the current OS is {@code NetBSD}.
     *
     * @return whether the current OS is {@code NetBSD}
     */
    public static boolean isNetBSD() {
        return getOsName().contains("NetBSD");
    }

    /**
     * Returns whether the current OS is {@code z/OS}.
     *
     * @return whether the current OS is {@code z/OS}
     */
    public static boolean isZOS() {
        return getOsName().contains("z/OS");
    }

    private static @Nonnull String getOsName() {
        return SystemKit.getOsName();
    }

    private OSKit() {
    }
}
