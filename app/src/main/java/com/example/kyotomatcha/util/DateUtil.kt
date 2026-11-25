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

    /** "2026-11-10" -> "10 Nov 2026" */
    fun isoToMedium(iso: String): String = "${isoToShort(iso)} ${iso.take(4)}"

    /** "2026-11-10" -> "10 Nov" */
    fun isoToShort(iso: String): String {
        val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
        val parts = iso.take(10).split("-")
        val month = parts.getOrNull(1)?.toIntOrNull()?.let { months.getOrNull(it - 1) } ?: return iso
        return "${parts[2].toInt()} $month"
    }

    /** Sapaan menurut jam di HP: pagi, siang, sore, malam. */
    fun greeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 11 -> "Selamat pagi"
            hour < 15 -> "Selamat siang"
            hour < 18 -> "Selamat sore"
            else -> "Selamat malam"
        }
    }

    fun getCurrentTimeFormatted(): String {
        val sdf = SimpleDateFormat("HH:mm", localeId)
        return sdf.format(Date())
    }
}
