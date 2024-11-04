package test.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.testng.annotations.Test;
import xyz.sunqian.annotations.NonNull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Flag;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.bean.*;
import xyz.sunqian.common.bean.handlers.NonGetterPrefixResolverHandler;
import xyz.sunqian.common.bean.handlers.NonPrefixResolverHandler;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.reflect.JieReflect;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.TypeRef;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

public class BeanTest {

    @Test
    public void testProvider() {
        BeanProvider provider = BeanProvider.defaultProvider();
        BeanInfo b1 = provider.getBeanInfo(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        BeanInfo b2 = provider.getBeanInfo(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        assertSame(b1, b2);
        assertEquals(b1, b2);
        assertEquals(b1.toString(), b2.toString());
        assertEquals(b1.hashCode(), b2.hashCode());
        BeanProvider provider2 = BeanProvider.withResolver(BeanResolver.defaultResolver());
        BeanInfo b3 = provider2.getBeanInfo(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        assertNotSame(b1, b3);
        assertEquals(b1, b3);
        assertEquals(b1.toString(), b3.toString());
        assertEquals(b1.hashCode(), b3.hashCode());
    }

    @Test
    public void testBeanInfo() {
        BeanInfo b1 = BeanInfo.get(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        BeanInfo b2 = BeanResolver.defaultResolver().resolve(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        assertEquals(b1.getType(), new TypeRef<Inner<Short, Long>>() {
        }.getType());
        assertEquals(b1.getRawType(), Inner.class);
        assertNotSame(b1, b2);
        assertEquals(b1, b2);
        assertEquals(b1.toString(), b2.toString());
        assertEquals(b1.hashCode(), b2.hashCode());
        assertTrue(b1.equals(b1));
        assertFalse(b1.equals(null));
        assertFalse(b1.equals(""));
        assertFalse(b1.equals(BeanInfo.get(new TypeRef<Inner<Long, Long>>() {
        }.getType())));
        assertEquals(
            JieColl.putAll(new HashMap<>(), b1.getProperties(), k -> k, BasePropertyInfo::getType),
            Jie.hashMap("ffFf1", String.class
                , "ffFf2", Short.class
                , "ffFf3", Long.class
                , "ffFf4", JieType.parameterized(List.class, new Type[]{String.class})
                , "ffFf5", JieType.array(JieType.parameterized(List.class, new Type[]{String.class}))
                , "class", JieType.parameterized(Class.class, new Type[]{JieType.questionMark()})
                , "c1", Short.class
                , "bb", boolean.class
                , "bb2", Boolean.class
                , "bb3", Boolean.class)
        );
        Set<Method> mSet = new HashSet<>(Jie.list(Inner.class.getMethods()));
        mSet.removeIf(m ->
            (
                ((m.getName().startsWith("get") || m.getName().startsWith("set")) && m.getName().length() > 3)
                    || (m.getName().startsWith("is") && m.getName().length() > 2)
            ) && (
                !Jie.list("gett", "sett", "iss", "isss", "getMm", "setMm", "gettAa", "issAa", "settAa").contains(m.getName())
            )
        );
        assertEquals(
            JieColl.addAll(new HashSet<>(), b1.getMethods(), BaseMethodInfo::getMethod),
            mSet
        );
        assertEquals(
            b1.getType(), new TypeRef<Inner<Short, Long>>() {
            }.getType()
        );

        BeanInfo b3 = BeanInfo.get(Inner.class);
        assertEquals(
            JieColl.putAll(new HashMap<>(), b3.getProperties(), k -> k, BasePropertyInfo::getType),
            Jie.hashMap("ffFf1", String.class
                , "ffFf2", Inner.class.getTypeParameters()[0]
                , "ffFf3", Inner.class.getTypeParameters()[1]
                , "ffFf4", JieType.parameterized(List.class, new Type[]{String.class})
                , "ffFf5", JieType.array(JieType.parameterized(List.class, new Type[]{String.class}))
                , "class", JieType.parameterized(Class.class, new Type[]{JieType.questionMark()})
                , "c1", Inner.class.getTypeParameters()[0]
                , "bb", boolean.class
                , "bb2", Boolean.class
                , "bb3", Boolean.class)
        );

        BeanInfo bo1 = BeanInfo.get(Object.class);
        assertEquals(bo1.getProperties().size(), 1);
        BeanInfo bo2 = BeanResolver.withHandlers(new NonPrefixResolverHandler()).resolve(Object.class);
        assertEquals(bo2.getProperties().size(), 0);
        assertNotNull(bo1.getProperty("class"));
        BeanInfo bc = BeanInfo.get(InnerSuperChild.class);
        PropertyInfo pc = bc.getProperty("c1");
        assertEquals(pc.getType(), String.class);
        BeanInfo bs3 = BeanResolver.withHandlers(new NonPrefixResolverHandler()).resolve(Simple3.class);
        PropertyInfo ps3 = bs3.getProperty("c1");
        assertEquals(ps3.getType(), String.class);
        BeanInfo s1 = BeanResolver.withHandlers(new NonGetterPrefixResolverHandler()).resolve(Simple1.class);
        assertEquals(s1.getProperties().size(), 1);
        assertNotNull(s1.getProperty("aa"));
    }

    @Test
    public void testMember() throws Exception {
        BeanInfo b1 = BeanInfo.get(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        BeanInfo b3 = BeanResolver.defaultResolver().resolve(new TypeRef<Inner<Short, Long>>() {
        }.getType());
        PropertyInfo p1 = b1.getProperty("ffFf1");
        assertEquals(p1.getOwner(), b1);
        assertEquals(b1.toString(), b1.getType().getTypeName());
        assertEquals(p1.toString(),
            b1.getType().getTypeName() + "." + p1.getName() + "[" + p1.getType().getTypeName() + "]");
        MethodInfo m1 = b1.getMethod("m1");
        assertEquals(m1.toString(),
            b1.getType().getTypeName() + "." + m1.getName() + "()[" + m1.getMethod().getGenericReturnType() + "]");
        assertEquals(p1, b3.getProperty("ffFf1"));
        assertEquals(p1.toString(), b3.getProperty("ffFf1").toString());
        assertEquals(p1.hashCode(), b3.getProperty("ffFf1").hashCode());
        assertNotSame(p1, b3.getProperty("ffFf1"));
        assertSame(p1, p1);
        assertTrue(p1.equals(p1));
        assertFalse(p1.equals(null));
        assertFalse(p1.equals(""));
        assertEquals(m1, b3.getMethod("m1"));
        assertEquals(m1.toString(), b3.getMethod("m1").toString());
        assertEquals(m1.hashCode(), b3.getMethod("m1").hashCode());
        assertNotSame(m1, b3.getMethod("m1"));
        assertSame(m1, m1);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(null));
        assertFalse(m1.equals(""));

        PropertyInfo p2 = b1.getProperty("ffFf2");
        PropertyInfo p3 = b1.getProperty("ffFf3");
        PropertyInfo p4 = b1.getProperty("ffFf4");
        PropertyInfo p5 = b1.getProperty("ffFf5");
        PropertyInfo c1 = b1.getProperty("c1");
        assertEquals(p1.getAnnotations().get(0).annotationType(), Nullable.class);
        assertEquals(p1.getGetterAnnotations().get(0).annotationType(), Nullable.class);
        assertTrue(p1.getSetterAnnotations().isEmpty());
        assertTrue(p1.getFieldAnnotations().isEmpty());
        assertNotNull(p1.getAnnotation(Nullable.class));
        assertEquals(p4.getAnnotations().get(0).annotationType(), Nullable.class);
        assertEquals(p4.getSetterAnnotations().get(0).annotationType(), Nullable.class);
        assertTrue(p4.getGetterAnnotations().isEmpty());
        assertTrue(p4.getFieldAnnotations().isEmpty());
        assertNotNull(p4.getAnnotation(Nullable.class));
        assertEquals(c1.getAnnotations().get(0).annotationType(), Nullable.class);
        assertEquals(c1.getFieldAnnotations().get(0).annotationType(), Nullable.class);
        assertTrue(c1.getSetterAnnotations().isEmpty());
        assertTrue(c1.getGetterAnnotations().isEmpty());
        assertNotNull(c1.getAnnotation(Nullable.class));
        assertEquals(m1.getAnnotations().get(0).annotationType(), Nullable.class);
        assertNull(p2.getAnnotation(Nullable.class));
        assertNull(p3.getAnnotation(Nullable.class));
        assertTrue(p1.isReadable());
        assertFalse(p1.isWriteable());
        assertTrue(p2.isReadable());
        assertTrue(p2.isWriteable());
        assertTrue(p3.isReadable());
        assertTrue(p3.isWriteable());
        assertFalse(p4.isReadable());
        assertTrue(p4.isWriteable());
        assertTrue(p5.isReadable());
        assertTrue(p5.isWriteable());
        assertTrue(c1.isReadable());
        assertTrue(c1.isWriteable());
        assertEquals(p4.getRawType(), List.class);
        assertEquals(p2.getGetter(), Inner.class.getMethod("getFfFf2"));
        assertEquals(p2.getSetter(), Inner.class.getMethod("setFfFf2", Object.class));
        assertEquals(p2.getField(), Inner.class.getDeclaredField("ffFf2"));
        assertEquals(c1.getField(), InnerSuper.class.getDeclaredField("c1"));
        assertFalse(p1.equals(c1));
        assertNull(p1.getAnnotation(NonNull.class));

        Inner<Short, Long> inner = new Inner<>();
        inner.setFfFf2((short) 22);
        assertEquals(m1.invoke(inner), (short) 22);
        assertEquals(p2.getValue(inner), (short) 22);
        p2.setValue(inner, (short) 111);
        assertEquals(p2.getValue(inner), (short) 111);
        expectThrows(BeanException.class, () -> p4.getValue(inner));
        expectThrows(BeanException.class, () -> p1.setValue(inner, 111));
        assertNull(b1.getMethod("m1", Object.class));
        assertNull(b1.getMethod("m11"));
        assertFalse(m1.equals(b1.getMethod("get")));
    }

    @Test
    public void testResolver() {
        TestHandler h1 = new TestHandler();
        TestHandler h2 = new TestHandler();
        TestHandler h3 = new TestHandler();
        BeanResolver r1 = BeanResolver.withHandlers(h1);
        BeanInfo b1 = r1.resolve(Inner.class);
        assertEquals(b1.getProperties(), Collections.emptyMap());
        assertEquals(b1.getMethods(), Collections.emptyList());
        assertEquals(h1.times, 1);
        r1 = BeanResolver.withHandlers(Jie.list(h1));
        b1 = r1.resolve(Inner.class);
        assertEquals(b1.getProperties(), Collections.emptyMap());
        assertEquals(b1.getMethods(), Collections.emptyList());
        assertEquals(h1.times, 2);
        r1 = r1.addFirstHandler(h2);
        b1 = r1.resolve(Inner.class);
        assertEquals(b1.getProperties(), Collections.emptyMap());
        assertEquals(b1.getMethods(), Collections.emptyList());
        assertEquals(h1.times, 3);
        assertEquals(h2.times, 1);
        r1 = r1.addLastHandler(h3);
        b1 = r1.resolve(Inner.class);
        assertEquals(b1.getProperties(), Collections.emptyMap());
        assertEquals(b1.getMethods(), Collections.emptyList());
        assertEquals(h1.times, 4);
        assertEquals(h2.times, 2);
        assertEquals(h3.times, 1);
        r1 = r1.replaceFirstHandler(h3);
        r1 = r1.replaceFirstHandler(h3);
        b1 = r1.resolve(Inner.class);
        assertEquals(b1.getProperties(), Collections.emptyMap());
        assertEquals(b1.getMethods(), Collections.emptyList());
        assertEquals(h1.times, 5);
        assertEquals(h2.times, 2);
        assertEquals(h3.times, 3);
        r1 = r1.replaceLastHandler(h2);
        r1 = r1.replaceLastHandler(h2);
        b1 = r1.resolve(Inner.class);
        assertEquals(b1.getProperties(), Collections.emptyMap());
        assertEquals(b1.getMethods(), Collections.emptyList());
        assertEquals(h1.times, 6);
        assertEquals(h2.times, 3);
        assertEquals(h3.times, 4);
        // h3 -> h1 -> h2
        // h4 = h3 -> h1 -> h2
        BeanResolver.Handler h4 = r1.asHandler();
        // h3 -> h1 -> h2 -> h4
        r1 = r1.addLastHandler(h4);
        b1 = r1.resolve(Inner.class);
        assertEquals(b1.getProperties(), Collections.emptyMap());
        assertEquals(b1.getMethods(), Collections.emptyList());
        assertEquals(h1.times, 8);
        assertEquals(h2.times, 5);
        assertEquals(h3.times, 6);
        BreakHandler h5 = new BreakHandler();
        // h5 -> h3 -> h1 -> h2 -> h4
        r1 = r1.addFirstHandler(h5);
        b1 = r1.resolve(Inner.class);
        assertEquals(b1.getProperties(), Collections.emptyMap());
        assertEquals(b1.getMethods(), Collections.emptyList());
        assertEquals(h1.times, 8);
        assertEquals(h2.times, 5);
        assertEquals(h3.times, 6);

        BeanResolver r4 = (BeanResolver) h4;
        // h5 -> h4
        r4 = r4.addFirstHandler(h5);
        h4 = r4.asHandler();
        BeanResolver r5 = BeanResolver.withHandlers(h4);
        b1 = r5.resolve(Inner.class);
        assertEquals(b1.getProperties(), Collections.emptyMap());
        assertEquals(b1.getMethods(), Collections.emptyList());
        assertEquals(h1.times, 8);
        assertEquals(h2.times, 5);
        assertEquals(h3.times, 6);

        ThrowHandler h6 = new ThrowHandler();
        // h6 -> h5 -> h4
        BeanResolver r6 = r5.addFirstHandler(h6);
        expectThrows(BeanResolvingException.class, () -> r6.resolve(Inner.class));
    }

    @Test
    public void testHandler() {
        expectThrows(BeanResolvingException.class, () -> BeanInfo.get(JieType.array(String.class)));
        BeanInfo b1 = BeanResolver.withHandlers(new NonGetterPrefixResolverHandler()).resolve(Simple1.class);
        assertEquals(b1.getProperties().size(), 1);
        Simple1 s1 = new Simple1();
        PropertyInfo aa1 = b1.getProperty("aa");
        assertEquals(aa1.getValue(s1), null);
        aa1.setValue(s1, "ss");
        assertEquals(aa1.getValue(s1), "ss");

        BeanInfo b2 = BeanResolver.withHandlers(new NonPrefixResolverHandler()).resolve(Simple2.class);
        assertEquals(b2.getProperties().size(), 1);
        Simple2 s2 = new Simple2();
        PropertyInfo aa2 = b2.getProperty("aa");
        assertEquals(aa2.getValue(s2), null);
        aa2.setValue(s2, "ss");
        assertEquals(aa2.getValue(s2), "ss");
    }

    @Test
    public void testExtra() {
        Map<TypeVariable<?>, Type> extra =
            JieReflect.getTypeParameterMapping(new TypeRef<TestExtra<String>>() {}.getType());
        Map<TypeVariable<?>, Type> empty = Collections.emptyMap();
        BeanInfo b1 = BeanInfo.get(new TypeRef<TestExtra<String>>() {}.getType());
        BeanInfo b2 = JieBean.withExtraTypeVariableMapping(BeanInfo.get(TestExtra.class), extra);
        assertNotEquals(b1, b2);
        assertNotEquals(b1.getProperty("tt"), b2.getProperty("tt"));
        assertEquals(b1.getProperty("tt").getType(), b2.getProperty("tt").getType());
        BeanInfo b3 = BeanInfo.get(TestExtra.class);
        assertNotEquals(b3, b2);
        assertNotEquals(b3.getProperty("tt"), b2.getProperty("tt"));
        assertNotEquals(b3.getProperty("tt").getType(), b2.getProperty("tt").getType());
        BeanInfo b4 = JieBean.withExtraTypeVariableMapping(BeanInfo.get(TestExtra.class), empty);
        assertEquals(b4, b3);
        assertEquals(b4.getProperty("tt"), b4.getProperty("tt"));
        BeanInfo b5 = JieBean.withExtraTypeVariableMapping(BeanInfo.get(TestExtra.class),
            JieReflect.getTypeParameterMapping(new TypeRef<TestExtra2<String>>() {}.getType()));
        assertEquals(b5, b3);
        assertEquals(b5.getProperty("tt"), b3.getProperty("tt"));

        assertEquals(b1.getRawType(), b2.getRawType());
        assertEquals(b2.getRawType(), b3.getRawType());
        assertEquals(
            b1.getProperties().values().stream().map(BasePropertyInfo::getType).collect(Collectors.toList()),
            b2.getProperties().values().stream().map(BasePropertyInfo::getType).collect(Collectors.toList())
        );
        assertNotEquals(
            b2.getProperties().values().stream().map(BasePropertyInfo::getType).collect(Collectors.toList()),
            b3.getProperties().values().stream().map(BasePropertyInfo::getType).collect(Collectors.toList())
        );

        assertNotEquals(b1.hashCode(), b2.hashCode());
        assertNotEquals(b1.toString(), b2.toString());
        assertEquals(b2.hashCode(), b3.hashCode());
        assertEquals(b2.toString(), b3.toString());
        assertEquals(b1.getProperty("tt").hashCode(), b2.getProperty("tt").hashCode());
        assertNotEquals(b1.getProperty("tt").toString(), b2.getProperty("tt").toString());

        assertNotEquals(b1.getMethods(), b2.getMethods());
        assertEquals(b2.getMethods(), b3.getMethods());
        assertNotEquals(b1.getMethod("hashCode"), b2.getMethod("hashCode"));
        assertEquals(b2.getMethod("hashCode"), b3.getMethod("hashCode"));

        PropertyInfo p1 = b1.getProperty("tt");
        PropertyInfo p2 = b2.getProperty("tt");
        PropertyInfo p3 = b3.getProperty("tt");
        assertEquals(p1.getAnnotations(), p2.getAnnotations());
        assertEquals(p2.getAnnotations(), p3.getAnnotations());
        assertEquals(p1.getAnnotation(Nullable.class), p2.getAnnotation(Nullable.class));
        assertEquals(p2.getAnnotation(Nullable.class), p3.getAnnotation(Nullable.class));
        TestExtra<?> te = new TestExtra<>();
        p1.setValue(te, "1");
        p2.setValue(te, "1");
        p3.setValue(te, "1");
        assertEquals(p1.getValue(te), p2.getValue(te));
        assertEquals(p2.getValue(te), p3.getValue(te));
        assertEquals(p1.getRawType(), p2.getRawType());
        assertNotEquals(p2.getRawType(), p3.getRawType());
        assertEquals(p1.getGetter(), p2.getGetter());
        assertEquals(p2.getSetter(), p3.getSetter());
        assertEquals(p1.getField(), p2.getField());
        assertEquals(p2.getField(), p3.getField());
        assertEquals(p1.getFieldAnnotations(), p2.getFieldAnnotations());
        assertEquals(p2.getFieldAnnotations(), p3.getFieldAnnotations());
        assertEquals(p1.getGetterAnnotations(), p2.getGetterAnnotations());
        assertEquals(p2.getGetterAnnotations(), p3.getGetterAnnotations());
        assertEquals(p1.getSetterAnnotations(), p2.getSetterAnnotations());
        assertEquals(p2.getSetterAnnotations(), p3.getSetterAnnotations());
        assertEquals(p1.isWriteable(), p2.isWriteable());
        assertEquals(p2.isWriteable(), p3.isWriteable());
        assertEquals(p1.isReadable(), p2.isReadable());
        assertEquals(p2.isReadable(), p3.isReadable());
    }

    public static class TestHandler implements BeanResolver.Handler {

        public int times = 0;

        @Override
        public @Nullable Flag resolve(BeanResolver.Context context) throws BeanResolvingException {
            times++;
            return null;
        }
    }

    public static class BreakHandler implements BeanResolver.Handler {

        @Override
        public @Nullable Flag resolve(BeanResolver.Context context) throws BeanResolvingException {
            return Flag.BREAK;
        }
    }

    public static class ThrowHandler implements BeanResolver.Handler {

        @Override
        public @Nullable Flag resolve(BeanResolver.Context context) throws BeanResolvingException {
            throw new IllegalStateException("");
        }
    }

    public static class Inner<T1, T2> extends InnerSuper<T1> {
        private String ffFf1;
        @Getter
        @Setter
        private T1 ffFf2;
        @Getter
        @Setter
        private T2 ffFf3;
        private List<String> ffFf4;
        @Getter
        @Setter
        private List<String>[] ffFf5;
        @Getter
        @Setter
        private boolean bb;
        @Getter
        @Setter
        private Boolean bb2;
        @Setter
        private Boolean bb3;

        @Nullable
        public T1 m1() {
            return ffFf2;
        }

        @Nullable
        public String getFfFf1() {
            return ffFf1;
        }

        @Nullable
        public void setFfFf4(List<String> ffFf4) {
            this.ffFf4 = ffFf4;
        }

        public Boolean isBb3() {
            return bb3;
        }

        public String get() {
            return null;
        }

        public void set() {
        }

        public void set(Object obj) {
        }

        public boolean is() {
            return false;
        }

        public String gett() {
            return null;
        }

        public void sett(Object obj) {
        }

        public boolean iss() {
            return false;
        }

        public String isss() {
            return null;
        }

        public String getMm() {
            return null;
        }

        public void setMm(int mm) {
            //
        }

        public String gettAa() {
            return null;
        }

        public String issAa() {
            return null;
        }

        public void settAa(String aa) {
        }
    }

    @Data
    public static class InnerSuper<C> {
        @Nullable
        private C c1;
    }

    public static class InnerSuperChild extends InnerSuper<String> {
        @Override
        public @Nullable String getC1() {
            return super.getC1();
        }

        @Override
        public void setC1(@Nullable String c1) {
            super.setC1(c1);
        }

        public void setC2(String c2) {
        }
    }

    public static class Simple1 {
        private String aa;

        public String aa() {
            return aa;
        }

        public void setAa(String aa) {
            this.aa = aa;
        }

        public void set(Object a) {
        }

        public void set(Object a, Object b) {
        }

        public void sett(Object obj) {
        }

        public void settAa(Object aa) {
        }
    }

    public static class Simple2 {
        private String aa;

        public String aa() {
            return aa;
        }

        public void aa(String aa) {
            this.aa = aa;
        }

        public void aa(String aa, String bb) {
            this.aa = aa;
        }
    }

    public static class Simple3 {
        public void c1(String c1) {
        }
    }

    @Data
    public static class TestExtra<T> {
        private T tt;
    }

    @Data
    public static class TestExtra2<T> {
        private T tt;
    }
}
