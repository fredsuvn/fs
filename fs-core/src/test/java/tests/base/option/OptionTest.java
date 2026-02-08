package tests.base.option;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
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

        // equals and hash code
        Option<?, ?> opt = Option.of("a", 1);
        assertEquals("a", opt.key());
        assertEquals(1, opt.value());
        assertEquals(opt, Option.of("a", 1));
        assertNotEquals(opt, Option.of("a", 2));
        assertNotEquals(opt, Option.of("b", 1));
        assertNotEquals(opt, Option.of("b", 2));
        assertEquals(
            Objects.hash("a", 1),
            opt.hashCode()
        );
        assertNotEquals(opt, new Object());

        // toString
        assertEquals(
            "[a: 1]",
            opt.toString()
        );
    }

    @Test
    public void testMergeOption() {
        Option<String, String>[] defaultOptions = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));
        Option<String, String>[] additionalOptions = array();
        assertSame(
            defaultOptions,
            OptionKit.mergeOptions(defaultOptions, additionalOptions)
        );
        assertArrayEquals(
            array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3")),
            defaultOptions
        );
        assertSame(
            defaultOptions,
            OptionKit.mergeOptions(defaultOptions, defaultOptions)
        );
        assertArrayEquals(
            array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3")),
            defaultOptions
        );
        Option<String, String>[] additionalOptions2 = array(Option.of("b", "22"));
        assertSame(
            defaultOptions,
            OptionKit.mergeOptions(defaultOptions, additionalOptions2)
        );
        assertArrayEquals(
            array(Option.of("a", "1"), Option.of("b", "22"), Option.of("c", "3")),
            defaultOptions
        );
        Option<String, String>[] additionalOptions3 = array(Option.of("d", "4"), Option.of("e", "5"));
        assertNotSame(
            defaultOptions,
            OptionKit.mergeOptions(defaultOptions, additionalOptions3)
        );
        assertArrayEquals(
            array(Option.of("a", "1"), Option.of("b", "22"), Option.of("c", "3"), Option.of("e", "5"), Option.of("d", "4")),
            OptionKit.mergeOptions(defaultOptions, additionalOptions3)
        );
        Option<String, String>[] additionalOptions4 = array(Option.of("c", "3"), Option.of("d", "4"));
        assertNotSame(
            defaultOptions,
            OptionKit.mergeOptions(defaultOptions, additionalOptions4)
        );
        assertArrayEquals(
            array(Option.of("a", "1"), Option.of("b", "22"), Option.of("c", "3"), Option.of("d", "4")),
            OptionKit.mergeOptions(defaultOptions, additionalOptions4)
        );
    }
}
