package space.sunqian.fs.net.tcp;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.io.IOKit;

enum NullServerHandler implements TcpServerHandler {

    INST;

    @Override
    public void channelOpen(@Nonnull TcpContext context) {
    }

    @Override
    public void channelClose(@Nonnull TcpContext context) {
    }

    @Override
    public void channelRead(@Nonnull TcpContext channel) {
        IOKit.availableTo(channel.channel(), IOKit.nullOutputStream());
    }

    @Override
    public void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause) {
    }
}
