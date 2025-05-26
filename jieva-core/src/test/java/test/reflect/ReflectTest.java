package test.reflect;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import test.utils.ErrorConstructor;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.reflect.JieReflect;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.ReflectionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;
import static xyz.sunqian.test.JieTest.reflectThrows;

public class ReflectTest {

    @Test
    public void testLastName() throws Exception {
        assertEquals(JieReflect.getLastName(ReflectTest.class), ReflectTest.class.getSimpleName());
        Method method = JieReflect.class.getDeclaredMethod("getLastName", String.class);
        method.setAccessible(true);
        assertEquals(method.invoke(null, "123"), "123");
        assertEquals(method.invoke(null, ".123"), "123");
        assertEquals(method.invoke(null, ".1.23"), "23");
        assertEquals(method.invoke(null, ".12.3"), "3");
    }

    @Test
    public void testRawClass() throws Exception {
        class X {
            private List<String> list;
        }
        assertEquals(JieReflect.getRawClass(String.class), String.class);
        Type listType = X.class.getDeclaredField("list").getGenericType();
        assertEquals(JieReflect.getRawClass(listType), List.class);
        assertNull(JieReflect.getRawClass(List.class.getTypeParameters()[0]));
        assertNull(JieReflect.getRawClass(errorParameterizedType()));
    }

    @Test
    public void testBounds() throws Exception {
        class X<T extends String, U> {
            public List<? extends String> upper = null;
            public List<? super String> lower = null;
        }
        ParameterizedType upperParam = (ParameterizedType) X.class.getField("upper").getGenericType();
        WildcardType upper = (WildcardType) upperParam.getActualTypeArguments()[0];
        assertEquals(JieReflect.getUpperBound(upper), String.class);
        assertNull(JieReflect.getLowerBound(upper));
        ParameterizedType lowerParam = (ParameterizedType) X.class.getField("lower").getGenericType();
        WildcardType lower = (WildcardType) lowerParam.getActualTypeArguments()[0];
        assertEquals(JieReflect.getLowerBound(lower), String.class);
        assertEquals(JieReflect.getUpperBound(lower), Object.class);
        TypeVariable<?> t = X.class.getTypeParameters()[0];
        assertEquals(JieReflect.getFirstBound(t), String.class);
        TypeVariable<?> u = X.class.getTypeParameters()[1];
        assertEquals(JieReflect.getFirstBound(u), Object.class);

        // special:
        assertEquals(JieReflect.getUpperBound(new WildcardType() {

            @Override
            public Type @Nonnull [] getUpperBounds() {
                return new Type[0];
            }

            @Override
            public Type @Nonnull [] getLowerBounds() {
                return new Type[0];
            }
        }), Object.class);
        assertEquals(JieReflect.getFirstBound(new TypeVariable<Class<?>>() {

            @Override
            public <T extends Annotation> @Nullable T getAnnotation(@Nonnull Class<T> annotationClass) {
                return null;
            }

            @Override
            public Annotation @Nonnull [] getAnnotations() {
                return new Annotation[0];
            }

            @Override
            public Annotation @Nonnull [] getDeclaredAnnotations() {
                return new Annotation[0];
            }

            @Override
            public Type @Nonnull [] getBounds() {
                return new Type[0];
            }

            @Override
            public Class<?> getGenericDeclaration() {
                return null;
            }

            @Override
            public String getName() {
                return "";
            }

            @Override
            public AnnotatedType @Nonnull [] getAnnotatedBounds() {
                return new AnnotatedType[0];
            }
        }), Object.class);
    }

    @Test
    public void testMember() throws Exception {
        // fields:
        Field c1 = Cls1.class.getDeclaredField("c1");
        Field c2 = Cls2.class.getDeclaredField("c2");
        Field c3 = Cls3.class.getDeclaredField("c3");
        Field pc1 = Cls1.class.getDeclaredField("pc1");
        Field pc2 = Cls2.class.getDeclaredField("pc2");
        Field pc3 = Cls3.class.getDeclaredField("pc3");
        Field i0 = Inter0.class.getDeclaredField("i0");
        Field i1 = Inter1.class.getDeclaredField("i1");
        Field i2 = Inter2.class.getDeclaredField("i2");
        Field i3 = Inter3.class.getDeclaredField("i3");
        assertEquals(JieReflect.getField(Cls3.class, "c3"), c3);
        assertEquals(JieReflect.getField(Cls3.class, "pc3"), pc3);
        assertNull(JieReflect.getField(Cls3.class, "pc3", false));
        assertNull(JieReflect.getField(Cls3.class, "x"));
        assertEquals(JieReflect.searchField(Cls3.class, "c1"), c1);
        assertEquals(JieReflect.searchField(Cls3.class, "c2"), c2);
        assertEquals(JieReflect.searchField(Cls3.class, "c3"), c3);
        assertEquals(JieReflect.searchField(Cls3.class, "pc1"), pc1);
        assertEquals(JieReflect.searchField(Cls3.class, "pc2"), pc2);
        assertEquals(JieReflect.searchField(Cls3.class, "pc3"), pc3);
        assertEquals(JieReflect.searchField(Cls3.class, "i0"), i0);
        assertEquals(JieReflect.searchField(Cls3.class, "i1"), i1);
        assertEquals(JieReflect.searchField(Cls3.class, "i2"), i2);
        assertEquals(JieReflect.searchField(Cls3.class, "i3"), i3);
        assertNull(JieReflect.searchField(Cls3.class, "x"));

        // methods:
        Method cm1 = Cls1.class.getDeclaredMethod("cm1");
        Method cm2 = Cls2.class.getDeclaredMethod("cm2");
        Method cm3 = Cls3.class.getDeclaredMethod("cm3");
        Method pcm1 = Cls1.class.getDeclaredMethod("pcm1");
        Method pcm2 = Cls2.class.getDeclaredMethod("pcm2");
        Method pcm3 = Cls3.class.getDeclaredMethod("pcm3");
        Method im0 = Inter0.class.getDeclaredMethod("im0");
        Method im1 = Inter1.class.getDeclaredMethod("im1");
        Method im2 = Inter2.class.getDeclaredMethod("im2");
        Method im3 = Inter3.class.getDeclaredMethod("im3");
        Class<?>[] params = {};
        assertEquals(JieReflect.getMethod(Cls3.class, "cm3", params), cm3);
        assertEquals(JieReflect.getMethod(Cls3.class, "pcm3", params), pcm3);
        assertNull(JieReflect.getMethod(Cls3.class, "pcm3", params, false));
        assertNull(JieReflect.getMethod(Cls3.class, "x", params));
        assertEquals(JieReflect.searchMethod(Cls3.class, "cm1", params), cm1);
        assertEquals(JieReflect.searchMethod(Cls3.class, "cm2", params), cm2);
        assertEquals(JieReflect.searchMethod(Cls3.class, "cm3", params), cm3);
        assertEquals(JieReflect.searchMethod(Cls3.class, "pcm1", params), pcm1);
        assertEquals(JieReflect.searchMethod(Cls3.class, "pcm2", params), pcm2);
        assertEquals(JieReflect.searchMethod(Cls3.class, "pcm3", params), pcm3);
        assertEquals(JieReflect.searchMethod(Cls3.class, "im0", params), im0);
        assertEquals(JieReflect.searchMethod(Cls3.class, "im1", params), im1);
        assertEquals(JieReflect.searchMethod(Cls3.class, "im2", params), im2);
        assertEquals(JieReflect.searchMethod(Cls3.class, "im3", params), im3);
        assertNull(JieReflect.searchMethod(Cls3.class, "x", params));

        // constructors:
        Constructor<?> cl3 = Cls3.class.getConstructor();
        Constructor<?> pcl3 = Cls3.class.getDeclaredConstructor(int.class);
        assertEquals(JieReflect.getConstructor(Cls3.class, params), cl3);
        assertEquals(JieReflect.getConstructor(Cls3.class, Jie.array(int.class)), pcl3);
        assertNull(JieReflect.getConstructor(Cls3.class, Jie.array(int.class), false));
        assertNull(JieReflect.getConstructor(Cls3.class, Jie.array(long.class)));
    }

    @Test
    public void testNewInstance() throws Exception {
        assertEquals(JieReflect.newInstance(String.class.getName()), "");
        assertNull(JieReflect.newInstance("123"));
        assertNull(JieReflect.newInstance(List.class));
        Constructor<?> constructor = String.class.getConstructor();
        assertEquals(JieReflect.newInstance(constructor), "");
        assertNull(JieReflect.newInstance(ErrorConstructor.class.getConstructor()));
    }

    @Test
    public void testArrayClass() throws Exception {
        // class:
        assertEquals(JieReflect.arrayClass(Object.class), Object[].class);
        assertEquals(JieReflect.arrayClass(Object[].class), Object[][].class);
        assertEquals(JieReflect.arrayClass(boolean.class), boolean[].class);
        assertEquals(JieReflect.arrayClass(boolean[].class), boolean[][].class);
        assertEquals(JieReflect.arrayClass(byte.class), byte[].class);
        assertEquals(JieReflect.arrayClass(short.class), short[].class);
        assertEquals(JieReflect.arrayClass(char.class), char[].class);
        assertEquals(JieReflect.arrayClass(int.class), int[].class);
        assertEquals(JieReflect.arrayClass(long.class), long[].class);
        assertEquals(JieReflect.arrayClass(float.class), float[].class);
        assertEquals(JieReflect.arrayClass(double.class), double[].class);
        assertNull(JieReflect.arrayClass(void.class));

        // parameterized types:
        class X {
            public List<? extends String> l1 = null;
            public List<? extends String>[] l2 = null;
            public List<? extends String>[][] l3 = null;
        }
        Type l1Type = X.class.getDeclaredField("l1").getGenericType();
        assertEquals(JieReflect.arrayClass(l1Type), List[].class);
        Type l2Type = X.class.getDeclaredField("l2").getGenericType();
        assertEquals(JieReflect.arrayClass(l2Type), List[][].class);
        Type l3Type = X.class.getDeclaredField("l3").getGenericType();
        assertEquals(JieReflect.arrayClass(l3Type), List[][][].class);

        // is array:
        assertFalse(JieReflect.isArray(l1Type));
        assertTrue(JieReflect.isArray(l2Type));
        assertTrue(JieReflect.isArray(l3Type));
        assertTrue(JieReflect.isArray(int[].class));
        assertFalse(JieReflect.isArray(int.class));

        // component type:
        assertNull(JieReflect.getComponentType(l1Type));
        assertEquals(JieReflect.getComponentType(l2Type), l1Type);
        assertEquals(JieReflect.getComponentType(int[].class), int.class);
    }

    @Test
    public void testRuntimeClass() throws Exception {
        assertEquals(JieReflect.toRuntimeClass(int.class), int.class);
        class X<T> {
            public List<? extends String> l1 = null;
            public List<? extends String>[] l2 = null;
        }
        Type l1Type = X.class.getDeclaredField("l1").getGenericType();
        assertEquals(JieReflect.toRuntimeClass(l1Type), List.class);
        Type l2Type = X.class.getDeclaredField("l2").getGenericType();
        assertEquals(JieReflect.toRuntimeClass(l2Type), List[].class);
        assertNull(JieReflect.toRuntimeClass(X.class.getTypeParameters()[0]));
        GenericArrayType arrayType = JieType.array(X.class.getTypeParameters()[0]);
        assertNull(JieReflect.toRuntimeClass(arrayType));
    }

    @Test
    public void testWrapper() throws Exception {
        assertEquals(JieReflect.wrapperClass(boolean.class), Boolean.class);
        assertEquals(JieReflect.wrapperClass(byte.class), Byte.class);
        assertEquals(JieReflect.wrapperClass(short.class), Short.class);
        assertEquals(JieReflect.wrapperClass(char.class), Character.class);
        assertEquals(JieReflect.wrapperClass(int.class), Integer.class);
        assertEquals(JieReflect.wrapperClass(long.class), Long.class);
        assertEquals(JieReflect.wrapperClass(float.class), Float.class);
        assertEquals(JieReflect.wrapperClass(double.class), Double.class);
        assertEquals(JieReflect.wrapperClass(void.class), Void.class);
        assertEquals(JieReflect.wrapperClass(Object.class), Object.class);

        // unreachable:
        Method wrapperPrimitive = JieReflect.class.getDeclaredMethod("wrapperPrimitive", Class.class);
        reflectThrows(UnreachablePointException.class, wrapperPrimitive, null, Object.class);
    }

    @Test
    public void testClassExists() {
        assertTrue(JieReflect.classExists(String.class.getName()));
        assertFalse(JieReflect.classExists("123"));
        assertNull(JieReflect.classForName("123", null));
        assertNull(JieReflect.classForName("123", Thread.currentThread().getContextClassLoader()));
    }

    @Test
    public void testResolvingActualTypeArguments() {
        abstract class X<T> extends AbstractMap<String, Integer> {
        }
        assertEquals(
            JieReflect.resolveActualTypeArguments(X.class, Map.class),
            Arrays.asList(String.class, Integer.class)
        );
        assertEquals(
            JieReflect.resolveActualTypeArguments(X[].class, Map[].class),
            Arrays.asList(String.class, Integer.class)
        );
        assertEquals(
            JieReflect.resolveActualTypeArguments(X[].class, Object.class),
            Collections.emptyList()
        );
        abstract class Y<T> extends X<T> {
        }
        assertEquals(
            JieReflect.resolveActualTypeArguments(Y.class, X.class),
            Collections.singletonList(Y.class.getTypeParameters()[0])
        );
        // exception:
        expectThrows(ReflectionException.class, () -> JieReflect.resolveActualTypeArguments(X[].class, Map.class));
        expectThrows(ReflectionException.class, () -> JieReflect.resolveActualTypeArguments(X.class, Map[].class));
        expectThrows(
            ReflectionException.class,
            () -> JieReflect.resolveActualTypeArguments(X.class.getTypeParameters()[0], Map.class)
        );
    }

    @Test
    public void testMappingTypeParameter() throws Exception {
        class X {
            private MappingCls3 cls3 = null;
            private MappingCls2<Void> cls2 = null;
        }
        Type cls3 = X.class.getDeclaredField("cls3").getGenericType();
        Map<TypeVariable<?>, Type> map = JieReflect.mapTypeParameters(cls3);
        assertEquals(getTypeParameter(map, MappingCls2.class, 0), CharSequence.class);
        assertEquals(getTypeParameter(map, MappingCls1.class, 0), String.class);
        assertEquals(getTypeParameter(map, MappingCls1.class, 1), MappingCls2.class.getTypeParameters()[0]);
        assertEquals(getTypeParameter(map, MappingInterA.class, 0), Integer.class);
        assertEquals(getTypeParameter(map, MappingInterA.class, 1), Long.class);
        assertEquals(getTypeParameter(map, MappingInterA.class, 2), Float.class);
        assertEquals(getTypeParameter(map, MappingInterA.class, 3), MappingCls1.class.getTypeParameters()[0]);
        assertEquals(getTypeParameter(map, MappingInterB.class, 0), Boolean.class);
        assertEquals(getTypeParameter(map, MappingInterB.class, 1), Byte.class);
        assertEquals(getTypeParameter(map, MappingInterB.class, 2), Short.class);
        assertEquals(getTypeParameter(map, MappingInterB.class, 3), MappingCls1.class.getTypeParameters()[1]);
        assertEquals(getTypeParameter(map, MappingInterA1.class, 0), MappingInterA.class.getTypeParameters()[0]);
        assertEquals(getTypeParameter(map, MappingInterA1.class, 1), MappingInterA.class.getTypeParameters()[1]);
        assertEquals(getTypeParameter(map, MappingInterA2.class, 0), MappingInterA.class.getTypeParameters()[2]);
        assertEquals(getTypeParameter(map, MappingInterA2.class, 1), MappingInterA.class.getTypeParameters()[3]);
        assertEquals(getTypeParameter(map, MappingInterB1.class, 0), MappingInterB.class.getTypeParameters()[0]);
        assertEquals(getTypeParameter(map, MappingInterB1.class, 1), MappingInterB.class.getTypeParameters()[1]);
        assertEquals(getTypeParameter(map, MappingInterB2.class, 0), MappingInterB.class.getTypeParameters()[2]);
        assertEquals(getTypeParameter(map, MappingInterB2.class, 1), MappingInterB.class.getTypeParameters()[3]);
        assertEquals(map.size(), 19);
        Type cls2 = X.class.getDeclaredField("cls2").getGenericType();
        Map<TypeVariable<?>, Type> map2 = JieReflect.mapTypeParameters(cls2);
        assertEquals(getTypeParameter(map2, MappingCls2.class, 0), Void.class);
        assertEquals(map2.size(), 19);

        // special exception:
        Method mapTypeVariables = JieReflect.class.getDeclaredMethod("mapTypeVariables", Type.class, Map.class);
        mapTypeVariables.setAccessible(true);
        Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping = new HashMap<>();
        Type errorParam = errorParameterizedType();
        mapTypeVariables.invoke(null, errorParam, mapping);
        assertTrue(mapping.isEmpty());
    }

    private Type getTypeParameter(Map<TypeVariable<?>, Type> map, Class<?> cls, int index) {
        return map.get(cls.getTypeParameters()[index]);
    }

    private ParameterizedType errorParameterizedType() {
        return new ParameterizedType() {
            @Override
            @NotNull
            public Type[] getActualTypeArguments() {
                return new Type[0];
            }

            @NotNull
            @Override
            public Type getRawType() {
                return null;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
    }
    //
    // @Test
    // public void testReplaceType() {
    //     Type t = new TypeRef<List<Map<String, List<String>>>>() {
    //     }.type();
    //     Type tl = new TypeRef<List<String>>() {
    //     }.type();
    //     assertEquals(
    //         JieReflect.replaceType(t, tl, Integer.class),
    //         new TypeRef<List<Map<String, Integer>>>() {
    //         }.type()
    //     );
    //     Type tm = new TypeRef<Map<String, List<String>>>() {
    //     }.type();
    //     assertEquals(
    //         JieReflect.replaceType(tm, String.class, Integer.class),
    //         new TypeRef<Map<Integer, List<Integer>>>() {
    //         }.type()
    //     );
    //
    //     Type tw = new TypeRef<Map<String, ? extends List<String>>>() {
    //     }.type();
    //     assertEquals(
    //         JieReflect.replaceType(tw, String.class, Integer.class),
    //         new TypeRef<Map<Integer, ? extends List<Integer>>>() {
    //         }.type()
    //     );
    //
    //     Type tg = new TypeRef<Map<String, ? extends List<String>>[]>() {
    //     }.type();
    //     assertEquals(
    //         JieReflect.replaceType(tg, String.class, Integer.class),
    //         new TypeRef<Map<Integer, ? extends List<Integer>>[]>() {
    //         }.type()
    //     );
    //
    //     Type ts = new TypeRef<Map<String, ? extends List<String>>[]>() {
    //     }.type();
    //     assertSame(
    //         JieReflect.replaceType(ts, Integer.class, Integer.class),
    //         ts
    //     );
    //
    //     assertEquals(
    //         JieReflect.replaceType(new TypeRef<List<List<String>>>() {
    //         }.type(), List.class, ArrayList.class),
    //         new TypeRef<ArrayList<ArrayList<String>>>() {
    //         }.type()
    //     );
    //     assertEquals(
    //         JieReflect.replaceType(new TypeRef<List<ArrayList<String>>>() {
    //         }.type(), ArrayList.class, LinkedList.class),
    //         new TypeRef<List<LinkedList<String>>>() {
    //         }.type()
    //     );
    //     assertEquals(
    //         JieReflect.replaceType(new TypeRef<List<List<? extends String>>>() {
    //         }.type(), String.class, Integer.class),
    //         new TypeRef<List<List<? extends Integer>>>() {
    //         }.type()
    //     );
    //     assertEquals(
    //         JieReflect.replaceType(new TypeRef<List<List<? super String>>>() {
    //         }.type(), String.class, Integer.class),
    //         new TypeRef<List<List<? super Integer>>>() {
    //         }.type()
    //     );
    //     assertEquals(
    //         JieReflect.replaceType(JieType.array(String.class), String.class, Integer.class),
    //         JieType.array(Integer.class)
    //     );
    //     assertEquals(
    //         JieReflect.replaceType(JieType.lowerBound(JieType.lowerBound(String.class)), String.class, Integer.class),
    //         JieType.lowerBound(JieType.lowerBound(Integer.class))
    //     );
    //     assertEquals(
    //         JieReflect.replaceType(JieType.lowerBound(JieType.lowerBound(String.class)), String.class, String.class),
    //         JieType.lowerBound(JieType.lowerBound(String.class))
    //     );
    //
    //     assertEquals(
    //         JieReflect.replaceType(new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
    //         }.type(), NumberString1.class, NumberString2.class),
    //         new TypeRef<Inner<NumberString2>.SubInner<String, String>>() {
    //         }.type()
    //     );
    //     assertEquals(
    //         JieReflect.replaceType(new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
    //         }.type(), JieType.parameterized(Inner.class, Jie.array(NumberString1.class)), String.class),
    //         JieType.parameterized(
    //             Inner.SubInner.class,
    //             Jie.array(String.class, String.class),
    //             String.class
    //         )
    //     );
    // }

    // public interface SuperSuperInter1<SSI11, SSI12> {
    //     String ssif1 = null;
    //
    //     default void ssim1() {
    //     }
    // }
    //
    // public interface SuperSuperInter2<SSI21, SSI22> {
    //     String ssif2 = null;
    //
    //     default void ssim2() {
    //     }
    // }
    //
    // public interface SuperInter1<SI11, SI12> extends SuperSuperInter1<SI11, Integer>, SuperSuperInter2<Long, SI12> {
    //     String sif1 = null;
    //
    //     default void sim1() {
    //     }
    // }
    //
    // public interface SuperInter2<SI21, SSI22> {
    //     String sif2 = null;
    //
    //     default void sim2() {
    //     }
    // }
    //
    // public static class SuperClass1<SC11, SC12> {
    //     public static String scf1;
    //     private static String scf2;
    //
    //     public static void scm1() {
    //     }
    //
    //     private static void scm2() {
    //     }
    // }
    //
    // public static class Inner<T extends Number & CharSequence>
    //     extends SuperClass1<T, String> implements SuperInter1<Short, Character>, SuperInter2<Float, Double> {
    //
    //     public Inner() {
    //     }
    //
    //     public Inner(int i, String s) {
    //     }
    //
    //     private Inner(int i, String s, long l) {
    //     }
    //
    //     public static String f1;
    //     private static String f2;
    //
    //     public static void m1(int i) {
    //     }
    //
    //     private static void m2() {
    //     }
    //
    //     public class SubInner<SI1, SI2> {
    //     }
    //
    //     public T[] tArray;
    // }
    //
    // public static class NumberString1 extends Number implements CharSequence {
    //
    //     @Override
    //     public int length() {
    //         return 0;
    //     }
    //
    //     @Override
    //     public char charAt(int index) {
    //         return 0;
    //     }
    //
    //     @Nonnull
    //     @Override
    //     public CharSequence subSequence(int start, int end) {
    //         return null;
    //     }
    //
    //     @Override
    //     public int intValue() {
    //         return 0;
    //     }
    //
    //     @Override
    //     public long longValue() {
    //         return 0;
    //     }
    //
    //     @Override
    //     public float floatValue() {
    //         return 0;
    //     }
    //
    //     @Override
    //     public double doubleValue() {
    //         return 0;
    //     }
    // }
    //
    // public static class NumberString2 extends NumberString1 {
    // }


    public interface Inter0 {

        String i0 = "";

        default void im0() {
        }
    }

    public interface Inter1 {

        String i1 = "";

        default void im1() {
        }
    }

    public interface Inter2 extends Inter1, Inter0 {

        String i2 = "";

        default void im2() {
        }
    }

    public interface Inter3 extends Inter2, Inter0 {

        String i3 = "";

        default void im3() {
        }
    }

    public static class Cls1 implements Inter1, Inter0 {

        public String c1;
        private String pc1;

        public void cm1() {
        }

        private void pcm1() {
        }
    }

    public static class Cls2 extends Cls1 implements Inter2, Inter0 {

        public String c2;
        private String pc2;

        public void cm2() {
        }

        private void pcm2() {
        }
    }

    public static class Cls3 extends Cls2 implements Inter3, Inter0 {

        public String c3;
        private String pc3;

        public Cls3() {
        }

        private Cls3(int i) {
        }

        public void cm3() {
        }

        private void pcm3() {
        }
    }

    public interface MappingInterA1<A11, A12> {
    }

    public interface MappingInterA2<A21, A22> {
    }

    public interface MappingInterA<A1, A2, A3, A4> extends MappingInterA1<A1, A2>, MappingInterA2<A3, A4> {
    }

    public interface MappingInterB1<B11, B12> {
    }

    public interface MappingInterB2<B21, B22> {
    }

    public interface MappingInterB<B1, B2, B3, B4> extends MappingInterB1<B1, B2>, MappingInterB2<B3, B4> {
    }

    public static class MappingCls1<C1, C2> implements
        MappingInterA<Integer, Long, Float, C1>,
        MappingInterB<Boolean, Byte, Short, C2> {
    }

    public static class MappingCls2<C> extends MappingCls1<String, C> {
    }

    public static class MappingCls3 extends MappingCls2<CharSequence> {
    }
}
