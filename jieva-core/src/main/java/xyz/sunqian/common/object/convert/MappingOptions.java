package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
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
     * Returns an option to ignore copy properties with {@code null} values. If a {@link DataMapper.PropertyMapper} is
     * set, this option becomes invalid.
     * <p>
     * This option is not available by default.
     *
     * @return an option to ignore copy properties with {@code null} values
     */
    public static @Nonnull MappingOptions.IgnoreNull ignoreNull() {
        return IgnoreNull.SINGLETON;
    }

    /**
     * Returns an option to ignore copy properties with the specified property names (or key for map).
     * <p>
     * By default, all readable properties will be attempted to be copied.
     *
     * @param ignoredProperties the names (or key) of ignored properties
     * @return an option to ignore copy properties with the specified property names (or key for map)
     */
    public static @Nonnull Option<@Nonnull Key, @Nonnull Object @Nonnull []> ignoreProperties(
        @Nonnull Object @Nonnull ... ignoredProperties
    ) {
        return Option.of(Key.IGNORE_PROPERTIES, ignoredProperties);
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

        /**
         * Key of {@link #ignoreNull()}.
         */
        IGNORE_NULL_PROPERTIES,

        /**
         * Key of {@link #ignoreProperties(Object...)}.
         */
        IGNORE_PROPERTIES,
        ;
    }

    /**
     * Mapping option to ignore copy properties with {@code null} values.
     */
    public static final class IgnoreNull implements Option<Key, Object> {

        private static final @Nonnull MappingOptions.IgnoreNull SINGLETON = new IgnoreNull();

        @Override
        public @Nonnull Key key() {
            return Key.IGNORE_NULL_PROPERTIES;
        }

        @Override
        public @Nullable Object value() {
            return null;
        }
    }
}
