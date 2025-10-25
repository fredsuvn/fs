package tests.codec;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;
import space.sunqian.common.codec.HexKit;
import internal.test.DataTest;

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
            int midIndex = src.length / 2;
            String defectiveUpper = '%' +
                hexUpper.substring(0, midIndex) +
                '%' +
                hexUpper.substring(midIndex) +
                '%';
            String defectiveLower = '%' +
                hexLower.substring(0, midIndex) +
                '%' +
                hexLower.substring(midIndex) +
                '%';
            assertEquals(HexKit.decoder(false).decode(defectiveUpper), src);
            assertEquals(HexKit.decoder(false).decode(defectiveLower), src);
            ByteBuffer upperBuf2 = ByteBuffer.wrap(defectiveUpper.getBytes(StandardCharsets.ISO_8859_1));
            ByteBuffer lowerBuf2 = ByteBuffer.wrap(defectiveLower.getBytes(StandardCharsets.ISO_8859_1));
            assertEquals(HexKit.decoder(false).decode(upperBuf2), src);
            assertEquals(HexKit.decoder(false).decode(lowerBuf2), src);
            HexKit.HexException e;
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(defectiveUpper));
            assertEquals(e.position(), -1);
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(defectiveLower));
            assertEquals(e.position(), -1);
            String defectiveUpper2 =
                hexUpper.substring(0, midIndex) +
                    "%" +
                    hexUpper.substring(midIndex) +
                    '%';
            String defectiveLower2 =
                hexLower.substring(0, midIndex) +
                    "%" +
                    hexLower.substring(midIndex) +
                    '%';
            ByteBuffer upperBuf3 = ByteBuffer.wrap(defectiveUpper2.getBytes(StandardCharsets.ISO_8859_1));
            ByteBuffer lowerBuf3 = ByteBuffer.wrap(defectiveLower2.getBytes(StandardCharsets.ISO_8859_1));
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(upperBuf3));
            assertEquals(e.position(), midIndex);
            e = expectThrows(HexKit.HexException.class, () -> HexKit.decoder().decode(lowerBuf3));
            assertEquals(e.position(), midIndex);
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
