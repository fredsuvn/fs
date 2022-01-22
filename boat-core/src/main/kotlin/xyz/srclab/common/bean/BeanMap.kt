package xyz.srclab.common.bean

import xyz.srclab.common.collect.mapEntries

/**
 * A [Map] which is associated with a `bean`,
 * of which keys are properties' names, values are properties' value.
 *
 * Note:
 *
 * * For this map and origin bean, any modification will reflect each other;
 */
open class BeanMap(
    open val bean: Any,
    open val beanType: BeanType = bean::class.java.resolveBean()
) : AbstractMutableMap<String, Any?>() {

    //private val properties: Map<String, PropertyType> =
    //    beanType.properties.filter { it.key != "class" }

    private val entryMap: Map<String, MutableMap.MutableEntry<String, Any?>> =
        beanType.properties.mapEntries { name, propertyType -> name to BeanEntry(propertyType) }

    override val size: Int
        get() = entries.size

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>> = LinkedHashSet(entryMap.values)

    override fun containsKey(key: String): Boolean {
        return entryMap.containsKey(key)
    }

    override fun get(key: String): Any? {
        val entry = entryMap[key]
        if (entry === null) {
            return null
        }
        return entry.value
    }

    override fun isEmpty(): Boolean {
        return entryMap.isEmpty()
    }

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun put(key: String, value: Any?): Any? {
        val entry = entryMap[key]
        if (entry === null) {
            throw UnsupportedOperationException("Property $key not found.")
        }
        return entry.setValue(value)
    }

    override fun remove(key: String): Any? {
        throw UnsupportedOperationException()
    }

    private inner class BeanEntry(
        private val propertyType: PropertyType
    ) : MutableMap.MutableEntry<String, Any?> {

        override val key: String = propertyType.name

        override val value: Any?
            get() {
                return propertyType.getValue(bean)
            }

        override fun setValue(newValue: Any?): Any? {
            val old = propertyType.getValue(bean)
            propertyType.setValue(bean, newValue)
            return old
        }
    }
}