package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.number.NumFormatter;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.object.builder.BuilderOperatorProvider;
import space.sunqian.fs.object.schema.MapSchemaParser;
import space.sunqian.fs.object.schema.ObjectSchemaParser;

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
     * Key of {@link #objectSchemaParser(ObjectSchemaParser)}.
     */
    OBJECT_SCHEMA_PARSER,

    /**
     * Key of {@link #mapSchemaParser(MapSchemaParser)}.
     */
    MAP_SCHEMA_PARSER,

    /**
     * Key of {@link #builderOperatorProvider(BuilderOperatorProvider)}.
     */
    BUILDER_OPERATOR_PROVIDER,

    /**
     * Key of {@link #objectCopier(ObjectCopier)}.
     */
    OBJECT_COPIER,

    /**
     * Key of {@link #nameMapper(NameMapper)}.
     */
    NAME_MAPPER,

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

    /**
     * Key of {@link #numFormatter(NumFormatter)}.
     */
    NUM_FORMATTER,
    ;

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
     * Returns whether the given options specify to enable strict source type mode. The related option configuration
     * method is {@link #strictSourceTypeMode(boolean)}.
     *
     * @param options the given options
     * @return {@code true} if the given options specify to enable strict source type mode, {@code false} otherwise
     */
    public static boolean isStrictSourceTypeMode(@Nonnull Option<?, ?> @Nonnull [] options) {
        return OptionKit.isEnabled(ConvertOption.STRICT_SOURCE_TYPE_MODE, options);
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
     * Returns whether the given options specify to enable strict target type mode. The related option configuration
     * method is {@link #strictTargetTypeMode(boolean)}.
     *
     * @param options the given options
     * @return {@code true} if the given options specify to enable strict target type mode, {@code false} otherwise
     */
    public static boolean isStrictTargetTypeMode(@Nonnull Option<?, ?> @Nonnull [] options) {
        return OptionKit.isEnabled(ConvertOption.STRICT_TARGET_TYPE_MODE, options);
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
     * Returns whether the given options specify to enable new instance mode. The related option configuration method is
     * {@link #newInstanceMode(boolean)}.
     *
     * @param options the given options
     * @return {@code true} if the given options specify to enable new instance mode, {@code false} otherwise
     */
    public static boolean isNewInstanceMode(@Nonnull Option<?, ?> @Nonnull [] options) {
        return OptionKit.isEnabled(ConvertOption.NEW_INSTANCE_MODE, options);
    }

    /**
     * Returns an option to specify the object schema parser.
     * <p>
     * By default, {@link ConvertKit#objectSchemaParser()} is used.
     *
     * @param schemaParser the specified object schema parser
     * @return an option to specify the object schema parser
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull ObjectSchemaParser> objectSchemaParser(
        @Nonnull ObjectSchemaParser schemaParser
    ) {
        return Option.of(OBJECT_SCHEMA_PARSER, schemaParser);
    }

    /**
     * Returns the specified {@link ObjectSchemaParser} from the given options, or
     * {@link ConvertKit#objectSchemaParser()} if the given options does not contain a
     * {@link ConvertOption#OBJECT_SCHEMA_PARSER}.
     *
     * @param options the given options
     * @return the specified {@link ObjectSchemaParser} from the given options, or
     * {@link ConvertKit#objectSchemaParser()} if the given options does not contain a
     * {@link ConvertOption#OBJECT_SCHEMA_PARSER}
     */
    public static @Nonnull ObjectSchemaParser getObjectSchemaParser(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Fs.nonnull(
            OptionKit.findValue(ConvertOption.OBJECT_SCHEMA_PARSER, options),
            ConvertKit.objectSchemaParser()
        );
    }

    /**
     * Returns an option to specify the map schema parser.
     * <p>
     * By default, {@link ConvertKit#mapSchemaParser()} is used.
     *
     * @param schemaParser the specified map schema parser
     * @return an option to specify the map schema parser
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull MapSchemaParser> mapSchemaParser(
        @Nonnull MapSchemaParser schemaParser
    ) {
        return Option.of(MAP_SCHEMA_PARSER, schemaParser);
    }

    /**
     * Returns the specified {@link MapSchemaParser} from the given options, or {@link ConvertKit#mapSchemaParser()} if
     * the given options does not contain a {@link ConvertOption#MAP_SCHEMA_PARSER}.
     *
     * @param options the given options
     * @return the specified {@link MapSchemaParser} from the given options, or {@link ConvertKit#mapSchemaParser()} if
     * the given options does not contain a {@link ConvertOption#MAP_SCHEMA_PARSER}
     */
    public static @Nonnull MapSchemaParser getMapSchemaParser(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Fs.nonnull(
            OptionKit.findValue(ConvertOption.MAP_SCHEMA_PARSER, options),
            ConvertKit.mapSchemaParser()
        );
    }

    /**
     * Returns an option to specify the {@link BuilderOperatorProvider} to generate data object during the conversion.
     * <p>
     * By default, the {@link ConvertKit#builderProvider()} is used.
     *
     * @param operatorProvider the {@link BuilderOperatorProvider} to be specified
     * @return an option to specify the {@link BuilderOperatorProvider} to generate data object during the conversion
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull BuilderOperatorProvider> builderOperatorProvider(
        @Nonnull BuilderOperatorProvider operatorProvider
    ) {
        return Option.of(BUILDER_OPERATOR_PROVIDER, operatorProvider);
    }

    /**
     * Returns the specified {@link BuilderOperatorProvider} from the given options, or
     * {@link ConvertKit#builderProvider()} if the given options does not contain a
     * {@link ConvertOption#BUILDER_OPERATOR_PROVIDER}.
     *
     * @param options the given options
     * @return the specified {@link BuilderOperatorProvider} from the given options, or
     * {@link ConvertKit#builderProvider()} if the given options does not contain a
     * {@link ConvertOption#BUILDER_OPERATOR_PROVIDER}
     */
    public static @Nonnull BuilderOperatorProvider getBuilderProvider(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Fs.nonnull(
            OptionKit.findValue(ConvertOption.BUILDER_OPERATOR_PROVIDER, options),
            ConvertKit.builderProvider()
        );
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
     * Returns the specified {@link ObjectCopier} from the given options, or {@link ObjectCopier#defaultCopier()} if the
     * given options does not contain a {@link ConvertOption#OBJECT_COPIER}.
     *
     * @param options the given options
     * @return the specified {@link ObjectCopier} from the given options, or {@link ObjectCopier#defaultCopier()} if the
     * given options does not contain a {@link ConvertOption#OBJECT_COPIER}
     */
    public static @Nonnull ObjectCopier getObjectCopier(@Nonnull Option<?, ?> @Nonnull ... options) {
        return Fs.nonnull(
            OptionKit.findValue(ConvertOption.OBJECT_COPIER, options),
            ObjectCopier.defaultCopier()
        );
    }

    /**
     * Returns an option to specify the {@link NameMapper}.
     * <p>
     * Note that this configuration is only valid for the {@link String} type names, mainly for the property names (both
     * source and target), and executed before the configured {@link ObjectCopier.Handler} (if any, and it means the
     * property name received by the property mapper will be mapped by the name mapper first). For the property names
     * whose type is not {@link String}, such as non-{@link String} keys of a {@link Map}, this configuration will not
     * take effect.
     * <p>
     * By default, this option is disabled.
     *
     * @param nameMapper the {@link NameMapper} to be specified
     * @return an option to specify the {@link NameMapper}
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull NameMapper> nameMapper(
        @Nonnull NameMapper nameMapper
    ) {
        return Option.of(NAME_MAPPER, nameMapper);
    }

    /**
     * Returns the {@link NameMapper}specified by the options. The related option {@link #nameMapper(NameMapper)}.
     *
     * @param options the options to check
     * @return the {@link NameMapper} specified by the options, or {@link NameMapper#keep()} if not specified
     */
    public static @Nonnull NameMapper getNameMapper(@Nonnull Option<?, ?> @Nonnull [] options) {
        NameMapper mapper = OptionKit.findValue(ConvertOption.NAME_MAPPER, options);
        return mapper != null ? mapper : NameMapper.keep();
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
     * Check whether the property is specified to ignore by the given options. The related option
     * {@link #ignoreProperties(Object...)}.
     *
     * @param propertyName the property name to check
     * @param options      the given options
     * @return {@code true} if the property is specified to ignore, {@code false} otherwise
     */
    public static boolean isIgnoreProperty(@Nonnull Object propertyName, @Nonnull Option<?, ?> @Nonnull [] options) {
        Object[] ignoredProperties = OptionKit.findValue(ConvertOption.IGNORE_PROPERTIES, options);
        if (ignoredProperties == null) {
            return false;
        }
        return ArrayKit.indexOf(ignoredProperties, propertyName) >= 0;
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
     * Check whether the given option specifies to enable to ignore null values. The related option
     * {@link #ignoreNull(boolean)}.
     *
     * @param options the given options to check
     * @return {@code true} if the option specifies to enable to ignore null values, {@code false} otherwise
     */
    public static boolean isIgnoreNull(@Nonnull Option<?, ?> @Nonnull [] options) {
        return OptionKit.isEnabled(ConvertOption.IGNORE_NULL, options);
    }

    /**
     * Returns an option to specify whether includes {@code class} properties, which from {@link Object#getClass()}, for
     * the source object in the conversion.
     * <p>
     * By default, this option is disabled.
     *
     * @param includeClass whether includes {@code class} properties, which from {@link Object#getClass()}, for the
     *                     source object
     * @return an option to specify to include {@code class} properties, which from {@link Object#getClass()}, for the
     * source object in the conversion
     */
    public static @Nonnull Option<@Nonnull ConvertOption, ?> includeClass(boolean includeClass) {
        return Option.of(INCLUDE_CLASS, includeClass);
    }

    /**
     * Check whether the given option specifies to enable to include {@code class} properties, which from
     * {@link Object#getClass()}. The related option configuration method is {@link #includeClass(boolean)}.
     *
     * @param options the given options to check
     * @return {@code true} if the option specifies to enable to include {@code class} properties, {@code false}
     * otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isIncludeClass(@Nonnull Option<?, ?> @Nonnull [] options) {
        return OptionKit.isEnabled(ConvertOption.INCLUDE_CLASS, options);
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
     * Returns the specified {@link IOOperator} from the given options, or {@link IOOperator#defaultOperator()} if the
     * given options does not contain a {@link ConvertOption#IO_OPERATOR}.
     *
     * @param options the given options
     * @return the specified {@link IOOperator} from the given options, or {@link IOOperator#defaultOperator()} if the
     * given options does not contain a {@link ConvertOption#IO_OPERATOR}
     */
    public static @Nonnull IOOperator getIOOperator(@Nonnull Option<?, ?> @Nonnull [] options) {
        return Fs.nonnull(
            OptionKit.findValue(ConvertOption.IO_OPERATOR, options),
            IOOperator.defaultOperator()
        );
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
     * Returns the specified {@link Charset} from the given options, or {@link CharsKit#defaultCharset()} if the given
     * options does not contain a {@link ConvertOption#CHARSET}.
     *
     * @param options the given options
     * @return the specified {@link Charset} from the given options, or {@link CharsKit#defaultCharset()} if the given
     * options does not contain a {@link ConvertOption#CHARSET}
     */
    public static @Nonnull Charset getCharset(@Nonnull Option<?, ?> @Nonnull [] options) {
        return Fs.nonnull(
            OptionKit.findValue(ConvertOption.CHARSET, options),
            CharsKit.defaultCharset()
        );
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

    /**
     * Returns the specified {@link DateFormatter} from the given options, or {@link DateFormatter#defaultFormatter()}
     * if the given options does not contain a {@link ConvertOption#DATE_FORMATTER}.
     *
     * @param options the given options
     * @return the specified {@link DateFormatter} from the given options, or {@link DateFormatter#defaultFormatter()}
     * if the given options does not contain a {@link ConvertOption#DATE_FORMATTER}
     */
    public static @Nonnull DateFormatter getDateFormatter(@Nonnull Option<?, ?> @Nonnull [] options) {
        return Fs.nonnull(
            OptionKit.findValue(ConvertOption.DATE_FORMATTER, options),
            DateFormatter.defaultFormatter()
        );
    }

    /**
     * Returns an option to specify the {@link NumFormatter} if needed.
     * <p>
     * By default, no {@link NumFormatter} is used.
     *
     * @param numFormatter the {@link NumFormatter} to be specified
     * @return an option to specify the {@link NumFormatter} if needed
     */
    public static @Nonnull Option<@Nonnull ConvertOption, @Nonnull NumFormatter> numFormatter(
        @Nonnull NumFormatter numFormatter
    ) {
        return Option.of(NUM_FORMATTER, numFormatter);
    }

    /**
     * Returns the specified {@link NumFormatter} from the given options, or {@link NumFormatter#common()} if the given
     * options does not contain a {@link ConvertOption#NUM_FORMATTER}.
     *
     * @param options the given options
     * @return the specified {@link NumFormatter} from the given options, or {@link NumFormatter#common()} if the given
     * options does not contain a {@link ConvertOption#NUM_FORMATTER}
     */
    public static @Nonnull NumFormatter getNumFormatter(@Nonnull Option<?, ?> @Nonnull [] options) {
        return Fs.nonnull(
            OptionKit.findValue(ConvertOption.NUM_FORMATTER, options),
            NumFormatter.common()
        );
    }
}
