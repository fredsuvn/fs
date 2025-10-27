package tests.base.lang;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.lang.CodingKit;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CodingTest {

    @Test
    public void testIfAdd() {
        assertNull(CodingKit.ifAdd(null, null));
        assertEquals(CodingKit.ifAdd(null, "111"), "111");
        assertEquals(CodingKit.ifAdd("222", null), "222");
        assertEquals(CodingKit.ifAdd("111", "222"), Arrays.asList("111", "222"));
        {
            Object objOrList = null;
            objOrList = CodingKit.ifAdd(objOrList, "111");
            objOrList = CodingKit.ifAdd(objOrList, "222");
            objOrList = CodingKit.ifAdd(objOrList, null);
            objOrList = CodingKit.ifAdd(objOrList, "333");
            assertEquals(objOrList, Arrays.asList("111", "222", "333"));
        }
        {
            Object objOrList = null;
            objOrList = CodingKit.ifAdd(objOrList, null);
            objOrList = CodingKit.ifAdd(objOrList, null);
            objOrList = CodingKit.ifAdd(objOrList, null);
            assertNull(objOrList);
        }
        {
            Object objOrList = null;
            objOrList = CodingKit.ifAdd(objOrList, null);
            objOrList = CodingKit.ifAdd(objOrList, "222");
            objOrList = CodingKit.ifAdd(objOrList, null);
            assertEquals(objOrList, "222");
        }
    }

    @Test
    public void testIfMerge() {
        assertEquals(CodingKit.ifMerge(null, c -> {
            assertEquals(c, Collections.emptyList());
            return "666";
        }), "666");
        assertEquals(CodingKit.ifMerge("111", c -> {
            assertEquals(c, Arrays.asList("111"));
            return "111";
        }), "111");
        assertEquals(CodingKit.ifMerge(Arrays.asList("111", "222"), c -> {
            assertEquals(c, Arrays.asList("111", "222"));
            return "111222";
        }), "111222");
    }
}
