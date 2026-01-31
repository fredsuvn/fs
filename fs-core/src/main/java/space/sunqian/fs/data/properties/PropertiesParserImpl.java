package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Properties;

enum PropertiesParserImpl implements PropertiesParser {
    INST;

    @Override
    public @Nonnull PropertiesData parse(
        @Nonnull InputStream input, @Nonnull Charset charset
    ) throws IORuntimeException {
        return parse(IOKit.newReader(input, charset));
    }

    @Override
    public @Nonnull PropertiesData parse(@Nonnull Reader reader) throws IORuntimeException {
        Properties properties = new Properties();
        Fs.uncheck(() -> properties.load(reader), IORuntimeException::new);
        return wrap(properties);
    }
}
