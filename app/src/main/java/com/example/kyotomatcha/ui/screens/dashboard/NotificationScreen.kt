package com.example.kyotomatcha.ui.screens.dashboard

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
import com.example.kyotomatcha.util.DateUtil

@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val error by viewModel.error.collectAsState()
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
                    text = "↻",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.clickable { viewModel.load() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val list = items
            when {
                list == null -> CircularProgressIndicator()
                error.isNotEmpty() -> Text(text = error, fontSize = 13.sp, color = Color(0xFFC53030))
                list.isEmpty() -> Text(
                    text = "Belum ada notifikasi. Pembayaran QRIS yang masuk dan stok yang hampir habis muncul di sini.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
                else -> list.forEachIndexed { index, n ->
                    if (index > 0) Spacer(modifier = Modifier.height(12.dp))
                    NotificationCard(
                        title = n.title,
                        // created_at: "2026-11-25T00:01:36+08:00" -> "25 Nov 00:01"
                        subtitle = "${DateUtil.isoToShort(n.created_at.take(10))} ${n.created_at.substring(11, 16)} • ${n.body}",
                        isWarning = n.type != "pembayaran_qris"
                    )
                }
            }
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
