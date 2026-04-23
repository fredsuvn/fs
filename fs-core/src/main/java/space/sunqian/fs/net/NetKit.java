package space.sunqian.fs.net;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.CollectKit;
import space.sunqian.fs.collect.StreamKit;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Network utilities.
 *
 * @author sunqian
 */
public class NetKit {

    /**
     * The IP address: {@code 255.255.255.255}. This is the default broadcast IP.
     */
    public static final @Nonnull InetAddress INET_ADDR_255_255_255_255;

    /**
     * The IP address: {@code 0.0.0.0}.
     */
    public static final @Nonnull InetAddress INET_ADDR_0_0_0_0;

    static {
        INET_ADDR_255_255_255_255 =
            Fs.uncheck(() -> InetAddress.getByName("255.255.255.255"), NetException::new);
        INET_ADDR_0_0_0_0 =
            Fs.uncheck(() -> InetAddress.getByName("0.0.0.0"), NetException::new);
    }

    /**
     * Returns a broadcast IP address, if the local broadcast IP is not found, returns {@code 255.255.255.255}.
     *
     * @return a broadcast IP address, if the local broadcast IP is not found, returns {@code 255.255.255.255}
     * @throws NetException if an error occurs
     */
    public static @Nonnull InetAddress getBroadcastAddress() throws NetException {
        return Fs.uncheck(NetKit::getBroadcastAddress0, NetException::new);
    }

    private static @Nonnull InetAddress getBroadcastAddress0() throws Exception {
        return allBroadcastAddresses0()
            .stream()
            .findFirst()
            .orElse(INET_ADDR_255_255_255_255);
    }

    /**
     * Returns all broadcast IP address, if the local broadcast IP is not found, returns an empty list.
     *
     * @return all broadcast IP address, if the local broadcast IP is not found, returns an empty list
     * @throws NetException if an error occurs
     */
    public static @Nonnull @Immutable List<@Nonnull InetAddress> allBroadcastAddresses() throws NetException {
        return Fs.uncheck(NetKit::allBroadcastAddresses0, NetException::new);
    }

    private static @Nonnull @Immutable List<@Nonnull InetAddress> allBroadcastAddresses0() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        return StreamKit.stream(() -> CollectKit.asIterator(interfaces))
            .filter(networkInterface ->
                Fs.uncheck(networkInterface::isUp, NetException::new))
            .filter(networkInterface ->
                Fs.uncheck(networkInterface::supportsMulticast, NetException::new))
            .filter(((Predicate<NetworkInterface>) networkInterface ->
                Fs.uncheck(networkInterface::isLoopback, NetException::new)).negate())
            .filter(((Predicate<NetworkInterface>) networkInterface ->
                Fs.uncheck(networkInterface::isPointToPoint, NetException::new)).negate())
            .flatMap(networkInterface -> networkInterface.getInterfaceAddresses().stream())
            .map(InterfaceAddress::getBroadcast)
            .filter(Objects::nonNull)
            //.filter(ip -> !ip.equals(INET_ADDR_0_0_0_0))
            .collect(Collectors.toList());
    }

    private NetKit() {
    }
}
