package tests.base.option;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.option.Option;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static space.sunqian.fs.collect.ArrayKit.array;

public class OptionTest {

    @Test
    public void testOption() {
        Option<?, ?>[] opts = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));
        assertEquals("2", Option.findValue("b", opts));
        assertEquals((String) null, Option.findValue("d", opts));
        assertEquals((String) null, Option.findValue("d", Option.emptyOptions()));
        assertEquals("2", Option.findValue("b", opts));
        assertTrue(Option.containsKey("a", opts));
        assertFalse(Option.containsKey("d", opts));
    }
}
