package test;

import org.testng.annotations.Test;
import xyz.sunqian.test.JieTest;
import xyz.sunqian.test.JieTestException;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class TestForTest {

    @Test
    public void testExpect() throws Exception {
        Method throwError = T.class.getDeclaredMethod("throwError");
        assertEquals(JieTest.testThrow(JieTestException.class, throwError, null).getClass(), JieTestException.class);
    }

    @Test
    public void testFile() throws Exception {
        Path path = Paths.get("src", "test", "resources", "test.test");
        byte[] data = {'1', '2', '3'};
        JieTest.createFile(path, data);
        expectThrows(IllegalStateException.class, () -> JieTest.createFile(path, data));
        path.toFile().delete();
    }

    private static final class T {

        private static void throwError() {
            throw new JieTestException();
        }
    }

    @Test
    public void coverage() {
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException();
        });
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException("");
        });
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException("", new RuntimeException());
        });
        expectThrows(JieTestException.class, () -> {
            throw new JieTestException(new RuntimeException());
        });
    }
}
