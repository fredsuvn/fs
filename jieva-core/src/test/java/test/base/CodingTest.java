package test.base;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieCoding;

import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static xyz.sunqian.common.coll.JieArray.asList;

public class CodingTest {

    @Test
    public void testIfAdd() {
        assertNull(JieCoding.ifAdd(null, null));
        assertEquals(JieCoding.ifAdd(null, "111"), "111");
        assertEquals(JieCoding.ifAdd("222", null), "222");
        assertEquals(JieCoding.ifAdd("111", "222"), asList("111", "222"));
        {
            Object objOrList = null;
            objOrList = JieCoding.ifAdd(objOrList, "111");
            objOrList = JieCoding.ifAdd(objOrList, "222");
            objOrList = JieCoding.ifAdd(objOrList, null);
            objOrList = JieCoding.ifAdd(objOrList, "333");
            assertEquals(objOrList, asList("111", "222", "333"));
        }
        {
            Object objOrList = null;
            objOrList = JieCoding.ifAdd(objOrList, null);
            objOrList = JieCoding.ifAdd(objOrList, null);
            objOrList = JieCoding.ifAdd(objOrList, null);
            assertNull(objOrList);
        }
        {
            Object objOrList = null;
            objOrList = JieCoding.ifAdd(objOrList, null);
            objOrList = JieCoding.ifAdd(objOrList, "222");
            objOrList = JieCoding.ifAdd(objOrList, null);
            assertEquals(objOrList, "222");
        }
    }

    @Test
    public void testIfMerge() {
        assertEquals(JieCoding.ifMerge(null, c -> {
            assertEquals(c, Collections.emptyList());
            return "666";
        }), "666");
        assertEquals(JieCoding.ifMerge("111", c -> {
            assertEquals(c, asList("111"));
            return "111";
        }), "111");
        assertEquals(JieCoding.ifMerge(asList("111", "222"), c -> {
            assertEquals(c, asList("111", "222"));
            return "111222";
        }), "111222");
    }
}
