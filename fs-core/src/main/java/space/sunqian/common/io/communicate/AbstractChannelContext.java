// package space.sunqian.common.io.communicate;
//
// import space.sunqian.annotations.Nonnull;
// import space.sunqian.annotations.Nullable;
// import space.sunqian.common.io.IOKit;
// import space.sunqian.common.io.IOOperator;
// import space.sunqian.common.io.IORuntimeException;
//
// import java.nio.ByteBuffer;
// import java.nio.channels.ByteChannel;
//
// /**
//  * Skeletal implementation of {@link ChannelContext} to help minimize effort in implementing {@link ChannelContext}.
//  * <p>
//  * The read and write methods of this class is based on a given {@link ByteChannel}, which also serves as the underlying
//  * channel. These two field is accessible for subclasses: {@link #channel} and {@link #bufSize}.
//  *
//  * @param <C> the type of the underlying channel
//  * @author sunqian
//  */
// public abstract class AbstractChannelContext<C extends ByteChannel> implements ChannelContext<C> {
//
//     /**
//      * The given underlying channel.
//      */
//     protected final @Nonnull C channel;
//     protected final int bufSize;
//
//     /**
//      * The {@link IOOperator} for advanced I/O operations.
//      */
//     // protected final @Nonnull IOOperator operator;
//
//     /**
//      * Constructs with the given {@link ByteChannel} and the buffer size for advanced I/O operations.
//      *
//      * @param channel the given {@link ByteChannel}
//      * @param bufSize the buffer size for advanced I/O operations
//      * @throws IllegalArgumentException if the buffer size {@code <=0}
//      */
//     protected AbstractChannelContext(@Nonnull C channel, int bufSize) throws IllegalArgumentException {
//         this.channel = channel;
//         // this.operator = IOOperator.get(bufSize);
//         this.bufSize = bufSize;
//     }
//
//     @Override
//     public byte @Nullable [] availableBytes() throws IORuntimeException {
//         //channel.
//         return IOKit.a
//     }
//
//     @Override
//     public @Nullable ByteBuffer availableBuffer() throws IORuntimeException {
//         return operator.available(channel);
//     }
//
//     @Override
//     public @Nonnull C channel() {
//         return channel;
//     }
// }
