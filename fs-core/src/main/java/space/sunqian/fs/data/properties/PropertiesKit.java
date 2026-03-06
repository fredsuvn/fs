package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;

import java.util.Properties;

/**
 * Utilities for {@code Properties}.
 *
 * @author sunqian
 */
public class PropertiesKit {

    /**
     * Wraps the given {@link Properties} object into a {@link PropertiesData} object.
     *
     * @param properties the given {@link Properties} object
     * @return the wrapped {@link PropertiesData} object
     */
    public static @Nonnull PropertiesData wrap(@Nonnull Properties properties) {
        return new PropertiesData() {

            @Override
            public @Nonnull Properties asProperties() {
                return properties;
            }

            @Override
            public @Nonnull String toString() {
                return properties.toString();
            }
        };
    }

    private PropertiesKit() {
    }
}
