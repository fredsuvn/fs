package test.base.system;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.system.JvmException;

import static org.testng.Assert.expectThrows;

public class ExceptionTest {

    @Test
    public void testExceptions() {
        {
            // JvmException
            expectThrows(JvmException.class, () -> {
                throw new JvmException();
            });
            expectThrows(JvmException.class, () -> {
                throw new JvmException("");
            });
            expectThrows(JvmException.class, () -> {
                throw new JvmException("", new RuntimeException());
            });
            expectThrows(JvmException.class, () -> {
                throw new JvmException(new RuntimeException());
            });
        }
    }
}
