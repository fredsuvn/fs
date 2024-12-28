package test.encode;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.encode.DecodingException;
import xyz.sunqian.common.encode.EncodingException;
import xyz.sunqian.common.encode.JieHex;
import xyz.sunqian.common.io.IOEncodingException;
import xyz.sunqian.common.io.JieIO;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import static org.testng.Assert.*;

public class HexTest {

    @Test
    public void testCoding() {

        assertSame(JieHex.encoder(), JieHex.encoder());
        assertSame(JieHex.decoder(), JieHex.decoder());
        assertEquals(JieHex.encoder().getBlockSize(), -1);
        assertEquals(JieHex.decoder().getBlockSize(), 2);

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
        testCoding(JieIO.BUFFER_SIZE);
        testCoding(JieIO.BUFFER_SIZE + 10086);
        testCoding(57);
        testCoding(1024);
        testCoding(1024 * 1024);

        // error
        expectThrows(EncodingException.class, () -> JieHex.encoder().getOutputSize(-1));
        expectThrows(DecodingException.class, () -> JieHex.decoder().getOutputSize(-1));
        byte[] encoded = new byte[3];
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded));
        byte[] encoded2 = new byte[2];
        encoded2[0] = '0' - 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = '9' + 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = 'A' - 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = 'F' + 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = 'a' - 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
        encoded2[0] = 'f' + 1;
        expectThrows(DecodingException.class, () -> JieHex.decoder().decode(encoded2));
    }

    private void testCoding(int size) {
        for (int i = 1; i < 10; i++) {
            byte[] source = JieRandom.fill(new byte[size]);
            byte[] target = Hex.encodeHexString(source).toUpperCase().getBytes(JieChars.latinCharset());
            EncodeTest.testEncoding(JieHex.encoder(), source, target, i);
            if (i % 2 == 0) {
                EncodeTest.testDecoding(JieHex.decoder(), target, source, i);
            }
        }
    }

    @Test
    public void testToChars() throws Exception {
        String hexStr = "0123456789ABCDEFabcdef";
        byte[] srcBytes = Hex.decodeHex(hexStr);
        assertEquals(JieHex.encoder().toString(srcBytes), hexStr.toUpperCase());
        assertEquals(JieHex.encoder().toString(ByteBuffer.wrap(srcBytes)), hexStr.toUpperCase());
        assertEquals(JieHex.decoder().decode(hexStr), srcBytes);
        assertEquals(JieHex.decoder().decode(CharBuffer.wrap(hexStr)), ByteBuffer.wrap(srcBytes));
        assertEquals(JieHex.decoder().decode(hexStr.toCharArray()), srcBytes);
    }

    @Test
    public void testOthers() throws Exception {
        String s = "0123456789ABCDEFabcdef";
        {
            // Hex
            byte[] en = JieHex.encoder().encode(s.getBytes(JieChars.latinCharset()));
            en[11] = 'Q';
            String[] error = new String[1];
            try {
                JieHex.decoder().decode(en);
            } catch (DecodingException e) {
                error[0] = e.getMessage();
            } finally {
                assertEquals(error[0], "Invalid hex char at pos 11: Q.");
                error[0] = null;
            }
            try {
                JieIO.processor(en).readBlockSize(1)
                    .encoder(JieHex.decoder().streamEncoder()).writeTo(new ByteArrayOutputStream());
            } catch (IOEncodingException e) {
                error[0] = e.getCause().getMessage();
            } finally {
                assertEquals(error[0], "Invalid hex char at pos 11: Q.");
                error[0] = null;
            }
        }
    }
}
