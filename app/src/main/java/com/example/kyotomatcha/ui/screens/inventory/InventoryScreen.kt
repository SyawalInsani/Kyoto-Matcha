package com.example.kyotomatcha.ui.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.kyotomatcha.util.DateUtil
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.components.OmochaLogo
import com.example.kyotomatcha.ui.theme.*

@Composable
fun InventoryScreen(
    onNavigateHome: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStockIn: () -> Unit,
    onNavigateToStockOut: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Bahan") }
    var searchQuery by remember { mutableStateOf("") }
    val currentDate = remember { DateUtil.getCurrentDateFormatted() }
    val scrollState = rememberScrollState()

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
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(label = "Beranda", icon = Icons.Filled.Home, selected = false, onClick = onNavigateHome)
                    BottomNavItem(label = "Absen", icon = Icons.AutoMirrored.Filled.Assignment, selected = false, onClick = onNavigateToAbsen)
                    BottomNavItem(label = "Pesanan", icon = Icons.Filled.ShoppingBag, selected = false, onClick = onNavigateToOrder)
                    BottomNavItem(label = "Inventaris", icon = Icons.Filled.Inventory2, selected = true)
                    BottomNavItem(label = "Profil", icon = Icons.Filled.Person, selected = false, onClick = onNavigateToProfile)
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
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OmochaLogo(size = 36.dp)
                    Column {
                        Text(
                            text = "Inventaris",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "Kyoto-Matcha · $currentDate",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
                IconButton(onClick = onNavigateToHistory) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "Riwayat",
                        tint = PrimaryDarkGreen,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Stats Card
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Total item", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "12", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "jenis item", fontSize = 10.sp, color = TextMuted)
                    }
                    Column {
                        Text(text = "Tersedia", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "12", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "item siap", fontSize = 10.sp, color = TextMuted)
                    }
                    Column {
                        Text(text = "Menipis", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "3", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD69E2E))
                        Text(text = "2 bahan + Gelas", fontSize = 10.sp, color = TextMuted)
                    }
                    Column {
                        Text(text = "Habis", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "0", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "item", fontSize = 10.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari item...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted) },
                trailingIcon = { Icon(Icons.Filled.Tune, contentDescription = null, tint = TextMuted) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    disabledContainerColor = White,
                    focusedBorderColor = MatchaGreen,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Bahan", "Kemasan", "Dessert").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Button(
                        onClick = { selectedTab = tab },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) PrimaryDarkGreen else White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) White else PrimaryDarkGreen,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons (Stok Masuk & Stok Keluar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToStockIn,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                ) {
                    Icon(Icons.Filled.SouthEast, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Stok Masuk", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = onNavigateToStockOut,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryDarkGreen, containerColor = White)
                ) {
                    Icon(Icons.Filled.NorthEast, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Stok Keluar", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (selectedTab) {
                        "Bahan" -> "Bahan Baku"
                        "Kemasan" -> "Kemasan"
                        else -> "Dessert"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                Text(
                    text = when (selectedTab) {
                        "Bahan" -> "8 item · Riwayat"
                        "Kemasan" -> "1 item · Riwayat"
                        else -> "3 item · Riwayat"
                    },
                    fontSize = 12.sp,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.clickable { onNavigateToHistory() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items List based on tab
            if (searchQuery.isNotEmpty() && !listOf("matcha", "fresh", "oat", "gelas", "cheesecake", "tiramisu", "fudgy").any { it.contains(searchQuery.lowercase()) }) {
                // Not found state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = CardBackground,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Inventory2, contentDescription = null, tint = PrimaryDarkGreen, modifier = Modifier.size(36.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Bahan tidak ditemukan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tidak ada bahan dengan nama \"$searchQuery\".\nCoba kata kunci lain atau hapus filter.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = { searchQuery = "" },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = White)
                    ) {
                        Text(text = "Hapus pencarian", color = PrimaryDarkGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                when (selectedTab) {
                    "Bahan" -> {
                        InventoryItemCard("Matcha Powder", "1,2 kg · Min. 2 kg", "1,2 kg", "Menipis", true)
                        Spacer(modifier = Modifier.height(12.dp))
                        InventoryItemCard("Fresh Milk", "4 liter · Min. 2 liter", "4 liter", "Tersedia", false)
                        Spacer(modifier = Modifier.height(12.dp))
                        InventoryItemCard("Oat Milk", "2 liter · Min. 1 liter", "2 liter", "Tersedia", false)
                    }
                    "Kemasan" -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = White,
                            shadowElevation = 1.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(text = "Gelas", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                                        Text(text = "Kemasan minuman utama", fontSize = 12.sp, color = TextMuted)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFEFCBF)
                                    ) {
                                        Text(
                                            text = "Menipis",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFB7791F),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(text = "18 pcs", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                                    }
                                    Text(text = "Min. 20 pcs", fontSize = 12.sp, color = TextMuted)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Berkurang otomatis dari pesanan minuman", fontSize = 11.sp, color = TextMuted)

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBackground)

                                Text(text = "Alur Stok", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Stok Awal", fontSize = 12.sp, color = TextMuted)
                                    Text(text = "20 pcs", fontSize = 12.sp, color = PrimaryDarkGreen)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Stok Masuk", fontSize = 12.sp, color = TextMuted)
                                    Text(text = "+28 pcs", fontSize = 12.sp, color = MatchaGreen, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Terpakai", fontSize = 12.sp, color = TextMuted)
                                    Text(text = "-30 pcs", fontSize = 12.sp, color = Color(0xFFE53E3E), fontWeight = FontWeight.Medium)
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBackground)

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Sisa", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                                    Text(text = "18 pcs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBackground)

                                Text(text = "Penggunaan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "30 pesanan", fontSize = 12.sp, color = TextMuted)
                                    Text(text = "30 gelas", fontSize = 12.sp, color = PrimaryDarkGreen)
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBackground)

                                Text(text = "Pemasok", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Nama", fontSize = 12.sp, color = TextMuted)
                                    Text(text = "PT Kemasan Nusantara", fontSize = 12.sp, color = PrimaryDarkGreen)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Satuan", fontSize = 12.sp, color = TextMuted)
                                    Text(text = "pcs", fontSize = 12.sp, color = PrimaryDarkGreen)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "Total Pembelian", fontSize = 12.sp, color = TextMuted)
                                    Text(text = "Rp28.000", fontSize = 12.sp, color = PrimaryDarkGreen, fontWeight = FontWeight.Bold)
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBackground)

                                Text(text = "Aksi", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = onNavigateToStockIn,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(text = "Stok Masuk", color = PrimaryDarkGreen)
                                    }
                                    OutlinedButton(
                                        onClick = onNavigateToStockOut,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(text = "Stok Keluar", color = PrimaryDarkGreen)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = onNavigateToHistory,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "Riwayat", color = PrimaryDarkGreen)
                                }
                            }
                        }
                    }
                    "Dessert" -> {
                        InventoryItemCard("Cheesecake", "Min. 2 pcs", "6 pcs", "Tersedia", false)
                        Spacer(modifier = Modifier.height(12.dp))
                        InventoryItemCard("Tiramisu", "Min. 2 pcs", "4 pcs", "Tersedia", false)
                        Spacer(modifier = Modifier.height(12.dp))
                        InventoryItemCard("Fudgy", "Min. 2 pcs", "5 pcs", "Tersedia", false)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "8 bahan · 1 Gelas · 3 dessert. Menipis: 2 bahan + Gelas; tidak ada stok habis.",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun InventoryItemCard(name: String, subtitle: String, amount: String, status: String, isWarning: Boolean) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardBackground,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Inventory2, contentDescription = null, tint = PrimaryDarkGreen, modifier = Modifier.size(22.dp))
                    }
                }
                Column {
                    Text(text = name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isWarning) Color(0xFFFEFCBF) else Color(0xFFC6F6D5)
                    ) {
                        Text(
                            text = status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isWarning) Color(0xFFB7791F) else Color(0xFF22543D),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(text = amount, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
        }
    }
}
