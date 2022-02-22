package com.lt.lrmd.hamradio.quiz.view

import android.content.Context
import kotlin.jvm.JvmOverloads
import com.lt.lrmd.hamradio.quiz.R
import com.lt.lrmd.hamradio.quiz.view.QuizButton
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

/**
 * This button has two extra pieces of drawable state: correct: is this button
 * the one for the correct answer? showingAnswer: are we showing the answer?
 */
class QuizButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.style.Button
) : AppCompatButton(context, attrs, defStyle) {
    private var mCorrect: Boolean
    private var mShowingAnswer: Boolean
    var isCorrect: Boolean
        get() = mCorrect
        set(correct) {
            if (correct != mCorrect) {
                mCorrect = correct
                refreshDrawableState()
            }
        }
    var isShowingAnswer: Boolean
        get() = mShowingAnswer
        set(showingAnswer) {
            if (showingAnswer != mShowingAnswer) {
                mShowingAnswer = showingAnswer
                refreshDrawableState()
            }
        }

    protected override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState: IntArray = super.onCreateDrawableState(extraSpace + 2)
        if (isCorrect) {
            mergeDrawableStates(drawableState, STATE_CORRECT)
        }
        if (isShowingAnswer) {
            mergeDrawableStates(drawableState, STATE_SHOWING_ANSWER)
        }
        return drawableState
    }

    companion object {
        private val STATE_SHOWING_ANSWER = intArrayOf(R.attr.state_showing_answer)
        private val STATE_CORRECT = intArrayOf(R.attr.state_correct)
    }

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.QuizButton, defStyle, 0
        )
        mCorrect = a.getBoolean(R.styleable.QuizButton_correct, true)
        mShowingAnswer = a.getBoolean(R.styleable.QuizButton_showing_answer, false)
    }
}