package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.number.NumKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.IOException;
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
            JReader jReader = new JReader(reader);
            Object result = parseJson(jReader, new StringBuilder(), true);
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

    private Object parseJson(
        @Nonnull JReader reader, @Nonnull StringBuilder strBuilder, boolean toEnd
    ) throws Exception {
        Object result = null;
        int i;
        while ((i = reader.nextChar()) != -1) {
            char c = (char) i;
            if (Character.isWhitespace(c)) {
                continue;
            }
            switch (c) {
                case 'n':
                    parseNull(reader);
                    break;
                case 't':
                    parseTrue(reader);
                    result = true;
                    break;
                case 'f':
                    parseFalse(reader);
                    result = false;
                    break;
                case '\"': {
                    parseString(reader, strBuilder);
                    result = strBuilder.toString();
                    strBuilder.setLength(0);
                    break;
                }
                case '{': {
                    Map<String, Object> objBuilder = new LinkedHashMap<>();
                    parseObject(reader, objBuilder, strBuilder);
                    result = objBuilder;
                    break;
                }
                case '[': {
                    List<Object> arrBuilder = new ArrayList<>();
                    parseArray(reader, arrBuilder, strBuilder);
                    result = arrBuilder;
                    break;
                }
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '-':
                    Number number = parseNumber(reader, strBuilder);
                    result = number;
                    strBuilder.setLength(0);
                    break;
                default:
                    throw new JsonParsingDataException(reader.nextIndex() - 1, String.valueOf(c), null);
            }
        }
        if (toEnd) {
            skipToEof(reader);
        }
        return result;
    }

    private void parseObject(
        @Nonnull JReader reader,
        @Nonnull Map<@Nonnull String, @Nullable Object> objBuilder,
        @Nonnull StringBuilder strBuilder
    ) throws Exception {
        boolean first = true;
        int i;
        while ((i = reader.nextChar()) != -1) {
            char c = (char) i;
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == '\"') {
                parseString(reader, strBuilder);
                skipToChar(reader, ':');
                String key = strBuilder.toString();
                strBuilder.setLength(0);
                Object obj = parseJson(reader, strBuilder, false);
                objBuilder.put(key, obj);
                first = false;
                continue;
            }
            if (c == ',') {
                if (!first) {
                    continue;
                }
            }
            if (c == '}') {
                return;
            }
            throw new JsonParsingDataException(reader.nextIndex() - 1, String.valueOf(c), null);
        }
        throw new JsonParsingDataException(reader.nextIndex(), null, null);
    }

    private void parseArray(
        @Nonnull JReader reader,
        @Nonnull List<@Nullable Object> arrBuilder,
        @Nonnull StringBuilder strBuilder
    ) throws Exception {
        PARSING:
        while (true) {
            Object element = parseJson(reader, strBuilder, false);
            arrBuilder.add(element);
            int i;
            while ((i = reader.nextChar()) != -1) {
                char c = (char) i;
                if (Character.isWhitespace(c)) {
                    continue;
                }
                if (c == ',') {
                    continue PARSING;
                }
                if (c == ']') {
                    return;
                }
                throw new JsonParsingDataException(reader.nextIndex() - 1, String.valueOf(c), null);
            }
            throw new JsonParsingDataException(reader.nextIndex(), null, null);
        }
    }

    private void parseNull(@Nonnull JReader reader) throws Exception {
        nextChar(reader, 'u');
        nextChar(reader, 'l');
        nextChar(reader, 'l');
    }

    private void parseTrue(@Nonnull JReader reader) throws Exception {
        nextChar(reader, 'r');
        nextChar(reader, 'u');
        nextChar(reader, 'e');
    }

    private void parseFalse(@Nonnull JReader reader) throws Exception {
        nextChar(reader, 'a');
        nextChar(reader, 'l');
        nextChar(reader, 's');
        nextChar(reader, 'e');
    }

    private void parseString(
        @Nonnull JReader reader, @Nonnull StringBuilder builder
    ) throws Exception {
        int i;
        while ((i = reader.nextChar()) != -1) {
            char c = (char) i;
            switch (c) {
                case '\"':
                    return;
                case '\\':
                    parseEscape(reader, builder);
                default:
                    builder.append(c);
            }
        }
        throw new JsonParsingDataException(reader.nextIndex(), null, "\"");
    }

    private void parseEscape(
        @Nonnull JReader reader, @Nonnull StringBuilder builder
    ) throws Exception {
        int i = reader.nextChar();
        if (i != -1) {
            char c = (char) i;
            switch (c) {
                case '\"':
                case '\\':
                    builder.append(c);
                    return;
                case 'r':
                    builder.append('\r');
                    return;
                case 'n':
                    builder.append('\n');
                    return;
                case 't':
                    builder.append('\t');
                    return;
                case 'b':
                    builder.append('\b');
                    return;
                case 'f':
                    builder.append('\f');
                    return;
                case 'u':
                    parseUnicode(reader, builder);
                    return;
                default:
                    throw new JsonParsingDataException(reader.nextIndex(), String.valueOf(c), null);
            }
        }
        throw new JsonParsingDataException(reader.nextIndex(), null, null);
    }

    private void parseUnicode(
        @Nonnull JReader reader, @Nonnull StringBuilder builder
    ) throws Exception {
        char c1 = nextChar(reader);
        char c2 = nextChar(reader);
        char c3 = nextChar(reader);
        char c4 = nextChar(reader);
        builder.append(CharsKit.unicodeToChar(c1, c2, c3, c4));
    }

    private Number parseNumber(
        @Nonnull JReader reader, @Nonnull StringBuilder strBuilder
    ) throws Exception {
        int startIndex = reader.nextIndex();
        int i;
        while ((i = reader.nextChar()) != -1) {
            char c = (char) i;
            if (
                (c >= '0' && c <= '9')
                    || (c == '.')
                    || (c == 'e')
                    || (c == 'E')
            ) {
                strBuilder.append(c);
                // continue;
            } else {
                reader.swallow(i);
                break;
            }
        }
        String numberString = strBuilder.toString();
        try {
            return NumKit.toNumber(numberString);
        } catch (Exception e) {
            throw new JsonParsingDataException(startIndex, numberString, null);
        }
    }

    private char nextChar(@Nonnull JReader reader) throws Exception {
        int i = reader.nextChar();
        if (i == -1) {
            throw new JsonParsingDataException(reader.nextIndex(), null, null);
        }
        return (char) i;
    }

    private void nextChar(@Nonnull JReader reader, char shouldBe) throws Exception {
        int i = reader.nextChar();
        if (i == -1) {
            throw new JsonParsingDataException(reader.nextIndex(), null, String.valueOf(shouldBe));
        }
        char c = (char) i;
        if (shouldBe != c) {
            throw new JsonParsingDataException(reader.nextIndex() - 1, String.valueOf(c), String.valueOf(shouldBe));
        }
    }

    private void skipToEof(@Nonnull JReader reader) throws Exception {
        int i;
        while ((i = reader.nextChar()) != -1) {
            char c = (char) i;
            if (!Character.isWhitespace(c)) {
                throw new JsonParsingDataException(reader.nextIndex() - 1, String.valueOf(c), null);
            }
        }
    }

    private void skipToChar(@Nonnull JReader reader, char target) throws Exception {
        int i;
        while ((i = reader.nextChar()) != -1) {
            char c = (char) i;
            if (c == target) {
                return;
            }
            if (!Character.isWhitespace(c)) {
                throw new JsonParsingDataException(reader.nextIndex() - 1, String.valueOf(c), String.valueOf(target));
            }
        }
        throw new JsonParsingDataException(reader.nextIndex(), null, String.valueOf(target));
    }

    private static final class JReader {

        private final @Nonnull Reader reader;
        private int index = 0;
        private int swallowedChar = -1;

        private JReader(@Nonnull Reader reader) {
            this.reader = reader;
        }

        public int nextChar() throws IOException {
            int result;
            if (swallowedChar != -1) {
                result = swallowedChar;
                swallowedChar = -1;
            } else {
                result = reader.read();
            }
            if (result != -1) {
                index++;
            }
            return result;
        }

        public int nextIndex() {
            return index;
        }

        public void swallow(int buf) {
            this.swallowedChar = buf;
            index--;
        }
    }
}