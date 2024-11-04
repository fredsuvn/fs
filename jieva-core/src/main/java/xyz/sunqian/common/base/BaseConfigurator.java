package xyz.sunqian.common.base;

/**
 * Base interface for {@code Configurator}. The {@code Configurator} is used to set then do-final in method chaining
 * like following:
 * <pre>
 *     long l = JieCodec.base64()
 *         .inputLatin(latinChars)
 *         .output(output)
 *         .blockSize(32)
 *         .encode()
 *         .doFinal();
 * </pre>
 *
 * @param <T> actual type of {@code Configurator}
 * @author fredsuvn
 */
public interface BaseConfigurator<T extends BaseConfigurator<T>> {

    /**
     * Resets current configurations.
     *
     * @return this
     */
    T reset();
}
