package test.encode;

import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.encode.ByteDecoder;
import xyz.sunqian.common.encode.ByteEncoder;
import xyz.sunqian.common.encode.DecodingException;
import xyz.sunqian.common.encode.EncodingException;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;
import static xyz.sunqian.test.MaterialBox.directBuffer;
import static xyz.sunqian.test.MaterialBox.paddedBuffer;

public class EncodeTest {

    static void testEncoding(
        ByteEncoder encoder,
        byte[] source,
        byte[] target,
        int blockSize
    ) {
        {
            // -> byte[]
            byte[] encoded = encoder.encode(source);
            assertEquals(encoded, target);
        }
        {
            // -> buffer
            ByteBuffer encoded = encoder.encode(ByteBuffer.wrap(source));
            assertEquals(encoded, ByteBuffer.wrap(target));
            encoded = encoder.encode(paddedBuffer(source));
            assertEquals(encoded, ByteBuffer.wrap(target));
            encoded = encoder.encode(directBuffer(source));
            assertEquals(encoded, ByteBuffer.wrap(target));
        }
        {
            // byte[] -> byte[]
            byte[] dst = new byte[target.length];
            encoder.encode(source, dst);
            assertEquals(dst, target);
        }
        {
            // buffer -> buffer
            ByteBuffer dst = ByteBuffer.allocate(target.length);
            encoder.encode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = paddedBuffer(new byte[target.length]);
            encoder.encode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = directBuffer(new byte[target.length]);
            encoder.encode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));

            dst = ByteBuffer.allocate(target.length);
            encoder.encode(paddedBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = paddedBuffer(new byte[target.length]);
            encoder.encode(paddedBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = directBuffer(new byte[target.length]);
            encoder.encode(paddedBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));

            dst = ByteBuffer.allocate(target.length);
            encoder.encode(directBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = paddedBuffer(new byte[target.length]);
            encoder.encode(directBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = directBuffer(new byte[target.length]);
            encoder.encode(directBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
        }
        {
            // stream
            BytesBuilder bb = new BytesBuilder();
            long c = JieBytes.process(source).encoder(blockSize, encoder.streamEncoder()).writeTo(bb);
            if (source.length == 0) {
                assertEquals(c, 0);
            } else {
                assertEquals(c, source.length);
            }
            assertEquals(bb.toByteArray(), target);
        }
        {
            // error
            if (source.length > 0) {
                expectThrows(EncodingException.class, () ->
                    encoder.encode(source, new byte[0]));
                expectThrows(EncodingException.class, () ->
                    encoder.encode(ByteBuffer.wrap(source), ByteBuffer.wrap(new byte[0])));
            }
        }
    }

    static void testDecoding(
        ByteDecoder decoder,
        byte[] source,
        byte[] target,
        int blockSize
    ) {
        {
            // -> byte[]
            byte[] decoded = decoder.decode(source);
            assertEquals(decoded, target);
        }
        {
            // -> buffer
            ByteBuffer decoded = decoder.decode(ByteBuffer.wrap(source));
            assertEquals(decoded, ByteBuffer.wrap(target));
            decoded = decoder.decode(paddedBuffer(source));
            assertEquals(decoded, ByteBuffer.wrap(target));
            decoded = decoder.decode(directBuffer(source));
            assertEquals(decoded, ByteBuffer.wrap(target));
        }
        {
            // byte[] -> byte[]
            byte[] dst = new byte[target.length];
            decoder.decode(source, dst);
            assertEquals(Arrays.copyOfRange(dst, 0, target.length), target);
        }
        {
            // buffer -> buffer
            ByteBuffer dst = ByteBuffer.allocate(target.length);
            decoder.decode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = paddedBuffer(new byte[target.length]);
            decoder.decode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = directBuffer(new byte[target.length]);
            decoder.decode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));

            dst = ByteBuffer.allocate(target.length);
            decoder.decode(paddedBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = paddedBuffer(new byte[target.length]);
            decoder.decode(paddedBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = directBuffer(new byte[target.length]);
            decoder.decode(paddedBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));

            dst = ByteBuffer.allocate(target.length);
            decoder.decode(directBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = paddedBuffer(new byte[target.length]);
            decoder.decode(directBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = directBuffer(new byte[target.length]);
            decoder.decode(directBuffer(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
        }
        {
            // stream
            BytesBuilder bb = new BytesBuilder();
            long c = JieBytes.process(source).encoder(blockSize, decoder.streamEncoder()).writeTo(bb);
            if (source.length == 0) {
                assertEquals(c, 0);
            } else {
                assertEquals(c, source.length);
            }
            assertEquals(bb.toByteArray(), target);
        }
        {
            // error
            if (source.length > 0) {
                expectThrows(DecodingException.class, () ->
                    decoder.decode(source, new byte[0]));
                expectThrows(DecodingException.class, () ->
                    decoder.decode(ByteBuffer.wrap(source), ByteBuffer.wrap(new byte[0])));
            }
        }
    }
}
