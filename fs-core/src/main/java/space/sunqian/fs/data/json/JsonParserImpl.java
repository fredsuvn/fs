package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

enum JsonParserImpl implements JsonParser {
    INST;

    @Override
    public @Nonnull JsonData parse(@Nonnull InputStream input) throws IORuntimeException {
        Reader reader = new InputStreamReader(input, CharsKit.defaultCharset());
        return parse(reader);
    }

    @Override
    public @Nonnull JsonData parse(@Nonnull ReadableByteChannel channel) throws IORuntimeException {
        // compatible with JDK8
        @SuppressWarnings("CharsetObjectCanBeUsed")
        Reader reader = Channels.newReader(channel, CharsKit.defaultCharset().name());
        return parse(reader);
    }

    @Override
    public @Nonnull JsonData parse(@Nonnull Reader reader) throws IORuntimeException {
        return null;
    }
}
