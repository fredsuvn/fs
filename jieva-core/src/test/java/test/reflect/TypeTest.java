package test.reflect;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class TypeTest {

    @Test
    public void testTypeCheck() throws Exception {
        class X<T> {
            List<String> l1;
            List<? extends String> l2;
            List<String>[] l3;
        }
        assertTrue(JieType.isClass(Object.class));
        assertFalse(JieType.isClass(X.class.getDeclaredField("l1").getGenericType()));
        assertTrue(JieType.isParameterized(X.class.getDeclaredField("l1").getGenericType()));
        assertFalse(JieType.isParameterized(Object.class));
        assertTrue(JieType.isWildcard(((ParameterizedType) X.class.getDeclaredField("l2").getGenericType())
            .getActualTypeArguments()[0]));
        assertFalse(JieType.isWildcard(Object.class));
        assertTrue(JieType.isTypeVariable(X.class.getTypeParameters()[0]));
        assertFalse(JieType.isTypeVariable(Object.class));
        assertTrue(JieType.isGenericArray(X.class.getDeclaredField("l3").getGenericType()));
        assertFalse(JieType.isGenericArray(Object.class));
    }

    @Test
    public void testLastName() throws Exception {
        assertEquals(JieType.getLastName(ReflectTest.class), ReflectTest.class.getSimpleName());
        Method method = JieType.class.getDeclaredMethod("getLastName", String.class);
        method.setAccessible(true);
        assertEquals(method.invoke(null, "123"), "123");
        assertEquals(method.invoke(null, ".123"), "123");
        assertEquals(method.invoke(null, ".1.23"), "23");
        assertEquals(method.invoke(null, ".12.3"), "3");
    }

    @Test
    public void testRawClass() throws Exception {
        class X {
            List<String> list;
        }
        assertEquals(JieType.getRawClass(String.class), String.class);
        Type listType = X.class.getDeclaredField("list").getGenericType();
        assertEquals(JieType.getRawClass(listType), List.class);
        assertNull(JieType.getRawClass(List.class.getTypeParameters()[0]));
        assertNull(JieType.getRawClass(TypeTest.errorParameterizedType()));
    }

    @Test
    public void testBounds() throws Exception {
        class X<T extends String, U> {
            List<? extends String> upper = null;
            List<? super String> lower = null;
            List<?> query = null;
        }
        ParameterizedType upperParam = (ParameterizedType) X.class.getDeclaredField("upper").getGenericType();
        WildcardType upper = (WildcardType) upperParam.getActualTypeArguments()[0];
        assertEquals(JieType.getUpperBound(upper), String.class);
        assertNull(JieType.getLowerBound(upper));
        ParameterizedType lowerParam = (ParameterizedType) X.class.getDeclaredField("lower").getGenericType();
        WildcardType lower = (WildcardType) lowerParam.getActualTypeArguments()[0];
        assertEquals(JieType.getLowerBound(lower), String.class);
        assertEquals(JieType.getUpperBound(lower), Object.class);
        ParameterizedType queryParam = (ParameterizedType) X.class.getDeclaredField("query").getGenericType();
        WildcardType query = (WildcardType) queryParam.getActualTypeArguments()[0];
        assertNull(JieType.getLowerBound(upper));
        assertEquals(JieType.getUpperBound(query), Object.class);
        TypeVariable<?> t = X.class.getTypeParameters()[0];
        assertEquals(JieType.getFirstBound(t), String.class);
        TypeVariable<?> u = X.class.getTypeParameters()[1];
        assertEquals(JieType.getFirstBound(u), Object.class);

        // special:
        assertEquals(JieType.getUpperBound(new WildcardType() {

            @Override
            public Type @Nonnull [] getUpperBounds() {
                return new Type[0];
            }

            @Override
            public Type @Nonnull [] getLowerBounds() {
                return new Type[0];
            }
        }), Object.class);
        assertEquals(JieType.getFirstBound(new TypeVariable<Class<?>>() {

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
    public void testArrayClass() throws Exception {
        class X {
            List<? extends String> l1 = null;
            List<? extends String>[] l2 = null;
            List<? extends String>[][] l3 = null;
        }
        Type l1Type = X.class.getDeclaredField("l1").getGenericType();
        Type l2Type = X.class.getDeclaredField("l2").getGenericType();
        Type l3Type = X.class.getDeclaredField("l3").getGenericType();

        // is array:
        assertFalse(JieType.isArray(l1Type));
        assertTrue(JieType.isArray(l2Type));
        assertTrue(JieType.isArray(l3Type));
        assertTrue(JieType.isArray(int[].class));
        assertFalse(JieType.isArray(int.class));

        // component type:
        assertNull(JieType.getComponentType(l1Type));
        assertEquals(JieType.getComponentType(l2Type), l1Type);
        assertEquals(JieType.getComponentType(int[].class), int.class);
    }

    @Test
    public void testRuntimeClass() throws Exception {
        assertEquals(JieType.toRuntimeClass(int.class), int.class);
        class X<T extends String, U extends List<? extends String>> {
            List<? extends String> l1 = null;
            List<? extends String>[] l2 = null;
        }
        Type l1Type = X.class.getDeclaredField("l1").getGenericType();
        assertEquals(JieType.toRuntimeClass(l1Type), List.class);
        Type l2Type = X.class.getDeclaredField("l2").getGenericType();
        assertEquals(JieType.toRuntimeClass(l2Type), List[].class);
        assertEquals(JieType.toRuntimeClass(X.class.getTypeParameters()[0]), String.class);
        assertEquals(JieType.toRuntimeClass(X.class.getTypeParameters()[1]), List.class);
        GenericArrayType arrayType = JieType.newArrayType(X.class.getTypeParameters()[0]);
        assertEquals(JieType.toRuntimeClass(arrayType), String[].class);
        ParameterizedType p = (ParameterizedType) JieType.getFirstBound(X.class.getTypeParameters()[1]);
        Type w = p.getActualTypeArguments()[0];
        assertNull(JieType.toRuntimeClass(w));
        assertNull(JieType.toRuntimeClass(JieType.newArrayType(w)));
    }

    @Test
    public void testParameterized() throws Exception {
        class X<T> {

            List<String> list;

            class Y<U> {
            }
        }
        Type x = new TypeRef<X<String>>() {
        }.type();
        Type xx = JieType.newParameterizedType(X.class, Jie.array(String.class));
        assertEquals(x, xx);
        assertEquals(x.toString(), xx.toString());
        assertEquals(x.hashCode(), xx.hashCode());
        Type y = new TypeRef<X<String>.Y<Integer>>() {
        }.type();
        Type yy = JieType.newParameterizedType(
            X.Y.class,
            Jie.array(Integer.class),
            JieType.newParameterizedType(X.class, Jie.array(String.class))
        );
        assertEquals(y, yy);
        assertEquals(y.toString(), yy.toString());
        assertEquals(y.hashCode(), yy.hashCode());

        // hello class
        Type h = new TypeRef<Hello<String>>() {
        }.type();
        Type hh = JieType.newParameterizedType(Hello.class, Jie.array(String.class));
        assertEquals(h, hh);
        assertEquals(h.toString(), hh.toString());
        assertEquals(h.hashCode(), hh.hashCode());
        Type w1 = new TypeRef<Hello.W1<Integer>>() {
        }.type();
        Type ww1 = JieType.newParameterizedType(Hello.W1.class, Jie.array(Integer.class));
        assertEquals(w1, ww1);
        assertEquals(w1.toString(), ww1.toString());
        assertEquals(w1.hashCode(), ww1.hashCode());
        Type w2 = new TypeRef<Hello<String>.W2<Integer, Long>>() {
        }.type();
        Type ww2 = JieType.newParameterizedType(
            Hello.W2.class,
            Jie.array(Integer.class, Long.class),
            JieType.newParameterizedType(Hello.class, Jie.array(String.class))
        );
        assertEquals(w2, ww2);
        assertEquals(w2.toString(), ww2.toString());
        assertEquals(w2.hashCode(), ww2.hashCode());

        // equals:
        assertEquals(ww1, ww1);
        assertFalse(ww1.equals(null));
        Type ww3 = JieType.newParameterizedType(
            Hello.W2.class,
            Jie.array(Integer.class, Long.class),
            JieType.newParameterizedType(Hello.class, Jie.array(String.class))
        );
        assertEquals(ww2, ww3);
        Type ww4 = JieType.newParameterizedType(
            List.class,
            Jie.array(Integer.class, Long.class),
            JieType.newParameterizedType(Hello.class, Jie.array(String.class))
        );
        assertNotEquals(ww2, ww4);
        Type ww5 = JieType.newParameterizedType(
            Hello.W2.class,
            Jie.array(Integer.class, Long.class),
            List.class
        );
        assertNotEquals(ww2, ww5);
        assertFalse(ww1.equals(String.class));
        assertFalse(ww1.equals(w2));
        Type w3 = new TypeRef<Hello.W3<Integer>>() {
        }.type();
        assertFalse(ww1.equals(w3));
    }

    @Test
    public void testWildcard() throws Exception {
        class X {
            List<? extends String> list1;
            List<? super String> list2;
            List<?> list3;
        }
        Type list1 = ((ParameterizedType) (X.class.getDeclaredField("list1").getGenericType()))
            .getActualTypeArguments()[0];
        Type l1 = JieType.newWildcardUpper(String.class);
        assertEquals(l1, list1);
        assertEquals(l1.toString(), list1.toString());
        assertEquals(l1.hashCode(), list1.hashCode());
        Type list2 = ((ParameterizedType) (X.class.getDeclaredField("list2").getGenericType()))
            .getActualTypeArguments()[0];
        Type l2 = JieType.newWildcardLower(String.class);
        assertEquals(l2, list2);
        assertEquals(l2.toString(), list2.toString());
        assertEquals(l2.hashCode(), list2.hashCode());
        Type list3 = ((ParameterizedType) (X.class.getDeclaredField("list3").getGenericType()))
            .getActualTypeArguments()[0];
        Type l3 = JieType.wildcardChar();
        assertEquals(l3, list3);
        assertEquals(l3.toString(), list3.toString());
        assertEquals(l3.hashCode(), list3.hashCode());

        // equals:
        assertEquals(l1, l1);
        assertFalse(l1.equals(null));
        assertFalse(l1.equals(l2));
        assertFalse(l1.equals(l3));
        assertFalse(l1.equals(list2));
        assertFalse(l1.equals(list3));
        assertFalse(l1.equals(String.class));
        assertEquals(JieType.newWildcardType(Jie.array(), Jie.array()).toString(), "??");
    }

    @Test
    public void testArray() throws Exception {
        class X {
            List<? extends String> list;
            List<? extends String>[] array;
        }
        Type list = X.class.getDeclaredField("list").getGenericType();
        Type array = X.class.getDeclaredField("array").getGenericType();
        Type genericArray = JieType.newArrayType(list);
        assertEquals(genericArray, array);
        assertEquals(genericArray.toString(), array.toString());
        assertEquals(genericArray.hashCode(), array.hashCode());

        // equals:
        assertEquals(genericArray, genericArray);
        assertFalse(genericArray.equals(null));
        assertFalse(genericArray.equals(String.class));
        assertNotEquals(JieType.newArrayType(String.class), JieType.newArrayType(Integer.class));
    }

    @Test
    public void testOther() throws Exception {
        Type other1 = JieType.newOtherType();
        Type other2 = JieType.newOtherType();
        assertEquals(other1, other1);
        assertNotEquals(other1, other2);
        assertEquals(other1.toString(), other2.toString());
        assertEquals(other1.hashCode(), other2.hashCode());
    }

    public static ParameterizedType errorParameterizedType() {
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
}
