package com.lt.lrmd.hamradio.quiz.util

class Task(private val mRunnable: Runnable) : Runnable {
    private var mCancelled = false
    fun cancel() {
        mCancelled = true
    }

    override fun run() {
        if (!mCancelled) {
            mRunnable.run()
        }
    }
}