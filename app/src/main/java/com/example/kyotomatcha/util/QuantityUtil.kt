package com.example.kyotomatcha.util

import java.util.Locale

object QuantityUtil {
    /** 1200.0 -> "1.200", 1.5 -> "1,5", 0.25 -> "0,25". Maksimal 2 angka desimal. */
    fun format(value: Double): String {
        val locale = Locale.forLanguageTag("id")
        return if (value % 1.0 == 0.0) {
            String.format(locale, "%,d", value.toLong())
        } else {
            String.format(locale, "%,.2f", value).trimEnd('0').trimEnd(',')
        }
    }

    /** "1.200 gram" */
    fun withUnit(value: Double, unit: String): String = "${format(value)} $unit"

    /** Teks dari kolom isian ("1,5" atau "1.5") menjadi angka; null bila bukan angka. */
    fun parse(input: String): Double? = input.trim().replace(',', '.').toDoubleOrNull()
}
