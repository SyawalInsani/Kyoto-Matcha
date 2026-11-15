package com.example.kyotomatcha.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kyotomatcha.data.remote.ChartDayDto

/** Grafik batang penjualan harian; batang terakhir (hari ini) lebih gelap. */
@Composable
fun SalesBarChart(chart: List<ChartDayDto>) {
    val max = chart.maxOfOrNull { it.total } ?: 0
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        chart.forEachIndexed { index, day ->
            // Tinggi minimal 4% supaya hari tanpa penjualan tetap terlihat sebagai garis tipis.
            val fraction = if (max > 0) (day.total.toFloat() / max).coerceAtLeast(0.04f) else 0.04f
            BarChartItem(day = day.date.takeLast(2), heightFraction = fraction, isDark = index == chart.lastIndex)
        }
    }
}
