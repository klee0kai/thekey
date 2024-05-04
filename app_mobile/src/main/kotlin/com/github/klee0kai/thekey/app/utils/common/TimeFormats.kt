package com.github.klee0kai.thekey.app.utils.common

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.TimeZone

object TimeFormats {

    fun simpleDateFormat(): DateFormat {
        val formatter = SimpleDateFormat.getDateInstance()
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter
    }

}
