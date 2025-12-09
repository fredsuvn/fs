package tests.reflect;

import org.junit.jupiter.api.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.Fs;
import space.sunqian.common.reflect.ReflectionException;
import space.sunqian.common.reflect.TypeKit;
import space.sunqian.common.reflect.TypeRef;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeTest {

    @Test
    public void testTypeCheck() throws Exception {
        class X<T> {
            List<String> l1;
            List<? extends String> l2;
            List<String>[] l3;
        }
        assertTrue(TypeKit.isClass(Object.class));
        assertFalse(TypeKit.isClass(X.class.getDeclaredField("l1").getGenericType()));
        assertTrue(TypeKit.isParameterized(X.class.getDeclaredField("l1").getGenericType()));
        assertFalse(TypeKit.isParameterized(Object.class));
        assertTrue(TypeKit.isWildcard(((ParameterizedType) X.class.getDeclaredField("l2").getGenericType())
            .getActualTypeArguments()[0]));
        assertFalse(TypeKit.isWildcard(Object.class));
        assertTrue(TypeKit.isTypeVariable(X.class.getTypeParameters()[0]));
        assertFalse(TypeKit.isTypeVariable(Object.class));
        assertTrue(TypeKit.isGenericArray(X.class.getDeclaredField("l3").getGenericType()));
        assertFalse(TypeKit.isGenericArray(Object.class));
    }

    @Test
    public void testLastName() throws Exception {
        assertEquals(TypeKit.getLastName(TypeKit.class), TypeKit.class.getSimpleName());
        Method method = TypeKit.class.getDeclaredMethod("getLastName", String.class);
        method.setAccessible(true);
        assertEquals("123", method.invoke(null, "123"));
        assertEquals("123", method.invoke(null, ".123"));
        assertEquals("23", method.invoke(null, ".1.23"));
        assertEquals("3", method.invoke(null, ".12.3"));
    }

    @Test
    public void testRawClass() throws Exception {
        class X {
            List<String> list;
        }
        assertEquals(String.class, TypeKit.getRawClass(String.class));
        Type listType = X.class.getDeclaredField("list").getGenericType();
        assertEquals(List.class, TypeKit.getRawClass(listType));
        assertNull(TypeKit.getRawClass(List.class.getTypeParameters()[0]));
        assertNull(TypeKit.getRawClass(TypeTest.errorParameterizedType()));
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
        assertEquals(String.class, TypeKit.getUpperBound(upper));
        assertNull(TypeKit.getLowerBound(upper));
        ParameterizedType lowerParam = (ParameterizedType) X.class.getDeclaredField("lower").getGenericType();
        WildcardType lower = (WildcardType) lowerParam.getActualTypeArguments()[0];
        assertEquals(String.class, TypeKit.getLowerBound(lower));
        assertEquals(Object.class, TypeKit.getUpperBound(lower));
        ParameterizedType queryParam = (ParameterizedType) X.class.getDeclaredField("query").getGenericType();
        WildcardType query = (WildcardType) queryParam.getActualTypeArguments()[0];
        assertNull(TypeKit.getLowerBound(upper));
        assertEquals(Object.class, TypeKit.getUpperBound(query));
        TypeVariable<?> t = X.class.getTypeParameters()[0];
        assertEquals(String.class, TypeKit.getFirstBound(t));
        TypeVariable<?> u = X.class.getTypeParameters()[1];
        assertEquals(Object.class, TypeKit.getFirstBound(u));

        // special:
        assertEquals(Object.class, TypeKit.getUpperBound(new WildcardType() {

            @Override
            public Type @Nonnull [] getUpperBounds() {
                return new Type[0];
            }

            @Override
            public Type @Nonnull [] getLowerBounds() {
                return new Type[0];
            }
        }));
        assertEquals(Object.class, TypeKit.getFirstBound(new TypeVariable<Class<?>>() {

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
        }));
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
        assertFalse(TypeKit.isArray(l1Type));
        assertTrue(TypeKit.isArray(l2Type));
        assertTrue(TypeKit.isArray(l3Type));
        assertTrue(TypeKit.isArray(int[].class));
        assertFalse(TypeKit.isArray(int.class));

        // component type:
        assertNull(TypeKit.getComponentType(l1Type));
        assertEquals(TypeKit.getComponentType(l2Type), l1Type);
        assertEquals(int.class, TypeKit.getComponentType(int[].class));
    }

    @Test
    public void testRuntimeClass() throws Exception {
        assertEquals(int.class, TypeKit.toRuntimeClass(int.class));
        class X<T extends String, U extends List<? extends String>> {
            List<? extends String> l1 = null;
            List<? extends String>[] l2 = null;
        }
        Type l1Type = X.class.getDeclaredField("l1").getGenericType();
        assertEquals(List.class, TypeKit.toRuntimeClass(l1Type));
        Type l2Type = X.class.getDeclaredField("l2").getGenericType();
        assertEquals(List[].class, TypeKit.toRuntimeClass(l2Type));
        assertEquals(String.class, TypeKit.toRuntimeClass(X.class.getTypeParameters()[0]));
        assertEquals(List.class, TypeKit.toRuntimeClass(X.class.getTypeParameters()[1]));
        GenericArrayType arrayType = TypeKit.arrayType(X.class.getTypeParameters()[0]);
        assertEquals(String[].class, TypeKit.toRuntimeClass(arrayType));
        ParameterizedType p = (ParameterizedType) TypeKit.getFirstBound(X.class.getTypeParameters()[1]);
        Type w = p.getActualTypeArguments()[0];
        assertNull(TypeKit.toRuntimeClass(w));
        assertNull(TypeKit.toRuntimeClass(TypeKit.arrayType(w)));
    }

    @Test
    public void testResolvingActualTypeArguments() {
        abstract class X<T> extends AbstractMap<String, Integer> {
        }
        assertEquals(
            TypeKit.resolveActualTypeArguments(X.class, Map.class),
            Arrays.asList(String.class, Integer.class)
        );
        assertEquals(
            TypeKit.resolveActualTypeArguments(X[].class, Map[].class),
            Arrays.asList(String.class, Integer.class)
        );
        assertEquals(
            TypeKit.resolveActualTypeArguments(X[].class, Object.class),
            Collections.emptyList()
        );
        abstract class Y<T> extends X<T> {
        }
        assertEquals(
            TypeKit.resolveActualTypeArguments(Y.class, X.class),
            Collections.singletonList(Y.class.getTypeParameters()[0])
        );
        // exception:
        assertThrows(ReflectionException.class, () -> TypeKit.resolveActualTypeArguments(TypeKit.otherType(), Object.class));
        assertThrows(ReflectionException.class, () -> TypeKit.resolveActualTypeArguments(X[].class, Map.class));
        assertThrows(ReflectionException.class, () -> TypeKit.resolveActualTypeArguments(X.class, Map[].class));
        assertThrows(
            ReflectionException.class,
            () -> TypeKit.resolveActualTypeArguments(X.class.getTypeParameters()[0], Map.class)
        );
    }

    @Test
    public void testMappingTypeParameter() throws Exception {
        class X {
            MappingCls3 cls3 = null;
            MappingCls2<Void> cls2 = null;
        }
        Type cls3 = X.class.getDeclaredField("cls3").getGenericType();
        Map<TypeVariable<?>, Type> map = TypeKit.typeParametersMapping(cls3);
        assertEquals(CharSequence.class, getTypeParameter(map, MappingCls2.class, 0));
        assertEquals(String.class, getTypeParameter(map, MappingCls1.class, 0));
        assertEquals(getTypeParameter(map, MappingCls1.class, 1), MappingCls2.class.getTypeParameters()[0]);
        assertEquals(Integer.class, getTypeParameter(map, MappingInterA.class, 0));
        assertEquals(Long.class, getTypeParameter(map, MappingInterA.class, 1));
        assertEquals(Float.class, getTypeParameter(map, MappingInterA.class, 2));
        assertEquals(getTypeParameter(map, MappingInterA.class, 3), MappingCls1.class.getTypeParameters()[0]);
        assertEquals(Boolean.class, getTypeParameter(map, MappingInterB.class, 0));
        assertEquals(Byte.class, getTypeParameter(map, MappingInterB.class, 1));
        assertEquals(Short.class, getTypeParameter(map, MappingInterB.class, 2));
        assertEquals(getTypeParameter(map, MappingInterB.class, 3), MappingCls1.class.getTypeParameters()[1]);
        assertEquals(getTypeParameter(map, MappingInterA1.class, 0), MappingInterA.class.getTypeParameters()[0]);
        assertEquals(getTypeParameter(map, MappingInterA1.class, 1), MappingInterA.class.getTypeParameters()[1]);
        assertEquals(getTypeParameter(map, MappingInterA2.class, 0), MappingInterA.class.getTypeParameters()[2]);
        assertEquals(getTypeParameter(map, MappingInterA2.class, 1), MappingInterA.class.getTypeParameters()[3]);
        assertEquals(getTypeParameter(map, MappingInterB1.class, 0), MappingInterB.class.getTypeParameters()[0]);
        assertEquals(getTypeParameter(map, MappingInterB1.class, 1), MappingInterB.class.getTypeParameters()[1]);
        assertEquals(getTypeParameter(map, MappingInterB2.class, 0), MappingInterB.class.getTypeParameters()[2]);
        assertEquals(getTypeParameter(map, MappingInterB2.class, 1), MappingInterB.class.getTypeParameters()[3]);
        assertEquals(19, map.size());
        Type cls2 = X.class.getDeclaredField("cls2").getGenericType();
        Map<TypeVariable<?>, Type> map2 = TypeKit.typeParametersMapping(cls2);
        assertEquals(Void.class, getTypeParameter(map2, MappingCls2.class, 0));
        assertEquals(19, map2.size());

        // special exception:
        {
            Method mapTypeVariables = TypeKit.class.getDeclaredMethod("mapTypeVariables", Type.class, Map.class);
            mapTypeVariables.setAccessible(true);
            Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping = new HashMap<>();
            Type errorParam = TypeTest.errorParameterizedType();
            mapTypeVariables.invoke(null, errorParam, mapping);
            assertTrue(mapping.isEmpty());
        }
        {
            Method mapTypeVariables = TypeKit.class.getDeclaredMethod("mapTypeVariables", Type[].class, Map.class);
            mapTypeVariables.setAccessible(true);
            Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping = new HashMap<>();
            Type errorParam = TypeTest.errorParameterizedType();
            mapTypeVariables.invoke(null, Fs.array(errorParam), mapping);
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
        assertEquals(Object.class, TypeKit.replaceType(X.class, X.class, Object.class));
        Type t1 = X.class.getDeclaredField("f1").getGenericType();
        Type r1 = X.class.getDeclaredField("r1").getGenericType();
        Type rp1 = TypeKit.replaceType(t1, String.class, Integer.class);
        assertEquals(rp1, r1);
        Type t2 = X.class.getDeclaredField("f2").getGenericType();
        Type r2 = X.class.getDeclaredField("r2").getGenericType();
        Type rp2 = TypeKit.replaceType(t2, String.class, Integer.class);
        assertEquals(rp2, r2);
        Type t3 = X.class.getDeclaredField("f3").getGenericType();
        Type r3 = X.class.getDeclaredField("r3").getGenericType();
        Type rp3 = TypeKit.replaceType(t3, String.class, Integer.class);
        assertEquals(rp3, r3);
        Type t4 = X.class.getDeclaredField("f4").getGenericType();
        Type r4 = X.class.getDeclaredField("r4").getGenericType();
        Type rp4 = TypeKit.replaceType(t4, String.class, Integer.class);
        assertEquals(rp4, r4);
        Type t5 = X.class.getDeclaredField("f5").getGenericType();
        Type r5 = X.class.getDeclaredField("r5").getGenericType();
        Type rp5 = TypeKit.replaceType(t5, String.class, Integer.class);
        assertEquals(rp5, r5);
        Type t6 = X.class.getDeclaredField("f6").getGenericType();
        Type r6 = X.class.getDeclaredField("r6").getGenericType();
        Type rp6 = TypeKit.replaceType(t6, HashMap.class, Hashtable.class);
        assertEquals(rp6, r6);

        // same:
        assertSame(TypeKit.replaceType(t1, Integer.class, String.class), t1);
        assertSame(TypeKit.replaceType(t2, Integer.class, String.class), t2);
        assertSame(TypeKit.replaceType(t3, Integer.class, String.class), t3);
        assertSame(TypeKit.replaceType(t4, Integer.class, String.class), t4);
        assertSame(TypeKit.replaceType(t5, Integer.class, String.class), t5);

        // special:
        assertThrows(ReflectionException.class, () ->
            TypeKit.replaceType(TypeTest.errorParameterizedType(), String.class, Integer.class));
        Type p1 = TypeKit.parameterizedType(List.class, Fs.array(), Integer.class);
        Type p2 = TypeKit.parameterizedType(List.class, Fs.array(), Long.class);
        assertEquals(TypeKit.replaceType(p1, Integer.class, Long.class), p2);
        assertSame(TypeKit.replaceType(p1, Integer.class, Integer.class), p1);
    }

    @Test
    public void testParameterized() throws Exception {
        class X<T> {

            List<String> list;

            class Y<U> {}

            class Z<U> {}
        }
        // no owner
        Type x = new TypeRef<X<String>>() {}.type();
        Type xx = TypeKit.parameterizedType(X.class, Fs.array(String.class));
        assertEquals(x, xx);
        assertEquals(x.toString(), xx.toString());
        assertEquals(x.hashCode(), xx.hashCode());
        assertTrue(xx.equals(xx));
        assertFalse(xx.equals(null));
        Type xf1 = TypeKit.parameterizedType(String.class, Fs.array(String.class));
        assertFalse(xx.equals(xf1));
        Type xf2 = TypeKit.parameterizedType(X.class, Fs.array(Integer.class));
        assertFalse(xx.equals(xf2));
        // has owner
        Type y = new TypeRef<X<String>.Y<Integer>>() {}.type();
        Type yy = TypeKit.parameterizedType(
            X.Y.class,
            Fs.array(Integer.class),
            TypeKit.parameterizedType(X.class, Fs.array(String.class))
        );
        assertEquals(y, yy);
        assertEquals(y.toString(), yy.toString());
        assertEquals(y.hashCode(), yy.hashCode());
        assertTrue(yy.equals(yy));
        assertFalse(yy.equals(null));
        Type yf1 = TypeKit.parameterizedType(
            X.class,
            Fs.array(Integer.class),
            TypeKit.parameterizedType(X.class, Fs.array(String.class))
        );
        assertFalse(yy.equals(yf1));
        Type yf2 = TypeKit.parameterizedType(
            X.Y.class,
            Fs.array(String.class),
            TypeKit.parameterizedType(X.class, Fs.array(String.class))
        );
        assertFalse(yy.equals(yf2));
        Type yf3 = TypeKit.parameterizedType(
            X.Y.class,
            Fs.array(Integer.class),
            TypeKit.parameterizedType(X.class, Fs.array(Integer.class))
        );
        assertFalse(yy.equals(yf3));
        Type yf4 = new TypeRef<X<String>.Z<Integer>>() {}.type();
        assertFalse(yy.equals(yf4));
        Type yf5 = new TypeRef<X<Integer>.Y<Integer>>() {}.type();
        assertFalse(yy.equals(yf5));
        Type yf6 = new TypeRef<X<String>.Y<String>>() {}.type();
        assertFalse(yy.equals(yf6));

        assertFalse(yy.equals(""));
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
        Type l1 = TypeKit.upperWildcard(String.class);
        assertEquals(l1, list1);
        assertEquals(l1.toString(), list1.toString());
        assertEquals(l1.hashCode(), list1.hashCode());
        Type list2 = ((ParameterizedType) (X.class.getDeclaredField("list2").getGenericType()))
            .getActualTypeArguments()[0];
        Type l2 = TypeKit.lowerWildcard(String.class);
        assertEquals(l2, list2);
        assertEquals(l2.toString(), list2.toString());
        assertEquals(l2.hashCode(), list2.hashCode());
        Type list3 = ((ParameterizedType) (X.class.getDeclaredField("list3").getGenericType()))
            .getActualTypeArguments()[0];
        Type l3 = TypeKit.wildcardChar();
        assertEquals(l3, list3);
        assertEquals(l3.toString(), list3.toString());
        assertEquals(l3.hashCode(), list3.hashCode());

        // equals:
        assertEquals(l1, l1);
        assertEquals(l1, TypeKit.upperWildcard(String.class));
        assertFalse(l1.equals(null));
        assertFalse(l1.equals(l2));
        assertFalse(l1.equals(l3));
        assertFalse(l1.equals(list2));
        assertFalse(l1.equals(list3));
        assertFalse(l1.equals(String.class));
        assertEquals("??", TypeKit.wildcardType(Fs.array(), Fs.array()).toString());
    }

    @Test
    public void testArray() throws Exception {
        class X {
            List<? extends String> list;
            List<? extends String>[] array;
        }
        Type list = X.class.getDeclaredField("list").getGenericType();
        Type array = X.class.getDeclaredField("array").getGenericType();
        Type genericArray = TypeKit.arrayType(list);
        assertEquals(genericArray, array);
        assertEquals(genericArray.toString(), array.toString());
        assertEquals(genericArray.hashCode(), array.hashCode());

        // equals:
        assertEquals(genericArray, genericArray);
        assertFalse(genericArray.equals(null));
        assertFalse(genericArray.equals(String.class));
        assertNotEquals(TypeKit.arrayType(String.class), TypeKit.arrayType(Integer.class));
    }

    @Test
    public void testOther() throws Exception {
        Type other1 = TypeKit.otherType();
        Type other2 = TypeKit.otherType();
        assertEquals(other1, other1);
        assertNotEquals(other1, other2);
        assertEquals(other1.toString(), other2.toString());
        assertEquals(other1.hashCode(), other2.hashCode());
    }

    public static ParameterizedType errorParameterizedType() {
        return new ParameterizedType() {
            @Override
            public Type @Nonnull [] getActualTypeArguments() {
                return new Type[0];
            }

            @Override
            public @Nonnull Type getRawType() {
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
