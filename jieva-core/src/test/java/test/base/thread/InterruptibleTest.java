package test.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.thread.Interruptible;

import java.time.Duration;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class InterruptibleTest {

    @Test
    public void testInterruptible() {
        Interruptible interruptible1 = new InterruptibleImpl(false);
        interruptible1.await();
        assertTrue(interruptible1.await(Duration.ofMillis(1)));
        Interruptible interruptible2 = new InterruptibleImpl(true);
        expectThrows(AwaitingException.class, interruptible2::await);
        expectThrows(AwaitingException.class, () -> interruptible2.await(Duration.ofMillis(1)));
    }

    private static final class InterruptibleImpl implements Interruptible {

        private final boolean throwException;

        private InterruptibleImpl(boolean throwException) {
            this.throwException = throwException;
        }

        @Override
        public void awaitInterruptibly() throws InterruptedException {
            if (throwException) {
                throw new InterruptedException();
            }
        }

        @Override
        public boolean awaitInterruptibly(Duration duration) throws InterruptedException {
            if (throwException) {
                throw new InterruptedException();
            }
            return true;
        }
    }
}
