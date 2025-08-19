package xyz.sunqian.common.net;

import xyz.sunqian.common.io.communicate.IOChannelHandler;

import java.net.InetSocketAddress;

/**
 * Handles network IO events.
 *
 * @author sunqian
 */
public interface NetChannelHandler extends IOChannelHandler<InetSocketAddress, NetChannelContext> {
}
