package tests.base.version;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.version.SemVer;
import space.sunqian.fs.collect.ListKit;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VersionTest {

    @Test
    public void testVersion() throws Exception {
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
        SemVer v9 = SemVer.of(1, 0, 0, "alpha.beta", "012abc---");
        SemVer v10 = SemVer.of(1, 0, 0, "beta", "012abc---");
        assertEquals("1.0.0-alpha", v1.toString());
        assertEquals("1.0.0-alpha.1", v2.toString());
        assertEquals("1.0.0-alpha.beta", v3.toString());
        assertEquals("1.0.0-beta", v4.toString());
        assertEquals("1.0.0-beta.2", v5.toString());
        assertEquals("1.0.0-beta.11", v6.toString());
        assertEquals("1.0.0-rc.1", v7.toString());
        assertEquals("1.0.0", v8.toString());
        assertEquals("1.0.0-alpha.beta+012abc---", v9.toString());
        assertEquals("1.0.0-beta+012abc---", v10.toString());
        assertEquals("1.0.0-alpha".hashCode(), v1.hashCode());
        assertEquals("1.0.0-alpha.1".hashCode(), v2.hashCode());
        assertEquals("1.0.0-alpha.beta".hashCode(), v3.hashCode());
        assertEquals("1.0.0-beta".hashCode(), v4.hashCode());
        assertEquals("1.0.0-beta.2".hashCode(), v5.hashCode());
        assertEquals("1.0.0-beta.11".hashCode(), v6.hashCode());
        assertEquals("1.0.0-rc.1".hashCode(), v7.hashCode());
        assertEquals("1.0.0".hashCode(), v8.hashCode());
        assertEquals("1.0.0-alpha.beta+012abc---".hashCode(), v9.hashCode());
        assertEquals("1.0.0-beta+012abc---".hashCode(), v10.hashCode());
        List<SemVer> versions = ListKit.arrayList(v7, v4, v1, v3, v8, v6, v5, v2);
        assertEquals(
            ListKit.list(v1, v2, v3, v4, v5, v6, v7, v8),
            versions.stream().sorted().collect(Collectors.toList())
        );
        assertEquals(v3, v9);
        assertEquals(v4, v10);
        assertThrows(IllegalArgumentException.class, () -> SemVer.parse("1.0.0-alpha.01.2"));
        assertThrows(IllegalArgumentException.class, () -> SemVer.parse("1.0.0-alpha.01.2++"));
    }
}
