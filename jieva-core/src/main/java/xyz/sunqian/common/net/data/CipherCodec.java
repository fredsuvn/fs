package xyz.sunqian.common.net.data;

import xyz.sunqian.annotations.Nullable;
//import xyz.sunqian.common.io.ByteEncoder;
import xyz.sunqian.common.io.IOKit;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Cipher implementation of {@link CodecConfigurator} to encrypt/decrypt with {@link Cipher}.
 *
 * @author fredsuvn
 */
public class CipherCodec implements CodecConfigurator<CipherCodec> {

    private Cipher cipher;
    private Object input;
    private Object output;
    private int blockSize;
    private SecureRandom secureRandom;
    private Certificate certificate;
    private AlgorithmParameters algorithmParameters;
    private AlgorithmParameterSpec algorithmParameterSpec;
    private int mode;
    private Key key;

    CipherCodec() {
        reset();
    }

    @Override
    public CipherCodec input(byte[] array) {
        return input(ByteBuffer.wrap(array));
    }

    @Override
    public CipherCodec input(ByteBuffer buffer) {
        this.input = buffer;
        return this;
    }

    @Override
    public CipherCodec input(InputStream in) {
        this.input = in;
        return this;
    }

    @Override
    public CipherCodec output(byte[] array) {
        return output(ByteBuffer.wrap(array));
    }

    @Override
    public CipherCodec output(ByteBuffer buffer) {
        this.output = buffer;
        return this;
    }

    @Override
    public CipherCodec output(OutputStream out) {
        this.output = out;
        return this;
    }

    @Override
    public CipherCodec reset() {
        this.cipher = null;
        this.input = null;
        this.output = null;
        this.blockSize = 0;
        this.secureRandom = null;
        this.certificate = null;
        this.algorithmParameters = null;
        this.algorithmParameterSpec = null;
        this.mode = 0;
        this.key = null;
        return this;
    }

    /**
     * Sets cipher algorithm.
     *
     * @param cipher cipher algorithm
     * @return this
     */
    public CipherCodec algorithm(Cipher cipher) {
        this.cipher = cipher;
        return this;
    }

    /**
     * Sets cipher algorithm and provider.
     *
     * @param algorithm cipher algorithm
     * @return this
     */
    public CipherCodec algorithm(String algorithm) {
        return algorithm(algorithm, null);
    }

    /**
     * Sets cipher algorithm and provider.
     *
     * @param algorithm cipher algorithm
     * @param provider  cipher provider
     * @return this
     */
    public CipherCodec algorithm(String algorithm, @Nullable Provider provider) {
        try {
            this.cipher = provider == null ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
            return this;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    /**
     * Sets block size for encryption/decryption operations.
     *
     * @param blockSize block size
     * @return this
     */
    public CipherCodec blockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    /**
     * Sets secure random.
     *
     * @param secureRandom secure random
     * @return this
     */
    public CipherCodec secureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    /**
     * Sets certificate.
     *
     * @param certificate certificate
     * @return this
     */
    public CipherCodec certificate(Certificate certificate) {
        this.certificate = certificate;
        return this;
    }

    /**
     * Sets algorithm parameters.
     *
     * @param algorithmParameters algorithm parameters
     * @return this
     */
    public CipherCodec algorithmParameters(AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }

    /**
     * Sets algorithm parameter spec.
     *
     * @param algorithmParameterSpec algorithm parameter spec
     * @return this
     */
    public CipherCodec algorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
        this.algorithmParameterSpec = algorithmParameterSpec;
        return this;
    }

    /**
     * Sets mode to {@link Cipher#ENCRYPT_MODE}.
     *
     * @return this
     */
    public CipherCodec encrypt() {
        this.mode = Cipher.ENCRYPT_MODE;
        return this;
    }

    /**
     * Sets mode to {@link Cipher#DECRYPT_MODE}.
     *
     * @return this
     */
    public CipherCodec decrypt() {
        this.mode = Cipher.DECRYPT_MODE;
        return this;
    }

    /**
     * Sets key.
     *
     * @param key key
     * @return this
     */
    public CipherCodec key(Key key) {
        this.key = key;
        return this;
    }

    @Override
    public long doFinal() {
        try {
            if (cipher == null) {
                throw new CodecException("Cipher algorithm is not set.");
            }
            initCipher(cipher);
            if (input instanceof ByteBuffer) {
                if (output instanceof ByteBuffer) {
                    return JieCodec.doCipher(cipher, (ByteBuffer) input, (ByteBuffer) output, blockSize);
                } else if (output instanceof OutputStream) {
                    return JieCodec.doCipher(cipher, (ByteBuffer) input, (OutputStream) output, blockSize);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof InputStream) {
                if (output instanceof ByteBuffer) {
                    return JieCodec.doCipher(cipher, (InputStream) input, (ByteBuffer) output, blockSize);
                } else if (output instanceof OutputStream) {
                    return JieCodec.doCipher(cipher, (InputStream) input, (OutputStream) output, blockSize);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else {
                throw new CodecException("Unknown input type: " + input.getClass());
            }
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    @Override
    public InputStream finalStream() {
        try {
            if (cipher == null) {
                throw new CodecException("Cipher algorithm is not set.");
            }
            initCipher(cipher);
            InputStream source;
            if (input instanceof ByteBuffer) {
                source = IOKit.newInputStream((ByteBuffer) input);
            } else if (input instanceof InputStream) {
                source = (InputStream) input;
            } else {
                throw new CodecException("Unknown input type: " + input.getClass());
            }
            return null;//ByteEncoder.from(source).readBlockSize(blockSize).handler((data, end) ->
                    //ByteBuffer.wrap(JieCodec.doCipher(cipher, data)))
                //.asInputStream();
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    private void initCipher(Cipher cipher) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (secureRandom != null) {
            if (certificate != null) {
                cipher.init(mode, certificate, secureRandom);
            } else if (algorithmParameters != null) {
                cipher.init(mode, key, algorithmParameters, secureRandom);
            } else if (algorithmParameterSpec != null) {
                cipher.init(mode, key, algorithmParameterSpec, secureRandom);
            } else {
                cipher.init(mode, key, secureRandom);
            }
        } else {
            if (certificate != null) {
                cipher.init(mode, certificate);
            } else if (algorithmParameters != null) {
                cipher.init(mode, key, algorithmParameters);
            } else if (algorithmParameterSpec != null) {
                cipher.init(mode, key, algorithmParameterSpec);
            } else {
                cipher.init(mode, key);
            }
        }
    }
}
