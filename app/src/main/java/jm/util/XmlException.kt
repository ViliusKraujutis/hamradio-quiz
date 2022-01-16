package jm.util

import java.lang.Exception
import java.lang.StringBuilder
import kotlin.jvm.JvmOverloads

class XmlException : Exception {
    var file: String? = null
        private set
    var line = -1
        private set

    constructor() : super() {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
    constructor(message: String?) : super(message) {}
    constructor(cause: Throwable?) : super(cause) {}

    @JvmOverloads
    constructor(message: String?, file: String?, line: Int = -1) : super(message) {
        this.file = file
        this.line = line
    }

    constructor(message: String?, file: String?, line: Int, cause: Throwable?) : super(
        message,
        cause
    ) {
        this.file = file
        this.line = line
    }

    constructor(message: String?, file: String?, cause: Throwable?) : this(
        message,
        file,
        -1,
        cause
    ) {
    }

    override fun toString(): String {
        val sb = StringBuilder("XmlException")
        if (file != null) sb.append(" in ").append(file)
        if (line != -1) sb.append(" at line ").append(line)
        sb.append(": ").append(message)
        return sb.toString()
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}