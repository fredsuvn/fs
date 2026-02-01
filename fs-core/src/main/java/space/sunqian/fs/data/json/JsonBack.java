package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.Charset;

final class JsonBack {

    static boolean isNullValue(@Nullable Object value) {
        return value == null || value instanceof JsonNull;
    }

    static @Nonnull JsonData wrap(@Nonnull Object value) {
        if (value instanceof String) {
            return new JsonString((String) value);
        }
        throw new JsonDataException("Unsupported type: " + value.getClass().getName());
    }

    private static abstract class AbstractJsonData implements JsonData {

        @Override
        public @Nonnull String asString() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON string.");
        }

        @Override
        public @Nonnull JsonMap asObject() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON map.");
        }

        @Override
        public @Nonnull JsonList asArray() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON array.");
        }

        @Override
        public int asInt() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON number.");
        }

        @Override
        public long asLong() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON number.");
        }

        @Override
        public float asFloat() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON number.");
        }

        @Override
        public double asDouble() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON number.");
        }

        @Override
        public @Nonnull BigDecimal asBigDecimal() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON number.");
        }

        @Override
        public boolean asBoolean() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON boolean.");
        }

        @Override
        public @Nonnull JsonNull asNUll() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON null.");
        }

        @Override
        public void writeTo(@Nonnull OutputStream out, @Nonnull Charset charset) throws IORuntimeException {
            Writer writer = IOKit.newWriter(out, charset);
            writeTo(writer);
        }

        protected abstract void writeTo(@Nonnull Writer writer) throws IORuntimeException;
    }

    private static final class JsonString extends AbstractJsonData {

        private final @Nonnull String value;

        private JsonString(@Nonnull String value) {
            this.value = value;
        }

        @Override
        public @Nonnull JsonType type() {
            return JsonType.STRING;
        }

        @Override
        public @Nonnull String asString() throws JsonDataException {
            return value;
        }

        @Override
        public <T> T asObject(Type type) throws JsonDataException {
            return null;
        }

        @Override
        protected void writeTo(@Nonnull Writer writer) throws IORuntimeException {
            Fs.uncheck(() -> {
                writer.write("\"");
                writer.write(value);
                writer.write("\"");
            }, IORuntimeException::new);
        }

        @Override
        public String toString() {
            return toJsonString();
        }
    }

    private JsonBack() {
    }
}