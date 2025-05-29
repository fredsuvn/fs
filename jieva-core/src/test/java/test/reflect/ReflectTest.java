package test.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.JieReflect;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.ReflectionException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ReflectTest {

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
        expectThrows(ReflectionException.class, () -> JieReflect.resolveActualTypeArguments(JieType.otherType(), Object.class));
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
            MappingCls3 cls3 = null;
            MappingCls2<Void> cls2 = null;
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
        {
            Method mapTypeVariables = JieReflect.class.getDeclaredMethod("mapTypeVariables", Type.class, Map.class);
            mapTypeVariables.setAccessible(true);
            Map<@Nonnull TypeVariable<?>, @Nullable Type> mapping = new HashMap<>();
            Type errorParam = TypeTest.errorParameterizedType();
            mapTypeVariables.invoke(null, errorParam, mapping);
            assertTrue(mapping.isEmpty());
        }
        {
            Method mapTypeVariables = JieReflect.class.getDeclaredMethod("mapTypeVariables", Type[].class, Map.class);
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
        assertEquals(JieReflect.replaceType(X.class, X.class, Object.class), Object.class);
        Type t1 = X.class.getDeclaredField("f1").getGenericType();
        Type r1 = X.class.getDeclaredField("r1").getGenericType();
        Type rp1 = JieReflect.replaceType(t1, String.class, Integer.class);
        assertEquals(rp1, r1);
        Type t2 = X.class.getDeclaredField("f2").getGenericType();
        Type r2 = X.class.getDeclaredField("r2").getGenericType();
        Type rp2 = JieReflect.replaceType(t2, String.class, Integer.class);
        assertEquals(rp2, r2);
        Type t3 = X.class.getDeclaredField("f3").getGenericType();
        Type r3 = X.class.getDeclaredField("r3").getGenericType();
        Type rp3 = JieReflect.replaceType(t3, String.class, Integer.class);
        assertEquals(rp3, r3);
        Type t4 = X.class.getDeclaredField("f4").getGenericType();
        Type r4 = X.class.getDeclaredField("r4").getGenericType();
        Type rp4 = JieReflect.replaceType(t4, String.class, Integer.class);
        assertEquals(rp4, r4);
        Type t5 = X.class.getDeclaredField("f5").getGenericType();
        Type r5 = X.class.getDeclaredField("r5").getGenericType();
        Type rp5 = JieReflect.replaceType(t5, String.class, Integer.class);
        assertEquals(rp5, r5);
        Type t6 = X.class.getDeclaredField("f6").getGenericType();
        Type r6 = X.class.getDeclaredField("r6").getGenericType();
        Type rp6 = JieReflect.replaceType(t6, HashMap.class, Hashtable.class);
        assertEquals(rp6, r6);

        // same:
        assertSame(JieReflect.replaceType(t1, Integer.class, String.class), t1);
        assertSame(JieReflect.replaceType(t2, Integer.class, String.class), t2);
        assertSame(JieReflect.replaceType(t3, Integer.class, String.class), t3);
        assertSame(JieReflect.replaceType(t4, Integer.class, String.class), t4);
        assertSame(JieReflect.replaceType(t5, Integer.class, String.class), t5);

        // special:
        expectThrows(ReflectionException.class, () ->
            JieReflect.replaceType(TypeTest.errorParameterizedType(), String.class, Integer.class));
        Type p1 = JieType.parameterizedType(List.class, Jie.array(), Integer.class);
        Type p2 = JieType.parameterizedType(List.class, Jie.array(), Long.class);
        assertEquals(JieReflect.replaceType(p1, Integer.class, Long.class), p2);
        assertSame(JieReflect.replaceType(p1, Integer.class, Integer.class), p1);
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
