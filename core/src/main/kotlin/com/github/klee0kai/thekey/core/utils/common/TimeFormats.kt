package com.github.klee0kai.thekey.core.utils.common

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.TimeZone

object TimeFormats {

    fun simpleDateFormat(): DateFormat {
        val formatter = SimpleDateFormat.getDateInstance()
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter
    }

    fun timeDateFormat(): DateFormat {
        val formatter = SimpleDateFormat.getDateTimeInstance()
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter
    }

}
