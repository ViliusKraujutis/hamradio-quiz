package com.lt.lrmd.hamradio.quiz.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView
import android.text.Spanned
import android.widget.TextView.BufferType
import android.text.Html
import android.text.Html.ImageGetter
import android.util.AttributeSet
import android.util.Log
import java.lang.Exception
import java.util.HashMap

class HtmlTextView : TextView {
    // we remember the last value passed to set text
    // to avoid multiple calls to Html.fromHtml
    private val mHtmlCache = HashMap<String, Spanned?>()

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    override fun setText(text: CharSequence, type: BufferType) {
        var text: CharSequence? = text
        if (text is String) {
            val textString = text
            var cached = mHtmlCache[textString]
            if (cached == null) {
                cached = Html.fromHtml(textString, mImageGetter, null)
                mHtmlCache[textString] = cached
            }
            linksClickable = true
            text = cached
        }
        super.setText(text, type)
    }

    private val mImageGetter = ImageGetter { source ->
        try {
            val `in` = context.assets.open(source)
            val d = Drawable.createFromStream(`in`, source)
            d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
            return@ImageGetter d
        } catch (e: Exception) {
            Log.e(
                "HtmlTextView\$ImageGetter",
                "error loading image: $source", e
            )
            return@ImageGetter null
        }
    }
}