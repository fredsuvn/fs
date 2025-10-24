package tests.utils;

import xyz.sunqian.common.base.Kit;

public class Utils {

    public static void awaitUntilExecuteTo(Thread thread, String className, String methodName) {
        Kit.until(() -> {
            StackTraceElement[] traceElements = thread.getStackTrace();
            for (StackTraceElement traceElement : traceElements) {
                if (Kit.equals(className, traceElement.getClassName())
                    && Kit.equals(methodName, traceElement.getMethodName())) {
                    return true;
                }
            }
            return false;
        });
    }
}
