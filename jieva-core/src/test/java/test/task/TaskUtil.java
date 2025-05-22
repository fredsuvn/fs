package test.task;

import java.time.Instant;

import static org.testng.Assert.assertTrue;

public class TaskUtil {

    static final int DELAY_MILLIS = 1000;

    public static void shouldAfterNow(Instant startTime, long delayTime) {
        Instant now = Instant.now();
        long startSecond = startTime.getEpochSecond();
        long startNano = startTime.getNano();
        long delaySecond = delayTime / 1000;
        long delayNano = (delayTime - (delayTime / 1000 * 1000)) * 1000000;
        long diffSecond = now.getEpochSecond() - startSecond;
        long diffNanos = now.getNano() - startNano;
        boolean isDelay = diffSecond >= delaySecond && diffNanos >= delayNano;
        System.out.println(
            "now: " + now
                + ", delaySecond: " + delaySecond
                + ", delayNano: " + delayNano
                + ", diffSecond: " + diffSecond
                + ", diffNanos: " + diffNanos
                + ", isDelay: " + isDelay
        );
        assertTrue(isDelay);
    }
}
