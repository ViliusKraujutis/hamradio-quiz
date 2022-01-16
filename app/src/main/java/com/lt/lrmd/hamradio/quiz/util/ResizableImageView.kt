package com.lt.lrmd.hamradio.quiz.util

import android.content.Context
import android.util.AttributeSet
import android.view.View.MeasureSpec
import android.widget.ImageView

/**
 * Argument check helpers
 */
class ResizableImageView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = drawable
        if (d != null) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = Math.ceil(
                (width.toFloat() * d.intrinsicHeight.toFloat() / d.intrinsicWidth
                    .toFloat()).toDouble()
            ).toInt()
            setMeasuredDimension(width, height / 2)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}