package test.reflect;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.ReflectionException;
import xyz.sunqian.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

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
        assertEquals(JieType.getLastName(JieType.class), JieType.class.getSimpleName());
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
        GenericArrayType arrayType = JieType.arrayType(X.class.getTypeParameters()[0]);
        assertEquals(JieType.toRuntimeClass(arrayType), String[].class);
        ParameterizedType p = (ParameterizedType) JieType.getFirstBound(X.class.getTypeParameters()[1]);
        Type w = p.getActualTypeArguments()[0];
        assertNull(JieType.toRuntimeClass(w));
        assertNull(JieType.toRuntimeClass(JieType.arrayType(w)));
    }

    @Test
    public void testResolvingActualTypeArguments() {
        abstract class X<T> extends AbstractMap<String, Integer> {
        }
        assertEquals(
            JieType.resolveActualTypeArguments(X.class, Map.class),
            Arrays.asList(String.class, Integer.class)
        );
        assertEquals(
            JieType.resolveActualTypeArguments(X[].class, Map[].class),
            Arrays.asList(String.class, Integer.class)
        );
        assertEquals(
            JieType.resolveActualTypeArguments(X[].class, Object.class),
            Collections.emptyList()
        );
        abstract class Y<T> extends X<T> {
        }
        assertEquals(
            JieType.resolveActualTypeArguments(Y.class, X.class),
            Collections.singletonList(Y.class.getTypeParameters()[0])
        );
        // exception:
        expectThrows(ReflectionException.class, () -> JieType.resolveActualTypeArguments(JieType.otherType(), Object.class));
        expectThrows(ReflectionException.class, () -> JieType.resolveActualTypeArguments(X[].class, Map.class));
        expectThrows(ReflectionException.class, () -> JieType.resolveActualTypeArguments(X.class, Map[].class));
        expectThrows(
            ReflectionException.class,
            () -> JieType.resolveActualTypeArguments(X.class.getTypeParameters()[0], Map.class)
        );
    }

    @Test
    public void testMappingTypeParameter() throws Exception {
        class X {
            MappingCls3 cls3 = null;
            MappingCls2<Void> cls2 = null;
        }
        Type cls3 = X.class.getDeclaredField("cls3").getGenericType();
        Map<TypeVariable<?>, Type> map = JieType.mapTypeParameters(cls3);
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
        Map<TypeVariable<?>, Type> map2 = JieType.mapTypeParameters(cls2);
        assertEquals(getTypeParameter(map2, MappingCls2.class, 0), Void.class);
        assertEquals(map2.size(), 19);

        // special exception:
        {
            Method mapTypeVariables = JieType.class.getDeclaredMethod("mapTypeVariables", Type.class, Map.class);
            mapTypeVariables.setAccessible(true);
            Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping = new HashMap<>();
            Type errorParam = TypeTest.errorParameterizedType();
            mapTypeVariables.invoke(null, errorParam, mapping);
            assertTrue(mapping.isEmpty());
        }
        {
            Method mapTypeVariables = JieType.class.getDeclaredMethod("mapTypeVariables", Type[].class, Map.class);
            mapTypeVariables.setAccessible(true);
            Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping = new HashMap<>();
            Type errorParam = TypeTest.errorParameterizedType();
            mapTypeVariables.invoke(null, Jie.array(errorParam), mapping);
            assertTrue(mapping.isEmpty());
        }
    }

    private Type getTypeParameter(Map<TypeVariable<?>, Type> map, Class<?> cls, int index) {
        return map.get(cls.getTypeParameters()[index]);
    }

    @Test
    public void testReplaceType() throws Exception {
        class X {
            // String:
            List<String> f1;
            List<? extends String> f2;
            List<? extends String>[] f3;
            List<? extends List<? extends String>[]> f4;
            Map<
                ? super List<? extends List<? extends String>[]>,
                ? extends List<? extends List<? extends String>[]>
                > f5;
            HashMap<
                ? super List<? extends List<? extends HashMap>[]>,
                ? extends List<? extends List<? extends HashMap>[]>
                > f6;

            // Integer
            List<Integer> r1;
            List<? extends Integer> r2;
            List<? extends Integer>[] r3;
            List<? extends List<? extends Integer>[]> r4;
            Map<
                ? super List<? extends List<? extends Integer>[]>,
                ? extends List<? extends List<? extends Integer>[]>
                > r5;
            Hashtable<
                ? super List<? extends List<? extends Hashtable>[]>,
                ? extends List<? extends List<? extends Hashtable>[]>
                > r6;
        }
        assertEquals(JieType.replaceType(X.class, X.class, Object.class), Object.class);
        Type t1 = X.class.getDeclaredField("f1").getGenericType();
        Type r1 = X.class.getDeclaredField("r1").getGenericType();
        Type rp1 = JieType.replaceType(t1, String.class, Integer.class);
        assertEquals(rp1, r1);
        Type t2 = X.class.getDeclaredField("f2").getGenericType();
        Type r2 = X.class.getDeclaredField("r2").getGenericType();
        Type rp2 = JieType.replaceType(t2, String.class, Integer.class);
        assertEquals(rp2, r2);
        Type t3 = X.class.getDeclaredField("f3").getGenericType();
        Type r3 = X.class.getDeclaredField("r3").getGenericType();
        Type rp3 = JieType.replaceType(t3, String.class, Integer.class);
        assertEquals(rp3, r3);
        Type t4 = X.class.getDeclaredField("f4").getGenericType();
        Type r4 = X.class.getDeclaredField("r4").getGenericType();
        Type rp4 = JieType.replaceType(t4, String.class, Integer.class);
        assertEquals(rp4, r4);
        Type t5 = X.class.getDeclaredField("f5").getGenericType();
        Type r5 = X.class.getDeclaredField("r5").getGenericType();
        Type rp5 = JieType.replaceType(t5, String.class, Integer.class);
        assertEquals(rp5, r5);
        Type t6 = X.class.getDeclaredField("f6").getGenericType();
        Type r6 = X.class.getDeclaredField("r6").getGenericType();
        Type rp6 = JieType.replaceType(t6, HashMap.class, Hashtable.class);
        assertEquals(rp6, r6);

        // same:
        assertSame(JieType.replaceType(t1, Integer.class, String.class), t1);
        assertSame(JieType.replaceType(t2, Integer.class, String.class), t2);
        assertSame(JieType.replaceType(t3, Integer.class, String.class), t3);
        assertSame(JieType.replaceType(t4, Integer.class, String.class), t4);
        assertSame(JieType.replaceType(t5, Integer.class, String.class), t5);

        // special:
        expectThrows(ReflectionException.class, () ->
            JieType.replaceType(TypeTest.errorParameterizedType(), String.class, Integer.class));
        Type p1 = JieType.parameterizedType(List.class, Jie.array(), Integer.class);
        Type p2 = JieType.parameterizedType(List.class, Jie.array(), Long.class);
        assertEquals(JieType.replaceType(p1, Integer.class, Long.class), p2);
        assertSame(JieType.replaceType(p1, Integer.class, Integer.class), p1);
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
        Type xx = JieType.parameterizedType(X.class, Jie.array(String.class));
        assertEquals(x, xx);
        assertEquals(x.toString(), xx.toString());
        assertEquals(x.hashCode(), xx.hashCode());
        Type y = new TypeRef<X<String>.Y<Integer>>() {
        }.type();
        Type yy = JieType.parameterizedType(
            X.Y.class,
            Jie.array(Integer.class),
            JieType.parameterizedType(X.class, Jie.array(String.class))
        );
        assertEquals(y, yy);
        assertEquals(y.toString(), yy.toString());
        assertEquals(y.hashCode(), yy.hashCode());

        // hello class
        Type h = new TypeRef<Hello<String>>() {
        }.type();
        Type hh = JieType.parameterizedType(Hello.class, Jie.array(String.class));
        assertEquals(h, hh);
        assertEquals(h.toString(), hh.toString());
        assertEquals(h.hashCode(), hh.hashCode());
        Type w1 = new TypeRef<Hello.W1<Integer>>() {
        }.type();
        Type ww1 = JieType.parameterizedType(Hello.W1.class, Jie.array(Integer.class));
        assertEquals(w1, ww1);
        assertEquals(w1.toString(), ww1.toString());
        assertEquals(w1.hashCode(), ww1.hashCode());
        Type w2 = new TypeRef<Hello<String>.W2<Integer, Long>>() {
        }.type();
        Type ww2 = JieType.parameterizedType(
            Hello.W2.class,
            Jie.array(Integer.class, Long.class),
            JieType.parameterizedType(Hello.class, Jie.array(String.class))
        );
        assertEquals(w2, ww2);
        assertEquals(w2.toString(), ww2.toString());
        assertEquals(w2.hashCode(), ww2.hashCode());

        // equals:
        assertEquals(ww1, ww1);
        assertFalse(ww1.equals(null));
        Type ww3 = JieType.parameterizedType(
            Hello.W2.class,
            Jie.array(Integer.class, Long.class),
            JieType.parameterizedType(Hello.class, Jie.array(String.class))
        );
        assertEquals(ww2, ww3);
        Type ww4 = JieType.parameterizedType(
            List.class,
            Jie.array(Integer.class, Long.class),
            JieType.parameterizedType(Hello.class, Jie.array(String.class))
        );
        assertNotEquals(ww2, ww4);
        Type ww5 = JieType.parameterizedType(
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
        Type l1 = JieType.upperWildcard(String.class);
        assertEquals(l1, list1);
        assertEquals(l1.toString(), list1.toString());
        assertEquals(l1.hashCode(), list1.hashCode());
        Type list2 = ((ParameterizedType) (X.class.getDeclaredField("list2").getGenericType()))
            .getActualTypeArguments()[0];
        Type l2 = JieType.lowerWildcard(String.class);
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
        assertEquals(JieType.wildcardType(Jie.array(), Jie.array()).toString(), "??");
    }

    @Test
    public void testArray() throws Exception {
        class X {
            List<? extends String> list;
            List<? extends String>[] array;
        }
        Type list = X.class.getDeclaredField("list").getGenericType();
        Type array = X.class.getDeclaredField("array").getGenericType();
        Type genericArray = JieType.arrayType(list);
        assertEquals(genericArray, array);
        assertEquals(genericArray.toString(), array.toString());
        assertEquals(genericArray.hashCode(), array.hashCode());

        // equals:
        assertEquals(genericArray, genericArray);
        assertFalse(genericArray.equals(null));
        assertFalse(genericArray.equals(String.class));
        assertNotEquals(JieType.arrayType(String.class), JieType.arrayType(Integer.class));
    }

    @Test
    public void testOther() throws Exception {
        Type other1 = JieType.otherType();
        Type other2 = JieType.otherType();
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
