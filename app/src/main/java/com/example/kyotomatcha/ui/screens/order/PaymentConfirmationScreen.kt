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
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun PaymentConfirmationScreen(
    onNavigateBack: () -> Unit,
    onConfirmSuccess: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("Nominal bukti tidak sesuai dengan total pesanan.") }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = White,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC53030)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC53030))
                    ) {
                        Text(text = "Tolak", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC53030))
                    }

                    Button(
                        onClick = { onConfirmSuccess() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                    ) {
                        Text(text = "Konfirmasi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = White)
                    }
                }
            }
        },
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Top App Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
                Column {
                    Text(
                        text = "Konfirmasi Pembayaran",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Text(
                        text = "Kyoto Matcha Senopati • Salsa",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total Pesanan Header Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Total pesanan", fontSize = 13.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "KM-261103-038 • Offline (QRIS)", fontSize = 11.sp, color = TextMuted)
                    }
                    Text(text = "Rp80.000", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pratinjau Bukti Bayar Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFE9F5ED),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Pratinjau bukti", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
                        Text(text = "⛶", fontSize = 14.sp, color = PrimaryDarkGreen)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = White
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Contoh bukti bayar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Rp80.000", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "03 Nov 2026 • 09:42", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Ref. OM2611030942", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "ILUSTRASI • BUKAN BUKTI TRANSAKSI", fontSize = 9.sp, color = TextMuted)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Diunggah oleh Salsa • 09:42", fontSize = 11.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(16.dp))

            // Hasil Baca Otomatis Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Hasil baca otomatis", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE6F4EA)
                        ) {
                            Text(
                                text = "Sesuai",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF137333)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ConfirmationInfoRow(label = "Nominal", value = "Rp80.000")
                    Spacer(modifier = Modifier.height(8.dp))
                    ConfirmationInfoRow(label = "Waktu bayar", value = "09:42")
                    Spacer(modifier = Modifier.height(8.dp))
                    ConfirmationInfoRow(label = "Nomor referensi", value = "OM2611030942")

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Perbandingan bukti, bukan konfirmasi pembayaran.", fontSize = 11.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pastikan saldo masuk di akun merchant sebelum konfirmasi.",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Reject Dialog
        if (showRejectDialog) {
            AlertDialog(
                onDismissRequest = { showRejectDialog = false },
                title = {
                    Text(text = "Tolak bukti pembayaran", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                },
                text = {
                    Column {
                        Text(
                            text = "Bukti Rp60.000 tidak sesuai dengan total pesanan Rp80.000.",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Alasan penolakan *", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = rejectReason,
                            onValueChange = { rejectReason = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showRejectDialog = false; onNavigateBack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC53030)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Tolak Bukti", color = White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showRejectDialog = false },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Batal", color = PrimaryDarkGreen)
                    }
                },
                containerColor = White
            )
        }
    }
}

@Composable
fun ConfirmationInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
    }
}
