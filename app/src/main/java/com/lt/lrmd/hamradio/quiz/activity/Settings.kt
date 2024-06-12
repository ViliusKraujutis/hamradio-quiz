package com.lt.lrmd.hamradio.quiz.activity

import android.preference.PreferenceActivity
import android.os.Bundle
import com.lt.lrmd.hamradio.quiz.R

class Settings : PreferenceActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }
}