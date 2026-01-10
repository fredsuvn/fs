package space.sunqian.fs.base.version;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.util.List;

/**
 * This interface represents the semantic version as defined by <a href="https://semver.org/">Semantic Versioning</a>.
 * <p>
 * A semantic version has the format {@code MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]} where:
 * <ul>
 *     <li>
 *         {@code MAJOR}, {@code MINOR}, and {@code PATCH} are non-negative integers.
 *     </li>
 *     <li>
 *         {@code PRERELEASE} is a dot-separated list of identifiers consisting of alphanumeric characters and
 *         hyphens ([0-9A-Za-z-]).
 *     </li>
 *     <li>
 *         {@code BUILD} is a dot-separated list of identifiers consisting of alphanumeric characters and hyphens
 *         ([0-9A-Za-z-]).
 *     </li>
 * </ul>
 *
 * @author sunqian
 */
@Immutable
public interface SemVer extends Comparable<SemVer> {

    /**
     * Parses the given string as a semantic version.
     *
     * @param version the string to parse
     * @return the parsed semantic version
     * @throws IllegalArgumentException if the given string is not a valid semantic version
     */
    static @Nonnull SemVer parse(@Nonnull String version) throws IllegalArgumentException {
        return new SemVerImpl(version);
    }

    /**
     * Creates a semantic version with the given major, minor, and patch version numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @param patch the patch version number
     * @return the created semantic version
     */
    static @Nonnull SemVer of(int major, int minor, int patch) {
        return of(major, minor, patch, null, null);
    }

    /**
     * Creates a semantic version with the given major, minor, patch version numbers, pre-release version, and build
     * metadata.
     *
     * @param major      the major version number
     * @param minor      the minor version number
     * @param patch      the patch version number
     * @param preRelease the pre-release version
     * @param buildMeta  the build metadata
     * @return the created semantic version
     * @throws IllegalArgumentException if the pre-release version or build metadata is invalid
     */
    static @Nonnull SemVer of(
        int major, int minor, int patch, @Nullable String preRelease, @Nullable String buildMeta
    ) throws IllegalArgumentException {
        return parse(
            major + "." + minor + "." + patch
                + (preRelease == null ? "" : "-" + preRelease)
                + (buildMeta == null ? "" : "+" + buildMeta)
        );
    }

    /**
     * Returns the major version number.
     *
     * @return the major version number
     */
    int major();

    /**
     * Returns the minor version number.
     *
     * @return the minor version number
     */
    int minor();

    /**
     * Returns the patch version number.
     *
     * @return the patch version number
     */
    int patch();

    /**
     * Returns the pre-release version in the current {@link SemVer}.
     *
     * @return the pre-release version in the current {@link SemVer}
     */
    @Nullable
    PreRelease preRelease();

    /**
     * Returns the build metadata in the current {@link SemVer}.
     *
     * @return the build metadata in the current {@link SemVer}
     */
    @Nullable
    BuildMeta buildMeta();

    /**
     * Returns this {@link SemVer} as a string.
     *
     * @return this {@link SemVer} as a string
     */
    @Nonnull
    String toString();

    /**
     * Returns whether the current {@link SemVer} is equal to the given object.
     *
     * @param other the object to compare with
     * @return whether the current {@link SemVer} is equal to the given object
     */
    boolean equals(@Nullable Object other);

    /**
     * Represents the pre-release version in the {@link SemVer}.
     */
    interface PreRelease extends Comparable<PreRelease> {

        /**
         * Returns the pre-release version identifiers in the {@link SemVer}. The type of each identifier is either
         * {@link String} or {@link Integer}.
         *
         * @return the pre-release version identifiers in the {@link SemVer}
         */
        @Nonnull
        List<@Nonnull Object> identifiers();

        /**
         * Returns the pre-release version in the {@link SemVer} as a string.
         *
         * @return the pre-release version in the {@link SemVer} as a string
         */
        @Nonnull
        String toString();

        /**
         * Returns whether the current {@link PreRelease} is equal to the given object.
         * <p>
         * Note that this method only compares its own content, regardless of the instance of {@link SemVer} it belongs
         * to.
         *
         * @param other the object to compare with
         * @return whether the current {@link PreRelease} is equal to the given object
         */
        boolean equals(@Nullable Object other);

        /**
         * Compares this {@link PreRelease} with the specified {@link PreRelease} for order.
         * <p>
         * Returns a negative integer, zero, or a positive integer as this {@link PreRelease} is less than, equal to, or
         * greater than the specified {@link PreRelease}.
         * <p>
         * Note that this method only compares its own content, regardless of the instance of {@link SemVer} it belongs
         * to.
         *
         * @param o the {@link PreRelease} to be compared
         * @return a negative integer, zero, or a positive integer as this {@link PreRelease} is less than, equal to, or
         * greater than the specified {@link PreRelease}
         */
        @Override
        int compareTo(@Nonnull PreRelease o);
    }

    /**
     * Represents the build metadata in the {@link SemVer}.
     */
    interface BuildMeta {

        /**
         * Returns the build metadata identifiers in the {@link SemVer}.
         *
         * @return the build metadata identifiers in the {@link SemVer}
         */
        @Nonnull
        List<@Nonnull String> identifiers();

        /**
         * Returns the build metadata in the {@link SemVer} as a string.
         *
         * @return the build metadata in the {@link SemVer} as a string
         */
        @Nonnull
        String toString();

        /**
         * Returns whether the current {@link BuildMeta} is equal to the given object.
         * <p>
         * Note that this method only compares its own content, regardless of the instance of {@link SemVer} it belongs
         * to.
         *
         * @param other the object to compare with
         * @return whether the current {@link BuildMeta} is equal to the given object
         */
        boolean equals(@Nullable Object other);
    }
}
