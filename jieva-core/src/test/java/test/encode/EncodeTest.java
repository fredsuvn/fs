package test.encode;

import test.TU;
import xyz.sunqian.common.encode.ByteDecoder;
import xyz.sunqian.common.encode.ByteEncoder;
import xyz.sunqian.common.encode.DecodingException;
import xyz.sunqian.common.encode.EncodingException;
import xyz.sunqian.common.io.BytesBuilder;
import xyz.sunqian.common.io.BytesProcessor;

import java.nio.ByteBuffer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.expectThrows;

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
            encoded = encoder.encode(TU.bufferDangling(source));
            assertEquals(encoded, ByteBuffer.wrap(target));
            encoded = encoder.encode(TU.bufferDirect(source));
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
            dst = TU.bufferDangling(new byte[target.length]);
            encoder.encode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDirect(new byte[target.length]);
            encoder.encode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));

            dst = ByteBuffer.allocate(target.length);
            encoder.encode(TU.bufferDangling(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDangling(new byte[target.length]);
            encoder.encode(TU.bufferDangling(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDirect(new byte[target.length]);
            encoder.encode(TU.bufferDangling(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));

            dst = ByteBuffer.allocate(target.length);
            encoder.encode(TU.bufferDirect(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDangling(new byte[target.length]);
            encoder.encode(TU.bufferDirect(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDirect(new byte[target.length]);
            encoder.encode(TU.bufferDirect(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
        }
        {
            // stream
            BytesBuilder bb = new BytesBuilder();
            long c = BytesProcessor.from(source).encoder(encoder.streamEncoder(), blockSize).writeTo(bb);
            if (source.length == 0) {
                assertEquals(c, -1);
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
            decoded = decoder.decode(TU.bufferDangling(source));
            assertEquals(decoded, ByteBuffer.wrap(target));
            decoded = decoder.decode(TU.bufferDirect(source));
            assertEquals(decoded, ByteBuffer.wrap(target));
        }
        {
            // byte[] -> byte[]
            byte[] dst = new byte[target.length];
            decoder.decode(source, dst);
            assertEquals(dst, target);
        }
        {
            // buffer -> buffer
            ByteBuffer dst = ByteBuffer.allocate(target.length);
            decoder.decode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDangling(new byte[target.length]);
            decoder.decode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDirect(new byte[target.length]);
            decoder.decode(ByteBuffer.wrap(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));

            dst = ByteBuffer.allocate(target.length);
            decoder.decode(TU.bufferDangling(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDangling(new byte[target.length]);
            decoder.decode(TU.bufferDangling(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDirect(new byte[target.length]);
            decoder.decode(TU.bufferDangling(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));

            dst = ByteBuffer.allocate(target.length);
            decoder.decode(TU.bufferDirect(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDangling(new byte[target.length]);
            decoder.decode(TU.bufferDirect(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
            dst = TU.bufferDirect(new byte[target.length]);
            decoder.decode(TU.bufferDirect(source), dst);
            dst.flip();
            assertEquals(dst, ByteBuffer.wrap(target));
        }
        {
            // stream
            BytesBuilder bb = new BytesBuilder();
            long c = BytesProcessor.from(source).encoder(decoder.streamEncoder(), blockSize).writeTo(bb);
            if (source.length == 0) {
                assertEquals(c, -1);
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
