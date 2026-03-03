package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
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
            Fs.uncheck(() -> writeAny(data, appender), IORuntimeException::new);
        }

        private void writeAny(@Nullable Object any, @Nonnull Appendable appender) throws Exception {
            if (any == null) {
                writeDirect("null", appender);
                return;
            }
            if (any instanceof CharSequence) {
                writeString(any.toString(), appender);
                return;
            }
            if (any instanceof Boolean) {
                writeDirect(any.toString(), appender);
                return;
            }
            if (any instanceof Number) {
                writeDirect(any.toString(), appender);
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
            writeDirect("\"", appender);
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (c == '\\') {
                    writeDirect("\\\\", appender);
                } else if (c == '\"') {
                    writeDirect("\\\"", appender);
                } else {
                    appender.append(c);
                }
            }
            writeDirect("\"", appender);
        }

        private void writeDirect(@Nonnull String string, @Nonnull Appendable appender) throws Exception {
            appender.append(string);
        }

        private void writeMap(@Nonnull Map<?, ?> map, @Nonnull Appendable appender) throws Exception {
            writeDirect("{", appender);
            boolean comma = false;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (ignoreNullValue && value == null) {
                    continue;
                }
                if (comma) {
                    writeDirect(",", appender);
                }
                writeString(String.valueOf(entry.getKey()), appender);
                writeDirect(":", appender);
                writeAny(value, appender);
                comma = true;
            }
            writeDirect("}", appender);
        }

        private void writeObject(@Nonnull Object object, @Nonnull Appendable appender) throws Exception {
            ObjectSchema schema = objectParser.parse(object.getClass());
            writeDirect("{", appender);
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
                    writeDirect(",", appender);
                }
                writeProperty(property, value, appender);
                comma = true;
            }
            writeDirect("}", appender);
        }

        private void writeProperty(
            @Nonnull ObjectProperty property, @Nullable Object value, @Nonnull Appendable appender
        ) throws Exception {
            writeString(property.name(), appender);
            writeDirect(":", appender);
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
                    writeDirect(numString, appender);
                    return;
                }
            }
            writeAny(value, appender);
        }

        private void writeArray(@Nonnull Iterable<?> array, @Nonnull Appendable appender) throws Exception {
            writeDirect("[", appender);
            boolean comma = false;
            for (Object object : array) {
                if (comma) {
                    writeDirect(",", appender);
                }
                writeAny(object, appender);
                comma = true;
            }
            writeDirect("]", appender);
        }

        private void writeArray(Object @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            writeDirect("[", appender);
            boolean comma = false;
            for (Object object : array) {
                if (comma) {
                    writeDirect(",", appender);
                }
                writeAny(object, appender);
                comma = true;
            }
            writeDirect("]", appender);
        }

        private void writeArray(boolean @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            writeDirect("[", appender);
            boolean comma = false;
            for (boolean element : array) {
                if (comma) {
                    writeDirect(",", appender);
                }
                writeDirect(element ? "true" : "false", appender);
                comma = true;
            }
            writeDirect("]", appender);
        }

        private void writeArray(short @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            writeDirect("[", appender);
            boolean comma = false;
            for (short element : array) {
                if (comma) {
                    writeDirect(",", appender);
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            writeDirect("]", appender);
        }

        private void writeArray(int @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            writeDirect("[", appender);
            boolean comma = false;
            for (int element : array) {
                if (comma) {
                    writeDirect(",", appender);
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            writeDirect("]", appender);
        }

        private void writeArray(long @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            writeDirect("[", appender);
            boolean comma = false;
            for (long element : array) {
                if (comma) {
                    writeDirect(",", appender);
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            writeDirect("]", appender);
        }

        private void writeArray(float @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            writeDirect("[", appender);
            boolean comma = false;
            for (float element : array) {
                if (comma) {
                    writeDirect(",", appender);
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            writeDirect("]", appender);
        }

        private void writeArray(double @Nonnull [] array, @Nonnull Appendable appender) throws Exception {
            writeDirect("[", appender);
            boolean comma = false;
            for (double element : array) {
                if (comma) {
                    writeDirect(",", appender);
                }
                appender.append(String.valueOf(element));
                comma = true;
            }
            writeDirect("]", appender);
        }
    }

    private JsonFormatterBack() {
    }
}
