package tests.base.option;

import org.junit.jupiter.api.Test;
import space.sunqian.common.base.option.Option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static space.sunqian.common.collect.ArrayKit.array;

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
