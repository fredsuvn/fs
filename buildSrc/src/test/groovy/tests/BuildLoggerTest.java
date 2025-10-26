package tests;

import internal.test.PrintTest;
import org.testng.annotations.Test;
import space.sunqian.common.app.SimpleApp;
import space.sunqian.common.app.SimpleAppException;

import static org.testng.Assert.expectThrows;

public class BuildLoggerTest implements PrintTest {

    @Test
    public void testSimpleApp() throws Exception {
        SimpleApp app = () -> {
        };
        app.shutdown();
    }

    @Test
    public void testException() throws Exception {
    }
}
