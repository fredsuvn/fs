package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.IORuntimeException;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumPattern;
import space.sunqian.fs.object.convert.ConvertKit;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.schema.ObjectProperty;
import space.sunqian.fs.object.schema.ObjectSchema;
import space.sunqian.fs.object.schema.ObjectSchemaParser;
import space.sunqian.fs.utils.codec.Base64Kit;

import java.nio.ByteBuffer;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
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

        private void writeAny(@Nullable Object any, @Nonnull Appendable appender) throws Exception {
            if (any == null) {
                appender.append("null");
                return;
            }
            if (any instanceof String) {
                writeString((String) any, appender);
                return;
            }
            if (any instanceof CharSequence) {
                writeString(any.toString(), appender);
                return;
            }
            if (any instanceof Boolean) {
                appender.append(any.toString());
                return;
            }
            if (any instanceof Number) {
                appender.append(any.toString());
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
            if (any instanceof boolean[]) {
                writeArray((boolean[]) any, appender);
                return;
            }
            if (any instanceof byte[]) {
                // base64
                writeString(Base64Kit.encoder().encodeToString((byte[]) any), appender);
                return;
            }
            if (any instanceof ByteBuffer) {
                // base64
                writeString(Base64Kit.encoder().encodeToString(((ByteBuffer) any)), appender);
                return;
            }
            if (any instanceof short[]) {
                writeArray((short[]) any, appender);
                return;
            }
            if (any instanceof char[]) {
                // string
                writeString(new String((char[]) any), appender);
                return;
            }
            if (any instanceof int[]) {
                writeArray((int[]) any, appender);
                return;
            }
            if (any instanceof long[]) {
                writeArray((long[]) any, appender);
                return;
            }
            if (any instanceof float[]) {
                writeArray((float[]) any, appender);
                return;
            }
            if (any instanceof double[]) {
                writeArray((double[]) any, appender);
                return;
            }
            writeObject(any, appender);
        }

        private void writeString(@Nonnull String string, @Nonnull Appendable appender) throws Exception {
            appender.append("\"");
            char[] chars = string.toCharArray();
            for (char c : chars) {
                switch (c) {
                    case '"':
                        appender.append("\\\"");
                        continue;
                    case '\\':
                        appender.append("\\\\");
                        continue;
                    case '\b':
                        appender.append("\\b");
                        continue;
                    case '\f':
                        appender.append("\\f");
                        continue;
                    case '\n':
                        appender.append("\\n");
                        continue;
                    case '\r':
                        appender.append("\\r");
                        continue;
                    case '\t':
                        appender.append("\\t");
                        continue;
                    default:
                        String escaped = CharsKit.toUnicodeEscape(c);
                        if (escaped != null) {
                            appender.append(escaped);
                        } else {
                            appender.append(c);
                        }
                }
            }
            appender.append("\"");
        }

        private void writeMap(@Nonnull Map<?, ?> map, @Nonnull Appendable appender) throws Exception {
            appender.append("{");
            boolean comma = false;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (ignoreNullValue && value == null) {
                    continue;
                }
                if (comma) {
                    appender.append(",");
                }
                writeString(String.valueOf(entry.getKey()), appender);
                appender.append(":");
                writeAny(value, appender);
                comma = true;
            }
            appender.append("}");
        }

        private void writeObject(@Nonnull Object object, @Nonnull Appendable appender) throws Exception {
            ObjectSchema schema = objectParser.parse(object.getClass());
            appender.append("{");
            boolean comma = false;
            for (Map.Entry<@Nonnull String, @Nonnull ObjectProperty> entry : schema.properties().entrySet()) {
                ObjectProperty property = entry.getValue();
                // ignore class
                if ("class".equals(property.name())) {
                    continue;
                }
                Object value = property.getValue(object);
                if (ignoreNullValue && value == null) {
                    continue;
                }
                if (comma) {
                    appender.append(",");
                }
                writeProperty(property, value, appender);
                comma = true;
            }
            appender.append("}");
        }

        private void writeProperty(
            @Nonnull ObjectProperty property, @Nullable Object value, @Nonnull Appendable appender
        ) throws Exception {
            writeString(property.name(), appender);
            appender.append(":");
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
            appender.append("[");
            boolean comma = false;
            for (Object object : array) {
                if (comma) {
                    appender.append(",");
                }
                writeAny(object, appender);
                comma = true;
            }
            appender.append("]");
        }

        private void writeArray(Object @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append("[");
            boolean comma = false;
            for (Object object : array) {
                if (comma) {
                    appender.append(",");
                }
                writeAny(object, appender);
                comma = true;
            }
            appender.append("]");
        }

        private void writeArray(boolean @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append("[");
            boolean comma = false;
            for (boolean element : array) {
                if (comma) {
                    appender.append(",");
                }
                appender.append(element ? "true" : "false");
                comma = true;
            }
            appender.append("]");
        }

        private void writeArray(short @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append("[");
            boolean comma = false;
            for (short element : array) {
                if (comma) {
                    appender.append(",");
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            appender.append("]");
        }

        private void writeArray(int @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append("[");
            boolean comma = false;
            for (int element : array) {
                if (comma) {
                    appender.append(",");
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            appender.append("]");
        }

        private void writeArray(long @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append("[");
            boolean comma = false;
            for (long element : array) {
                if (comma) {
                    appender.append(",");
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            appender.append("]");
        }

        private void writeArray(float @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append("[");
            boolean comma = false;
            for (float element : array) {
                if (comma) {
                    appender.append(",");
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            appender.append("]");
        }

        private void writeArray(double @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            appender.append("[");
            boolean comma = false;
            for (double element : array) {
                if (comma) {
                    appender.append(",");
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            appender.append("]");
        }
    }

    private JsonFormatterBack() {
    }
}
