package test.base.option;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.option.JieOption;
import xyz.sunqian.common.base.option.Option;

import static org.testng.Assert.assertEquals;
import static xyz.sunqian.common.collect.ArrayKit.array;

public class OptionTest {

    @Test
    public void testOption() {
        Option<?, ?>[] opts = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));
        assertEquals(JieOption.findValue("b", opts), "2");
        assertEquals(JieOption.findValue("d", opts), (String) null);
        assertEquals(JieOption.findValue("d", Option.empty()), (String) null);
        opts[0] = null;
        assertEquals(JieOption.findValue("b", opts), "2");
    }
}
