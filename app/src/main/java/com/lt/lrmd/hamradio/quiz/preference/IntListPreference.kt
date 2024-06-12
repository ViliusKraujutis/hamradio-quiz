package com.lt.lrmd.hamradio.quiz.preference

import android.content.Context
import android.preference.ListPreference
import android.util.AttributeSet

class IntListPreference : ListPreference {
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun persistString(value: String): Boolean {
        return persistInt(Integer.valueOf(value))
    }

    override fun getPersistedString(defaultReturnValue: String): String {
        return getPersistedInt(-1).toString()
    }
}