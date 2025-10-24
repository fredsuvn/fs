package tests.object.convert;

import org.testng.annotations.Test;
import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.exception.UnreachablePointException;
import space.sunqian.common.object.data.DataObjectException;
import space.sunqian.common.object.data.ObjectBuilder;
import space.sunqian.common.object.data.ObjectBuilderProvider;
import space.sunqian.common.runtime.reflect.TypeKit;
import space.sunqian.common.runtime.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class ObjectBuilderTest {

    @Test
    public void testObjectBuilder() {
        ObjectBuilder ob0 = ObjectBuilder.get(String.class);
        Object str = ob0.newBuilder();
        assertNotNull(str);
        assertEquals(str, "");
        assertEquals(ob0.builderType(), String.class);
        class X {}
        Map<Type, ObjectBuilder> cache = new HashMap<>();
        ObjectBuilderProvider provider = ObjectBuilderProvider.newProvider(
            ObjectBuilderProvider.newBuilderCache(cache),
            target -> {
                if (X.class.equals(target)) {
                    return new ObjectBuilder() {
                        @Override
                        public @Nonnull Object newBuilder() throws DataObjectException {
                            return new X();
                        }

                        @Override
                        public @Nonnull Type builderType() {
                            return X.class;
                        }

                        @Override
                        public @Nonnull Object build(@Nonnull Object builder) throws DataObjectException {
                            return builder;
                        }
                    };
                }
                return null;
            }
        );
        ObjectBuilder ob = provider.builder(X.class);
        assertNotNull(ob);
        Object x1 = ob.newBuilder();
        assertNotNull(x1);
        assertSame(x1, ob.build(x1));
        assertEquals(ob.builderType(), X.class);
        assertNull(provider.builder(String.class));
        {
            // withLastHandler
            cache.clear();
            ObjectBuilderProvider provider2 = provider.withLastHandler(ObjectBuilderProvider.defaultProvider().asHandler());
            ObjectBuilder ob2 = provider2.builder(X.class);
            assertNotNull(ob2);
            Object x2 = ob2.newBuilder();
            assertNotNull(x2);
            assertSame(x2, ob2.build(x2));
            assertEquals(ob2.builderType(), X.class);
            ObjectBuilder ob3 = provider2.builder(String.class);
            assertNotNull(ob3);
            Object x3 = ob3.newBuilder();
            assertNotNull(x3);
            assertEquals(ob3.build(x3), "");
            assertEquals(ob3.builderType(), String.class);
        }
        {
            // withFirstHandler
            cache.clear();
            ObjectBuilderProvider provider2 = provider.withFirstHandler(ObjectBuilderProvider.defaultProvider().asHandler());
            ObjectBuilder ob2 = provider2.builder(X.class);
            assertNotNull(ob2);
            Object x2 = ob2.newBuilder();
            assertNotNull(x2);
            assertSame(x2, ob2.build(x2));
            assertEquals(ob2.builderType(), X.class);
            ObjectBuilder ob3 = provider2.builder(String.class);
            assertNotNull(ob3);
            Object x3 = ob3.newBuilder();
            assertNotNull(x3);
            assertEquals(ob3.build(x3), "");
            assertEquals(ob3.builderType(), String.class);
        }
        {
            // error
            cache.clear();
            ObjectBuilderProvider err = provider.withFirstHandler(target -> {
                throw new UnreachablePointException();
            });
            expectThrows(DataObjectException.class, () -> err.builder(X.class));
            ObjectBuilderProvider err2 = ObjectBuilderProvider.newProvider(
                ObjectBuilderProvider.defaultProvider().asHandler());
            expectThrows(DataObjectException.class, () -> err2.builder(E.class).newBuilder());
        }
        {
            // generic
            ObjectBuilder tb = ObjectBuilder.get(new TypeRef<T<String>>() {}.type());
            assertNotNull(tb);
            Object t = tb.newBuilder();
            assertNotNull(t);
            assertTrue(t instanceof T);
            assertEquals(tb.builderType(), new TypeRef<T<String>>() {}.type());
            assertNull(ObjectBuilder.get(TypeKit.otherType()));
        }
    }

    public static class E {
        public E() {
            throw new UnreachablePointException();
        }
    }

    public static class T<T> {}
}
