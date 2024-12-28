package test.encode;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.encode.DecodingException;
import xyz.sunqian.common.encode.EncodingException;
import xyz.sunqian.common.encode.JieBase64;
import xyz.sunqian.common.io.IOEncodingException;
import xyz.sunqian.common.io.JieIO;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Base64;

import static org.testng.Assert.*;

public class Base64Test {

    @Test
    public void testCoding() {

        assertSame(JieBase64.encoder(), JieBase64.encoder());
        assertSame(JieBase64.urlEncoder(), JieBase64.urlEncoder());
        assertEquals(JieBase64.encoder().getBlockSize(), -1);
        assertEquals(JieBase64.decoder().getBlockSize(), -1);

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
        expectThrows(EncodingException.class, () -> JieBase64.encoder().getOutputSize(-1));
        expectThrows(DecodingException.class, () -> JieBase64.decoder().getOutputSize(-1));
        // byte[] encoded = new byte[3];
        // expectThrows(DecodingException.class, () -> JieBase64.decoder().decode(encoded));
        // byte[] encoded2 = new byte[2];
        // encoded2[0] = '0' - 1;
        // expectThrows(DecodingException.class, () -> JieBase64.decoder().decode(encoded2));
        // encoded2[0] = '9' + 1;
        // expectThrows(DecodingException.class, () -> JieBase64.decoder().decode(encoded2));
        // encoded2[0] = 'A' - 1;
        // expectThrows(DecodingException.class, () -> JieBase64.decoder().decode(encoded2));
        // encoded2[0] = 'F' + 1;
        // expectThrows(DecodingException.class, () -> JieBase64.decoder().decode(encoded2));
        // encoded2[0] = 'a' - 1;
        // expectThrows(DecodingException.class, () -> JieBase64.decoder().decode(encoded2));
        // encoded2[0] = 'f' + 1;
        // expectThrows(DecodingException.class, () -> JieBase64.decoder().decode(encoded2));
    }

    private void testCoding(int size) {
        for (int i = 1; i < 10; i++) {
            byte[] source = JieRandom.fill(new byte[size]);
            byte[] target = Base64.getEncoder().encode(source);
            EncodeTest.testEncoding(JieBase64.encoder(), source, target, i);
            // if (i % 2 == 0) {
            //     EncodeTest.testDecoding(JieBase64.decoder(), target, source, i);
            // }
        }
    }

    @Test
    public void testToChars() throws Exception {
        String str = "0123456789ABCDEFabcdef";
        assertEquals(JieBase64.decoder().decode(str), Hex.decodeHex(str));
        assertEquals(JieBase64.decoder().decode(str.toCharArray()), Hex.decodeHex(str));
        CharBuffer cb = CharBuffer.wrap(str);
        assertEquals(JieBytes.copyBytes(JieBase64.decoder().decode(cb)), Hex.decodeHex(str));
        assertEquals(cb.position(), str.length());

        byte[] bytes = JieBase64.decoder().decode(str);
        assertEquals(str.toUpperCase(), JieBase64.encoder().toString(bytes));
        assertEquals(str.toUpperCase(), JieBase64.encoder().toString(ByteBuffer.wrap(bytes)));
    }

    // @Test
    // public void testOthers() throws Exception {
    //     String s = "0123456789ABCDEFabcdef";
    //     {
    //         // Hex
    //         byte[] en = JieBase64.encoder().encode(s.getBytes(JieChars.latinCharset()));
    //         en[11] = 'Q';
    //         String[] error = new String[1];
    //         try {
    //             JieBase64.decoder().decode(en);
    //         } catch (DecodingException e) {
    //             error[0] = e.getMessage();
    //         } finally {
    //             assertEquals(error[0], "Invalid hex char at pos 11: Q.");
    //             error[0] = null;
    //         }
    //         try {
    //             JieIO.processor(en).readBlockSize(1)
    //                 .encoder(JieBase64.decoder().streamEncoder()).writeTo(new ByteArrayOutputStream());
    //         } catch (IOEncodingException e) {
    //             error[0] = e.getCause().getMessage();
    //         } finally {
    //             assertEquals(error[0], "Invalid hex char at pos 11: Q.");
    //             error[0] = null;
    //         }
    //     }
    // }
}
