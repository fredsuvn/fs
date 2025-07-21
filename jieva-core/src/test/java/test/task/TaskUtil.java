package test.task;

import java.time.Instant;

import static org.testng.Assert.assertTrue;

public class TaskUtil {

    public static final int DELAY_MILLIS = 10;

    public static void shouldAfterNow(Instant startTime, long delayMillis) {
        Instant now = Instant.now();
        long startSecond = startTime.getEpochSecond();
        long startNano = startTime.getNano();
        long delaySecond = delayMillis / 1000;
        long delayNano = (delayMillis - (delayMillis / 1000 * 1000)) * 1000000;
        long diffSecond = now.getEpochSecond() - startSecond;
        long diffNanos = now.getNano() - startNano;
        boolean isDelay = diffSecond == delaySecond ? (diffNanos >= delayNano) : (diffSecond > delaySecond);
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
