package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.object.ObjectCreatorProvider;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.ObjectParser;

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
     * Key of {@link #schemaParser(ObjectParser)}.
     */
    OBJECT_SCHEMA_PARSER,

    /**
     * Key of {@link #schemaParser(MapParser)}.
     */
    MAP_SCHEMA_PARSER,

    /**
     * Key of {@link #propertyMapper(PropertyMapper)}.
     */
    PROPERTY_MAPPER,

    /**
     * Key of {@link #exceptionHandler(ObjectCopier.ExceptionHandler)}.
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
     * Key of {@link #dataMapper(ObjectCopier)}.
     */
    DATA_MAPPER,

    /**
     * Key of {@link #creatorProvider(ObjectCreatorProvider)}.
     */
    CREATOR_PROVIDER,

    /**
     * Key of {@link #ioOperator(IOOperator)}.
     */
    IO_OPERATOR,

    /**
     * Key of {@link #charset(Charset)}.
     */
    CHARSET,

    /**
     * Key of {@link #dateFormatter(DateFormatter)}.
     */
    DATE_FORMATTER,
    ;

    /**
     * Returns an option to specify the object schema parser.
     * <p>
     * By default, {@link ObjectParser#defaultParser()} is used.
     *
     * @param schemaParser the specified object schema parser
     * @return an option to specify the object schema parser
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull ObjectParser> schemaParser(
        @Nonnull ObjectParser schemaParser
    ) {
        return Option.of(OBJECT_SCHEMA_PARSER, schemaParser);
    }

    /**
     * Returns an option to specify the map schema parser.
     * <p>
     * By default, {@link MapParser#defaultParser()} is used.
     *
     * @param schemaParser the specified map schema parser
     * @return an option to specify the map schema parser
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull MapParser> schemaParser(
        @Nonnull MapParser schemaParser
    ) {
        return Option.of(MAP_SCHEMA_PARSER, schemaParser);
    }

    /**
     * Returns an option to specify the {@link PropertyMapper}.
     * <p>
     * By default, all properties which is both readable and writable will be copied with their original names.
     *
     * @param propertyMapper the {@link PropertyMapper} to be specified
     * @return an option to specify the {@link PropertyMapper}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull PropertyMapper> propertyMapper(
        @Nonnull PropertyMapper propertyMapper
    ) {
        return Option.of(PROPERTY_MAPPER, propertyMapper);
    }

    /**
     * Returns an option to specify the {@link ObjectCopier.ExceptionHandler}.
     * <p>
     * By default, the exception will be thrown directly.
     *
     * @param exceptionHandler the {@link ObjectCopier.ExceptionHandler} to be specified
     * @return an option to specify the {@link ObjectCopier.ExceptionHandler}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, ObjectCopier.@Nonnull ExceptionHandler> exceptionHandler(
        @Nonnull ObjectCopier.ExceptionHandler exceptionHandler
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
     * Returns an option to specify the {@link ObjectCopier}.
     * <p>
     * By default, the {@link ObjectCopier#defaultCopier()} is used.
     *
     * @param objectCopier the {@link ObjectCopier} to be specified
     * @return an option to specify the {@link ObjectCopier}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull ObjectCopier> dataMapper(
        @Nonnull ObjectCopier objectCopier
    ) {
        return Option.of(DATA_MAPPER, objectCopier);
    }

    /**
     * Returns an option to specify the {@link ObjectCreatorProvider} to generate data object during the conversion.
     * <p>
     * By default, the {@link ObjectCreatorProvider#defaultProvider()} is used.
     *
     * @param creatorFactory the {@link ObjectCreatorProvider} to be specified
     * @return an option to specify the {@link ObjectCreatorProvider} to generate data object during the conversion
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull ObjectCreatorProvider> creatorProvider(
        @Nonnull ObjectCreatorProvider creatorFactory
    ) {
        return Option.of(CREATOR_PROVIDER, creatorFactory);
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
     * Returns an option to specify the {@link DateFormatter} if needed.
     * <p>
     * By default, the {@link DateFormatter#defaultFormatter()} is used.
     *
     * @param dateFormatter the {@link DateFormatter} to be specified
     * @return an option to specify the {@link DateFormatter} if needed
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull DateFormatter> dateFormatter(
        @Nonnull DateFormatter dateFormatter
    ) {
        return Option.of(DATE_FORMATTER, dateFormatter);
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
