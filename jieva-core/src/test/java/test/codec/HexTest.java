package test.codec;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;
import xyz.sunqian.common.codec.HexKit;
import xyz.sunqian.test.DataTest;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.expectThrows;

public class HexTest implements DataTest {

    @Test
    public void testHex() {
        {
            // instance
            assertSame(HexKit.encoder(), HexKit.encoder());
            assertSame(HexKit.encoder(false), HexKit.encoder(false));
            assertSame(HexKit.decoder(), HexKit.decoder());
            assertSame(HexKit.decoder(false), HexKit.decoder(false));
        }
        {
            // encoding/decoding
            for (int i = 0; i < 32; i++) {
                testHex(i);
            }
            testHex(1333);
        }
        {
            // exception
            HexKit.HexException e;
            byte[] errorLen = new byte[3];
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorLen));
            assertEquals(e.position(), -1);
            byte[] errorChar = new byte[2];
            errorChar[0] = '0' - 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 0);
            errorChar[0] = '9' + 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 0);
            errorChar[0] = 'A' - 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 0);
            errorChar[0] = 'F' + 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 0);
            errorChar[0] = 'a' - 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 0);
            errorChar[0] = 'f' + 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 0);
            errorChar[0] = '0';
            errorChar[1] = '0' - 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 1);
            errorChar[1] = '9' + 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 1);
            errorChar[1] = 'A' - 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 1);
            errorChar[1] = 'F' + 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 1);
            errorChar[1] = 'a' - 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 1);
            errorChar[1] = 'f' + 1;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(errorChar));
            assertEquals(e.position(), 1);
        }
    }

    private void testHex(int size) {
        byte[] src = randomBytes(size);
        String hexUpper;
        String hexLower;
        {
            // base
            hexUpper = HexKit.encoder().encodeToString(src);
            String hexUpperBuf = HexKit.encoder().encodeToString(ByteBuffer.wrap(src));
            assertEquals(hexUpperBuf, hexUpper);
            hexLower = HexKit.encoder(false).encodeToString(src);
            assertEquals(hexUpper, Hex.encodeHexString(src).toUpperCase());
            assertEquals(hexLower, Hex.encodeHexString(src).toLowerCase());
            byte[] deUpperStrict = HexKit.decoder().decode(hexUpper);
            byte[] deUpperLoose = HexKit.decoder(false).decode(hexUpper);
            byte[] deLowerStrict = HexKit.decoder().decode(hexLower);
            byte[] deLowerLoose = HexKit.decoder(false).decode(hexLower);
            assertEquals(deUpperStrict, src);
            assertEquals(deUpperLoose, src);
            assertEquals(deLowerStrict, src);
            assertEquals(deLowerLoose, src);
            // decode by buffer
            ByteBuffer upperBuf = ByteBuffer.wrap(hexUpper.getBytes(StandardCharsets.ISO_8859_1));
            ByteBuffer lowerBuf = ByteBuffer.wrap(hexLower.getBytes(StandardCharsets.ISO_8859_1));
            assertEquals(HexKit.decoder().decode(upperBuf), src);
            assertEquals(HexKit.decoder().decode(lowerBuf), src);
        }
        {
            // add bad char
            byte[] src2 = new byte[src.length * 2];
            System.arraycopy(src, 0, src2, 0, src.length);
            System.arraycopy(src, 0, src2, src.length, src.length);
            String hexUpper2 = hexUpper + "hhh" + hexUpper;
            String hexLower2 = hexLower + "hhh" + hexLower;
            assertEquals(HexKit.decoder(false).decode(hexUpper2), src2);
            assertEquals(HexKit.decoder(false).decode(hexLower2), src2);
            ByteBuffer upperBuf2 = ByteBuffer.wrap(hexUpper2.getBytes(StandardCharsets.ISO_8859_1));
            ByteBuffer lowerBuf2 = ByteBuffer.wrap(hexLower2.getBytes(StandardCharsets.ISO_8859_1));
            assertEquals(HexKit.decoder(false).decode(upperBuf2), src2);
            assertEquals(HexKit.decoder(false).decode(lowerBuf2), src2);
            HexKit.HexException e;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(hexUpper2));
            assertEquals(e.position(), -1);
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(hexLower2));
            assertEquals(e.position(), -1);
            String hexUpper3 = hexUpper + "hhhh" + hexUpper;
            String hexLower3 = hexLower + "hhhh" + hexLower;
            ByteBuffer upperBuf3 = ByteBuffer.wrap(hexUpper3.getBytes(StandardCharsets.ISO_8859_1));
            ByteBuffer lowerBuf3 = ByteBuffer.wrap(hexLower3.getBytes(StandardCharsets.ISO_8859_1));
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(upperBuf3));
            assertEquals(e.position(), hexUpper.length());
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(lowerBuf3));
            assertEquals(e.position(), hexLower.length());
            String upperTail = hexUpper + "A";
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder(false).decode(upperTail));
            assertEquals(e.position(), -1);
        }
    }

    @Test
    public void testHexException() {
        HexKit.HexException e;
        e = expectThrows(HexKit.HexException.class, () -> {
            throw new HexKit.HexException();
        });
        assertEquals(e.position(), -1);
        e = expectThrows(HexKit.HexException.class, () -> {
            throw new HexKit.HexException("");
        });
        assertEquals(e.position(), -1);
        e = expectThrows(HexKit.HexException.class, () -> {
            throw new HexKit.HexException("", new RuntimeException());
        });
        assertEquals(e.position(), -1);
        e = expectThrows(HexKit.HexException.class, () -> {
            throw new HexKit.HexException(new RuntimeException());
        });
        assertEquals(e.position(), -1);
        e = expectThrows(HexKit.HexException.class, () -> {
            throw new HexKit.HexException(66);
        });
        assertEquals(e.position(), 66);
        e = expectThrows(HexKit.HexException.class, () -> {
            throw new HexKit.HexException(66, "");
        });
        assertEquals(e.position(), 66);
        e = expectThrows(HexKit.HexException.class, () -> {
            throw new HexKit.HexException(66, "", new RuntimeException());
        });
        assertEquals(e.position(), 66);
        e = expectThrows(HexKit.HexException.class, () -> {
            throw new HexKit.HexException(66, new RuntimeException());
        });
        assertEquals(e.position(), 66);
    }
}
