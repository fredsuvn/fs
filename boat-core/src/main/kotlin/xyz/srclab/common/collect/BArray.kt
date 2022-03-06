@file:JvmName("BArray")

package xyz.srclab.common.collect

import xyz.srclab.common.base.*
import xyz.srclab.common.collect.ArrayBridge.Companion.toArrayBridge
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Type
import java.util.function.Function
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt

private const val NOT_ARRAY_TYPE_PREFIX = "Not an array type"

/**
 * Returns given length of given array ([this]).
 */
@JvmName("getLength")
fun Any.arrayLength(): Int {
    return java.lang.reflect.Array.getLength(this)
}

/**
 * Returns a new array which is a copy of the specified range of the original array.
 *
 * @param fromIndex start index inclusive
 * @param toIndex end index exclusive
 */
@JvmName("copyOfRange")
@JvmOverloads
fun <A : Any> A.arrayCopyOfRannge(fromIndex: Int = 0, toIndex: Int = this.arrayLength()): A {
    return when (this) {
        is Array<*> -> this.copyOfRange(fromIndex, toIndex)
        is BooleanArray -> this.copyOfRange(fromIndex, toIndex)
        is ByteArray -> this.copyOfRange(fromIndex, toIndex)
        is ShortArray -> this.copyOfRange(fromIndex, toIndex)
        is CharArray -> this.copyOfRange(fromIndex, toIndex)
        is IntArray -> this.copyOfRange(fromIndex, toIndex)
        is LongArray -> this.copyOfRange(fromIndex, toIndex)
        is FloatArray -> this.copyOfRange(fromIndex, toIndex)
        is DoubleArray -> this.copyOfRange(fromIndex, toIndex)
        else -> throw IllegalArgumentException("Not an array: $this!")
    }.asTyped()
}

@JvmName("arrayOf")
fun <T> newArrayOf(vararg elements: T): Array<T> {
    return elements.asTyped()
}

fun <T> Class<T>.newArray(length: Int): Array<T> {
    return java.lang.reflect.Array.newInstance(this, length).asTyped()
}

fun <T> Type.newArray(length: Int): Array<T> {
    return this.rawClass.newArray(length).asTyped()
}

fun Class<*>.newPrimitiveArray(length: Int): Any {
    return java.lang.reflect.Array.newInstance(this, length)
}

@JvmOverloads
fun <T> Array<T>.add(element: T, index: Int = this.size): Array<T> {
    if (index == this.size) {
        val result = this.copyOf(this.size + 1)
        result[this.size] = element
        return result.asTyped()
    }
    index.checkInBounds(0, this.size)
    val result: Array<T?> = this.javaClass.componentType.newArray(this.size + 1).asTyped()
    System.arraycopy(this, 0, result, 0, index)
    result[index] = element
    System.arraycopy(this, index, result, index + 1, this.size - index)
    return result.asTyped()
}

@JvmOverloads
fun <T> Array<T>.remove(index: Int = this.size - 1): Array<T> {
    if (index == this.size - 1) {
        val result = this.copyOf(this.size - 1)
        return result.asTyped()
    }
    index.checkInBounds(0, this.size)
    val result: Array<T?> = this.javaClass.componentType.newArray(this.size - 1).asTyped()
    System.arraycopy(this, 0, result, 0, index)
    System.arraycopy(this, index + 1, result, index, this.size - index - 1)
    return result.asTyped()
}

/**
 * Returns a fixed-size array associated given array,
 * or throws [IllegalArgumentException] if given object is not an array.
 */
@JvmName("asList")
fun <T> Any.arrayAsList(): MutableList<T> {
    return when (this) {
        is Array<*> -> this.asList().asTyped()
        is BooleanArray -> this.asList().asTyped()
        is ByteArray -> this.asList().asTyped()
        is ShortArray -> this.asList().asTyped()
        is CharArray -> this.asList().asTyped()
        is IntArray -> this.asList().asTyped()
        is LongArray -> this.asList().asTyped()
        is FloatArray -> this.asList().asTyped()
        is DoubleArray -> this.asList().asTyped()
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun <T> Array<T>.asList(): MutableList<T> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun ByteArray.asList(): MutableList<Byte> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun ShortArray.asList(): MutableList<Short> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun IntArray.asList(): MutableList<Int> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun LongArray.asList(): MutableList<Long> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun FloatArray.asList(): MutableList<Float> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun DoubleArray.asList(): MutableList<Double> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun BooleanArray.asList(): MutableList<Boolean> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun CharArray.asList(): MutableList<Char> {
    return ArrayBridgeList(this.toArrayBridge())
}

@JvmOverloads
fun <T> Array<T>.indexOfArray(content: Array<T>, start: Int = 0, end: Int = content.size): Int {
    return this.indexOfArray(0, content, start, end)
}

@JvmOverloads
fun BooleanArray.indexOfArray(content: BooleanArray, start: Int = 0, end: Int = content.size): Int {
    return this.indexOfArray(0, content, start, end)
}

@JvmOverloads
fun ByteArray.indexOfArray(content: ByteArray, start: Int = 0, end: Int = content.size): Int {
    return this.indexOfArray(0, content, start, end)
}

@JvmOverloads
fun ShortArray.indexOfArray(content: ShortArray, start: Int = 0, end: Int = content.size): Int {
    return this.indexOfArray(0, content, start, end)
}

@JvmOverloads
fun CharArray.indexOfArray(content: CharArray, start: Int = 0, end: Int = content.size): Int {
    return this.indexOfArray(0, content, start, end)
}

@JvmOverloads
fun IntArray.indexOfArray(content: IntArray, start: Int = 0, end: Int = content.size): Int {
    return this.indexOfArray(0, content, start, end)
}

@JvmOverloads
fun LongArray.indexOfArray(content: LongArray, start: Int = 0, end: Int = content.size): Int {
    return this.indexOfArray(0, content, start, end)
}

@JvmOverloads
fun FloatArray.indexOfArray(content: FloatArray, start: Int = 0, end: Int = content.size): Int {
    return this.indexOfArray(0, content, start, end)
}

@JvmOverloads
fun DoubleArray.indexOfArray(content: DoubleArray, start: Int = 0, end: Int = content.size): Int {
    return this.indexOfArray(0, content, start, end)
}

@JvmOverloads
fun <T> Array<T>.indexOfArray(offset: Int, content: Array<T>, start: Int = 0, end: Int = content.size): Int {
    var i = offset
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun BooleanArray.indexOfArray(offset: Int, content: BooleanArray, start: Int = 0, end: Int = content.size): Int {
    var i = offset
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun ByteArray.indexOfArray(offset: Int, content: ByteArray, start: Int = 0, end: Int = content.size): Int {
    var i = offset
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun ShortArray.indexOfArray(offset: Int, content: ShortArray, start: Int = 0, end: Int = content.size): Int {
    var i = offset
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun CharArray.indexOfArray(offset: Int, content: CharArray, start: Int = 0, end: Int = content.size): Int {
    var i = offset
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun IntArray.indexOfArray(offset: Int, content: IntArray, start: Int = 0, end: Int = content.size): Int {
    var i = offset
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun LongArray.indexOfArray(offset: Int, content: LongArray, start: Int = 0, end: Int = content.size): Int {
    var i = offset
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun FloatArray.indexOfArray(offset: Int, content: FloatArray, start: Int = 0, end: Int = content.size): Int {
    var i = offset
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun DoubleArray.indexOfArray(offset: Int, content: DoubleArray, start: Int = 0, end: Int = content.size): Int {
    var i = offset
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
@JvmName("joinToString")
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    transform: Function<Any?, CharSequence>? = null
): String {
    return this.arrayJoinToString0(separator = separator, transform = transform)
}

@JvmName("joinToString")
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): String {
    return this.arrayJoinToString0(
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform,
    )
}

@JvmName("joinToString")
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): String {
    return this.arrayJoinToString0(separator, prefix, suffix, limit, truncated, transform)
}

private fun Any.arrayJoinToString0(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): String {
    return when (this) {
        is Array<*> -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is BooleanArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is ByteArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is ShortArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is CharArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is IntArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is LongArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is FloatArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is DoubleArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}

@JvmOverloads
@JvmName("joinTo")
fun <A : Appendable> Any.arrayJoinTo(
    dest: A,
    separator: CharSequence = ", ",
    transform: Function<Any?, CharSequence>? = null
): A {
    return this.arrayJoinTo0(dest = dest, separator = separator, transform = transform)
}

@JvmName("joinTo")
fun <A : Appendable> Any.arrayJoinTo(
    dest: A,
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): A {
    return this.arrayJoinTo0(
        dest = dest,
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform
    )
}

@JvmName("joinTo")
fun <A : Appendable> Any.arrayJoinTo(
    dest: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): A {
    return this.arrayJoinTo0(dest, separator, prefix, suffix, limit, truncated, transform)
}

private fun <A : Appendable> Any.arrayJoinTo0(
    dest: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): A {
    return when (this) {
        is Array<*> -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is BooleanArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is ByteArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is ShortArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is CharArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is IntArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is LongArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is FloatArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        is DoubleArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}

/**
 * This class specifies a segment for an array of type [A].
 */
open class ArraySeg<A : Any> @JvmOverloads constructor(
    @get:JvmName("array") val array: A,
    @get:JvmName("offset") val offset: Int = 0,
    @get:JvmName("length") val length: Int = remainingLength(array.arrayLength(), offset),
) : FinalObject() {

    init {
        checkState(array.javaClass.isArray) { "Not an array: $array!" }
    }

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("startIndex")
    open val startIndex: Int = offset

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("endIndex")
    open val endIndex: Int = startIndex + length

    /**
     * Returns the absolute index of [array],
     * which is computed from given [index] -- a relative index of the offset of this segment.
     */
    fun absIndex(index: Int): Int {
        return offset + index
    }

    /**
     * Returns the copy of array range which is specified by this segment.
     */
    fun copyOfRange(): A {
        return array.arrayCopyOfRannge(startIndex, endIndex)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ArraySeg<*>
        if (array != other.array) return false
        if (offset != other.offset) return false
        if (length != other.length) return false
        return true
    }

    override fun hashCode0(): Int {
        return toString().hashCode()
    }

    override fun toString0(): String {
        return "ArraySeg[$array, $offset, $length]"
    }
}