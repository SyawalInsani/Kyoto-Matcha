package com.example.kyotomatcha.ui.screens.dashboard

import androidx.compose.foundation.background
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
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    Text(
                        text = "Notifikasi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                }

                Text(
                    text = "✓",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.clickable { /* mark all as read */ }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Selasa, 3 November 2026",
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Unread badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE6F4EA)
            ) {
                Text(
                    text = "3 notifikasi belum dibaca",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF137333)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Item 1: Pesanan baru diterima
            NotificationCard(
                title = "Pesanan baru diterima",
                subtitle = "09:15 • KM-261103-038 menunggu bayar.",
                isWarning = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notification Item 2: Gelas hampir habis
            NotificationCard(
                title = "Gelas hampir habis",
                subtitle = "Sisa 18 pcs • Minimum 20 pcs.",
                isWarning = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notification Item 3: Bahan perlu ditambah
            NotificationCard(
                title = "Bahan perlu ditambah",
                subtitle = "Matcha Powder 1,2 kg • Whipped Cream 0,5 liter.",
                isWarning = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hari ini sebelumnya",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Older Notification: Absen Masuk • Terlambat
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Absen Masuk • Terlambat",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "08:07 • Raka. Terlambat 7 menit dari jadwal 08:00.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "2 pesanan menunggu bayar belum dihitung sebagai penjualan.",
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun NotificationCard(title: String, subtitle: String, isWarning: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isWarning) Color(0xFFFDE8E8) else Color(0xFFF1F8F5)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isWarning) Color(0xFFC53030) else Color(0xFF137333),
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = if (isWarning) "!" else "✓", fontSize = 13.sp, color = White)
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isWarning) Color(0xFFC53030) else PrimaryDarkGreen
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    }
}
