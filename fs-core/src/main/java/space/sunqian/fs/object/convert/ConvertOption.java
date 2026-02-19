package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.object.build.BuilderProvider;
import space.sunqian.fs.object.schema.MapParser;
import space.sunqian.fs.object.schema.ObjectParser;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Option for object conversion and data mapping.
 *
 * @author sunqian
 */
public enum ConvertOption {

    /**
     * Key of {@link #strictSourceTypeMode(boolean)}.
     * <p>
     * Option to enable strict source type mode. In strict type mode, the conversion will strictly treat the source
     * object as the specified source type.
     * <p>
     * By default, this option is disabled. In this case, if some error occurs when parsing the source type, the
     * conversion will try again with the {@link Object#getClass()} as the source type.
     */
    STRICT_SOURCE_TYPE_MODE,

    /**
     * Key of {@link #strictTargetTypeMode(boolean)}.
     * <p>
     * Option to enable strict target type mode. In strict type mode, the conversion will strictly convert the source
     * object to the target type, especially for target wildcard type and type variables.
     * <p>
     * By default, this option is disabled. In this case, the target type for wildcard and type variables will be
     * treated as their bounds type. For example, the target type of {@code ? extends String} will be treated as
     * {@code String}.
     */
    STRICT_TARGET_TYPE_MODE,

    /**
     * Key of {@link #newInstanceMode(boolean)}.
     * <p>
     * Option to enable new instance mode. In new instance mode, the conversion will always create a new instance of the
     * target type even if the target type is assignable from the source type.
     * <p>
     * By default, this option is disabled. In this case, the conversion could return the source object if the target
     * type is assignable from the source type.
     */
    NEW_INSTANCE_MODE,

    /**
     * Key of {@link #schemaParser(ObjectParser)}.
     */
    OBJECT_SCHEMA_PARSER,

    /**
     * Key of {@link #schemaParser(MapParser)}.
     */
    MAP_SCHEMA_PARSER,

    /**
     * Key of {@link #builderProvider(BuilderProvider)}.
     */
    BUILDER_PROVIDER,

    /**
     * Key of {@link #objectCopier(ObjectCopier)}.
     */
    OBJECT_COPIER,

    /**
     * Key of {@link #propertyNameMapper(NameMapper)}.
     */
    PROPERTY_NAME_MAPPER,

    /**
     * Key of {@link #ignoreProperties(Object...)}.
     */
    IGNORE_PROPERTIES,

    /**
     * Key of {@link #ignoreNull(boolean)}.
     */
    IGNORE_NULL,

    /**
     * Key of {@link #includeClass(boolean)}.
     */
    INCLUDE_CLASS,

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
     * Check whether the property is specified to ignore by the options. The related option
     * {@link #ignoreProperties(Object...)}.
     *
     * @param propertyName the property name to check
     * @param options      the options to check
     * @return {@code true} if the property is specified to ignore, {@code false} otherwise
     */
    public static boolean ignoresProperty(@Nonnull Object propertyName, @Nonnull Option<?, ?> @Nonnull [] options) {
        Object[] ignoredProperties = OptionKit.findValue(ConvertOption.IGNORE_PROPERTIES, options);
        if (ignoredProperties == null) {
            return false;
        }
        return ArrayKit.indexOf(ignoredProperties, propertyName) >= 0;
    }

    /**
     * Check whether the option specifies to enable to ignore null values. The related option
     * {@link #ignoreNull(boolean)}.
     *
     * @param options the options to check
     * @return {@code true} if the option specifies to enable to ignore null values, {@code false} otherwise
     */
    public static boolean ignoresNull(@Nonnull Option<?, ?> @Nonnull [] options) {
        return OptionKit.isEnabled(ConvertOption.IGNORE_NULL, options);
    }

    /**
     * Check whether the option specifies to enable to include class. The related option
     * {@link #includeClass(boolean)}.
     *
     * @param options the options to check
     * @return {@code true} if the option specifies to enable to include class, {@code false} otherwise
     */
    public static boolean includesClass(@Nonnull Option<?, ?> @Nonnull [] options) {
        return OptionKit.isEnabled(ConvertOption.INCLUDE_CLASS, options);
    }

    /**
     * Returns the {@link NameMapper}specified by the options. The related option
     * {@link #propertyNameMapper(NameMapper)}.
     *
     * @param options the options to check
     * @return the {@link NameMapper} specified by the options, or {@link NameMapper#keep()} if not specified
     */
    public static @Nonnull NameMapper getNameMapper(@Nonnull Option<?, ?> @Nonnull [] options) {
        NameMapper mapper = OptionKit.findValue(ConvertOption.PROPERTY_NAME_MAPPER, options);
        return mapper != null ? mapper : NameMapper.keep();
    }

    /**
     * Sets option to enable strict source type mode. In strict type mode, the conversion will strictly treat the source
     * object as the specified source type.
     * <p>
     * By default, this option is disabled. In this case, if some error occurs when parsing the source type, the
     * conversion will try again with the {@link Object#getClass()} as the source type.
     *
     * @param strictSourceType whether to enable strict source type mode
     * @return an option to specify to enable/disable strict source type mode
     */
    public static @Nonnull Option<@Nonnull ConvertOption, ?> strictSourceTypeMode(boolean strictSourceType) {
        return Option.of(STRICT_SOURCE_TYPE_MODE, strictSourceType);
    }

    /**
     * Sets option to enable new instance mode. In new instance mode, the conversion will always create a new instance
     * of the target type even if the target type is assignable from the source type.
     * <p>
     * By default, this option is disabled. In this case, the conversion could return the source object if the target
     * type is assignable from the source type.
     *
     * @param strictTargetType whether to enable strict target type mode
     * @return an option to specify to enable/disable strict target type mode
     */
    public static @Nonnull Option<@Nonnull ConvertOption, ?> strictTargetTypeMode(boolean strictTargetType) {
        return Option.of(STRICT_TARGET_TYPE_MODE, strictTargetType);
    }

    /**
     * Sets option to enable new instance mode. In new instance mode, the conversion will always create a new instance
     * of the target type even if the target type is assignable from the source type.
     * <p>
     * By default, this option is disabled. In this case, the conversion could return the source object if the target
     * type is assignable from the source type.
     *
     * @param newInstanceMode whether to enable new instance mode
     * @return an option to specify to enable/disable new instance mode
     */
    public static @Nonnull Option<@Nonnull ConvertOption, ?> newInstanceMode(boolean newInstanceMode) {
        return Option.of(NEW_INSTANCE_MODE, newInstanceMode);
    }

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
     * Returns an option to specify the {@link BuilderProvider} to generate data object during the conversion.
     * <p>
     * By default, the {@link ConvertKit#builderProvider()} is used.
     *
     * @param builderProvider the {@link BuilderProvider} to be specified
     * @return an option to specify the {@link BuilderProvider} to generate data object during the conversion
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull BuilderProvider> builderProvider(
        @Nonnull BuilderProvider builderProvider
    ) {
        return Option.of(BUILDER_PROVIDER, builderProvider);
    }

    /**
     * Returns an option to specify the {@link ObjectCopier}.
     * <p>
     * By default, the {@link ObjectCopier#defaultCopier()} is used.
     *
     * @param objectCopier the {@link ObjectCopier} to be specified
     * @return an option to specify the {@link ObjectCopier}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull ObjectCopier> objectCopier(
        @Nonnull ObjectCopier objectCopier
    ) {
        return Option.of(OBJECT_COPIER, objectCopier);
    }

    /**
     * Returns an option to specify the {@link NameMapper}.
     * <p>
     * Note that this configuration is only valid for the {@link String} type property names (both source and target),
     * and executed before the configured {@link ObjectCopier.Handler} (if any, and it means the property name received
     * by the property mapper will be mapped by the name mapper first). For property names whose type is not
     * {@link String}, such as non-{@link String} keys of a {@link Map}, this configuration will not take effect.
     * <p>
     * By default, this option is disabled.
     *
     * @param nameMapper the {@link NameMapper} to be specified
     * @return an option to specify the {@link PropertyNameMapper}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull NameMapper> propertyNameMapper(
        @Nonnull NameMapper nameMapper
    ) {
        return Option.of(PROPERTY_NAME_MAPPER, nameMapper);
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
     * Returns an option to specify whether ignores {@code null} properties from the source object in the conversion.
     * <p>
     * By default, this option is disabled.
     *
     * @param ignoreNull whether ignores {@code null} properties from the source object
     * @return an option to specify to ignore null properties from the source object in the conversion
     */
    public static @Nonnull Option<@Nonnull ConvertOption, ?> ignoreNull(boolean ignoreNull) {
        return Option.of(IGNORE_NULL, ignoreNull);
    }

    /**
     * Returns an option to specify whether includes {@code class} properties from the source object in the conversion.
     * <p>
     * By default, this option is disabled.
     *
     * @param includeClass whether includes {@code class} properties from the source object
     * @return an option to specify to include class properties from the source object in the conversion
     */
    public static @Nonnull Option<@Nonnull ConvertOption, ?> includeClass(boolean includeClass) {
        return Option.of(INCLUDE_CLASS, includeClass);
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
}
