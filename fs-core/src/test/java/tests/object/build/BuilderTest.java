package tests.object.build;

import internal.test.AssertTest;
import internal.test.PrintTest;
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
import space.sunqian.fs.object.convert.ConvertKit;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BuilderTest implements AssertTest, PrintTest {

    @Test
    public void testCreator() throws Exception {
        testCreator(BuilderOperatorProvider.defaultProvider());
        testCreator(ConvertKit.builderProvider());
        {
            // test default provider
            BuilderOperatorProvider p1 = BuilderOperatorProvider.defaultProvider();
            BuilderOperatorProvider p2 = BuilderOperatorProvider.defaultProvider();
            BuilderOperatorProvider p3 = BuilderOperatorProvider.newProvider(BuilderOperatorProvider.defaultProvider().handlers());
            assertSame(p1, p2);
            assertNotSame(p1, p3);
            BuilderOperator builder = BuilderOperator.of(Info.class);
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
        {
            // test new provider
            BuilderOperatorProvider.Handler handler = new BuilderOperatorProvider.Handler() {
                @Override
                public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            BuilderOperator builder = BuilderOperatorProvider
                .newProvider(handler)
                .forType(Info.class);
            assertNull(builder);
        }
        {
            // test new provider with exception
            BuilderOperatorProvider.Handler handler = new BuilderOperatorProvider.Handler() {
                @Override
                public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                    throw new Exception();
                }
            };
            assertThrows(ObjectException.class, () -> {
                BuilderOperatorProvider
                    .newProvider(handler)
                    .forType(Info.class);
            });
        }
        {
            // test cache
            BuilderOperatorProvider provider = BuilderOperatorProvider.newCachedProvider(
                SimpleCache.ofStrong(),
                BuilderOperatorProvider.defaultProvider()
            );
            assertSame(provider.forType(SimpleCache.class), provider.forType(SimpleCache.class));
            assertSame(
                BuilderOperatorProvider.defaultProvider().handlers(),
                provider.handlers()
            );
            assertSame(
                BuilderOperatorProvider.defaultProvider().asHandler(),
                provider.asHandler()
            );
            testCreator(provider);
        }
    }

    private void testCreator(BuilderOperatorProvider provider) throws Exception {
        {
            // test common
            BuilderOperator builder = provider.forType(Info.class);
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
        {
            // test with first handler
            BuilderOperatorProvider.Handler handler1 = new BuilderOperatorProvider.Handler() {
                @Override
                public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            BuilderOperator builder = provider
                .withFirstHandler(handler1)
                .forType(Info.class);
            assertNotNull(builder);
            Info info = Fs.as(builder.createBuilder());
            info.setIntValue(6);
            info.setStringValue("str666");
            assertEquals(
                new Info(6, "str666"),
                builder.buildTarget(info)
            );
        }
        {
            // test new provider with first handler
            BuilderOperatorProvider.Handler handler1 = new BuilderOperatorProvider.Handler() {
                @Override
                public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            BuilderOperator builder = BuilderOperatorProvider
                .newProvider(handler1, provider.asHandler())
                .forType(Info.class);
            assertNotNull(builder);
            Info info = Fs.as(builder.createBuilder());
            info.setIntValue(6);
            info.setStringValue("str666");
            assertEquals(
                new Info(6, "str666"),
                builder.buildTarget(info)
            );
        }
        {
            // test wrong type
            class X<T> {}
            BuilderOperator builder = provider.forType(X.class.getTypeParameters()[0]);
            assertNull(builder);
            builder = provider.forType(X.class);
            assertNull(builder);
            BuilderOperator errBuilder = provider.forType(Err.class);
            assertNotNull(errBuilder);
            assertThrows(ObjectException.class, errBuilder::createBuilder);
        }
    }

    @Test
    public void testException() {
        {
            // ObjectCreateException
            assertThrows(ObjectBuilderException.class, () -> {
                throw new ObjectBuilderException();
            });
            assertThrows(ObjectBuilderException.class, () -> {
                throw new ObjectBuilderException("");
            });
            assertThrows(ObjectBuilderException.class, () -> {
                throw new ObjectBuilderException("", new RuntimeException());
            });
            assertThrows(ObjectBuilderException.class, () -> {
                throw new ObjectBuilderException(new RuntimeException());
            });
        }
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
