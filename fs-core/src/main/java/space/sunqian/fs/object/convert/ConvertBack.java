package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.collect.ArrayKit;

final class ConvertBack {

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

    static @Nonnull PropertyNameMapper getNameMapper(@Nonnull Option<?, ?> @Nonnull ... options) {
        PropertyNameMapper mapper = OptionKit.findValue(ConvertOption.PROPERTY_NAME_MAPPER, options);
        return mapper != null ? mapper : PropertyNameMapper.defaultMapper();
    }

    private ConvertBack() {
    }
}
