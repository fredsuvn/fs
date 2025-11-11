package space.sunqian.common.net;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.Kit;
import space.sunqian.common.collect.CollectKit;
import space.sunqian.common.collect.StreamKit;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Predicate;

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
            .filter(((Predicate<NetworkInterface>) networkInterface ->
                Kit.uncheck(networkInterface::isLoopback, NetException::new)).negate())
            .filter(((Predicate<NetworkInterface>) networkInterface ->
                Kit.uncheck(networkInterface::isPointToPoint, NetException::new)).negate())
            .flatMap(networkInterface -> networkInterface.getInterfaceAddresses().stream())
            .map(InterfaceAddress::getBroadcast)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(InetAddress.getByName("255.255.255.255"));
    }

    private NetKit() {
    }
}
