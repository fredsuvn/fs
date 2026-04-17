package tests.core.base.lang;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.lang.CodingKit;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CodingTest {

    @Test
    public void testIfAdd() {
        // Test with null inputs
        assertNull(CodingKit.ifAdd(null, null));

        // Test with single non-null input
        assertEquals("111", CodingKit.ifAdd(null, "111"));
        assertEquals("222", CodingKit.ifAdd("222", null));

        // Test with two non-null inputs
        assertEquals(Arrays.asList("111", "222"), CodingKit.ifAdd("111", "222"));

        // Test with multiple additions
        Object objOrList = null;
        objOrList = CodingKit.ifAdd(objOrList, "111");
        objOrList = CodingKit.ifAdd(objOrList, "222");
        objOrList = CodingKit.ifAdd(objOrList, null); // Should ignore null
        objOrList = CodingKit.ifAdd(objOrList, "333");
        assertEquals(Arrays.asList("111", "222", "333"), objOrList);

        // Test with only null inputs
        objOrList = null;
        objOrList = CodingKit.ifAdd(objOrList, null);
        objOrList = CodingKit.ifAdd(objOrList, null);
        objOrList = CodingKit.ifAdd(objOrList, null);
        assertNull(objOrList);

        // Test with mixed null and non-null inputs
        objOrList = null;
        objOrList = CodingKit.ifAdd(objOrList, null);
        objOrList = CodingKit.ifAdd(objOrList, "222");
        objOrList = CodingKit.ifAdd(objOrList, null); // Should ignore null
        assertEquals("222", objOrList);
    }

    @Test
    public void testIfMerge() {
        // Test with null input
        assertEquals("666", CodingKit.ifMerge(null, c -> {
            assertEquals(Collections.emptyList(), c);
            return "666";
        }));

        // Test with single object input
        assertEquals("111", CodingKit.ifMerge("111", c -> {
            assertEquals(Arrays.asList("111"), c);
            return "111";
        }));

        // Test with list input
        assertEquals("111222", CodingKit.ifMerge(Arrays.asList("111", "222"), c -> {
            assertEquals(Arrays.asList("111", "222"), c);
            return "111222";
        }));
    }
}
