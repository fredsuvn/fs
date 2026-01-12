package tests.object;

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
import space.sunqian.fs.cache.CacheFunction;
import space.sunqian.fs.object.ObjectCreator;
import space.sunqian.fs.object.ObjectCreatorProvider;
import space.sunqian.fs.object.ObjectException;

import java.lang.reflect.Type;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObjectCreatorTest implements AssertTest, PrintTest {

    @Test
    public void testCreator() throws Exception {
        {
            ObjectCreatorProvider p1 = ObjectCreatorProvider.defaultProvider();
            ObjectCreatorProvider p2 = ObjectCreatorProvider.defaultProvider();
            ObjectCreatorProvider p3 = ObjectCreatorProvider.newProvider(ObjectCreatorProvider.defaultHandler());
            assertSame(p1, p2);
            assertNotSame(p1, p3);
        }
        {
            ObjectCreator creator = ObjectCreatorProvider.defaultProvider().creatorForType(Info.class);
            assertNotNull(creator);
            Info builder = Fs.as(creator.createBuilder());
            builder.setIntValue(6);
            builder.setStringValue("str666");
            assertEquals(
                new Info(6, "str666"),
                creator.createTarget(builder)
            );
            assertEquals(Info.class, creator.targetType());
            assertEquals(Info.class, creator.builderType());
        }
        {
            ObjectCreatorProvider.Handler handler1 = new ObjectCreatorProvider.Handler() {
                @Override
                public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            ObjectCreator creator = ObjectCreatorProvider
                .newProvider(CacheFunction.ofMap(new HashMap<>()), handler1, ObjectCreatorProvider.defaultHandler())
                .creatorForType(Info.class);
            assertNotNull(creator);
            Info builder = Fs.as(creator.createBuilder());
            builder.setIntValue(6);
            builder.setStringValue("str666");
            assertEquals(
                new Info(6, "str666"),
                creator.createTarget(builder)
            );
        }
        {
            ObjectCreatorProvider.Handler handler1 = new ObjectCreatorProvider.Handler() {
                @Override
                public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            ObjectCreator creator = ObjectCreatorProvider
                .newProvider(handler1, ObjectCreatorProvider.defaultProvider().asHandler())
                .creatorForType(Info.class);
            assertNotNull(creator);
            Info builder = Fs.as(creator.createBuilder());
            builder.setIntValue(6);
            builder.setStringValue("str666");
            assertEquals(
                new Info(6, "str666"),
                creator.createTarget(builder)
            );
        }
        {
            ObjectCreatorProvider.Handler handler1 = new ObjectCreatorProvider.Handler() {
                @Override
                public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
                    return null;
                }
            };
            ObjectCreator creator = ObjectCreatorProvider
                .newProvider(handler1)
                .creatorForType(Info.class);
            assertNull(creator);
        }
        {
            ObjectCreatorProvider.Handler handler1 = new ObjectCreatorProvider.Handler() {
                @Override
                public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
                    throw new Exception();
                }
            };
            assertThrows(ObjectException.class, () -> {
                ObjectCreatorProvider
                    .newProvider(handler1)
                    .creatorForType(Info.class);
            });
        }
        {
            class X<T> {}
            ObjectCreator creator = ObjectCreatorProvider
                .defaultProvider()
                .creatorForType(X.class.getTypeParameters()[0]);
            assertNull(creator);
            creator = ObjectCreatorProvider
                .defaultProvider()
                .creatorForType(X.class);
            assertNull(creator);
            ObjectCreator errCreator = ObjectCreatorProvider
                .defaultProvider()
                .creatorForType(Err.class);
            assertNotNull(errCreator);
            assertThrows(ObjectException.class, errCreator::createBuilder);
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
