package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.data.DataParsingException;
import space.sunqian.fs.io.IOKit;

import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;

enum PropertiesParserImpl implements PropertiesParser {
    INST;

    @Override
    public @Nonnull PropertiesData parse(@Nonnull InputStream input) throws DataParsingException {
        Reader reader = IOKit.newReader(input, CharsKit.defaultCharset());
        return parse(reader);
    }

    @Override
    public @Nonnull PropertiesData parse(@Nonnull ReadableByteChannel channel) throws DataParsingException {
        // compatible with JDK8
        @SuppressWarnings("CharsetObjectCanBeUsed")
        Reader reader = Channels.newReader(channel, CharsKit.defaultCharset().name());
        return parse(reader);
    }

    @Override
    public @Nonnull PropertiesData parse(@Nonnull Reader reader) throws DataParsingException {
        Properties properties = new Properties();
        Fs.uncheck(() -> properties.load(reader), DataParsingException::new);
        return PropertiesKit.wrap(properties);
    }
}
