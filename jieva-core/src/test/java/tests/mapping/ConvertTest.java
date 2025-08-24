// package test;
//
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;
// import org.testng.Assert;
// import org.testng.annotations.Test;
// import xyz.fslabo.annotations.Nullable;
// import xyz.sunqian.common.base.Flag;
// import xyz.sunqian.common.base.Jie;
// import xyz.sunqian.common.base.time.JieDate;
// import xyz.sunqian.common.bean.PropertyInfo;
// import xyz.sunqian.common.mapping.Mapper;
// import xyz.sunqian.common.mapping.MappingOptions;
// import xyz.sunqian.common.mapping.handlers.*;
// import xyz.sunqian.common.reflect.TypeRef;
//
// import java.lang.reflect.Type;
// import java.nio.ByteBuffer;
// import java.time.*;
// import java.time.format.DateTimeFormatter;
// import java.util.*;
//
// public class ConvertTest {
//
//    @Test
//    public void testConvert() {
//        long now = System.currentTimeMillis();
//        Assert.assertEquals(
//            Instant.ofEpochMilli(now),
//            Jie.map(new Date(now), Instant.class)
//        );
//        Instant instant = Instant.ofEpochMilli(now);
//        Assert.assertSame(
//            instant,
//            Jie.map(instant, Instant.class)
//        );
//        Assert.assertEquals(
//            Arrays.asList("1", "2", "3"),
//            Mapper.defaultMapper().map(
//                Arrays.asList("1", "2", "3"), new TypeRef<List<String>>() {
//                }.getType(),
//                new TypeRef<List<String>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertEquals(
//            Arrays.asList(1, 2, 3),
//            Mapper.defaultMapper().map(
//                Arrays.asList("1", "2", "3"), new TypeRef<List<String>>() {
//                }.getType(),
//                new TypeRef<List<Integer>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertEquals(
//            Arrays.asList(1, 2, 3),
//            Mapper.defaultMapper().map(
//                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
//                }.getType(),
//                new TypeRef<List<Integer>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertEquals(
//            Arrays.asList(1, 2, 3),
//            Mapper.defaultMapper().map(
//                Arrays.asList("1", "2", "3"), new TypeRef<List<? extends String>>() {
//                }.getType(),
//                new TypeRef<List<Integer>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertEquals(
//            Arrays.asList(1, 2, 3),
//            Mapper.defaultMapper().map(
//                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
//                }.getType(),
//                new TypeRef<List<? super Integer>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertEquals(
//            Arrays.asList(1, 2, 3),
//            Mapper.defaultMapper().map(
//                Arrays.asList("1", "2", "3"), new TypeRef<List<? super String>>() {
//                }.getType(),
//                new TypeRef<List<? extends Integer>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        List<String> strList = Arrays.asList("1", "2", "3");
//        Assert.assertSame(
//            strList,
//            Mapper.defaultMapper().map(
//                strList, new TypeRef<List<? super CharSequence>>() {
//                }.getType(),
//                new TypeRef<List<? super String>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertNotSame(
//            strList,
//            Mapper.defaultMapper().map(
//                strList, new TypeRef<List<? super String>>() {
//                }.getType(),
//                new TypeRef<List<? super CharSequence>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertEquals(
//            strList,
//            Mapper.defaultMapper().map(
//                strList, new TypeRef<List<? super String>>() {
//                }.getType(),
//                new TypeRef<List<? super CharSequence>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertEquals(
//            strList,
//            Jie.map(
//                strList, List.class
//            )
//        );
//        Assert.assertEquals(
//            Arrays.asList(1, 2, 3),
//            Jie.map(
//                strList,
//                new TypeRef<List<? super Integer>>() {
//                }.getType()
//            )
//        );
//        Assert.assertEquals(
//            Arrays.asList(new T1("1"), new T1("2")),
//            Mapper.defaultMapper().map(
//                Arrays.asList(new T2("1"), new T2("2")),
//                new TypeRef<List<T2>>() {
//                }.getType(),
//                new TypeRef<List<? super T1>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertNotEquals(
//            Arrays.asList(new T1("1"), new T1("2")),
//            Mapper.defaultMapper().map(
//                Arrays.asList(new T2("1"), new T2("2")),
//                new TypeRef<List<T2>>() {
//                }.getType(),
//                new TypeRef<List<? extends T1>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            )
//        );
//        Assert.assertEquals(
//            E1.E1,
//            Jie.map(
//                "E1",
//                E1.class
//            )
//        );
//        Assert.assertEquals(
//            "E2",
//            Jie.map(
//                E1.E2,
//                String.class
//            )
//        );
//        Assert.assertEquals(
//            E2.E1,
//            Jie.map(
//                E1.E1,
//                E2.class
//            )
//        );
//        Assert.assertEquals(
//            E1.E2,
//            Jie.map(
//                E2.E2,
//                E1.class
//            )
//        );
//    }
//
//    @Test
//    public void testConvertBytes() {
//        byte[] low = {1, 2, 3};
//        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{1, 2, 3});
//        Mapper converter = Mapper.defaultMapper();
//        Mapper newConverter = converter.replaceOptions(MappingOptions.builder()
//            .copyLevel(MappingOptions.COPY_LEVEL_DEEP).build());
//        Assert.assertEquals(
//            converter.map(low, byte[].class),
//            low
//        );
//        Assert.assertEquals(
//            converter.map(low, ByteBuffer.class),
//            buffer.slice()
//        );
//        Assert.assertEquals(
//            converter.map(buffer, byte[].class),
//            low
//        );
//        Assert.assertEquals(
//            converter.map(buffer, ByteBuffer.class),
//            buffer.slice()
//        );
//        Assert.assertSame(
//            converter.map(low, byte[].class),
//            low
//        );
//        Assert.assertEquals(
//            newConverter.map(low, byte[].class),
//            low
//        );
//        Assert.assertNotSame(
//            newConverter.map(low, byte[].class),
//            low
//        );
//        Assert.assertNotSame(
//            converter.map(buffer, ByteBuffer.class),
//            buffer.slice()
//        );
//    }
//
//    @Test
//    public void testConvertHandler() {
//        Object x = new Object();
//        Mapper.Handler handler = new Mapper.Handler() {
//            @Override
//            public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
//                return x;
//            }
//
//            @Override
//            public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
//                return x;
//            }
//        };
//        Mapper converter = Mapper.newMapper(Arrays.asList(
//            new AssignableMapperHandler(),
//            handler,
//            new EnumMapperHandler(),
//            new TypedMapperHandler(),
//            new CollectionMappingHandler(),
//            new BeanMapperHandler()
//        ));
//        Assert.assertSame(
//            x,
//            converter.map(
//                "123",
//                new TypeRef<List<? super Integer>>() {
//                }.getType()
//            )
//        );
//        Assert.assertSame(converter, converter.replaceFirstHandler(converter.getHandlers().get(0)));
//        Assert.assertSame(converter, converter.replaceLastHandler(converter.getHandlers().get(converter.getHandlers().size() - 1)));
//        Assert.assertNotSame(converter, converter.replaceFirstHandler(handler));
//        Assert.assertNotSame(converter, converter.replaceLastHandler(handler));
//        Assert.assertSame(converter, converter.replaceOptions(converter.getOptions()));
//        Assert.assertNotSame(converter, converter.replaceOptions(converter.getOptions().toBuilder().copyLevel(999).build()));
//    }
//
//    @Test
//    public void testConvertAsHandler() {
//        List<Mapper.Handler> handlers = Arrays.asList(
//            new AssignableMapperHandler(),
//            new Mapper.Handler() {
//                @Override
//                public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
//                    if (Objects.equals(source, "1")) {
//                        return "2";
//                    }
//                    if (Objects.equals(source, "2")) {
//                        return "1";
//                    }
//                    if (Objects.equals(source, "3")) {
//                        return Flag.CONTINUE;
//                    }
//                    return Flag.BREAK;
//                }
//
//                @Override
//                public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
//                    return map(source, sourceType, targetType, mapper, options);
//                }
//            },
//            new EnumMapperHandler(),
//            new TypedMapperHandler(),
//            new CollectionMappingHandler(),
//            new BeanMapperHandler()
//        );
//        Mapper mapper = Mapper.newMapper(handlers);
//        Mapper.Handler handler = mapper.asHandler();
//        Assert.assertEquals(
//            handler.map("1", String.class, Integer.class, Mapper.defaultMapper(), mapper.getOptions()), "2");
//        Assert.assertEquals(
//            handler.map("2", String.class, Integer.class, Mapper.defaultMapper(), mapper.getOptions()), "1");
//        Assert.assertEquals(
//            handler.map("3", String.class, Integer.class, Mapper.defaultMapper(), mapper.getOptions()), 3);
//        Assert.assertEquals(
//            handler.map("4", String.class, Integer.class, Mapper.defaultMapper(), mapper.getOptions()), Flag.BREAK);
//    }
//
//    @Test
//    public void testFormat() throws Exception {
//    }
//
//    private void testDateFormat(@Nullable ZoneOffset offset) throws Exception {
//        String pattern = "yyyy-MM-dd HH:mm:ss";
//        String time = "2222-12-22 12:22:22";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
//        Date date = JieDate.parse(time, pattern);
//        Mapper mapper = Mapper.defaultMapper();
//        mapper = mapper.replaceOptions(mapper.getOptions().toBuilder()
//            .dateFormat(formatter)
//            .zoneOffset(offset)
//            .build()
//        );
//        ZoneOffset zoneOffset = offset == null ? JieDate.zoneOffset() : offset;
//        Assert.assertEquals(mapper.map(date, Date.class), date);
//        Assert.assertEquals(mapper.map(date, String.class), time);
//        //Assert.assertEquals(mapper.map(now, Instant.class), now.toInstant());
//        //Assert.assertEquals(mapper.map(now, LocalDateTime.class), LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault()));
//
//    }
//
//    public enum E1 {
//        E1, E2, E3,
//        ;
//    }
//
//    public enum E2 {
//        E1, E2, E3,
//        ;
//    }
//
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @EqualsAndHashCode
//    @Data
//    public static class T1 {
//        private String value;
//    }
//
//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    public static class T2 extends T1 {
//
//        public T2() {
//        }
//
//        public T2(String value) {
//            super(value);
//        }
//    }
//
//    @Data
//    public static class D1 {
//        private ZonedDateTime d1;
//        private LocalDateTime d2;
//    }
//
//    @Data
//    public static class D2 {
//        private String d1;
//        private String d2;
//    }
//}
