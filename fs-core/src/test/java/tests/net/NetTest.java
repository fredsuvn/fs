package tests.net;

import internal.test.PrintTest;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.net.NetException;
import space.sunqian.fs.net.NetKit;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class NetTest implements PrintTest {

    @Test
    public void testBroadcastAddress() throws Exception {
        printFor("allBroadcastAddresses", NetKit.allBroadcastAddresses());
        printFor("getBroadcastAddress", NetKit.getBroadcastAddress());
    }

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
