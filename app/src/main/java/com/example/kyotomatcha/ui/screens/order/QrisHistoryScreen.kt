package com.example.kyotomatcha.ui.screens.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.data.remote.ProofDto
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White
import com.example.kyotomatcha.util.CurrencyUtil
import kotlinx.coroutines.launch

/** Hasil pembayaran offline lewat QRIS hari ini: nominal terbaca, status pencocokan, dan jam foto. */
@Composable
fun QrisHistoryScreen(onNavigateBack: () -> Unit, viewModel: OrderViewModel = viewModel()) {
    var rows by remember { mutableStateOf<List<ProofDto>?>(null) }
    var error by remember { mutableStateOf("") }
    var reload by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(reload) {
        val (list, err) = viewModel.loadQrisPayments()
        rows = list
        error = err.orEmpty()
    }

    Scaffold(containerColor = BackgroundCream) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("←", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen, modifier = Modifier.clickable { onNavigateBack() })
                Column {
                    Text("Riwayat Bayar QRIS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Text("Hasil baca foto bukti bayar hari ini", fontSize = 11.sp, color = TextMuted)
                }
            }
            Spacer(Modifier.height(14.dp))

            val list = rows
            when {
                error.isNotEmpty() -> {
                    Text(error, fontSize = 13.sp, color = Color(0xFFC53030))
                    TextButton(onClick = { reload++ }) { Text("Coba lagi") }
                }
                list == null -> CircularProgressIndicator()
                list.isEmpty() -> Text("Belum ada bukti bayar QRIS hari ini.", fontSize = 13.sp, color = TextMuted)
                else -> list.forEach { row ->
                    QrisHistoryCard(row, onDeleteImage = {
                        scope.launch {
                            val err = viewModel.deleteProofImage(row.payment_id)
                            if (err != null) error = err else reload++
                        }
                    })
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun QrisHistoryCard(p: ProofDto, onDeleteImage: () -> Unit) {
    val (label, bg, fg) = when (p.status) {
        "terkonfirmasi" -> Triple("Sudah dicatat", Color(0xFFE6F4EA), Color(0xFF137333))
        "sesuai" -> Triple("Sesuai, belum dilanjutkan", Color(0xFFE0F2FE), Color(0xFF075985))
        "perlu_dicek" -> Triple("Perlu dicek", Color(0xFFFEF3C7), Color(0xFFA16207))
        else -> Triple("Tidak sesuai", Color(0xFFFDE8E8), Color(0xFFC53030))
    }
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = White, shadowElevation = 1.dp) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(p.order_number, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                Surface(shape = RoundedCornerShape(12.dp), color = bg) {
                    Text(label, Modifier.padding(horizontal = 10.dp, vertical = 3.dp), fontSize = 11.sp, fontWeight = FontWeight.Medium, color = fg)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pesanan", fontSize = 12.sp, color = TextMuted)
                Text(CurrencyUtil.formatRupiah(p.expected_amount), fontSize = 13.sp, color = PrimaryDarkGreen)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Terbaca di foto", fontSize = 12.sp, color = TextMuted)
                Text(p.ocr_amount?.let { CurrencyUtil.formatRupiah(it) } ?: "Tidak terbaca", fontSize = 13.sp, color = PrimaryDarkGreen)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Waktu foto", fontSize = 12.sp, color = TextMuted)
                Text(p.created_at.substring(11, 16), fontSize = 13.sp, color = PrimaryDarkGreen)
            }
            p.reference?.let {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Referensi", fontSize = 12.sp, color = TextMuted)
                    Text(it, fontSize = 13.sp, color = PrimaryDarkGreen)
                }
            }
            if (p.has_image) {
                TextButton(onClick = onDeleteImage, contentPadding = PaddingValues(0.dp)) {
                    Text("Hapus foto bukti (hemat ruang)", fontSize = 12.sp, color = Color(0xFFC53030))
                }
            }
        }
    }
}
