package tests.core.data;

import internal.utils.ErrorList;
import internal.utils.Mocker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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

    private DataList dataList;

    @BeforeEach
    public void setUp() {
        dataList = DataList.newList();
    }

    @Test
    public void testDataListBasicOperations() {
        dataList.add(1111);
        testGetOperations();
        testSetOperations();
        testEqualityOperations();
        testToStringOperation();
    }

    @Test
    public void testDataListConversions() {
        dataList.add(1111);
        dataList.set(0, 2222);
        dataList.add(3333);
        dataList.set(1, 4444);
        testTypeConversions();
        testErrorHandling();
    }

    @Test
    public void testDataListToList() {
        dataList.add(MapKit.map("str111", "1111"));
        dataList.add(MapKit.map("str111", "2222"));
        testToListWithClass();
        testToListWithTypeRef();
        testToObjectWithArray();
    }

    @Test
    public void testDataListWrapper() throws Exception {
        dataList.add(1);
        for (Method method : DataList.class.getMethods()) {
            if (method.getDeclaringClass().equals(DataList.class)) {
                continue;
            }
            Class<?>[] parameters = method.getParameterTypes();
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                // Handle index parameter to ensure it's within valid range
                if (parameters[i] == int.class) {
                    // For methods that require index, use valid index value
                    String methodName = method.getName();
                    if (methodName.equals("set") || methodName.equals("get") || methodName.equals("add") || methodName.equals("remove") || methodName.equals("listIterator")) {
                        args[i] = 0; // Use index of the first element
                    } else {
                        args[i] = Mocker.mock(parameters[i]);
                    }
                } else {
                    args[i] = Mocker.mock(parameters[i]);
                }
            }
            try {
                method.invoke(dataList, args);
            } catch (Exception e) {
                // Ignore possible exceptions as we're just testing method invocation
            }
        }
    }

    private void testGetOperations() {
        assertEquals(1111, dataList.get(0));
        assertEquals(2222, dataList.get(1, 2222));
        assertNull(dataList.get(1, null));
    }

    private void testSetOperations() {
        // Test set method at index 0
        Object oldValue = dataList.set(0, 2222);
        assertEquals(1111, oldValue);
        assertEquals(2222, dataList.get(0));

        // Add another element and test set at index 1
        dataList.add(3333);
        oldValue = dataList.set(1, 4444);
        assertEquals(3333, oldValue);
        assertEquals(4444, dataList.get(1));
    }

    private void testEqualityOperations() {
        assertEquals(
            DataList.wrap(ListKit.list(2222, 4444)),
            dataList
        );
        assertTrue(dataList.contentEquals(ListKit.list(2222, 4444)));
        assertTrue(dataList.equals(ListKit.list(2222, 4444)));
        assertFalse(dataList.equals(""));
    }

    private void testToStringOperation() {
        assertEquals(
            dataList.toString(),
            ListKit.arrayList(2222, 4444).toString()
        );
    }

    private void testTypeConversions() {
        assertEquals("2222", dataList.getString(0));
        assertEquals("4444", dataList.getString(1, "4444"));
        assertEquals(2222, dataList.getInt(0));
        assertEquals(4444, dataList.getInt(1, 4444));
        assertEquals(2222L, dataList.getLong(0));
        assertEquals(4444L, dataList.getLong(1, 4444L));
        assertEquals(2222.0F, dataList.getFloat(0));
        assertEquals(4444.0F, dataList.getFloat(1, 4444.0F));
        assertEquals(2222.0, dataList.getDouble(0));
        assertEquals(4444.0, dataList.getDouble(1, 4444.0));
        assertEquals(new BigDecimal("2222"), dataList.getBigDecimal(0));
        assertEquals(new BigDecimal("4444"), dataList.getBigDecimal(1, new BigDecimal("4444")));
    }

    private void testErrorHandling() {
        assertThrows(DataException.class, () -> {
            DataList.wrap(new ErrorList<>()).getBigDecimal(1);
        });
        assertThrows(DataException.class, () -> {
            DataList.wrap(new ErrorList<>()).toList(String.class);
        });
    }

    private void testToListWithClass() {
        assertEquals(new Cls("1111"), dataList.toList(Cls.class).get(0));
    }

    private void testToListWithTypeRef() {
        assertEquals(new Cls("2222"), dataList.toList(new TypeRef<Cls>() {}).get(1));
    }

    private void testToObjectWithArray() {
        assertEquals(new Cls("1111"), dataList.toObject(new TypeRef<Cls[]>() {})[0]);
        assertEquals(new Cls("2222"), dataList.toObject(Cls[].class)[1]);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cls {
        private String str111;
    }
}
