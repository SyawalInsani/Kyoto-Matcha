package com.example.kyotomatcha.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtil {
    private val localeId = Locale.forLanguageTag("id")

    fun formatRupiah(amount: Number): String {
        val formatter = NumberFormat.getNumberInstance(localeId)
        return "Rp${formatter.format(amount)}"
    }
}
