package tests.base.string;

import org.testng.annotations.Test;
import space.sunqian.common.base.string.NameFormatException;
import space.sunqian.common.base.string.NameFormatter;
import space.sunqian.common.base.value.Span;
import space.sunqian.common.collect.ArrayKit;
import space.sunqian.test.ErrorAppender;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class NameFormatterTest {

    @Test
    public void testSplit() {
        {
            // file
            NameFormatter format = NameFormatter.fileNaming();
            assertEquals(format.parse("a.b.c"), ArrayKit.array("a.b", "c"));
            assertEquals(format.parse(""), ArrayKit.array(""));
            assertEquals(format.parse("."), ArrayKit.array("."));
            assertEquals(format.parse("a."), ArrayKit.array("a."));
            assertEquals(format.parse("a.b"), ArrayKit.array("a", "b"));
        }
        {
            // delimiter "-"
            NameFormatter format = NameFormatter.delimiterCase("-");
            assertEquals(format.parse("a-b-c"), ArrayKit.array("a", "b", "c"));
            assertEquals(format.parse("a-b"), ArrayKit.array("a", "b"));
            assertEquals(format.parse("a-b-"), ArrayKit.array("a", "b", ""));
            assertEquals(format.parse("-a-b-"), ArrayKit.array("", "a", "b", ""));
            assertEquals(format.parse("-"), ArrayKit.array("", ""));
            assertEquals(format.parse("a"), ArrayKit.array("a"));
            assertEquals(format.parse(""), ArrayKit.array(""));
        }
        {
            // delimiter "--"
            NameFormatter format = NameFormatter.delimiterCase("--");
            assertEquals(format.parse("a--b--c"), ArrayKit.array("a", "b", "c"));
            assertEquals(format.parse("a--b"), ArrayKit.array("a", "b"));
            assertEquals(format.parse("a--b--"), ArrayKit.array("a", "b", ""));
            assertEquals(format.parse("--a--b--"), ArrayKit.array("", "a", "b", ""));
            assertEquals(format.parse("--"), ArrayKit.array("", ""));
            assertEquals(format.parse("a"), ArrayKit.array("a"));
            assertEquals(format.parse(""), ArrayKit.array(""));
            assertEquals(format.parse("-"), ArrayKit.array("-"));
            assertEquals(format.parse("a--b-c"), ArrayKit.array("a", "b-c"));
        }
        {
            // delimiter error
            expectThrows(IllegalArgumentException.class, () -> NameFormatter.delimiterCase(""));
        }
        {
            // lower camel
            NameFormatter format = NameFormatter.lowerCamel();
            assertEquals(format.parse("aBc"), ArrayKit.array("a", "Bc"));
            assertEquals(format.parse("aBcD"), ArrayKit.array("a", "Bc", "D"));
            assertEquals(format.parse("aBcDe"), ArrayKit.array("a", "Bc", "De"));
            assertEquals(format.parse("aBcD0"), ArrayKit.array("a", "Bc", "D", "0"));
            assertEquals(format.parse("aBcDe0"), ArrayKit.array("a", "Bc", "De", "0"));
            assertEquals(format.parse("0aBcDe0"), ArrayKit.array("0", "a", "Bc", "De", "0"));
            assertEquals(format.parse("01aaBcDe0"), ArrayKit.array("01", "aa", "Bc", "De", "0"));
            assertEquals(format.parse("01AABcDe0"), ArrayKit.array("01", "AA", "Bc", "De", "0"));
            assertEquals(format.parse("01AaBcDe0"), ArrayKit.array("01", "Aa", "Bc", "De", "0"));
            assertEquals(format.parse("AaBcDe0"), ArrayKit.array("Aa", "Bc", "De", "0"));
            assertEquals(format.parse("AABcDe0"), ArrayKit.array("AA", "Bc", "De", "0"));
            assertEquals(format.parse("AA0BcDe0"), ArrayKit.array("AA", "0", "Bc", "De", "0"));
            assertEquals(format.parse("AA00BcDe0"), ArrayKit.array("AA", "00", "Bc", "De", "0"));
            assertEquals(format.parse("AA中BcDe0"), ArrayKit.array("AA", "中", "Bc", "De", "0"));
            assertEquals(format.parse("AA0中BcDe0"), ArrayKit.array("AA", "0", "中", "Bc", "De", "0"));
            assertEquals(format.parse("AA0中BcDe中"), ArrayKit.array("AA", "0", "中", "Bc", "De", "中"));
            assertEquals(
                format.parse("AaBcDe/中01AabBb"),
                ArrayKit.array("Aa", "Bc", "De", "/中", "01", "Aab", "Bb")
            );
        }
        {
            // upper camel
            NameFormatter format = NameFormatter.upperCamel();
            assertEquals(format.parse("ABc"), ArrayKit.array("A", "Bc"));
            assertEquals(format.parse("ABcD"), ArrayKit.array("A", "Bc", "D"));
            assertEquals(format.parse("AaBcDe"), ArrayKit.array("Aa", "Bc", "De"));
            assertEquals(format.parse("AaBcD0"), ArrayKit.array("Aa", "Bc", "D", "0"));
            assertEquals(format.parse("AaBcDe0"), ArrayKit.array("Aa", "Bc", "De", "0"));
            assertEquals(format.parse("aBc"), ArrayKit.array("a", "Bc"));
            assertEquals(format.parse("aBcD"), ArrayKit.array("a", "Bc", "D"));
            assertEquals(format.parse("aBcDe"), ArrayKit.array("a", "Bc", "De"));
            assertEquals(format.parse("aBcD0"), ArrayKit.array("a", "Bc", "D", "0"));
            assertEquals(format.parse("aBcDe0"), ArrayKit.array("a", "Bc", "De", "0"));
            assertEquals(format.parse("0aBcDe0"), ArrayKit.array("0", "a", "Bc", "De", "0"));
            assertEquals(format.parse("01aaBcDe0"), ArrayKit.array("01", "aa", "Bc", "De", "0"));
            assertEquals(format.parse("01AABcDe0"), ArrayKit.array("01", "AA", "Bc", "De", "0"));
            assertEquals(format.parse("01AaBcDe0"), ArrayKit.array("01", "Aa", "Bc", "De", "0"));
            assertEquals(format.parse("AaBcDe0"), ArrayKit.array("Aa", "Bc", "De", "0"));
            assertEquals(format.parse("AABcDe0"), ArrayKit.array("AA", "Bc", "De", "0"));
            assertEquals(format.parse("AA0BcDe0"), ArrayKit.array("AA", "0", "Bc", "De", "0"));
            assertEquals(format.parse("AA00BcDe0"), ArrayKit.array("AA", "00", "Bc", "De", "0"));
            assertEquals(format.parse("AA中BcDe0"), ArrayKit.array("AA", "中", "Bc", "De", "0"));
            assertEquals(format.parse("AA0中BcDe0"), ArrayKit.array("AA", "0", "中", "Bc", "De", "0"));
            assertEquals(format.parse("AA0中BcDe中"), ArrayKit.array("AA", "0", "中", "Bc", "De", "中"));
            assertEquals(
                format.parse("AaBcDe/中01AabBb"),
                ArrayKit.array("Aa", "Bc", "De", "/中", "01", "Aab", "Bb")
            );
        }
        {
            // camel empty
            NameFormatter format = NameFormatter.lowerCamel();
            assertEquals(format.parse(""), ArrayKit.array(""));
            assertEquals(format.parse("a"), ArrayKit.array("a"));
            assertEquals(format.parse("A"), ArrayKit.array("A"));
            assertEquals(format.parse("0"), ArrayKit.array("0"));
            assertEquals(format.parse("中"), ArrayKit.array("中"));
            assertEquals(format.parse("0中a"), ArrayKit.array("0", "中", "a"));
        }
    }

    @Test
    public void testJoin() {
        {
            // file
            NameFormatter format = NameFormatter.fileNaming();
            assertEquals(
                format.format("a", "b", "c"),
                "ab.c"
            );
            assertEquals(
                format.format("a", "b"),
                "a.b"
            );
            assertEquals(
                format.format("a"),
                "a"
            );
            assertEquals(
                format.format(""),
                ""
            );
            assertEquals(
                format.format(),
                ""
            );
            assertEquals(
                format.format("", format),
                ""
            );
            assertEquals(
                format.format("a.b.c", NameFormatter.fileNaming()),
                "a.b.c"
            );
            assertEquals(
                format.format("a.b", NameFormatter.fileNaming()),
                "a.b"
            );
            assertEquals(
                format.format("a", NameFormatter.fileNaming()),
                "a"
            );
            assertEquals(
                format.format("abc", new Span[]{Span.of(0, 1), Span.of(1, 2), Span.of(2, 3)}),
                "ab.c"
            );
            assertEquals(
                format.format("abc", new Span[0]),
                ""
            );
            expectThrows(NameFormatException.class, () ->
                format.format(ArrayKit.array("a"), new ErrorAppender()));
            expectThrows(NameFormatException.class, () ->
                format.format("a", new Span[]{Span.of(0, 1)}, new ErrorAppender()));
        }
        {
            // delimiter
            NameFormatter format = NameFormatter.delimiterCase("-");
            assertEquals(
                format.format("a", "b", "c"),
                "a-b-c"
            );
            assertEquals(
                format.format("a", "b"),
                "a-b"
            );
            assertEquals(
                format.format("a"),
                "a"
            );
            assertEquals(
                format.format(""),
                ""
            );
            assertEquals(
                format.format(),
                ""
            );
            assertEquals(
                format.format("", format),
                ""
            );
            assertEquals(
                format.format("a-b-c", NameFormatter.delimiterCase(".")),
                "a.b.c"
            );
            assertEquals(
                format.format("a-b", NameFormatter.delimiterCase(".")),
                "a.b"
            );
            assertEquals(
                format.format("a", NameFormatter.delimiterCase(".")),
                "a"
            );
            assertEquals(
                format.format("abc", new Span[]{Span.of(0, 1), Span.of(1, 2), Span.of(2, 3)}),
                "a-b-c"
            );
            assertEquals(
                format.format("abc", new Span[]{Span.of(0, 1), Span.of(1, 2), Span.of(2, 2), Span.of(2, 3)}),
                "a-b--c"
            );
            assertEquals(
                format.format("abc", new Span[0]),
                ""
            );
            expectThrows(NameFormatException.class, () ->
                format.format(ArrayKit.array("a"), new ErrorAppender()));
            expectThrows(NameFormatException.class, () ->
                format.format("a", new Span[]{Span.of(0, 1)}, new ErrorAppender()));
        }
        {
            // upper camel
            NameFormatter format = NameFormatter.upperCamel();
            assertEquals(
                format.format("aa", "b", "cc"),
                "AaBCc"
            );
            assertEquals(
                format.format("aa", "BB", "cc"),
                "AaBBCc"
            );
            assertEquals(
                format.format("a", "b"),
                "AB"
            );
            assertEquals(
                format.format("a"),
                "A"
            );
            assertEquals(
                format.format(""),
                ""
            );
            assertEquals(
                format.format(),
                ""
            );
            assertEquals(
                format.format("", format),
                ""
            );
            assertEquals(
                format.format("someName", NameFormatter.lowerCamel()),
                "someName"
            );
            assertEquals(
                format.format("SomeName", NameFormatter.lowerCamel()),
                "someName"
            );
            assertEquals(
                format.format("SOMEName", NameFormatter.lowerCamel()),
                "SOMEName"
            );
            assertEquals(
                format.format("someName", new Span[]{Span.of(0, 4), Span.of(4, 8)}),
                "SomeName"
            );
            assertEquals(
                format.format("someName", new Span[]{Span.of(0, 4), Span.of(4, 4), Span.of(4, 8)}),
                "SomeName"
            );
            assertEquals(
                format.format("SOMEName", new Span[]{Span.of(0, 4), Span.of(4, 8)}),
                "SOMEName"
            );
            assertEquals(
                format.format("SOMENAME", new Span[]{Span.of(0, 4), Span.of(4, 8)}),
                "SOMENAME"
            );
            assertEquals(
                format.format("someName", new Span[]{Span.of(0, 8)}),
                "SomeName"
            );
            assertEquals(
                format.format("someName", new Span[0]),
                ""
            );
            expectThrows(NameFormatException.class, () ->
                format.format(ArrayKit.array("a"), new ErrorAppender()));
            expectThrows(NameFormatException.class, () ->
                format.format("a", new Span[]{Span.of(0, 1)}, new ErrorAppender()));
        }
        {
            // lower camel
            NameFormatter format = NameFormatter.lowerCamel();
            assertEquals(
                format.format("aa", "b", "cc"),
                "aaBCc"
            );
            assertEquals(
                format.format("aa", "BB", "cc"),
                "aaBBCc"
            );
            assertEquals(
                format.format("a", "b"),
                "aB"
            );
            assertEquals(
                format.format("a"),
                "a"
            );
            assertEquals(
                format.format(""),
                ""
            );
            assertEquals(
                format.format(),
                ""
            );
            assertEquals(
                format.format("", format),
                ""
            );
            assertEquals(
                format.format("someName", NameFormatter.upperCamel()),
                "SomeName"
            );
            assertEquals(
                format.format("SomeName", NameFormatter.upperCamel()),
                "SomeName"
            );
            assertEquals(
                format.format("SOMEName", NameFormatter.upperCamel()),
                "SOMEName"
            );
            assertEquals(
                format.format("someName", new Span[]{Span.of(0, 4), Span.of(4, 8)}),
                "someName"
            );
            assertEquals(
                format.format("someName", new Span[]{Span.of(0, 4), Span.of(4, 4), Span.of(4, 8)}),
                "someName"
            );
            assertEquals(
                format.format("SOMEName", new Span[]{Span.of(0, 4), Span.of(4, 8)}),
                "SOMEName"
            );
            assertEquals(
                format.format("SOMENAME", new Span[]{Span.of(0, 4), Span.of(4, 8)}),
                "SOMENAME"
            );
            assertEquals(
                format.format("someName", new Span[]{Span.of(0, 8)}),
                "someName"
            );
            assertEquals(
                format.format("someName", new Span[0]),
                ""
            );
            expectThrows(NameFormatException.class, () ->
                format.format(ArrayKit.array("a"), new ErrorAppender()));
            expectThrows(NameFormatException.class, () ->
                format.format("a", new Span[]{Span.of(0, 1)}, new ErrorAppender()));
        }
    }

    @Test
    public void testFormat() {
        NameFormatter lowerCase = NameFormatter.lowerCamel();
        NameFormatter upperCase = NameFormatter.upperCamel();
        NameFormatter delimiterCase = NameFormatter.delimiterCase("-");
        NameFormatter delimiterUpper = NameFormatter.delimiterCase("-",
            (dst, originalName, span, index) ->
                dst.append(originalName.subSequence(span.startIndex(), span.endIndex()).toString().toUpperCase()));
        assertEquals(lowerCase.format("someName", upperCase), "SomeName");
        assertEquals(lowerCase.format("someName", delimiterCase), "some-Name");
        assertEquals(lowerCase.format("someName", delimiterUpper), "SOME-NAME");
    }

    @Test
    public void testException() {
        {
            // NameFormatException
            expectThrows(NameFormatException.class, () -> {
                throw new NameFormatException();
            });
            expectThrows(NameFormatException.class, () -> {
                throw new NameFormatException("");
            });
            expectThrows(NameFormatException.class, () -> {
                throw new NameFormatException("", new RuntimeException());
            });
            expectThrows(NameFormatException.class, () -> {
                throw new NameFormatException(new RuntimeException());
            });
        }
    }
}
