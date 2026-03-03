package tests.benchmarks;

import internal.tests.api.PropertiesCopier;
import internal.tests.common.TestPropsData;
import internal.tests.common.TestPropsTarget;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertiesCopierTest {

    @Test
    public void testCopier() throws Exception {
        testCopier("fs", true);
        testCopier("fs-newInstMode", true);
        testCopier("spring", true);
        testCopier("apache", true);
        testCopier("hutool", true);
        testCopier("direct", true);
        testCopier("fs", false);
        testCopier("fs-newInstMode", false);
        testCopier("spring", false);
        testCopier("apache", false);
        testCopier("hutool", false);
        testCopier("direct", false);
    }

    private void testCopier(String copierType, boolean format) throws Exception {
        TestPropsData data = new TestPropsData();
        data.setI1(1);
        data.setL1(2L);
        data.setStr1("hello");
        data.setIi1(3);
        data.setLl1(4L);
        data.setBb1(new BigDecimal("5.0"));
        data.setI2(1);
        data.setL2(2L);
        data.setStr2("hello");
        data.setIi2(3);
        data.setLl2(4L);
        data.setBb2(new BigDecimal("5.0"));
        Date now = new Date();
        String nowStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
        if (format) {
            data.setFmt1(nowStr);
            data.setFmt2(nowStr);
            data.setFmt3(nowStr);
            data.setFmt4(nowStr);
        }
        TestPropsTarget target = new TestPropsTarget();
        PropertiesCopier copier = PropertiesCopier.createCopier(copierType);
        copier.copyProperties(data, target, format);
        assertEquals(data.getI1(), target.getI1());
        assertEquals(data.getL1(), target.getL1());
        assertEquals(data.getStr1(), target.getStr1());
        assertEquals(data.getIi1(), target.getIi1());
        assertEquals(data.getLl1(), target.getLl1());
        assertEquals(data.getBb1(), target.getBb1());
        assertEquals(data.getIi2(), target.getIi2());
        assertEquals(data.getLl2(), target.getLl2());
        assertEquals(data.getStr2(), target.getStr2());
        assertEquals(data.getIi2(), target.getIi2());
        assertEquals(data.getLl2(), target.getLl2());
        assertEquals(data.getBb2(), target.getBb2());
        if (format) {
            Date nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nowStr);
            assertEquals(nowDate, target.getFmt1());
            assertEquals(nowDate, target.getFmt2());
            assertEquals(nowDate, target.getFmt3());
            assertEquals(nowDate, target.getFmt4());
        }
    }
}
