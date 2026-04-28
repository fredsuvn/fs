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
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.ObjectException;
import space.sunqian.fs.object.builder.BuilderManager;
import space.sunqian.fs.object.builder.BuilderOperator;
import space.sunqian.fs.object.builder.ObjectBuilderException;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BuilderTest implements Asserter, TestPrint {

    @Test
    public void testOperator() throws Exception {
        testDefaultManager();
        testNewManagerWithException();
        testNewManager();
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

    private void testDefaultManager() throws Exception {
        // Test singleton
        assertSame(BuilderManager.defaultManager(), BuilderManager.defaultManager());
        assertNotEquals(
            BuilderManager.defaultManager(),
            BuilderManager.newManager(ListKit.list(BuilderManager.defaultManager().asHandler()), SimpleCache.ofSoft())
        );
        // Test default manager
        BuilderOperator builder = BuilderOperator.of(Info.class);
        testBuilderFunctionality(builder);
    }

    private void testNewManagerWithException() {
        // Test new manager with exception-throwing handler
        BuilderManager.Handler exceptionHandler = new BuilderManager.Handler() {
            @Override
            public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                throw new Exception();
            }
        };
        assertThrows(ObjectException.class, () -> {
            BuilderManager
                .newManager(ListKit.list(exceptionHandler), SimpleCache.ofSoft())
                .getOperator(Info.class);
        });
    }

    private void testNewManager() throws Exception {
        // Test new manager with null handler
        BuilderManager.Handler nullHandler = new BuilderManager.Handler() {
            @Override
            public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                return null;
            }
        };
        BuilderOperator builder = BuilderManager
            .newManager(ListKit.list(nullHandler), SimpleCache.ofSoft())
            .getOperator(Info.class);
        assertNull(builder);

        // Test cached manager
        BuilderManager manager = BuilderManager.newManager(
            ListKit.list(BuilderManager.defaultManager().asHandler()),
            SimpleCache.ofStrong()
        );

        // Test cache functionality
        assertSame(manager.getOperator(Info.class), manager.getOperator(Info.class));

        // Test manager and handler are the same instance
        assertSame(
            manager,
            manager.asHandler()
        );

        // Test handlers delegate
        assertSame(
            BuilderManager.defaultManager().asHandler(),
            manager.handlers().get(0)
        );

        // Test basic functionality
        testOperator(manager);
    }

    private void testOperator(BuilderManager manager) throws Exception {
        testCommonBuilder(manager);
        testWrongType(manager);
    }

    private void testCommonBuilder(BuilderManager manager) throws Exception {
        // Test common builder functionality
        BuilderOperator builder = manager.getOperator(Info.class);
        testBuilderFunctionality(builder);
    }

    private void testWrongType(BuilderManager manager) {
        // Test wrong type
        class X<T> {}
        BuilderOperator builder = manager.getOperator(X.class.getTypeParameters()[0]);
        assertNull(builder);

        builder = manager.getOperator(X.class);
        assertNull(builder);

        // Test error creation
        BuilderOperator errBuilder = manager.getOperator(Err.class);
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

    @Test
    public void testNewImplementation() throws Exception {
        class HandlerImpl implements BuilderManager.Handler {

            @Override
            public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                if (target.equals(Err.class)) {
                    throw new Exception();
                }
                return BuilderManager.defaultManager().asHandler().newOperator(target);
            }
        }
        List<BuilderManager.Handler> handlers = ListKit.list(new HandlerImpl());
        class Impl implements BuilderManager, BuilderManager.Handler {

            @Override
            public @Nonnull List<@Nonnull Handler> handlers() {
                return handlers;
            }

            @Override
            public @Nonnull Handler asHandler() {
                return this;
            }

            @Override
            public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
                return getOperator(target);
            }
        }
        BuilderManager nonCached = new Impl();
        assertSame(handlers, nonCached.handlers());
        assertNotSame(nonCached.getOperator(Info.class), nonCached.getOperator(Info.class));
        assertThrows(ObjectBuilderException.class, () -> nonCached.getOperator(Err.class));
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