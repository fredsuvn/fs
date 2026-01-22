package space.sunqian.fs.utils.version;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.collect.ListKit;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class SemVerImpl implements SemVer {

    private static final @Nonnull Pattern SEM_VER_PATTERN = Pattern.compile(
        "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
    );

    private final int major;
    private final int minor;
    private final int patch;
    private final @Nullable PreRelease preRelease;
    private final @Nullable BuildMeta buildMeta;

    SemVerImpl(@Nonnull String version) throws IllegalArgumentException {
        Matcher matcher = SEM_VER_PATTERN.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid semantic version: " + version);
        }
        major = Integer.parseInt(matcher.group(1));
        minor = Integer.parseInt(matcher.group(2));
        patch = Integer.parseInt(matcher.group(3));
        String g4 = matcher.group(4);
        if (g4 != null) {
            String[] ids = g4.split("\\.");
            Object[] preReleaseIds = new Object[ids.length];
            for (int i = 0; i < ids.length; i++) {
                try {
                    Object intObj = Integer.valueOf(ids[i]);
                    preReleaseIds[i] = intObj;
                } catch (NumberFormatException e) {
                    preReleaseIds[i] = ids[i];
                }
            }
            preRelease = new PreReleaseImpl(preReleaseIds);
        } else {
            preRelease = null;
        }
        String g5 = matcher.group(5);
        if (g5 != null) {
            String[] ids = g5.split("\\.");
            buildMeta = new BuildMetaImpl(ids);
        } else {
            buildMeta = null;
        }
    }

    @Override
    public int major() {
        return major;
    }

    @Override
    public int minor() {
        return minor;
    }

    @Override
    public int patch() {
        return patch;
    }

    @Override
    public @Nullable PreRelease preRelease() {
        return preRelease;
    }

    @Override
    public @Nullable BuildMeta buildMeta() {
        return buildMeta;
    }

    @Override
    public @Nonnull String toString() {
        return major() + "." + minor() + "." + patch()
            + (preRelease() == null ? "" : "-" + preRelease())
            + (buildMeta() == null ? "" : "+" + buildMeta());
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, preRelease, buildMeta);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof SemVer)) {
            return false;
        }
        return compareTo((SemVer) obj) == 0;
    }

    @Override
    public int compareTo(@Nonnull SemVer o) {
        // Precedence MUST be calculated by separating the version into major, minor, patch and pre-release identifiers
        // in that order (Build metadata does not figure into precedence).
        //
        // Precedence is determined by the first difference when comparing each of these identifiers from left to right
        // as follows: Major, minor, and patch versions are always compared numerically.
        // Example: 1.0.0 < 2.0.0 < 2.1.0 < 2.1.1.
        if (major < o.major()) {
            return -1;
        }
        if (major > o.major()) {
            return 1;
        }
        if (minor < o.minor()) {
            return -1;
        }
        if (minor > o.minor()) {
            return 1;
        }
        if (patch < o.patch()) {
            return -1;
        }
        if (patch > o.patch()) {
            return 1;
        }
        // When major, minor, and patch are equal, a pre-release version has lower precedence than a normal version:
        //
        // Example: 1.0.0-alpha < 1.0.0.
        PreRelease oRelease = o.preRelease();
        if (preRelease == null && oRelease != null) {
            return 1;
        }
        if (preRelease != null && oRelease == null) {
            return -1;
        }
        if (preRelease != null) {
            return preRelease.compareTo(oRelease);
        }
        return 0;
    }

    private static final class PreReleaseImpl implements PreRelease {

        private final @Nonnull Object @Nonnull [] identifiers;
        private final @Nonnull List<@Nonnull Object> identifierList;

        private PreReleaseImpl(@Nonnull Object @Nonnull [] identifiers) {
            this.identifiers = identifiers;
            this.identifierList = ListKit.list(identifiers);
        }

        @Override
        public @Nonnull List<@Nonnull Object> identifiers() {
            return identifierList;
        }

        @Override
        public @Nonnull String toString() {
            return identifierList.stream().map(Object::toString).collect(Collectors.joining("."));
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(identifiers);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof PreRelease)) {
                return false;
            }
            return compareTo((PreRelease) obj) == 0;
        }

        @Override
        public int compareTo(@Nonnull PreRelease o) {
            int size = Math.min(identifiers.length, o.identifiers().size());
            for (int i = 0; i < size; i++) {
                Object id = identifiers[i];
                Object oId = o.identifiers().get(i);
                if (id instanceof Integer) {
                    if (oId instanceof Integer) {
                        // Identifiers consisting of only digits are compared numerically.
                        int cmp = ((Integer) id).compareTo((Integer) oId);
                        if (cmp != 0) {
                            return cmp;
                        }
                    } else {
                        // Numeric identifiers always have lower precedence than non-numeric identifiers.
                        return -1;
                    }
                } else {
                    if (oId instanceof Integer) {
                        // Identifiers consisting of only digits are compared numerically.
                        return 1;
                    } else {
                        // Identifiers with letters or hyphens are compared lexically in ASCII sort order.
                        int cmp = ((String) id).compareTo((String) oId);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                }
            }
            // A larger set of pre-release fields has a higher precedence than a smaller set,
            // if all of the preceding identifiers are equal.
            return Integer.compare(identifiers.length, o.identifiers().size());
        }
    }

    private static final class BuildMetaImpl implements BuildMeta {

        private final @Nonnull String @Nonnull [] identifiers;
        private final @Nonnull List<@Nonnull String> identifierList;

        BuildMetaImpl(@Nonnull String @Nonnull [] identifiers) {
            this.identifiers = identifiers;
            this.identifierList = ListKit.list(identifiers);
        }

        @Override
        public @Nonnull List<@Nonnull String> identifiers() {
            return identifierList;
        }

        @Override
        public @Nonnull String toString() {
            return String.join(".", identifiers);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(identifiers);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof BuildMeta)) {
                return false;
            }
            return identifierList.equals(((BuildMeta) obj).identifiers());
        }
    }
}
