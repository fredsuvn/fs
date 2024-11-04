package xyz.sunqian.common.net.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Represents header info of UDP datagram packet, such as address and port.
 *
 * @author fredsuvn
 */
public interface GekUdpHeader {

    /**
     * Returns UDP header of which content comes from given datagram packet.
     *
     * @param packet given packet
     * @return UDP header
     */
    static GekUdpHeader from(DatagramPacket packet) {
        SocketAddress socketAddress = packet.getSocketAddress();
        if (socketAddress instanceof InetSocketAddress) {
            return of((InetSocketAddress) socketAddress);
        }
        InetSocketAddress inetSocketAddress = new InetSocketAddress(packet.getAddress(), packet.getPort());
        return of(inetSocketAddress);
    }

    /**
     * Returns UDP header of given address.
     *
     * @param address given address
     * @return UDP header
     */
    static GekUdpHeader of(InetSocketAddress address) {
        return new GekUdpHeader() {

            @Override
            public InetAddress getAddress() {
                return address.getAddress();
            }

            @Override
            public int getPort() {
                return address.getPort();
            }

            @Override
            public InetSocketAddress getInetSocketAddress() {
                return address;
            }
        };
    }

    /**
     * Returns address of the datagram packet.
     *
     * @return address of the datagram packet
     */
    InetAddress getAddress();

    /**
     * Returns port of the datagram packet.
     *
     * @return port of the datagram packet
     */
    int getPort();

    /**
     * Returns {@link InetSocketAddress}.
     *
     * @return {@link InetSocketAddress}
     */
    InetSocketAddress getInetSocketAddress();
}
