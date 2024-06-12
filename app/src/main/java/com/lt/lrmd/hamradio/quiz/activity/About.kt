package com.lt.lrmd.hamradio.quiz.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lt.lrmd.hamradio.quiz.R
import android.widget.TextView
import android.text.method.ScrollingMovementMethod
import android.text.Html
import android.view.View

class About : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_activity)
        val tv = findViewById<View>(R.id.about_activity_html_text_view) as TextView
        tv.movementMethod = ScrollingMovementMethod()
        tv.text = Html.fromHtml(getString(R.string.about_activity_html_source))
    }
}