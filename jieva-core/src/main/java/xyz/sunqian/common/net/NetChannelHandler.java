package xyz.sunqian.common.net;

import xyz.sunqian.common.io.communicate.IOChannelHandler;

import java.net.InetSocketAddress;

/**
 * Handler for handling network channel events.
 *
 * @author sunqian
 */
public interface NetChannelHandler
    extends IOChannelHandler<InetSocketAddress, NetChannelContext> {
}
