// package xyz.sunqian.common.net;
//
// import xyz.sunqian.annotations.Nonnull;
// import xyz.sunqian.annotations.Nullable;
//
// /**
//  * An Implementation of {@link NetChannelHandler} that only reads and then discards all received data. Using
//  * {@link #SINGLETON} to get the singleton instance.
//  *
//  * @author sunqian
//  */
// public class NullNetChannelHandler implements NetChannelHandler {
//
//     public static final @Nonnull NullNetChannelHandler SINGLETON = new NullNetChannelHandler();
//
//     private NullNetChannelHandler() {
//     }
//
//     @Override
//     public void channelOpen(@Nonnull NetChannelContext context) {
//     }
//
//     @Override
//     public void channelClose(@Nonnull NetChannelContext context) {
//     }
//
//     @SuppressWarnings("resource")
//     @Override
//     public void channelRead(@Nonnull NetChannelContext context) {
//         context.ioChannel().availableBytes();
//     }
//
//     @Override
//     public void exceptionCaught(@Nullable NetChannelContext context, @Nonnull Throwable cause) {
//     }
// }
