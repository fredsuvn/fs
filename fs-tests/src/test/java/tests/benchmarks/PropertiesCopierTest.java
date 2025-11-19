package tests.benchmarks;

import internal.tests.common.CommonData;
import internal.tests.common.PropertiesCopier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertiesCopierTest {

    @Test
    public void testCopier() throws Exception {
        testCopier("fs");
        testCopier("apache");
        testCopier("hutool");
        testCopier("direct");
    }

    public void testCopier(String copierType) throws Exception {
        CommonData data = new CommonData();
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
        CommonData copy = new CommonData();
        PropertiesCopier copier = PropertiesCopier.createCopier(copierType);
        copier.copyProperties(data, copy);
        assertEquals(data, copy);
    }
}
