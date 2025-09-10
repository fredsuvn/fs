package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.option.Option;

/**
 * Provides options for data mapping.
 *
 * @author sunqian
 */
public class MappingOptions {

    /**
     * Returns an option to specify the {@link DataMapper.PropertyMapper}.
     * <p>
     * By default, all properties which is both readable and writable will be copied with their original names.
     *
     * @param propertyMapper the {@link DataMapper.PropertyMapper} to be specified
     * @return an option to specify the {@link DataMapper.PropertyMapper}
     */
    public static @Nonnull Option<@Nonnull Key, DataMapper.@Nonnull PropertyMapper> propertyMapper(
        @Nonnull DataMapper.PropertyMapper propertyMapper
    ) {
        return Option.of(Key.PROPERTY_MAPPER, propertyMapper);
    }

    /**
     * Returns an option to specify the {@link DataMapper.ExceptionHandler}.
     * <p>
     * By default, the exception will be thrown directly.
     *
     * @param exceptionHandler the {@link DataMapper.ExceptionHandler} to be specified
     * @return an option to specify the {@link DataMapper.ExceptionHandler}
     */
    public static @Nonnull Option<@Nonnull Key, DataMapper.@Nonnull ExceptionHandler> exceptionHandler(
        @Nonnull DataMapper.ExceptionHandler exceptionHandler
    ) {
        return Option.of(Key.EXCEPTION_HANDLER, exceptionHandler);
    }

    /**
     * Option key for data mapping.
     */
    public enum Key {

        /**
         * Key of {@link #propertyMapper(DataMapper.PropertyMapper)}.
         */
        PROPERTY_MAPPER,

        /**
         * Key of {@link #exceptionHandler(DataMapper.ExceptionHandler)}.
         */
        EXCEPTION_HANDLER,
        ;
    }
}
