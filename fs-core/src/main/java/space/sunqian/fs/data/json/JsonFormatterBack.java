package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumPattern;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.object.schema.ObjectSchemaParser;
import space.sunqian.fs.utils.codec.Base64Kit;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

final class JsonFormatterBack {

    static @Nonnull JsonFormatter defaultFormatter() {
        return JsonFormatterImpl.DEFAULT;
    }

    static @Nonnull JsonFormatterImpl newFormatter(
        @Nonnull ObjectSchemaParser objectParser,
        @Nonnull ObjectConverter objectConverter,
        boolean ignoreNullValue
    ) {
        return new JsonFormatterImpl(objectParser, objectConverter, ignoreNullValue);
    }

    private static final class JsonFormatterImpl implements JsonFormatter {

        private static final @Nonnull JsonFormatterImpl DEFAULT = newFormatter(
            ObjectSchemaParser.defaultCachedParser(),
            ObjectConverter.defaultConverter(),
            false
        );

        private interface Formatter {

            void formatTo(
                @Nonnull JsonFormatterImpl impl,
                @Nonnull Object data,
                @Nonnull Appendable appender
            ) throws Exception;
        }

        private static final @Nonnull Map<@Nonnull Type, @Nonnull Formatter> FORMAT_MAP;

        static {
            FORMAT_MAP = new HashMap<>();
            FORMAT_MAP.put(String.class, (impl, obj, appender) -> impl.writeString((String) obj, appender));
            // FORMAT_MAP.put(boolean.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(Boolean.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(Byte.class, (impl, obj, appender) -> appender.append(obj.toString()));
            // FORMAT_MAP.put(short.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(Short.class, (impl, obj, appender) -> appender.append(obj.toString()));
            // FORMAT_MAP.put(char.class, (impl, obj, appender) -> appender.append(obj.toString()));
            // FORMAT_MAP.put(int.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(Integer.class, (impl, obj, appender) -> appender.append(obj.toString()));
            // FORMAT_MAP.put(long.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(Long.class, (impl, obj, appender) -> appender.append(obj.toString()));
            // FORMAT_MAP.put(float.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(Float.class, (impl, obj, appender) -> appender.append(obj.toString()));
            // FORMAT_MAP.put(double.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(Double.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(BigInteger.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(BigDecimal.class, (impl, obj, appender) -> appender.append(obj.toString()));
            FORMAT_MAP.put(boolean[].class, (impl, data, appender) -> impl.writeArray((boolean[]) data, appender));
            FORMAT_MAP.put(short[].class, (impl, data, appender) -> impl.writeArray((short[]) data, appender));
            FORMAT_MAP.put(int[].class, (impl, data, appender) -> impl.writeArray((int[]) data, appender));
            FORMAT_MAP.put(long[].class, (impl, data, appender) -> impl.writeArray((long[]) data, appender));
            FORMAT_MAP.put(float[].class, (impl, data, appender) -> impl.writeArray((float[]) data, appender));
            FORMAT_MAP.put(double[].class, (impl, data, appender) -> impl.writeArray((double[]) data, appender));
            // as base64
            FORMAT_MAP.put(byte[].class, (impl, data, appender) ->
                impl.writeString(Base64Kit.encoder().encodeToString((byte[]) data), appender));
            // as string
            FORMAT_MAP.put(char[].class, (impl, data, appender) ->
                impl.writeString(new String((char[]) data), appender));
            // as char
            FORMAT_MAP.put(Character.class, (impl, obj, appender) -> {
                char c = (char) obj;
                appender.append('\"');
                String escaped = ControlTables.toUnicodeEscape(c);
                if (escaped != null) {
                    appender.append(escaped);
                } else {
                    appender.append(c);
                }
                appender.append('\"');
            });
        }

        private final @Nonnull ObjectSchemaParser objectParser;
        private final @Nonnull ObjectConverter objectConverter;
        private final boolean ignoreNullValue;

        JsonFormatterImpl(
            @Nonnull ObjectSchemaParser objectParser,
            @Nonnull ObjectConverter objectConverter,
            boolean ignoreNullValue
        ) {
            this.objectParser = objectParser;
            this.objectConverter = objectConverter;
            this.ignoreNullValue = ignoreNullValue;
        }

        @Override
        public void formatTo(@Nullable Object data, @Nonnull Appendable appender) throws IORuntimeException {
            try {
                writeAny(data, appender);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void formatTo(@Nullable Object data, @Nonnull OutputStream out) throws IORuntimeException {
            formatTo(data, IOKit.newWriter(out));
        }

        private void writeAny(@Nullable Object any, @Nonnull Appendable appender) throws Exception {
            if (any == null) {
                appender.append("null");
                return;
            }
            // if (any instanceof JsonData) {
            //     ((JsonData) any).writeTo(appender);
            //     return;
            // }
            Formatter formatter = FORMAT_MAP.get(any.getClass());
            if (formatter != null) {
                formatter.formatTo(this, any, appender);
                return;
            }
            if (any instanceof CharSequence) {
                writeString(any.toString(), appender);
                return;
            }
            if (any instanceof Date) {
                writeString(any.toString(), appender);
                return;
            }
            if (any instanceof TemporalAccessor) {
                writeString(any.toString(), appender);
                return;
            }
            if (any instanceof Enum) {
                writeString(any.toString(), appender);
                return;
            }
            if (any instanceof Map<?, ?>) {
                writeMap((Map<?, ?>) any, appender);
                return;
            }
            if (any instanceof Iterable<?>) {
                writeArray((Iterable<?>) any, appender);
                return;
            }
            if (any instanceof Object[]) {
                writeArray((Object[]) any, appender);
                return;
            }
            if (any instanceof ByteBuffer) {
                // as base64
                writeString(Base64Kit.encoder().encodeToString((ByteBuffer) any), appender);
                return;
            }
            writeObject(any, appender);
        }

        private void writeString(@Nonnull String string, @Nonnull Appendable appender) throws Exception {
            appender.append('\"');
            int s = 0;
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                switch (c) {
                    case '"':
                        appender.append(string, s, i);
                        s = i + 1;
                        appender.append("\\\"");
                        continue;
                    case '\\':
                        appender.append(string, s, i);
                        s = i + 1;
                        appender.append("\\\\");
                        continue;
                    default:
                        String escaped = ControlTables.toUnicodeEscape(c);
                        if (escaped != null) {
                            appender.append(string, s, i);
                            s = i + 1;
                            appender.append(escaped);
                        }
                }
            }
            appender.append(string, s, string.length());
            appender.append('\"');
        }

        private void writeMap(@Nonnull Map<?, ?> map, @Nonnull Appendable appender) throws Exception {
            appender.append('{');
            boolean[] isFirst = {true};
            map.forEach((key, value) -> {
                if (ignoreNullValue && value == null) {
                    return;
                }
                try {
                    if (isFirst[0]) {
                        isFirst[0] = false;
                    } else {
                        appender.append(',');
                    }
                    writeString(String.valueOf(key), appender);
                    appender.append(':');
                    writeAny(value, appender);
                } catch (Exception e) {
                    throw new JsonDataException(e);
                }
            });
            appender.append('}');
        }

        private void writeObject(@Nonnull Object object, @Nonnull Appendable appender) throws Exception {
            ObjectSchema schema = objectParser.parse(object.getClass());
            appender.append('{');
            boolean[] isFirst = {true};
            schema.properties().forEach((key, property) -> {
                try {
                    // ignore class
                    if ("class".equals(property.name())) {
                        return;
                    }
                    Object value = property.getValue(object);
                    if (ignoreNullValue && value == null) {
                        return;
                    }
                    if (isFirst[0]) {
                        isFirst[0] = false;
                    } else {
                        appender.append(',');
                    }
                    writeProperty(property, value, appender);
                } catch (Exception e) {
                    throw new JsonDataException(e);
                }
            });
            appender.append('}');
        }

        private void writeProperty(
            @Nonnull ObjectProperty property, @Nullable Object value, @Nonnull Appendable appender
        ) throws Exception {
            writeString(property.name(), appender);
            appender.append(':');
            if (value != null) {
                if (value instanceof Date || value instanceof TemporalAccessor) {
                    DatePattern datePattern = property.getAnnotation(DatePattern.class);
                    if (datePattern != null) {
                        String dateString = objectConverter.convert(
                            value,
                            String.class,
                            ConvertKit.getDateFormatterOption(datePattern)
                        );
                        writeString(dateString, appender);
                        return;
                    }
                }
                if (value instanceof Number) {
                    NumPattern numPattern = property.getAnnotation(NumPattern.class);
                    if (numPattern != null) {
                        String numString = objectConverter.convert(
                            value,
                            String.class,
                            ConvertKit.getNumFormatterOption(numPattern)
                        );
                        appender.append(numString);
                        return;
                    }
                }
            }
            writeAny(value, appender);
        }

        private void writeArray(@Nonnull Iterable<?> array, @Nonnull Appendable appender) throws Exception {
            appender.append('[');
            boolean comma = false;
            for (Object object : array) {
                if (comma) {
                    appender.append(',');
                }
                writeAny(object, appender);
                comma = true;
            }
            appender.append(']');
        }

        private void writeArray(Object @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append('[');
            boolean comma = false;
            for (Object object : array) {
                if (comma) {
                    appender.append(',');
                }
                writeAny(object, appender);
                comma = true;
            }
            appender.append(']');
        }

        private void writeArray(boolean @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append('[');
            boolean comma = false;
            for (boolean element : array) {
                if (comma) {
                    appender.append(',');
                }
                appender.append(Boolean.toString(element));
                comma = true;
            }
            appender.append(']');
        }

        private void writeArray(short @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append('[');
            boolean comma = false;
            for (short element : array) {
                if (comma) {
                    appender.append(',');
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            appender.append(']');
        }

        private void writeArray(int @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append('[');
            boolean comma = false;
            for (int element : array) {
                if (comma) {
                    appender.append(',');
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            appender.append(']');
        }

        private void writeArray(long @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append('[');
            boolean comma = false;
            for (long l : array) {
                if (comma) {
                    appender.append(',');
                }
                appender.append(String.valueOf(l));
                comma = true;
            }
            appender.append(']');
        }

        private void writeArray(float @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append('[');
            boolean comma = false;
            for (float element : array) {
                if (comma) {
                    appender.append(',');
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            appender.append(']');
        }

        private void writeArray(double @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append('[');
            boolean comma = false;
            for (double element : array) {
                if (comma) {
                    appender.append(',');
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            appender.append(']');
        }
    }

    private static final class ControlTables {

        private static final @Nonnull String @Nonnull [] CONTROL_CHAR_ESCAPE_TABLE;

        static {
            CONTROL_CHAR_ESCAPE_TABLE = new String[32];
            for (int i = 0; i < CONTROL_CHAR_ESCAPE_TABLE.length; i++) {
                CONTROL_CHAR_ESCAPE_TABLE[i] = String.format("\\u%04X", i);
            }
            CONTROL_CHAR_ESCAPE_TABLE['\b'] = "\\b";
            CONTROL_CHAR_ESCAPE_TABLE['\t'] = "\\t";
            CONTROL_CHAR_ESCAPE_TABLE['\n'] = "\\n";
            CONTROL_CHAR_ESCAPE_TABLE['\r'] = "\\r";
            CONTROL_CHAR_ESCAPE_TABLE['\f'] = "\\f";
            // case '\b':
            //     appender.append(string, s, i);
            //     s = i + 1;
            //     appender.append('\\');
            //     appender.append('b');
            //     continue;
            // case '\f':
            //     appender.append(string, s, i);
            //     s = i + 1;
            //     appender.append('\\');
            //     appender.append('f');
            //     continue;
            // case '\n':
            //     appender.append(string, s, i);
            //     s = i + 1;
            //     appender.append('\\');
            //     appender.append('n');
            //     continue;
            // case '\r':
            //     appender.append(string, s, i);
            //     s = i + 1;
            //     appender.append('\\');
            //     appender.append('r');
            //     continue;
            // case '\t':
            //     appender.append(string, s, i);
            //     s = i + 1;
            //     appender.append('\\');
            //     appender.append('t');
            //     continue;
        }

        private static @Nullable String toUnicodeEscape(char c) {
            if (c >= CONTROL_CHAR_ESCAPE_TABLE.length) {
                return null;
            }
            return CONTROL_CHAR_ESCAPE_TABLE[c];
        }
    }

    private JsonFormatterBack() {
    }
}
