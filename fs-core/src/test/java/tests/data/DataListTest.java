package tests.data;

import internal.test.ErrorList;
import internal.test.Mocker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.collect.MapKit;
import space.sunqian.fs.data.DataException;
import space.sunqian.fs.data.DataList;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataListTest {

    @Test
    public void testDataList() throws Exception {
        DataList dataList = DataList.newList();
        dataList.add(1111);
        assertEquals(
            DataList.wrap(ListKit.list(1111)),
            dataList
        );
        assertTrue(dataList.contentEquals(ListKit.list(1111)));
        assertFalse(dataList.equals(ListKit.list(1111)));
        assertEquals(
            dataList.toString(),
            ListKit.arrayList(1111).toString()
        );
        // conversions
        assertEquals(1111, dataList.get(0));
        assertEquals(2222, dataList.get(1, 2222));
        assertNull(dataList.get(1, null));
        assertEquals("1111", dataList.getString(0));
        assertEquals("2222", dataList.getString(1, "2222"));
        assertEquals(1111, dataList.getInt(0));
        assertEquals(2222, dataList.getInt(1, 2222));
        assertEquals(1111L, dataList.getLong(0));
        assertEquals(2222L, dataList.getLong(1, 2222L));
        assertEquals(1111.0F, dataList.getFloat(0));
        assertEquals(2222.0F, dataList.getFloat(1, 2222.0F));
        assertEquals(1111.0, dataList.getDouble(0));
        assertEquals(2222.0, dataList.getDouble(1, 2222.0));
        assertEquals(new BigDecimal("1111"), dataList.getBigDecimal(0));
        assertEquals(new BigDecimal("1222"), dataList.getBigDecimal(1, new BigDecimal("1222")));
        assertThrows(DataException.class, () -> {
            DataList.wrap(new ErrorList<>()).getBigDecimal(1);
        });

        // to list
        dataList.clear();
        dataList.add(MapKit.map("str111", "1111"));
        dataList.add(MapKit.map("str111", "2222"));
        assertEquals(new Cls("1111"), dataList.toObjectList(Cls.class).get(0));
        assertEquals(new Cls("2222"), dataList.toObjectList(new TypeRef<Cls>() {}).get(1));
    }

    @Test
    public void testWrapper() throws Exception {
        DataList dataList = DataList.newList();
        for (Method method : DataList.class.getMethods()) {
            if (method.getDeclaringClass().equals(DataList.class)) {
                continue;
            }
            Class<?>[] parameters = method.getParameterTypes();
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                args[i] = Mocker.mock(parameters[i]);
            }
            // method.setAccessible(true);
            dataList.add(1);
            method.invoke(dataList, args);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cls {
        private String str111;
    }
}
