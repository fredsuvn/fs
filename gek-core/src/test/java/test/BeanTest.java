//package test;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import xyz.fslabo.annotations.Nullable;
//import xyz.fslabo.common.base.*;
//import xyz.fslabo.common.bean.*;
//import xyz.fslabo.common.bean.handlers.JavaBeanResolverHandler;
//import xyz.fslabo.common.bean.handlers.NonGetterPrefixResolverHandler;
//import xyz.fslabo.common.mapping.BeanMapper;
//import xyz.fslabo.common.mapping.Mapper;
//import xyz.fslabo.common.mapping.MappingOptions;
//import xyz.fslabo.common.reflect.TypeRef;
//
//import java.lang.annotation.*;
//import java.lang.reflect.Type;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.time.Instant;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class BeanTest {
//
//    @Test
//    public void testTypeBean() throws Exception {
//        Type ccType = new TypeRef<Cc<Double>>() {
//        }.getType();
//        BeanInfo ccBean = BeanInfo.get(ccType);
//        JieLog.system().info("ccBean: ", ccBean);
//        PropertyInfo cc = ccBean.getProperty("cc");
//        PropertyInfo c1 = ccBean.getProperty("c1");
//        PropertyInfo c2 = ccBean.getProperty("c2");
//        PropertyInfo i1 = ccBean.getProperty("i1");
//        PropertyInfo i2 = ccBean.getProperty("i2");
//        Assert.assertEquals(cc.getType(), Double.class);
//        Assert.assertEquals(c2.getType(), Long.class);
//        Assert.assertEquals(i1.getType(), String.class);
//        Assert.assertEquals(i2.getType(), Integer.class);
//        Assert.assertNull(c1);
//        Assert.assertEquals(
//            c2.getFieldAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
//            Arrays.asList(C2.class.getDeclaredField("c2").getAnnotations()[0].toString())
//        );
//        Assert.assertEquals(
//            c2.getGetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
//            Arrays.asList(C2.class.getMethod("getC2").getAnnotations()[0].toString())
//        );
//        Assert.assertEquals(
//            c2.getSetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
//            Arrays.asList(C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString())
//        );
//        Assert.assertEquals(
//            c2.getAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
//            Arrays.asList(
//                C2.class.getMethod("getC2").getAnnotations()[0].toString(),
//                C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString(),
//                C2.class.getDeclaredField("c2").getAnnotations()[0].toString()
//            ));
//    }
//
//    @Test
//    public void testClassBean() throws Exception {
//        Type ccType = Cc.class;
//        BeanInfo ccBean = BeanInfo.get(ccType);
//        JieLog.system().info("ccBean: ", ccBean);
//        PropertyInfo cc = ccBean.getProperty("cc");
//        PropertyInfo c1 = ccBean.getProperty("c1");
//        PropertyInfo c2 = ccBean.getProperty("c2");
//        PropertyInfo i1 = ccBean.getProperty("i1");
//        PropertyInfo i2 = ccBean.getProperty("i2");
//        PropertyInfo e1 = ccBean.getProperty("e1");
//        PropertyInfo e2 = ccBean.getProperty("e2");
//        Assert.assertEquals(cc.getType().toString(), "T");
//        Assert.assertEquals(c2.getType(), Long.class);
//        Assert.assertEquals(i1.getType(), String.class);
//        Assert.assertEquals(i2.getType(), Integer.class);
//        Assert.assertEquals(e1.getType(), E1.class);
//        Assert.assertEquals(e2.getType(), E2.class);
//        Assert.assertNull(c1);
//        Assert.assertEquals(
//            c2.getFieldAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
//            Arrays.asList(C2.class.getDeclaredField("c2").getAnnotations()[0].toString())
//        );
//        Assert.assertEquals(
//            c2.getGetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
//            Arrays.asList(C2.class.getMethod("getC2").getAnnotations()[0].toString())
//        );
//        Assert.assertEquals(
//            c2.getSetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
//            Arrays.asList(C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString())
//        );
//        Assert.assertEquals(
//            c2.getAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
//            Arrays.asList(
//                C2.class.getMethod("getC2").getAnnotations()[0].toString(),
//                C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString(),
//                C2.class.getDeclaredField("c2").getAnnotations()[0].toString()
//            ));
//    }
//
//    @Test
//    public void testBeanResolver() {
//        Type ccType = new TypeRef<Cc<Double>>() {
//        }.getType();
//        BeanInfo ccBean1 = BeanInfo.get(ccType);
//        BeanInfo ccBean2 = BeanInfo.get(ccType);
//        Assert.assertSame(ccBean1, ccBean2);
//        BeanResolver resolver = BeanResolver.withHandlers(
//            new JavaBeanResolverHandler()
//        );
//        BeanInfo ccBean3 = resolver.resolve(ccType);
//        Assert.assertNotSame(ccBean1, ccBean3);
//        Assert.assertEquals(ccBean1, ccBean3);
//        BeanInfo ccBean4 = resolver.resolve(ccType);
//        Assert.assertNotSame(ccBean4, ccBean3);
//        Assert.assertEquals(ccBean4, ccBean3);
//
//        BeanInfo aaa = BeanInfo.get(TestHandler.class);
//        Assert.assertEquals(aaa.getProperties().size(), 3);
//        Assert.assertNotNull(aaa.getProperty("aaa"));
//        Assert.assertNotNull(aaa.getProperty("bbb"));
//        Assert.assertTrue(aaa.getProperty("aaa").isReadable());
//        Assert.assertTrue(aaa.getProperty("aaa").isWriteable());
//        Assert.assertFalse(aaa.getProperty("bbb").isReadable());
//        Assert.assertTrue(aaa.getProperty("bbb").isWriteable());
//        BeanInfo bbb = BeanResolver.withHandlers(new NonGetterPrefixResolverHandler()).resolve(TestHandler.class);
//        Assert.assertEquals(bbb.getProperties().size(), 3);
//        Assert.assertNotNull(bbb.getProperty("aaa"));
//        Assert.assertNotNull(bbb.getProperty("bbb"));
//        Assert.assertNotNull(bbb.getProperty("getAaa"));
//        Assert.assertFalse(bbb.getProperty("aaa").isReadable());
//        Assert.assertTrue(bbb.getProperty("aaa").isWriteable());
//        Assert.assertTrue(bbb.getProperty("bbb").isReadable());
//        Assert.assertTrue(bbb.getProperty("bbb").isWriteable());
//        Assert.assertTrue(bbb.getProperty("getAaa").isReadable());
//        Assert.assertFalse(bbb.getProperty("getAaa").isWriteable());
//    }
//
//    @Test
//    public void testBeanResolveHandler() {
//        BeanResolver resolver = BeanResolver.defaultResolver();
//        Assert.assertSame(resolver, resolver.replaceFirstHandler(resolver.getHandlers().get(0)));
//        Assert.assertSame(resolver, resolver.replaceLastHandler(resolver.getHandlers().get(resolver.getHandlers().size() - 1)));
//        BeanResolver.Handler handler = new BeanResolver.Handler() {
//            @Override
//            public @Nullable Flag resolve(BeanResolver.Context context) throws BeanResolvingException {
//                return null;
//            }
//        };
//        Assert.assertNotSame(resolver, resolver.replaceFirstHandler(handler));
//        Assert.assertNotSame(resolver, resolver.replaceLastHandler(handler));
//    }
//
//    @Test
//    public void testCopyProperties() {
//        MappingOptions ignoreNull = MappingOptions.builder().ignoreNull(true).build();
//        //bean -> bean
//        Cc<Long> cc1 = new Cc<>();
//        cc1.setI1("i1");
//        cc1.setI2(2);
//        cc1.setC2(22L);
//        cc1.setCc(33L);
//        cc1.setE1(E1.E2);
//        cc1.setE2(E2.E3);
//        Cc<Long> cc2 = Jie.copyProperties(cc1, new Cc<>());
//        Assert.assertEquals(cc2, cc1);
//        cc1.setI1(null);
//        cc2.setI1("888");
//        Jie.copyProperties(cc1, cc2);
//        Assert.assertEquals(cc2, cc1);
//        Assert.assertEquals(cc2.getI1(), cc1.getI1());
//        Assert.assertNull(cc2.getI1());
//        Assert.assertSame(cc2.getE1(), E1.E2);
//        Assert.assertSame(cc2.getE2(), E2.E3);
//        cc2.setI1("888");
//        Jie.copyProperties(cc1, cc2, ignoreNull);
//        Assert.assertEquals("888", cc2.getI1());
//        cc1.setI1("aaaa");
//        cc2 = Jie.copyProperties(cc1, new Cc<>());
//        Assert.assertNotNull(cc2.getC2());
//        cc2 = Jie.copyProperties(cc1, new Cc<>(), "c2");
//        Assert.assertEquals(cc1.getI1(), cc2.getI1());
//        Assert.assertEquals(cc1.getI2(), cc2.getI2());
//        Assert.assertEquals(cc1.getCc(), cc2.getCc());
//        Assert.assertNull(cc2.getC2());
//        Assert.assertEquals(cc1.getC2().longValue(), 22);
//        cc1.setI1(null);
//        cc2 = Jie.copyProperties(cc1, new Cc<>(), "c2");
//        cc2.setI1("qqqq");
//        Jie.copyProperties(cc1, new Cc<>(), "c2");
//        Assert.assertEquals("qqqq", cc2.getI1());
//        Assert.assertEquals(cc1.getI2(), cc2.getI2());
//        Assert.assertEquals(cc1.getCc(), cc2.getCc());
//        Assert.assertNull(cc2.getC2());
//        Assert.assertEquals(cc1.getC2().longValue(), 22);
//        Assert.expectThrows(ClassCastException.class, () -> {
//            Cc<Long> ccl = new Cc<>();
//            BeanMapper.defaultMapper().copyProperties(cc1, new TypeRef<Cc<Double>>() {
//                }.getType(),
//                ccl, new TypeRef<Cc<String>>() {
//                }.getType(),
//                MappingOptions.defaultOptions()
//            );
//            Long l = ccl.getCc();
//            System.out.println(l);
//        });
//        Cc<String> ccs = BeanMapper.defaultMapper().copyProperties(cc1, new TypeRef<Cc<Double>>() {
//            }.getType(),
//            new Cc<>(), new TypeRef<Cc<String>>() {
//            }.getType(), MappingOptions.defaultOptions());
//        Assert.assertEquals(ccs.getCc(), cc1.getCc().toString());
//
//        Mapper kConverter = Mapper.defaultMapper().addFirstHandler(new Mapper.Handler() {
//            @Override
//            public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
//                if (Objects.equals(targetType, Kk.class)) {
//                    return new Kk(String.valueOf(source));
//                }
//                return Flag.CONTINUE;
//            }
//
//            @Override
//            public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
//                if (Objects.equals(targetType, Kk.class)) {
//                    return new Kk(source + "Property");
//                }
//                Ann ann = targetProperty.getAnnotation(Ann.class);
//                if (ann == null) {
//                    return Flag.CONTINUE;
//                }
//                String format = ann.value();
//                if (JieString.isNotEmpty(format) && source instanceof Instant && Objects.equals(targetType, String.class)) {
//                    Instant instant = (Instant) source;
//                    return JieDate.format(Date.from(instant), format);
//                }
//                return Flag.CONTINUE;
//            }
//        });
//        BeanMapper copier = BeanMapper.defaultMapper();
//
//        //bean -> map
//        Map<Kk, String> map1 = copier.copyProperties(
//            cc1,
//            new TypeRef<Cc<Long>>() {
//            }.getType(),
//            new HashMap<>(),
//            new TypeRef<Map<Kk, String>>() {
//            }.getType(),
//            MappingOptions.builder().ignoreNull(true).mapper(kConverter).build()
//        );
//        Assert.assertEquals(map1.get(new Kk("i1")), Jie.orNull(cc1.getI1(), String::valueOf));
//        Assert.assertEquals(map1.get(new Kk("i2")), Jie.orNull(cc1.getI2(), String::valueOf));
//        Assert.assertEquals(map1.get(new Kk("cc")), Jie.orNull(cc1.getCc(), String::valueOf));
//        Assert.assertEquals(map1.get(new Kk("c2")), Jie.orNull(cc1.getC2(), String::valueOf));
//
//        // map -> bean
//        map1.put(new Kk("i1"), "88888");
//        Cc<String> cs2 = copier.copyProperties(
//            map1,
//            new TypeRef<Map<Kk, String>>() {
//            }.getType(),
//            new Cc<>(),
//            new TypeRef<Cc<String>>() {
//            }.getType(),
//            MappingOptions.builder().mapper(kConverter).build()
//        );
//        Assert.assertEquals(map1.get(new Kk("i1")), Jie.orNull(cs2.getI1(), String::valueOf));
//        Assert.assertEquals(map1.get(new Kk("i2")), Jie.orNull(cs2.getI2(), String::valueOf));
//        Assert.assertEquals(map1.get(new Kk("cc")), Jie.orNull(cs2.getCc(), String::valueOf));
//        Assert.assertEquals(map1.get(new Kk("c2")), Jie.orNull(cs2.getC2(), String::valueOf));
//
//        // map -> map
//        Map<String, Kk> map2 = copier.copyProperties(
//            map1,
//            new TypeRef<Map<Kk, String>>() {
//            }.getType(),
//            new HashMap<>(),
//            new TypeRef<Map<String, Kk>>() {
//            }.getType(),
//            MappingOptions.builder().mapper(kConverter).build()
//        );
//        Assert.assertEquals(map1.get(new Kk("i1")), Jie.orNull(map2.get("i1"), String::valueOf));
//        Assert.assertEquals(map1.get(new Kk("i2")), Jie.orNull(map2.get("i2"), String::valueOf));
//        Assert.assertEquals(map1.get(new Kk("cc")), Jie.orNull(map2.get("cc"), String::valueOf));
//        Assert.assertEquals(map1.get(new Kk("c2")), Jie.orNull(map2.get("c2"), String::valueOf));
//
//        //test mapProperty
//        Date now = new Date();
//        cc1.setD(now.toInstant());
//        Dd dd = copier.copyProperties(
//            cc1,
//            new TypeRef<Cc<Long>>() {
//            }.getType(),
//            new Dd(),
//            Dd.class,
//            MappingOptions.builder().mapper(kConverter).build()
//        );
//        Assert.assertEquals(dd.getD(), JieDate.format(now, "yyyy-MM-dd"));
//    }
//
//    @Test
//    public void testCopyPropertiesComplex() {
//        Map<? extends Number, ? extends String> pm = new HashMap<>();
//        pm.put(Jie.as(1), Jie.as("11"));
//        CopyP1 p1 = new CopyP1("22", new List[]{Arrays.asList(pm)}, E1.E1);
//        Map<String, ? extends CopyP1> cm = new HashMap<>();
//        cm.put("33", Jie.as(p1));
//        CopyA ca = new CopyA("44", new List[]{Arrays.asList(55)}, cm, p1);
//        CopyB cb = Jie.copyProperties(ca, new CopyB());
//        Assert.assertEquals(cb.getS(), new Long(44L));
//        Assert.assertEquals(cb.getList().get(0), new int[]{55});
//        Assert.assertSame(cb.getP().getE(), E2.E1);
//        Map<? super BigDecimal, ? super BigInteger> bm = new LinkedHashMap<>();
//        bm.put(new BigDecimal("1"), new BigInteger("11"));
//        CopyP2 p2 = new CopyP2(22L, new List[]{Arrays.asList(bm)}, E2.E1);
//        Assert.assertEquals(cb.getP(), p2);
//        Map<Integer, ? super CopyP2> cm2 = new LinkedHashMap<>();
//        cm2.put(33, p2);
//        Assert.assertEquals(cb.getMap(), cm2);
//    }
//
//    @Test
//    public void testResolverAsHandler() {
//        int[] x = {0};
//        BeanResolver.Handler handler = BeanResolver.defaultResolver()
//            .addFirstHandler(new BeanResolver.Handler() {
//                @Override
//                public @Nullable Flag resolve(BeanResolver.Context context) {
//                    if (Objects.equals(context.getType(), Integer.class)) {
//                        x[0]++;
//                        return Flag.CONTINUE;
//                    }
//                    return Flag.BREAK;
//                }
//            })
//            .asHandler();
//        BeanResolver.Context context1 = new BeanResolver.Context() {
//
//            @Override
//            public Type getType() {
//                return Integer.class;
//            }
//
//            @Override
//            public Map<String, BasePropertyInfo> getProperties() {
//                return new HashMap<>();
//            }
//
//            @Override
//            public List<BaseMethodInfo> getMethods() {
//                return new LinkedList<>();
//            }
//        };
//        handler.resolve(context1);
//        Assert.assertEquals(x[0], 1);
//        BeanResolver.Context context2 = new BeanResolver.Context() {
//
//            @Override
//            public Type getType() {
//                return String.class;
//            }
//
//            @Override
//            public Map<String, BasePropertyInfo> getProperties() {
//                return new HashMap<>();
//            }
//
//            @Override
//            public List<BaseMethodInfo> getMethods() {
//                return new LinkedList<>();
//            }
//        };
//        handler.resolve(context2);
//        Assert.assertEquals(x[0], 1);
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
//    public interface I2<T> {
//
//        T getI2();
//
//        void setI2(T t);
//    }
//
//    public interface I1 {
//
//        String getI1();
//
//        void setI1(String i1);
//    }
//
//    @Documented
//    @Retention(RetentionPolicy.RUNTIME)
//    @Inherited
//    public @interface Ann {
//        String value();
//    }
//
//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    public static class Cc<T> extends C2<Long> implements I1, I2<Integer> {
//
//        private String i1;
//        private T cc;
//        private Integer i2;
//        private E1 e1;
//        private E2 e2;
//        private Instant d;
//
//        @Override
//        public String getI1() {
//            return i1;
//        }
//
//        @Override
//        public void setI1(String i1) {
//            this.i1 = i1;
//        }
//    }
//
//    @EqualsAndHashCode
//    public static class C2<T> {
//
//        @Ann("c2")
//        private T c2;
//
//        @Ann("getC2")
//        public T getC2() {
//            return c2;
//        }
//
//        @Ann("setC2")
//        public void setC2(T c2) {
//            this.c2 = c2;
//        }
//    }
//
//    @Data
//    public static class C1 {
//        private String c1;
//    }
//
//    @Data
//    public static class Dd {
//        @Ann("yyyy-MM-dd")
//        private String d;
//    }
//
//    @Data
//    @EqualsAndHashCode
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class Kk {
//
//        private String value;
//
//        @Override
//        public String toString() {
//            return value;
//        }
//    }
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class CopyP1 {
//        private String p;
//        private List<Map<? extends Number, ? extends String>>[] list;
//        private E1 e;
//    }
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @EqualsAndHashCode
//    public static class CopyP2 {
//        private Long p;
//        private List<Map<? extends BigDecimal, ? extends BigInteger>>[] list;
//        private E2 e;
//    }
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class CopyA {
//        private String s;
//        private List<? extends Number>[] list;
//        private Map<String, ? extends CopyP1> map;
//        private CopyP1 p;
//    }
//
//    @Data
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class CopyB {
//        private Long s;
//        private List<int[]> list;
//        private Map<Integer, ? extends CopyP2> map;
//        private CopyP2 p;
//    }
//
//    public static class TestHandler {
//
//        public String getAaa() {
//            return null;
//        }
//
//        public String setAaa(String aaa) {
//            return null;
//        }
//
//        public String bbb() {
//            return null;
//        }
//
//        public String setBbb(String bbb) {
//            return null;
//        }
//    }
//}
