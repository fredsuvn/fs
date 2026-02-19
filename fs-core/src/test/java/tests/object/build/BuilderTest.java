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
import space.sunqian.fs.object.build.BuilderExecutor;
import space.sunqian.fs.object.build.BuilderProvider;
import space.sunqian.fs.object.build.ObjectBuildingException;
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
        testCreator(BuilderProvider.defaultProvider());
        testCreator(ConvertKit.builderProvider());
        {
            // test default provider
            BuilderProvider p1 = BuilderProvider.defaultProvider();
            BuilderProvider p2 = BuilderProvider.defaultProvider();
            BuilderProvider p3 = BuilderProvider.newProvider(BuilderProvider.defaultProvider().handlers());
            assertSame(p1, p2);
            assertNotSame(p1, p3);
            BuilderExecutor builder = BuilderExecutor.forType(Info.class);
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
            BuilderProvider.Handler handler = new BuilderProvider.Handler() {
                @Override
                public @Nullable BuilderExecutor newExecutor(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            BuilderExecutor builder = BuilderProvider
                .newProvider(handler)
                .forType(Info.class);
            assertNull(builder);
        }
        {
            // test new provider with exception
            BuilderProvider.Handler handler = new BuilderProvider.Handler() {
                @Override
                public @Nullable BuilderExecutor newExecutor(@Nonnull Type target) throws Exception {
                    throw new Exception();
                }
            };
            assertThrows(ObjectException.class, () -> {
                BuilderProvider
                    .newProvider(handler)
                    .forType(Info.class);
            });
        }
        {
            // test cache
            BuilderProvider provider = BuilderProvider.cachedProvider(
                SimpleCache.ofStrong(),
                BuilderProvider.defaultProvider()
            );
            assertSame(provider.forType(SimpleCache.class), provider.forType(SimpleCache.class));
            assertSame(
                BuilderProvider.defaultProvider().handlers(),
                provider.handlers()
            );
            assertSame(
                BuilderProvider.defaultProvider().asHandler(),
                provider.asHandler()
            );
            testCreator(provider);
        }
    }

    private void testCreator(BuilderProvider provider) throws Exception {
        {
            // test common
            BuilderExecutor builder = provider.forType(Info.class);
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
            BuilderProvider.Handler handler1 = new BuilderProvider.Handler() {
                @Override
                public @Nullable BuilderExecutor newExecutor(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            BuilderExecutor builder = provider
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
            BuilderProvider.Handler handler1 = new BuilderProvider.Handler() {
                @Override
                public @Nullable BuilderExecutor newExecutor(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            BuilderExecutor builder = BuilderProvider
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
            BuilderExecutor builder = provider.forType(X.class.getTypeParameters()[0]);
            assertNull(builder);
            builder = provider.forType(X.class);
            assertNull(builder);
            BuilderExecutor errBuilder = provider.forType(Err.class);
            assertNotNull(errBuilder);
            assertThrows(ObjectException.class, errBuilder::createBuilder);
        }
    }

    @Test
    public void testException() {
        {
            // ObjectCreateException
            assertThrows(ObjectBuildingException.class, () -> {
                throw new ObjectBuildingException();
            });
            assertThrows(ObjectBuildingException.class, () -> {
                throw new ObjectBuildingException("");
            });
            assertThrows(ObjectBuildingException.class, () -> {
                throw new ObjectBuildingException("", new RuntimeException());
            });
            assertThrows(ObjectBuildingException.class, () -> {
                throw new ObjectBuildingException(new RuntimeException());
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
