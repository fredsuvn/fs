package tests.core.base.option;

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
    public void testOptionBasicFunctionality() {
        Option<?, ?>[] opts = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));

        // Test findValue
        assertEquals("2", OptionKit.findValue("b", opts));
        assertEquals((String) null, OptionKit.findValue("d", opts));
        assertEquals((String) null, OptionKit.findValue("d", Option.emptyOptions()));

        // Test containsKey
        assertTrue(OptionKit.containsKey("a", opts));
        assertFalse(OptionKit.containsKey("d", opts));

        // Test Option creation without value
        Option<String, ?> strOpt = Option.of("a");
        assertEquals("a", strOpt.key());
        assertNull(strOpt.value());
    }

    @Test
    public void testOptionEqualsAndHashCode() {
        Option<?, ?> opt = Option.of("a", 1);
        assertEquals("a", opt.key());
        assertEquals(1, opt.value());

        // Test equals with same key and value
        assertEquals(opt, Option.of("a", 1));

        // Test equals with different value
        assertNotEquals(opt, Option.of("a", 2));

        // Test equals with different key
        assertNotEquals(opt, Option.of("b", 1));

        // Test equals with different key and value
        assertNotEquals(opt, Option.of("b", 2));

        // Test equals with different object type
        assertNotEquals(opt, new Object());

        // Test hashCode
        assertEquals(Objects.hash("a", 1), opt.hashCode());
    }

    @Test
    public void testOptionToString() {
        Option<?, ?> opt = Option.of("a", 1);
        assertEquals("[a: 1]", opt.toString());
    }

    @Test
    public void testMergeOptions() {
        Option<String, String>[] defaultOptions = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));

        // Test merge with empty additional options
        Option<String, String>[] additionalOptions = array();
        assertSame(defaultOptions, OptionKit.mergeOptions(defaultOptions, additionalOptions));
        assertArrayEquals(array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3")), defaultOptions);

        // Test merge with same options
        assertNotSame(defaultOptions, OptionKit.mergeOptions(defaultOptions, defaultOptions));
        assertArrayEquals(array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3")), OptionKit.mergeOptions(defaultOptions, defaultOptions));

        // Test merge with overriding option
        Option<String, String>[] additionalOptions2 = array(Option.of("b", "22"));
        assertNotSame(defaultOptions, OptionKit.mergeOptions(defaultOptions, additionalOptions2));
        assertArrayEquals(array(Option.of("a", "1"), Option.of("b", "22"), Option.of("c", "3")), OptionKit.mergeOptions(defaultOptions, additionalOptions2));

        // Test merge with new options
        Option<String, String>[] additionalOptions3 = array(Option.of("d", "4"), Option.of("e", "5"));
        assertNotSame(defaultOptions, OptionKit.mergeOptions(defaultOptions, additionalOptions3));
        assertArrayEquals(array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"), Option.of("e", "5"), Option.of("d", "4")), OptionKit.mergeOptions(defaultOptions, additionalOptions3));

        // Test merge with mixed overriding and new options
        Option<String, String>[] additionalOptions4 = array(Option.of("c", "3"), Option.of("d", "4"));
        assertNotSame(defaultOptions, OptionKit.mergeOptions(defaultOptions, additionalOptions4));
        assertArrayEquals(array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"), Option.of("d", "4")), OptionKit.mergeOptions(defaultOptions, additionalOptions4));
    }

    @Test
    public void testMergeOption() {
        Option<String, String>[] defaultOptions = array(Option.of("a", "1"), Option.of("b", "2"), Option.of("c", "3"));

        // Test merge with overriding option
        Option<String, String> additionalOption = Option.of("b", "3");
        assertArrayEquals(array(Option.of("a", "1"), Option.of("b", "3"), Option.of("c", "3")), OptionKit.mergeOption(defaultOptions, additionalOption));

        // Test merge with option not in default options
        assertArrayEquals(array(Option.of("a", "1"), Option.of("c", "3"), Option.of("b", "3")), OptionKit.mergeOption(array(Option.of("a", "1"), Option.of("c", "3")), additionalOption));
    }

    @Test
    public void testIsEnabled() {
        // Test enabled cases
        assertTrue(OptionKit.isEnabled("a", Option.of("a", "1"), Option.of("b", "2")));
        assertTrue(OptionKit.isEnabled("a", Option.of("a", 1), Option.of("b", "2")));
        assertTrue(OptionKit.isEnabled("a", Option.of("a", true), Option.of("b", "2")));

        // Test disabled cases
        assertFalse(OptionKit.isEnabled("b", Option.of("a", "1"), Option.of("b", "2")));
        assertFalse(OptionKit.isEnabled("b", Option.of("a", "1"), Option.of("b", 2)));
        assertFalse(OptionKit.isEnabled("b", Option.of("a", "1"), Option.of("b", false)));
        assertFalse(OptionKit.isEnabled("b", Option.of("a", "1"), Option.of("b", new Object())));
        assertFalse(OptionKit.isEnabled("b", Option.of("a", "1"), Option.of("b", null)));

        // Test non-existent option
        assertFalse(OptionKit.isEnabled("c", Option.of("a", "1"), Option.of("b", "2")));
    }
}
