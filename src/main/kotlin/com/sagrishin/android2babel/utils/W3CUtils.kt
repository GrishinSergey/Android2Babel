package com.sagrishin.android2babel.utils

import org.w3c.dom.*
import org.w3c.dom.ls.DOMImplementationLS
import org.w3c.dom.ls.LSSerializer
import javax.xml.transform.stream.StreamResult

operator fun NodeList.get(i: Int): Node? {
    return item(i)
}

operator fun NodeList.iterator() = object : Iterator<Node> {
    private var index = 0

    override fun hasNext(): Boolean {
        return index < length
    }

    override fun next(): Node {
        if (!hasNext()) throw NoSuchElementException()
        return item(index++)
    }
}

fun NodeList.asList(): Iterable<Node> {
    val res = mutableListOf<Node>()
    for (node in this) res += node
    return res
}

fun NodeList.asSequence(): Sequence<Node> {
    return (0 until this.length).asSequence().mapNotNull { this[it] }
}

fun Node.getChildrenBy(tagName: String): Sequence<Node> {
    require(this is Element) { "Node must be instance of Element to have children nodes" }
    return getChildrenBy(tagName)
}

fun Element.getChildrenBy(tagName: String): Sequence<Node> {
    return getElementsByTagName(tagName).asSequence()
}

fun Node.requireAttributes(): NamedNodeMap {
    return requireNotNull(attributes) { "Attributes must not be null for translation '${nodeValue()}'" }
}

private var _lsSerializer: LSSerializer? = null
private val Node.lsSerializer: LSSerializer
    get() {
        if (_lsSerializer == null) {
            _lsSerializer = (ownerDocument.implementation.getFeature("LS", "3.0") as DOMImplementationLS)
                .createLSSerializer()
                .apply { domConfig["xml-declaration"] = false }
        }
        return _lsSerializer!!
    }

fun Node.nodeValue(): String {
    return childNodes
        .asSequence()
        .map { if (it.hasChildNodes()) lsSerializer.writeToString(it) else it.nodeValue }
        .joinToString(separator = "")
}

operator fun DOMConfiguration.set(key: String, value: Any) {
    setParameter(key, value)
}

inline operator fun <reified T> NamedNodeMap.get(key: String): T {
    val nodeValue = getNamedItem(key)?.nodeValue.let {
        if (null is T) it else requireNotNull(it) { "Required value is missing for key '$key'" }
    }

    return when (T::class.java) {
        Char::class.java -> nodeValue?.toCharArray()?.firstOrNull() as T
        String::class.java -> nodeValue as T
        Int::class.java -> nodeValue?.toIntOrNull() as T
        Float::class.java -> nodeValue?.toFloatOrNull() as T
        Long::class.java -> nodeValue?.toLongOrNull() as T
        Boolean::class.java -> nodeValue?.toBoolean() as T
        else -> nodeValue as T
    }
}

fun Element.appendUnescaped(child: Element) {
    appendChild(ownerDocument.createProcessingInstruction(StreamResult.PI_DISABLE_OUTPUT_ESCAPING, "&"))
    appendChild(child)
    appendChild(ownerDocument.createProcessingInstruction(StreamResult.PI_ENABLE_OUTPUT_ESCAPING, "&"))
}
