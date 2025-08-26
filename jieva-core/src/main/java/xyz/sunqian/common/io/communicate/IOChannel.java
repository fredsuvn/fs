package xyz.sunqian.common.io.communicate;

import java.nio.channels.ByteChannel;

/**
 * Channel for IO Communication, typically used for network or IPC (Inter-Process Communication).
 * <p>
 * IOChannel extends the {@link ByteChannel}, {@link InChannel} and {@link OutChannel}, and provides more advanced
 * methods for reading and writing. For its read methods, if the number of bytes read is {@code -1}, means the channel
 * is closed; if is {@code 0}, means all available data has been read but the channel is still alive.
 * <p>
 * There is a skeletal implementation: {@link AbstractIOChannel}, which can help implement this interface with minimal
 * effort.
 *
 * @author sunqian
 */
public interface IOChannel extends ByteChannel, InChannel, OutChannel {
}
