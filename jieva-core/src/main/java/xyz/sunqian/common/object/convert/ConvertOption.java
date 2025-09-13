package xyz.sunqian.common.object.convert;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.option.Option;
import xyz.sunqian.common.base.time.TimeFormatter;
import xyz.sunqian.common.io.IOOperator;
import xyz.sunqian.common.object.data.MapSchemaParser;
import xyz.sunqian.common.object.data.ObjectSchemaParser;

import java.nio.charset.Charset;

/**
 * Option for object conversion and data mapping.
 *
 * @author sunqian
 */
public enum ConvertOption implements Option<ConvertOption, Object> {

    /**
     * Option to enable strict type mode. In strict type mode, the target type for wildcard and type variables will be
     * strictly converted.
     * <p>
     * This option is disabled by default, in this case, the target type for wildcard and type variables will be
     * converted as their bounds type. For example, the target type of {@code ? extends String} will be treated as
     * {@code String}.
     */
    STRICT_TYPE_MODE,

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
     * Key of {@link #dataMapper(DataMapper)}.
     */
    DATA_MAPPER,

    /**
     * Key of {@link #builderFactory(DataBuilderFactory)}.
     */
    BUILDER_FACTORY,

    /**
     * Key of {@link #ioOperator(IOOperator)}.
     */
    IO_OPERATOR,

    /**
     * Key of {@link #charset(Charset)}.
     */
    CHARSET,

    /**
     * Key of {@link #timeFormatter(TimeFormatter)}.
     */
    TIME_FORMATTER,
    ;

    /**
     * Returns an option to specify the object schema parser.
     * <p>
     * By default, {@link ObjectSchemaParser#defaultParser()} is used.
     *
     * @param schemaParser the specified object schema parser
     * @return an option to specify the object schema parser
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull ObjectSchemaParser> schemaParser(
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
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull MapSchemaParser> schemaParser(
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
    public static @Nonnull Option<@Nonnull ConvertOption, DataMapper.@Nonnull PropertyMapper> propertyMapper(
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
    public static @Nonnull Option<@Nonnull ConvertOption, DataMapper.@Nonnull ExceptionHandler> exceptionHandler(
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
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull Object @Nonnull []> ignoreProperties(
        @Nonnull Object @Nonnull ... ignoredProperties
    ) {
        return Option.of(IGNORE_PROPERTIES, ignoredProperties);
    }

    /**
     * Returns an option to specify the {@link DataMapper}.
     * <p>
     * By default, the {@link DataMapper#defaultMapper()} is used.
     *
     * @param dataMapper the {@link DataMapper} to be specified
     * @return an option to specify the {@link DataMapper}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull DataMapper> dataMapper(
        @Nonnull DataMapper dataMapper
    ) {
        return Option.of(DATA_MAPPER, dataMapper);
    }

    /**
     * Returns an option to specify the {@link DataBuilderFactory} to generate data object during the conversion.
     * <p>
     * By default, the {@link DataBuilderFactory#defaultFactory()} is used.
     *
     * @param builderFactory the {@link DataBuilderFactory} to be specified
     * @return an option to specify the {@link DataBuilderFactory} to generate data object during the conversion
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull DataBuilderFactory> builderFactory(
        @Nonnull DataBuilderFactory builderFactory
    ) {
        return Option.of(BUILDER_FACTORY, builderFactory);
    }

    /**
     * Returns an option to specify the {@link IOOperator} if needed.
     * <p>
     * By default, the {@link IOOperator#defaultOperator()} is used.
     *
     * @param ioOperator the {@link IOOperator} to be specified
     * @return an option to specify the {@link IOOperator} if needed
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull IOOperator> ioOperator(
        @Nonnull IOOperator ioOperator
    ) {
        return Option.of(IO_OPERATOR, ioOperator);
    }

    /**
     * Returns an option to specify the {@link Charset} if needed.
     * <p>
     * By default, the {@link CharsKit#defaultCharset()} is used.
     *
     * @param charset the {@link Charset} to be specified
     * @return an option to specify the {@link Charset} if needed
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull Charset> charset(
        @Nonnull Charset charset
    ) {
        return Option.of(CHARSET, charset);
    }

    /**
     * Returns an option to specify the {@link TimeFormatter} if needed.
     * <p>
     * By default, the {@link TimeFormatter#defaultFormatter()} is used.
     *
     * @param timeFormatter the {@link TimeFormatter} to be specified
     * @return an option to specify the {@link TimeFormatter} if needed
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull TimeFormatter> timeFormatter(
        @Nonnull TimeFormatter timeFormatter
    ) {
        return Option.of(TIME_FORMATTER, timeFormatter);
    }

    @Override
    public @Nonnull ConvertOption key() {
        return this;
    }

    @Override
    public @Nullable Object value() {
        return null;
    }
}
