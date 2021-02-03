package xyz.srclab.common.codec

import xyz.srclab.common.codec.CodecBytes
import javax.crypto.spec.SecretKeySpec
import xyz.srclab.common.codec.CodecAlgorithmConstants
import java.lang.IllegalStateException
import xyz.srclab.common.codec.AsymmetricCipher
import java.security.KeyPairGenerator
import java.security.spec.X509EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.NoSuchAlgorithmException
import xyz.srclab.common.codec.CodecAlgorithm
import java.io.IOException
import xyz.srclab.common.codec.AbstractCodecKeyPair
import xyz.srclab.common.codec.CodecKeyPair
import xyz.srclab.common.codec.ReversibleCipher
import xyz.srclab.common.codec.CodecImpl
import xyz.srclab.common.codec.CodecKeySupport
import xyz.srclab.common.codec.SymmetricCipherImpl
import xyz.srclab.common.codec.DigestCipher
import xyz.srclab.common.codec.DigestCipherImpl
import xyz.srclab.common.codec.HmacDigestCipher
import xyz.srclab.common.codec.HmacDigestCipherImpl
import xyz.srclab.common.codec.CodecAlgorithmSupport
import java.util.NoSuchElementException
import xyz.srclab.common.codec.CodecAlgorithmSupport.CodecAlgorithmImpl
import xyz.srclab.common.codec.CodecCipher
import java.lang.Exception
import java.security.MessageDigest
import javax.crypto.*

/**
 * @author sunqian
 */
internal class HmacDigestCipherImpl(private val algorithm: String?) : HmacDigestCipher<SecretKey?> {
    override fun digest(key: SecretKey?, data: ByteArray?): ByteArray? {
        return try {
            val mac = Mac.getInstance(algorithm)
            mac.init(key)
            mac.doFinal(data)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun digest(keyBytes: ByteArray?, data: ByteArray?): ByteArray? {
        return digest(Codec.Companion.secretKey(keyBytes, algorithm), data)
    }

    override fun name(): String? {
        return algorithm
    }
}