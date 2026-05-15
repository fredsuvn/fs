package internal.data;

import space.sunqian.fs.collect.ListKit;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataGenerator {

    public static TestJsonData createJsonParseTestData() {
        TestJsonData data = new TestJsonData();

        // Set properties for test data
        data.setI1(1);
        data.setL1(2L);
        data.setStr1("hello\\u0001中文");
        data.setIi1(3);
        data.setLl1(4L);
        data.setBb1(new BigDecimal("5.0"));
        data.setLa1(new long[]{1L, 2L});
        data.setBa1(new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        data.setSa1(ListKit.list("a", "b"));
        data.setI2(1);
        data.setL2(2L);
        data.setStr2("hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello");
        data.setIi2(3);
        data.setLl2(4L);
        data.setBb2(new BigDecimal("5.0"));
        data.setLa2(new long[]{1L, 2L});
        data.setBa2(new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        data.setSa2(ListKit.list("a", "b"));
        data.setI3(1);
        data.setL3(2L);
        data.setStr3("hello");
        data.setIi3(3);
        data.setLl3(4444444444444444444L);
        data.setBb3(new BigDecimal("5555555555555555555555555555555555555555555555555.5555555555555555555555555555555"));
        data.setLa3(new long[]{1L, 2L});
        data.setBa3(new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        data.setSa3(ListKit.list("a", "b"));

        try {
            Date d = new Date();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
            data.setD1(sdf1.parse(sdf1.format(d)));
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            data.setD2(sdf2.parse(sdf2.format(d)));
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            data.setD3(sdf3.parse(sdf3.format(d)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    public static Map<String, Object> createJsonParseTestMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        // Set properties for test map
        map.put("i1", 1);
        map.put("l1", 2L);
        map.put("str1", "hello\\u0001中文");
        map.put("ii1", 3);
        map.put("ll1", 4L);
        map.put("bb1", new BigDecimal("5.0"));
        map.put("la1", new long[]{1L, 2L});
        map.put("ba1", new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        map.put("sa1", ListKit.list("a", "b"));
        map.put("i2", 1);
        map.put("l2", 2L);
        map.put("str2", "hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello");
        map.put("ii2", 3);
        map.put("ll2", 4L);
        map.put("bb2", new BigDecimal("5.0"));
        map.put("la2", new long[]{1L, 2L});
        map.put("ba2", new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        map.put("sa2", ListKit.list("a", "b"));
        map.put("i3", 1);
        map.put("l3", 2L);
        map.put("str3", "hello");
        map.put("ii3", 3);
        map.put("ll3", 4444444444444444444L);
        map.put("bb3", new BigDecimal("5555555555555555555555555555555555555555555555555.5555555555555555555555555555555"));
        map.put("la3", new long[]{1L, 2L});
        map.put("ba3", new BigDecimal[]{new BigDecimal("1.0"), new BigDecimal("2.0")});
        map.put("sa3", ListKit.list("a", "b"));

        try {
            Date d = new Date();
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
            map.put("d1", sdf1.format(d));
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            map.put("d2", sdf2.format(d));
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("d3", sdf3.format(d));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return map;
    }
}
