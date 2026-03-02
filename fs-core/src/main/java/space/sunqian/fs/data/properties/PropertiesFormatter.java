package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.data.CharDataFormatter;

/**
 * Represents properties data formatter that formats a given properties data to formatting string.
 *
 * @author sunqian
 */
public interface PropertiesFormatter extends CharDataFormatter<PropertiesData> {

    /**
     * Returns the default formatter of properties data.
     *
     * @return the default formatter of properties data
     *
     */
    static @Nonnull PropertiesFormatter defaultFormatter() {
        return PropertiesFormatterImpl.INST;
    }
}
