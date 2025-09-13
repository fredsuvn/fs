package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.object.data.MapSchemaParser;
import xyz.sunqian.common.object.data.ObjectSchemaParser;

/**
 * Option for data mapping.
 *
 * @author sunqian
 */
public enum MappingOption implements Option<MappingOption, Object> {

    /**
     * Key of {@link #schemaParser(ObjectSchemaParser)}.
     */
    OBJECT_SCHEMA_PARSER,

    /**
     * Key of {@link #schemaParser(MapSchemaParser)}.
     */
    MAP_SCHEMA_PARSER,

    /**
     * Key of {@link #propertyMapper(DataMapper.PropertyMapper)}.
     */
    PROPERTY_MAPPER,

    /**
     * Key of {@link #exceptionHandler(DataMapper.ExceptionHandler)}.
     */
    EXCEPTION_HANDLER,

    /**
     * Option to ignore copy properties with {@code null} values. If a {@link #PROPERTY_MAPPER} is set, this option
     * becomes invalid. This option is disabled by default.
     */
    IGNORE_NULL,

    /**
     * Key of {@link #ignoreProperties(Object...)}.
     */
    IGNORE_PROPERTIES,

    /**
     * Option to enable strict type mode. In strict type mode, wildcard types and type variables will be considered as
     * {@link Object}.class. This option is disabled by default.
     */
    STRICT_TYPE,
    ;

    /**
     * Returns an option to specify the object schema parser.
     * <p>
     * By default, {@link ObjectSchemaParser#defaultParser()} is used.
     *
     * @param schemaParser the specified object schema parser
     * @return an option to specify the object schema parser
     */
    public static @Nonnull Option<@Nonnull MappingOption, @Nonnull ObjectSchemaParser> schemaParser(
        @Nonnull ObjectSchemaParser schemaParser
    ) {
        return Option.of(OBJECT_SCHEMA_PARSER, schemaParser);
    }

    /**
     * Returns an option to specify the map schema parser.
     * <p>
     * By default, {@link MapSchemaParser#defaultParser()} is used.
     *
     * @param schemaParser the specified map schema parser
     * @return an option to specify the map schema parser
     */
    public static @Nonnull Option<@Nonnull MappingOption, @Nonnull MapSchemaParser> schemaParser(
        @Nonnull MapSchemaParser schemaParser
    ) {
        return Option.of(MAP_SCHEMA_PARSER, schemaParser);
    }

    /**
     * Returns an option to specify the {@link DataMapper.PropertyMapper}.
     * <p>
     * By default, all properties which is both readable and writable will be copied with their original names.
     *
     * @param propertyMapper the {@link DataMapper.PropertyMapper} to be specified
     * @return an option to specify the {@link DataMapper.PropertyMapper}
     */
    public static @Nonnull Option<@Nonnull MappingOption, DataMapper.@Nonnull PropertyMapper> propertyMapper(
        @Nonnull DataMapper.PropertyMapper propertyMapper
    ) {
        return Option.of(PROPERTY_MAPPER, propertyMapper);
    }

    /**
     * Returns an option to specify the {@link DataMapper.ExceptionHandler}.
     * <p>
     * By default, the exception will be thrown directly.
     *
     * @param exceptionHandler the {@link DataMapper.ExceptionHandler} to be specified
     * @return an option to specify the {@link DataMapper.ExceptionHandler}
     */
    public static @Nonnull Option<@Nonnull MappingOption, DataMapper.@Nonnull ExceptionHandler> exceptionHandler(
        @Nonnull DataMapper.ExceptionHandler exceptionHandler
    ) {
        return Option.of(EXCEPTION_HANDLER, exceptionHandler);
    }

    /**
     * Returns an option to ignore copy properties with the specified property names (or key for map).
     * <p>
     * By default, all readable properties will be attempted to be copied.
     *
     * @param ignoredProperties the names (or key) of ignored properties
     * @return an option to ignore copy properties with the specified property names (or key for map)
     */
    public static @Nonnull Option<@Nonnull MappingOption, @Nonnull Object @Nonnull []> ignoreProperties(
        @Nonnull Object @Nonnull ... ignoredProperties
    ) {
        return Option.of(IGNORE_PROPERTIES, ignoredProperties);
    }

    @Override
    public @Nonnull MappingOption key() {
        return this;
    }

    @Override
    public @Nullable Object value() {
        return null;
    }
}
