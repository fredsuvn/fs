package tests.app;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.app.SimpleApp;
import space.sunqian.fs.app.SimpleAppException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppTest implements PrintTest {

    @Test
    public void testSimpleApp() throws Exception {
        SimpleApp app = () -> {
        };
        app.shutdown();
    }

    @Test
    public void testException() throws Exception {
        {
            // SimpleAppException
            assertThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException();
            });
            assertThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException("");
            });
            assertThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException("", new RuntimeException());
            });
            assertThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException(new RuntimeException());
            });
        }
    }
}
