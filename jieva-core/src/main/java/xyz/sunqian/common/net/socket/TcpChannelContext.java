package xyz.sunqian.common.net.socket;

import xyz.sunqian.common.io.communicate.IOChannelContext;

import java.net.InetSocketAddress;

/**
 * Represents a context of a Tcp channel, can be used to obtain information from the remote endpoint of the channel and
 * send data to it.
 *
 * @author sunqian
 */
public interface TcpChannelContext extends IOChannelContext<InetSocketAddress> {
}
