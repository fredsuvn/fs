package tests.data;

import internal.test.ErrorMap;
import internal.test.Mocker;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.data.DataException;
import space.sunqian.fs.data.DataMap;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataMapTest {

    @Test
    public void testDataMap() throws Exception {
        DataMap dataMap = DataMap.newMap();
        dataMap.put("1", 1111);
        assertEquals(
            DataMap.wrap(MapKit.map("1", 1111)),
            dataMap
        );
        assertTrue(dataMap.contentEquals(DataMap.wrap(MapKit.map("1", 1111))));
        assertFalse(dataMap.equals(MapKit.map("1", 1111)));
        assertEquals(
            dataMap.toString(),
            MapKit.linkedHashMap("1", 1111).toString()
        );
        // conversions
        assertEquals(1111, dataMap.get("1"));
        assertEquals(2222, dataMap.get("2", 2222));
        assertNull(dataMap.get("2", null));
        assertEquals("1111", dataMap.getString("1"));
        assertEquals("2222", dataMap.getString("2", "2222"));
        assertEquals(1111, dataMap.getInt("1"));
        assertEquals(2222, dataMap.getInt("2", 2222));
        assertEquals(1111L, dataMap.getLong("1"));
        assertEquals(2222L, dataMap.getLong("2", 2222L));
        assertEquals(1111.0F, dataMap.getFloat("1"));
        assertEquals(2222.0F, dataMap.getFloat("2", 2222.0F));
        assertEquals(1111.0, dataMap.getDouble("1"));
        assertEquals(2222.0, dataMap.getDouble("2", 2222.0));
        assertEquals(new BigDecimal("1111"), dataMap.getBigDecimal("1"));
        assertEquals(new BigDecimal("1222"), dataMap.getBigDecimal("2", new BigDecimal("1222")));
        assertThrows(DataException.class, () -> {
            DataMap.wrap(new ErrorMap<>()).getBigDecimal("2");
        });
    }

    @Test
    public void testWrapper() throws Exception {
        DataMap dataMap = DataMap.newMap();
        Type[] mapTypes = Map.class.getTypeParameters();
        for (Method method : DataMap.class.getMethods()) {
            if (method.getDeclaringClass().equals(DataMap.class)) {
                continue;
            }
            Type[] parameters = method.getGenericParameterTypes();
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Class<?> cls;
                if (parameters[i].equals(mapTypes[0])) {
                    cls = String.class;
                } else if (parameters[i].equals(mapTypes[1])) {
                    cls = Object.class;
                } else {
                    cls = TypeKit.getRawClass(parameters[i]);
                }
                args[i] = Mocker.mock(cls);
            }
            // method.setAccessible(true);
            method.invoke(dataMap, args);
        }
    }
}
