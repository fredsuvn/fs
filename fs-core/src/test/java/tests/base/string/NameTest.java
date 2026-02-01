package tests.base.string;

import internal.test.ErrorAppender;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.string.NameFormatException;
import space.sunqian.fs.base.string.NameFormatter;
import space.sunqian.fs.base.value.Span;
import space.sunqian.fs.collect.ArrayKit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NameTest {

    @Test
    public void testSplit() {
        {
            // file
            NameFormatter format = NameFormatter.fileNaming();
            assertArrayEquals(format.parse("a.b.c"), ArrayKit.array("a.b", "c"));
            assertArrayEquals(format.parse(""), ArrayKit.array(""));
            assertArrayEquals(format.parse("."), ArrayKit.array("."));
            assertArrayEquals(format.parse("a."), ArrayKit.array("a."));
            assertArrayEquals(format.parse("a.b"), ArrayKit.array("a", "b"));
        }
        {
            // delimiter "-"
            NameFormatter format = NameFormatter.delimiter("-");
            assertArrayEquals(format.parse("a-b-c"), ArrayKit.array("a", "b", "c"));
            assertArrayEquals(format.parse("a-b"), ArrayKit.array("a", "b"));
            assertArrayEquals(format.parse("a-b-"), ArrayKit.array("a", "b", ""));
            assertArrayEquals(format.parse("-a-b-"), ArrayKit.array("", "a", "b", ""));
            assertArrayEquals(format.parse("-"), ArrayKit.array("", ""));
            assertArrayEquals(format.parse("a"), ArrayKit.array("a"));
            assertArrayEquals(format.parse(""), ArrayKit.array(""));
        }
        {
            // delimiter "--"
            NameFormatter format = NameFormatter.delimiter("--");
            assertArrayEquals(format.parse("a--b--c"), ArrayKit.array("a", "b", "c"));
            assertArrayEquals(format.parse("a--b"), ArrayKit.array("a", "b"));
            assertArrayEquals(format.parse("a--b--"), ArrayKit.array("a", "b", ""));
            assertArrayEquals(format.parse("--a--b--"), ArrayKit.array("", "a", "b", ""));
            assertArrayEquals(format.parse("--"), ArrayKit.array("", ""));
            assertArrayEquals(format.parse("a"), ArrayKit.array("a"));
            assertArrayEquals(format.parse(""), ArrayKit.array(""));
            assertArrayEquals(format.parse("-"), ArrayKit.array("-"));
            assertArrayEquals(format.parse("a--b-c"), ArrayKit.array("a", "b-c"));
        }
        {
            // delimiter error
            assertThrows(IllegalArgumentException.class, () -> NameFormatter.delimiter(""));
        }
        {
            // lower camel
            NameFormatter format = NameFormatter.lowerCamel();
            assertArrayEquals(format.parse("aBc"), ArrayKit.array("a", "Bc"));
            assertArrayEquals(format.parse("aBcD"), ArrayKit.array("a", "Bc", "D"));
            assertArrayEquals(format.parse("aBcDe"), ArrayKit.array("a", "Bc", "De"));
            assertArrayEquals(format.parse("aBcD0"), ArrayKit.array("a", "Bc", "D", "0"));
            assertArrayEquals(format.parse("aBcDe0"), ArrayKit.array("a", "Bc", "De", "0"));
            assertArrayEquals(format.parse("0aBcDe0"), ArrayKit.array("0", "a", "Bc", "De", "0"));
            assertArrayEquals(format.parse("01aaBcDe0"), ArrayKit.array("01", "aa", "Bc", "De", "0"));
            assertArrayEquals(format.parse("01AABcDe0"), ArrayKit.array("01", "AA", "Bc", "De", "0"));
            assertArrayEquals(format.parse("01AaBcDe0"), ArrayKit.array("01", "Aa", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AaBcDe0"), ArrayKit.array("Aa", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AABcDe0"), ArrayKit.array("AA", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA0BcDe0"), ArrayKit.array("AA", "0", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA00BcDe0"), ArrayKit.array("AA", "00", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA中BcDe0"), ArrayKit.array("AA", "中", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA0中BcDe0"), ArrayKit.array("AA", "0", "中", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA0中BcDe中"), ArrayKit.array("AA", "0", "中", "Bc", "De", "中"));
            assertArrayEquals(
                format.parse("AaBcDe/中01AabBb"),
                ArrayKit.array("Aa", "Bc", "De", "/中", "01", "Aab", "Bb")
            );
        }
        {
            // upper camel
            NameFormatter format = NameFormatter.upperCamel();
            assertArrayEquals(format.parse("ABc"), ArrayKit.array("A", "Bc"));
            assertArrayEquals(format.parse("ABcD"), ArrayKit.array("A", "Bc", "D"));
            assertArrayEquals(format.parse("AaBcDe"), ArrayKit.array("Aa", "Bc", "De"));
            assertArrayEquals(format.parse("AaBcD0"), ArrayKit.array("Aa", "Bc", "D", "0"));
            assertArrayEquals(format.parse("AaBcDe0"), ArrayKit.array("Aa", "Bc", "De", "0"));
            assertArrayEquals(format.parse("aBc"), ArrayKit.array("a", "Bc"));
            assertArrayEquals(format.parse("aBcD"), ArrayKit.array("a", "Bc", "D"));
            assertArrayEquals(format.parse("aBcDe"), ArrayKit.array("a", "Bc", "De"));
            assertArrayEquals(format.parse("aBcD0"), ArrayKit.array("a", "Bc", "D", "0"));
            assertArrayEquals(format.parse("aBcDe0"), ArrayKit.array("a", "Bc", "De", "0"));
            assertArrayEquals(format.parse("0aBcDe0"), ArrayKit.array("0", "a", "Bc", "De", "0"));
            assertArrayEquals(format.parse("01aaBcDe0"), ArrayKit.array("01", "aa", "Bc", "De", "0"));
            assertArrayEquals(format.parse("01AABcDe0"), ArrayKit.array("01", "AA", "Bc", "De", "0"));
            assertArrayEquals(format.parse("01AaBcDe0"), ArrayKit.array("01", "Aa", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AaBcDe0"), ArrayKit.array("Aa", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AABcDe0"), ArrayKit.array("AA", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA0BcDe0"), ArrayKit.array("AA", "0", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA00BcDe0"), ArrayKit.array("AA", "00", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA中BcDe0"), ArrayKit.array("AA", "中", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA0中BcDe0"), ArrayKit.array("AA", "0", "中", "Bc", "De", "0"));
            assertArrayEquals(format.parse("AA0中BcDe中"), ArrayKit.array("AA", "0", "中", "Bc", "De", "中"));
            assertArrayEquals(
                format.parse("AaBcDe/中01AabBb"),
                ArrayKit.array("Aa", "Bc", "De", "/中", "01", "Aab", "Bb")
            );
        }
        {
            // camel empty
            NameFormatter format = NameFormatter.lowerCamel();
            assertArrayEquals(format.parse(""), ArrayKit.array(""));
            assertArrayEquals(format.parse("a"), ArrayKit.array("a"));
            assertArrayEquals(format.parse("A"), ArrayKit.array("A"));
            assertArrayEquals(format.parse("0"), ArrayKit.array("0"));
            assertArrayEquals(format.parse("中"), ArrayKit.array("中"));
            assertArrayEquals(format.parse("0中a"), ArrayKit.array("0", "中", "a"));
        }
    }

    @Test
    public void testJoin() {
        {
            // file
            NameFormatter format = NameFormatter.fileNaming();
            assertEquals(
                "ab.c",
                format.format("a", "b", "c")
            );
            assertEquals(
                "a.b",
                format.format("a", "b")
            );
            assertEquals(
                "a",
                format.format("a")
            );
            assertEquals(
                "",
                format.format("")
            );
            assertEquals(
                "",
                format.format()
            );
            assertEquals(
                "",
                format.format("", format)
            );
            assertEquals(
                "a.b.c",
                format.format("a.b.c", NameFormatter.fileNaming())
            );
            assertEquals(
                "a.b",
                format.format("a.b", NameFormatter.fileNaming())
            );
            assertEquals(
                "a",
                format.format("a", NameFormatter.fileNaming())
            );
            assertEquals(
                "ab.c",
                format.format("abc", new Span[]{Span.of(0, 1), Span.of(1, 2), Span.of(2, 3)})
            );
            assertEquals(
                "",
                format.format("abc", new Span[0])
            );
            assertThrows(NameFormatException.class, () ->
                format.format(ArrayKit.array("a"), new ErrorAppender()));
            assertThrows(NameFormatException.class, () ->
                format.format("a", new Span[]{Span.of(0, 1)}, new ErrorAppender()));
        }
        {
            // delimiter
            NameFormatter format = NameFormatter.delimiter("-");
            assertEquals(
                "a-b-c",
                format.format("a", "b", "c")
            );
            assertEquals(
                "a-b",
                format.format("a", "b")
            );
            assertEquals(
                "a",
                format.format("a")
            );
            assertEquals(
                "",
                format.format("")
            );
            assertEquals(
                "",
                format.format()
            );
            assertEquals(
                "",
                format.format("", format)
            );
            assertEquals(
                "a.b.c",
                format.format("a-b-c", NameFormatter.delimiter("."))
            );
            assertEquals(
                "a.b",
                format.format("a-b", NameFormatter.delimiter("."))
            );
            assertEquals(
                "a",
                format.format("a", NameFormatter.delimiter("."))
            );
            assertEquals(
                "a-b-c",
                format.format("abc", new Span[]{Span.of(0, 1), Span.of(1, 2), Span.of(2, 3)})
            );
            assertEquals(
                "a-b--c",
                format.format("abc", new Span[]{Span.of(0, 1), Span.of(1, 2), Span.of(2, 2), Span.of(2, 3)})
            );
            assertEquals(
                "",
                format.format("abc", new Span[0])
            );
            assertThrows(NameFormatException.class, () ->
                format.format(ArrayKit.array("a"), new ErrorAppender()));
            assertThrows(NameFormatException.class, () ->
                format.format("a", new Span[]{Span.of(0, 1)}, new ErrorAppender()));
        }
        {
            // upper camel
            NameFormatter format = NameFormatter.upperCamel();
            assertEquals(
                "AaBCc",
                format.format("aa", "b", "cc")
            );
            assertEquals(
                "AaBBCc",
                format.format("aa", "BB", "cc")
            );
            assertEquals(
                "AB",
                format.format("a", "b")
            );
            assertEquals(
                "A",
                format.format("a")
            );
            assertEquals(
                "",
                format.format("")
            );
            assertEquals(
                "",
                format.format()
            );
            assertEquals(
                "",
                format.format("", format)
            );
            assertEquals(
                "someName",
                format.format("someName", NameFormatter.lowerCamel())
            );
            assertEquals(
                "someName",
                format.format("SomeName", NameFormatter.lowerCamel())
            );
            assertEquals(
                "SOMEName",
                format.format("SOMEName", NameFormatter.lowerCamel())
            );
            assertEquals(
                "SomeName",
                format.format("someName", new Span[]{Span.of(0, 4), Span.of(4, 8)})
            );
            assertEquals(
                "SomeName",
                format.format("someName", new Span[]{Span.of(0, 4), Span.of(4, 4), Span.of(4, 8)})
            );
            assertEquals(
                "SOMEName",
                format.format("SOMEName", new Span[]{Span.of(0, 4), Span.of(4, 8)})
            );
            assertEquals(
                "SOMENAME",
                format.format("SOMENAME", new Span[]{Span.of(0, 4), Span.of(4, 8)})
            );
            assertEquals(
                "SomeName",
                format.format("someName", new Span[]{Span.of(0, 8)})
            );
            assertEquals(
                "",
                format.format("someName", new Span[0])
            );
            assertThrows(NameFormatException.class, () ->
                format.format(ArrayKit.array("a"), new ErrorAppender()));
            assertThrows(NameFormatException.class, () ->
                format.format("a", new Span[]{Span.of(0, 1)}, new ErrorAppender()));
        }
        {
            // lower camel
            NameFormatter format = NameFormatter.lowerCamel();
            assertEquals(
                "aaBCc",
                format.format("aa", "b", "cc")
            );
            assertEquals(
                "aaBBCc",
                format.format("aa", "BB", "cc")
            );
            assertEquals(
                "aB",
                format.format("a", "b")
            );
            assertEquals(
                "a",
                format.format("a")
            );
            assertEquals(
                "",
                format.format("")
            );
            assertEquals(
                "",
                format.format()
            );
            assertEquals(
                "",
                format.format("", format)
            );
            assertEquals(
                "SomeName",
                format.format("someName", NameFormatter.upperCamel())
            );
            assertEquals(
                "SomeName",
                format.format("SomeName", NameFormatter.upperCamel())
            );
            assertEquals(
                "SOMEName",
                format.format("SOMEName", NameFormatter.upperCamel())
            );
            assertEquals(
                "someName",
                format.format("someName", new Span[]{Span.of(0, 4), Span.of(4, 8)})
            );
            assertEquals(
                "someName",
                format.format("someName", new Span[]{Span.of(0, 4), Span.of(4, 4), Span.of(4, 8)})
            );
            assertEquals(
                "SOMEName",
                format.format("SOMEName", new Span[]{Span.of(0, 4), Span.of(4, 8)})
            );
            assertEquals(
                "SOMENAME",
                format.format("SOMENAME", new Span[]{Span.of(0, 4), Span.of(4, 8)})
            );
            assertEquals(
                "someName",
                format.format("someName", new Span[]{Span.of(0, 8)})
            );
            assertEquals(
                "",
                format.format("someName", new Span[0])
            );
            assertThrows(NameFormatException.class, () ->
                format.format(ArrayKit.array("a"), new ErrorAppender()));
            assertThrows(NameFormatException.class, () ->
                format.format("a", new Span[]{Span.of(0, 1)}, new ErrorAppender()));
        }
    }

    @Test
    public void testFormat() {
        NameFormatter lowerCase = NameFormatter.lowerCamel();
        NameFormatter upperCase = NameFormatter.upperCamel();
        NameFormatter delimiterCase = NameFormatter.delimiter("-");
        NameFormatter delimiterLower = NameFormatter.delimiter("-", true);
        NameFormatter delimiterUpper = NameFormatter.delimiter("_", false);
        NameFormatter delimiterCustom = NameFormatter.delimiter("-",
            (dst, originalName, span, index) ->
                dst.append(originalName.subSequence(span.startIndex(), span.endIndex()).toString().toUpperCase()));
        assertEquals("SomeName", lowerCase.format("someName", upperCase));
        assertEquals("some-Name", lowerCase.format("someName", delimiterCase));
        assertEquals("some-name", lowerCase.format("someName", delimiterLower));
        assertEquals("SOME_NAME", lowerCase.format("someName", delimiterUpper));
        assertEquals("SOME-NAME", lowerCase.format("someName", delimiterCustom));
    }

    @Test
    public void testException() {
        {
            // NameFormatException
            assertThrows(NameFormatException.class, () -> {
                throw new NameFormatException();
            });
            assertThrows(NameFormatException.class, () -> {
                throw new NameFormatException("");
            });
            assertThrows(NameFormatException.class, () -> {
                throw new NameFormatException("", new RuntimeException());
            });
            assertThrows(NameFormatException.class, () -> {
                throw new NameFormatException(new RuntimeException());
            });
        }
    }
}
