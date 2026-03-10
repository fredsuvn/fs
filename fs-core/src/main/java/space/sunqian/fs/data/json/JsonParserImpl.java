package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.value.Var;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        try {
            Var<Object> parsed = Var.of(null);
            parseJson(reader, parsed, 0, true);
            Object result = parsed.get();
            if (result == null) {
                return JsonData.ofNull();
            }
            if (result instanceof String) {
                return JsonData.ofString((String) result);
            }
            if (result instanceof Boolean) {
                return JsonData.ofBoolean((Boolean) result);
            }
            if (result instanceof Number) {
                return JsonData.ofNumber((Number) result);
            }
            if (result instanceof List<?>) {
                return JsonData.ofList(Fs.as(result));
            }
            return JsonData.ofMap(Fs.as(result));
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    private int parseJson(
        @Nonnull Reader reader, @Nonnull Var<Object> out, final int lastIndex, boolean toEnd
    ) throws Exception {
        int hasRead = 0;
        Object result = null;
        int i;
        while ((i = reader.read()) != -1) {
            hasRead++;
            char c = (char) i;
            if (Character.isWhitespace(c)) {
                continue;
            }
            switch (c) {
                case 'n':
                    hasRead += parseNull(reader, lastIndex + hasRead);
                    break;
                case 't':
                    hasRead += parseTrue(reader, lastIndex + hasRead);
                    result = true;
                    break;
                case 'f':
                    hasRead += parseFalse(reader, lastIndex + hasRead);
                    result = false;
                    break;
                case '\"': {
                    StringBuilder builder = new StringBuilder();
                    hasRead += parseString(reader, builder, lastIndex + hasRead);
                    result = builder.toString();
                    break;
                }
                case '{': {
                    Map<String, Object> builder = new LinkedHashMap<>();
                    hasRead += parseObject(reader, builder, lastIndex + hasRead);
                    result = builder;
                    break;
                }
                case '[': {
                    List<Object> builder = new ArrayList<>();
                    hasRead += parseArray(reader, builder, lastIndex + hasRead);
                    result = builder;
                    break;
                }
                default:
                    throw new JsonParsingDataException(lastIndex + hasRead, String.valueOf(c), null);
            }
        }
        if (toEnd) {
            hasRead += skipToEof(reader, lastIndex + hasRead);
        }
        out.set(result);
        return hasRead;
    }

    private int parseObject(
        @Nonnull Reader reader, @Nonnull Map<@Nonnull String, @Nullable Object> builder, int lastIndex
    ) throws Exception {
        int hasRead = 0;
        boolean first = true;
        int i;
        while ((i = reader.read()) != -1) {
            hasRead++;
            char c = (char) i;
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == '\"') {
                StringBuilder keyBuilder = new StringBuilder();
                hasRead += parseString(reader, keyBuilder, lastIndex + hasRead);
                hasRead += skipToChar(reader, ':', lastIndex + hasRead);
                Var<Object> json = Var.of(null);
                hasRead += parseJson(reader, json, lastIndex + hasRead, false);
                builder.put(keyBuilder.toString(), json.get());
                first = false;
                continue;
            }
            if (c == ',') {
                if (!first) {
                    continue;
                }
            }
            if (c == '}') {
                return hasRead;
            }
            throw new JsonParsingDataException(lastIndex + hasRead, String.valueOf(c), null);
        }
        throw new JsonParsingDataException(lastIndex + hasRead + 1, null, null);
    }

    private int parseArray(
        @Nonnull Reader reader, @Nonnull List<@Nullable Object> builder, int lastIndex
    ) throws Exception {
        int hasRead = 0;
        PARSING:
        while (true) {
            Var<Object> json = Var.of(null);
            hasRead += parseJson(reader, json, lastIndex + hasRead, false);
            builder.add(json.get());
            int i;
            while ((i = reader.read()) != -1) {
                hasRead++;
                char c = (char) i;
                if (Character.isWhitespace(c)) {
                    continue;
                }
                if (c == ',') {
                    continue PARSING;
                }
                if (c == ']') {
                    return hasRead;
                }
                throw new JsonParsingDataException(lastIndex + hasRead, String.valueOf(c), null);
            }
            throw new JsonParsingDataException(lastIndex + hasRead + 1, null, null);
        }
    }

    private int parseNull(@Nonnull Reader reader, int lastIndex) throws Exception {
        nextChar(reader, 'u', lastIndex);
        nextChar(reader, 'l', lastIndex + 1);
        nextChar(reader, 'l', lastIndex + 2);
        return 3;
    }

    private int parseTrue(@Nonnull Reader reader, int lastIndex) throws Exception {
        nextChar(reader, 'r', lastIndex);
        nextChar(reader, 'u', lastIndex + 1);
        nextChar(reader, 'e', lastIndex + 2);
        return 3;
    }

    private int parseFalse(@Nonnull Reader reader, int lastIndex) throws Exception {
        nextChar(reader, 'a', lastIndex);
        nextChar(reader, 'l', lastIndex + 1);
        nextChar(reader, 's', lastIndex + 2);
        nextChar(reader, 'e', lastIndex + 3);
        return 4;
    }

    // private int parseNumber(
    //     @Nonnull Reader reader, char firstChar, @Nonnull Var<@Nonnull Object> out, final int lastIndex
    // ) throws Exception {
    //     int hasRead = 0;
    //     boolean isNegative = firstChar == '-';
    //
    //     int i;
    //     while ((i = reader.read()) != -1) {
    //         hasRead++;
    //         char c = (char) i;
    //         switch (c) {
    //             case '\"':
    //                 return hasRead;
    //             case '\\':
    //                 hasRead += parseEscape(reader, builder, lastIndex + hasRead);
    //             default:
    //                 builder.append(c);
    //         }
    //     }
    //     throw new JsonParsingDataException(lastIndex + hasRead + 1, null, "\"");
    // }

    private int parseString(
        @Nonnull Reader reader, @Nonnull StringBuilder builder, final int lastIndex
    ) throws Exception {
        int hasRead = 0;
        int i;
        while ((i = reader.read()) != -1) {
            hasRead++;
            char c = (char) i;
            switch (c) {
                case '\"':
                    return hasRead;
                case '\\':
                    hasRead += parseEscape(reader, builder, lastIndex + hasRead);
                default:
                    builder.append(c);
            }
        }
        throw new JsonParsingDataException(lastIndex + hasRead + 1, null, "\"");
    }

    private int parseEscape(
        @Nonnull Reader reader, @Nonnull StringBuilder builder, final int lastIndex
    ) throws Exception {
        int i = reader.read();
        if (i != -1) {
            char c = (char) i;
            switch (c) {
                case '\"':
                case '\\':
                    builder.append(c);
                    return 1;
                case 'r':
                    builder.append('\r');
                    return 1;
                case 'n':
                    builder.append('\n');
                    return 1;
                case 't':
                    builder.append('\t');
                    return 1;
                case 'b':
                    builder.append('\b');
                    return 1;
                case 'f':
                    builder.append('\f');
                    return 1;
                case 'u':
                    parseUnicode(reader, builder, lastIndex + 1);
                    return 5;
                default:
                    throw new JsonParsingDataException(lastIndex + 1, String.valueOf(c), null);
            }
        }
        throw new JsonParsingDataException(lastIndex + 1, null, null);
    }

    private void parseUnicode(
        @Nonnull Reader reader, @Nonnull StringBuilder builder, final int lastIndex
    ) throws Exception {
        char c1 = nextChar(reader, lastIndex);
        char c2 = nextChar(reader, lastIndex + 1);
        char c3 = nextChar(reader, lastIndex + 2);
        char c4 = nextChar(reader, lastIndex + 3);
        builder.append(CharsKit.unicodeToChar(c1, c2, c3, c4));
    }

    private char nextChar(@Nonnull Reader reader, final int lastIndex) throws Exception {
        int i = reader.read();
        if (i == -1) {
            throw new JsonParsingDataException(lastIndex + 1, null, null);
        }
        return (char) i;
    }

    private void nextChar(@Nonnull Reader reader, char shouldBe, final int lastIndex) throws Exception {
        int i = reader.read();
        if (i == -1) {
            throw new JsonParsingDataException(lastIndex + 1, null, String.valueOf(shouldBe));
        }
        char c = (char) i;
        if (shouldBe != c) {
            throw new JsonParsingDataException(lastIndex + 1, String.valueOf(c), String.valueOf(shouldBe));
        }
    }

    private int skipToEof(@Nonnull Reader reader, final int lastIndex) throws Exception {
        int hasRead = 0;
        int i;
        while ((i = reader.read()) != -1) {
            hasRead++;
            char c = (char) i;
            if (!Character.isWhitespace(c)) {
                throw new JsonParsingDataException(lastIndex + hasRead, String.valueOf(c), null);
            }
        }
        return hasRead;
    }

    private int skipToChar(@Nonnull Reader reader, char target, final int lastIndex) throws Exception {
        int hasRead = 0;
        int i;
        while ((i = reader.read()) != -1) {
            hasRead++;
            char c = (char) i;
            if (c == target) {
                return hasRead;
            }
            if (!Character.isWhitespace(c)) {
                throw new JsonParsingDataException(lastIndex + hasRead, String.valueOf(c), String.valueOf(target));
            }
        }
        throw new JsonParsingDataException(lastIndex + hasRead + 1, null, String.valueOf(target));
    }
}