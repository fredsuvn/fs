package test.base;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.option.Option;

import static org.testng.Assert.assertEquals;
import static xyz.sunqian.common.coll.JieArray.array;

public class OptionTest {

    @Test
    public void testOption() {
        Option<?, ?>[] opts = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));
        assertEquals(Option.find("b", opts), "2");
        assertEquals(Option.find("d", opts), (String) null);
        assertEquals(Option.find("d", Option.empty()), (String) null);
        opts[0] = null;
        assertEquals(Option.find("b", opts), "2");
    }
}
