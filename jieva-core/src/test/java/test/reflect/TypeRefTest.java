package test.reflect;

import org.testng.annotations.Test;
import xyz.sunqian.common.reflect.ReflectionException;
import xyz.sunqian.common.reflect.TypeRef;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static xyz.sunqian.test.JieTest.reflectThrows;

public class TypeRefTest {

    @Test
    public void testTypeRef() throws Exception {
        class X {
            private List<String> list;
        }
        Type listType = X.class.getDeclaredField("list").getGenericType();
        ParameterizedType parameterizedType = new TypeRef<List<String>>() {
        }.asParameterized();
        assertEquals(parameterizedType, listType);
        ;
        assertEquals(new TypeRef<String>() {}.type(), String.class);

        // inheritance:
        class TestRef extends TypeRef<String> {
        }
        TestRef testRef = new TestRef();
        assertEquals(testRef.type(), String.class);
        class TestRef2 extends TestRef {
        }
        TestRef2 testRef2 = new TestRef2();
        assertEquals(testRef2.type(), String.class);
        class TestRef3<T> extends TypeRef<T> {
        }
        assertEquals(new TestRef3<String>() {
        }.type(), String.class);

        // unreachable point:
        Method get0 = TypeRef.class.getDeclaredMethod("get0", List.class);
        reflectThrows(ReflectionException.class, get0, new TypeRef<Object>() {
        }, Collections.emptyList());
    }
}
