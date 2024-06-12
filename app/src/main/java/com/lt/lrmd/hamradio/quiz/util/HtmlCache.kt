package com.lt.lrmd.hamradio.quiz.util

import android.text.Spanned
import android.text.Html
import java.util.HashMap

class HtmlCache {
    private val mCache = HashMap<String, Spanned>()
    fun getHtml(rawHtml: String?): Spanned? {
        if (rawHtml == null) return null
        var html = mCache[rawHtml]
        if (html == null) {
            mCache[rawHtml] = Html.fromHtml(rawHtml).also { html = it }
        }
        return html
    }
}