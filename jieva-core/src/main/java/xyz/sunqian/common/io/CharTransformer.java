package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.nio.CharBuffer;

/**
 * This interface is used to transform the input data, typically used for {@link CharProcessor}, to do the specific data
 * transformation work.
 *
 * @author sunqian
 */
public interface CharTransformer {

    /**
     * Returns a {@link CharTransformer} wrapper that wraps the given transformer to ensure that the size of the data
     * passed to it is a specified fixed size.
     * <p>
     * The wrapper splits the input data into blocks of a specified fixed size and sequentially passes each block to the
     * given transformer. If any remaining data is insufficient to form a full block, it will be buffered and prepended
     * to the next input data in subsequent invocations. The combined data will be processed with the same
     * block-splitting logic.
     * <p>
     * Specifically, if the {@code end} flag is {@code true}, even if the remaining data is insufficient to form a full
     * block, it will still be passed to the given transformer.
     *
     * @param transformer the given transformer
     * @param size        the specified fixed size, must {@code > 0}
     * @return a {@link CharTransformer} wrapper that wraps the given transformer to ensure that the size of the data
     * passed to it is a specified fixed size
     * @throws IllegalArgumentException if the specified fixed size {@code <= 0}
     */
    static @Nonnull CharTransformer withFixedSize(
        @Nonnull CharTransformer transformer, int size
    ) throws IllegalArgumentException {
        IOChecker.checkSize(size);
        return new CharProcessorImpl.FixedSizeHandler(transformer, size);
    }

    /**
     * Returns a {@link CharTransformer} wrapper that wraps the given transformer to ensure that the size of the data
     * passed to it is a multiple of the specified size. The multiple is calculated as the largest multiple of the
     * specified size not exceeding the input length.
     * <p>
     * The wrapper truncates the input data to the calculated multiple length, and passes the truncated data to the
     * given transformer. If any residual data remains after truncation (shorter than the specified size), it will be
     * buffered and prepended to the next input data in subsequent invocations. The combined data will be processed with
     * the same truncation logic.
     * <p>
     * Specifically, if the {@code end} flag is {@code true}, even if the residual data is shorter than the specified
     * size, it will still be passed to the given transformer.
     *
     * @param transformer the given transformer
     * @param size        the specified size, must {@code > 0}
     * @return a {@link CharTransformer} wrapper that wraps the given transformer to ensure that the size of the data
     * passed to it is a multiple of the specified size
     * @throws IllegalArgumentException if the specified size {@code <= 0}
     */
    static @Nonnull CharTransformer withMultipleSize(
        @Nonnull CharTransformer transformer, int size
    ) throws IllegalArgumentException {
        IOChecker.checkSize(size);
        return new CharProcessorImpl.MultipleSizeHandler(transformer, size);
    }

    /**
     * Returns a {@link CharTransformer} wrapper that wraps the given transformer to support buffering unconsumed data.
     * <p>
     * The wrapper passes input data to the given transformer, after processing, the unconsumed data (if any) will be
     * buffered and prepended to the next input data in subsequent invocations. The combined data will be processed with
     * the same buffering logic.
     * <p>
     * Specifically, if the {@code end} flag is {@code true}, the unconsumed data (if any) will be discarded.
     *
     * @param transformer the given transformer
     * @return a {@link CharTransformer} wrapper that wraps the given transformer to support buffering unconsumed data
     */
    static @Nonnull CharTransformer withBuffered(@Nonnull CharTransformer transformer) {
        return new CharProcessorImpl.BufferedHandler(transformer);
    }

    /**
     * Returns an empty {@link CharTransformer} which does nothing but only returns the input data directly.
     *
     * @return an empty {@link CharTransformer} which does nothing but only returns the input data directly
     */
    static @Nonnull CharTransformer empty() {
        return CharProcessorImpl.EmptyHandler.INST;
    }

    /**
     * Transforms the input data, and returns the result. The input data will not be {@code null} (but may be empty),
     * and the return value can be {@code null}.
     *
     * @param data the input data
     * @param end  whether the input data is the last segment and there is no more data
     * @return the result
     * @throws Exception if any problem occurs
     */
    @Nullable
    CharBuffer transform(@Nonnull CharBuffer data, boolean end) throws Exception;
}
