package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.io.IORuntimeException;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

final class JsonParserImpl implements JsonParser {

    static final @Nonnull JsonParserImpl DEFAULT = new JsonParserImpl(
        ObjectConverter.defaultConverter(),
        ConvertOption.mapSchemaParser(ConvertKit.mapSchemaParser()),
        ConvertOption.objectSchemaParser(ConvertKit.objectSchemaParser()),
        ConvertOption.builderOperatorProvider(ConvertKit.builderOperatorProvider())
    );

    private final @Nonnull ObjectConverter converter;
    private final @Nonnull Option<?, ?> @Nonnull [] options;

    JsonParserImpl(@Nonnull ObjectConverter converter, @Nonnull Option<?, ?> @Nonnull ... options) {
        this.converter = converter;
        this.options = options;
    }

    @Override
    public @Nonnull JsonData parse(@Nonnull InputStream input) throws IORuntimeException {
        return null;
    }

    @Override
    public @Nonnull JsonData parse(@Nonnull ReadableByteChannel channel) throws IORuntimeException {
        return null;
    }

    // @Override
    // public @Nonnull JsonData parse(
    //     @Nonnull InputStream input
    // ) throws IORuntimeException {
    //     return parse(IOKit.newReader(input, charset));
    // }
    //
    // @Override
    // public @Nonnull JsonData parse(@Nonnull Reader reader) throws IORuntimeException {
    //     throw new IORuntimeException();
    // }

    // @Override
    // public @Nonnull JsonData parse(@Nonnull Reader reader) throws IORuntimeException {
    //     ObjectConverter
    //     try {
    //         JsonTokenizer tokenizer = new JsonTokenizer(reader);
    //         JsonToken token = tokenizer.nextToken();
    //         if (token == null) {
    //             throw new JsonDataException("Empty JSON document");
    //         }
    //         JsonData result = parseValue(tokenizer, token);
    //         // Ensure there's no more content after the main JSON value
    //         JsonToken nextToken = tokenizer.nextToken();
    //         if (nextToken != null && nextToken.type != JsonTokenType.EOF) {
    //             throw new JsonDataException("Unexpected content after end of JSON");
    //         }
    //         return result;
    //     } catch (IOException e) {
    //         throw new IORuntimeException(e);
    //     }
    // }
}