package tests.net;

import org.testng.annotations.Test;
import space.sunqian.common.net.NetException;
import internal.test.PrintTest;

import static org.testng.Assert.expectThrows;

public class NetTest implements PrintTest {

    @Test
    public void testException() throws Exception {
        {
            // NetException
            expectThrows(NetException.class, () -> {
                throw new NetException();
            });
            expectThrows(NetException.class, () -> {
                throw new NetException("");
            });
            expectThrows(NetException.class, () -> {
                throw new NetException("", new RuntimeException());
            });
            expectThrows(NetException.class, () -> {
                throw new NetException(new RuntimeException());
            });
        }
    }
}
