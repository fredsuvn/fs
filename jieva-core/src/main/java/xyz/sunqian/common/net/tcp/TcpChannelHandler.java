package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.communicate.IOChannelHandler;

/**
 * {@link IOChannelHandler} for tcp network.
 *
 * @author sunqian
 */
public interface TcpChannelHandler extends IOChannelHandler<TcpChannel> {

    /**
     * Returns an instance of {@link TcpChannelHandler} that does nothing but reads and discards available data in the
     * channel.
     *
     * @return an instance of {@link TcpChannelHandler} that does nothing but reads and discards available data in the
     * channel
     */
    static @Nonnull TcpChannelHandler nullHandler() {
        return NullTcpChannelHandler.SINGLETON;
    }
}
