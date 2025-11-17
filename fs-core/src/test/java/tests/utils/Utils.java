package tests.utils;

import space.sunqian.common.Fs;

public class Utils {

    public static void awaitUntilExecuteTo(Thread thread, String className, String methodName) {
        Fs.until(() -> {
            StackTraceElement[] traceElements = thread.getStackTrace();
            for (StackTraceElement traceElement : traceElements) {
                if (Fs.equals(className, traceElement.getClassName())
                    && Fs.equals(methodName, traceElement.getMethodName())) {
                    return true;
                }
            }
            return false;
        });
    }
}
