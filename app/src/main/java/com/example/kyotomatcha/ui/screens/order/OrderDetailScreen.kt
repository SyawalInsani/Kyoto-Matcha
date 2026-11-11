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
fun OrderDetailScreen(
    orderId: String,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("Pelanggan membatalkan pesanan") }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = White,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { /* Tandai Selesai */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                    ) {
                        Text(text = "Tandai Selesai", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = White)
                    }

                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC53030)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC53030))
                    ) {
                        Text(text = "Batalkan Pesanan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC53030))
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
                        text = "Detail Pesanan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Text(
                        text = orderId,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Source & Status Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "Gojek",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD97706)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F0FE)
                ) {
                    Text(
                        text = "Diproses",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1967D2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = orderId,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OrderInfoRow(label = "Waktu pesanan", value = "3 Nov 2026 • 09:35")
                    Spacer(modifier = Modifier.height(8.dp))
                    OrderInfoRow(label = "Dicatat oleh", value = "Salsa • Kasir")
                    Spacer(modifier = Modifier.height(8.dp))
                    OrderInfoRow(label = "Cabang aktif", value = "Kyoto Matcha")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rincian Pesanan Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Rincian pesanan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DetailMenuItemRow(name = "Matcha Latte", qty = "1 ×", price = "Rp25.000", notes = "Catatan: Kurangi gula")
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailMenuItemRow(name = "Strawberry Matcha", qty = "1 ×", price = "Rp30.000", notes = "Catatan: Es sedikit")

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total pesanan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "Rp55.000", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Pesanan Timeline Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Status pesanan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TimelineEventRow(time = "09:35", title = "Pesanan dicatat oleh Salsa")
                    Spacer(modifier = Modifier.height(10.dp))
                    TimelineEventRow(time = "09:36", title = "Diproses — sedang disiapkan")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Cancel Dialog
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = {
                    Text(text = "Batalkan pesanan ini?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                },
                text = {
                    Column {
                        Text(
                            text = "Pesanan $orderId yang dibatalkan tidak dihitung dalam penjualan.",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Alasan pembatalan", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = cancelReason,
                            onValueChange = { cancelReason = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showCancelDialog = false; onNavigateBack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC53030)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Batalkan Pesanan", color = White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showCancelDialog = false },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Kembali", color = PrimaryDarkGreen)
                    }
                },
                containerColor = White
            )
        }
    }
}

@Composable
fun OrderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
    }
}

@Composable
fun DetailMenuItemRow(name: String, qty: String, price: String, notes: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CardBackground,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🍵", fontSize = 18.sp)
                }
            }

            Column {
                Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = qty, fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = notes, fontSize = 11.sp, color = MatchaGreen)
            }
        }

        Text(text = price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
    }
}

@Composable
fun TimelineEventRow(time: String, title: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🕒", fontSize = 14.sp)
        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Text(text = time, fontSize = 11.sp, color = TextMuted)
        }
    }
}
