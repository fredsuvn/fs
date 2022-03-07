@file:JvmName("BEncode")

package xyz.srclab.common.base

import xyz.srclab.common.io.BytesAppender
import xyz.srclab.common.io.asInputStream
import xyz.srclab.common.io.unclose
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

fun getBase64Length(sourceSize: Int): Int {
    val expected = sourceSize / 3 * 4
    return if (sourceSize % 3 == 0) expected else expected + 4
}

fun getBase64Length(sourceSize: Long): Long {
    val expected = sourceSize / 3 * 4
    return if (sourceSize % 3 == 0L) expected else expected + 4
}

fun getDeBase64Length(base64Size: Int): Int {
    return base64Size / 4 * 3
}

fun getDeBase64Length(base64Size: Long): Long {
    return base64Size / 4 * 3
}

@JvmOverloads
fun CharSequence.base64(charset: Charset = defaultCharset()): String {
    return this.charsToBytes(charset).base64().bytesToString8Bit()
}

fun ByteArray.base64(): ByteArray {
    return Base64.getEncoder().encode(this)
}

fun ByteBuffer.base64(): ByteBuffer {
    return Base64.getEncoder().encode(this)
}

@JvmOverloads
fun ByteArray.base64(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    return this.asInputStream(offset, length).base64()
}

fun InputStream.base64(): ByteArray {
    val output = BytesAppender(getBase64Length(this.available()))
    val encOut = Base64.getEncoder().wrap(output)
    this.copyTo(encOut)
    encOut.close()
    return output.toBytes()
}

fun InputStream.base64(output: OutputStream): Long {
    val out = output.unclose()
    val encOut = Base64.getEncoder().wrap(out)
    val result = this.copyTo(encOut)
    encOut.close()
    return result
}

@JvmOverloads
fun CharSequence.deBase64(charset: Charset = defaultCharset()): String {
    return this.charsToBytes8Bit().deBase64().bytesToString(charset)
}

fun ByteArray.deBase64(): ByteArray {
    return Base64.getDecoder().decode(this)
}

fun ByteBuffer.deBase64(): ByteBuffer {
    return Base64.getDecoder().decode(this)
}

@JvmOverloads
fun ByteArray.deBase64(offset: Int, length: Int = remainingLength(this.size, offset)): ByteArray {
    return this.asInputStream(offset, length).deBase64()
}

fun InputStream.deBase64(): ByteArray {
    val encIn = Base64.getDecoder().wrap(this)
    val output = BytesAppender(getBase64Length(this.available()))
    encIn.copyTo(output)
    return output.toBytes()
}

fun InputStream.deBase64(output: OutputStream): Long {
    val encIn = Base64.getDecoder().wrap(this)
    return encIn.copyTo(output)
}