package com.example.reminders.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object DateTextFormatter {
    fun date(millis: Long?, locale: Locale = Locale.getDefault()): String? {
        if (millis == null) return null
        return SimpleDateFormat("MMM d, yyyy", locale).format(Date(millis))
    }

    fun dateTime(millis: Long?, locale: Locale = Locale.getDefault()): String? {
        if (millis == null) return null
        return SimpleDateFormat("MMM d, HH:mm", locale).format(Date(millis))
    }
}