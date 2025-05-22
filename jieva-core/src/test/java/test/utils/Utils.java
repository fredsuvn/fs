package test.utils;


import xyz.sunqian.common.base.Jie;

import java.util.Objects;

public class Utils {

    public static void awaitUntilExecuteTo(Thread thread, String className, String methodName) {
        Jie.until(() -> {
            StackTraceElement[] traceElements = thread.getStackTrace();
            for (StackTraceElement traceElement : traceElements) {
                if (Objects.equals(className, traceElement.getClassName())
                    && Objects.equals(methodName, traceElement.getMethodName())) {
                    return true;
                }
            }
            return false;
        });
    }
}
