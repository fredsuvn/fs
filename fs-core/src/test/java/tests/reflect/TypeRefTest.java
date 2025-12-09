package tests.reflect;

import org.junit.jupiter.api.Test;
import space.sunqian.common.reflect.TypeRef;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeRefTest {

    @Test
    public void testTypeRef() throws Exception {
        class X {
            List<String> list;
        }
        Type listType = X.class.getDeclaredField("list").getGenericType();
        ParameterizedType parameterizedType = new TypeRef<List<String>>() {
        }.asParameterized();
        assertEquals(parameterizedType, listType);
        ;
        assertEquals(String.class, new TypeRef<String>() {
        }.type());

        // inheritance:
        class TestRef extends TypeRef<String> {
        }
        TestRef testRef = new TestRef();
        assertEquals(String.class, testRef.type());
        class TestRef2 extends TestRef {
        }
        TestRef2 testRef2 = new TestRef2();
        assertEquals(String.class, testRef2.type());
        class TestRef3<T> extends TypeRef<T> {
        }
        assertEquals(String.class, new TestRef3<String>() {
        }.type());
    }
}
