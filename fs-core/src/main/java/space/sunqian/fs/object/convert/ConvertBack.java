package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.collect.ArrayKit;

final class ConvertBack {

    private static final @Nonnull NameMapper EMPTY_NAME_MAPPER = name -> name;

    static final @Nonnull ObjectCopier.PropertyMapper DEFAULT_PROPERTY_MAPPER = new ObjectCopier.PropertyMapper() {};

    static boolean ignored(@Nonnull Object propertyName, @Nonnull Option<?, ?> @Nonnull ... options) {
        Object[] ignoredProperties = OptionKit.findValue(ConvertOption.IGNORE_PROPERTIES, options);
        if (ignoredProperties == null) {
            return false;
        }
        return ArrayKit.indexOf(ignoredProperties, propertyName) >= 0;
    }

    static boolean ignoreNull(@Nonnull Option<?, ?> @Nonnull ... options) {
        return OptionKit.containsKey(ConvertOption.IGNORE_NULL, options);
    }

    static @Nonnull NameMapper getNameMapper(@Nonnull Option<?, ?> @Nonnull ... options) {
        NameMapper mapper = OptionKit.findValue(ConvertOption.PROPERTY_NAME_MAPPER, options);
        return mapper != null ? mapper : EMPTY_NAME_MAPPER;
    }

    private ConvertBack() {
    }
}
