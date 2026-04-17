package tests.core.base.version;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.utils.version.SemVer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VersionTest {

    @Test
    public void testVersion() throws Exception {
        testPreReleaseVersions();
        testStableVersions();
    }

    private void testPreReleaseVersions() throws Exception {
        // 1.0.0-alpha < 1.0.0-alpha.1 < 1.0.0-alpha.beta < 1.0.0-beta
        // < 1.0.0-beta.2 < 1.0.0-beta.11 < 1.0.0-rc.1 < 1.0.0.
        SemVer v1 = SemVer.parse("1.0.0-alpha");
        SemVer v2 = SemVer.parse("1.0.0-alpha.1");
        SemVer v3 = SemVer.parse("1.0.0-alpha.beta");
        SemVer v4 = SemVer.parse("1.0.0-beta");
        SemVer v5 = SemVer.parse("1.0.0-beta.2");
        SemVer v6 = SemVer.parse("1.0.0-beta.11");
        SemVer v7 = SemVer.of(1, 0, 0, "rc.1", null);
        SemVer v8 = SemVer.of(1, 0, 0);
        SemVer v9 = SemVer.of(1, 0, 0, "alpha.beta", "012abc.a--");
        SemVer v10 = SemVer.of(1, 0, 0, "beta", "012abc.a--");

        testVersionToString(v1, "1.0.0-alpha");
        testVersionToString(v2, "1.0.0-alpha.1");
        testVersionToString(v3, "1.0.0-alpha.beta");
        testVersionToString(v4, "1.0.0-beta");
        testVersionToString(v5, "1.0.0-beta.2");
        testVersionToString(v6, "1.0.0-beta.11");
        testVersionToString(v7, "1.0.0-rc.1");
        testVersionToString(v8, "1.0.0");
        testVersionToString(v9, "1.0.0-alpha.beta+012abc.a--");
        testVersionToString(v10, "1.0.0-beta+012abc.a--");

        testVersionHashCode(v1, Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"alpha"}), null));
        testVersionHashCode(v2, Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"alpha", 1}), null));
        testVersionHashCode(v3, Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"alpha", "beta"}), null));
        testVersionHashCode(v4, Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"beta"}), null));
        testVersionHashCode(v5, Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"beta", 2}), null));
        testVersionHashCode(v6, Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"beta", 11}), null));
        testVersionHashCode(v7, Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"rc", 1}), null));
        testVersionHashCode(v8, Objects.hash(1, 0, 0, null, null));
        testVersionHashCode(v9, Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"alpha", "beta"}), Arrays.hashCode(new String[]{"012abc", "a--"})));
        testVersionHashCode(v10, Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"beta"}), Arrays.hashCode(new String[]{"012abc", "a--"})));

        testVersionSorting(ListKit.arrayList(v7, v4, v1, v3, v8, v6, v5, v2), ListKit.list(v1, v2, v3, v4, v5, v6, v7, v8));
        testVersionSorting(ListKit.arrayList(v1, v2, v3, v4, v5, v6, v7, v8), ListKit.list(v1, v2, v3, v4, v5, v6, v7, v8));
        testVersionSorting(ListKit.arrayList(v8, v7, v6, v5, v4, v3, v2, v1), ListKit.list(v1, v2, v3, v4, v5, v6, v7, v8));

        testVersionEquality(v3, v9);
        testVersionEquality(v4, v10);
        testVersionInequality(v1, "1.0.0-alpha");
        testVersionInequality(v1, v2);

        SemVer v19 = SemVer.of(2, 0, 0, "alpha.beta", "012abc.a--");
        testVersionInequality(v3, v19);
        testVersionInequality(v9, v19);
        assertEquals(v3.preRelease(), v19.preRelease());
        assertEquals(v9.buildMeta(), v19.buildMeta());
        assertNotEquals(v1.preRelease(), v3.preRelease());
        assertFalse(v3.preRelease().equals("alpha.beta"));
        assertFalse(v9.buildMeta().equals("012abc.a--"));

        testVersionComparison(v2, v3, -1);
        testVersionComparison(v3, v2, 1);

        testVersionParsingError("1.0.0-alpha.01.2");
        testVersionParsingError("1.0.0-alpha.01.2++");
    }

    private void testStableVersions() throws Exception {
        // 1.0.0 < 2.0.0 < 2.1.0 < 2.1.1
        SemVer v1 = SemVer.parse("1.0.0");
        SemVer v2 = SemVer.parse("2.0.0");
        SemVer v3 = SemVer.parse("2.1.0");
        SemVer v4 = SemVer.parse("2.1.1");

        testVersionToString(v1, "1.0.0");
        testVersionToString(v2, "2.0.0");
        testVersionToString(v3, "2.1.0");
        testVersionToString(v4, "2.1.1");

        testVersionHashCode(v1, Objects.hash(1, 0, 0, null, null));
        testVersionHashCode(v2, Objects.hash(2, 0, 0, null, null));
        testVersionHashCode(v3, Objects.hash(2, 1, 0, null, null));
        testVersionHashCode(v4, Objects.hash(2, 1, 1, null, null));

        testVersionSorting(ListKit.arrayList(v4, v1, v3, v2), ListKit.list(v1, v2, v3, v4));
        testVersionSorting(ListKit.arrayList(v1, v2, v3, v4), ListKit.list(v1, v2, v3, v4));
        testVersionSorting(ListKit.arrayList(v4, v3, v2, v1), ListKit.list(v1, v2, v3, v4));
    }

    private void testVersionToString(SemVer version, String expected) {
        assertEquals(expected, version.toString());
    }

    private void testVersionHashCode(SemVer version, int expected) {
        assertEquals(expected, version.hashCode());
    }

    private void testVersionSorting(List<SemVer> input, List<SemVer> expected) {
        assertEquals(expected, input.stream().sorted().collect(Collectors.toList()));
    }

    private void testVersionEquality(SemVer version1, SemVer version2) {
        assertEquals(version1, version2);
    }

    private void testVersionInequality(SemVer version1, Object version2) {
        assertNotEquals(version1, version2);
    }

    private void testVersionComparison(SemVer version1, SemVer version2, int expected) {
        assertEquals(expected, version1.compareTo(version2));
    }

    private void testVersionParsingError(String versionString) {
        assertThrows(IllegalArgumentException.class, () -> SemVer.parse(versionString));
    }
}
