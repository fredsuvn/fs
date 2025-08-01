package test.base.system;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.system.SystemKeys;
import xyz.sunqian.common.base.system.SystemKit;
import xyz.sunqian.test.PrintTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

public class SystemTest implements PrintTest {

    @Test
    public void testSystemProperties() throws Exception {
        printFor("JDK description", SystemKit.jdkDescription());
        Field[] keyFields = SystemKeys.class.getFields();
        for (Field keyField : keyFields) {
            String fieldName = keyField.getName();
            String key = (String) keyField.get(null);
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
    }

    @Test
    public void testJavaVersion() throws Exception {
        printFor("JDK major version", SystemKit.jdkMajorVersion());
    }
}
