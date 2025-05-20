package test.base.thread;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.exception.AwaitingException;
import xyz.sunqian.common.base.thread.AwaitingAdaptor;

import java.time.Duration;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

public class AwaitingTest {

    @Test
    public void testInterruptible() {
        AwaitingAdaptor interruptible1 = new Awaiting(false);
        interruptible1.await();
        assertTrue(interruptible1.await(Duration.ofMillis(1)));
        AwaitingAdaptor interruptible2 = new Awaiting(true);
        expectThrows(AwaitingException.class, interruptible2::await);
        expectThrows(AwaitingException.class, () -> interruptible2.await(1));
        expectThrows(AwaitingException.class, () -> interruptible2.await(Duration.ofMillis(1)));
    }

    private static final class Awaiting implements AwaitingAdaptor {

        private final boolean throwException;

        private Awaiting(boolean throwException) {
            this.throwException = throwException;
        }

        @Override
        public void awaitInterruptibly() throws Exception {
            if (throwException) {
                AwaitingAdaptor.super.awaitInterruptibly();
            }
        }

        @Override
        public boolean awaitInterruptibly(long millis) throws Exception {
            if (throwException) {
                return AwaitingAdaptor.super.awaitInterruptibly(millis);
            }
            return true;
        }

        @Override
        public boolean awaitInterruptibly(@Nonnull Duration duration) throws Exception {
            if (throwException) {
                return AwaitingAdaptor.super.awaitInterruptibly(duration);
            }
            return true;
        }
    }
}
