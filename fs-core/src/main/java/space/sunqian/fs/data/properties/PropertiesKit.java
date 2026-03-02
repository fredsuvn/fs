package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;

/**
 * Utilities for {@code Properties}.
 *
 * @author sunqian
 */
public class PropertiesKit {

    /**
     * Returns a string formatted by {@link PropertiesFormatter#defaultFormatter()} of the given data.
     *
     * @param data the given properties data
     * @return a string formatted by {@link PropertiesFormatter#defaultFormatter()} of the given data
     */
    public static @Nonnull String toPropertiesString(@Nonnull PropertiesData data) {
        return PropertiesFormatter.defaultFormatter().format(data);
    }

    private PropertiesKit() {
    }
}
