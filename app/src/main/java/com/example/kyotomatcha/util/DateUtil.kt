package com.example.kyotomatcha.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    private val localeId = Locale.forLanguageTag("id")

    fun getCurrentDateFormatted(): String {
        val sdf = SimpleDateFormat("d MMM yyyy", localeId)
        return sdf.format(Date())
    }

    fun getCurrentDateLongFormatted(): String {
        val sdf = SimpleDateFormat("d MMMM yyyy", localeId)
        return sdf.format(Date())
    }

    fun getCurrentDayAndDateFormatted(): String {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", localeId)
        return sdf.format(Date())
    }

    fun getCurrentTimeFormatted(): String {
        val sdf = SimpleDateFormat("HH:mm", localeId)
        return sdf.format(Date())
    }
}
