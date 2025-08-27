package tests.net;

import org.testng.annotations.Test;
import xyz.sunqian.common.net.NetException;

import static org.testng.Assert.expectThrows;

public class NetTest {

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
