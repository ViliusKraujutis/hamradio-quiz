package com.lt.lrmd.hamradio.quiz.fragment

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.inject.Inject

abstract class QuestionFragment : Fragment() {
    /**
     * Must be implemented by the parent activity.
     */
    interface QuestionListener {
        fun onNextQuestion()
        fun onQuestionAnswered(answer: Int)
    }

    @Inject
    protected var mApp: App? = null

    @InjectView(R.id.questionText)
    protected var mQuestionText: TextView? = null

    @InjectView(R.id.questionImage)
    protected var mQuestionImage: ImageView? = null

    @InjectView(R.id.next)
    protected var mNextButton: Button? = null
    protected var mQuestion: Question? = null
    protected var mLastQuestion = false
    protected var mAnswer = NO_ANSWER
    protected var mAnswered = false

    /** implemented to provide layouts for subclasses  */
    protected abstract val layoutResourceId: Int
    protected val questionListener: QuestionListener?
        protected get() = activity as QuestionListener?

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // don't do anything here because injection hasn't happened yet
        return inflater.inflate(layoutResourceId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mQuestion = arguments!!.getParcelable(QUESTION_KEY)
        mLastQuestion = arguments!!.getBoolean(LAST_QUESTION_KEY, false)
        if (savedInstanceState != null) {
            mAnswer = savedInstanceState.getInt(ANSWER_KEY, NO_ANSWER)
            mAnswered = savedInstanceState.getBoolean(ANSWERED_KEY)
        } else {
            mAnswer = NO_ANSWER
            mAnswered = false
        }
        mQuestionText.setText(mApp.getHtmlCache().getHtml(mQuestion.getText()))
        mQuestionText.setTextSize(mApp.getConfig().fontSize().toFloat())
        val d: Drawable = mApp.getAssetCache().getDrawable(mQuestion.getImage())
        if (d != null) {
            mQuestionImage!!.visibility = View.VISIBLE
            mQuestionImage!!.setImageDrawable(d)
        } else {
            mQuestionImage!!.visibility = View.GONE
        }
        mNextButton!!.setOnClickListener { questionListener!!.onNextQuestion() }
        mNextButton!!.text = if (mLastQuestion) "Pabaiga" else "Kitas klausimas"
        onAnswerChanged()
    }

    protected open val isNextQuestionButtonEnabled: Boolean
        protected get() = mAnswer != NO_ANSWER

    protected open fun onAnswerChanged() {
        mNextButton!!.isEnabled = isNextQuestionButtonEnabled
        if (mAnswer != NO_ANSWER && !mAnswered) {
            mAnswered = true
            questionListener!!.onQuestionAnswered(mAnswer)
        }
    }

    fun setAnswer(answer: Int) {
        if (answer != mAnswer) {
            mAnswer = answer
            onAnswerChanged()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ANSWER_KEY, mAnswer)
        outState.putBoolean(ANSWERED_KEY, mAnswered)
    }

    companion object {
        var NO_ANSWER = -1
        const val QUESTION_KEY = "question"
        const val ANSWER_KEY = "answer"
        const val ANSWERED_KEY = "answered"
        const val LAST_QUESTION_KEY = "lastQuestion"
        fun newArguments(question: Question?, lastQuestion: Boolean): Bundle {
            val b = Bundle()
            b.putParcelable(QUESTION_KEY, question)
            b.putBoolean(LAST_QUESTION_KEY, lastQuestion)
            return b
        }
    }
}