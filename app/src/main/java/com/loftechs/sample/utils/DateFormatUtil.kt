package com.loftechs.sample.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatUtil {
    fun getStringFormat(timestamp: Long, formatPattern: String): String {
        return SimpleDateFormat(formatPattern, Locale.getDefault()).format(Date(timestamp))
    }

    fun formatTime(seconds: Int): String {
        val time = String.format("%02d : %02d", seconds / 60 % 60, seconds % 60)
        val hours = seconds / 3600
        return if (hours != 0) {
            "$hours - $time"
        } else {
            time
        }
    }
}