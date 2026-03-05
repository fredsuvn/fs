package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.collect.ArrayKit;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Map;

final class JsonDataBack {

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

    enum JsonNull implements DefaultData {
        INST;

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
            return "null";
        }
    }

    enum JsonBoolean implements DefaultData {
        TRUE(true), FALSE(false);

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
    }

    private JsonDataBack() {
    }
}
