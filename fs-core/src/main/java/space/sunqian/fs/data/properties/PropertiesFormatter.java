package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.data.ByteDataFormatter;
import space.sunqian.fs.data.CharDataFormatter;

/**
 * Represents properties data formatter that formats a given properties data. By default, it uses
 * {@link CharsKit#defaultCharset()} to formats the data in bytes.
 *
 * @author sunqian
 */
public interface PropertiesFormatter extends ByteDataFormatter<PropertiesData>, CharDataFormatter<PropertiesData> {

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
