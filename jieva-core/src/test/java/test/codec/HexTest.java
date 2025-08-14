package test.codec;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;
import xyz.sunqian.common.codec.HexKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.test.DataTest;

import java.nio.ByteBuffer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

public class HexTest implements DataTest {

    @Test
    public void testCoding() {

        assertSame(HexKit.encoder(), HexKit.encoder());
        assertSame(HexKit.decoder(), HexKit.decoder());

        for (int i = 0; i < 10; i++) {
            testCoding(i);
        }
        testCoding(1139);
        testCoding(1140);
        testCoding(1141);
        testCoding(384 * 3 - 1);
        testCoding(384 * 3);
        testCoding(384 * 3 + 1);
        testCoding(10086);
        testCoding(99);
        testCoding(IOKit.bufferSize());
        testCoding(IOKit.bufferSize() + 10086);
        testCoding(IOKit.bufferSize() * 11 + 1);
        testCoding(IOKit.bufferSize() * 11 + 2);
        testCoding(57);
        testCoding(1024);
        testCoding(1024 * 1024);

        // error
        // expectThrows(EncodingException.class, () -> JieHex.encoder().getOutputSize(-1));
        // expectThrows(DecodingException.class, () -> JieHex.decoder().getOutputSize(-1));
        // byte[] encoded = new byte[3];
        // expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded));
        // byte[] encoded2 = new byte[2];
        // encoded2[0] = '0' - 1;
        // expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        // encoded2[0] = '9' + 1;
        // expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        // encoded2[0] = 'A' - 1;
        // expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        // encoded2[0] = 'F' + 1;
        // expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        // encoded2[0] = 'a' - 1;
        // expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        // encoded2[0] = 'f' + 1;
        // expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
    }

    private void testCoding(int size) {
        byte[] src = randomBytes(size);
        assertEquals(HexKit.encoder().encodeToLatin1(src), Hex.encodeHexString(src).toUpperCase());
        assertEquals(HexKit.encoder().encodeToLatin1(ByteBuffer.wrap(src)), Hex.encodeHexString(src).toUpperCase());
    }

    // @Test
    // public void testOthers() throws Exception {
    //     String s = "0123456789ABCDEFabcdef";
    //     {
    //         byte[] en = JieHex.encoder().encode(s.getBytes(JieChars.latinCharset()));
    //         en[11] = 'Q';
    //         String[] error = new String[1];
    //         try {
    //             JieHex.decoder().decode(en);
    //         } catch (DecodingException e) {
    //             error[0] = e.getMessage();
    //         } finally {
    //             assertEquals(error[0], "Invalid hex char at pos 11: Q.");
    //             error[0] = null;
    //         }
    //         try {
    //             ByteEncoder.from(en).readBlockSize(1)
    //                 .handler(JieHex.decoder().streamEncoder()).writeTo(new BytesBuilder());
    //         } catch (ProcessingException e) {
    //             error[0] = e.getCause().getMessage();
    //         } finally {
    //             assertEquals(error[0], "Invalid hex char at pos 11: Q.");
    //             error[0] = null;
    //         }
    //     }
    // }
}
