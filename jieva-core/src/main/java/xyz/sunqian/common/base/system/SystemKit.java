package xyz.sunqian.common.base.system;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Kit;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utilities for system.
 *
 * @author sunqian
 */
public class SystemKit {

    /**
     * Returns system property of {@link SystemKeys#JAVA_VERSION}: Java Runtime Environment version.
     *
     * @return system property of {@link SystemKeys#JAVA_VERSION}
     */
    public static @Nonnull String getJavaVersion() {
        return System.getProperty(SystemKeys.JAVA_VERSION);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_VENDOR}: Java Runtime Environment vendor.
     *
     * @return system property of {@link SystemKeys#JAVA_VENDOR}
     */
    public static @Nonnull String getJavaVendor() {
        return System.getProperty(SystemKeys.JAVA_VENDOR);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_VENDOR_URL}: Java vendor URL.
     *
     * @return system property of {@link SystemKeys#JAVA_VENDOR_URL}
     */
    public static @Nonnull String getJavaVendorUrl() {
        return System.getProperty(SystemKeys.JAVA_VENDOR_URL);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_HOME}: Java installation directory.
     *
     * @return system property of {@link SystemKeys#JAVA_HOME}
     */
    public static @Nonnull String getJavaHome() {
        return System.getProperty(SystemKeys.JAVA_HOME);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_VM_SPECIFICATION_VERSION}: Java Virtual Machine specification
     * version.
     *
     * @return system property of {@link SystemKeys#JAVA_VM_SPECIFICATION_VERSION}
     */
    public static @Nonnull String getJavaVmSpecificationVersion() {
        return System.getProperty(SystemKeys.JAVA_VM_SPECIFICATION_VERSION);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_SPECIFICATION_MAINTENANCE_VERSION}: Java Runtime Environment
     * specification maintenance version.
     *
     * @return system property of {@link SystemKeys#JAVA_SPECIFICATION_MAINTENANCE_VERSION}
     */
    public static @Nonnull String getJavaSpecificationMaintenanceVersion() {
        return System.getProperty(SystemKeys.JAVA_SPECIFICATION_MAINTENANCE_VERSION);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_VM_SPECIFICATION_VENDOR}: Java Virtual Machine specification
     * vendor.
     *
     * @return system property of {@link SystemKeys#JAVA_VM_SPECIFICATION_VENDOR}
     */
    public static @Nonnull String getJavaVmSpecificationVendor() {
        return System.getProperty(SystemKeys.JAVA_VM_SPECIFICATION_VENDOR);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_VM_SPECIFICATION_NAME}: Java Virtual Machine specification
     * name.
     *
     * @return system property of {@link SystemKeys#JAVA_VM_SPECIFICATION_NAME}
     */
    public static @Nonnull String getJavaVmSpecificationName() {
        return System.getProperty(SystemKeys.JAVA_VM_SPECIFICATION_NAME);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_VM_VERSION}: Java Virtual Machine implementation version.
     *
     * @return system property of {@link SystemKeys#JAVA_VM_VERSION}
     */
    public static @Nonnull String getJavaVmVersion() {
        return System.getProperty(SystemKeys.JAVA_VM_VERSION);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_VM_VENDOR}: Java Virtual Machine implementation vendor.
     *
     * @return system property of {@link SystemKeys#JAVA_VM_VENDOR}
     */
    public static @Nonnull String getJavaVmVendor() {
        return System.getProperty(SystemKeys.JAVA_VM_VENDOR);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_VM_NAME}: Java Virtual Machine implementation name.
     *
     * @return system property of {@link SystemKeys#JAVA_VM_NAME}
     */
    public static @Nonnull String getJavaVmName() {
        return System.getProperty(SystemKeys.JAVA_VM_NAME);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_SPECIFICATION_VERSION}: Java Runtime Environment specification
     * version.
     *
     * @return system property of {@link SystemKeys#JAVA_SPECIFICATION_VERSION}
     */
    public static @Nonnull String getJavaSpecificationVersion() {
        return System.getProperty(SystemKeys.JAVA_SPECIFICATION_VERSION);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_SPECIFICATION_VENDOR}: Java Runtime Environment specification
     * vendor.
     *
     * @return system property of {@link SystemKeys#JAVA_SPECIFICATION_VENDOR}
     */
    public static @Nonnull String getJavaSpecificationVendor() {
        return System.getProperty(SystemKeys.JAVA_SPECIFICATION_VENDOR);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_SPECIFICATION_NAME}: Java Runtime Environment specification
     * name.
     *
     * @return system property of {@link SystemKeys#JAVA_SPECIFICATION_NAME}
     */
    public static @Nonnull String getJavaSpecificationName() {
        return System.getProperty(SystemKeys.JAVA_SPECIFICATION_NAME);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_CLASS_VERSION}: Java class format version number.
     *
     * @return system property of {@link SystemKeys#JAVA_CLASS_VERSION}
     */
    public static @Nonnull String getJavaClassVersion() {
        return System.getProperty(SystemKeys.JAVA_CLASS_VERSION);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_CLASS_PATH}: Java class path.
     *
     * @return system property of {@link SystemKeys#JAVA_CLASS_PATH}
     */
    public static @Nonnull String getJavaClassPath() {
        return System.getProperty(SystemKeys.JAVA_CLASS_PATH);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_LIBRARY_PATH}: List of paths to search when loading libraries.
     *
     * @return system property of {@link SystemKeys#JAVA_LIBRARY_PATH}
     */
    public static @Nonnull String getJavaLibraryPath() {
        return System.getProperty(SystemKeys.JAVA_LIBRARY_PATH);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_IO_TMPDIR}: Default temp file path.
     *
     * @return system property of {@link SystemKeys#JAVA_IO_TMPDIR}
     */
    public static @Nonnull String getJavaIOTmpdir() {
        return System.getProperty(SystemKeys.JAVA_IO_TMPDIR);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_COMPILER}: Name of JIT compiler to use.
     *
     * @return system property of {@link SystemKeys#JAVA_COMPILER}
     */
    public static @Nonnull String getJavaCompiler() {
        return System.getProperty(SystemKeys.JAVA_COMPILER);
    }

    /**
     * Returns system property of {@link SystemKeys#JAVA_EXT_DIRS}: Path of extension directory or directories
     * Deprecated. This property, and the mechanism which implements it, may be removed in a future release.
     *
     * @return system property of {@link SystemKeys#JAVA_EXT_DIRS}
     */
    public static @Nonnull String getJavaExtDirs() {
        return System.getProperty(SystemKeys.JAVA_EXT_DIRS);
    }

    /**
     * Returns system property of {@link SystemKeys#OS_NAME}: Operating system name.
     *
     * @return system property of {@link SystemKeys#OS_NAME}
     */
    public static @Nonnull String getOsName() {
        return System.getProperty(SystemKeys.OS_NAME);
    }

    /**
     * Returns system property of {@link SystemKeys#OS_ARCH}: Operating system architecture.
     *
     * @return system property of {@link SystemKeys#OS_ARCH}
     */
    public static @Nonnull String getOsArch() {
        return System.getProperty(SystemKeys.OS_ARCH);
    }

    /**
     * Returns system property of {@link SystemKeys#OS_VERSION}: Operating system version.
     *
     * @return system property of {@link SystemKeys#OS_VERSION}
     */
    public static @Nonnull String getOsVersion() {
        return System.getProperty(SystemKeys.OS_VERSION);
    }

    /**
     * Returns system property of {@link SystemKeys#FILE_SEPARATOR}: File separator ("/" on UNIX).
     *
     * @return system property of {@link SystemKeys#FILE_SEPARATOR}
     */
    public static @Nonnull String getFileSeparator() {
        return System.getProperty(SystemKeys.FILE_SEPARATOR);
    }

    /**
     * Returns system property of {@link SystemKeys#PATH_SEPARATOR}: Path separator (":" on UNIX).
     *
     * @return system property of {@link SystemKeys#PATH_SEPARATOR}
     */
    public static @Nonnull String getPathSeparator() {
        return System.getProperty(SystemKeys.PATH_SEPARATOR);
    }

    /**
     * Returns system property of {@link SystemKeys#LINE_SEPARATOR}: Line separator ("\n" on UNIX).
     *
     * @return system property of {@link SystemKeys#LINE_SEPARATOR}
     */
    public static @Nonnull String getLineSeparator() {
        return System.getProperty(SystemKeys.LINE_SEPARATOR);
    }

    /**
     * Returns system property of {@link SystemKeys#USER_NAME}: User's account name.
     *
     * @return system property of {@link SystemKeys#USER_NAME}
     */
    public static @Nonnull String getUserName() {
        return System.getProperty(SystemKeys.USER_NAME);
    }

    /**
     * Returns system property of {@link SystemKeys#USER_HOME}: User's home directory.
     *
     * @return system property of {@link SystemKeys#USER_HOME}
     */
    public static @Nonnull String getUserHome() {
        return System.getProperty(SystemKeys.USER_HOME);
    }

    /**
     * Returns system property of {@link SystemKeys#USER_DIR}: User's current working directory.
     *
     * @return system property of {@link SystemKeys#USER_DIR}
     */
    public static @Nonnull String getUserDir() {
        return System.getProperty(SystemKeys.USER_DIR);
    }

    /**
     * Returns system property of {@link SystemKeys#NATIVE_ENCODING}: Character encoding name derived from the host
     * environment and/or the user's settings.
     * <p>
     * Note this property is only available on {@code JDK17+}.
     *
     * @return system property of {@link SystemKeys#NATIVE_ENCODING}
     */
    public static @Nullable String getNativeEncoding() {
        return System.getProperty(SystemKeys.NATIVE_ENCODING);
    }

    /**
     * Returns system property of {@link SystemKeys#FILE_ENCODING}: JVM default encoding.
     * <p>
     * This is not a standard property.
     *
     * @return system property of {@link SystemKeys#FILE_ENCODING}
     */
    public static @Nullable String getFileEncoding() {
        return System.getProperty(SystemKeys.FILE_ENCODING);
    }

    /**
     * Returns an immutable copy of {@link System#getProperties()}.
     *
     * @return an immutable copy of {@link System#getProperties()}
     */
    public static @Nonnull @Immutable Map<String, String> getProperties() {
        return Kit.as(
            new LinkedHashMap<>(System.getProperties())
        );
    }
}
