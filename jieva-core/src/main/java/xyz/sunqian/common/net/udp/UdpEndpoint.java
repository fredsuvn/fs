// package xyz.sunqian.common.net.udp;
//
// import xyz.sunqian.annotations.Nonnull;
// import xyz.sunqian.common.net.NetServer;
//
// import java.net.InetSocketAddress;
// import java.nio.ByteBuffer;
//
// /**
//  * Represents a UDP endpoint that can simultaneously support serving as a server and sending UDP datagrams.
//  * <p>
//  * can be built with {@link #newBuilder()}.
//  *
//  * @author sunqian
//  */
// public interface UdpEndpoint extends NetServer<InetSocketAddress> {
//
//     /**
//      * Sends data to the specified address.
//      *
//      * @param address the specified address to send to
//      * @param data    the data to be sent
//      */
//     void sendData(InetSocketAddress address, @Nonnull ByteBuffer data);
//
//     /**
//      * Sends data to the specified address.
//      *
//      * @param address the specified address to send to
//      * @param data    the data to be sent
//      */
//     void sendData(InetSocketAddress address, @Nonnull ByteBuffer data);
// }
