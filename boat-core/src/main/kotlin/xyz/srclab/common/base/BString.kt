@file:JvmName("BString")

package xyz.srclab.common.base

import com.google.common.base.CharMatcher
import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Supplier
import kotlin.text.lines as linesKt
import kotlin.text.toCollection as toCollectionKt
import kotlin.text.toHashSet as toHashSetKt
import kotlin.text.toList as toListKt
import kotlin.text.toSet as toSetKt
import kotlin.text.toSortedSet as toSortedSetKt

/**
 * Default charset: UTF-8.
 */
@JvmField
val DEFAULT_CHARSET: Charset = StandardCharsets.UTF_8

/**
 * [CharMatcher] of pattern dot: `.`
 */
@JvmField
val DOT_MATCHER: CharMatcher = CharMatcher.`is`('.')

/**
 * [CharMatcher] of pattern underscore: `_`
 */
@JvmField
val UNDERSCORE_MATCHER: CharMatcher = CharMatcher.`is`('_')

/**
 * [CharMatcher] of pattern space: ` `
 */
@JvmField
val SPACE_MATCHER: CharMatcher = CharMatcher.`is`(' ')

/**
 * [CharMatcher] of pattern hyphen: `-`
 */
@JvmField
val HYPHEN_MATCHER: CharMatcher = CharMatcher.`is`('-')

/**
 * [CharMatcher] of pattern plus sign: `+`
 */
@JvmField
val PLUS_MATCHER: CharMatcher = CharMatcher.`is`('+')

/**
 * [CharMatcher] of pattern `[0-9]`
 */
@JvmField
val NUMERIC_MATCHER: CharMatcher = CharMatcher.inRange('0', '9')

/**
 * [CharMatcher] of pattern `[A-Z]`
 */
@JvmField
val UPPER_CASE_MATCHER: CharMatcher = CharMatcher.inRange('A', 'Z')

/**
 * [CharMatcher] of pattern `[a-z]`
 */
@JvmField
val LOWER_CASE_MATCHER: CharMatcher = CharMatcher.inRange('a', 'z')

/**
 * [CharMatcher] of pattern `[A-Z][a-z]`
 */
@JvmField
val LETTER_MATCHER: CharMatcher = UPPER_CASE_MATCHER.and(LOWER_CASE_MATCHER)

//Utils:

/**
 * Checks if given chars is empty or null.
 */
fun CharSequence?.isEmpty(): Boolean {
    return this.isNullOrEmpty()
}

/**
 * Checks if given chars is empty or all blank.
 */
fun CharSequence?.isBlank(): Boolean {
    return this.isNullOrBlank()
}

/**
 * Checks if given chars is numeric by [Character.isDigit].
 */
fun CharSequence?.isNumeric(): Boolean {
    return StringUtils.isNumeric(this)
}

/**
 * Checks if given chars are all white space by [Character.isWhitespace].
 */
fun CharSequence?.isWhitespace(): Boolean {
    return StringUtils.isWhitespace(this)
}

/**
 * Checks if given chars contains leading zeros.
 */
fun CharSequence?.isLedByZeros(): Boolean {
    if (this === null || this.length < 2) {
        return false
    }
    return this[0] == '0'
}

fun anyEmpty(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.isEmpty()) {
            return true
        }
    }
    return false
}

fun anyEmpty(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.isEmpty()) {
            return true
        }
    }
    return false
}

fun allEmpty(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.isEmpty()) {
            return false
        }
    }
    return true
}

fun allEmpty(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.isEmpty()) {
            return false
        }
    }
    return true
}

fun anyBlank(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.isBlank()) {
            return true
        }
    }
    return false
}

fun anyBlank(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.isBlank()) {
            return true
        }
    }
    return false
}

fun allBlank(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.isBlank()) {
            return false
        }
    }
    return true
}

fun allBlank(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.isBlank()) {
            return false
        }
    }
    return true
}

fun CharSequence.lowerCase(): String {
    return this.toString().lowercase()
}

fun CharSequence.upperCase(): String {
    return this.toString().uppercase()
}

/**
 * Sets first character of given chars upper case.
 */
fun CharSequence.capitalize(): String {
    return StringUtils.capitalize(this.toString())
}

/**
 * Sets first character of given chars lower case.
 */
fun CharSequence.uncapitalize(): String {
    return StringUtils.uncapitalize(this.toString())
}

@JvmOverloads
fun CharSequence?.equals(other: CharSequence?, ignoreCase: Boolean = false): Boolean {
    return this.contentEquals(other, ignoreCase)
}

fun CharSequence?.equalsAny(vararg others: CharSequence?): Boolean {
    for (other in others) {
        if (this == other) {
            return true
        }
    }
    return false
}

fun CharSequence?.equalsAll(vararg others: CharSequence?): Boolean {
    for (other in others) {
        if (this != other) {
            return false
        }
    }
    return true
}

fun CharSequence?.equalsAnyIgnoreCase(vararg others: CharSequence?): Boolean {
    for (other in others) {
        if (this.equals(other, true)) {
            return true
        }
    }
    return false
}

fun CharSequence?.equalsAllIgnoreCase(vararg others: CharSequence?): Boolean {
    for (other in others) {
        if (this.equals(other, true)) {
            return false
        }
    }
    return true
}

@JvmOverloads
fun CharSequence?.equalsAny(others: Iterable<CharSequence?>, ignoreCase: Boolean = false): Boolean {
    for (other in others) {
        if (this.equals(other, ignoreCase)) {
            return true
        }
    }
    return false
}

@JvmOverloads
fun CharSequence?.equalsAll(others: Iterable<CharSequence?>, ignoreCase: Boolean = false): Boolean {
    for (other in others) {
        if (this.equals(other, ignoreCase)) {
            return false
        }
    }
    return true
}

fun CharSequence.toCharSet(): Charset {
    return Charset.forName(this.toString())
}

fun CharSequence.lines(): List<String> {
    return this.linesKt()
}

@JvmOverloads
fun CharSequence.byteArray(charset: Charset = DEFAULT_CHARSET): ByteArray {
    return this.toString().toByteArray(charset)
}

@JvmOverloads
fun CharArray.byteArray(charset: Charset = DEFAULT_CHARSET): ByteArray {
    return String(this).toByteArray(charset)
}

@JvmOverloads
fun ByteArray.string(charset: Charset = DEFAULT_CHARSET): String {
    return String(this, charset)
}

@JvmOverloads
fun ByteArray.charArray(charset: Charset = DEFAULT_CHARSET): CharArray {
    return String(this, charset).toCharArray()
}

fun CharSequence.charArray(): CharArray {
    val array = CharArray(this.length)
    for (c in this.withIndex()) {
        array[c.index] = c.value
    }
    return array
}

fun CharArray.string(): String {
    return String(this)
}

//toCollection:

fun <C : MutableCollection<in Char>> CharSequence.toCollection(destination: C): C {
    return this.toCollectionKt(destination)
}

fun CharSequence.toSet(): Set<Char> {
    return this.toSetKt()
}

fun CharSequence.toHashSet(): HashSet<Char> {
    return this.toHashSetKt()
}

fun CharSequence.toSortedSet(): SortedSet<Char> {
    return this.toSortedSetKt()
}

fun CharSequence.toList(): List<Char> {
    return this.toListKt()
}

//Lazy:

@JvmSynthetic
fun lazyString(supplier: () -> String): LazyString {
    return LazyString.of(supplier.toSupplier())
}

/**
 * String reference, to refer to a range of [CharSequence] with indexes but not store the copy of data.
 */
interface StringRef : CharSequence {

    /**
     * Copies and returns a new String of current range.
     */
    fun copyOfRange(): String

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun CharSequence.stringRef(startIndex: Int = 0, endIndex: Int = this.length): StringRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.length)
            return CharSeqStringRef(this, startIndex, endIndex)
        }

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun CharArray.stringRef(startIndex: Int = 0, endIndex: Int = this.size): StringRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return CharArrayStringRef(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun CharSequence.stringRefByOffset(
            offset: Int = 0,
            length: Int = remainingLength(this.length, offset)
        ): StringRef {
            return stringRef(offset, offset + length)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun CharArray.stringRefByOffset(offset: Int = 0, length: Int = remainingLength(this.size, offset)): StringRef {
            return stringRef(offset, offset + length)
        }

        private class CharSeqStringRef(
            private val chars: CharSequence,
            private val startIndex: Int,
            private val endIndex: Int
        ) : StringRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Char {
                index.checkIndexInBounds(0, endIndex)
                return chars[index.actualIndex()]
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return CharSeqStringRef(chars, startIndex.actualIndex(), endIndex.actualIndex())
            }

            override fun copyOfRange(): String {
                return chars.subSequence(startIndex, endIndex).toString()
            }

            private fun Int.actualIndex() = this + startIndex

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is CharSeqStringRef) return false
                if (chars !== other.chars) return false
                if (startIndex != other.startIndex) return false
                if (endIndex != other.endIndex) return false
                return true
            }

            override fun hashCode(): Int {
                var result = chars.hashCode()
                result = 31 * result + startIndex
                result = 31 * result + endIndex
                return result
            }

            override fun toString(): String {
                return copyOfRange()
            }
        }

        private class CharArrayStringRef(
            private val chars: CharArray,
            private val startIndex: Int,
            private val endIndex: Int
        ) : StringRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Char {
                index.checkIndexInBounds(0, endIndex)
                return chars[index.actualIndex()]
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return CharArrayStringRef(chars, startIndex.actualIndex(), endIndex.actualIndex())
            }

            override fun copyOfRange(): String {
                return String(chars, startIndex, remainingLength(chars.size, startIndex))
            }

            private fun Int.actualIndex() = this + startIndex

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is CharArrayStringRef) return false
                if (chars !== other.chars) return false
                if (startIndex != other.startIndex) return false
                if (endIndex != other.endIndex) return false
                return true
            }

            override fun hashCode(): Int {
                var result = chars.hashCode()
                result = 31 * result + startIndex
                result = 31 * result + endIndex
                return result
            }

            override fun toString(): String {
                return copyOfRange()
            }
        }
    }
}

/**
 * Represents a string with lazy initialization.
 *
 * This class is usually used for logging.
 */
interface LazyString : CharSequence {

    companion object {

        @JvmStatic
        fun of(supplier: Supplier<String>): LazyString {
            return LazyStringImpl(supplier)
        }

        private class LazyStringImpl(private val supplier: Supplier<String>) : LazyString {

            private val value: String by lazy { supplier.get() }

            override val length: Int
                get() = value.length

            override fun get(index: Int): Char {
                return value[index]
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return value.subSequence(startIndex, endIndex)
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is LazyString) return false
                return value == other.toString()
            }

            override fun hashCode(): Int {
                return value.hashCode()
            }

            override fun toString(): String {
                return value
            }
        }
    }
}

/**
 * Represents password, use [clear] to clear content.
 */
interface Password : CharSequence {

    val isClear: Boolean

    fun clear()

    fun toCharArray(): CharArray

    companion object {

        @JvmName("of")
        @JvmStatic
        fun CharArray.toPassword(offset: Int = 0, length: Int = remainingLength(this.size, offset)): Password {
            return PasswordImpl(this, offset, length)
        }

        @JvmName("of")
        @JvmStatic
        fun CharSequence.toPassword(): Password {
            return PasswordImpl(this)
        }

        private class PasswordImpl : Password {

            private val passwordChars: CharArray
            private var clear = false

            constructor(chars: CharArray, offset: Int, length: Int) {
                this.passwordChars = chars.copyOfRange(offset, offset + length)
            }

            constructor(chars: CharSequence) {
                this.passwordChars = CharArray(chars.length)
                var i = 0
                while (i < passwordChars.size) {
                    passwordChars[i] = chars[i]
                    i++
                }
            }

            override val length: Int
                get() = passwordChars.size

            override val isClear: Boolean
                get() = clear

            override fun get(index: Int): Char {
                return passwordChars[index]
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return PasswordImpl(passwordChars, startIndex, endIndex)
            }

            override fun clear() {
                var i = 0
                while (i < passwordChars.size) {
                    passwordChars[i] = 0.toChar()
                    i++
                }
                clear = true
            }

            override fun toCharArray(): CharArray {
                return passwordChars.copyOfRange(0, passwordChars.size)
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Password) return false
                return this.contentEquals(other)
            }

            override fun hashCode(): Int {
                return passwordChars.contentHashCode()
            }

            override fun toString(): String {
                return String(passwordChars)
            }
        }
    }
}