package xyz.sunqian.common.base.system;

import xyz.sunqian.annotations.Nonnull;

/**
 * Provides keys of system properties. Most of them are standard keys which always have values, but some are not.
 *
 * @author sunqian
 * @see System#getProperties()
 * @see System#getProperty(String)
 */
public class SystemKeys {

    /**
     * java.version: Java Runtime Environment version.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_VERSION = "java.version";

    /**
     * java.vendor: Java Runtime Environment vendor.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_VENDOR = "java.vendor";

    /**
     * java.vendor.url: Java vendor URL.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_VENDOR_URL = "java.vendor.url";

    /**
     * java.home: Java installation directory.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_HOME = "java.home";

    /**
     * java.vm.specification.version: Java Virtual Machine specification version.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version";

    /**
     * java.specification.maintenance.version: Java Runtime Environment specification maintenance version.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_SPECIFICATION_MAINTENANCE_VERSION = "java.specification.maintenance.version";

    /**
     * java.vm.specification.vendor: Java Virtual Machine specification vendor.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";

    /**
     * java.vm.specification.name: Java Virtual Machine specification name.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name";

    /**
     * java.vm.version: Java Virtual Machine implementation version.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_VM_VERSION = "java.vm.version";

    /**
     * java.vm.vendor: Java Virtual Machine implementation vendor.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_VM_VENDOR = "java.vm.vendor";

    /**
     * java.vm.name: Java Virtual Machine implementation name.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_VM_NAME = "java.vm.name";

    /**
     * java.specification.version: Java Runtime Environment specification version.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_SPECIFICATION_VERSION = "java.specification.version";

    /**
     * java.specification.vendor: Java Runtime Environment specification vendor.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_SPECIFICATION_VENDOR = "java.specification.vendor";

    /**
     * java.specification.name: Java Runtime Environment specification name.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_SPECIFICATION_NAME = "java.specification.name";

    /**
     * java.class.version: Java class format version number.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_CLASS_VERSION = "java.class.version";

    /**
     * java.class.path: Java class path.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_CLASS_PATH = "java.class.path";

    /**
     * java.library.path: List of paths to search when loading libraries.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_LIBRARY_PATH = "java.library.path";

    /**
     * java.io.tmpdir: Default temp file path.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_IO_TMPDIR = "java.io.tmpdir";

    /**
     * java.compiler: Name of JIT compiler to use.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_COMPILER = "java.compiler";

    /**
     * java.ext.dirs: Path of extension directory or directories Deprecated. This property, and the mechanism which
     * implements it, may be removed in a future release.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String JAVA_EXT_DIRS = "java.ext.dirs";

    /**
     * os.name: Operating system name.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String OS_NAME = "os.name";

    /**
     * os.arch: Operating system architecture.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String OS_ARCH = "os.arch";

    /**
     * os.version: Operating system version.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String OS_VERSION = "os.version";

    /**
     * file.separator: File separator ("/" on UNIX).
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String FILE_SEPARATOR = "file.separator";

    /**
     * path.separator: Path separator (":" on UNIX).
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String PATH_SEPARATOR = "path.separator";

    /**
     * line.separator: Line separator ("\n" on UNIX).
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String LINE_SEPARATOR = "line.separator";

    /**
     * user.name: User's account name.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String USER_NAME = "user.name";

    /**
     * user.home: User's home directory.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String USER_HOME = "user.home";

    /**
     * user.dir: User's current working directory.
     * <p>
     * This is a standard key.
     */
    public static final @Nonnull String USER_DIR = "user.dir";

    /**
     * native.encoding: Character encoding name derived from the host environment and/or the user's settings. Setting
     * this system property has no effect.
     * <p>
     * This key is only available on {@code JDK17+}.
     */
    public static final @Nonnull String NATIVE_ENCODING = "native.encoding";

    /**
     * file.encoding: JVM default encoding.
     * <p>
     * This is not a standard key.
     */
    public static final @Nonnull String FILE_ENCODING = "file.encoding";
}
