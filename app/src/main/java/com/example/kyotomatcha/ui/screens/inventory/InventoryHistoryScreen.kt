package com.example.kyotomatcha.ui.screens.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kyotomatcha.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryHistoryScreen(
    onNavigateBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Semua") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Riwayat Inventaris", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "Kyoto-Matcha", fontSize = 12.sp, color = TextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryDarkGreen)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Calendar", tint = PrimaryDarkGreen)
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
                    Text(text = "Filter tanggal", fontSize = 13.sp, color = TextMuted)
                    Text(text = "2–3 Nov 2026", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

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

            Text(text = "3 November 2026", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(10.dp))

            if (selectedFilter == "Semua" || selectedFilter == "Stok Keluar") {
                HistoryItemCard(
                    name = "Gelas",
                    amount = "-30 pcs",
                    type = "Stok Keluar",
                    time = "09:15 · Otomatis",
                    desc = "30 pesanan minuman · 48 -> 18 pcs",
                    ref = "OUT-261103-021",
                    isOut = true
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (selectedFilter == "Semua" || selectedFilter == "Stok Masuk") {
                HistoryItemCard(
                    name = "Gelas",
                    amount = "+28 pcs",
                    type = "Stok Masuk",
                    time = "07:30 · Salsa",
                    desc = "PT Kemasan Nusantara · 20 -> 48 pcs",
                    ref = "IN-261103-012",
                    isOut = false
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (selectedFilter == "Semua" || selectedFilter == "Stok Masuk") {
                Text(text = "2 November 2026", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                Spacer(modifier = Modifier.height(10.dp))
                HistoryItemCard(
                    name = "Es batu",
                    amount = "+3 kg",
                    type = "Stok Masuk",
                    time = "15:30 · Salsa",
                    desc = "2 -> 5 kg",
                    ref = "",
                    isOut = false
                )
            }
        }
    }
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
                Text(text = name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                Text(
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOut) Color(0xFFE53E3E) else MatchaGreen
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
