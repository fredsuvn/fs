package test.reflect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.JieReflect;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.NotPrimitiveException;
import xyz.sunqian.common.reflect.ReflectionException;
import xyz.sunqian.common.reflect.TypeRef;
import xyz.sunqian.test.JieTest;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

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
    public void testRawType() {
        assertEquals(JieReflect.getRawType(String.class), String.class);
        assertEquals(JieReflect.getRawType(JieType.parameterized(List.class, Jie.array(String.class))), List.class);
        assertNull(JieReflect.getRawType(List.class.getTypeParameters()[0]));
    }

    @Test
    public void testBounds() throws Exception {

        class TestBounds<T extends String, U> {
            public List<? extends String> upper = null;
            public List<? super String> lower = null;
        }

        ParameterizedType upperParam = (ParameterizedType) TestBounds.class.getField("upper").getGenericType();
        WildcardType upper = (WildcardType) upperParam.getActualTypeArguments()[0];
        assertEquals(JieReflect.getUpperBound(upper), String.class);
        assertNull(JieReflect.getLowerBound(upper));
        ParameterizedType lowerParam = (ParameterizedType) TestBounds.class.getField("lower").getGenericType();
        WildcardType lower = (WildcardType) lowerParam.getActualTypeArguments()[0];
        assertEquals(JieReflect.getLowerBound(lower), String.class);
        assertEquals(JieReflect.getUpperBound(lower), Object.class);
        TypeVariable<?> t = TestBounds.class.getTypeParameters()[0];
        assertEquals(JieReflect.getFirstBound(t), String.class);
        TypeVariable<?> u = TestBounds.class.getTypeParameters()[1];
        assertEquals(JieReflect.getFirstBound(u), Object.class);

        // special
        assertEquals(JieReflect.getUpperBound(new WildcardType() {

            @Override
            @NotNull
            public Type[] getUpperBounds() {
                return new Type[0];
            }

            @Override
            @NotNull
            public Type[] getLowerBounds() {
                return new Type[0];
            }
        }), Object.class);
        assertEquals(JieReflect.getFirstBound(new TypeVariable<Class<?>>() {

            @Nullable
            @Override
            public <T extends Annotation> T getAnnotation(@NotNull Class<T> annotationClass) {
                return null;
            }

            @Override
            @NotNull
            public Annotation[] getAnnotations() {
                return new Annotation[0];
            }

            @Override
            @NotNull
            public Annotation[] getDeclaredAnnotations() {
                return new Annotation[0];
            }

            @Override
            @NotNull
            public Type[] getBounds() {
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
            @NotNull
            public AnnotatedType[] getAnnotatedBounds() {
                return new AnnotatedType[0];
            }
        }), Object.class);
    }

    @Test
    public void testMember() throws Exception {

        // interface Inter1 {
        // }

        class Cls1  {
        }

        class Cls2 extends Cls1 {
        }

        class Cls3 extends Cls2 {
        }



        Field ssif1 = SuperSuperInter1.class.getDeclaredField("ssif1");
        Field ssif2 = SuperSuperInter2.class.getDeclaredField("ssif2");
        Field sif1 = SuperInter1.class.getDeclaredField("sif1");
        Field sif2 = SuperInter2.class.getDeclaredField("sif2");
        Field scf1 = SuperClass1.class.getDeclaredField("scf1");
        Field scf2 = SuperClass1.class.getDeclaredField("scf2");
        Field f1 = Inner.class.getDeclaredField("f1");
        Field f2 = Inner.class.getDeclaredField("f2");
        assertEquals(JieReflect.getField(Inner.class, "ssif1"), ssif1);
        assertEquals(JieReflect.getField(Inner.class, "ssif2"), ssif2);
        assertEquals(JieReflect.getField(Inner.class, "sif1"), sif1);
        assertEquals(JieReflect.getField(Inner.class, "sif2"), sif2);
        assertEquals(JieReflect.getField(Inner.class, "scf1"), scf1);
        assertEquals(JieReflect.getField(Inner.class, "scf2"), scf2);
        assertEquals(JieReflect.getField(Inner.class, "f1"), f1);
        assertEquals(JieReflect.getField(Inner.class, "f2"), f2);

        Method ssim1 = SuperSuperInter1.class.getDeclaredMethod("ssim1");
        Method ssim2 = SuperSuperInter2.class.getDeclaredMethod("ssim2");
        Method sim1 = SuperInter1.class.getDeclaredMethod("sim1");
        Method sim2 = SuperInter2.class.getDeclaredMethod("sim2");
        Method scm1 = SuperClass1.class.getDeclaredMethod("scm1");
        Method scm2 = SuperClass1.class.getDeclaredMethod("scm2");
        Method m1 = Inner.class.getDeclaredMethod("m1", int.class);
        Method m2 = Inner.class.getDeclaredMethod("m2");
        assertEquals(JieReflect.getMethod(Inner.class, "ssim1", Jie.array()), ssim1);
        assertEquals(JieReflect.getMethod(Inner.class, "ssim2", Jie.array()), ssim2);
        assertEquals(JieReflect.getMethod(Inner.class, "sim1", Jie.array()), sim1);
        assertEquals(JieReflect.getMethod(Inner.class, "sim2", Jie.array()), sim2);
        assertEquals(JieReflect.getMethod(Inner.class, "scm1", Jie.array()), scm1);
        assertEquals(JieReflect.getMethod(Inner.class, "scm2", Jie.array()), scm2);
        assertEquals(JieReflect.getMethod(Inner.class, "m1", Jie.array(int.class)), m1);
        assertEquals(JieReflect.getMethod(Inner.class, "m2", Jie.array()), m2);

        // assertNull(JieReflect.getField(JieType.other(), "1"));
        assertNull(JieReflect.getField(Inner.class, "1"));
        assertNull(JieReflect.getField(Inner.class, "1", false, false));
        // assertNull(JieReflect.getMethod(JieType.other(), "1", Jie.array()));
        assertNull(JieReflect.getMethod(Inner.class, "1", Jie.array()));
        assertNull(JieReflect.getMethod(Inner.class, "1", Jie.array(), false, false));

        Constructor<?> c1 = Inner.class.getConstructor();
        Constructor<?> c2 = Inner.class.getConstructor(int.class, String.class);
        Constructor<?> c3 = Inner.class.getDeclaredConstructor(int.class, String.class, long.class);
        assertEquals(c1, JieReflect.getConstructor(Inner.class, Jie.array()));
        assertEquals(c2, JieReflect.getConstructor(Inner.class, Jie.array(int.class, String.class)));
        assertEquals(c3, JieReflect.getConstructor(Inner.class, Jie.array(int.class, String.class, long.class)));
        assertNull(JieReflect.getConstructor(Inner.class, Jie.array(int.class, String.class, long.class), false));
        assertNull(JieReflect.getConstructor(Inner.class, Jie.array(int.class, String.class, long.class, double.class)));
    }

    @Test
    public void testNewInstance() throws Exception {
        assertEquals(JieReflect.newInstance(String.class.getName()), "");
        assertNull(JieReflect.newInstance("123"));
        assertNull(JieReflect.newInstance(List.class));
        Constructor<?> c2 = Inner.class.getConstructor(int.class, String.class);
        assertNotNull(JieReflect.newInstance(c2, 1, "s"));
        assertNull(JieReflect.newInstance(c2, 1, "s", 6));
    }

    @Test
    public void testArrayClass() {
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
        expectThrows(ReflectionException.class, () -> JieReflect.arrayClass(void.class));
        assertEquals(
            JieReflect.arrayClass(JieType.parameterized(List.class, Jie.array(JieType.upperBound(String.class)))),
            List[].class
        );
        assertEquals(
            JieReflect.arrayClass(JieType.array(JieType.parameterized(List.class, Jie.array(JieType.upperBound(String.class))))),
            List[][].class
        );
        assertEquals(
            JieReflect.arrayClass(JieType.array(JieType.array(JieType.parameterized(List.class, Jie.array(JieType.upperBound(String.class)))))),
            List[][][].class
        );
        assertEquals(
            JieReflect.arrayClass(JieType.array(String.class)),
            String[][].class
        );
        expectThrows(ReflectionException.class, () -> JieReflect.arrayClass(JieType.array(JieType.other())));
        expectThrows(ReflectionException.class, () -> JieReflect.arrayClass(Inner.class.getTypeParameters()[0]));
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

        // exception
        Method wrapperPrimitive = JieReflect.class.getDeclaredMethod("wrapperPrimitive", Class.class);
        JieTest.reflectThrows(NotPrimitiveException.class, wrapperPrimitive, null, Object.class);
    }

    @Test
    public void testClassExists() {
        assertTrue(JieReflect.classExists(String.class.getName()));
        assertFalse(JieReflect.classExists("123"));
        assertNull(JieReflect.classForName("123", null));
        assertNull(JieReflect.classForName("123", Thread.currentThread().getContextClassLoader()));
    }

    @Test
    public void testActualTypeArgs() {
        doTestActualTypeArgs(Inner.class, SuperSuperInter1.class, Short.class, Integer.class);
        doTestActualTypeArgs(
            JieType.parameterized(Inner.class, Jie.array(NumberString1.class)), SuperSuperInter1.class,
            Short.class, Integer.class
        );
        doTestActualTypeArgs(SuperInter1.class, SuperSuperInter1.class, SuperInter1.class.getTypeParameters()[0], Integer.class);
        doTestActualTypeArgs(List.class, ArrayList.class);
        doTestActualTypeArgs(JieType.parameterized(List.class, Jie.array(String.class)), ArrayList.class);
        doTestActualTypeArgs(JieType.other(), ArrayList.class);
        doTestActualTypeArgs(String.class, String.class);
        doTestActualTypeArgs(
            SuperSuperInter1.class, SuperSuperInter1.class,
            SuperSuperInter1.class.getTypeParameters()[0], SuperSuperInter1.class.getTypeParameters()[1]
        );
    }

    private void doTestActualTypeArgs(Type type, Class<?> rawType, Type... args) {
        List<Type> argsList = JieReflect.resolveActualTypeArguments(type, rawType);
        assertEquals(argsList.size(), args.length);
        for (int i = 0; i < argsList.size(); i++) {
            assertEquals(argsList.get(i), args[i]);
        }
    }

    @Test
    public void testTypeParameterMapping() {
        Map<TypeVariable<?>, Type> map = JieReflect.mapTypeParameters(new TypeRef<Inner<NumberString1>>() {
        }.getType());
        assertEquals(map.get(Inner.class.getTypeParameters()[0]), NumberString1.class);
        assertEquals(map.get(SuperClass1.class.getTypeParameters()[0]), Inner.class.getTypeParameters()[0]);
        assertEquals(map.get(SuperClass1.class.getTypeParameters()[1]), String.class);
        assertEquals(map.get(SuperInter1.class.getTypeParameters()[0]), Short.class);
        assertEquals(map.get(SuperInter1.class.getTypeParameters()[1]), Character.class);
        assertEquals(map.get(SuperInter2.class.getTypeParameters()[0]), Float.class);
        assertEquals(map.get(SuperInter2.class.getTypeParameters()[1]), Double.class);
        assertEquals(map.get(SuperSuperInter1.class.getTypeParameters()[0]), SuperInter1.class.getTypeParameters()[0]);
        assertEquals(map.get(SuperSuperInter1.class.getTypeParameters()[1]), Integer.class);
        assertEquals(map.get(SuperSuperInter2.class.getTypeParameters()[0]), Long.class);
        assertEquals(map.get(SuperSuperInter2.class.getTypeParameters()[1]), SuperInter1.class.getTypeParameters()[1]);
        assertEquals(JieReflect.mapTypeParameters(CharSequence.class), Collections.emptyMap());
        assertEquals(JieReflect.mapTypeParameters(JieType.other()), Collections.emptyMap());
    }

    @Test
    public void testReplaceType() {
        Type t = new TypeRef<List<Map<String, List<String>>>>() {
        }.getType();
        Type tl = new TypeRef<List<String>>() {
        }.getType();
        assertEquals(
            JieReflect.replaceType(t, tl, Integer.class),
            new TypeRef<List<Map<String, Integer>>>() {
            }.getType()
        );
        Type tm = new TypeRef<Map<String, List<String>>>() {
        }.getType();
        assertEquals(
            JieReflect.replaceType(tm, String.class, Integer.class),
            new TypeRef<Map<Integer, List<Integer>>>() {
            }.getType()
        );

        Type tw = new TypeRef<Map<String, ? extends List<String>>>() {
        }.getType();
        assertEquals(
            JieReflect.replaceType(tw, String.class, Integer.class),
            new TypeRef<Map<Integer, ? extends List<Integer>>>() {
            }.getType()
        );

        Type tg = new TypeRef<Map<String, ? extends List<String>>[]>() {
        }.getType();
        assertEquals(
            JieReflect.replaceType(tg, String.class, Integer.class),
            new TypeRef<Map<Integer, ? extends List<Integer>>[]>() {
            }.getType()
        );

        Type ts = new TypeRef<Map<String, ? extends List<String>>[]>() {
        }.getType();
        assertSame(
            JieReflect.replaceType(ts, Integer.class, Integer.class),
            ts
        );

        assertEquals(
            JieReflect.replaceType(new TypeRef<List<List<String>>>() {
            }.getType(), List.class, ArrayList.class),
            new TypeRef<ArrayList<ArrayList<String>>>() {
            }.getType()
        );
        assertEquals(
            JieReflect.replaceType(new TypeRef<List<ArrayList<String>>>() {
            }.getType(), ArrayList.class, LinkedList.class),
            new TypeRef<List<LinkedList<String>>>() {
            }.getType()
        );
        assertEquals(
            JieReflect.replaceType(new TypeRef<List<List<? extends String>>>() {
            }.getType(), String.class, Integer.class),
            new TypeRef<List<List<? extends Integer>>>() {
            }.getType()
        );
        assertEquals(
            JieReflect.replaceType(new TypeRef<List<List<? super String>>>() {
            }.getType(), String.class, Integer.class),
            new TypeRef<List<List<? super Integer>>>() {
            }.getType()
        );
        assertEquals(
            JieReflect.replaceType(JieType.array(String.class), String.class, Integer.class),
            JieType.array(Integer.class)
        );
        assertEquals(
            JieReflect.replaceType(JieType.lowerBound(JieType.lowerBound(String.class)), String.class, Integer.class),
            JieType.lowerBound(JieType.lowerBound(Integer.class))
        );
        assertEquals(
            JieReflect.replaceType(JieType.lowerBound(JieType.lowerBound(String.class)), String.class, String.class),
            JieType.lowerBound(JieType.lowerBound(String.class))
        );

        assertEquals(
            JieReflect.replaceType(new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
            }.getType(), NumberString1.class, NumberString2.class),
            new TypeRef<Inner<NumberString2>.SubInner<String, String>>() {
            }.getType()
        );
        assertEquals(
            JieReflect.replaceType(new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
            }.getType(), JieType.parameterized(Inner.class, Jie.array(NumberString1.class)), String.class),
            JieType.parameterized(
                Inner.SubInner.class,
                Jie.array(String.class, String.class),
                String.class
            )
        );
    }

    @Test
    public void testTypeRef() throws Exception {
        ParameterizedType parameterizedType = new TypeRef<List<String>>() {
        }.getParameterized();
        assertEquals(parameterizedType, JieType.parameterized(List.class, Jie.array(String.class)));
        Class<?> stringType = (Class<?>) new TypeRef<String>() {
        }.getType();
        assertEquals(stringType, String.class);
        Class<?> classType = (Class<?>) new TypeRef<Object>() {
        }.getType();
        assertEquals(classType, Object.class);

        class TestRef extends TypeRef<String> {
        }
        TestRef testRef = new TestRef();
        assertEquals(testRef.getType(), String.class);
        class TestRef2 extends TestRef {
        }
        TestRef2 testRef2 = new TestRef2();
        assertEquals(testRef2.getType(), String.class);
        class TestRef3<T> extends TypeRef<T> {
        }
        assertEquals(new TestRef3<String>() {
        }.getType(), String.class);

        Method get0 = TypeRef.class.getDeclaredMethod("get0", List.class);
        JieTest.reflectThrows(ReflectionException.class, get0, new TypeRef<Object>() {
        }, Collections.emptyList());
    }

    @Test
    public void testType() {
        ParameterizedType p1 = JieType.parameterized(Map.class, Jie.list(String.class, String.class));
        Type t1 = new TypeRef<Map<String, String>>() {
        }.getType();
        assertTrue(p1.equals(t1));
        assertFalse(p1.equals(String.class));
        assertEquals(p1.hashCode(), t1.hashCode());
        assertNotEquals(p1, new TypeRef<Map<String, Integer>>() {
        }.getType());
        ParameterizedType p2 = JieType.parameterized(
            Inner.SubInner.class,
            Jie.list(String.class, String.class),
            JieType.parameterized(Inner.class, Jie.array(NumberString1.class))
        );
        assertEquals(p2, new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
        }.getType());
        assertTrue(p1.equals(p1));
        assertFalse(p1.equals(null));
        assertNotEquals(p1, p2);
        assertNotEquals(p1, String.class);
        assertEquals(p1.toString(), new TypeRef<Map<String, String>>() {
        }.getType().toString());
        assertEquals(p2.toString(), new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
        }.getType().toString());
        assertEquals(p1.hashCode(), new TypeRef<Map<String, String>>() {
        }.getType().hashCode());
        assertEquals(p2.hashCode(), new TypeRef<Inner<NumberString1>.SubInner<String, String>>() {
        }.getType().hashCode());
        assertEquals(JieType.parameterized(List.class, Jie.array()).toString(), List.class.getName());
        assertFalse(
            JieType.parameterized(List.class, Jie.array(String.class), List.class).equals(
                JieType.parameterized(List.class, Jie.array(String.class)))
        );
        assertFalse(
            JieType.parameterized(List.class, Jie.array(String.class), List.class).equals(
                new TypeRef<List<String>>() {
                }.getType()
            ));
        assertFalse(
            JieType.parameterized(List.class, Jie.array(String.class), List.class).equals(
                new TypeRef<ArrayList<String>>() {
                }.getType()
            ));

        WildcardType w1 = JieType.upperBound(String.class);
        assertEquals(w1, new TypeRef<List<? extends String>>() {
        }.getParameterized().getActualTypeArguments()[0]);
        WildcardType w2 = JieType.lowerBound(String.class);
        assertEquals(w2, new TypeRef<List<? super String>>() {
        }.getParameterized().getActualTypeArguments()[0]);
        assertTrue(w1.equals(w1));
        assertFalse(w1.equals(null));
        assertNotEquals(w1, w2);
        assertNotEquals(w1, String.class);
        assertEquals(w1.toString(), new TypeRef<List<? extends String>>() {
        }.getParameterized().getActualTypeArguments()[0].toString());
        assertEquals(w2.toString(), new TypeRef<List<? super String>>() {
        }.getParameterized().getActualTypeArguments()[0].toString());
        assertEquals(w1.hashCode(), new TypeRef<List<? extends String>>() {
        }.getParameterized().getActualTypeArguments()[0].hashCode());
        assertEquals(w2.hashCode(), new TypeRef<List<? super String>>() {
        }.getParameterized().getActualTypeArguments()[0].hashCode());
        WildcardType w3 = JieType.wildcard(Jie.array(), Jie.array());
        assertEquals(w3.toString(), "?");
        WildcardType w4 = JieType.upperBound(Object.class);
        assertEquals(w4, new TypeRef<List<? extends Object>>() {
        }.getParameterized().getActualTypeArguments()[0]);
        assertEquals(w4.toString(), new TypeRef<List<? extends Object>>() {
        }.getParameterized().getActualTypeArguments()[0].toString());
        WildcardType w5 = JieType.wildcard(Jie.array(String.class, Integer.class), Jie.array());
        assertEquals(w5.toString(), "? extends " + String.class.getName() + " & " + Integer.class.getName());
        WildcardType w6 = JieType.lowerBound(null);
        assertEquals(w6.toString(), "? super java.lang.Object");
        assertFalse(JieType.questionMark().equals(new TypeRef<List<? extends String>>() {
        }.getParameterized().getActualTypeArguments()[0]));

        GenericArrayType g1 = JieType.array(JieType.parameterized(List.class, Jie.array(String.class)));
        assertEquals(g1, new TypeRef<List<String>[]>() {
        }.getType());
        assertTrue(g1.equals(g1));
        assertFalse(g1.equals(null));
        assertNotEquals(g1, w2);
        assertNotEquals(g1, String.class);
        assertEquals(g1.toString(), new TypeRef<List<String>[]>() {
        }.getType().toString());
        assertEquals(g1.hashCode(), new TypeRef<List<String>[]>() {
        }.getType().hashCode());
        assertEquals(JieType.array(String.class).toString(), String.class.getName() + "[]");

        Type other = JieType.other();
        assertEquals(other.getTypeName(), "Hello, Jieva!");
        assertTrue(other.equals(other));
        assertFalse(other.equals(null));
        assertNotEquals(other, w2);
        assertNotEquals(other, String.class);
        assertEquals(other.hashCode(), 1);
    }

    public interface SuperSuperInter1<SSI11, SSI12> {
        String ssif1 = null;

        default void ssim1() {
        }
    }

    public interface SuperSuperInter2<SSI21, SSI22> {
        String ssif2 = null;

        default void ssim2() {
        }
    }

    public interface SuperInter1<SI11, SI12> extends SuperSuperInter1<SI11, Integer>, SuperSuperInter2<Long, SI12> {
        String sif1 = null;

        default void sim1() {
        }
    }

    public interface SuperInter2<SI21, SSI22> {
        String sif2 = null;

        default void sim2() {
        }
    }

    public static class SuperClass1<SC11, SC12> {
        public static String scf1;
        private static String scf2;

        public static void scm1() {
        }

        private static void scm2() {
        }
    }

    public static class Inner<T extends Number & CharSequence>
        extends SuperClass1<T, String> implements SuperInter1<Short, Character>, SuperInter2<Float, Double> {

        public Inner() {
        }

        public Inner(int i, String s) {
        }

        private Inner(int i, String s, long l) {
        }

        public static String f1;
        private static String f2;

        public static void m1(int i) {
        }

        private static void m2() {
        }

        public class SubInner<SI1, SI2> {
        }

        public T[] tArray;
    }

    public static class NumberString1 extends Number implements CharSequence {

        @Override
        public int length() {
            return 0;
        }

        @Override
        public char charAt(int index) {
            return 0;
        }

        @NotNull
        @Override
        public CharSequence subSequence(int start, int end) {
            return null;
        }

        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }

        @Override
        public float floatValue() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0;
        }
    }

    public static class NumberString2 extends NumberString1 {
    }
}
