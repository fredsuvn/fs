package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Map;

enum DefaultJsonFormatter implements JsonFormatter {
    INST;

    @Override
    public void format(
        @Nonnull Object jsonData,
        @Nonnull OutputStream output,
        @Nonnull Charset charset,
        @Nonnull JsonPropertyMapper propertyMapper
    ) throws JsonDataException {
        JsonKit.format(jsonData, output, charset, propertyMapper);
    }

    private void writeObject(
        @Nullable Object jsonData,
        @Nonnull OutputStream output,
        @Nonnull Charset charset,
        @Nonnull JsonPropertyMapper propertyMapper
    ) throws Exception {
        if (jsonData instanceof String) {
            writeString((String) jsonData, output, charset);
        } else if (jsonData instanceof Number) {
            writeNumber((Number) jsonData, output, charset);
        } else if (jsonData instanceof Boolean) {
            writeBoolean((Boolean) jsonData, output, charset);
        } else if (jsonData == null) {
            writeNull(output, charset);
        } else if (jsonData instanceof Iterable<?>) {
            Iterable<Object> iterable = Fs.as(jsonData);
            output.write("[".getBytes(charset));
            for (Object object : iterable) {
                BigDecimal decimal = new BigDecimal(object.toString());
                decimal.toString();
            }
            output.write("]".getBytes(charset));
        }
    }

    private void writeString(
        @Nonnull String jsonData,
        @Nonnull OutputStream output,
        @Nonnull Charset charset
    ) throws Exception {
        output.write("\"".getBytes(charset));
        output.write(jsonData.getBytes(charset));
        output.write("\"".getBytes(charset));
    }

    private void writeNumber(
        @Nonnull Number jsonData,
        @Nonnull OutputStream output,
        @Nonnull Charset charset
    ) throws Exception {
        output.write(jsonData.toString().getBytes(charset));
    }

    private void writeBoolean(
        @Nonnull Boolean jsonData,
        @Nonnull OutputStream output,
        @Nonnull Charset charset
    ) throws Exception {
        output.write(jsonData.toString().getBytes(charset));
    }

    private void writeNull(
        @Nonnull OutputStream output,
        @Nonnull Charset charset
    ) throws Exception {
        output.write("null".getBytes(charset));
    }
}
