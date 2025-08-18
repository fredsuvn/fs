package xyz.sunqian.common.net.tcp.handlers;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.trash.data.IOMisc;
import xyz.sunqian.common.net.tcp.GekTcpChannel;
import xyz.sunqian.common.net.tcp.GekTcpChannelHandler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Length based handler implementation, to split passed byte buffer in length:
 * <pre>
 *     buffer -&gt; data1data2data3..
 * </pre>
 * The returned object of {@link #onMessage(GekTcpChannel, ByteBuffer)} is {@link List}&lt;{@link ByteBuffer}&gt;, each
 * byte buffer is split in:
 * <ul>
 *     <li>
 *         Fixed length: created by {@link #LengthBasedTcpChannelHandler(int)}
 *         or {@link #LengthBasedTcpChannelHandler(int, IntFunction)},
 *         to split in fixed length with {@link IOMisc#split(ByteBuffer, int)}
 *         and make returned buffers readonly;
 *     </li>
 *     <li>
 *         Specified length: created by {@link #LengthBasedTcpChannelHandler(int, int)}
 *         or {@link #LengthBasedTcpChannelHandler(int, int, IntFunction)},
 *         to split in specified length with {@link IOMisc#split(ByteBuffer, int, int)}
 *         and make returned buffers readonly;
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public class LengthBasedTcpChannelHandler implements GekTcpChannelHandler<ByteBuffer> {

    private final int lengthOffset;
    private final int lengthSize;
    private final IntFunction<ByteBuffer> generator;

    /**
     * Constructs with given fixed length and using {@link ByteBuffer#allocate(int)} to create new buffer.
     *
     * @param length given fixed length
     */
    public LengthBasedTcpChannelHandler(int length) {
        this(length, ByteBuffer::allocate);
    }

    /**
     * Constructs with given fixed length and buffer generator.
     *
     * @param length    given fixed length
     * @param generator buffer generator, the function arguments is capacity
     */
    public LengthBasedTcpChannelHandler(int length, IntFunction<ByteBuffer> generator) {
        this.lengthOffset = length;
        this.lengthSize = -1;
        this.generator = generator;
    }

    /**
     * Constructs with given length offset, length size and using {@link ByteBuffer#allocate(int)} to create new
     * buffer.
     *
     * @param lengthOffset given length offset
     * @param lengthSize   given length size
     */
    public LengthBasedTcpChannelHandler(int lengthOffset, int lengthSize) {
        this(lengthOffset, lengthSize, ByteBuffer::allocate);
    }

    /**
     * Constructs with given length offset, length size and buffer generator.
     *
     * @param lengthOffset given length offset
     * @param lengthSize   given length size
     * @param generator    buffer generator, the function arguments is capacity
     */
    public LengthBasedTcpChannelHandler(int lengthOffset, int lengthSize, IntFunction<ByteBuffer> generator) {
        if (lengthSize != 1 && lengthSize != 2 && lengthSize != 4) {
            throw new IllegalArgumentException("lengthSize must in (1, 2, 4).");
        }
        this.lengthOffset = lengthOffset;
        this.lengthSize = lengthSize;
        this.generator = generator;
    }

    @Override
    public @Nullable Object onMessage(GekTcpChannel channel, ByteBuffer message) {
        return lengthSize == -1 ? onFixed(channel, message) : onSpecified(channel, message);
    }

    private @Nullable Object onFixed(GekTcpChannel channel, ByteBuffer message) {
        List<ByteBuffer> result = IOMisc.split(message, lengthOffset, generator);
        return result.isEmpty() ? null : asReadOnly(result);
    }

    private @Nullable Object onSpecified(GekTcpChannel channel, ByteBuffer message) {
        List<ByteBuffer> result = IOMisc.split(message, lengthOffset, lengthSize, generator);
        return result.isEmpty() ? null : asReadOnly(result);
    }

    private List<ByteBuffer> asReadOnly(List<ByteBuffer> buffers) {
        List<ByteBuffer> result = new ArrayList<>(buffers.size());
        for (ByteBuffer buffer : buffers) {
            if (buffer.isReadOnly()) {
                result.add(buffer);
            } else {
                result.add(buffer.asReadOnlyBuffer());
            }
        }
        return result;
    }
}
