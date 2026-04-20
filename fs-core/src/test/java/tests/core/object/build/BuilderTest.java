package tests.core.object.build;

import internal.utils.Asserter;
import internal.utils.TestPrint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.object.ObjectException;
import space.sunqian.fs.object.builder.BuilderOperator;
import space.sunqian.fs.object.builder.BuilderOperatorProvider;
import space.sunqian.fs.object.builder.ObjectBuilderException;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BuilderTest implements Asserter, TestPrint {

    @Test
    public void testCreator() throws Exception {
        testProviderSingletons();
        testDefaultProvider();
        testNewProvider();
        testNewProviderWithException();
        testCachedProvider();
    }

    @Test
    public void testException() {
        assertThrows(ObjectBuilderException.class, () -> {throw new ObjectBuilderException();});
        assertThrows(ObjectBuilderException.class, () -> {throw new ObjectBuilderException("");});
        assertThrows(ObjectBuilderException.class, () -> {
            throw new ObjectBuilderException("", new RuntimeException());
        });
        assertThrows(ObjectBuilderException.class, () -> {throw new ObjectBuilderException(new RuntimeException());});
    }

    private void testProviderSingletons() {
        // Test provider singletons
        assertSame(BuilderOperatorProvider.defaultProvider(), BuilderOperatorProvider.defaultProvider());
        assertSame(BuilderOperatorProvider.defaultCachedProvider(), BuilderOperatorProvider.defaultCachedProvider());
        assertNotSame(BuilderOperatorProvider.defaultProvider(), BuilderOperatorProvider.defaultCachedProvider());

        // Test different provider instances
        BuilderOperatorProvider p1 = BuilderOperatorProvider.defaultProvider();
        BuilderOperatorProvider p2 = BuilderOperatorProvider.defaultProvider();
        BuilderOperatorProvider p3 = BuilderOperatorProvider.newProvider(BuilderOperatorProvider.defaultProvider().handlers());
        assertSame(p1, p2);
        assertNotSame(p1, p3);
    }

    private void testDefaultProvider() throws Exception {
        // Test default provider
        BuilderOperator builder = BuilderOperator.of(Info.class);
        testBuilderFunctionality(builder);
    }

    private void testNewProvider() throws Exception {
        // Test new provider with null handler
        BuilderOperatorProvider.Handler nullHandler = new BuilderOperatorProvider.Handler() {
            @Override
            public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                return null;
            }
        };
        BuilderOperator builder = BuilderOperatorProvider
            .newProvider(nullHandler)
            .forType(Info.class);
        assertNull(builder);
    }

    private void testNewProviderWithException() {
        // Test new provider with exception-throwing handler
        BuilderOperatorProvider.Handler exceptionHandler = new BuilderOperatorProvider.Handler() {
            @Override
            public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                throw new Exception();
            }
        };
        assertThrows(ObjectException.class, () -> {
            BuilderOperatorProvider
                .newProvider(exceptionHandler)
                .forType(Info.class);
        });
    }

    private void testCachedProvider() throws Exception {
        // Test cached provider
        BuilderOperatorProvider provider = BuilderOperatorProvider.newCachedProvider(
            SimpleCache.ofStrong(),
            BuilderOperatorProvider.defaultProvider()
        );

        // Test cache functionality
        assertSame(provider.forType(SimpleCache.class), provider.forType(SimpleCache.class));

        // Test handler delegation
        assertSame(
            BuilderOperatorProvider.defaultProvider().handlers(),
            provider.handlers()
        );
        assertSame(
            BuilderOperatorProvider.defaultProvider().asHandler(),
            provider.asHandler()
        );

        // Test basic functionality
        testCreator(provider);
    }

    private void testCreator(BuilderOperatorProvider provider) throws Exception {
        testCommonBuilder(provider);
        testWithFirstHandler(provider);
        testNewProviderWithFirstHandler(provider);
        testWrongType(provider);
    }

    private void testCommonBuilder(BuilderOperatorProvider provider) throws Exception {
        // Test common builder functionality
        BuilderOperator builder = provider.forType(Info.class);
        testBuilderFunctionality(builder);
    }

    private void testWithFirstHandler(BuilderOperatorProvider provider) throws Exception {
        // Test with first handler
        BuilderOperatorProvider.Handler nullHandler = new BuilderOperatorProvider.Handler() {
            @Override
            public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                return null;
            }
        };
        BuilderOperator builder = provider
            .withFirstHandler(nullHandler)
            .forType(Info.class);
        testBuilderFunctionality(builder);
    }

    private void testNewProviderWithFirstHandler(BuilderOperatorProvider provider) throws Exception {
        // Test new provider with first handler
        BuilderOperatorProvider.Handler nullHandler = new BuilderOperatorProvider.Handler() {
            @Override
            public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                return null;
            }
        };
        BuilderOperator builder = BuilderOperatorProvider
            .newProvider(nullHandler, provider.asHandler())
            .forType(Info.class);
        testBuilderFunctionality(builder);
    }

    private void testWrongType(BuilderOperatorProvider provider) {
        // Test wrong type
        class X<T> {}
        BuilderOperator builder = provider.forType(X.class.getTypeParameters()[0]);
        assertNull(builder);

        builder = provider.forType(X.class);
        assertNull(builder);

        // Test error creation
        BuilderOperator errBuilder = provider.forType(Err.class);
        assertNotNull(errBuilder);
        assertThrows(ObjectException.class, errBuilder::createBuilder);
    }

    private void testBuilderFunctionality(BuilderOperator builder) throws Exception {
        // Test builder functionality
        assertNotNull(builder);
        Info info = Fs.as(builder.createBuilder());
        info.setIntValue(6);
        info.setStringValue("str666");
        assertEquals(
            new Info(6, "str666"),
            builder.buildTarget(info)
        );
        assertEquals(Info.class, builder.targetType());
        assertEquals(Info.class, builder.builderType());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Info {
        private int intValue;
        private String stringValue;
    }

    public static class Err {
        public Err() {
            throw new RuntimeException();
        }
    }
}