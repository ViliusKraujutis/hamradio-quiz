package jm.util

import kotlin.Throws
import jm.util.XmlException
import jm.util.SimpleSaxParser
import jm.util.SimpleSaxParser.StartTagHandler
import jm.util.SimpleSaxParser.EndTagHandler
import android.util.Xml
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.StringBuilder
import java.util.HashMap

class SimpleSaxParser {
    interface StartTagHandler {
        @Throws(XmlException::class)
        fun handleStartTag(
            parser: SimpleSaxParser?, name: String?,
            attrs: Attributes?
        )
    }

    interface EndTagHandler {
        @Throws(XmlException::class)
        fun handleEndTag(parser: SimpleSaxParser?, name: String?)
    }

    private val buffer = StringBuilder()
    private val startTagHandlers: MutableMap<String, StartTagHandler?> = HashMap()
    private val endTagHandlers: MutableMap<String, EndTagHandler?> = HashMap()
    private var defaultStartTagHandler = dummyStartTagHandler
    private var defaultEndTagHandler = dummyEndTagHandler
    fun setStartTagHandler(name: String?, handler: StartTagHandler?) {
        var handler = handler
        if (name == null) throw NullPointerException("name")
        if (handler == null) handler = dummyStartTagHandler
        startTagHandlers[name] = handler
    }

    fun setEndTagHandler(name: String?, handler: EndTagHandler?) {
        var handler = handler
        if (name == null) throw NullPointerException("name")
        if (handler == null) handler = dummyEndTagHandler
        endTagHandlers[name] = handler
    }

    fun setDefaultStartTagHandler(handler: StartTagHandler?) {
        defaultStartTagHandler = handler ?: dummyStartTagHandler
    }

    fun setDefaultEndTagHandler(handler: EndTagHandler?) {
        defaultEndTagHandler = handler ?: dummyEndTagHandler
    }

    fun getBuffer(): String {
        return buffer.toString().trim { it <= ' ' }
    }

    fun takeBuffer(): String {
        val s = getBuffer()
        clearBuffer()
        return s
    }

    fun clearBuffer() {
        buffer.delete(0, buffer.length)
    }

    @Throws(XmlException::class)
    fun parse(source: Reader?) {
        try {
            Xml.parse(source, Handler())
        } catch (e: SAXException) {
            throw unwindException(e)
        } catch (e: IOException) {
            throw XmlException(e)
        }
    }

    @Throws(XmlException::class)
    fun parse(`in`: InputStream?) {
        try {
            Xml.parse(`in`, null, Handler())
        } catch (e: SAXException) {
            throw unwindException(e)
        } catch (e: IOException) {
            throw XmlException(e)
        }
    }

    private fun getStartTagHandler(name: String): StartTagHandler? {
        var h = startTagHandlers[name]
        if (h == null) h = defaultStartTagHandler
        return h
    }

    private fun getEndTagHandler(name: String): EndTagHandler? {
        var h = endTagHandlers[name]
        if (h == null) h = defaultEndTagHandler
        return h
    }

    private inner class Handler : DefaultHandler() {
        @Throws(SAXException::class)
        override fun characters(ch: CharArray, start: Int, length: Int) {
            buffer.append(ch, start, length)
        }

        @Throws(SAXException::class)
        override fun startElement(
            uri: String, localName: String, qName: String,
            attributes: Attributes
        ) {
            try {
                getStartTagHandler(localName)!!.handleStartTag(
                    this@SimpleSaxParser,
                    localName, attributes
                )
            } catch (e: XmlException) {
                throw SAXException(e)
            }
        }

        @Throws(SAXException::class)
        override fun endElement(uri: String, localName: String, qName: String) {
            try {
                getEndTagHandler(localName)!!.handleEndTag(this@SimpleSaxParser, localName)
            } catch (e: XmlException) {
                throw SAXException(e)
            }
        }
    }

    companion object {
        private val dummyStartTagHandler: StartTagHandler = object : StartTagHandler {
            override fun handleStartTag(
                parser: SimpleSaxParser?, name: String?,
                attrs: Attributes?
            ) { /* empty */
            }
        }
        private val dummyEndTagHandler: EndTagHandler = object : EndTagHandler {
            override fun handleEndTag(parser: SimpleSaxParser?, name: String?) { /* empty */
            }
        }

        private fun unwindException(e: Exception): XmlException {
            var found: XmlException? = null
            var current: Throwable? = e
            while (current != null) {
                if (current is XmlException) {
                    found = current
                    break
                }
                if (current.cause == null || current === current.cause) {
                    break
                }
                current = current.cause
            }
            return found ?: XmlException(e)
        }
    }
}