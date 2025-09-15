package tests.object.convert;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.UnreachablePointException;
import xyz.sunqian.common.object.convert.DataBuilderFactory;
import xyz.sunqian.common.object.convert.ObjectConvertException;
import xyz.sunqian.common.runtime.invoke.Invocable;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class DataBuilderFactoryTest {

    @Test
    public void testDataBuilderFactory() {
        class X {}
        Map<Class<?>, Invocable> cache = new HashMap<>();
        DataBuilderFactory factory = DataBuilderFactory.newFactory(
            DataBuilderFactory.newConstructorCache(cache),
            new DataBuilderFactory.Handler() {
                @Override
                public @Nullable Invocable newConstructor(@Nonnull Class<?> target) throws Exception {
                    if (X.class.equals(target)) {
                        return (inst, args) -> new X();
                    }
                    return null;
                }

                @Override
                public @Nullable Object build(@Nonnull Object builder) throws Exception {
                    if (builder instanceof X) {
                        return builder;
                    }
                    return null;
                }
            }
        );
        Object x1 = factory.newBuilder(X.class);
        assertNotNull(x1);
        assertSame(x1, factory.build(x1));
        assertNull(factory.newBuilder(String.class));
        expectThrows(ObjectConvertException.class, () -> factory.build(new Object()));
        {
            // withLastHandler
            cache.clear();
            DataBuilderFactory factory2 = factory.withLastHandler(DataBuilderFactory.defaultFactory().asHandler());
            Object x2 = factory2.newBuilder(X.class);
            assertNotNull(x2);
            assertSame(x2, factory2.build(x2));
            Object s2 = factory2.newBuilder(String.class);
            assertNotNull(s2);
            assertSame(s2, factory2.build(s2));
        }
        {
            // withFirstHandler
            cache.clear();
            DataBuilderFactory factory2 = factory.withFirstHandler(DataBuilderFactory.defaultFactory().asHandler());
            Object x2 = factory2.newBuilder(X.class);
            assertNotNull(x2);
            assertSame(x2, factory2.build(x2));
            Object s2 = factory2.newBuilder(String.class);
            assertNotNull(s2);
            assertSame(s2, factory2.build(s2));
        }
        {
            // error
            cache.clear();
            DataBuilderFactory factory2 = factory.withFirstHandler(new DataBuilderFactory.Handler() {

                @Override
                public @Nullable Invocable newConstructor(@Nonnull Class<?> target) throws Exception {
                    throw new UnreachablePointException();
                }

                @Override
                public @Nullable Object build(@Nonnull Object builder) throws Exception {
                    throw new UnreachablePointException();
                }
            });
            expectThrows(ObjectConvertException.class, () -> factory2.newBuilder(X.class));
            expectThrows(ObjectConvertException.class, () -> factory2.build(new X()));
        }
    }
}
