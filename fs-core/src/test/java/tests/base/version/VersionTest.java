package tests.base.version;

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
        {
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
            assertEquals("1.0.0-alpha", v1.toString());
            assertEquals("1.0.0-alpha.1", v2.toString());
            assertEquals("1.0.0-alpha.beta", v3.toString());
            assertEquals("1.0.0-beta", v4.toString());
            assertEquals("1.0.0-beta.2", v5.toString());
            assertEquals("1.0.0-beta.11", v6.toString());
            assertEquals("1.0.0-rc.1", v7.toString());
            assertEquals("1.0.0", v8.toString());
            assertEquals("1.0.0-alpha.beta+012abc.a--", v9.toString());
            assertEquals("1.0.0-beta+012abc.a--", v10.toString());
            assertEquals(Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"alpha"}), null), v1.hashCode());
            assertEquals(Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"alpha", 1}), null), v2.hashCode());
            assertEquals(Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"alpha", "beta"}), null), v3.hashCode());
            assertEquals(Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"beta"}), null), v4.hashCode());
            assertEquals(Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"beta", 2}), null), v5.hashCode());
            assertEquals(Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"beta", 11}), null), v6.hashCode());
            assertEquals(Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"rc", 1}), null), v7.hashCode());
            assertEquals(Objects.hash(1, 0, 0, null, null), v8.hashCode());
            assertEquals(
                Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"alpha", "beta"}),
                    Arrays.hashCode(new String[]{"012abc", "a--"})),
                v9.hashCode()
            );
            assertEquals(
                Objects.hash(1, 0, 0, Arrays.hashCode(new Object[]{"beta"}),
                    Arrays.hashCode(new String[]{"012abc", "a--"})),
                v10.hashCode()
            );
            List<SemVer> versions = ListKit.arrayList(v7, v4, v1, v3, v8, v6, v5, v2);
            assertEquals(
                ListKit.list(v1, v2, v3, v4, v5, v6, v7, v8),
                versions.stream().sorted().collect(Collectors.toList())
            );
            versions = ListKit.arrayList(v1, v2, v3, v4, v5, v6, v7, v8);
            assertEquals(
                ListKit.list(v1, v2, v3, v4, v5, v6, v7, v8),
                versions.stream().sorted().collect(Collectors.toList())
            );
            versions = ListKit.arrayList(v8, v7, v6, v5, v4, v3, v2, v1);
            assertEquals(
                ListKit.list(v1, v2, v3, v4, v5, v6, v7, v8),
                versions.stream().sorted().collect(Collectors.toList())
            );
            assertEquals(v3, v9);
            assertEquals(v4, v10);
            assertFalse(v1.equals("1.0.0-alpha"));
            assertNotEquals(v1, v2);
            SemVer v19 = SemVer.of(2, 0, 0, "alpha.beta", "012abc.a--");
            assertNotEquals(v3, v19);
            assertNotEquals(v9, v19);
            assertEquals(v3.preRelease(), v19.preRelease());
            assertEquals(v9.buildMeta(), v19.buildMeta());
            assertNotEquals(v1.preRelease(), v3.preRelease());
            assertFalse(v3.preRelease().equals("alpha.beta"));
            assertFalse(v9.buildMeta().equals("012abc.a--"));
            assertEquals(-1, v2.compareTo(v3));
            assertEquals(1, v3.compareTo(v2));
            assertThrows(IllegalArgumentException.class, () -> SemVer.parse("1.0.0-alpha.01.2"));
            assertThrows(IllegalArgumentException.class, () -> SemVer.parse("1.0.0-alpha.01.2++"));
        }
        {
            // 1.0.0 < 2.0.0 < 2.1.0 < 2.1.1
            SemVer v1 = SemVer.parse("1.0.0");
            SemVer v2 = SemVer.parse("2.0.0");
            SemVer v3 = SemVer.parse("2.1.0");
            SemVer v4 = SemVer.parse("2.1.1");
            assertEquals("1.0.0", v1.toString());
            assertEquals("2.0.0", v2.toString());
            assertEquals("2.1.0", v3.toString());
            assertEquals("2.1.1", v4.toString());
            assertEquals(Objects.hash(1, 0, 0, null, null), v1.hashCode());
            assertEquals(Objects.hash(2, 0, 0, null, null), v2.hashCode());
            assertEquals(Objects.hash(2, 1, 0, null, null), v3.hashCode());
            assertEquals(Objects.hash(2, 1, 1, null, null), v4.hashCode());
            List<SemVer> versions = ListKit.arrayList(v4, v1, v3, v2);
            assertEquals(
                ListKit.list(v1, v2, v3, v4),
                versions.stream().sorted().collect(Collectors.toList())
            );
            versions = ListKit.arrayList(v1, v2, v3, v4);
            assertEquals(
                ListKit.list(v1, v2, v3, v4),
                versions.stream().sorted().collect(Collectors.toList())
            );
            versions = ListKit.arrayList(v4, v3, v2, v1);
            assertEquals(
                ListKit.list(v1, v2, v3, v4),
                versions.stream().sorted().collect(Collectors.toList())
            );
        }
    }
}
