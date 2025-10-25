package tests.app;

import org.testng.annotations.Test;
import space.sunqian.common.app.SimpleApp;
import space.sunqian.common.app.SimpleAppException;
import space.sunqian.test.PrintTest;

import static org.testng.Assert.expectThrows;

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
            expectThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException();
            });
            expectThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException("");
            });
            expectThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException("", new RuntimeException());
            });
            expectThrows(SimpleAppException.class, () -> {
                throw new SimpleAppException(new RuntimeException());
            });
        }
    }
}
