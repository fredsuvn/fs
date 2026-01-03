package tests.base.system;

import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.system.JvmException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionTest {

    @Test
    public void testExceptions() {
        {
            // JvmException
            assertThrows(JvmException.class, () -> {
                throw new JvmException();
            });
            assertThrows(JvmException.class, () -> {
                throw new JvmException("");
            });
            assertThrows(JvmException.class, () -> {
                throw new JvmException("", new RuntimeException());
            });
            assertThrows(JvmException.class, () -> {
                throw new JvmException(new RuntimeException());
            });
        }
    }
}
