package tests.base.system;

import org.testng.annotations.Test;
import space.sunqian.common.base.system.SystemKeys;
import space.sunqian.common.base.system.SystemKit;
import space.sunqian.test.PrintTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class SystemTest implements PrintTest {

    @Test
    public void testSystemProperties() throws Exception {
        Field[] keyFields = SystemKeys.class.getFields();
        Set<String> keyset = new LinkedHashSet<>();
        for (Field keyField : keyFields) {
            String fieldName = keyField.getName();
            String key = (String) keyField.get(null);
            keyset.add(key);
            String getterName = "get" + Arrays.stream(fieldName.split("_")).map(w -> {
                if (w.equals("IO")) {
                    return w;
                }
                return w.charAt(0) + w.substring(1).toLowerCase();
            }).collect(Collectors.joining(""));
            Method getter = SystemKit.class.getMethod(getterName);
            assertEquals(getter.invoke(null), System.getProperty(key));
            printFor(key, getter.invoke(null));
        }
        assertEquals(SystemKeys.keyset(), keyset);
        assertEquals(SystemKit.getProperties(), System.getProperties());
    }
}
