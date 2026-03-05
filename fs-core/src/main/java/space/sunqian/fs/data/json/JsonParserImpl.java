package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
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
            JsonTokenizer tokenizer = new JsonTokenizer(reader);
            JsonToken token = tokenizer.nextToken();
            return parseValue(token, tokenizer);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (JsonDataException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonDataException("Failed to parse JSON", e);
        }
    }

    private @Nonnull JsonData parseValue(@Nullable JsonToken token, @Nonnull JsonTokenizer tokenizer) throws IOException {
        if (token == null) {
            throw new JsonDataException("Unexpected end of JSON input");
        }

        switch (token.type) {
            case NULL:
                return JsonData.ofNull();
            case TRUE:
                return JsonData.ofBoolean(true);
            case FALSE:
                return JsonData.ofBoolean(false);
            case STRING:
                return JsonData.ofString(token.value);
            case NUMBER:
                return parseNumber(token.value);
            case LEFT_BRACE:
                return parseObject(tokenizer);
            case LEFT_BRACKET:
                return parseArray(tokenizer);
            default:
                throw new JsonDataException("Unexpected token: " + token.type);
        }
    }

    private @Nonnull JsonData parseNumber(@Nonnull String numberStr) {
        try {
            if (numberStr.contains(".") || numberStr.contains("e") || numberStr.contains("E")) {
                return JsonData.ofNumber(Double.parseDouble(numberStr));
            } else {
                long longValue = Long.parseLong(numberStr);
                if (longValue <= Integer.MAX_VALUE && longValue >= Integer.MIN_VALUE) {
                    return JsonData.ofNumber((int) longValue);
                }
                return JsonData.ofNumber(longValue);
            }
        } catch (NumberFormatException e) {
            try {
                return JsonData.ofNumber(new java.math.BigDecimal(numberStr));
            } catch (Exception ex) {
                throw new JsonDataException("Invalid number format: " + numberStr, e);
            }
        }
    }

    private @Nonnull JsonData parseObject(@Nonnull JsonTokenizer tokenizer) throws IOException {
        Map<String, Object> map = new HashMap<>();
        @Nullable JsonToken token = tokenizer.nextToken();

        if (token != null && token.type == JsonToken.Type.RIGHT_BRACE) {
            return JsonData.ofMap(map);
        }

        while (token != null) {
            if (token.type != JsonToken.Type.STRING) {
                throw new JsonDataException("Expected string key in object, got: " + token.type);
            }

            @Nonnull String key = token.value;
            token = tokenizer.nextToken();
            if (token == null || token.type != JsonToken.Type.COLON) {
                throw new JsonDataException("Expected ':' after key in object");
            }

            token = tokenizer.nextToken();
            @Nonnull JsonData value = parseValue(token, tokenizer);
            map.put(key, convertJsonDataToObject(value));

            token = tokenizer.nextToken();
            if (token == null) {
                throw new JsonDataException("Unexpected end of JSON input");
            }

            if (token.type == JsonToken.Type.RIGHT_BRACE) {
                break;
            }

            if (token.type != JsonToken.Type.COMMA) {
                throw new JsonDataException("Expected ',' or '}' in object, got: " + token.type);
            }

            token = tokenizer.nextToken();
        }

        return JsonData.ofMap(map);
    }

    private @Nonnull JsonData parseArray(@Nonnull JsonTokenizer tokenizer) throws IOException {
        List<Object> list = new ArrayList<>();
        @Nullable JsonToken token = tokenizer.nextToken();

        if (token != null && token.type == JsonToken.Type.RIGHT_BRACKET) {
            return JsonData.ofList(list);
        }

        while (token != null) {
            @Nonnull JsonData value = parseValue(token, tokenizer);
            list.add(convertJsonDataToObject(value));

            token = tokenizer.nextToken();
            if (token == null) {
                throw new JsonDataException("Unexpected end of JSON input");
            }

            if (token.type == JsonToken.Type.RIGHT_BRACKET) {
                break;
            }

            if (token.type != JsonToken.Type.COMMA) {
                throw new JsonDataException("Expected ',' or ']' in array, got: " + token.type);
            }

            token = tokenizer.nextToken();
        }

        return JsonData.ofList(list);
    }

    private @Nullable Object convertJsonDataToObject(@Nonnull JsonData jsonData) {
        switch (jsonData.type()) {
            case NULL:
                return null;
            case BOOLEAN:
                return jsonData.asBoolean();
            case NUMBER:
                return jsonData.asNumber();
            case STRING:
                return jsonData.asString();
            case OBJECT:
                return jsonData.asMap();
            case ARRAY:
                return jsonData.asList();
            default:
                return null;
        }
    }

    private static class JsonTokenizer {
        private final @Nonnull Reader reader;
        private int currentChar = -1;
        private boolean eof = false;

        JsonTokenizer(@Nonnull Reader reader) throws IOException {
            this.reader = reader;
            advance();
        }

        @Nullable
        JsonToken nextToken() throws IOException {
            skipWhitespace();
            if (eof) {
                return null;
            }

            switch (currentChar) {
                case '{':
                    advance();
                    return new JsonToken(JsonToken.Type.LEFT_BRACE, null);
                case '}':
                    advance();
                    return new JsonToken(JsonToken.Type.RIGHT_BRACE, null);
                case '[':
                    advance();
                    return new JsonToken(JsonToken.Type.LEFT_BRACKET, null);
                case ']':
                    advance();
                    return new JsonToken(JsonToken.Type.RIGHT_BRACKET, null);
                case ',':
                    advance();
                    return new JsonToken(JsonToken.Type.COMMA, null);
                case ':':
                    advance();
                    return new JsonToken(JsonToken.Type.COLON, null);
                case '"':
                    return parseString();
                case 't':
                    return parseTrue();
                case 'f':
                    return parseFalse();
                case 'n':
                    return parseNull();
                default:
                    if (isDigit(currentChar) || currentChar == '-') {
                        return parseNumber();
                    }
                    throw new JsonDataException("Unexpected character: " + (char) currentChar);
            }
        }

        private void advance() throws IOException {
            currentChar = reader.read();
            if (currentChar == -1) {
                eof = true;
            }
        }

        private void skipWhitespace() throws IOException {
            while (!eof && isWhitespace(currentChar)) {
                advance();
            }
        }

        private boolean isWhitespace(int c) {
            return c == ' ' || c == '\t' || c == '\n' || c == '\r';
        }

        private boolean isDigit(int c) {
            return c >= '0' && c <= '9';
        }

        @Nonnull
        JsonToken parseString() throws IOException {
            advance(); // skip opening quote
            StringBuilder sb = new StringBuilder();

            while (!eof && currentChar != '"') {
                if (currentChar == '\\') {
                    advance();
                    if (eof) {
                        throw new JsonDataException("Unexpected end of input in string escape");
                    }

                    switch (currentChar) {
                        case '"':
                            sb.append('"');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '/':
                            sb.append('/');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'u':
                            // Parse unicode escape
                            @Nonnull StringBuilder unicode = new StringBuilder();
                            for (int i = 0; i < 4; i++) {
                                advance();
                                if (eof) {
                                    throw new JsonDataException("Unexpected end of input in unicode escape");
                                }
                                unicode.append((char) currentChar);
                            }
                            try {
                                int codePoint = Integer.parseInt(unicode.toString(), 16);
                                sb.append((char) codePoint);
                            } catch (NumberFormatException e) {
                                throw new JsonDataException("Invalid unicode escape: \\u" + unicode);
                            }
                            break;
                        default:
                            throw new JsonDataException("Invalid escape sequence: \\" + (char) currentChar);
                    }
                } else {
                    sb.append((char) currentChar);
                }
                advance();
            }

            if (eof) {
                throw new JsonDataException("Unexpected end of input in string");
            }

            advance(); // skip closing quote
            return new JsonToken(JsonToken.Type.STRING, sb.toString());
        }

        @Nonnull
        JsonToken parseNumber() throws IOException {
            @Nonnull StringBuilder sb = new StringBuilder();

            if (currentChar == '-') {
                sb.append('-');
                advance();
            }

            if (currentChar == '0') {
                sb.append('0');
                advance();
            } else if (isDigit(currentChar)) {
                while (!eof && isDigit(currentChar)) {
                    sb.append((char) currentChar);
                    advance();
                }
            } else {
                throw new JsonDataException("Invalid number format");
            }

            // Fraction part
            if (!eof && currentChar == '.') {
                sb.append('.');
                advance();
                if (!eof && isDigit(currentChar)) {
                    while (!eof && isDigit(currentChar)) {
                        sb.append((char) currentChar);
                        advance();
                    }
                } else {
                    throw new JsonDataException("Expected digit after '.' in number");
                }
            }

            // Exponent part
            if (!eof && (currentChar == 'e' || currentChar == 'E')) {
                sb.append((char) currentChar);
                advance();
                if (!eof && (currentChar == '+' || currentChar == '-')) {
                    sb.append((char) currentChar);
                    advance();
                }
                if (!eof && isDigit(currentChar)) {
                    while (!eof && isDigit(currentChar)) {
                        sb.append((char) currentChar);
                        advance();
                    }
                } else {
                    throw new JsonDataException("Expected digit in exponent");
                }
            }

            return new JsonToken(JsonToken.Type.NUMBER, sb.toString());
        }

        @Nonnull
        JsonToken parseTrue() throws IOException {
            if (matchKeyword("true")) {
                return new JsonToken(JsonToken.Type.TRUE, null);
            }
            throw new JsonDataException("Invalid token, expected 'true'");
        }

        @Nonnull
        JsonToken parseFalse() throws IOException {
            if (matchKeyword("false")) {
                return new JsonToken(JsonToken.Type.FALSE, null);
            }
            throw new JsonDataException("Invalid token, expected 'false'");
        }

        @Nonnull
        JsonToken parseNull() throws IOException {
            if (matchKeyword("null")) {
                return new JsonToken(JsonToken.Type.NULL, null);
            }
            throw new JsonDataException("Invalid token, expected 'null'");
        }

        private boolean matchKeyword(@Nonnull String keyword) throws IOException {
            for (int i = 0; i < keyword.length(); i++) {
                if (eof || currentChar != keyword.charAt(i)) {
                    return false;
                }
                advance();
            }
            return true;
        }
    }

    private static class JsonToken {
        enum Type {
            LEFT_BRACE, RIGHT_BRACE,
            LEFT_BRACKET, RIGHT_BRACKET,
            COMMA, COLON,
            STRING, NUMBER,
            TRUE, FALSE, NULL
        }

        private final @Nonnull Type type;
        private final @Nullable String value;

        JsonToken(@Nonnull Type type, @Nullable String value) {
            this.type = type;
            this.value = value;
        }
    }
}