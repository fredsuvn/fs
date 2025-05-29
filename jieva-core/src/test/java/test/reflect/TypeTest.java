package test.reflect;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.JieType;
import xyz.sunqian.common.reflect.TypeRef;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;

public class TypeTest {

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
