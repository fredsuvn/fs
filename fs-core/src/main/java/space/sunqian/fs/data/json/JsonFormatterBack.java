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

import java.io.Writer;
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
            ConvertKit.objectSchemaParser(),
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
        public void formatTo(@Nullable Object data, @Nonnull Writer writer) throws IORuntimeException {
            Fs.uncheck(() -> writeAny(data, writer), IORuntimeException::new);
        }

        private void writeAny(@Nullable Object any, @Nonnull Writer writer) throws Exception {
            if (any == null) {
                writeDirect("null", writer);
                return;
            }
            if (any instanceof CharSequence) {
                writeString(any.toString(), writer);
                return;
            }
            if (any instanceof Boolean) {
                writeDirect(any.toString(), writer);
                return;
            }
            if (any instanceof Number) {
                writeDirect(any.toString(), writer);
                return;
            }
            if (any instanceof Date) {
                writeString(any.toString(), writer);
                return;
            }
            if (any instanceof TemporalAccessor) {
                writeString(any.toString(), writer);
                return;
            }
            if (any instanceof Enum) {
                writeString(any.toString(), writer);
                return;
            }
            if (any instanceof Map<?, ?>) {
                writeMap((Map<?, ?>) any, writer);
                return;
            }
            writeObject(any, writer);
        }

        private void writeString(@Nonnull String string, @Nonnull Writer writer) throws Exception {
            writeDirect("\"", writer);
            writeDirect(string.replace("\"", "\\\""), writer);
            writeDirect("\"", writer);
        }

        private void writeDirect(@Nonnull String string, @Nonnull Writer writer) throws Exception {
            writer.write(string);
        }

        private void writeMap(@Nonnull Map<?, ?> map, @Nonnull Writer writer) throws Exception {
            writeDirect("{", writer);
            boolean comma = false;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (ignoreNullValue && value == null) {
                    continue;
                }
                if (comma) {
                    writeDirect(",", writer);
                }
                writeString(String.valueOf(entry.getKey()), writer);
                writeDirect(":", writer);
                writeAny(value, writer);
                comma = true;
            }
            writeDirect("}", writer);
        }

        private void writeObject(@Nonnull Object object, @Nonnull Writer writer) throws Exception {
            ObjectSchema schema = objectParser.parse(object.getClass());
            writeDirect("{", writer);
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
                    writeDirect(",", writer);
                }
                writeProperty(property, value, writer);
                comma = true;
            }
            writeDirect("}", writer);
        }

        private void writeProperty(
            @Nonnull ObjectProperty property, @Nullable Object value, @Nonnull Writer writer
        ) throws Exception {
            writeString(property.name(), writer);
            writeDirect(":", writer);
            if (value instanceof Date || value instanceof TemporalAccessor) {
                DatePattern datePattern = property.getAnnotation(DatePattern.class);
                if (datePattern != null) {
                    String dateString = objectConverter.convert(
                        value,
                        String.class,
                        ConvertKit.getDateFormatterOption(datePattern)
                    );
                    writeString(dateString, writer);
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
                    writeDirect(numString, writer);
                    return;
                }
            }
            writeAny(value, writer);
        }
    }

    private JsonFormatterBack() {
    }
}
