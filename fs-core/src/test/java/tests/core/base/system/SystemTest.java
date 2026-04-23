package tests.core.base.system;

import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.system.SystemKeys;
import space.sunqian.fs.base.system.SystemKit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemTest implements TestPrint {

    @Test
    public void testSystemProperties() throws Exception {
        testSystemKeyGetters();
        testSystemKeyset();
        testSystemPropertiesRetrieval();
    }

    private void testSystemKeyGetters() throws Exception {
        Field[] keyFields = SystemKeys.class.getFields();
        for (Field keyField : keyFields) {
            String fieldName = keyField.getName();
            String key = (String) keyField.get(null);
            String getterName = generateGetterName(fieldName);
            Method getter = SystemKit.class.getMethod(getterName);
            assertEquals(getter.invoke(null), System.getProperty(key));
            printFor(key, getter.invoke(null));
        }
    }

    private String generateGetterName(String fieldName) {
        return "get" + Arrays.stream(fieldName.split("_"))
            .map(w -> {
                if (w.equals("IO")) {
                    return w;
                }
                return w.charAt(0) + w.substring(1).toLowerCase();
            })
            .collect(Collectors.joining(""));
    }

    private void testSystemKeyset() throws Exception {
        Field[] keyFields = SystemKeys.class.getFields();
        Set<String> keyset = new LinkedHashSet<>();
        for (Field keyField : keyFields) {
            String key = (String) keyField.get(null);
            keyset.add(key);
        }
        assertEquals(SystemKeys.keyset(), keyset);
    }

    private void testSystemPropertiesRetrieval() {
        assertEquals(SystemKit.getProperties(), System.getProperties());
    }
}
