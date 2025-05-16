//package test;
//
//import org.jetbrains.annotations.NotNull;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import xyz.sunqian.common.base.JieString;
//import xyz.sunqian.common.coll.JieColl;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//public class StringTest {
//
//    @Test
//    public void testConcat() {
//        List<String> list = Arrays.asList(
//            "dsfasfas",
//            "fsafs",
//            "fasdf",
//            "fas",
//            "fdfsf",
//            "fsafsaf"
//        );
//        Assert.assertEquals(
//            JieString.concat(list.toArray()),
//            String.join("", list)
//        );
//        Assert.assertEquals(
//            JieString.concat(list),
//            String.join("", list)
//        );
//    }
//
//    @Test
//    public void testStartWith() {
//        String a = "123abc123";
//        Assert.assertEquals(
//            JieString.startWith(a, "456"),
//            "456" + a
//        );
//        Assert.assertEquals(
//            JieString.startWith(a, "123"),
//            a
//        );
//        Assert.assertEquals(
//            JieString.endWith(a, "456"),
//            a + "456"
//        );
//        Assert.assertEquals(
//            JieString.endWith(a, "123"),
//            a
//        );
//        Assert.assertEquals(
//            JieString.removeStart(a, "456"),
//            a
//        );
//        Assert.assertEquals(
//            JieString.removeStart(a, "123"),
//            "abc123"
//        );
//        Assert.assertEquals(
//            JieString.removeEnd(a, "456"),
//            a
//        );
//        Assert.assertEquals(
//            JieString.removeEnd(a, "123"),
//            "123abc"
//        );
//    }
//
//    @Test
//    public void testIndexOf() {
//        Assert.assertEquals(
//            JieString.indexOf("1234567890", "2"),
//            1
//        );
//        Assert.assertEquals(
//            JieString.lastIndexOf("1234567890", "2"),
//            1
//        );
//        Assert.assertEquals(
//            JieString.indexOf("12345678901234567890", "2", 9),
//            11
//        );
//        Assert.assertEquals(
//            JieString.lastIndexOf("12345678901234567890", "2", 9),
//            1
//        );
//        Assert.assertEquals(
//            JieString.indexOf("11", "11"),
//            0
//        );
//        Assert.assertEquals(
//            JieString.lastIndexOf("11", "11"),
//            0
//        );
//        Assert.assertEquals(
//            JieString.indexOf("", ""),
//            -1
//        );
//        Assert.assertEquals(
//            JieString.indexOf("1", "11"),
//            -1
//        );
//        Assert.assertEquals(
//            JieString.lastIndexOf("", ""),
//            -1
//        );
//        Assert.assertEquals(
//            JieString.lastIndexOf("1", "11"),
//            -1
//        );
//    }
//
//    @Test
//    public void testSplit() {
//        Assert.assertEquals(
//            JieColl.toStringList(JieString.split("123--123--123--", "--")),
//            Arrays.asList("123", "123", "123", "")
//        );
//        Assert.assertEquals(
//            JieColl.toStringList(JieString.split("123", "1234")),
//            Collections.emptyList()
//        );
//        Assert.assertEquals(
//            JieColl.toStringList(JieString.split("", "1234")),
//            Collections.emptyList()
//        );
//        Assert.assertEquals(
//            JieColl.toStringList(JieString.split("123", "123")),
//            Arrays.asList("", "")
//        );
//        Assert.assertEquals(
//            JieColl.toStringList(JieString.split("123--123--123----", "--")),
//            Arrays.asList("123", "123", "123", "", "")
//        );
//        Assert.assertEquals(
//            JieColl.toStringList(JieString.split("--123--123--123----", "--")),
//            Arrays.asList("", "123", "123", "123", "", "")
//        );
//    }
//
//    @Test
//    public void testReplace() {
//        Assert.assertEquals(
//            JieString.replace("123--123--123--", "--", "66"),
//            "123661236612366"
//        );
//        Assert.assertEquals(
//            JieString.replace("----123--123--123----", "--", "+++"),
//            "++++++123+++123+++123++++++"
//        );
//        Assert.assertEquals(
//            JieString.replace("-----123--123---123----", "--", "+++"),
//            "++++++-123+++123+++-123++++++"
//        );
//        Assert.assertEquals(
//            JieString.replace("-----123--123---123----", "--", ""),
//            "-123123-123"
//        );
//    }
//
//    @Test
//    public void testSubRef() {
//        Assert.assertEquals(
//            JieString.subChars("12345678", 2, 7).toString(),
//            "34567"
//        );
//        Assert.assertEquals(
//            JieString.subChars("12345678", 2, 7).charAt(3),
//            '6'
//        );
//        Assert.assertEquals(
//            JieString.subChars("12345678", 2, 7).subSequence(1, 3).toString(),
//            "45"
//        );
//    }
//
//    @Test
//    public void testEqual() {
//        Assert.assertTrue(
//            JieString.charEquals("123", "123")
//        );
//        Assert.assertTrue(
//            JieString.charEquals("123", "1123".substring(1))
//        );
//        Assert.assertFalse(
//            JieString.charEquals("1234", "123")
//        );
//        Assert.assertTrue(
//            JieString.charEquals("123", new CharSequence() {
//                @Override
//                public int length() {
//                    return 3;
//                }
//
//                @Override
//                public char charAt(int index) {
//                    if (index == 0) {
//                        return '1';
//                    }
//                    if (index == 1) {
//                        return '2';
//                    }
//                    if (index == 2) {
//                        return '3';
//                    }
//                    return ' ';
//                }
//
//                @NotNull
//                @Override
//                public CharSequence subSequence(int start, int end) {
//                    return null;
//                }
//            })
//        );
//    }
//
//    @Test
//    public void testCapitalize() {
//        Assert.assertEquals("Abc", JieString.capitalize("abc"));
//        Assert.assertEquals("Abc", JieString.capitalize("Abc"));
//        Assert.assertEquals("abc", JieString.uncapitalize("abc"));
//        Assert.assertEquals("abc", JieString.uncapitalize("Abc"));
//    }
//
//    @Test
//    public void testLazyString() {
//        int[] is = {0};
//        CharSequence lazy = JieString.lazyChars(() -> {
//            is[0] = 1;
//            return "8899";
//        });
//        Assert.assertEquals(is[0], 0);
//        Assert.assertEquals(lazy.toString(), "8899");
//        Assert.assertEquals(is[0], 1);
//    }
//}
