package xyz.sunqian.common.crypto;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.io.BytesProcessor;
import xyz.sunqian.common.io.JieIO;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;

public class JieCrypto {

    public static BytesProcessor.Encoder cipherEncoder(Cipher cipher, int blockSize, boolean useFinal) {
        BytesProcessor.Encoder encoder = new BytesProcessor.Encoder() {
            @Override
            public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
                try {
                    if (useFinal) {
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
        return JieIO.fixedSizeEncoder(blockSize, encoder);
    }

    private static ByteBuffer doFinal(Cipher cipher, ByteBuffer input) throws Exception {
        byte[] ret;
        if (input.hasArray()) {
            ret = cipher.doFinal(
                input.array(), input.arrayOffset() + input.position(), input.remaining()
            );
            input.position(input.position() + input.remaining());
        } else {
            byte[] inBytes = JieBytes.getBytes(input);
            ret = cipher.doFinal(inBytes);
        }
        return toBuffer(ret);
    }

    private static ByteBuffer doUpdate(Cipher cipher, ByteBuffer input) throws Exception {
        byte[] ret;
        if (input.hasArray()) {
            ret = cipher.update(
                input.array(), input.arrayOffset() + input.position(), input.remaining()
            );
            input.position(input.position() + input.remaining());
        } else {
            byte[] inBytes = JieBytes.getBytes(input);
            ret = cipher.update(inBytes);
        }
        return toBuffer(ret);
    }

    private static ByteBuffer toBuffer(@Nullable byte[] ret) {
        return JieArray.isEmpty(ret) ? null : ByteBuffer.wrap(ret);
    }
}
