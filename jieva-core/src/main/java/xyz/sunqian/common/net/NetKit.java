package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Kit;
import xyz.sunqian.common.collect.CollectKit;
import xyz.sunqian.common.collect.StreamKit;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Network utilities.
 *
 * @author sunqian
 */
public class NetKit {

    /**
     * Returns a broadcast IP address, if the local broadcast IP is not found, returns {@code 255.255.255.255}.
     *
     * @return a broadcast IP address, if the local broadcast IP is not found, returns {@code 255.255.255.255}
     * @throws NetException if an error occurs
     */
    public static @Nonnull InetAddress getBroadcastAddress() throws NetException {
        return Kit.uncheck(NetKit::getBroadcastAddress0, NetException::new);
    }

    private static @Nonnull InetAddress getBroadcastAddress0() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        return StreamKit.stream(() -> CollectKit.asIterator(interfaces))
            .filter(networkInterface ->
                Kit.uncheck(networkInterface::isUp, NetException::new))
            .filter(networkInterface ->
                Kit.uncheck(() -> !networkInterface.isLoopback(), NetException::new))
            .filter(networkInterface ->
                Kit.uncheck(() -> !networkInterface.isPointToPoint(), NetException::new))
            .flatMap(networkInterface -> networkInterface.getInterfaceAddresses().stream())
            .map(InterfaceAddress::getBroadcast)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(InetAddress.getByName("255.255.255.255"));
    }
}
