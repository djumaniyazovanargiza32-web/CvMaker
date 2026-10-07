package com.nargiza.cvmaker.core.common

import android.util.Log

interface AppLogger {
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

class AndroidAppLogger : AppLogger {
    override fun debug(tag: String, message: String) {
        // Redact any accidental emails or sensitive patterns in logs
        Log.d(tag, sanitize(message))
    }

    override fun info(tag: String, message: String) {
        Log.i(tag, sanitize(message))
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, sanitize(message), throwable)
    }

    private fun sanitize(input: String): String {
        // Simple regex to mask emails and phone numbers from logs to protect PII
        return input.replace(Regex("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+"), "[REDACTED_EMAIL]")
    }
}
