package com.lt.lrmd.hamradio.quiz.model

import android.content.Context
import kotlin.Throws
import android.util.Xml
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.IOException
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.util.ArrayList

class XmlLoader(private val context: Context) {
    interface XmlLoadedListener {
        fun onXmlLoaded()
    }

    private class CategoryInfo {
        var id: Long = 0
        var title: String? = null
        var text: String? = null
        var icon: String? = null
        var mode = 0
        var layout = 0
    }

    private class QuestionInfo {
        var text: String? = null
        var audio: String? = null
        var image: String? = null
        var choices = ArrayList<String>()
        var answer = 0
    }

    private val dataSource: DataSource
    @Throws(IOException::class, SAXException::class)
    fun load(fileName: String?) {
        dataSource.reset()
        val `in` = context.assets.open(fileName!!)
        Xml.parse(`in`, Xml.Encoding.UTF_8, XmlHandler())
    }

    private inner class XmlHandler : DefaultHandler() {
        private val chars = StringBuilder()
        private var category: CategoryInfo? = null
        private var question: QuestionInfo? = null
        private var questionCount = 0
        @Throws(SAXException::class)
        override fun startElement(
            uri: String, localName: String, qName: String,
            attributes: Attributes
        ) {
            // don't catch whitespace between tags
            chars.delete(0, chars.length)
            if ("category" == localName) {
                category = CategoryInfo()
                category!!.id = dataSource.addCategory()
            } else if ("question" == localName) {
                question = QuestionInfo()
            }
        }

        @Throws(SAXException::class)
        override fun characters(ch: CharArray, start: Int, length: Int) {
            chars.append(ch, start, length)
        }

        @Throws(SAXException::class)
        override fun endElement(uri: String, localName: String, qName: String) {
            if ("category" == localName) {
                finishCategory()
                category = null
            } else if ("question" == localName) {
                finishQuestion()
            } else if ("title" == localName) {
                category!!.title = getChars()
            } else if ("text" == localName) {
                if (question != null) {
                    question!!.text = getChars()
                } else {
                    category!!.text = getChars()
                }
            } else if ("image" == localName) {
                question!!.image = getChars()
            } else if ("audio" == localName) {
                question!!.audio = getChars()
            } else if ("choice" == localName) {
                question!!.choices.add(getChars())
            } else if ("answer" == localName) {
                question!!.answer = parseAnswer()
            }
        }

        @Throws(SAXException::class)
        private fun parseAnswer(): Int {
            val ch = getChars()
            return try {
                Integer.valueOf(ch)
            } catch (e: NumberFormatException) {
                throw throwException("invalid answer index $ch")
            }
        }

        @Throws(SAXException::class)
        private fun finishQuestion() {
            if (question!!.choices.size == 0) throwException("must have at least one choice")
            if (question!!.answer < 0
                || question!!.answer >= question!!.choices.size
            ) throwException("invalid answer index")
            if (question!!.text == null && question!!.image == null && question!!.audio == null) throwException(
                "question must contain at least one of text, image, and audio tags"
            )
            dataSource.addQuestion(
                category!!.id, question!!.text, question!!.image,
                question!!.audio, question!!.choices, question!!.answer
            )
            question = null
            questionCount++
        }

        @Throws(SAXException::class)
        private fun finishCategory() {
            if (category!!.title == null) throwException("missing category title")
            dataSource.updateCategory(
                category!!.id, category!!.title,
                category!!.text, "", 0
            )
            category = null
        }

        @Throws(SAXException::class)
        private fun throwException(message: String): SAXException {
            throw SAXException(
                "error while parsing question "
                        + questionCount + " in category "
                        + (if (category == null) "??" else category!!.id) + ": " + message
            )
        }

        private fun getChars(): String {
            val ch = chars.toString().trim { it <= ' ' }
            chars.delete(0, chars.length)
            return ch
        }
    }

    companion object {
        private const val TAG = "QuizXml"
    }

    init {
        dataSource = DataSource(context)
    }
}