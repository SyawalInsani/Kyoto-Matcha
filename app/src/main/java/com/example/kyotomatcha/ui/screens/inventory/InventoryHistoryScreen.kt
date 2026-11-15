package com.example.kyotomatcha.ui.screens.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.data.remote.MovementDto
import com.example.kyotomatcha.ui.theme.*
import com.example.kyotomatcha.util.CurrencyUtil
import com.example.kyotomatcha.util.QuantityUtil

private val monthNames = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")

/** "2026-11-15T09:37:06+08:00" -> "15 November 2026" (jam setempat kedai, sesuai yang dikirim server). */
private fun dateLabel(iso: String): String {
    val parts = iso.take(10).split("-")
    val month = parts.getOrNull(1)?.toIntOrNull()?.let { monthNames.getOrNull(it - 1) } ?: return iso.take(10)
    return "${parts[2].toIntOrNull() ?: parts[2]} $month ${parts[0]}"
}

private val purposeLabels = mapOf("produksi" to "Produksi", "rusak" to "Rusak", "kedaluwarsa" to "Kedaluwarsa", "lainnya" to "Lainnya")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: InventoryViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf("Semua") }
    val scrollState = rememberScrollState()
    val movements by viewModel.movements.collectAsState()
    val isLoading by viewModel.movementsLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadMovements() }

    val shown = movements.filter {
        when (selectedFilter) {
            "Stok Masuk" -> it.type == "masuk"
            "Stok Keluar" -> it.type == "keluar"
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Riwayat Stok", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "Kyoto-Matcha · 50 catatan terbaru", fontSize = 12.sp, color = TextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryDarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundCream)
            )
        },
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Semua", "Stok Masuk", "Stok Keluar").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Button(
                        onClick = { selectedFilter = filter },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryDarkGreen else White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = filter, color = if (isSelected) White else PrimaryDarkGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (shown.isEmpty()) {
                Text(
                    text = if (isLoading) "Memuat riwayat..." else "Belum ada catatan stok.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            // Kelompokkan per tanggal; urutan dari server sudah terbaru dulu.
            shown.groupBy { it.created_at.take(10) }.forEach { (_, rows) ->
                Text(text = dateLabel(rows.first().created_at), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                Spacer(modifier = Modifier.height(10.dp))
                rows.forEach { m ->
                    MovementCard(m)
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MovementCard(m: MovementDto) {
    val isOut = m.type == "keluar"
    val sign = if (isOut) "-" else "+"
    val desc = buildList {
        if (!isOut && !m.supplier.isNullOrBlank()) add(m.supplier)
        if (isOut) m.purpose?.let { add(purposeLabels[it] ?: it) }
        if (!isOut && m.purchase_price != null) add(CurrencyUtil.formatRupiah(m.purchase_price))
        if (!m.note.isNullOrBlank()) add(m.note)
        add("${QuantityUtil.format(m.qty_before)} -> ${QuantityUtil.withUnit(m.qty_after, m.unit)}")
    }.joinToString(" · ")

    HistoryItemCard(
        name = m.item_name,
        amount = "$sign${QuantityUtil.withUnit(m.qty, m.unit)}",
        type = if (isOut) "Stok Keluar" else "Stok Masuk",
        time = "${m.created_at.substring(11, 16)} · ${m.user_name}",
        desc = desc,
        ref = m.reference_no,
        isOut = isOut
    )
}

@Composable
fun HistoryItemCard(name: String, amount: String, type: String, time: String, desc: String, ref: String, isOut: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isOut) Color(0xFFFFF5F5) else Color(0xFFF0FFF4),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen, modifier = Modifier.weight(1f))
                Text(
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOut) Color(0xFFE53E3E) else MatchaGreen,
                    maxLines = 1,
                    softWrap = false
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = type, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (isOut) Color(0xFFE53E3E) else MatchaGreen)
                Text(text = time, fontSize = 12.sp, color = TextMuted)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = desc, fontSize = 12.sp, color = PrimaryDarkGreen)
            if (ref.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = ref, fontSize = 11.sp, color = TextMuted)
            }
        }
    }
}
