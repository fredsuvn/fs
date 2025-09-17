package tests.base.option;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.option.Option;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static xyz.sunqian.common.collect.ArrayKit.array;

public class OptionTest {

    @Test
    public void testOption() {
        Option<?, ?>[] opts = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));
        assertEquals(Option.findValue("b", opts), "2");
        assertEquals(Option.findValue("d", opts), (String) null);
        assertEquals(Option.findValue("d", Option.emptyOptions()), (String) null);
        assertEquals(Option.findValue("b", opts), "2");
        assertTrue(Option.containsKey("a", opts));
        assertFalse(Option.containsKey("d", opts));
    }
}
