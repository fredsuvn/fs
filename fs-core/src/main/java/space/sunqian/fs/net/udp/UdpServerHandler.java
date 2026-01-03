package space.sunqian.fs.net.udp;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

/**
 * Handler for handling udp server events for UDP network server, based on {@link DatagramChannel}.
 *
 * @author sunqian
 */
public interface UdpServerHandler {

    /**
     * Returns an instance of {@link UdpServerHandler} that does nothing but discards received data.
     *
     * @return an instance of {@link UdpServerHandler} that does nothing but discards received data
     */
    static @Nonnull UdpServerHandler nullHandler() {
        return NullServerHandler.INST;
    }

    /**
     * This method is invoked after the server receives a data packet.
     *
     * @param channel the channel where the data is received
     * @param data    the array contains the received data
     * @param address the address of the data sender
     * @throws Exception for any error
     */
    void channelRead(
        @Nonnull DatagramChannel channel, byte @Nonnull [] data, @Nonnull SocketAddress address
    ) throws Exception;

    /**
     * This method is invoked after catching an unhandled exception, the exception may come from this handler or from
     * the container running this handler.
     * <p>
     * The behavior is undefined if this method still throws an exception.
     *
     * @param channel the channel parameter of the method where this handler throws the exception, may be {@code null}
     *                if the exception is not thrown from this handler
     * @param cause   the unhandled exception
     */
    void exceptionCaught(@Nullable DatagramChannel channel, @Nonnull Throwable cause);
}
