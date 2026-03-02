package tests.benchmarks;

import internal.tests.api.PropertiesCopier;
import internal.tests.common.TestPropsData;
import internal.tests.common.TestPropsTarget;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertiesCopierTest {

    @Test
    public void testCopier() throws Exception {
        PropertiesCopier.init();
        testCopier("fs");
        testCopier("fs-instMode");
        testCopier("spring");
        testCopier("apache");
        testCopier("hutool");
        testCopier("direct");
    }

    private void testCopier(String copierType) throws Exception {
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
        data.setFmt1(now);
        data.setFmt2(nowStr);
        data.setFmt3(1111L);
        data.setFmt4(new BigDecimal("8888.0"));
        TestPropsTarget target = new TestPropsTarget();
        PropertiesCopier copier = PropertiesCopier.createCopier(copierType);
        copier.copyProperties(data, target);
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
        if ("fs".equals(copierType)) {
            assertEquals(nowStr, target.getFmt1());
            Date now2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nowStr);
            assertEquals(now2, target.getFmt2());
            String fmt3 = new DecimalFormat("#.000").format(data.getFmt3());
            assertEquals(fmt3, target.getFmt3());
            String fmt4 = new DecimalFormat("#.0000").format(data.getFmt4());
            assertEquals(fmt4, target.getFmt4());
        }
    }
}
