package test.work;

import org.testng.annotations.Test;
import test.utils.FlagException;
import xyz.sunqian.common.base.exception.WrappedException;
import xyz.sunqian.common.work.JieWork;

import java.util.concurrent.Callable;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class WorkTest {

    @Test
    public void testToRunnable() {
        int[] i = {0};
        Runnable runnable = JieWork.toRunnable(() -> {
            i[0]++;
            return null;
        });
        assertEquals(i[0], 0);
        runnable.run();
        assertEquals(i[0], 1);
        RunnableCall<?> work1 = new RunnableCall<Object>() {
            @Override
            public void run() {
                i[0]++;
            }

            @Override
            public Object call() {
                i[0]++;
                return null;
            }
        };
        Runnable workRunnable1 = JieWork.toRunnable(work1);
        workRunnable1.run();
        assertEquals(i[0], 2);
        Callable<?> work2 = (Callable<Object>) () -> {
            throw new FlagException(i);
        };
        Runnable workRunnable2 = JieWork.toRunnable(work2);
        expectThrows(WrappedException.class, workRunnable2::run);
        assertEquals(i[0], 3);
    }

    private interface RunnableCall<T> extends Runnable, Callable<T> {
    }
}
