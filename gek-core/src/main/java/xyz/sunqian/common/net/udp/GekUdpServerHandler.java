package xyz.sunqian.common.net.udp;

import xyz.sunqian.common.net.GekNetServerException;

import java.nio.ByteBuffer;

/**
 * UDP network handler in server endpoint.
 *
 * @author fredsuvn
 */
public interface GekUdpServerHandler {

    /**
     * Callback when an exception occurs on the server.
     *
     * @param exception the exception for server
     */
    default void onException(GekNetServerException exception) {
    }

    /**
     * Callback when an exception occurs for the udp packet.
     *
     * @param header    header of the packet
     * @param client    the server itself as a client
     * @param throwable the exception
     * @param buffer    remaining buffer of the packet, readonly and initial position is 0
     */
    default void onException(GekUdpHeader header, GekUdpClient client, Throwable throwable, ByteBuffer buffer) {
    }
}
