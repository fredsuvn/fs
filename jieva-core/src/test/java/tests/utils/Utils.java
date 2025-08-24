package tests.utils;

import xyz.sunqian.common.base.Jie;

public class Utils {

    public static void awaitUntilExecuteTo(Thread thread, String className, String methodName) {
        Jie.until(() -> {
            StackTraceElement[] traceElements = thread.getStackTrace();
            for (StackTraceElement traceElement : traceElements) {
                if (Jie.equals(className, traceElement.getClassName())
                    && Jie.equals(methodName, traceElement.getMethodName())) {
                    return true;
                }
            }
            return false;
        });
    }
}
