package test.utils;

public class ThreadUtil {

    public static void sleep() throws InterruptedException {
        while (true) {
            Thread.sleep(Integer.MAX_VALUE);
        }
    }
}
