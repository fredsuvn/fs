package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.base.number.NumberKit;
import space.sunqian.fs.base.string.StringView;
import space.sunqian.fs.data.DataParsingException;
import space.sunqian.fs.io.IOKit;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

enum JsonParserImpl implements JsonParser {
    INST;

    // private static boolean isWhitespace(char c) {
    //     return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    // }

    private static final char @Nonnull [] EXPECT_NULL = {'u', 'l', 'l'};
    private static final char @Nonnull [] EXPECT_TRUE = {'r', 'u', 'e'};
    private static final char @Nonnull [] EXPECT_FALSE = {'a', 'l', 's', 'e'};

    private static void parseEscape(
        @Nonnull JsonReader reader, @Nonnull StringBuilder builder
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
                    throw new JsonDataParsingException(reader.nextIndex(), String.valueOf(c), null);
            }
        }
        throw new JsonDataParsingException(reader.nextIndex(), null, null);
    }

    private static void parseUnicode(
        @Nonnull JsonReader reader, @Nonnull StringBuilder builder
    ) throws Exception {
        char c1 = nextChar(reader);
        char c2 = nextChar(reader);
        char c3 = nextChar(reader);
        char c4 = nextChar(reader);
        builder.append(CharsKit.unicodeToChar(c1, c2, c3, c4));
    }

    private static char nextChar(@Nonnull JsonReader reader) throws Exception {
        int i = reader.nextChar();
        if (i == -1) {
            throw new JsonDataParsingException(reader.nextIndex(), null, null);
        }
        return (char) i;
    }

    private static boolean isNumberMember(char c) {
        return (c >= '0' && c <= '9')
            || (c == '.')
            || (c == 'e')
            || (c == 'E')
            || (c == '+');
    }

    private static boolean isWhitespace(int c) {
        return c == (' ' & 0x0000ffff) || c == ('\t' & 0x0000ffff) || c == ('\r' & 0x0000ffff) || c == ('\n' & 0x0000ffff);
    }

    private static final @Nonnull Object NULL = new Object();

    @Override
    public @Nonnull JsonData parse(byte @Nonnull [] bytes) throws DataParsingException {
        String str = new String(bytes, CharsKit.defaultCharset());
        return parse(str);
    }

    @Override
    public @Nonnull JsonData parse(@Nonnull InputStream input) throws JsonDataParsingException {
        Reader reader = IOKit.newReader(input, CharsKit.defaultCharset());
        return parse(reader);
    }

    @Override
    public @Nonnull JsonData parse(@Nonnull ReadableByteChannel channel) throws JsonDataParsingException {
        // compatible with JDK8
        @SuppressWarnings("CharsetObjectCanBeUsed")
        Reader reader = Channels.newReader(channel, CharsKit.defaultCharset().name());
        return parse(reader);
    }

    @Override
    public @Nonnull JsonData parse(char @Nonnull [] chars) throws DataParsingException {
        return parse(StringView.of(chars));
    }

    @Override
    public @Nonnull JsonData parse(@Nonnull CharSequence charSequence) throws DataParsingException {
        return parse(JsonReader.from(charSequence));
    }

    @Override
    public @Nonnull JsonData parse(@Nonnull Reader reader) throws JsonDataParsingException {
        return parse(JsonReader.from(reader));
    }

    private @Nonnull JsonData parse(@Nonnull JsonReader jsonReader) throws JsonDataParsingException {
        try {
            Object result = parseJson(jsonReader, new StringBuilder(), true);
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
        } catch (JsonDataParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonDataParsingException(e);
        }
    }

    private Object parseJson(
        @Nonnull JsonReader reader, @Nonnull StringBuilder strBuilder, boolean toEnd
    ) throws Exception {
        Object result = null;
        int i;
        PARSING:
        while ((i = reader.nextCharSkipWhitespace()) != -1) {
            char c = (char) i;
            // if (isWhitespace(c)) {
            //     continue;
            // }
            switch (c) {
                case 'n':
                    // parseNull(reader);
                    reader.expect(EXPECT_NULL);
                    result = NULL;
                    break PARSING;
                case 't':
                    // parseTrue(reader);
                    reader.expect(EXPECT_TRUE);
                    result = true;
                    break PARSING;
                case 'f':
                    // parseFalse(reader);
                    reader.expect(EXPECT_FALSE);
                    result = false;
                    break PARSING;
                case '\"':
                    // parseString(reader, strBuilder);
                    // result = strBuilder.toString();
                    result = reader.nextString();
                    strBuilder.setLength(0);
                    break PARSING;
                case '{':
                    Map<String, Object> objBuilder = new LinkedHashMap<>();
                    parseObject(reader, objBuilder, strBuilder);
                    result = objBuilder;
                    break PARSING;
                case '[':
                    List<Object> arrBuilder = new ArrayList<>();
                    parseArray(reader, arrBuilder, strBuilder);
                    result = arrBuilder;
                    break PARSING;
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
                    // strBuilder.append(c);
                    //@SuppressWarnings("UnnecessaryLocalVariable")
                    // Number number = parseNumber(reader, strBuilder);
                    // result = number;
                    result = reader.nextNumber(c);
                    strBuilder.setLength(0);
                    break PARSING;
                default:
                    throw new JsonDataParsingException(reader.nextIndex() - 1, String.valueOf(c), null);
            }
        }
        if (result == null) {
            throw new JsonDataParsingException(reader.nextIndex(), null, null);
        }
        if (result == NULL) {
            return null;
        }
        if (toEnd) {
            reader.skipToEof();
        }
        return result;
    }

    private void parseObject(
        @Nonnull JsonReader reader,
        @Nonnull Map<@Nonnull String, @Nullable Object> objBuilder,
        @Nonnull StringBuilder strBuilder
    ) throws Exception {
        boolean first = true;
        int i;
        while ((i = reader.nextCharSkipWhitespace()) != -1) {
            char c = (char) i;
            // if (isWhitespace(c)) {
            //     continue;
            // }
            switch (c) {
                case '\"':
                    // parseString(reader, strBuilder);
                    // String key = strBuilder.toString();
                    String key = reader.nextString();
                    strBuilder.setLength(0);
                    reader.skipToChar(':');
                    Object obj = parseJson(reader, strBuilder, false);
                    objBuilder.put(key, obj);
                    first = false;
                    continue;
                case ',':
                    if (!first) {
                        continue;
                    } else {
                        throw new JsonDataParsingException(reader.nextIndex() - 1, String.valueOf(c), null);
                    }
                case '}':
                    return;
                default:
                    throw new JsonDataParsingException(reader.nextIndex() - 1, String.valueOf(c), null);
            }
        }
        throw new JsonDataParsingException(reader.nextIndex(), null, "}");
    }

    private void parseArray(
        @Nonnull JsonReader reader,
        @Nonnull List<@Nullable Object> arrBuilder,
        @Nonnull StringBuilder strBuilder
    ) throws Exception {
        int count = 0;
        int i;
        while ((i = reader.nextCharSkipWhitespace()) != -1) {
            char c = (char) i;
            // if (isWhitespace(c)) {
            //     continue;
            // }
            if (c == ',') {
                if (count == 0) {
                    throw new JsonDataParsingException(reader.nextIndex() - 1, String.valueOf(c), null);
                }
                continue;
            }
            if (c == ']') {
                return;
            }
            // parsing element
            reader.swallow(i);
            Object element = parseJson(reader, strBuilder, false);
            arrBuilder.add(element);
            count++;
        }
        throw new JsonDataParsingException(reader.nextIndex(), null, "]");
    }

    private interface JsonReader {

        static JsonReader from(@Nonnull Reader reader) {
            return new OfReader(reader);
        }

        static JsonReader from(@Nonnull CharSequence charSequence) {
            return new OfString(charSequence);
        }

        int nextChar() throws Exception;

        default int nextCharSkipWhitespace() throws Exception {
            int c;
            do {
                c = nextChar();
            } while (isWhitespace(c));
            return c;
        }

        default void expect(char @Nonnull [] shouldBe) throws Exception {
            for (char value : shouldBe) {
                int ci = nextChar();
                if (ci == -1) {
                    throw new JsonDataParsingException(nextIndex(), null, String.valueOf(value));
                }
                char c = (char) ci;
                if (c != value) {
                    throw new JsonDataParsingException(nextIndex() - 1, String.valueOf(c), String.valueOf(value));
                }
            }
        }

        @Nonnull
        String nextString() throws Exception;

        @Nonnull
        Number nextNumber(char first) throws Exception;

        default void skipToEof() throws Exception {
            int i;
            if ((i = nextCharSkipWhitespace()) != -1) {
                char c = (char) i;
                throw new JsonDataParsingException(nextIndex() - 1, String.valueOf(c), null);
            }
        }

        @SuppressWarnings("SameParameterValue")
        default void skipToChar(char target) throws Exception {
            int i;
            if ((i = nextCharSkipWhitespace()) != -1) {
                char c = (char) i;
                if (c == target) {
                    return;
                }
                throw new JsonDataParsingException(nextIndex() - 1, String.valueOf(c), String.valueOf(target));
            }
            throw new JsonDataParsingException(nextIndex(), null, String.valueOf(target));
        }

        int nextIndex();

        void swallow(int aChar);
    }

    private static final class OfString implements JsonReader {

        private final @Nonnull CharSequence charSequence;
        private int index = 0;
        // private int swallowedChar = -1;

        private OfString(@Nonnull CharSequence charSequence) {
            this.charSequence = charSequence;
        }

        @Override
        public int nextChar() {
            if (index >= charSequence.length()) {
                return -1;
            }
            return charSequence.charAt(index++);
        }

        @Override
        public @Nonnull String nextString() throws Exception {
            int start = index;
            int i;
            while ((i = nextChar()) != -1) {
                char c = (char) i;
                switch (c) {
                    case '\"':
                        return charSequence.subSequence(start, index - 1).toString();
                    case '\\':
                        StringBuilder builder = new StringBuilder(charSequence.subSequence(start, index - 1));
                        return nextStringWithEscape(builder);
                    default:
                        // builder.append(c);
                }
            }
            throw new JsonDataParsingException(index, null, "\"");
        }

        @Override
        public @Nonnull Number nextNumber(char first) {
            int start = index - 1;
            int i;
            while ((i = nextChar()) != -1) {
                char c = (char) i;
                if (!isNumberMember(c)) {
                    swallow(i);
                    break;
                }
            }
            CharSequence numberString = charSequence.subSequence(start, index);
            try {
                return NumberKit.toNumber(numberString);
            } catch (Exception e) {
                throw new JsonDataParsingException(start, numberString.toString(), null);
            }
        }

        private @Nonnull String nextStringWithEscape(@Nonnull StringBuilder builder) throws Exception {
            parseEscape(this, builder);
            int i;
            while ((i = nextChar()) != -1) {
                char c = (char) i;
                switch (c) {
                    case '\"':
                        return builder.toString();
                    case '\\':
                        parseEscape(this, builder);
                        continue;
                    default:
                        builder.append(c);
                }
            }
            throw new JsonDataParsingException(index, null, "\"");
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public void swallow(int aChar) {
            // this.swallowedChar = buf;
            index--;
        }
    }

    private static final class OfReader implements JsonReader {

        private static final @Nonnull ThreadLocal<char @Nonnull []> BUFFER =
            ThreadLocal.withInitial(() -> new char[IOKit.bufferSize()]);

        private final @Nonnull Reader reader;
        private int position = 0;
        private int swallowedChar = -1;

        // private final char @Nonnull [] buffer = new char[1024];
        private int index = 0;
        private int length = 0;

        private OfReader(@Nonnull Reader reader) {
            this.reader = reader;
        }

        @Override
        public int nextChar() throws IOException {
            if (swallowedChar != -1) {
                int result = swallowedChar;
                swallowedChar = -1;
                return result;
            }
            refreshBuffer();
            if (length == -1) {
                return -1;
            }
            position++;
            return BUFFER.get()[index++];
        }

        @Override
        public @Nonnull String nextString() throws Exception {
            StringBuilder builder = new StringBuilder();
            int i;
            while ((i = nextChar()) != -1) {
                char c = (char) i;
                switch (c) {
                    case '\"':
                        return builder.toString();
                    case '\\':
                        parseEscape(this, builder);
                        continue;
                    default:
                        builder.append(c);
                }
            }
            throw new JsonDataParsingException(nextIndex(), null, "\"");
        }

        @Override
        public @Nonnull Number nextNumber(char first) throws Exception {
            StringBuilder builder = new StringBuilder();
            builder.append(first);
            int startIndex = nextIndex() - 1;
            int i;
            while ((i = nextChar()) != -1) {
                char c = (char) i;
                if (
                    (c >= '0' && c <= '9')
                        || (c == '.')
                        || (c == 'e')
                        || (c == 'E')
                        || (c == '+')
                ) {
                    builder.append(c);
                    // continue;
                } else {
                    swallow(i);
                    break;
                }
            }
            // String numberString = strBuilder.toString();
            try {
                return NumberKit.toNumber(builder);
            } catch (Exception e) {
                throw new JsonDataParsingException(startIndex, builder.toString(), null);
            }
        }

        @Override
        public int nextIndex() {
            return position;
        }

        @Override
        public void swallow(int aChar) {
            this.swallowedChar = aChar;
            position--;
        }

        private void refreshBuffer() throws IOException {
            if (index < length) {
                return;
            }
            this.index = 0;
            this.length = reader.read(BUFFER.get());
        }
    }
}