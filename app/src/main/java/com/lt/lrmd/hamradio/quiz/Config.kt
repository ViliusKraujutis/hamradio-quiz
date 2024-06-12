package com.lt.lrmd.hamradio.quiz

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.io.File

//@Singleton
//@Inject
class Config constructor(private val mContext: Context) {
    private fun setDefaultPrefs() {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        val prefsTime: Long = prefs.getLong("__mtime", 0)
        val apkTime = File(mContext.packageCodePath).lastModified()
        if (apkTime <= prefsTime) {
            return
        }
        val editor: SharedPreferences.Editor = prefs.edit()
        var key = getString(R.string.key_soundEnabled)
        editor.putBoolean(key, getBool(R.bool.config_defaultSoundEnabled))
        key = getString(R.string.key_quizLength)
        editor.putString(key, Integer.toString(getInt(R.integer.config_defaultNumQuestions)))
        key = getString(R.string.key_autoNextQuestion)
        editor.putBoolean(key, getBool(R.bool.config_autoNext))
        editor.putLong("__mtime", apkTime)
        editor.commit()
    }

    fun questionsFile(): String {
        return getString(R.string.config_questionsFile)
    }

    fun highScores(): Boolean {
        return getBool(R.bool.config_highScores)
    }

    fun flashcardMode(): Boolean {
        return getBool(R.bool.config_flashcardMode)
    }

    fun fixedLayout(): Boolean {
        return getBool(R.bool.config_fixedLayout)
    }

    fun autoNext(): Boolean {
        return getBool(R.bool.config_autoNext)
    }

    fun autoNextDelay(): Int {
        return getInt(R.integer.config_autoNextDelay)
    }

    fun fontSize(): Int {
        return getInt(R.integer.config_fontSize)
    }

    fun soundEnabled(): Boolean {
        return prefs.getBoolean(getString(R.string.key_soundEnabled), true)
    }

    fun numQuestions(): Int {
        return Integer.valueOf(prefs.getString(getString(R.string.key_quizLength), "15")!!)
    }

    private fun getString(resId: Int): String {
        return mContext.getString(resId)
    }

    private fun getBool(resId: Int): Boolean {
        return mContext.resources.getBoolean(resId)
    }

    private fun getInt(resId: Int): Int {
        return mContext.resources.getInteger(resId)
    }

    private val prefs: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(mContext)

    companion object {
        const val MODE_MULTIPLE_CHOICE = 0
        const val MODE_FLASHCARD = 1
        const val MODE_VARIABLE = 2
        const val NAVIGATION_BUTTONS = 0x01
        const val NAVIGATION_SWIPE = 0x02
        const val NAVIGATION_BOTH = (NAVIGATION_BUTTONS
                or NAVIGATION_SWIPE)
        const val LAYOUT_FIXED_BUTTONS = 1
        const val LAYOUT_SCROLL_BUTTONS = 2
        const val ANIMATION_NONE = 0
        const val ANIMATION_SLIDE = 1
        const val ANIMATION_FLIP = 2
    }

    init {
        setDefaultPrefs()
    }
}