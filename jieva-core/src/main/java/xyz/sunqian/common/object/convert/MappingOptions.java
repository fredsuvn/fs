package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.base.value.Val;
import xyz.sunqian.common.object.data.DataSchema;

import java.util.Map;

/**
 * Provides options for copying object properties.
 *
 * @author sunqian
 */
public class MappingOptions {

    /**
     * Returns an option to specify the {@link PropertyMapper}.
     * <p>
     * By default, all properties which is both readable and writable will be copied with their original names.
     *
     * @param propertyMapper the {@link PropertyMapper} to be specified
     * @return an option to specify the {@link PropertyMapper}
     */
    public static @Nonnull Option<Key, @Nonnull PropertyMapper> propertyMapper(
        @Nonnull PropertyMapper propertyMapper
    ) {
        return Option.of(Key.PROPERTY_MAPPER, propertyMapper);
    }

    /**
     * Returns an option to specify the {@link ExceptionHandler}.
     * <p>
     * By default, the exception will be thrown directly.
     *
     * @param exceptionHandler the {@link ExceptionHandler} to be specified
     * @return an option to specify the {@link ExceptionHandler}
     */
    public static @Nonnull Option<Key, @Nonnull ExceptionHandler> exceptionHandler(
        @Nonnull ExceptionHandler exceptionHandler
    ) {
        return Option.of(Key.EXCEPTION_HANDLER, exceptionHandler);
    }

    /**
     * Option key for copying properties.
     */
    public enum Key {

        /**
         * Key of {@link #propertyMapper(PropertyMapper)}.
         */
        PROPERTY_MAPPER,

        /**
         * Key of {@link #exceptionHandler(ExceptionHandler)}.
         */
        EXCEPTION_HANDLER,
        ;
    }

    /**
     * Property mapper for copying object property.
     */
    public interface PropertyMapper {

        /**
         * Maps the name and value of the source property. The returned entry's value will be copied to the destination
         * property with the name specified by the returned entry's key. If this method returns {@code null}, then the
         * source property will not be copied.
         *
         * @param name      the name of the source property to be copied
         * @param src       the source object
         * @param srcSchema the schema of the source object, may be {@code null} if the source object is a {@link Map}
         * @return the mapped name of the target property, may be {@code null} to ignore the property to copy
         */
        Map.@Nullable Entry<@Nonnull String, Object> mapName(
            @Nonnull String name, @Nonnull Object src, @Nullable DataSchema srcSchema
        );
    }

    /**
     * Exception handler for copying object property.
     */
    public interface ExceptionHandler {

        /**
         * Handles the exception thrown when copying a property.
         * <p>
         * This method can throw an exception to break the copy-properties operation, or return {@code null} to ignore
         * the copy of this property, or return an object wrapped by {@link Val} as the copied value. Note if an object
         * wrapped by {@link Val} is returned, its value will be copied, including {@code null}, and if an exception is
         * thrown here, it will not be caught.
         *
         * @param e         the exception
         * @param name      the name of the source property to be copied
         * @param src       the source object
         * @param srcSchema the schema of the source object, may be {@code null} if the source object is a {@link Map}
         * @return {@code null} to ignore the copy of this property, or an object wrapped by {@link Val} as the copied
         * value
         */
        @Nullable
        Val<Object> handle(
            @Nonnull Throwable e, @Nonnull String name, @Nonnull Object src, @Nullable DataSchema srcSchema
        );
    }
}
