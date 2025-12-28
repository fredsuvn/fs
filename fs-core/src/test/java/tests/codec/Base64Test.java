package tests.codec;

import internal.test.DataTest;
import org.junit.jupiter.api.Test;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.codec.Base64Kit;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Base64Test implements DataTest {

    @Test
    public void testBase64() {
        {
            // instance
            assertSame(Base64Kit.encoder(), Base64Kit.encoder());
            assertSame(Base64Kit.encoder(true, true), Base64Kit.encoder(true, true));
            assertSame(Base64Kit.encoder(true, false), Base64Kit.encoder(true, false));
            assertSame(Base64Kit.encoder(false, true), Base64Kit.encoder(false, true));
            assertSame(Base64Kit.encoder(false, false), Base64Kit.encoder(false, false));
            assertSame(Base64Kit.decoder(), Base64Kit.decoder());
            assertSame(Base64Kit.decoder(false), Base64Kit.decoder(false));
        }
        {
            // encoding/decoding
            for (int i = 0; i < 32; i++) {
                testBase64(i);
            }
            testBase64(1333);
        }
        {
            // url safe
            String urlSafe = "+/==";
            assertArrayEquals(
                Base64Kit.decoder().decode(urlSafe),
                Base64.getDecoder().decode(urlSafe)
            );
        }
        {
            // exception
            Base64Kit.Base64Exception e;
            String err1 = "+/%==";
            e = assertThrows(Base64Kit.Base64Exception.class, () -> Base64Kit.decoder().decode(err1));
            assertEquals(2, e.position());
            String err2 = "+/+/=";
            e = assertThrows(Base64Kit.Base64Exception.class, () -> Base64Kit.decoder().decode(err2));
            assertEquals(4, e.position());
            String err3 = "+/==0";
            e = assertThrows(Base64Kit.Base64Exception.class, () -> Base64Kit.decoder().decode(err3));
            assertEquals(4, e.position());
            String err4 = "+/=0=";
            e = assertThrows(Base64Kit.Base64Exception.class, () -> Base64Kit.decoder().decode(err4));
            assertEquals(3, e.position());
            String err5 = "+/=";
            e = assertThrows(Base64Kit.Base64Exception.class, () -> Base64Kit.decoder().decode(err5));
            assertEquals(2, e.position());
            String err6 = "+/h==";
            e = assertThrows(Base64Kit.Base64Exception.class, () -> Base64Kit.decoder().decode(err6));
            assertEquals(4, e.position());
        }
    }

    private void testBase64(int size) {
        byte[] src = randomBytes(size);
        String basePadding = Base64.getEncoder().encodeToString(src);
        String baseNoPadding = Base64.getEncoder().withoutPadding().encodeToString(src);
        String urlPadding = Base64.getUrlEncoder().encodeToString(src);
        String urlNoPadding = Base64.getUrlEncoder().withoutPadding().encodeToString(src);
        {
            // encode
            assertEquals(Base64Kit.encoder().encodeToString(src), basePadding);
            assertEquals(Base64Kit.encoder(false, false).encodeToString(src), baseNoPadding);
            assertEquals(Base64Kit.encoder(true, true).encodeToString(src), urlPadding);
            assertEquals(Base64Kit.encoder(true, false).encodeToString(src), urlNoPadding);
            String basePaddingBuf = Base64Kit.encoder().encodeToString(ByteBuffer.wrap(src));
            String baseNoPaddingBuf = Base64Kit.encoder(false, false).encodeToString(ByteBuffer.wrap(src));
            String urlPaddingBuf = Base64Kit.encoder(true, true).encodeToString(ByteBuffer.wrap(src));
            String urlNoPaddingBuf = Base64Kit.encoder(true, false).encodeToString(ByteBuffer.wrap(src));
            assertEquals(basePaddingBuf, basePadding);
            assertEquals(baseNoPaddingBuf, baseNoPadding);
            assertEquals(urlPaddingBuf, urlPadding);
            assertEquals(urlNoPaddingBuf, urlNoPadding);
            // decode
            byte[] basePaddingDe = Base64Kit.decoder().decode(basePadding);
            byte[] baseNoPaddingDe = Base64Kit.decoder().decode(baseNoPadding);
            byte[] urlPaddingDe = Base64Kit.decoder().decode(urlPadding);
            byte[] urlNoPaddingDe = Base64Kit.decoder().decode(urlNoPadding);
            assertArrayEquals(basePaddingDe, src);
            assertArrayEquals(baseNoPaddingDe, src);
            assertArrayEquals(urlPaddingDe, src);
            assertArrayEquals(urlNoPaddingDe, src);
            // decode by buffer
            ByteBuffer basePaddingBuffer = ByteBuffer.wrap(basePadding.getBytes(StandardCharsets.ISO_8859_1));
            ByteBuffer baseNoPaddingBuffer = ByteBuffer.wrap(baseNoPadding.getBytes(StandardCharsets.ISO_8859_1));
            ByteBuffer urlPaddingBuffer = ByteBuffer.wrap(urlPadding.getBytes(StandardCharsets.ISO_8859_1));
            ByteBuffer urlNoPaddingBuffer = ByteBuffer.wrap(urlNoPadding.getBytes(StandardCharsets.ISO_8859_1));
            assertArrayEquals(Base64Kit.decoder().decode(basePaddingBuffer), src);
            assertArrayEquals(Base64Kit.decoder().decode(baseNoPaddingBuffer), src);
            assertArrayEquals(Base64Kit.decoder().decode(urlPaddingBuffer), src);
            assertArrayEquals(Base64Kit.decoder().decode(urlNoPaddingBuffer), src);
        }
        {
            // add bad char
            int midIndex = src.length / 2;
            String defective =
                baseNoPadding.substring(0, midIndex) +
                    '%' +
                    baseNoPadding.substring(midIndex) +
                    '%';
            assertArrayEquals(Base64Kit.decoder(false).decode(defective), src);
            ByteBuffer defectiveBuf = ByteBuffer.wrap(defective.getBytes(StandardCharsets.ISO_8859_1));
            assertArrayEquals(Base64Kit.decoder(false).decode(defectiveBuf), src);
            Base64Kit.Base64Exception e;
            e = assertThrows(Base64Kit.Base64Exception.class, () -> Base64Kit.decoder().decode(defective));
            assertEquals(e.position(), midIndex);
            defectiveBuf.clear();
            e = assertThrows(Base64Kit.Base64Exception.class, () -> Base64Kit.decoder().decode(defectiveBuf));
            assertEquals(e.position(), midIndex);
        }
        {
            // decodeToString
            char[] chars = randomChars(size, 'a', 'z');
            String str = new String(chars);
            String base64 = Base64.getEncoder().encodeToString(str.getBytes(CharsKit.defaultCharset()));
            assertEquals(Base64Kit.decoder().decodeToString(base64), str);
        }
    }

    @Test
    public void testBase64Exception() {
        Base64Kit.Base64Exception e;
        e = assertThrows(Base64Kit.Base64Exception.class, () -> {
            throw new Base64Kit.Base64Exception();
        });
        assertEquals(-1, e.position());
        e = assertThrows(Base64Kit.Base64Exception.class, () -> {
            throw new Base64Kit.Base64Exception("");
        });
        assertEquals(-1, e.position());
        e = assertThrows(Base64Kit.Base64Exception.class, () -> {
            throw new Base64Kit.Base64Exception("", new RuntimeException());
        });
        assertEquals(-1, e.position());
        e = assertThrows(Base64Kit.Base64Exception.class, () -> {
            throw new Base64Kit.Base64Exception(new RuntimeException());
        });
        assertEquals(-1, e.position());
        e = assertThrows(Base64Kit.Base64Exception.class, () -> {
            throw new Base64Kit.Base64Exception(66);
        });
        assertEquals(66, e.position());
        e = assertThrows(Base64Kit.Base64Exception.class, () -> {
            throw new Base64Kit.Base64Exception(66, "");
        });
        assertEquals(66, e.position());
        e = assertThrows(Base64Kit.Base64Exception.class, () -> {
            throw new Base64Kit.Base64Exception(66, "", new RuntimeException());
        });
        assertEquals(66, e.position());
        e = assertThrows(Base64Kit.Base64Exception.class, () -> {
            throw new Base64Kit.Base64Exception(66, new RuntimeException());
        });
        assertEquals(66, e.position());
    }
}
