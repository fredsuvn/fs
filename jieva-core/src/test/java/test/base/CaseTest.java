// package test;
//
// import com.google.common.base.CaseFormat;
// import org.testng.Assert;
// import org.testng.annotations.Test;
// import xyz.sunqian.common.base.CaseFormatter;
// import xyz.sunqian.common.base.JieString;
// import xyz.sunqian.common.coll.JieColl;
//
// import java.util.Arrays;
//
// public class CaseTest {
//
//    @Test
//    public void testCamelCase() {
//        Assert.assertEquals(CaseFormatter.UPPER_CAMEL.to(CaseFormatter.LOWER_CAMEL, "AaBbCcc"), "aaBbCcc");
//        Assert.assertEquals(CaseFormatter.LOWER_CAMEL.to(CaseFormatter.UPPER_CAMEL, "aaBbCcc"), "AaBbCcc");
//        Assert.assertEquals(CaseFormatter.UPPER_CAMEL.to(CaseFormatter.LOWER_CAMEL, "AABbCcc"), "AABbCcc");
//        Assert.assertEquals(CaseFormatter.LOWER_CAMEL.to(CaseFormatter.UPPER_CAMEL, "AABbCcc"), "AABbCcc");
//        Assert.assertEquals(CaseFormatter.UPPER_CAMEL.to(CaseFormatter.LOWER_CAMEL, "A0BbCcc"), "a0BbCcc");
//        Assert.assertEquals(CaseFormatter.LOWER_CAMEL.to(CaseFormatter.UPPER_CAMEL, "a0BbCcc"), "A0BbCcc");
//        Assert.assertEquals(CaseFormatter.UPPER_CAMEL.to(CaseFormatter.LOWER_CAMEL, "AaBbCCcc"), "aaBbCCcc");
//        Assert.assertEquals(CaseFormatter.LOWER_CAMEL.to(CaseFormatter.UPPER_CAMEL, "aaBbCCcc"), "AaBbCCcc");
//        Assert.assertEquals(
//            JieColl.toStringList(CaseFormatter.UPPER_CAMEL.resolve("AAAAABBBBBCCCCCcccccDDEe")),
//            Arrays.asList("AAAAABBBBBCCCC", "Cccccc", "DD", "Ee")
//        );
//
//        Assert.assertEquals(CaseFormatter.UPPER_CAMEL.to(CaseFormatter.LOWER_CAMEL, "AAA"), "AAA");
//        Assert.assertEquals(CaseFormatter.UPPER_CAMEL.to(CaseFormatter.LOWER_CAMEL, "AAa"), "aAa");
//        Assert.assertEquals(CaseFormatter.UPPER_CAMEL.to(CaseFormatter.UPPER_CAMEL, "aAa"), "AAa");
//        Assert.assertEquals(CaseFormatter.LOWER_CAMEL.to(CaseFormatter.LOWER_CAMEL, "AAa"), "aAa");
//    }
//
//    @Test
//    public void testDelimiterCase() {
//        Assert.assertEquals(CaseFormatter.LOWER_UNDERSCORE.to(CaseFormatter.LOWER_UNDERSCORE, "aa_bb_cc"), "aa_bb_cc");
//        Assert.assertEquals(CaseFormatter.LOWER_UNDERSCORE.to(CaseFormatter.UPPER_UNDERSCORE, "aa_bb_cc"), "AA_BB_CC");
//        Assert.assertEquals(CaseFormatter.UPPER_UNDERSCORE.to(CaseFormatter.LOWER_UNDERSCORE, "AA_BB_CC"), "aa_bb_cc");
//        Assert.assertEquals(CaseFormatter.UPPER_UNDERSCORE.to(CaseFormatter.UPPER_UNDERSCORE, "AA_BB_CC"), "AA_BB_CC");
//        Assert.assertEquals(CaseFormatter.LOWER_UNDERSCORE.to(CaseFormatter.UPPER_HYPHEN, "aa_bb_cc"), "AA-BB-CC");
//        Assert.assertEquals(CaseFormatter.UPPER_HYPHEN.to(CaseFormatter.LOWER_UNDERSCORE, "AA-BB-CC"), "aa_bb_cc");
//        Assert.assertEquals(CaseFormatter.LOWER_UNDERSCORE.to(CaseFormatter.LOWER_HYPHEN, "aa_bb_cc_"), "aa-bb-cc-");
//        Assert.assertEquals(CaseFormatter.LOWER_HYPHEN.to(CaseFormatter.LOWER_UNDERSCORE, "-aa-bb-cc-"), "_aa_bb_cc_");
//        Assert.assertEquals(CaseFormatter.LOWER_UNDERSCORE.to(CaseFormatter.LOWER_UNDERSCORE, "AA_bb_cc"), "aa_bb_cc");
//        Assert.assertEquals(CaseFormatter.UPPER_UNDERSCORE.to(CaseFormatter.UPPER_UNDERSCORE, "aa_BB_CC"), "AA_BB_CC");
//        Assert.assertEquals(
//            CaseFormatter.delimiterCase("_", JieString::upperCase).to(
//                CaseFormatter.delimiterCase("-", JieString::upperCase), "aa_bb_cc"),
//            "AA-BB-CC"
//        );
//        Assert.assertEquals(
//            CaseFormatter.delimiterCase("_", JieString::lowerCase).to(
//                CaseFormatter.delimiterCase("-", JieString::lowerCase), "AA_BB_CC"),
//            "aa-bb-cc"
//        );
//        //Guava bug!
//        // Assert.assertEquals(
//        //     CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_UNDERSCORE, "AA_BB_CC"),
//        //     "aa_bb_cc");
//        Assert.assertEquals(CaseFormatter.UNDERSCORE.to(CaseFormatter.HYPHEN, "aa_bb_Cc"), "aa-bb-Cc");
//        Assert.assertEquals(CaseFormatter.HYPHEN.to(CaseFormatter.UNDERSCORE, "aa-Bb-cc"), "aa_Bb_cc");
//    }
//
//    @Test
//    public void testMixCase() {
//        Assert.assertEquals(CaseFormatter.UPPER_UNDERSCORE.to(CaseFormatter.UPPER_CAMEL, "aa_bb_cc"), "AaBbCc");
//        Assert.assertEquals(CaseFormatter.LOWER_HYPHEN.to(CaseFormatter.LOWER_CAMEL, "aa-bb-cc-"), "aaBbCc");
//        //has an empty part
//        Assert.assertEquals(CaseFormatter.UPPER_HYPHEN.to(CaseFormatter.LOWER_CAMEL, "-aa-bb-cc-"), "AaBbCc");
//
//        Assert.assertEquals(CaseFormatter.UPPER_CAMEL.to(CaseFormatter.LOWER_HYPHEN, "AaBbCcc"), "aa-bb-ccc");
//        Assert.assertEquals(CaseFormatter.UPPER_CAMEL.to(CaseFormatter.UPPER_UNDERSCORE, "A0BbCcc"), "A0_BB_CCC");
//        Assert.assertEquals(
//            CaseFormatter.UPPER_UNDERSCORE.to(CaseFormatter.LOWER_CAMEL, "get_Simple_Name_Options_Builder"),
//            "getSimpleNameOptionsBuilder");
//        Assert.assertEquals(
//            CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "get_Simple_Name_Options_Builder"),
//            "getSimpleNameOptionsBuilder");
//    }
//
/// /    @Test /    public void testNumberCase() { /        CaseFormatter case1 = CaseFormatter.camelCase(true,
/// CaseFormatter.AS_INDEPENDENT); /        Assert.assertEquals(case1.to("aaBbCc00cC0", CaseFormatter.LOWER_HYPHEN),
/// "aa-bb-cc-00-c-c-0"); /        CaseFormatter case2 = CaseFormatter.camelCase(true, CaseFormatter.AS_INDEPENDENT); /
/// Assert.assertEquals(case2.to("aaBbCc00cC0", CaseFormatter.LOWER_CAMEL), "aaBbCc00CC0"); /        CaseFormatter case3
/// = CaseFormatter.camelCase(true, CaseFormatter.AS_UPPER); /        Assert.assertEquals(case3.to("aaBbCc00cC0",
/// CaseFormatter.LOWER_CAMEL), "aaBbCc00cC0"); /    }
//}
