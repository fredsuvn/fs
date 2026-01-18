package tests.object.create;

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
import space.sunqian.fs.object.create.CreatorProvider;
import space.sunqian.fs.object.create.ObjectCreateException;
import space.sunqian.fs.object.create.ObjectCreator;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateTest implements AssertTest, PrintTest {

    @Test
    public void testCreator() throws Exception {
        {
            // test default provider
            CreatorProvider p1 = CreatorProvider.defaultProvider();
            CreatorProvider p2 = CreatorProvider.defaultProvider();
            CreatorProvider p3 = CreatorProvider.newProvider(CreatorProvider.defaultProvider().handlers());
            assertSame(p1, p2);
            assertNotSame(p1, p3);
        }
        {
            // test common
            ObjectCreator creator = ObjectCreator.forType(Info.class);
            assertNotNull(creator);
            Info builder = Fs.as(creator.createBuilder());
            builder.setIntValue(6);
            builder.setStringValue("str666");
            assertEquals(
                new Info(6, "str666"),
                creator.buildTarget(builder)
            );
            assertEquals(Info.class, creator.targetType());
            assertEquals(Info.class, creator.builderType());
        }
        {
            // test with first handler
            CreatorProvider.Handler handler1 = new CreatorProvider.Handler() {
                @Override
                public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            ObjectCreator creator = CreatorProvider.defaultProvider()
                .withFirstHandler(handler1)
                .forType(Info.class);
            assertNotNull(creator);
            Info builder = Fs.as(creator.createBuilder());
            builder.setIntValue(6);
            builder.setStringValue("str666");
            assertEquals(
                new Info(6, "str666"),
                creator.buildTarget(builder)
            );
        }
        {
            // test with first handler and default provider
            CreatorProvider.Handler handler1 = new CreatorProvider.Handler() {
                @Override
                public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            ObjectCreator creator = CreatorProvider
                .newProvider(handler1, CreatorProvider.defaultProvider().asHandler())
                .forType(Info.class);
            assertNotNull(creator);
            Info builder = Fs.as(creator.createBuilder());
            builder.setIntValue(6);
            builder.setStringValue("str666");
            assertEquals(
                new Info(6, "str666"),
                creator.buildTarget(builder)
            );
        }
        {
            // test new provider
            CreatorProvider.Handler handler1 = new CreatorProvider.Handler() {
                @Override
                public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            ObjectCreator creator = CreatorProvider
                .newProvider(handler1)
                .forType(Info.class);
            assertNull(creator);
        }
        {
            // test new provider with exception
            CreatorProvider.Handler handler1 = new CreatorProvider.Handler() {
                @Override
                public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
                    throw new Exception();
                }
            };
            assertThrows(ObjectException.class, () -> {
                CreatorProvider
                    .newProvider(handler1)
                    .forType(Info.class);
            });
        }
        {
            // test wrong type
            class X<T> {}
            ObjectCreator creator = CreatorProvider
                .defaultProvider()
                .forType(X.class.getTypeParameters()[0]);
            assertNull(creator);
            creator = CreatorProvider
                .defaultProvider()
                .forType(X.class);
            assertNull(creator);
            ObjectCreator errCreator = CreatorProvider
                .defaultProvider()
                .forType(Err.class);
            assertNotNull(errCreator);
            assertThrows(ObjectException.class, errCreator::createBuilder);
        }
        {
            // test cache
            CreatorProvider provider = CreatorProvider.cachedProvider(
                SimpleCache.ofStrong(),
                CreatorProvider.defaultProvider()
            );
            assertSame(provider.forType(SimpleCache.class), provider.forType(SimpleCache.class));
            assertSame(
                CreatorProvider.defaultProvider().handlers(),
                provider.handlers()
            );
            assertSame(
                CreatorProvider.defaultProvider().asHandler(),
                provider.asHandler()
            );
        }
    }

    @Test
    public void testException() {
        {
            // ObjectCreateException
            assertThrows(ObjectCreateException.class, () -> {
                throw new ObjectCreateException();
            });
            assertThrows(ObjectCreateException.class, () -> {
                throw new ObjectCreateException("");
            });
            assertThrows(ObjectCreateException.class, () -> {
                throw new ObjectCreateException("", new RuntimeException());
            });
            assertThrows(ObjectCreateException.class, () -> {
                throw new ObjectCreateException(new RuntimeException());
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
