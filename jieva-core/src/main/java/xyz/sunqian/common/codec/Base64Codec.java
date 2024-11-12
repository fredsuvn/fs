package xyz.sunqian.common.codec;

import lombok.Data;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.io.ByteStream;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Base64 implementation of {@link CodecConfigurator} to encode/decode base64 with {@link Base64}.
 *
 * @author fredsuvn
 */
public class Base64Codec implements CodecConfigurator<Base64Codec> {

    private static final int ENCODE_MODE = 0;
    private static final int DECODE_MODE = 1;
    private static final int BASIC_TYPE = 0;
    private static final int URL_TYPE = 1;
    private static final int MIME_TYPE = 2;

    private Object input;
    private Object output;
    private int blockSize;
    private int mode;
    private int type;
    private boolean withoutPadding;
    private MimeParams mimeParams;

    Base64Codec() {
        reset();
    }

    @Override
    public Base64Codec input(byte[] array) {
        this.input = array;
        return this;
    }

    @Override
    public Base64Codec input(ByteBuffer buffer) {
        this.input = buffer;
        return this;
    }

    @Override
    public Base64Codec input(InputStream in) {
        this.input = in;
        return this;
    }

    @Override
    public Base64Codec output(byte[] array) {
        this.output = array;
        return this;
    }

    @Override
    public Base64Codec output(ByteBuffer buffer) {
        this.output = buffer;
        return this;
    }

    @Override
    public Base64Codec output(OutputStream out) {
        this.output = out;
        return this;
    }

    @Override
    public Base64Codec reset() {
        this.input = null;
        this.output = null;
        this.blockSize = JieIO.BUFFER_SIZE;
        this.mode = ENCODE_MODE;
        this.type = BASIC_TYPE;
        this.withoutPadding = false;
        this.mimeParams = null;
        return this;
    }

    /**
     * Sets block size for encoding/decoding operations.
     *
     * @param blockSize block size
     * @return this
     */
    public Base64Codec blockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    /**
     * Sets mode to encoding which backed by {@link Base64.Encoder}.
     *
     * @return this
     */
    public Base64Codec encode() {
        this.mode = ENCODE_MODE;
        return this;
    }

    /**
     * Sets mode to decoding which backed by {@link Base64.Decoder}.
     *
     * @return this
     */
    public Base64Codec decode() {
        this.mode = DECODE_MODE;
        return this;
    }

    /**
     * Sets type to basic which backed by {@link Base64#getEncoder()} or {@link Base64#getDecoder()}.
     *
     * @return this
     */
    public Base64Codec basic() {
        this.type = BASIC_TYPE;
        return this;
    }

    /**
     * Sets type to URL and Filename safe type which backed by {@link Base64#getUrlEncoder()} or
     * {@link Base64#getUrlDecoder()}.
     *
     * @return this
     */
    public Base64Codec url() {
        this.type = URL_TYPE;
        return this;
    }

    /**
     * Sets type to MIME type which backed by {@link Base64#getMimeEncoder()} or {@link Base64#getMimeDecoder()}.
     *
     * @return this
     */
    public Base64Codec mime() {
        this.type = URL_TYPE;
        return this;
    }

    /**
     * Sets type to MIME type which backed by {@link Base64#getMimeEncoder(int, byte[])}  or
     * {@link Base64#getMimeDecoder()}.
     *
     * @param lineLength    the length of each output line (rounded down to nearest multiple of 4). If
     *                      {@code lineLength <= 0} the output will not be separated in lines
     * @param lineSeparator the line separator for each output line
     * @return this
     */
    public Base64Codec mime(int lineLength, byte[] lineSeparator) {
        this.type = URL_TYPE;
        this.mimeParams = new MimeParams(lineLength, lineSeparator);
        return this;
    }

    /**
     * Sets whether the encoding/decoding without padding: {@link Base64.Encoder#withoutPadding()}.
     *
     * @return this
     */
    public Base64Codec withoutPadding(boolean withoutPadding) {
        this.withoutPadding = withoutPadding;
        return this;
    }

    @Override
    public long doFinal() {
        switch (mode) {
            case ENCODE_MODE:
                return doEncode();
            case DECODE_MODE:
                return doDecode();
        }
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    private long doEncode() {
        try {
            Base64.Encoder encoder = getEncoder();
            if (input instanceof byte[]) {
                byte[] src = (byte[]) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    return encoder.encode(src, dest);
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    if (JieBuffer.isSimpleWrapper(dest)) {
                        int writeNum = encoder.encode(src, dest.array());
                        dest.position(writeNum);
                        return writeNum;
                    }
                    byte[] encoded = encoder.encode(src);
                    dest.put(encoded);
                    return encoded.length;
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    byte[] encoded = encoder.encode(src);
                    dest.write(encoded);
                    return encoded.length;
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof ByteBuffer) {
                ByteBuffer src = (ByteBuffer) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    if (JieBuffer.isSimpleWrapper(src)) {
                        int writeNum = encoder.encode(src.array(), dest);
                        src.position(src.limit());
                        return writeNum;
                    }
                    ByteBuffer encoded = encoder.encode(src);
                    int writeNum = encoded.remaining();
                    JieBuffer.readTo(encoded, dest);
                    return writeNum;
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    if (JieBuffer.isSimpleWrapper(src) && JieBuffer.isSimpleWrapper(dest)) {
                        int writeNum = encoder.encode(src.array(), dest.array());
                        src.position(src.limit());
                        dest.position(writeNum);
                        return writeNum;
                    }
                    ByteBuffer encoded = encoder.encode(src);
                    int writeNum = encoded.remaining();
                    dest.put(encoded);
                    return writeNum;
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    ByteBuffer encoded = encoder.encode(src);
                    int writeNum = encoded.remaining();
                    if (JieBuffer.isSimpleWrapper(encoded)) {
                        dest.write(encoded.array());
                    } else {
                        dest.write(JieBuffer.read(encoded));
                    }
                    return writeNum;
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof InputStream) {
                InputStream src = (InputStream) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    byte[] in = JieIO.read(src);
                    return encoder.encode(in, dest);
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    if (JieBuffer.isSimpleWrapper(dest)) {
                        int writeNum = encoder.encode(JieIO.read(src), dest.array());
                        dest.position(writeNum);
                        return writeNum;
                    }
                    byte[] encoded = encoder.encode(JieIO.read(src));
                    dest.put(encoded);
                    return encoded.length;
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    OutputCounter counter = new OutputCounter(dest);
                    OutputStream wrapper = encoder.wrap(counter);
                    ByteStream.from(src).to(wrapper).blockSize(blockSize).start();
                    wrapper.close();
                    return counter.count;
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else {
                throw new CodecException("Unknown input type: " + input.getClass());
            }
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    private long doDecode() {
        try {
            Base64.Decoder decoder = getDecoder();
            if (input instanceof byte[]) {
                byte[] src = (byte[]) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    return decoder.decode(src, dest);
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    if (JieBuffer.isSimpleWrapper(dest)) {
                        int writeNum = decoder.decode(src, dest.array());
                        dest.position(writeNum);
                        return writeNum;
                    }
                    byte[] encoded = decoder.decode(src);
                    dest.put(encoded);
                    return encoded.length;
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    byte[] encoded = decoder.decode(src);
                    dest.write(encoded);
                    return encoded.length;
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof ByteBuffer) {
                ByteBuffer src = (ByteBuffer) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    if (JieBuffer.isSimpleWrapper(src)) {
                        int writeNum = decoder.decode(src.array(), dest);
                        src.position(src.limit());
                        return writeNum;
                    }
                    ByteBuffer encoded = decoder.decode(src);
                    int writeNum = encoded.remaining();
                    JieBuffer.readTo(encoded, dest);
                    return writeNum;
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    if (JieBuffer.isSimpleWrapper(src) && JieBuffer.isSimpleWrapper(dest)) {
                        int writeNum = decoder.decode(src.array(), dest.array());
                        src.position(src.limit());
                        dest.position(writeNum);
                        return writeNum;
                    }
                    ByteBuffer encoded = decoder.decode(src);
                    int writeNum = encoded.remaining();
                    dest.put(encoded);
                    return writeNum;
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    ByteBuffer encoded = decoder.decode(src);
                    int writeNum = encoded.remaining();
                    if (JieBuffer.isSimpleWrapper(encoded)) {
                        dest.write(encoded.array());
                    } else {
                        dest.write(JieBuffer.read(encoded));
                    }
                    return writeNum;
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof InputStream) {
                InputStream src = (InputStream) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    byte[] in = JieIO.read(src);
                    return decoder.decode(in, dest);
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    if (JieBuffer.isSimpleWrapper(dest)) {
                        int writeNum = decoder.decode(JieIO.read(src), dest.array());
                        dest.position(writeNum);
                        return writeNum;
                    }
                    byte[] encoded = decoder.decode(JieIO.read(src));
                    dest.put(encoded);
                    return encoded.length;
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    InputStream wrapper = decoder.wrap(src);
                    OutputCounter counter = new OutputCounter(dest);
                    ByteStream.from(wrapper).to(counter).blockSize(blockSize).start();
                    return counter.count;
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else {
                throw new CodecException("Unknown input type: " + input.getClass());
            }
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    @Override
    public byte[] finalBytes() {
        switch (mode) {
            case ENCODE_MODE:
                return getEncoder().encode(inputToBytes());
            case DECODE_MODE:
                return getDecoder().decode(inputToBytes());
        }
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    @Override
    public String finalLatin() {
        switch (mode) {
            case ENCODE_MODE:
                return getEncoder().encodeToString(inputToBytes());
            case DECODE_MODE:
                return JieString.of(getDecoder().decode(inputToBytes()), JieChars.latinCharset());
        }
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    /**
     * For base64:
     * <ul>
     *     <li>
     *         If it is encode mode, same with {@link #finalLatin()};
     *     </li>
     *     <li>
     *         Otherwise with {@link JieChars#defaultCharset()}.
     *     </li>
     * </ul>
     *
     * @return encode mode same with {@link #finalLatin()}, otherwise {@link JieChars#defaultCharset()}
     */
    @Override
    public String finalString() {
        return mode == ENCODE_MODE ? finalLatin() : finalString(JieChars.defaultCharset());
    }

    @Override
    public InputStream finalStream() {
        switch (mode) {
            case ENCODE_MODE:
                Base64.Encoder encoder = getEncoder();
                InputStream enIn = inputToInputStream();
                ByteArrayOutputStream bsOut = new ByteArrayOutputStream();
                OutputStream wrapper = encoder.wrap(bsOut);
                return JieIO.transform(enIn, blockSize, bytes -> {
                    try {
                        wrapper.write(bytes);
                        if (bytes.length != blockSize) {
                            wrapper.close();
                        }
                        return bsOut.toByteArray();
                    } catch (IOException e) {
                        throw new CodecException(e);
                    }
                });

            case DECODE_MODE:
                Base64.Decoder decoder = getDecoder();
                InputStream deIn = inputToInputStream();
                return decoder.wrap(deIn);
        }
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    private byte[] inputToBytes() {
        if (input instanceof byte[]) {
            return (byte[]) input;
        }
        if (input instanceof ByteBuffer) {
            return JieBuffer.read((ByteBuffer) input);
        }
        if (input instanceof InputStream) {
            return JieIO.read((InputStream) input);
        }
        throw new CodecException("Unknown input type: " + input.getClass());
    }

    private InputStream inputToInputStream() {
        if (input instanceof byte[]) {
            return JieIO.in((byte[]) input);
        }
        if (input instanceof ByteBuffer) {
            return JieIO.in((ByteBuffer) input);
        }
        if (input instanceof InputStream) {
            return (InputStream) input;
        }
        throw new CodecException("Unknown input type: " + input.getClass());
    }

    private Base64.Encoder getEncoder() {
        Base64.Encoder encoder = getEncoder0();
        return withoutPadding ? encoder.withoutPadding() : encoder;
    }

    private Base64.Encoder getEncoder0() {
        switch (type) {
            case BASIC_TYPE:
                return Base64.getEncoder();
            case URL_TYPE:
                return Base64.getUrlEncoder();
            case MIME_TYPE:
                return mimeParams == null ?
                    Base64.getMimeEncoder() : Base64.getMimeEncoder(mimeParams.lineLength, mimeParams.lineSeparator);
        }
        throw new IllegalStateException("Unknown type: " + type);
    }

    private Base64.Decoder getDecoder() {
        switch (type) {
            case BASIC_TYPE:
                return Base64.getDecoder();
            case URL_TYPE:
                return Base64.getUrlDecoder();
            case MIME_TYPE:
                return Base64.getMimeDecoder();
        }
        throw new IllegalStateException("Unknown type: " + type);
    }

    @Data
    private static final class MimeParams {
        private final int lineLength;
        private final byte[] lineSeparator;
    }

    private static final class OutputCounter extends OutputStream {

        private final OutputStream outputStream;
        private long count = 0;

        private OutputCounter(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(byte[] b) throws IOException {
            outputStream.write(b);
            count += b.length;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            outputStream.write(b, off, len);
            count += len;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            count++;
        }

        @Override
        public void close() {
            // do nothing
        }
    }
}
