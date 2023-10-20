package xyz.fsgek.common.net.tcp;

import java.nio.ByteBuffer;

/**
 * TCP/IP network handler for client actions.
 *
 * @author fredsuvn
 */
public interface GekTcpClientHandler {

    /**
     * Callback when the channel was opened.
     *
     * @param channel the channel
     */
    default void onOpen(GekTcpChannel channel) {
    }

    /**
     * Callback when the channel was closed.
     *
     * @param channel the channel
     * @param buffer  remaining buffer of the channel, readonly and initial position is 0
     */
    default void onClose(GekTcpChannel channel, ByteBuffer buffer) {
    }

    /**
     * Callback when an exception occurs in the channel.
     *
     * @param channel   the channel
     * @param throwable the exception
     * @param buffer    remaining buffer of the channel, readonly and initial position is 0
     */
    default void onException(GekTcpChannel channel, Throwable throwable, ByteBuffer buffer) {
    }

    /**
     * Callback for each read loop of channel.
     * <p>
     * Client has a list of handlers, if there has new data read from remote endpoint,
     * {@link GekTcpChannelHandler#onMessage(GekTcpChannel, Object)} will be called for each handler.
     * <b>Only all onMessage method of handlers have been called</b>, then this method would be called.
     * <p>
     * If an exception is thrown, {@link #onException(GekTcpChannel, Throwable, ByteBuffer)} will be called.
     *
     * @param channel    the channel
     * @param hasNewData whether there has new data read in current loop
     * @param buffer     remaining buffer of the channel, readonly and initial position is 0
     */
    default void onLoop(GekTcpChannel channel, boolean hasNewData, ByteBuffer buffer) {
    }
}
