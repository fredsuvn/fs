package tests.core.object.meta;

import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.meta.DataMetaException;
import space.sunqian.fs.object.meta.MapMeta;
import space.sunqian.fs.object.meta.MapMetaIntrospector;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapMetaTest implements TestPrint {

    @Test
    public void testMapMetaBasicProperties() throws Exception {
        MapMeta meta = MapMeta.of(HelloMap.class);
        verifyMapMetaBasic(meta, HelloMap.class, String.class, Long.class);
        printFor("MapMeta toString", meta);
        assertEquals(
            meta.toString(),
            meta.type().getTypeName()
        );
        assertThrows(DataMetaException.class, () -> MapMeta.of(String.class));
    }

    @Test
    public void testMapMetaEqualHashCode() throws Exception {
        MapMeta m1 = MapMeta.of(Map.class);
        MapMeta m2 = MapMeta.of(Map.class);
        assertEquals(m1, m2);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(""));
        MapMeta m3 = MapMeta.of(new TypeRef<Map<String, Integer>>() {}.type());
        assertNotEquals(m1, m3);
        assertNotEquals(m3, m1);

        MapMetaIntrospector introspector2 = new CustomMapMetaIntrospector();
        MapMeta m4 = introspector2.introspect(Map.class);
        assertNotEquals(m1, m4);
        assertNotEquals(m4, m1);

        int result = 1;
        result = 31 * result + m1.type().hashCode();
        result = 31 * result + m1.introspector().hashCode();
        assertEquals(m1.hashCode(), result);

        {
            // Test different introspector
            MapMetaIntrospector softIntrospector = MapMetaIntrospector.newIntrospector(
                SimpleCache.ofSoft(),
                MapMetaIntrospector.defaultIntrospector().asHandler()
            );
            MapMeta ms = softIntrospector.introspect(Map.class);
            assertNotEquals(m1, ms);
            MapMetaIntrospector phantomIntrospector = MapMetaIntrospector.newIntrospector(
                SimpleCache.ofPhantom(),
                MapMetaIntrospector.defaultIntrospector().asHandler()
            );
            MapMeta mp1 = phantomIntrospector.introspect(Map.class);
            MapMeta mp2 = phantomIntrospector.introspect(Map.class);
            assertEquals(mp1, mp2);
        }
    }

    @Test
    public void testMapMetaIntrospector() throws Exception {
        assertSame(MapMetaIntrospector.defaultIntrospector(), MapMetaIntrospector.defaultIntrospector());
        testMapMetaIntrospectorWithIntrospector(MapMetaIntrospector.defaultIntrospector());
        ;
    }

    private void testMapMetaIntrospectorWithIntrospector(MapMetaIntrospector introspector) throws Exception {
        MapMeta meta = introspector.introspect(HelloMap.class);
        verifyMapMetaWithIntrospector(meta, HelloMap.class, String.class, Long.class, introspector);
        printFor("MapMeta toString", meta);
        assertEquals(
            meta.toString(),
            meta.type().getTypeName()
        );
        assertThrows(DataMetaException.class, () -> introspector.introspect(String.class));
        testMapMetaEqualityWithIntrospector(introspector);
    }

    private void testMapMetaEqualityWithIntrospector(MapMetaIntrospector introspector) throws Exception {
        MapMeta m1 = introspector.introspect(Map.class);
        MapMeta m2 = introspector.introspect(Map.class);
        assertEquals(m1, m2);
        assertTrue(m1.equals(m1));
        assertFalse(m1.equals(""));
        MapMeta m3 = introspector.introspect(new TypeRef<Map<String, Integer>>() {}.type());
        assertNotEquals(m1, m3);
        assertNotEquals(m3, m1);

        MapMetaIntrospector introspector2 = new CustomMapMetaIntrospector();
        MapMeta m4 = introspector2.introspect(Map.class);
        assertNotEquals(m1, m4);
        assertNotEquals(m4, m1);

        int result = 1;
        result = 31 * result + m1.type().hashCode();
        result = 31 * result + m1.introspector().hashCode();
        assertEquals(m1.hashCode(), result);
    }

    @Test
    public void testMapMetaCachedIntrospector() {
        MapMetaIntrospector mapIntrospector = MapMetaIntrospector.newIntrospector(SimpleCache.ofStrong(), MapMetaIntrospector.defaultIntrospector().asHandler());
        assertSame(mapIntrospector.introspect(Map.class), mapIntrospector.introspect(Map.class));
    }

    private void verifyMapMetaBasic(MapMeta meta, Class<?> expectedType, Class<?> expectedKeyType, Class<?> expectedValueType) {
        assertSame(meta.introspector(), MapMetaIntrospector.defaultIntrospector());
        assertEquals(expectedType, meta.type());
        assertEquals(expectedType, meta.rawType());
        assertTrue(meta.isMapMeta());
        assertFalse(meta.isObjectMeta());
        assertSame(meta.asMapMeta(), meta);
        assertThrows(ClassCastException.class, meta::asObjectMeta);
        assertEquals(expectedKeyType, meta.keyType());
        assertEquals(expectedValueType, meta.valueType());
    }

    private void verifyMapMetaWithIntrospector(MapMeta meta, Class<?> expectedType, Class<?> expectedKeyType, Class<?> expectedValueType, MapMetaIntrospector expectedIntrospector) {
        assertEquals(expectedType, meta.type());
        assertEquals(expectedType, meta.rawType());
        assertTrue(meta.isMapMeta());
        assertFalse(meta.isObjectMeta());
        assertSame(meta.asMapMeta(), meta);
        assertThrows(ClassCastException.class, meta::asObjectMeta);
        assertEquals(expectedKeyType, meta.keyType());
        assertEquals(expectedValueType, meta.valueType());
    }

    @Test
    public void testMapMetaIntrospectorWithHandler() throws Exception {
        {
            // Test handlers
            List<MapMetaIntrospector.Handler> handlers =
                ListKit.list(MapMetaIntrospector.defaultIntrospector().asHandler());
            MapMetaIntrospector introspector = MapMetaIntrospector.newIntrospector(SimpleCache.ofStrong(), handlers);
            assertSame(handlers, introspector.handlers());
        }
        {
            // Test throw exception
            MapMetaIntrospector.Handler errHandler = (type, introspector) -> {
                throw new RuntimeException("errHandler");
            };
            MapMetaIntrospector errIntrospector = MapMetaIntrospector.newIntrospector(SimpleCache.ofStrong(), errHandler);
            DataMetaException de = assertThrows(DataMetaException.class, () -> errIntrospector.introspect(Map.class));
            assertEquals("errHandler", de.getCause().getMessage());
        }
        {
            // Test return null
            MapMetaIntrospector.Handler nullHandler = (type, introspector) -> null;
            MapMetaIntrospector nullIntrospector = MapMetaIntrospector.newIntrospector(SimpleCache.ofStrong(), nullHandler);
            assertThrows(DataMetaException.class, () -> nullIntrospector.introspect(Map.class));
        }
    }

    private static class CustomMapMetaIntrospector implements MapMetaIntrospector {
        @Override
        public @Nonnull MapMeta introspect(@Nonnull Type type) throws DataMetaException {
            return new MapMeta() {
                @Override
                public @Nonnull MapMetaIntrospector introspector() {
                    return CustomMapMetaIntrospector.this;
                }

                @Override
                public @Nonnull Type keyType() {
                    return Object.class;
                }

                @Override
                public @Nonnull Type valueType() {
                    return Object.class;
                }

                @Override
                public @Nonnull Type type() {
                    return type;
                }
            };
        }

        @Override
        public @Nonnull List<@Nonnull Handler> handlers() {
            return Collections.emptyList();
        }

        @Override
        public @Nonnull Handler asHandler() {
            return null;
        }
    }

    public static class HelloMap extends AbstractMap<String, Long> {
        @Override
        public Set<Entry<String, Long>> entrySet() {
            return null;
        }
    }
}