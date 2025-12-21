package tests.net;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.common.net.NetException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class NetTest implements PrintTest {

    @Test
    public void testException() throws Exception {
        {
            // NetException
            assertThrows(NetException.class, () -> {
                throw new NetException();
            });
            assertThrows(NetException.class, () -> {
                throw new NetException("");
            });
            assertThrows(NetException.class, () -> {
                throw new NetException("", new RuntimeException());
            });
            assertThrows(NetException.class, () -> {
                throw new NetException(new RuntimeException());
            });
        }
    }
}
