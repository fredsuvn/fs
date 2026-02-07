package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.object.create.CreatorProvider;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.ObjectParser;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Option for object conversion and data mapping.
 *
 * @author sunqian
 */
public enum ConvertOption implements Option<ConvertOption, Object> {

    /**
     * Option to enable strict source type mode. In strict type mode, the conversion will strictly treat the source
     * object as the specified source type.
     * <p>
     * By default, this option is disabled. In this case, if some error occurs when parsing the source type, the
     * conversion will try again with the {@link Object#getClass()} as the source type.
     */
    STRICT_SOURCE_TYPE,

    /**
     * Option to enable strict target type mode. In strict type mode, the conversion will strictly convert the source
     * object to the target type, especially for target wildcard type and type variables.
     * <p>
     * By default, this option is disabled. In this case, the target type for wildcard and type variables will be
     * treated as their bounds type. For example, the target type of {@code ? extends String} will be treated as
     * {@code String}.
     */
    STRICT_TARGET_TYPE,

    /**
     * Key of {@link #schemaParser(ObjectParser)}.
     */
    OBJECT_SCHEMA_PARSER,

    /**
     * Key of {@link #schemaParser(MapParser)}.
     */
    MAP_SCHEMA_PARSER,

    /**
     * Key of {@link #creatorProvider(CreatorProvider)}.
     */
    CREATOR_PROVIDER,

    /**
     * Key of {@link #propertyCopier(PropertyCopier)}.
     */
    PROPERTY_COPIER,

    /**
     * Key of {@link #propertyNameMapper(PropertyNameMapper)}.
     */
    PROPERTY_NAME_MAPPER,

    /**
     * Key of {@link #ignoreProperties(Object...)}.
     */
    IGNORE_PROPERTIES,

    /**
     * Key of {@link #ignoreNull()}.
     */
    IGNORE_NULL,

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
     * By default, {@link ConvertKit#objectParser()} is used.
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
     * By default, {@link ConvertKit#mapParser()} is used.
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
     * Returns an option to specify the {@link CreatorProvider} to generate data object during the conversion.
     * <p>
     * By default, the {@link ConvertKit#creatorProvider()} is used.
     *
     * @param creatorProvider the {@link CreatorProvider} to be specified
     * @return an option to specify the {@link CreatorProvider} to generate data object during the conversion
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull CreatorProvider> creatorProvider(
        @Nonnull CreatorProvider creatorProvider
    ) {
        return Option.of(CREATOR_PROVIDER, creatorProvider);
    }

    /**
     * Returns an option to specify the {@link PropertyCopier}.
     * <p>
     * By default, the {@link PropertyCopier#defaultCopier()} is used.
     *
     * @param propertyCopier the {@link PropertyCopier} to be specified
     * @return an option to specify the {@link PropertyCopier}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull PropertyCopier> propertyCopier(
        @Nonnull PropertyCopier propertyCopier
    ) {
        return Option.of(PROPERTY_COPIER, propertyCopier);
    }

    /**
     * Returns an option to specify the {@link PropertyNameMapper}.
     * <p>
     * Note that this configuration is only valid for the {@link String} type property names (both source and target),
     * and executed before the configured {@link PropertyCopier.PropertyMapper} (if any, and it means the property name
     * received by the property mapper will be the mapped name by the property name mapper). For property names whose
     * type is not {@link String}, such as non-{@link String} keys of a {@link Map}, this configuration will not take
     * effect.
     * <p>
     * By default, this option is disabled.
     *
     * @param propertyNameMapper the {@link PropertyNameMapper} to be specified
     * @return an option to specify the {@link PropertyNameMapper}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull PropertyNameMapper> propertyNameMapper(
        @Nonnull PropertyNameMapper propertyNameMapper
    ) {
        return Option.of(PROPERTY_NAME_MAPPER, propertyNameMapper);
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
     * Returns an option to specify to ignore null properties from the source object. If there exists a
     * {@link PropertyCopier.PropertyMapper} in the current {@link PropertyCopier}, this option will be ignored.
     * <p>
     * By default, this option is disabled, the value of {@link #IGNORE_NULL} is {@code null}.
     *
     * @return an option to specify to ignore null properties from the source object
     */
    public static @Nonnull Option<@Nonnull ConvertOption, ?> ignoreNull() {
        return IGNORE_NULL;
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
