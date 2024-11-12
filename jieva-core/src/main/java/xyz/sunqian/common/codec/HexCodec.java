package xyz.sunqian.common.codec;

import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Hex implementation of {@link CodecConfigurator} to encode/decode HEX.
 *
 * @author fredsuvn
 */
public class HexCodec implements CodecConfigurator<HexCodec> {

    private static final int ENCODE_MODE = 0;
    private static final int DECODE_MODE = 1;

    private Object input;
    private Object output;
    private int blockSize;
    private int mode;

    HexCodec() {
        reset();
    }

    @Override
    public HexCodec input(byte[] array) {
        this.input = array;
        return this;
    }

    @Override
    public HexCodec input(ByteBuffer buffer) {
        this.input = buffer;
        return this;
    }

    @Override
    public HexCodec input(InputStream in) {
        this.input = in;
        return this;
    }

    @Override
    public HexCodec output(byte[] array) {
        this.output = array;
        return this;
    }

    @Override
    public HexCodec output(ByteBuffer buffer) {
        this.output = buffer;
        return this;
    }

    @Override
    public HexCodec output(OutputStream out) {
        this.output = out;
        return this;
    }

    @Override
    public HexCodec reset() {
        this.input = null;
        this.output = null;
        this.blockSize = JieIO.BUFFER_SIZE;
        this.mode = ENCODE_MODE;
        return this;
    }

    /**
     * Sets block size for encoding/decoding operations.
     *
     * @param blockSize block size
     * @return this
     */
    public HexCodec blockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    /**
     * Sets mode to encoding which backed by {@link Base64.Encoder}.
     *
     * @return this
     */
    public HexCodec encode() {
        this.mode = ENCODE_MODE;
        return this;
    }

    /**
     * Sets mode to decoding which backed by {@link Base64.Decoder}.
     *
     * @return this
     */
    public HexCodec decode() {
        this.mode = DECODE_MODE;
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
            if (input instanceof byte[]) {
                byte[] src = (byte[]) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    return doEncode(ByteBuffer.wrap(src), ByteBuffer.wrap(dest));
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    return doEncode(ByteBuffer.wrap(src), dest);
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    return doEncode(JieIO.in(src), dest);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof ByteBuffer) {
                ByteBuffer src = (ByteBuffer) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    return doEncode(src, ByteBuffer.wrap(dest));
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    return doEncode(src, dest);
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    return doEncode(JieIO.in(src), dest);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof InputStream) {
                InputStream src = (InputStream) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    return doEncode(src, JieIO.out(dest));
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    return doEncode(src, JieIO.out(dest));
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    return doEncode(src, dest);
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

    private int doEncode(ByteBuffer src, ByteBuffer dest) {
        int srcRemaining = src.remaining();
        if (dest.remaining() < srcRemaining * 2) {
            throw new CodecException("Dest remaining space not enough.");
        }
        while (src.hasRemaining()) {
            byte b = src.get();
            dest.put(encodeByte(b >>> 4));
            dest.put(encodeByte(b));
        }
        return srcRemaining * 2;
    }

    private long doEncode(InputStream src, OutputStream dest) throws IOException {
        long writeNum = 0;
        while (true) {
            int i = src.read();
            if (i == -1) {
                break;
            }
            dest.write(encodeByte(i >>> 4));
            dest.write(encodeByte(i));
            writeNum += 2;
        }
        return writeNum;
    }

    private long doDecode() {
        try {
            if (input instanceof byte[]) {
                byte[] src = (byte[]) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    return doDecode(ByteBuffer.wrap(src), ByteBuffer.wrap(dest));
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    return doDecode(ByteBuffer.wrap(src), dest);
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    return doDecode(JieIO.in(src), dest);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof ByteBuffer) {
                ByteBuffer src = (ByteBuffer) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    return doDecode(src, ByteBuffer.wrap(dest));
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    return doDecode(src, dest);
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    return doDecode(JieIO.in(src), dest);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof InputStream) {
                InputStream src = (InputStream) input;
                if (output instanceof byte[]) {
                    byte[] dest = (byte[]) output;
                    return doDecode(src, JieIO.out(dest));
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer dest = (ByteBuffer) output;
                    return doDecode(src, JieIO.out(dest));
                } else if (output instanceof OutputStream) {
                    OutputStream dest = (OutputStream) output;
                    return doDecode(src, dest);
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

    private int doDecode(ByteBuffer src, ByteBuffer dest) {
        int srcRemaining = src.remaining();
        if (srcRemaining % 2 != 0) {
            throw new CodecException("Source may not hex data: srcRemaining % 2 != 0");
        }
        if (dest.remaining() < srcRemaining / 2) {
            throw new CodecException("Dest remaining space not enough.");
        }
        while (src.hasRemaining()) {
            byte b1 = src.get();
            byte b2 = src.get();
            dest.put(mergeByte(decodeByte(b1), decodeByte(b2)));
        }
        return srcRemaining / 2;
    }

    private long doDecode(InputStream src, OutputStream dest) throws IOException {
        long writeNum = 0;
        while (true) {
            int i1 = src.read();
            if (i1 == -1) {
                break;
            }
            int i2 = src.read();
            if (i2 == -1) {
                throw new CodecException("Source may not hex data: srcRemaining % 2 != 0");
            }
            dest.write(mergeByte(decodeByte(i1), decodeByte(i2)));
            writeNum++;
        }
        return writeNum;
    }

    @Override
    public byte[] finalBytes() {
        switch (mode) {
            case ENCODE_MODE: {
                byte[] src = inputToBytes();
                byte[] dest = new byte[src.length * 2];
                doEncode(ByteBuffer.wrap(src), ByteBuffer.wrap(dest));
                return dest;
            }
            case DECODE_MODE: {
                byte[] src = inputToBytes();
                if (src.length % 2 != 0) {
                    throw new CodecException("Source may not hex data: src.length % 2 != 0");
                }
                byte[] dest = new byte[src.length / 2];
                doDecode(ByteBuffer.wrap(src), ByteBuffer.wrap(dest));
                return dest;
            }
        }
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    /**
     * For hex:
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
                return JieIO.transform(inputToInputStream(), blockSize, bytes -> {
                    byte[] dest = new byte[bytes.length * 2];
                    doEncode(ByteBuffer.wrap(bytes), ByteBuffer.wrap(dest));
                    return dest;
                });
            case DECODE_MODE:
                return JieIO.transform(inputToInputStream(), blockSize, bytes -> {
                    if (bytes.length % 2 != 0) {
                        throw new CodecException("Source may not hex data: bytes.length % 2 != 0");
                    }
                    byte[] dest = new byte[bytes.length / 2];
                    doDecode(ByteBuffer.wrap(bytes), ByteBuffer.wrap(dest));
                    return dest;
                });
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

    private byte encodeByte(int b) {
        int s = b & 0x0f;
        switch (s) {
            case 0:
                return '0';
            case 1:
                return '1';
            case 2:
                return '2';
            case 3:
                return '3';
            case 4:
                return '4';
            case 5:
                return '5';
            case 6:
                return '6';
            case 7:
                return '7';
            case 8:
                return '8';
            case 9:
                return '9';
            case 10:
                return 'A';
            case 11:
                return 'B';
            case 12:
                return 'C';
            case 13:
                return 'D';
            case 14:
                return 'E';
            case 15:
                return 'F';
        }
        throw new CodecException("Illegal byte: 0x" + Integer.toBinaryString(s));
    }

    private int decodeByte(int b) {
        int c = b & 0x00ff;
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'A':
            case 'a':
                return 10;
            case 'B':
            case 'b':
                return 11;
            case 'C':
            case 'c':
                return 12;
            case 'D':
            case 'd':
                return 13;
            case 'E':
            case 'e':
                return 14;
            case 'F':
            case 'f':
                return 15;
        }
        throw new CodecException("Illegal byte: " + (char) c);
    }

    private byte mergeByte(int i1, int i2) {
        int b1 = i1 & 0x0f;
        int b2 = i2 & 0x0f;
        return (byte) ((b1 << 4) | b2);
    }
}
