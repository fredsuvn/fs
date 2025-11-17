package tests.codec;

import org.junit.jupiter.api.Test;
import space.sunqian.common.codec.DigestKit;
import space.sunqian.common.io.ByteProcessor;
import internal.test.DataTest;

import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertArrayEquals(enBytes, digest.digest(data));
    }
}
