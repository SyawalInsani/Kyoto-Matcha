package com.example.kyotomatcha.ui.screens.order

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.data.remote.ProofDto
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White
import com.example.kyotomatcha.util.CurrencyUtil

private val Red = Color(0xFFC53030)

/**
 * Alur bayar QRIS offline: kasir sudah memilih menu -> pelanggan membayar -> kasir memotret bukti di dalam bingkai
 * -> hasil baca muncul beberapa detik kemudian -> bila sesuai, "Lanjut" mencatat sebagai penjualan hari itu.
 * orderNumber kosong = pesanan terbaru.
 */
@Composable
fun PaymentConfirmationScreen(
    orderNumber: String = "",
    onNavigateBack: () -> Unit,
    onConfirmSuccess: () -> Unit,
    viewModel: OrderViewModel = viewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val proofState by viewModel.proof.collectAsState()
    val order = orders.firstOrNull { it.id == orderNumber } ?: orders.firstOrNull()
    val isOwner = viewModel.isOwner

    var photo by remember { mutableStateOf<Bitmap?>(null) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("Pelanggan membatalkan pesanan") }
    var actionError by remember { mutableStateOf("") }

    if (order == null) {
        Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text("Pesanan tidak ditemukan. Kembali lalu muat ulang daftar pesanan.", color = TextMuted, fontSize = 13.sp)
        }
        return
    }

    val done = proofState as? ProofState.Done
    val canContinue = done != null && (done.proof.status == "sesuai" || (done.proof.status == "perlu_dicek" && isOwner))
    val showCamera = proofState is ProofState.Idle || proofState is ProofState.Failed

    Scaffold(
        bottomBar = {
            Surface(Modifier.fillMaxWidth().navigationBarsPadding(), color = White, shadowElevation = 12.dp) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (actionError.isNotEmpty()) Text(actionError, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Red)

                    if (done != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { actionError = ""; photo = null; viewModel.resetProof() },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(26.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MatchaGreen),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MatchaGreen)
                            ) { Text("Foto ulang", fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                            Button(
                                enabled = canContinue,
                                onClick = {
                                    viewModel.confirmProof(order.id) { error ->
                                        actionError = error.orEmpty()
                                        if (error == null) onConfirmSuccess()
                                    }
                                },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(26.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                            ) { Text("Lanjut", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = White) }
                        }
                    }
                    if (proofState is ProofState.Failed && isOwner) {
                        // Layanan baca foto mati: hanya pemilik yang boleh mengonfirmasi tanpa bukti terbaca.
                        OutlinedButton(
                            onClick = {
                                viewModel.updateOrderStatus(order.id, "Diproses", payMethod = "qris") { error ->
                                    actionError = error.orEmpty()
                                    if (error == null) onConfirmSuccess()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp)
                        ) { Text("Konfirmasi manual (pemilik)", fontSize = 14.sp, color = PrimaryDarkGreen) }
                    }
                    if (proofState !is ProofState.Reading) {
                        TextButton(onClick = { showCancelDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Batalkan pesanan", color = Red, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = BackgroundCream
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("←", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen, modifier = Modifier.clickable { onNavigateBack() })
                Column {
                    Text("Bukti Bayar QRIS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Text("${order.id} • Offline • ${order.cashier}", fontSize = 11.sp, color = TextMuted)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Menu yang dibayar pelanggan dengan QRIS.
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = White, shadowElevation = 1.dp) {
                Column(Modifier.padding(16.dp)) {
                    Text("Pelanggan membayar", fontSize = 12.sp, color = TextMuted)
                    Spacer(Modifier.height(6.dp))
                    order.items.forEach { line ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${line.quantity}× ${line.menuItem.name}", fontSize = 13.sp, color = PrimaryDarkGreen)
                            Text(CurrencyUtil.formatRupiah(line.menuItem.price * line.quantity), fontSize = 13.sp, color = TextMuted)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total yang harus dibayar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(CurrencyUtil.formatRupiah(order.total), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            when (val s = proofState) {
                is ProofState.Idle, is ProofState.Failed -> {
                    if (s is ProofState.Failed) {
                        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFFDE8E8)) {
                            Text(s.message, Modifier.padding(12.dp), fontSize = 12.sp, color = Red)
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    Text(
                        "Setelah pelanggan membayar, minta ia membuka layar bukti bayar yang berhasil, lalu foto di dalam kotak.",
                        fontSize = 12.sp, color = TextMuted
                    )
                    Spacer(Modifier.height(8.dp))
                    Surface(Modifier.fillMaxWidth().height(460.dp), shape = RoundedCornerShape(20.dp), color = Color.Black) {
                        QrisCameraCapture(
                            onCaptured = { jpeg, preview ->
                                photo = preview
                                actionError = ""
                                viewModel.sendProof(order.id, jpeg)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                is ProofState.Reading -> {
                    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = White, shadowElevation = 1.dp) {
                        Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            photo?.let { PhotoThumb(it) }
                            Spacer(Modifier.height(16.dp))
                            CircularProgressIndicator(color = MatchaGreen)
                            Spacer(Modifier.height(10.dp))
                            Text("Membaca foto…", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            Text("Mohon tunggu sebentar.", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }

                is ProofState.Done -> ProofResultCard(s.proof, photo, isOwner)
            }

            Spacer(Modifier.height(24.dp))
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Batalkan pesanan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen) },
                text = {
                    Column {
                        Text("Stok dikembalikan dan pesanan ${order.id} dibatalkan.", fontSize = 13.sp, color = TextMuted)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = cancelReason, onValueChange = { cancelReason = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showCancelDialog = false
                            viewModel.updateOrderStatus(order.id, "Batal", reason = cancelReason) { error ->
                                actionError = error.orEmpty()
                                if (error == null) onNavigateBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Red),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("Batalkan", color = White) }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showCancelDialog = false }, shape = RoundedCornerShape(16.dp)) { Text("Kembali", color = PrimaryDarkGreen) }
                },
                containerColor = White
            )
        }
    }
}

@Composable
private fun PhotoThumb(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(), contentDescription = "Foto bukti bayar",
        contentScale = ContentScale.Fit, modifier = Modifier.height(220.dp)
    )
}

@Composable
private fun ProofResultCard(proof: ProofDto, photo: Bitmap?, isOwner: Boolean) {
    val (label, bg, fg) = when (proof.status) {
        "sesuai" -> Triple("Sesuai", Color(0xFFE6F4EA), Color(0xFF137333))
        "perlu_dicek" -> Triple("Perlu dicek", Color(0xFFFEF3C7), Color(0xFFA16207))
        else -> Triple("Tidak sesuai", Color(0xFFFDE8E8), Red)
    }
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = White, shadowElevation = 1.dp) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Hasil baca foto", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                Surface(shape = RoundedCornerShape(12.dp), color = bg) {
                    Text(label, Modifier.padding(horizontal = 10.dp, vertical = 3.dp), fontSize = 11.sp, fontWeight = FontWeight.Medium, color = fg)
                }
            }
            photo?.let {
                Spacer(Modifier.height(12.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { PhotoThumb(it) }
            }
            Spacer(Modifier.height(14.dp))
            InfoRow("Nominal terbaca", proof.ocr_amount?.let { CurrencyUtil.formatRupiah(it) } ?: "Tidak terbaca")
            Spacer(Modifier.height(6.dp))
            InfoRow("Harus dibayar", CurrencyUtil.formatRupiah(proof.expected_amount))
            proof.bank?.takeIf { it != "TIDAK_DIKETAHUI" }?.let { Spacer(Modifier.height(6.dp)); InfoRow("Bank / e-wallet", it) }
            proof.reference?.let { Spacer(Modifier.height(6.dp)); InfoRow("Nomor referensi", it) }
            Spacer(Modifier.height(12.dp))
            proof.checks.orEmpty().forEach { c ->
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(when (c.ok) { true -> "✅"; false -> "❌"; null -> "⚠️" }, fontSize = 13.sp)
                    Text(c.label + (c.detail?.takeIf { it.isNotBlank() }?.let { " — $it" } ?: ""), fontSize = 12.sp, color = PrimaryDarkGreen)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                when (proof.status) {
                    "sesuai" -> "Semua cocok. Tekan Lanjut untuk mencatat sebagai penjualan hari ini."
                    "perlu_dicek" -> if (isOwner) "Ada yang belum jelas. Anda pemilik: boleh melanjutkan bila yakin, atau foto ulang."
                    else "Ada yang belum jelas. Foto ulang, atau minta pemilik menyetujui."
                    else -> "Tidak cocok dengan pesanan. Foto ulang bukti bayar."
                },
                fontSize = 12.sp, color = TextMuted
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = TextMuted)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
    }
}
