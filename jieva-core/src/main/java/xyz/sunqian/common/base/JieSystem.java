package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.string.StringKit;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * System utilities
 *
 * @author fredsuvn
 */
public class JieSystem {

    /**
     * Key of system property: java.version.
     */
    public static final String KEY_OF_JAVA_VERSION = "java.version";
    /**
     * Key of system property: java.vendor.
     */
    public static final String KEY_OF_JAVA_VENDOR = "java.vendor";
    /**
     * Key of system property: java.vendor.url.
     */
    public static final String KEY_OF_JAVA_VENDOR_URL = "java.vendor.url";
    /**
     * Key of system property: java.home.
     */
    public static final String KEY_OF_JAVA_HOME = "java.home";
    /**
     * Key of system property: java.vm.specification.version.
     */
    public static final String KEY_OF_JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version";
    /**
     * Key of system property: java.vm.specification.vendor.
     */
    public static final String KEY_OF_JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";
    /**
     * Key of system property: java.vm.specification.name.
     */
    public static final String KEY_OF_JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name";
    /**
     * Key of system property: java.vm.version.
     */
    public static final String KEY_OF_JAVA_VM_VERSION = "java.vm.version";
    /**
     * Key of system property: java.vm.vendor.
     */
    public static final String KEY_OF_JAVA_VM_VENDOR = "java.vm.vendor";
    /**
     * Key of system property: java.vm.name.
     */
    public static final String KEY_OF_JAVA_VM_NAME = "java.vm.name";
    /**
     * Key of system property: java.specification.version.
     */
    public static final String KEY_OF_JAVA_SPECIFICATION_VERSION = "java.specification.version";
    /**
     * Key of system property: java.specification.vendor.
     */
    public static final String KEY_OF_JAVA_SPECIFICATION_VENDOR = "java.specification.vendor";
    /**
     * Key of system property: java.specification.name.
     */
    public static final String KEY_OF_JAVA_SPECIFICATION_NAME = "java.specification.name";
    /**
     * Key of system property: java.class.version.
     */
    public static final String KEY_OF_JAVA_CLASS_VERSION = "java.class.version";
    /**
     * Key of system property: java.class.path.
     */
    public static final String KEY_OF_JAVA_CLASS_PATH = "java.class.path";
    /**
     * Key of system property: java.library.path.
     */
    public static final String KEY_OF_JAVA_LIBRARY_PATH = "java.library.path";
    /**
     * Key of system property: java.io.tmpdir.
     */
    public static final String KEY_OF_JAVA_IO_TMPDIR = "java.io.tmpdir";
    /**
     * Key of system property: java.compiler.
     */
    public static final String KEY_OF_JAVA_COMPILER = "java.compiler";
    /**
     * Key of system property: java.ext.dirs.
     */
    public static final String KEY_OF_JAVA_EXT_DIRS = "java.ext.dirs";
    /**
     * Key of system property: os.name.
     */
    public static final String KEY_OF_OS_NAME = "os.name";
    /**
     * Key of system property: os.arch.
     */
    public static final String KEY_OF_OS_ARCH = "os.arch";
    /**
     * Key of system property: os.version.
     */
    public static final String KEY_OF_OS_VERSION = "os.version";
    /**
     * Key of system property: file.separator.
     */
    public static final String KEY_OF_FILE_SEPARATOR = "file.separator";
    /**
     * Key of system property: path.separator.
     */
    public static final String KEY_OF_PATH_SEPARATOR = "path.separator";
    /**
     * Key of system property: line.separator.
     */
    public static final String KEY_OF_LINE_SEPARATOR = "line.separator";
    /**
     * Key of system property: user.name.
     */
    public static final String KEY_OF_USER_NAME = "user.name";
    /**
     * Key of system property: user.home.
     */
    public static final String KEY_OF_USER_HOME = "user.home";
    /**
     * Key of system property: user.dir.
     */
    public static final String KEY_OF_USER_DIR = "user.dir";
    /**
     * Key of system property: file.encoding.
     */
    public static final String KEY_OF_FILE_ENCODING = "file.encoding";

    // Add on JDK17:
    /**
     * Key of system property: native.encoding.
     */
    public static final String KEY_OF_NATIVE_ENCODING = "native.encoding";

    /**
     * Returns system property of {@link #KEY_OF_JAVA_VERSION}.
     *
     * @return system property of {@link #KEY_OF_JAVA_VERSION}
     */
    @Nullable
    public static String getJavaVersion() {
        return System.getProperty(KEY_OF_JAVA_VERSION);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_VENDOR}.
     *
     * @return system property of {@link #KEY_OF_JAVA_VENDOR}
     */
    @Nullable
    public static String getJavaVendor() {
        return System.getProperty(KEY_OF_JAVA_VENDOR);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_VENDOR_URL}.
     *
     * @return system property of {@link #KEY_OF_JAVA_VENDOR_URL}
     */
    @Nullable
    public static String getJavaVendorUrl() {
        return System.getProperty(KEY_OF_JAVA_VENDOR_URL);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_HOME}.
     *
     * @return system property of {@link #KEY_OF_JAVA_HOME}
     */
    @Nullable
    public static String getJavaHome() {
        return System.getProperty(KEY_OF_JAVA_HOME);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_VM_SPECIFICATION_VERSION}.
     *
     * @return system property of {@link #KEY_OF_JAVA_VM_SPECIFICATION_VERSION}
     */
    @Nullable
    public static String getJavaVmSpecificationVersion() {
        return System.getProperty(KEY_OF_JAVA_VM_SPECIFICATION_VERSION);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_VM_SPECIFICATION_VENDOR}.
     *
     * @return system property of {@link #KEY_OF_JAVA_VM_SPECIFICATION_VENDOR}
     */
    @Nullable
    public static String getJavaVmSpecificationVendor() {
        return System.getProperty(KEY_OF_JAVA_VM_SPECIFICATION_VENDOR);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_VM_SPECIFICATION_NAME}.
     *
     * @return system property of {@link #KEY_OF_JAVA_VM_SPECIFICATION_NAME}
     */
    @Nullable
    public static String getJavaVmSpecificationName() {
        return System.getProperty(KEY_OF_JAVA_VM_SPECIFICATION_NAME);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_VM_VERSION}.
     *
     * @return system property of {@link #KEY_OF_JAVA_VM_VERSION}
     */
    @Nullable
    public static String getJavaVmVersion() {
        return System.getProperty(KEY_OF_JAVA_VM_VERSION);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_VM_VENDOR}.
     *
     * @return system property of {@link #KEY_OF_JAVA_VM_VENDOR}
     */
    @Nullable
    public static String getJavaVmVendor() {
        return System.getProperty(KEY_OF_JAVA_VM_VENDOR);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_VM_NAME}.
     *
     * @return system property of {@link #KEY_OF_JAVA_VM_NAME}
     */
    @Nullable
    public static String getJavaVmName() {
        return System.getProperty(KEY_OF_JAVA_VM_NAME);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_SPECIFICATION_VERSION}.
     *
     * @return system property of {@link #KEY_OF_JAVA_SPECIFICATION_VERSION}
     */
    @Nullable
    public static String getJavaSpecificationVersion() {
        return System.getProperty(KEY_OF_JAVA_SPECIFICATION_VERSION);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_SPECIFICATION_VENDOR}.
     *
     * @return system property of {@link #KEY_OF_JAVA_SPECIFICATION_VENDOR}
     */
    @Nullable
    public static String getJavaSpecificationVendor() {
        return System.getProperty(KEY_OF_JAVA_SPECIFICATION_VENDOR);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_SPECIFICATION_NAME}.
     *
     * @return system property of {@link #KEY_OF_JAVA_SPECIFICATION_NAME}
     */
    @Nullable
    public static String getJavaSpecificationName() {
        return System.getProperty(KEY_OF_JAVA_SPECIFICATION_NAME);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_CLASS_VERSION}.
     *
     * @return system property of {@link #KEY_OF_JAVA_CLASS_VERSION}
     */
    @Nullable
    public static String getJavaClassVersion() {
        return System.getProperty(KEY_OF_JAVA_CLASS_VERSION);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_CLASS_PATH}.
     *
     * @return system property of {@link #KEY_OF_JAVA_CLASS_PATH}
     */
    @Nullable
    public static String getJavaClassPath() {
        return System.getProperty(KEY_OF_JAVA_CLASS_PATH);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_LIBRARY_PATH}.
     *
     * @return system property of {@link #KEY_OF_JAVA_LIBRARY_PATH}
     */
    @Nullable
    public static String getJavaLibraryPath() {
        return System.getProperty(KEY_OF_JAVA_LIBRARY_PATH);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_IO_TMPDIR}.
     *
     * @return system property of {@link #KEY_OF_JAVA_IO_TMPDIR}
     */
    @Nullable
    public static String getJavaIoTmpdir() {
        return System.getProperty(KEY_OF_JAVA_IO_TMPDIR);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_COMPILER}.
     *
     * @return system property of {@link #KEY_OF_JAVA_COMPILER}
     */
    @Nullable
    public static String getJavaCompiler() {
        return System.getProperty(KEY_OF_JAVA_COMPILER);
    }

    /**
     * Returns system property of {@link #KEY_OF_JAVA_EXT_DIRS}.
     *
     * @return system property of {@link #KEY_OF_JAVA_EXT_DIRS}
     */
    @Nullable
    public static String getJavaExtDirs() {
        return System.getProperty(KEY_OF_JAVA_EXT_DIRS);
    }

    /**
     * Returns system property of {@link #KEY_OF_OS_NAME}.
     *
     * @return system property of {@link #KEY_OF_OS_NAME}
     */
    @Nullable
    public static String getOsName() {
        return System.getProperty(KEY_OF_OS_NAME);
    }

    /**
     * Returns system property of {@link #KEY_OF_OS_ARCH}.
     *
     * @return system property of {@link #KEY_OF_OS_ARCH}
     */
    @Nullable
    public static String getOsArch() {
        return System.getProperty(KEY_OF_OS_ARCH);
    }

    /**
     * Returns system property of {@link #KEY_OF_OS_VERSION}.
     *
     * @return system property of {@link #KEY_OF_OS_VERSION}
     */
    @Nullable
    public static String getOsVersion() {
        return System.getProperty(KEY_OF_OS_VERSION);
    }

    /**
     * Returns system property of {@link #KEY_OF_FILE_SEPARATOR}.
     *
     * @return system property of {@link #KEY_OF_FILE_SEPARATOR}
     */
    @Nullable
    public static String getFileSeparator() {
        return System.getProperty(KEY_OF_FILE_SEPARATOR);
    }

    /**
     * Returns system property of {@link #KEY_OF_PATH_SEPARATOR}.
     *
     * @return system property of {@link #KEY_OF_PATH_SEPARATOR}
     */
    @Nullable
    public static String getPathSeparator() {
        return System.getProperty(KEY_OF_PATH_SEPARATOR);
    }

    /**
     * Returns system property of {@link #KEY_OF_LINE_SEPARATOR}.
     *
     * @return system property of {@link #KEY_OF_LINE_SEPARATOR}
     */
    @Nullable
    public static String getLineSeparator() {
        return System.getProperty(KEY_OF_LINE_SEPARATOR);
    }

    /**
     * Returns system property of {@link #KEY_OF_USER_NAME}.
     *
     * @return system property of {@link #KEY_OF_USER_NAME}
     */
    @Nullable
    public static String getUserName() {
        return System.getProperty(KEY_OF_USER_NAME);
    }

    /**
     * Returns system property of {@link #KEY_OF_USER_HOME}.
     *
     * @return system property of {@link #KEY_OF_USER_HOME}
     */
    @Nullable
    public static String getUserHome() {
        return System.getProperty(KEY_OF_USER_HOME);
    }

    /**
     * Returns system property of {@link #KEY_OF_USER_DIR}.
     *
     * @return system property of {@link #KEY_OF_USER_DIR}
     */
    @Nullable
    public static String getUserDir() {
        return System.getProperty(KEY_OF_USER_DIR);
    }

    /**
     * Returns system property of {@link #KEY_OF_FILE_ENCODING}.
     *
     * @return system property of {@link #KEY_OF_FILE_ENCODING}
     */
    @Nullable
    public static String getFileEncoding() {
        return System.getProperty(KEY_OF_FILE_ENCODING);
    }

    /**
     * Returns system property of {@link #KEY_OF_NATIVE_ENCODING}.
     *
     * @return system property of {@link #KEY_OF_NATIVE_ENCODING}
     */
    @Nullable
    public static String getNativeEncoding() {
        return System.getProperty(KEY_OF_NATIVE_ENCODING);
    }

    /**
     * Reads all system properties into a new map and returns.
     *
     * @return all system properties
     */
    public static Map<String, String> allProperties() {
        return Jie.as(
            new LinkedHashMap<>(System.getProperties())
        );
    }

    /**
     * Returns whether current OS is Windows.
     *
     * @return whether current OS is Windows
     */
    public static boolean isWindows() {
        String osName = getOsName();
        return osName != null && osName.startsWith("Windows");
    }

    /**
     * Returns whether current OS is Linux.
     *
     * @return whether current OS is Linux
     */
    public static boolean isLinux() {
        String osName = getOsName();
        return osName != null && (osName.startsWith("Linux") || osName.startsWith("LINUX"));
    }

    /**
     * Returns whether current OS is Mac.
     *
     * @return whether current OS is Mac
     */
    public static boolean isMac() {
        String osName = getOsName();
        return osName != null && (osName.startsWith("Mac"));
    }

    /**
     * Returns whether current OS is BSD (FreeBSD, OpenBSD or NetBSD).
     *
     * @return whether current OS is BSD (FreeBSD, OpenBSD or NetBSD)
     */
    public static boolean isBsd() {
        String osName = getOsName();
        return osName != null && (
            osName.startsWith("FreeBSD") || osName.startsWith("OpenBSD") || osName.startsWith("NetBSD")
        );
    }

    /**
     * Gets java major version.
     * <p>
     * If java version &lt;= 1.8, return second version number such as {@code 6} for {@code 1.6.x}, {@code 8} for
     * {@code 1.8.x}. Else return first number such as {@code 9} for {@code 9.0}.
     * <p>
     * Return -1 if obtain failed.
     *
     * @return java major version
     */
    public static int javaMajorVersion() {
        String javaVersion = getJavaVersion();
        if (javaVersion == null) {
            return -1;
        }
        int dotIndex = StringKit.indexOf(javaVersion, ".");
        if (dotIndex <= 0) {
            return Integer.parseInt(javaVersion);
        }
        int versionFirst = Integer.parseInt(javaVersion.substring(0, dotIndex));
        if (versionFirst >= 9) {
            return versionFirst;
        }
        if (versionFirst == 1) {
            int nextDotIndex = StringKit.indexOf(javaVersion, ".", dotIndex + 1);
            if (nextDotIndex < 0) {
                return -1;
            }
            return Integer.parseInt(javaVersion.substring(dotIndex + 1, nextDotIndex));
        }
        return -1;
    }

    /**
     * Returns whether current JDK version &gt; 8 (exclusive).
     *
     * @return whether current JDK version &gt; 8 (exclusive)
     */
    public static boolean isJava8Higher() {
        return javaMajorVersion() > 8;
    }
}
