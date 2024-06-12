package com.lt.lrmd.hamradio.quiz.util

import android.content.Context
import android.graphics.drawable.Drawable
import java.io.IOException
import java.util.HashMap

class AssetCache(private val mContext: Context) {
    private val mDrawables = HashMap<String, Drawable>()
    fun getDrawable(path: String?): Drawable? {
        if (path == null) return null
        var drawable = mDrawables[path]
        if (drawable == null) {
            drawable = loadDrawable(path)
            if (drawable != null) {
                mDrawables[path] = drawable
            }
        }
        return drawable
    }

    private fun loadDrawable(path: String): Drawable? {
        return try {
            val `in` = mContext.assets.open(path)
            Drawable.createFromStream(`in`, path)
        } catch (e: IOException) {
            error("unable to load drawable from $path: $e")
            null
        }
    }

    private fun error(error: String) {}
}