package com.lt.lrmd.hamradio.quiz.util

import java.lang.IllegalArgumentException
import java.lang.RuntimeException

/**
 * Argument check helpers
 */
object Arguments {
    fun <T> checkNotNull(obj: T?, name: String?): T {
        return checkThat(obj != null, obj, "%s must not be null", name)
    }

    fun <T> checkNotNull(obj: T): T {
        return checkNotNull(obj, "argument")
    }

    fun <T> checkThat(
        condition: Boolean,
        argument: T?,
        message: String,
        vararg messageArgs: Any?
    ): T {
        return checkThat(condition, argument, createThrowable(message, *messageArgs))
    }

    fun <T> checkThat(condition: Boolean, argument: T?, err: RuntimeException?): T? {
        if (!condition) throw err!!
        return argument
    }

    private fun createThrowable(format: String, vararg args: Any?): RuntimeException {
        var format: String? = format
        var args = args
        if (format == null) {
            format = "Illegal Argument"
            args = arrayOfNulls<Any>(0)
        }
        val message = String.format(format, *args)
        return IllegalArgumentException(message)
    }
}