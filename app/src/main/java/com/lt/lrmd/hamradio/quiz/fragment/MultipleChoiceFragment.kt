package com.lt.lrmd.hamradio.quiz.fragment

import com.lt.lrmd.hamradio.quiz.fragment.QuestionFragment
import com.lt.lrmd.hamradio.quiz.R
import android.os.Bundle
import android.view.View
import com.lt.lrmd.hamradio.quiz.fragment.MultipleChoiceFragment
import com.lt.lrmd.hamradio.quiz.fragment.MultipleChoiceFragment.ButtonListener
import com.lt.lrmd.hamradio.quiz.model.Question
import com.lt.lrmd.hamradio.quiz.view.QuizButton

class MultipleChoiceFragment : QuestionFragment() {
    override fun getLayoutResourceId(): Int {
        return if (mApp.config.fixedLayout()) R.layout.multiple_choice_fragment_fixed_buttons else R.layout.multiple_choice_fragment_scroll_buttons
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (i in sAnswerButtonIds.indices) view.findViewById<View>(
            sAnswerButtonIds[i]
        ).setOnClickListener(ButtonListener(i))
    }

    override fun onAnswerChanged() {
        super.onAnswerChanged()
        val view = view ?: return
        val question = mQuestion ?: return
        for (i in sAnswerButtonIds.indices) {
            val btn = view.findViewById(sAnswerButtonIds[i]) as QuizButton
            val showingAnswer = mAnswer != NO_ANSWER
            if (i < question.choices.size) {
                btn.setVisibility(View.VISIBLE)
                btn.isShowingAnswer = showingAnswer
                btn.isCorrect = i == question.answer
                btn.setText(mApp.htmlCache.getHtml(question.choices[i]))
                btn.setClickable(!showingAnswer)
            } else {
                btn.setVisibility(View.GONE)
            }
        }
    }

    private inner class ButtonListener(private val mIndex: Int) : View.OnClickListener {
        override fun onClick(v: View) {
            setAnswer(mIndex)
        }
    }

    companion object {
        private val sAnswerButtonIds = intArrayOf(
            R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4
        )

        fun newInstance(question: Question?, isLastQuestion: Boolean): MultipleChoiceFragment {
            val fragment = MultipleChoiceFragment()
            fragment.arguments = newArguments(question, isLastQuestion)
            return fragment
        }
    }
}