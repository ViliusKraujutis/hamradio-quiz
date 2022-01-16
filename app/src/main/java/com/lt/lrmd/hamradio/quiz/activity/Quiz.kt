package com.lt.lrmd.hamradio.quiz.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.lt.lrmd.hamradio.quiz.Config
import com.lt.lrmd.hamradio.quiz.model.DataSource
import com.lt.lrmd.hamradio.quiz.util.Task
import java.io.IOException
import java.util.*

class Quiz : FragmentActivity(), QuestionListener {
    @Inject
    private val mConfig: Config? = null

    @Inject
    private val mApp: App? = null

    @Inject
    private val mDataSource: DataSource? = null

    @InjectView(R.id.progress)
    private val mProgressTextView: TextView? = null

    @InjectView(R.id.fragmentContainer)
    private val mFragmentContainer: ViewGroup? = null

    @InjectView(R.id.messages)
    private val mSoundWarning: View? = null

    @InjectView(R.id.score)
    private val mScoreTextView: TextView? = null

    @InjectExtra(CATEGORY_ID_EXTRA)
    private val mCategoryId: Long = 0

    @InjectExtra(MODE_EXTRA)
    private val mMode = 0
    private var mQuestions: Array<Question?>
    private var mQuestionIndex = 0
    private var mSeed: Long = 0
    private var mNumAnswered = 0
    private var mNumCorrect = 0
    private var mNextQuestionTask: Task? = null
    private val mHandler = Handler()
    private var mSoundPool: SoundPoolAssistant? = null
    private var mHighScore = false
    private var mScore = 0f
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quiz)
        if (savedInstanceState != null) {
            mQuestionIndex = savedInstanceState.getInt(QUESTION_INDEX_KEY)
            mSeed = savedInstanceState.getLong(SEED_KEY)
            mNumAnswered = savedInstanceState.getInt(NUM_ANSWERED_KEY)
            mNumCorrect = savedInstanceState.getInt(NUM_CORRECT_KEY)
        } else {
            mSeed = System.currentTimeMillis()
        }
        if (mMode == Config.MODE_FLASHCARD) {
            mScoreTextView.setVisibility(View.GONE)
        } else {
            mScoreTextView.setVisibility(View.VISIBLE)
        }
        selectQuestions()
        loadAudio()
        updateProgress()
        updateScore()
        showCurrentQuestion()
    }

    protected override fun onDestroy() {
        super.onDestroy()
        // make sure we don't try to show the next question after we're dead
        if (mNextQuestionTask != null) {
            mNextQuestionTask!!.cancel()
        }
    }

    private fun loadAudio() {
        mSoundPool = SoundPoolAssistant(this, 1, AudioManager.STREAM_MUSIC)
        mSoundPool.load(R.raw.correct)
        mSoundPool.load(R.raw.incorrect)
        for (q in mQuestions) {
            if (q.getAudio() != null) {
                try {
                    mSoundPool.load(q.getAudio())
                } catch (e: IOException) {
                    Log.e("Quiz", "error loading sound " + q.getAudio())
                }
            }
        }
    }

    protected override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(QUESTION_INDEX_KEY, mQuestionIndex)
        outState.putLong(SEED_KEY, mSeed)
    }

    private fun selectQuestions() {
        var maxQuestions = mConfig!!.numQuestions()
        val cursor = mDataSource!!.queryQuestions(mCategoryId)
        cursor.moveToFirst()
        val totalQuestions = cursor.count
        if (maxQuestions == 0 || maxQuestions > totalQuestions) {
            maxQuestions = totalQuestions
        }

        // This is from Knuth 3.4.2, algorithm S. It selects
        // each question with equal probability, and surprisingly,
        // the cursor never runs off the end.
        var t = 0 // record index
        var m = 0 // records selected so far
        val r = Random(mSeed)
        mQuestions = arrayOfNulls<Question>(maxQuestions)
        while (m < maxQuestions) {
            if (r.nextFloat() * (totalQuestions - t) < maxQuestions - m) {
                mQuestions[m++] = Question(cursor)
            }
            t++
            cursor.moveToNext()
        }
        // Give the questions a shuffle
        for (i in 0 until maxQuestions) {
            val j = r.nextInt(maxQuestions)
            val tmp: Question? = mQuestions[i]
            mQuestions[i] = mQuestions[j]
            mQuestions[j] = tmp
        }
    }

    private fun updateProgress() {
        mProgressTextView.setText(progressText)
    }

    private fun updateScore() {
        mScoreTextView.setText(scoreText)
    }

    private val progressText: String
        private get() = String.format(
            "Klausimas %d iš %d", mQuestionIndex + 1,
            mQuestions.size
        )
    private val scoreText: String?
        private get() {
            if (mNumAnswered == 0) return null
            val pct = (100.0f * mNumCorrect / mNumAnswered).toInt()
            return String.format(
                "%d iš %d teisingai (%d%%)", mNumCorrect,
                mNumAnswered, pct
            )
        }
    private val completeDialogTitle: CharSequence
        private get() = if (mConfig!!.highScores() && mHighScore) {
            getText(R.string.complete_high_score)
        } else getText(R.string.complete)
    private val completeDialogMessage: CharSequence
        private get() {
            val pct = (100 * mScore).toInt()
            return getResources().getString(
                R.string.complete_score_format,
                mNumCorrect, mQuestions.size, pct
            )
        }

    private fun showCurrentQuestion() {
        val fragment: Fragment = createFragment()
        val transaction: FragmentTransaction = getSupportFragmentManager()
            .beginTransaction()
        if (mFragmentContainer.getChildCount() != 0) {
            transaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left
            ).replace(
                R.id.fragmentContainer,
                fragment
            )
        } else {
            transaction.add(R.id.fragmentContainer, fragment)
        }
        transaction.commit()
        mSoundWarning!!.visibility = View.GONE
        val q: Question? = mQuestions[mQuestionIndex]
        if (q.getAudio() != null) {
            if (mSoundPool.isSoundEnabled() && mConfig!!.soundEnabled()) {

                // use a little delay to let the question appear
                mHandler.postDelayed({ mSoundPool.play(q.getAudio()) }, 500)
            } else {
                mSoundWarning.visibility = View.VISIBLE
            }
        }
    }

    private fun createFragment(): Fragment {
        val question: Question? = mQuestions[mQuestionIndex]
        val last = mQuestionIndex + 1 == mQuestions.size
        return if (mMode == Config.MODE_FLASHCARD) FlashcardFragment
            .newInstance(question, last) else MultipleChoiceFragment
            .newInstance(question, last)
    }

    override fun onNextQuestion() {
        cancelNextQuestionTask()
        if (mQuestionIndex + 1 >= mQuestions.size) {
            finishQuiz()
        } else {
            mQuestionIndex++
            showCurrentQuestion()
        }
        updateProgress()
    }

    private fun finishQuiz() {
        // in flashcard mode we just go home
        if (mMode == Config.MODE_FLASHCARD) {
            finish()
            return
        }
        mScore = mNumCorrect.toFloat() / mQuestions.size.toFloat()
        mHighScore = mDataSource!!.saveScore(mCategoryId, mScore)

        // in normal mode we show the user her score
        AlertDialog.Builder(this, R.style.AlertDialog)
            .setTitle(completeDialogTitle)
            .setMessage(completeDialogMessage)
            .setCancelable(false)
            .setPositiveButton(R.string.try_again,
                DialogInterface.OnClickListener { dialog, which -> // start a new quiz activity, then finish
                    startActivity(
                        Intent(this@Quiz, Quiz::class.java)
                            .putExtra(
                                CATEGORY_ID_EXTRA,
                                mCategoryId
                            ).putExtra(
                                MODE_EXTRA, mMode
                            )
                    )
                    finish()
                })
            .setNegativeButton(R.string.home,
                DialogInterface.OnClickListener { dialog, which -> // just finish
                    finish()
                })
            .create()
            .show()
    }

    private fun cancelNextQuestionTask() {
        if (mNextQuestionTask != null) {
            mNextQuestionTask!!.cancel()
            mNextQuestionTask = null
        }
    }

    override fun onQuestionAnswered(answer: Int) {
        mNumAnswered++
        val correct = mQuestions[mQuestionIndex].getAnswer() == answer
        if (correct) {
            mNumCorrect++
        }
        mScoreTextView.setText(scoreText)
        if (mConfig!!.soundEnabled()) {
            mSoundPool.play(if (correct) R.raw.correct else R.raw.incorrect)
        }
        if (mConfig.autoNext()) {
            cancelNextQuestionTask()
            mHandler.postDelayed(Task { onNextQuestion() }
                .also { mNextQuestionTask = it }, mConfig.autoNextDelay().toLong())
        }
    }

    companion object {
        const val CATEGORY_ID_EXTRA = "categoryId"
        const val MODE_EXTRA = "mode"
        private const val QUESTION_INDEX_KEY = "questionIndex"
        private const val SEED_KEY = "seed"
        private const val NUM_ANSWERED_KEY = "numAnswered"
        private const val NUM_CORRECT_KEY = "numCorrect"
    }
}