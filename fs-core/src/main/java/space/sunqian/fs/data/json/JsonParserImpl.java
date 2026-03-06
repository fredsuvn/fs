package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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
            Object parsed = parseJson(reader, 0, true);
            if (parsed == null) {
                return JsonData.ofNull();
            }
            if (parsed instanceof String) {
                return JsonData.ofString(parsed.toString());
            }
            if (parsed instanceof Boolean) {
                return JsonData.ofBoolean((Boolean) parsed);
            }
            if (parsed instanceof Number) {
                return JsonData.ofNumber((Number) parsed);
            }
            if (parsed instanceof List<?>) {
                return JsonData.ofList(Fs.as(parsed));
            }
            return JsonData.ofMap(Fs.as(parsed));
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    private @Nullable Object parseJson(@Nonnull Reader reader, final int index, boolean toEnd) throws Exception {
        int hasRead = 0;
        int i;
        while ((i = reader.read()) != -1) {
            hasRead++;
            char c = (char) i;
            if (Character.isWhitespace(c)) {
                continue;
            }
            switch (c) {
                case 'n':
                    parseNull(reader, index + 1);
                    if (toEnd) {
                        skipToEof(reader, index + 4);
                    }
                    return null;
                case 't':
                    parseTrue(reader, index + 1);
                    if (toEnd) {
                        skipToEof(reader, index + 4);
                    }
                    return true;
                case 'f':
                    parseFalse(reader, index + 1);
                    if (toEnd) {
                        skipToEof(reader, index + 5);
                    }
                    return false;
                case '\"': {
                    int validIndex = index + hasRead;
                    StringBuilder builder = new StringBuilder();
                    int strLen = parseString(reader, builder, validIndex);
                    if (toEnd) {
                        skipToEof(reader, validIndex + strLen);
                    }
                    return builder.toString();
                }
                case '{': {
                    int validIndex = index + hasRead;
                    Map<String, Object> builder = new LinkedHashMap<>();
                    int strLen = parseObject(reader, builder, validIndex);
                    if (toEnd) {
                        skipToEof(reader, validIndex + strLen);
                    }
                    return builder;
                }
            }
        }
        throw new JsonDataException("Unexpected end of JSON string at index: " + index + ".");
    }

    private int parseObject(
        @Nonnull Reader reader, @Nonnull Map<@Nonnull String, @Nullable Object> builder, int index
    ) throws Exception {
        int hasRead = 0;
        int i;
        while ((i = reader.read()) != -1) {
            hasRead++;
            char c = (char) i;
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == '\"') {
                int validIndex = index + hasRead;
                StringBuilder keyBuilder = new StringBuilder();
                int strLen = parseString(reader, keyBuilder, validIndex);
                validIndex = validIndex + strLen;
                int skipLen = skipToChar(reader, ':', validIndex);
                validIndex = validIndex + skipLen;
                Object value = parseJson(reader, validIndex, false);
                builder.put(keyBuilder.toString(), value);
            }
        }
        throw new JsonDataException("Unexpected end of JSON string at index: " + index + ".");
    }

    private void parseNull(@Nonnull Reader reader, int index) throws Exception {
        nextChar(reader, 'u', index++);
        nextChar(reader, 'l', index++);
        nextChar(reader, 'l', index);
    }

    private void parseTrue(@Nonnull Reader reader, int index) throws Exception {
        nextChar(reader, 'r', index++);
        nextChar(reader, 'u', index++);
        nextChar(reader, 'e', index);
    }

    private void parseFalse(@Nonnull Reader reader, int index) throws Exception {
        nextChar(reader, 'a', index++);
        nextChar(reader, 'l', index++);
        nextChar(reader, 's', index++);
        nextChar(reader, 'e', index);
    }

    private int parseString(
        @Nonnull Reader reader, @Nonnull StringBuilder builder, final int index
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
                    int escapeLen = parseEscape(reader, builder, index + hasRead);
                    hasRead += escapeLen;
                default:
                    builder.append(c);
            }
        }
        throw new JsonParsingDataException(index + hasRead - 1, null, "\"");
    }

    private int parseEscape(@Nonnull Reader reader, @Nonnull StringBuilder builder, final int index) throws Exception {
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
                    parseUnicode(reader, builder, index + 1);
                    return 5;
                default:
                    throw new JsonParsingDataException(index, String.valueOf(c), null);
            }
        }
        throw new JsonParsingDataException(index, null, null);
    }

    private void parseUnicode(@Nonnull Reader reader, @Nonnull StringBuilder builder, final int index) throws Exception {
        char c1 = nextChar(reader, index);
        char c2 = nextChar(reader, index + 1);
        char c3 = nextChar(reader, index + 2);
        char c4 = nextChar(reader, index + 3);
        builder.append(CharsKit.unicodeToChar(c1, c2, c3, c4));
    }

    private char nextChar(@Nonnull Reader reader, final int index) throws Exception {
        int i = reader.read();
        if (i == -1) {
            throw new JsonParsingDataException(index, null, null);
        }
        return (char) i;
    }

    private void nextChar(@Nonnull Reader reader, char shouldBe, final int index) throws Exception {
        int i = reader.read();
        if (i == -1) {
            throw new JsonParsingDataException(index, null, String.valueOf(shouldBe));
        }
        char c = (char) i;
        if (shouldBe != c) {
            throw new JsonParsingDataException(index, String.valueOf(c), String.valueOf(shouldBe));
        }
    }

    private void skipToEof(@Nonnull Reader reader, final int index) throws Exception {
        int hasRead = 0;
        int i;
        while ((i = reader.read()) != -1) {
            hasRead++;
            char c = (char) i;
            if (!Character.isWhitespace(c)) {
                throw new JsonParsingDataException(index + hasRead - 1, String.valueOf(c), null);
            }
        }
    }

    private int skipToChar(@Nonnull Reader reader, char target, final int index) throws Exception {
        int hasRead = 0;
        int i;
        while ((i = reader.read()) != -1) {
            hasRead++;
            char c = (char) i;
            if (c == target) {
                return hasRead;
            }
            if (!Character.isWhitespace(c)) {
                throw new JsonParsingDataException(index + hasRead - 1, String.valueOf(c), String.valueOf(target));
            }
        }
        throw new JsonParsingDataException(index + hasRead - 1, null, String.valueOf(target));
    }
}