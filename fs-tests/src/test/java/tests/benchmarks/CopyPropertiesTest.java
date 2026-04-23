package tests.benchmarks;

import internal.api.PropertiesCopier;
import internal.data.TestPropsData;
import internal.data.TestPropsTarget;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CopyPropertiesTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testPropertiesCopierWithDifferentImplementations() throws Exception {
        // Test with date formatting
        testCopierImplementation("fs", true);
        testCopierImplementation("fs-newInstMode", true);
        testCopierImplementation("spring", true);
        testCopierImplementation("apache", true);
        testCopierImplementation("hutool", true);
        testCopierImplementation("direct", true);

        // Test without date formatting
        testCopierImplementation("fs", false);
        testCopierImplementation("fs-newInstMode", false);
        testCopierImplementation("spring", false);
        testCopierImplementation("apache", false);
        testCopierImplementation("hutool", false);
        testCopierImplementation("direct", false);
    }

    private void testCopierImplementation(String copierType, boolean format) throws Exception {
        TestPropsData source = createTestData(format);
        TestPropsTarget target = new TestPropsTarget();

        PropertiesCopier copier = PropertiesCopier.createCopier(copierType);
        copier.copyProperties(source, target, format);

        verifyCopiedProperties(source, target, format);
    }

    private TestPropsData createTestData(boolean format) {
        TestPropsData data = new TestPropsData();

        // Set common properties
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

        // Set date format properties if needed
        if (format) {
            String nowStr = DATE_FORMAT.format(new Date());
            data.setFmt1(nowStr);
            data.setFmt2(nowStr);
            data.setFmt3(nowStr);
            data.setFmt4(nowStr);
        }

        return data;
    }

    private void verifyCopiedProperties(TestPropsData source, TestPropsTarget target, boolean format) throws Exception {
        // Verify common properties
        assertEquals(source.getI1(), target.getI1());
        assertEquals(source.getL1(), target.getL1());
        assertEquals(source.getStr1(), target.getStr1());
        assertEquals(source.getIi1(), target.getIi1());
        assertEquals(source.getLl1(), target.getLl1());
        assertEquals(source.getBb1(), target.getBb1());
        assertEquals(source.getI2(), target.getI2());
        assertEquals(source.getL2(), target.getL2());
        assertEquals(source.getStr2(), target.getStr2());
        assertEquals(source.getIi2(), target.getIi2());
        assertEquals(source.getLl2(), target.getLl2());
        assertEquals(source.getBb2(), target.getBb2());

        // Verify date format properties if needed
        if (format) {
            Date nowDate = DATE_FORMAT.parse(source.getFmt1());
            assertEquals(nowDate, target.getFmt1());
            assertEquals(nowDate, target.getFmt2());
            assertEquals(nowDate, target.getFmt3());
            assertEquals(nowDate, target.getFmt4());
        }
    }
}
