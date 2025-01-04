package xyz.sunqian.common.crypto;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.io.BytesProcessor;
import xyz.sunqian.common.io.JieIO;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.security.Provider;

public class JieCrypto {

    public static Cipher newCipher(String algorithm, @Nullable Provider provider) {
        try {
            return provider == null ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public static BytesProcessor.Encoder cipherEncoder(Cipher cipher, int maxSize) {
        BytesProcessor.Encoder encoder = new BytesProcessor.Encoder() {
            @Override
            public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
                try {
                    if (maxSize > 0) {
                        if (JieBytes.isEmpty(data)) {
                            return null;
                        }
                        return doFinal(cipher, data);
                    }
                    ByteBuffer ret;
                    if (end) {
                        ret = doFinal(cipher, data);
                    } else {
                        ret = doUpdate(cipher, data);
                    }
                    return ret;
                } catch (Exception e) {
                    throw new CryptoException(e);
                }
            }
        };
        return JieIO.fixedSizeEncoder(maxSize > 0 ? maxSize : cipher.getBlockSize(), encoder);
    }

    private static ByteBuffer doFinal(Cipher cipher, ByteBuffer input) throws Exception {
        ByteBuffer out = ByteBuffer.allocate(cipher.getOutputSize(input.remaining()));
        cipher.doFinal(input, out);
        out.flip();
        if (out.limit() < out.capacity()) {
            return JieBytes.copyBuffer(out);
        }
        return out;
    }

    private static ByteBuffer doUpdate(Cipher cipher, ByteBuffer input) throws Exception {
        ByteBuffer out = ByteBuffer.allocate(cipher.getOutputSize(input.remaining()));
        cipher.update(input, out);
        out.flip();
        if (out.limit() < out.capacity()) {
            return JieBytes.copyBuffer(out);
        }
        return out;
    }
}
