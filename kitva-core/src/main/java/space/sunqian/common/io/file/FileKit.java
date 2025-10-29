package space.sunqian.common.io.file;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.io.IORuntimeException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

/**
 * Utilities for file related.
 *
 * @author sunqian
 */
public class FileKit {

    /**
     * Returns the path to the system's temporary directory.
     * <p>
     * The directory is determined by the {@code java.io.tmpdir} system property, which is platform-dependent but always
     * represents a valid temporary directory. Different OS may have different temporary directory, for examples:
     * <table summary="System's Temporary Directory">
     *   <tr>
     *     <th>OS</th>
     *     <th>Typical Path</th>
     *     <th>Notes</th>
     *   </tr>
     *   <tr>
     *     <td>Windows</td>
     *     <td><code>C:\\Users\\&lt;username&gt;\\AppData\\Local\\Temp</code></td>
     *     <td>User-specific temporary directory</td>
     *   </tr>
     *   <tr>
     *     <td>Linux</td>
     *     <td><code>/tmp</code></td>
     *     <td>System-wide temporary directory</td>
     *   </tr>
     *   <tr>
     *     <td>macOS</td>
     *     <td><code>/var/folders/xx/xxxxxxx/T</code></td>
     *     <td>Per-user private temporary directory</td>
     *   </tr>
     *   <tr>
     *     <td>Android</td>
     *     <td><code>/data/local/tmp</code></td>
     *     <td>Device temporary storage</td>
     *   </tr>
     * </table>
     *
     * @return the path to the system's temporary directory
     */
    public static @Nonnull Path getTempDir() {
        String tempDir = System.getProperty("java.io.tmpdir");
        return Paths.get(tempDir);
    }

    /**
     * Creates a temporary file in the system's temporary directory (specified by {@link #getTempDir()}), and returns
     * the created file. The file name is generated using the specified prefix and suffix.
     *
     * @param prefix the prefix of the file name
     * @param suffix the suffix of the file name
     * @param attrs  an optional list of file attributes to set atomically when creating the file
     * @return a temporary file with the specified file name
     */
    public static @Nonnull Path createTempFile(
        @Nullable String prefix,
        @Nullable String suffix,
        @Nonnull FileAttribute<?> @Nonnull ... attrs
    ) throws IORuntimeException {
        return createTempFile(getTempDir(), prefix, suffix, attrs);
    }

    private static @Nonnull Path createTempFile(
        @Nonnull Path dir,
        @Nullable String prefix,
        @Nullable String suffix,
        @Nonnull FileAttribute<?> @Nonnull ... attrs
    ) throws IORuntimeException {
        try {
            return Files.createTempFile(dir, prefix, suffix, attrs);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Creates a temporary file in the system's temporary directory (specified by {@link #getTempDir()}), and returns
     * the created file. The file name is generated using the specified prefix.
     *
     * @param prefix the prefix of the file name
     * @param attrs  an optional list of file attributes to set atomically when creating the file
     * @return a temporary file with the specified file name
     */
    public static @Nonnull Path createTempDir(
        @Nullable String prefix,
        @Nonnull FileAttribute<?> @Nonnull ... attrs
    ) throws IORuntimeException {
        return createTempDir(getTempDir(), prefix, attrs);
    }

    private static @Nonnull Path createTempDir(
        @Nonnull Path dir,
        @Nullable String prefix,
        @Nonnull FileAttribute<?> @Nonnull ... attrs
    ) throws IORuntimeException {
        try {
            return Files.createTempDirectory(dir, prefix, attrs);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }
}
