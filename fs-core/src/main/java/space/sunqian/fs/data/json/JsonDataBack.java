package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final class JsonDataBack {

    static @Nonnull JsonData ofNull() {
        return JsonNull.INST;
    }

    static @Nonnull JsonData ofBoolean(boolean bool) {
        return bool ? JsonBoolean.TRUE : JsonBoolean.FALSE;
    }

    static @Nonnull JsonData ofString(@Nonnull String string) {
        return new JsonString(string);
    }

    static @Nonnull JsonData ofNumber(@Nonnull Number number) {
        return new JsonNumber(number);
    }

    static @Nonnull JsonData ofMap(@Nonnull @RetainedParam Map<@Nonnull String, @Nullable Object> map) {
        return new JsonObject(map);
    }

    static @Nonnull JsonData ofList(@Nonnull @RetainedParam List<@Nullable Object> array) {
        return new JsonArray(array);
    }

    static @Nonnull JsonData ofArray(@Nullable Object @Nonnull @RetainedParam ... array) {
        return new JsonArray(array);
    }

    private interface DefaultData extends JsonData {

        @Override
        default @Nonnull String asString() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON string.");
        }

        @Override
        default @Nonnull Map<String, Object> asMap() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON object.");
        }

        @Override
        default @Nonnull List<Object> asList() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON array.");
        }

        @Override
        default @Nonnull Number asNumber() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON number.");
        }

        @Override
        default boolean asBoolean() throws JsonDataException {
            throw new JsonDataException("Current JSON data is not a JSON boolean.");
        }

        @Override
        default void writeTo(@Nonnull OutputStream out) throws IORuntimeException {
            writeTo(IOKit.newWriter(out));
        }

        @Override
        default void writeTo(@Nonnull WritableByteChannel channel) throws IORuntimeException {
            writeTo(Channels.newOutputStream(channel));
        }
    }

    abstract static class AbsData implements DefaultData {

        @Override
        public void writeTo(@Nonnull Appendable appender) throws IORuntimeException {
            try {
                doWrite(appender);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public @Nonnull String toString() {
            StringBuilder sb = new StringBuilder();
            writeTo(sb);
            return sb.toString();
        }

        protected abstract void doWrite(@Nonnull Appendable appender) throws Exception;
    }

    private static final class JsonNull implements DefaultData {

        private static final @Nonnull JsonNull INST = new JsonNull();

        @Override
        public @Nonnull JsonType type() {
            return JsonType.NULL;
        }

        @Override
        public void writeTo(@Nonnull Appendable appender) throws IORuntimeException {
            JsonKit.toJsonString(null, appender);
        }

        @Override
        public @Nonnull String toString() {
            return Fs.NULL_STRING;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof JsonData) {
                @SuppressWarnings("PatternVariableCanBeUsed")
                JsonData that = (JsonData) o;
                return type().equals(that.type());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    private static final class JsonBoolean implements DefaultData {

        private static final @Nonnull JsonBoolean TRUE = new JsonBoolean(true);
        private static final @Nonnull JsonBoolean FALSE = new JsonBoolean(false);

        private final boolean bool;

        JsonBoolean(boolean bool) {
            this.bool = bool;
        }

        @Override
        public @Nonnull JsonType type() {
            return JsonType.BOOLEAN;
        }

        @Override
        public boolean asBoolean() throws JsonDataException {
            return bool;
        }

        @Override
        public void writeTo(@Nonnull Appendable appender) throws IORuntimeException {
            JsonKit.toJsonString(bool, appender);
        }

        @Override
        public @Nonnull String toString() {
            return Boolean.toString(bool);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof JsonData) {
                @SuppressWarnings("PatternVariableCanBeUsed")
                JsonData that = (JsonData) o;
                return type().equals(that.type()) && Objects.equals(bool, that.asBoolean());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Boolean.valueOf(bool).hashCode();
        }
    }

    static final class JsonString extends AbsData {

        private final @Nonnull String string;

        JsonString(@Nonnull String string) {
            this.string = string;
        }

        @Override
        public @Nonnull JsonType type() {
            return JsonType.STRING;
        }

        @Override
        public @Nonnull String asString() throws JsonDataException {
            return string;
        }

        @Override
        public void doWrite(@Nonnull Appendable appender) throws Exception {
            appender.append("\"");
            appender.append(string);
            appender.append("\"");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof JsonData) {
                @SuppressWarnings("PatternVariableCanBeUsed")
                JsonData that = (JsonData) o;
                return type().equals(that.type()) && Objects.equals(string, that.asString());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return string.hashCode();
        }
    }

    static final class JsonNumber extends AbsData {

        private final @Nonnull Number number;

        JsonNumber(@Nonnull Number number) {
            this.number = number;
        }

        @Override
        public @Nonnull JsonType type() {
            return JsonType.NUMBER;
        }

        @Override
        public @Nonnull Number asNumber() throws JsonDataException {
            return number;
        }

        @Override
        public void doWrite(@Nonnull Appendable appender) throws Exception {
            appender.append(number.toString());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof JsonData) {
                @SuppressWarnings("PatternVariableCanBeUsed")
                JsonData that = (JsonData) o;
                return type().equals(that.type()) && Objects.equals(number, that.asNumber());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return number.hashCode();
        }
    }

    static final class JsonObject extends AbsData {

        private final @Nonnull Map<@Nonnull String, @Nullable Object> dataMap;

        JsonObject(@Nonnull Map<@Nonnull String, @Nullable Object> dataMap) {
            this.dataMap = dataMap;
        }

        @Override
        public @Nonnull JsonType type() {
            return JsonType.OBJECT;
        }

        @Override
        public @Nonnull Map<String, Object> asMap() throws JsonDataException {
            return dataMap;
        }

        @Override
        public void doWrite(@Nonnull Appendable appender) {
            JsonKit.toJsonString(dataMap, appender);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof JsonData) {
                @SuppressWarnings("PatternVariableCanBeUsed")
                JsonData that = (JsonData) o;
                return type().equals(that.type()) && Objects.equals(dataMap, that.asMap());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return dataMap.hashCode();
        }
    }

    static final class JsonArray extends AbsData {

        private final @Nonnull List<@Nullable Object> dataList;

        JsonArray(@Nonnull List<@Nullable Object> dataList) {
            this.dataList = dataList;
        }

        JsonArray(@Nullable Object @Nonnull [] dataList) {
            this.dataList = ArrayKit.asList(dataList);
        }

        @Override
        public @Nonnull JsonType type() {
            return JsonType.ARRAY;
        }

        @Override
        public @Nonnull List<@Nullable Object> asList() throws JsonDataException {
            return dataList;
        }

        @Override
        public void doWrite(@Nonnull Appendable appender) {
            JsonKit.toJsonString(dataList, appender);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof JsonData) {
                @SuppressWarnings("PatternVariableCanBeUsed")
                JsonData that = (JsonData) o;
                return type().equals(that.type()) && Objects.equals(dataList, that.asList());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return dataList.hashCode();
        }
    }

    private JsonDataBack() {
    }
}
