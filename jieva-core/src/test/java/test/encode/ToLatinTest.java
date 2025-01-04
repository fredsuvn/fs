package test.encode;

import org.apache.commons.codec.binary.Hex;
import org.testng.annotations.Test;
import xyz.sunqian.common.encode.JieHex;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import static org.testng.Assert.assertEquals;

public class ToLatinTest {

    @Test
    public void toLatinTest() throws Exception {
        String hexStr = "0123456789ABCDEFabcdef";
        byte[] srcBytes = Hex.decodeHex(hexStr);
        assertEquals(JieHex.encoder().toLatin(srcBytes), hexStr.toUpperCase());
        assertEquals(JieHex.encoder().toLatin(ByteBuffer.wrap(srcBytes)), hexStr.toUpperCase());
        assertEquals(JieHex.decoder().fromLatin(hexStr), srcBytes);
        assertEquals(JieHex.decoder().fromLatin(CharBuffer.wrap(hexStr)), ByteBuffer.wrap(srcBytes));
        assertEquals(JieHex.decoder().fromLatin(hexStr.toCharArray()), srcBytes);
    }
}
