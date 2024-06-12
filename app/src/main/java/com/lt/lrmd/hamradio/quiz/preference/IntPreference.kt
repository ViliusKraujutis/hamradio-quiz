package com.lt.lrmd.hamradio.quiz.preference

import android.content.Context
import android.preference.EditTextPreference
import android.text.InputType
import android.util.AttributeSet

class IntPreference : EditTextPreference {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        configureEditText()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        configureEditText()
    }

    private fun configureEditText() {
        editText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    override fun getPersistedString(defaultReturnValue: String): String {
        return getPersistedInt(-1).toString()
    }

    override fun persistString(value: String): Boolean {
        persistInt(Integer.valueOf(value))
        return true
    }
}