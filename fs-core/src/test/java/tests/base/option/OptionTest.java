package tests.base.option;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static space.sunqian.fs.collect.ArrayKit.array;

public class OptionTest {

    @Test
    public void testOption() {
        Option<?, ?>[] opts = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));
        assertEquals("2", OptionKit.findValue("b", opts));
        assertEquals((String) null, OptionKit.findValue("d", opts));
        assertEquals((String) null, OptionKit.findValue("d", Option.emptyOptions()));
        assertEquals("2", OptionKit.findValue("b", opts));
        assertTrue(OptionKit.containsKey("a", opts));
        assertFalse(OptionKit.containsKey("d", opts));
        Option<String, ?> strOpt = Option.of("a");
        assertEquals("a", strOpt.key());
        assertNull(strOpt.value());
    }

    @Test
    public void testMergeOption() {
        Option<String, String>[] defaultOptions = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));
        Option<String, String>[] additionalOptions = array();
        assertSame(
            defaultOptions,
            OptionKit.mergeOptions(defaultOptions, additionalOptions)
        );
        assertSame(
            defaultOptions,
            OptionKit.mergeOptions(defaultOptions, defaultOptions)
        );
    }
}
