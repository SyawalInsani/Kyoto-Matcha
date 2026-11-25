package com.example.kyotomatcha.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Menampilkan angka dengan titik ribuan (30000 -> 30.000) saat diketik.
 * Isi kolom yang disimpan tetap angka polos; titik hanya tampilan.
 */
object ThousandsDotTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val out = StringBuilder()
        val origToTrans = IntArray(digits.length + 1)
        for (i in digits.indices) {
            if (i > 0 && (digits.length - i) % 3 == 0) out.append('.')
            out.append(digits[i])
            origToTrans[i + 1] = out.length
        }
        val shown = out.toString()
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = origToTrans[offset.coerceIn(0, digits.length)]
            override fun transformedToOriginal(offset: Int): Int =
                shown.take(offset.coerceIn(0, shown.length)).count { it.isDigit() }
        }
        return TransformedText(AnnotatedString(shown), mapping)
    }
}
