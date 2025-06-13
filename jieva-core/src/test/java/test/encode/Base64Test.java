package test.encode;

import org.testng.annotations.Test;
import xyz.sunqian.common.base.JieRandom;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.encode.DataEncoder;
import xyz.sunqian.common.encode.DecodingException;
import xyz.sunqian.common.encode.EncodingException;
import xyz.sunqian.common.encode.JieBase64;
import xyz.sunqian.common.io.ByteProcessor;
import xyz.sunqian.common.io.JieIO;
import xyz.sunqian.test.JieAssert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class Base64Test {

    @Test
    public void testOutputSize() {
        assertEquals(JieBase64.mimeEncoder().getOutputSize(1), 4);
        assertEquals(JieBase64.mimeEncoder().getOutputSize(57), 76);
        assertEquals(JieBase64.mimeEncoder().getOutputSize(58), 76 + 2 + 4);
        assertEquals(JieBase64.mimeEncoder().getOutputSize(57 * 2), 76 * 2 + 2);
        assertEquals(JieBase64.mimeEncoder().getOutputSize(57 * 2 + 3), 76 * 2 + 4 + 4);
        assertEquals(JieBase64.mimeEncoder().getOutputSize(57 * 2 + 2), 76 * 2 + 4 + 4);
        assertEquals(JieBase64.mimeEncoder(false).getOutputSize(57 * 2 + 2), 76 * 2 + 4 + 3);
        assertEquals(JieBase64.pemEncoder().getOutputSize(1), 4 + 2);
        assertEquals(JieBase64.pemEncoder().getOutputSize(48), 64 + 2);
        assertEquals(JieBase64.pemEncoder().getOutputSize(49), 64 + 2 + 4 + 2);
        assertEquals(JieBase64.pemEncoder().getOutputSize(48 * 2), 64 * 2 + 2 * 2);
        assertEquals(JieBase64.pemEncoder().getOutputSize(48 * 2 + 3), 64 * 2 + 2 * 2 + 4 + 2);
        assertEquals(JieBase64.pemEncoder().getOutputSize(48 * 2 + 2), 64 * 2 + 2 * 2 + 4 + 2);
        assertEquals(JieBase64.pemEncoder(false).getOutputSize(48 * 2 + 2), 64 * 2 + 2 * 2 + 3 + 2);

        expectThrows(DecodingException.class, () -> JieBase64.decoder().getOutputSize(1));
    }

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
        testCoding(JieIO.bufferSize());
        testCoding(JieIO.bufferSize() + 10086);
        testCoding(JieIO.bufferSize() * 11 + 384 * 3 + 1);
        testCoding(JieIO.bufferSize() * 11 + 384 * 3 + 2);
        testCoding(57);
        testCoding(1024);
        testCoding(1024 * 1024);

        // error
        expectThrows(EncodingException.class, () -> JieBase64.encoder().getOutputSize(-1));
        expectThrows(DecodingException.class, () -> JieBase64.decoder().getOutputSize(-1));
        expectThrows(EncodingException.class, () ->
            JieBase64.lineEncoder(-1, new byte[0], true, true, true));
        expectThrows(EncodingException.class, () ->
            JieBase64.lineEncoder(3, new byte[0], true, true, true));
    }

    private void testCoding(int size) {
        byte[] source = JieRandom.fill(new byte[size]);
        byte[] target = Base64.getEncoder().encode(source);
        byte[] targetNoPadding = Base64.getEncoder().withoutPadding().encode(source);
        byte[] urlTarget = Base64.getUrlEncoder().encode(source);
        byte[] urlTargetNoPadding = Base64.getUrlEncoder().withoutPadding().encode(source);
        byte[] mimeTarget = Base64.getMimeEncoder().encode(source);
        byte[] mimeTargetNoPadding = Base64.getMimeEncoder().withoutPadding().encode(source);
        byte[] pemTarget = new org.apache.commons.codec.binary.Base64(
            64, new byte[]{(byte) '\r', (byte) '\n'}
        ).encode(source);
        DataEncoder lineEncoder = JieBase64.lineEncoder(
            76, new byte[]{(byte) '\r', (byte) '\n'}, true, false, false
        );
        DataEncoder lineEncoderUrl = JieBase64.lineEncoder(
            76, new byte[]{(byte) '\r', (byte) '\n'}, true, false, true
        );
        byte[] mimeTargetUrl = Arrays.copyOf(mimeTarget, mimeTarget.length);
        for (int i = 0; i < mimeTargetUrl.length; i++) {
            if (mimeTargetUrl[i] == '+') {
                mimeTargetUrl[i] = '-';
                continue;
            }
            if (mimeTargetUrl[i] == '/') {
                mimeTargetUrl[i] = '_';
            }
        }
        byte[] mixTarget = new byte[pemTarget.length * 3];
        for (int i = 0, j = 0; i < pemTarget.length; i++) {
            mixTarget[j++] = pemTarget[i];
            mixTarget[j++] = '.';
            mixTarget[j++] = '*';
        }
        for (int i = 1; i < 10; i++) {
            // encode
            EncodeTest.testEncoding(JieBase64.encoder(), source, target, i);
            EncodeTest.testEncoding(JieBase64.encoder(false), source, targetNoPadding, i);
            EncodeTest.testEncoding(JieBase64.urlEncoder(), source, urlTarget, i);
            EncodeTest.testEncoding(JieBase64.urlEncoder(false), source, urlTargetNoPadding, i);
            EncodeTest.testEncoding(JieBase64.mimeEncoder(), source, mimeTarget, i);
            EncodeTest.testEncoding(JieBase64.mimeEncoder(false), source, mimeTargetNoPadding, i);
            EncodeTest.testEncoding(JieBase64.pemEncoder(), source, pemTarget, i);
            EncodeTest.testEncoding(lineEncoder, source, mimeTarget, i);
            EncodeTest.testEncoding(lineEncoderUrl, source, mimeTargetUrl, i);
            // decode
            EncodeTest.testDecoding(JieBase64.decoder(), target, source, i);
            EncodeTest.testDecoding(JieBase64.decoder(), targetNoPadding, source, i);
            EncodeTest.testDecoding(JieBase64.decoder(), urlTarget, source, i);
            EncodeTest.testDecoding(JieBase64.decoder(), urlTargetNoPadding, source, i);
            EncodeTest.testDecoding(JieBase64.decoder(), mimeTarget, source, i);
            EncodeTest.testDecoding(JieBase64.decoder(), mimeTargetNoPadding, source, i);
            EncodeTest.testDecoding(JieBase64.decoder(), pemTarget, source, i);
            EncodeTest.testDecoding(JieBase64.decoder(), mimeTarget, source, i);
            EncodeTest.testDecoding(JieBase64.decoder(), mimeTargetUrl, source, i);
            EncodeTest.testDecoding(JieBase64.decoder(), mixTarget, source, i);
        }
        // encode
        EncodeTest.testEncoding(JieBase64.encoder(), source, target, JieIO.bufferSize());
        EncodeTest.testEncoding(JieBase64.encoder(false), source, targetNoPadding, JieIO.bufferSize());
        EncodeTest.testEncoding(JieBase64.urlEncoder(), source, urlTarget, JieIO.bufferSize());
        EncodeTest.testEncoding(JieBase64.urlEncoder(false), source, urlTargetNoPadding, JieIO.bufferSize());
        EncodeTest.testEncoding(JieBase64.mimeEncoder(), source, mimeTarget, JieIO.bufferSize());
        EncodeTest.testEncoding(JieBase64.mimeEncoder(false), source, mimeTargetNoPadding, JieIO.bufferSize());
        EncodeTest.testEncoding(JieBase64.pemEncoder(), source, pemTarget, JieIO.bufferSize());
        EncodeTest.testEncoding(lineEncoder, source, mimeTarget, JieIO.bufferSize());
        EncodeTest.testEncoding(lineEncoderUrl, source, mimeTargetUrl, JieIO.bufferSize());
        // decode
        EncodeTest.testDecoding(JieBase64.decoder(), target, source, JieIO.bufferSize());
        EncodeTest.testDecoding(JieBase64.decoder(), targetNoPadding, source, JieIO.bufferSize());
        EncodeTest.testDecoding(JieBase64.decoder(), urlTarget, source, JieIO.bufferSize());
        EncodeTest.testDecoding(JieBase64.decoder(), urlTargetNoPadding, source, JieIO.bufferSize());
        EncodeTest.testDecoding(JieBase64.decoder(), mimeTarget, source, JieIO.bufferSize());
        EncodeTest.testDecoding(JieBase64.decoder(), mimeTargetNoPadding, source, JieIO.bufferSize());
        EncodeTest.testDecoding(JieBase64.decoder(), pemTarget, source, JieIO.bufferSize());
        EncodeTest.testDecoding(JieBase64.decoder(), mimeTarget, source, JieIO.bufferSize());
        EncodeTest.testDecoding(JieBase64.decoder(), mimeTargetUrl, source, JieIO.bufferSize());
        EncodeTest.testDecoding(JieBase64.decoder(), mixTarget, source, JieIO.bufferSize());
    }

    @Test
    public void testOthers() {
        {
            String s = "ABCDABCDA=";
            String[] error = new String[1];
            try {
                JieBase64.decoder().decode(s.getBytes(JieChars.latinCharset()));
            } catch (DecodingException e) {
                error[0] = e.getMessage();
            } finally {
                assertEquals(error[0], "Invalid base64 char at pos 9: =.");
                error[0] = null;
            }
            try {
                ByteProcessor.from(s.getBytes(JieChars.latinCharset())).readBlockSize(1)
                    .encoder(JieBase64.decoder().streamEncoder()).writeTo(new BytesBuilder());
            } catch (ProcessingException e) {
                error[0] = e.getCause().getMessage();
            } finally {
                assertEquals(error[0], "Invalid base64 char at pos 9: =.");
                error[0] = null;
            }
        }
        {
            String s = "ABCDABCDAB=A";
            String[] error = new String[1];
            try {
                JieBase64.decoder().decode(s.getBytes(JieChars.latinCharset()));
            } catch (DecodingException e) {
                error[0] = e.getMessage();
            } finally {
                assertEquals(error[0], "Invalid base64 char at pos 11: A.");
                error[0] = null;
            }
            try {
                ByteProcessor.from(s.getBytes(JieChars.latinCharset())).readBlockSize(1)
                    .encoder(JieBase64.decoder().streamEncoder()).writeTo(new BytesBuilder());
            } catch (ProcessingException e) {
                error[0] = e.getCause().getMessage();
            } finally {
                assertEquals(error[0], "Invalid base64 char at pos 11: A.");
                error[0] = null;
            }
        }
        {
            String s = "ABCDABCDAB=";
            String[] error = new String[1];
            try {
                JieBase64.decoder().decode(s.getBytes(JieChars.latinCharset()));
            } catch (DecodingException e) {
                error[0] = e.getMessage();
            } finally {
                assertEquals(error[0], "Invalid base64 tail, must be xx, xxx, xx== or xxx=.");
                error[0] = null;
            }
            try {
                ByteProcessor.from(s.getBytes(JieChars.latinCharset())).readBlockSize(1)
                    .encoder(JieBase64.decoder().streamEncoder()).writeTo(new BytesBuilder());
            } catch (ProcessingException e) {
                error[0] = e.getCause().getMessage();
            } finally {
                assertEquals(error[0], "Invalid base64 tail, must be xx, xxx, xx== or xxx=.");
                error[0] = null;
            }
        }
    }

    @Test
    public void testUnreachablePoint() throws Exception {
        DataEncoder encoder = JieBase64.lineEncoder(16, new byte[]{'\t'}, true, true, false);
        Method getOutputSize = encoder.getClass().getDeclaredMethod("getOutputSize", int.class, long.class, boolean.class);
        JieAssert.invokeThrows(EncodingException.class, getOutputSize, encoder, 0, 0L, false);
    }
}
