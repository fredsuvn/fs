package tests.codec;

import org.testng.annotations.Test;
import xyz.sunqian.common.codec.DigestKit;
import xyz.sunqian.common.io.ByteProcessor;
import xyz.sunqian.test.DataTest;

import java.security.MessageDigest;

import static org.testng.Assert.assertEquals;

public class DigestTest implements DataTest {

    @Test
    public void testDigest() throws Exception {
        testDigest(MessageDigest.getInstance("MD5"), 1024, 16);
        testDigest(MessageDigest.getInstance("MD5"), 3337, 97);
    }

    private void testDigest(MessageDigest digest, int totalSize, int blockSize) {
        byte[] data = randomBytes(totalSize);
        byte[] enBytes = ByteProcessor.from(data)
            .readBlockSize(blockSize)
            .transformer(DigestKit.digestTransformer(digest))
            .toByteArray();
        assertEquals(enBytes, digest.digest(data));
    }
}
