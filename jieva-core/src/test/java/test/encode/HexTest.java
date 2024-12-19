package test.encode;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.encode.DecodingException;
import xyz.sunqian.common.encode.EncodingException;
import xyz.sunqian.common.encode.JieHex;
import xyz.sunqian.common.io.ByteProcessor;
import xyz.sunqian.common.io.IOEncodingException;
import xyz.sunqian.common.io.JieIO;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import static org.testng.Assert.*;

public class HexTest {

    @Test
    public void testHex() {

        assertSame(JieHex.encoder(), JieHex.encoder());
        assertSame(JieHex.decoder(), JieHex.decoder());

        for (int i = 0; i < 10; i++) {
            testHex(i);
        }
        testHex(1139);
        testHex(1140);
        testHex(1141);
        testHex(384 * 3 - 1);
        testHex(384 * 3);
        testHex(384 * 3 + 1);
        testHex(10086);
        testHex(99);
        testHex(JieIO.BUFFER_SIZE);
        testHex(JieIO.BUFFER_SIZE + 10086);
        testHex(57);
        testHex(1024);
        testHex(1024 * 1024);

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

    private void testHex(int size) {
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
        String str = "0123456789ABCDEFabcdef";
        assertEquals(JieHex.decoder().decode(str), Hex.decodeHex(str));
        assertEquals(JieHex.decoder().decode(str.toCharArray()), Hex.decodeHex(str));
        CharBuffer cb = CharBuffer.wrap(str);
        assertEquals(JieBytes.copyBytes(JieHex.decoder().decode(cb)), Hex.decodeHex(str));
        assertEquals(cb.position(), str.length());

        byte[] bytes = JieHex.decoder().decode(str);
        assertEquals(str.toUpperCase(), JieHex.encoder().toString(bytes));
        assertEquals(str.toUpperCase(), JieHex.encoder().toString(ByteBuffer.wrap(bytes)));
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
                ByteProcessor.from(en).readBlockSize(1)
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
