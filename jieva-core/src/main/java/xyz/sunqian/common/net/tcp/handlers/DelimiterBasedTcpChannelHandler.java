package xyz.sunqian.common.net.tcp.handlers;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.IOMisc;
import xyz.sunqian.common.net.tcp.GekTcpChannel;
import xyz.sunqian.common.net.tcp.GekTcpChannelHandler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Delimiter based handler implementation, to split passed byte buffer by delimiter:
 * <pre>
 *     buffer -&gt; data|data|data|..
 * </pre>
 * The returned object of {@link #onMessage(GekTcpChannel, ByteBuffer)} is {@link List}&lt;{@link ByteBuffer}&gt;, each
 * byte buffer is split by {@link IOMisc#split(ByteBuffer, byte, IntFunction)} and make returned buffers readonly.
 *
 * @author fredsuvn
 */
public class DelimiterBasedTcpChannelHandler implements GekTcpChannelHandler<ByteBuffer> {

    private final byte delimiter;
    private final IntFunction<ByteBuffer> generator;

    /**
     * Constructs with given delimiter and using {@link ByteBuffer#allocate(int)} to create new buffer.
     *
     * @param delimiter given delimiter
     */
    public DelimiterBasedTcpChannelHandler(byte delimiter) {
        this(delimiter, ByteBuffer::allocate);
    }

    /**
     * Constructs with given delimiter and buffer generator.
     *
     * @param delimiter given delimiter
     * @param generator buffer generator, the function arguments is capacity
     */
    public DelimiterBasedTcpChannelHandler(byte delimiter, IntFunction<ByteBuffer> generator) {
        this.delimiter = delimiter;
        this.generator = generator;
    }

    @Override
    public @Nullable Object onMessage(GekTcpChannel channel, ByteBuffer message) {
        List<ByteBuffer> result = IOMisc.split(message, delimiter, generator);
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
