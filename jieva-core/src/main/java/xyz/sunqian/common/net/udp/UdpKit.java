package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.CollectKit;
import xyz.sunqian.common.collect.StreamKit;
import xyz.sunqian.common.net.NetException;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Utilities for UDP.
 *
 * @author sunqian
 */
public class UdpKit {

    /**
     * Returns a broadcast IP address, if the local broadcast IP is not found, returns {@code 255.255.255.255}.
     *
     * @return a broadcast IP address, if the local broadcast IP is not found, returns {@code 255.255.255.255}
     * @throws NetException if an error occurs
     */
    public static @Nonnull InetAddress getBroadcastAddress() throws NetException {
        return Jie.uncheck(UdpKit::getBroadcastAddress0, NetException::new);
    }

    private static @Nonnull InetAddress getBroadcastAddress0() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        return StreamKit.stream(() -> CollectKit.asIterator(interfaces))
            .filter(networkInterface ->
                Jie.uncheck(networkInterface::isUp, NetException::new))
            .filter(networkInterface ->
                Jie.uncheck(() -> !networkInterface.isLoopback(), NetException::new))
            .filter(networkInterface ->
                Jie.uncheck(() -> !networkInterface.isPointToPoint(), NetException::new))
            .flatMap(networkInterface -> networkInterface.getInterfaceAddresses().stream())
            .map(InterfaceAddress::getBroadcast)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(InetAddress.getByName("255.255.255.255"));
    }

    static void channelRead(
        @Nonnull UdpServerHandler handler,
        @Nonnull DatagramChannel channel,
        byte @Nonnull [] data,
        @Nonnull SocketAddress address
    ) {
        try {
            handler.channelRead(channel, data, address);
        } catch (Throwable e) {
            handler.exceptionCaught(channel, e);
        }
    }
}
