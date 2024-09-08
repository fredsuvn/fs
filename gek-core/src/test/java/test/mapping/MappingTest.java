package test.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.mapping.BeanMapper;
import xyz.fslabo.common.mapping.Mapper;
import xyz.fslabo.common.mapping.MappingException;
import xyz.fslabo.common.mapping.MappingOptions;
import xyz.fslabo.common.ref.Val;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MappingTest {

    @Test
    public void testBeanMapping() throws Exception {
        BeanMapper beanMapper = BeanMapper.defaultMapper();
        Map<Integer, Double> map1 = new HashMap<>();
        Map<Double, Integer> map2 = new HashMap<>();
        // map -> map
        map1.put(1, 2.0);
        map1.put(2, 4.0);
        beanMapper.copyProperties(
            map1,
            new TypeRef<Map<Integer, Double>>() {
            }.getType(),
            map2,
            new TypeRef<Map<Double, Integer>>() {
            }.getType()
        );
        Assert.assertEquals(map2, Jie.hashMap(1.0, 2, 2.0, 4));
        // map -> bean
        M1 m1 = beanMapper.copyProperties(
            map1,
            new TypeRef<Map<Integer, Double>>() {
            }.getType(),
            new M1(),
            M1.class,
            MappingOptions.builder().nameMapper((n, t) -> "f" + n).build()
        );
        Assert.assertEquals(m1, new M1("2.0", 4L));
        // bean -> map
        map2 = beanMapper.copyProperties(
            m1,
            M1.class,
            map2,
            new TypeRef<Map<Double, Integer>>() {
            }.getType(),
            MappingOptions.builder().nameMapper((n, t) ->
                n.toString().startsWith("f") ? n.toString().substring(1) : n).build()
        );
        Assert.assertEquals(map2, Jie.hashMap(1.0, 2, 2.0, 4));
        // bean -> bean
        M2 m2 = beanMapper.copyProperties(m1, new M2());
        Assert.assertEquals(m2, new M2(2L, "4"));
        // exception
        Assert.expectThrows(MappingException.class, () ->
            beanMapper.copyProperties(m1, m2, MappingOptions.builder().nameMapper((n, t) -> {
                throw new IllegalStateException();
            }).build()));

        // Options
        // map -> map
        Map<String, String> map11 = new HashMap<>();
        map11.put("f1", "f1");
        Map<String, String> map12 = new HashMap<>();
        beanMapper.copyProperties(map11, map12, MappingOptions.builder().putNew(false).build());
        Assert.assertEquals(map12, Jie.hashMap());
        map12.put("f2", "f2");
        beanMapper.copyProperties(map11, map12);
        Assert.assertEquals(map11.size(), 1);
        Assert.assertEquals(map12.size(), 2);
        Assert.assertEquals(map12, Jie.hashMap("f1", "f1", "f2", "f2"));
        map11.put("f1", null);
        map12.remove("f1");
        beanMapper.copyProperties(map11, map12, MappingOptions.builder().ignoreNull(true).build());
        Assert.assertEquals(map12, Jie.hashMap("f2", "f2"));
        beanMapper.copyProperties(map11, map12, MappingOptions.builder().ignoreNull(false).build());
        Assert.assertEquals(map12, Jie.hashMap("f1", null, "f2", "f2"));
        map11.put("f1", "f1");
        map12.put("f1", "f11");
        Jie.copyProperties(map11, map12, "f1");
        Assert.assertNotEquals(map11, map12);
        Assert.assertEquals(map11.get("f1"), "f1");
        Assert.assertEquals(map12, Jie.hashMap("f1", "f11", "f2", "f2"));
        beanMapper.copyProperties(map11, map12, MappingOptions.builder().ignoreClass(false).build());
        Assert.assertEquals(map12, Jie.hashMap("f1", "f1", "f2", "f2"));
        map11.put("f1", "f11");
        map12.put("f3", "f33");
        beanMapper.copyProperties(map11, map12, MappingOptions.builder().nameMapper((n, t) -> null).build());
        Assert.assertEquals(map12, Jie.hashMap("f1", "f1", "f2", "f2", "f3", "f33"));
        // map -> bean
        Map<String, String> map21 = new HashMap<>();
        map21.put("f1", "f1");
        M3 m22 = new M3();
        m22.setF2("f2");
        beanMapper.copyProperties(map21, m22);
        Assert.assertEquals(m22, new M3("f1", "f2", null));
        map21.put("f1", null);
        m22.setF1("f11");
        m22.setF2("f22");
        beanMapper.copyProperties(map21, m22, MappingOptions.builder().ignoreNull(true).build());
        Assert.assertEquals(m22, new M3("f11", "f22", null));
        beanMapper.copyProperties(map21, m22, MappingOptions.builder().ignoreNull(false).build());
        Assert.assertEquals(m22, new M3(null, "f22", null));
        map21.put("f1", "f1");
        m22.setF1("f111");
        Jie.copyProperties(map21, m22, "f1");
        Assert.assertEquals(m22, new M3("f111", "f22", null));
        beanMapper.copyProperties(map21, m22, MappingOptions.builder().ignoreClass(false).build());
        Assert.assertEquals(m22, new M3("f1", "f22", null));
        map21.put("f1", "f11");
        m22.setF3("f33");
        beanMapper.copyProperties(map21, m22, MappingOptions.builder().nameMapper((n, t) -> null).build());
        Assert.assertEquals(m22, new M3("f1", "f22", "f33"));
        // bean -> map
        M3 m31 = new M3();
        m31.setF1("f1");
        Map<String, String> map32 = new HashMap<>();
        beanMapper.copyProperties(m31, map32, MappingOptions.builder().putNew(false).build());
        Assert.assertEquals(map32, Jie.hashMap());
        map32.put("f2", "f2");
        beanMapper.copyProperties(m31, map32);
        Assert.assertEquals(map32, Jie.hashMap("f1", "f1", "f2", null, "f3", null));
        map32.put("f2", "f2");
        map32.remove("f3");
        beanMapper.copyProperties(m31, map32, MappingOptions.builder().ignoreNull(true).build());
        Assert.assertEquals(map32, Jie.hashMap("f1", "f1", "f2", "f2"));
        map32.put("f1", "f11");
        Jie.copyProperties(m31, map32, "f1");
        Assert.assertEquals(map32, Jie.hashMap("f1", "f11", "f2", null, "f3", null));
        beanMapper.copyProperties(m31, map32, MappingOptions.builder().ignoreClass(false).build());
        Assert.assertEquals(map32, Jie.hashMap("f1", "f1", "f2", null, "f3", null, "class", M3.class));
        m31.setF1("f111");
        map32.put("f1", "f11");
        map32.put("f3", "f33");
        beanMapper.copyProperties(m31, map32, MappingOptions.builder().nameMapper((n, t) -> null).build());
        Assert.assertEquals(map32, Jie.hashMap("f1", "f11", "f2", null, "f3", "f33", "class", M3.class));
        // bean -> bean
        M3 m41 = new M3();
        m41.setF1("f1");
        M3 m42 = new M3();
        m42.setF2("f2");
        beanMapper.copyProperties(m41, m42);
        Assert.assertEquals(m41, m42);
        Assert.assertEquals(m42, new M3("f1", null, null));
        m42.setF2("f2");
        beanMapper.copyProperties(m41, m42, MappingOptions.builder().ignoreNull(true).build());
        Assert.assertNotEquals(m41, m42);
        Assert.assertEquals(m42, new M3("f1", "f2", null));
        m42.setF1("f11");
        Jie.copyProperties(m41, m42, "f1");
        Assert.assertNotEquals(m41, m42);
        Assert.assertEquals(m41.getF1(), "f1");
        Assert.assertEquals(m42, new M3("f11", null, null));
        beanMapper.copyProperties(m41, m42, MappingOptions.builder().ignoreClass(false).build());
        Assert.assertEquals(m41, m42);
        m41.setF1("f111");
        m42.setF3("f33");
        beanMapper.copyProperties(m41, m42, MappingOptions.builder().nameMapper((n, t) -> null).build());
        Assert.assertNotEquals(m41, m42);
        Assert.assertEquals(m42, new M3("f1", null, "f33"));

        // one -> more
        M3 m5 = new M3();
        m5.setF1("f1");
        Assert.assertEquals(
            beanMapper.copyProperties(m5, new HashMap<String, String>(), MappingOptions.builder().ignoreNull(true)
                .nameMapper((n, t) -> Jie.arrayList(n, n + "" + n)).build()),
            Jie.hashMap("f1", "f1", "f1f1", "f1")
        );
        Assert.assertEquals(
            beanMapper.copyProperties(m5, new M4(), MappingOptions.builder().ignoreNull(true)
                .nameMapper((n, t) -> Jie.arrayList(n, "ff1")).build()),
            new M4("f1", "f1")
        );

        // error
        M3 m6 = new M3();
        m6.setF1("d1");
        Assert.expectThrows(MappingException.class, () ->
            beanMapper.copyProperties(m6, new HashMap<String, String>(), MappingOptions.builder()
                .ignoreNull(true).ignoreError(true)
                .nameMapper((n, t) -> {
                    throw new IllegalStateException();
                }).build())
        );
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new M4(), MappingOptions.builder().ignoreNull(false)
                .ignoreClass(false)
                .ignoreError(true).build()),
            new M4("d1", null)
        );
        Mapper keyError = Mapper.newMapper(new ErrorMapperHandler(true, false, false, false, false, false, false, false, false));
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new HashMap<String, String>(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(true)
                .mapper(keyError).build()),
            Jie.hashMap()
        );
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new M4(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(true)
                .mapper(keyError).build()),
            new M4(null, null)
        );
        Mapper keyNull = Mapper.newMapper(new ErrorMapperHandler(false, false, true, false, false, false, false, false, false));
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new HashMap<String, String>(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(true)
                .mapper(keyNull).build()),
            Jie.hashMap()
        );
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new M4(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(true)
                .mapper(keyNull).build()),
            new M4(null, null)
        );
        Mapper destError = Mapper.newMapper(new ErrorMapperHandler(false, false, false, true, false, false, true, false, false));
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new HashMap<String, String>(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(true)
                .mapper(destError).build()),
            Jie.hashMap()
        );
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new M4(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(true)
                .mapper(destError).build()),
            new M4(null, null)
        );
        Mapper destNull = Mapper.newMapper(new ErrorMapperHandler(false, false, false, false, true, false, false, true, false));
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new HashMap<String, String>(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(true)
                .mapper(destNull).build()),
            Jie.hashMap()
        );
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new M4(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(true)
                .mapper(destNull).build()),
            new M4(null, null)
        );
        Assert.expectThrows(MappingException.class, () ->
            beanMapper.copyProperties(m6, new HashMap<String, String>(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(false)
                .mapper(destError).build()));
        Assert.expectThrows(MappingException.class, () ->
            beanMapper.copyProperties(m6, new M4(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(false)
                .mapper(destError).build()));
        Mapper destNullError = Mapper.newMapper(new ErrorMapperHandler(false, false, false, false, true, false, false, true, false));
        Assert.expectThrows(MappingException.class, () ->
            beanMapper.copyProperties(m6, new HashMap<String, String>(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(false)
                .mapper(destNullError).build()));
        Assert.expectThrows(MappingException.class, () ->
            beanMapper.copyProperties(m6, new M4(), MappingOptions.builder()
                .ignoreNull(false).ignoreError(false)
                .mapper(destNullError).build()));

        Mapper destNullVal = Mapper.newMapper(new ErrorMapperHandler(false, false, false, false, false, true, false, false, true));
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new HashMap<String, String>(), MappingOptions.builder()
                .ignoreNull(true).ignoreError(true)
                .mapper(destNullVal).build()),
            Jie.hashMap()
        );
        Assert.assertEquals(
            beanMapper.copyProperties(m6, new M4(), MappingOptions.builder()
                .ignoreNull(true).ignoreError(true)
                .mapper(destNullVal).build()),
            new M4(null, null)
        );
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class M1 {
        private String f1;
        private Long f2;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class M2 {
        private Long f1;
        private String f2;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class M3 {
        private String f1;
        private String f2;
        private String f3;

        public void setF5(String f5) {
            //
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class M4 {
        private String f1;
        private String ff1;
    }

    public static class ErrorMapperHandler implements Mapper.Handler {

        private final boolean keyError;
        private final boolean keyNull;
        private final boolean keyNullVal;
        private final boolean mapError;
        private final boolean mapNull;
        private final boolean mapNullVal;
        private final boolean propError;
        private final boolean propNull;
        private final boolean propNullVal;

        public ErrorMapperHandler(
            boolean keyError, boolean keyNull, boolean keyNullVal,
            boolean mapError, boolean mapNull, boolean mapNullVal,
            boolean propError, boolean propNull, boolean propNullVal
        ) {
            this.keyError = keyError;
            this.keyNull = keyNull;
            this.keyNullVal = keyNullVal;
            this.mapError = mapError;
            this.mapNull = mapNull;
            this.mapNullVal = mapNullVal;
            this.propError = propError;
            this.propNull = propNull;
            this.propNullVal = propNullVal;
        }

        @Override
        public Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
            if (isKeyMap(source)) {
                if (keyError) {
                    throw new IllegalStateException();
                }
                if (keyNull) {
                    return null;
                }
                if (keyNullVal) {
                    return Val.ofNull();
                }
                return source;
            }
            if (mapError) {
                throw new IllegalStateException();
            }
            if (mapNull) {
                return null;
            }
            if (mapNullVal) {
                return Val.ofNull();
            }
            return source;
        }

        @Override
        public Object mapProperty(@Nullable Object source, Type sourceType, Type targetType, PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
            if (propError) {
                throw new IllegalStateException();
            }
            if (propNull) {
                return null;
            }
            if (propNullVal) {
                return Val.ofNull();
            }
            return source;
        }

        private boolean isKeyMap(@Nullable Object source) {
            return source != null && source.toString().startsWith("f");
        }
    }
}
