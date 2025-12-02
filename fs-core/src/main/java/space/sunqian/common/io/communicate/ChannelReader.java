// package space.sunqian.common.io.communicate;
//
// import space.sunqian.annotations.Nonnull;
// import space.sunqian.annotations.Nullable;
// import space.sunqian.common.base.chars.CharsKit;
// import space.sunqian.common.io.IORuntimeException;
//
// import java.nio.ByteBuffer;
// import java.nio.channels.ReadableByteChannel;
// import java.nio.charset.Charset;
//
// /**
//  * Channel reader for IO Communication, typically used for network or IPC (Inter-Process Communication).
//  * <p>
//  * The reader holds an underlying channel to read and provides advanced read operations. If the read method of the
//  * underlying channel returns {@code -1}, it means the channel can no longer be read (for example: the TCP peer sent a
//  * {@code FIN} and entered the half-closed state). In this case, the channel can be closed to complete the close
//  * operation. If it returns {@code 0}, it indicates that all available data, for now, has been read, but the channel
//  * remains alive.
//  *
//  * @param <C> the type of the underlying channel
//  * @author sunqian
//  */
// public interface ChannelReader<C extends ReadableByteChannel> {
//
//     /**
//      * Returns all available bytes in the underlying channel, possibly empty if no data is available but the channel is
//      * still alive, or {@code null} if the channel is closed.
//      *
//      * @return the available bytes in the underlying channel, possibly empty, or {@code null} if the channel is closed.
//      * @throws IORuntimeException if an error occurs
//      */
//     byte @Nullable [] availableBytes() throws IORuntimeException;
//
//     /**
//      * Returns all available bytes as a buffer in the underlying channel, possibly empty if no data is available but the
//      * channel is still alive, or {@code null} if the channel is closed. The position of the buffer is {@code 0}.
//      *
//      * @return the available bytes as a buffer in the underlying channel, possibly empty, may be {@code null} if the
//      * channel is closed.
//      * @throws IORuntimeException if an error occurs
//      */
//     @Nullable
//     ByteBuffer availableBuffer() throws IORuntimeException;
//
//     /**
//      * Returns all available bytes as a string in the underlying channel, possibly empty if no data is available but the
//      * channel is still alive, or {@code null} if the channel is closed. The string is decoded by the
//      * {@link CharsKit#defaultCharset()}.
//      *
//      * @return the available bytes as a string in the underlying channel, possibly empty, may be {@code null} if the
//      * channel is closed.
//      * @throws IORuntimeException if an error occurs
//      */
//     default @Nullable String availableString() throws IORuntimeException {
//         return availableString(CharsKit.defaultCharset());
//     }
//
//     /**
//      * Returns all available bytes as a string in the underlying channel, possibly empty if no data is available but the
//      * channel is still alive, or {@code null} if the channel is closed. The string is decoded by the specified
//      * charset.
//      *
//      * @param charset the specified charset
//      * @return the available bytes as a string in the underlying channel, possibly empty, may be {@code null} if the
//      * channel is closed.
//      * @throws IORuntimeException if an error occurs
//      */
//     default @Nullable String availableString(@Nonnull Charset charset) throws IORuntimeException {
//         byte[] bytes = availableBytes();
//         return bytes == null ? null : new String(bytes, charset);
//     }
//
//     /**
//      * Returns the underlying channel of this reader.
//      *
//      * @return the underlying channel of this reader
//      */
//     @Nonnull
//     C channel();
// }
