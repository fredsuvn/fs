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

    private static final char @Nonnull [] EXPECT_NULL = {'u', 'l', 'l'};
    private static final char @Nonnull [] EXPECT_TRUE = {'r', 'u', 'e'};
    private static final char @Nonnull [] EXPECT_FALSE = {'a', 'l', 's', 'e'};

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
            Object result = parseJson(jsonReader, true);
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

    private Object parseJson(@Nonnull JsonReader reader, boolean toEnd) throws Exception {
        Object result = null;
        int i;
        if ((i = reader.nextCharSkipWhitespace()) != -1) {
            char c = (char) i;
            switch (c) {
                case 'n':
                    reader.expect(EXPECT_NULL);
                    result = NULL;
                    break;
                case 't':
                    reader.expect(EXPECT_TRUE);
                    result = true;
                    break;
                case 'f':
                    reader.expect(EXPECT_FALSE);
                    result = false;
                    break;
                case '\"':
                    result = reader.nextString();
                    break;
                case '{':
                    Map<String, Object> objBuilder = new LinkedHashMap<>();
                    parseObject(reader, objBuilder);
                    result = objBuilder;
                    break;
                case '[':
                    List<Object> arrBuilder = new ArrayList<>();
                    parseArray(reader, arrBuilder);
                    result = arrBuilder;
                    break;
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
                    result = reader.nextNumber();
                    break;
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
        @Nonnull Map<@Nonnull String, @Nullable Object> objBuilder
    ) throws Exception {
        boolean first = true;
        int i;
        while ((i = reader.nextCharSkipWhitespace()) != -1) {
            char c = (char) i;
            switch (c) {
                case '\"':
                    String key = reader.nextString();
                    reader.skipToChar(':');
                    Object obj = parseJson(reader, false);
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
        @Nonnull List<@Nullable Object> arrBuilder
    ) throws Exception {
        int count = 0;
        int i;
        while ((i = reader.nextCharSkipWhitespace()) != -1) {
            char c = (char) i;
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
            Object element = parseJson(reader, false);
            arrBuilder.add(element);
            count++;
        }
        throw new JsonDataParsingException(reader.nextIndex(), null, "]");
    }

    private static abstract class JsonReader {

        static JsonReader from(@Nonnull Reader reader) {
            return new OfReader(reader);
        }

        static JsonReader from(@Nonnull CharSequence charSequence) {
            return new OfString(charSequence);
        }

        public abstract int nextChar() throws Exception;

        public int nextCharSkipWhitespace() throws Exception {
            int c;
            do {
                c = nextChar();
            } while (isWhitespace(c));
            return c;
        }

        public void expect(char @Nonnull [] shouldBe) throws Exception {
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

        public abstract @Nonnull String nextString() throws Exception;

        public abstract @Nonnull Number nextNumber() throws Exception;

        public void skipToEof() throws Exception {
            int i;
            if ((i = nextCharSkipWhitespace()) != -1) {
                char c = (char) i;
                throw new JsonDataParsingException(nextIndex() - 1, String.valueOf(c), null);
            }
        }

        @SuppressWarnings("SameParameterValue")
        public void skipToChar(char target) throws Exception {
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

        public abstract int nextIndex();

        public abstract void swallow(int aChar);

        protected void parseEscape(@Nonnull StringBuilder builder) throws Exception {
            int i = nextChar();
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
                        parseUnicode(builder);
                        return;
                    default:
                        throw new JsonDataParsingException(nextIndex(), String.valueOf(c), null);
                }
            }
            throw new JsonDataParsingException(nextIndex(), null, null);
        }

        private void parseUnicode(@Nonnull StringBuilder builder) throws Exception {
            char c1 = nextCharExplicit();
            char c2 = nextCharExplicit();
            char c3 = nextCharExplicit();
            char c4 = nextCharExplicit();
            builder.append(CharsKit.unicodeToChar(c1, c2, c3, c4));
        }

        private char nextCharExplicit() throws Exception {
            int i = nextChar();
            if (i == -1) {
                throw new JsonDataParsingException(nextIndex(), null, null);
            }
            return (char) i;
        }
    }

    private static final class OfString extends JsonReader {

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

        private @Nonnull String nextStringWithEscape(@Nonnull StringBuilder builder) throws Exception {
            parseEscape(builder);
            int i;
            while ((i = nextChar()) != -1) {
                char c = (char) i;
                switch (c) {
                    case '\"':
                        return builder.toString();
                    case '\\':
                        parseEscape(builder);
                        continue;
                    default:
                        builder.append(c);
                }
            }
            throw new JsonDataParsingException(index, null, "\"");
        }

        @Override
        public @Nonnull Number nextNumber() {
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

    private static final class OfReader extends JsonReader {

        // private static final @Nonnull ThreadLocal<char @Nonnull []> BUFFER =
        //     ThreadLocal.withInitial(() -> new char[IOKit.bufferSize()]);

        private final @Nonnull Reader reader;
        private int position = 0;
        private int swallowedChar = -1;

        private final char @Nonnull [] buffer = new char[256];
        private int index = 0;
        private int length = 0;

        private final @Nonnull StringBuilder builder = new StringBuilder();;

        private OfReader(@Nonnull Reader reader) {
            this.reader = reader;
        }

        @Override
        public int nextChar() throws IOException {
            return nextChar(false);
        }

        private int nextChar(boolean versionFlag) throws IOException {
            if (swallowedChar != -1) {
                int result = swallowedChar;
                swallowedChar = -1;
                position++;
                return result;
            }
            if (length == -1) {
                return -1;
            }
            if (index >= length) {
                if (length > 0 && length < buffer.length) {
                    return -1;
                }
                if (versionFlag) {
                    return -2;
                }
                index = 0;
                length = reader.read(buffer);
                if (length == -1) {
                    return -1;
                }
            }
            position++;
            return buffer[index++];
        }

        @Override
        public @Nonnull String nextString() throws Exception {
            int start = index;
            while (true) {
                int i = nextChar(true);
                if (i == -1) {
                    break;
                }
                if (i == -2) {
                    int count = length - start;
                    //StringBuilder builder = new StringBuilder(count);
                    builder.setLength(0);
                    builder.append(buffer, start, count);
                    return nextStringWithBuilder(builder);
                }
                char c = (char) i;
                switch (c) {
                    case '\"':
                        return new String(buffer, start, index - 1 - start);
                    case '\\':
                        int count = index - 1 - start;
                        //StringBuilder builder = new StringBuilder(count);
                        builder.setLength(0);
                        builder.append(buffer, start, count);
                        parseEscape(builder);
                        return nextStringWithBuilder(builder);
                    default:
                        // builder.append(c);
                }
            }
            throw new JsonDataParsingException(index, null, "\"");
        }

        private @Nonnull String nextStringWithBuilder(@Nonnull StringBuilder builder) throws Exception {
            int i;
            while ((i = nextChar()) != -1) {
                char c = (char) i;
                switch (c) {
                    case '\"':
                        return builder.toString();
                    case '\\':
                        parseEscape(builder);
                        continue;
                    default:
                        builder.append(c);
                }
            }
            throw new JsonDataParsingException(nextIndex(), null, "\"");
        }

        // @Override
        // public @Nonnull String nextString() throws Exception {
        //     StringBuilder builder = new StringBuilder();
        //     int i;
        //     while ((i = nextChar()) != -1) {
        //         char c = (char) i;
        //         switch (c) {
        //             case '\"':
        //                 return builder.toString();
        //             case '\\':
        //                 parseEscape(builder);
        //                 continue;
        //             default:
        //                 builder.append(c);
        //         }
        //     }
        //     throw new JsonDataParsingException(nextIndex(), null, "\"");
        // }

        @Override
        public @Nonnull Number nextNumber() throws Exception {
            int start = index - 1;
            CharSequence numberString;
            while (true) {
                int i = nextChar(true);
                if (i == -1) {
                    numberString = new String(buffer, start, index - start);
                    break;
                }
                if (i == -2) {
                    int count = length - start;
                    //StringBuilder builder = new StringBuilder(count);
                    builder.setLength(0);
                    builder.append(buffer, start, count);
                    return nextNumberWithBuilder(builder);
                }
                char c = (char) i;
                if (!isNumberMember(c)) {
                    swallow(i);
                    numberString = new String(buffer, start, index - 1 - start);
                    break;
                }
            }
            try {
                return NumberKit.toNumber(numberString);
            } catch (Exception e) {
                throw new JsonDataParsingException(start, numberString.toString(), null);
            }
        }

        private @Nonnull Number nextNumberWithBuilder(@Nonnull StringBuilder builder) throws Exception {
            int startIndex = nextIndex() - 1;
            int i;
            while ((i = nextChar()) != -1) {
                char c = (char) i;
                if (isNumberMember(c)) {
                    builder.append(c);
                    // continue;
                } else {
                    swallow(i);
                    break;
                }
            }
            try {
                return NumberKit.toNumber(builder);
            } catch (Exception e) {
                throw new JsonDataParsingException(startIndex, builder.toString(), null);
            }
        }

        // @Override
        // public @Nonnull Number nextNumber(char first) throws Exception {
        //     StringBuilder builder = new StringBuilder();
        //     builder.append(first);
        //     int startIndex = nextIndex() - 1;
        //     int i;
        //     while ((i = nextChar()) != -1) {
        //         char c = (char) i;
        //         if (isNumberMember(c)) {
        //             builder.append(c);
        //             // continue;
        //         } else {
        //             swallow(i);
        //             break;
        //         }
        //     }
        //     try {
        //         return NumberKit.toNumber(builder);
        //     } catch (Exception e) {
        //         throw new JsonDataParsingException(startIndex, builder.toString(), null);
        //     }
        // }

        @Override
        public int nextIndex() {
            return position;
        }

        @Override
        public void swallow(int aChar) {
            this.swallowedChar = aChar;
            position--;
        }
    }
}