package tests.app;

import org.testng.annotations.Test;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.app.SimpleApp;
import xyz.sunqian.common.app.SimpleAppException;
import xyz.sunqian.common.app.SimpleResource;
import xyz.sunqian.common.app.SimpleResourceException;
import xyz.sunqian.test.PrintTest;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

public class AppTest implements PrintTest {

    @Test
    public void testSimpleApp() throws Exception {
        SimpleResource res = () -> SimpleResource.class;
        SimpleApp app = new SimpleApp() {
            @Override
            public void shutdown() throws SimpleAppException {

            }

            @Override
            public @Nonnull List<@Nonnull SimpleResource> resources() {
                return Collections.singletonList(res);
            }
        };
        assertEquals(app.resources(), Collections.singletonList(res));
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
        {
            // SimpleResourceException
            expectThrows(SimpleResourceException.class, () -> {
                throw new SimpleResourceException();
            });
            expectThrows(SimpleResourceException.class, () -> {
                throw new SimpleResourceException("");
            });
            expectThrows(SimpleResourceException.class, () -> {
                throw new SimpleResourceException("", new RuntimeException());
            });
            expectThrows(SimpleResourceException.class, () -> {
                throw new SimpleResourceException(new RuntimeException());
            });
        }
    }
}
