package com.lt.lrmd.hamradio.quiz.fragment

import com.lt.lrmd.hamradio.quiz.fragment.QuestionFragment
import com.lt.lrmd.hamradio.quiz.R
import android.widget.TextView
import android.widget.ViewFlipper
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.lt.lrmd.hamradio.quiz.fragment.FlashcardFragment
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection
import com.tekle.oss.android.animation.AnimationFactory
import com.lt.lrmd.hamradio.quiz.model.Question

class FlashcardFragment : QuestionFragment() {
    @InjectView(R.id.answerText)
    private val mAnswerText: TextView? = null

    @InjectView(R.id.flipper)
    private val mViewFlipper: ViewFlipper? = null

    @InjectView(R.id.buttons)
    private val mButtons: View? = null

    @InjectView(R.id.show)
    private val mShowButton: Button? = null
    private var mShowingAnswer = false
    override fun getLayoutResourceId(): Int {
        return R.layout.flashcard_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAnswerText!!.text = mApp.htmlCache.getHtml(
            mQuestion.choices[mQuestion.answer]
        )
        mAnswerText.textSize = mApp.config.fontSize().toFloat()
        mShowButton!!.setOnClickListener { toggleShowingAnswer() }
        if (savedInstanceState != null) {
            mShowingAnswer = savedInstanceState.getBoolean(SHOWING_ANSWER_KEY)
        }
        if (mShowingAnswer) {
            mViewFlipper!!.displayedChild = ANSWER_CHILD_INDEX
        } else {
            mViewFlipper!!.displayedChild = QUESTION_CHILD_INDEX
        }
    }

    private fun toggleShowingAnswer() {
        val direction = if (mShowingAnswer) ANSWER_TO_QUESTION_DIR else QUESTION_TO_ANSWER_DIR
        AnimationFactory.flipTransition(mViewFlipper, direction)
        mShowButton!!.setText(if (mShowingAnswer) R.string.show_question else R.string.show_answer)
        mShowingAnswer = !mShowingAnswer
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SHOWING_ANSWER_KEY, mShowingAnswer)
    }

    override fun isNextQuestionButtonEnabled(): Boolean {
        return true
    }

    companion object {
        private const val ANSWER_CHILD_INDEX = 1
        private const val QUESTION_CHILD_INDEX = 0
        private val QUESTION_TO_ANSWER_DIR = FlipDirection.LEFT_RIGHT
        private val ANSWER_TO_QUESTION_DIR = FlipDirection.RIGHT_LEFT
        private const val SHOWING_ANSWER_KEY = "showingAnswer"
        fun newInstance(question: Question?, isLastQuestion: Boolean): FlashcardFragment {
            val fragment = FlashcardFragment()
            fragment.arguments = newArguments(question, isLastQuestion)
            return fragment
        }
    }
}