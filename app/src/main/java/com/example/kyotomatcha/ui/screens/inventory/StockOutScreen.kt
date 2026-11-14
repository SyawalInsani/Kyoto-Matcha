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
fun StockOutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var isSubmitted by remember { mutableStateOf(false) }
    var jumlah by remember { mutableStateOf("30") }
    var tanggal by remember { mutableStateOf("3 November 2026") }
    var selectedTujuan by remember { mutableStateOf("Produksi") }
    var catatan by remember { mutableStateOf("Berkurang otomatis dari pesanan minuman") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Stok Keluar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "Pemakaian otomatis dari pesanan minuman", fontSize = 12.sp, color = TextMuted)
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
            if (isSubmitted) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MatchaGreen.copy(alpha = 0.2f),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = MatchaGreen, modifier = Modifier.size(36.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Stok keluar tersimpan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Pemakaian Gelas berhasil dicatat.", fontSize = 13.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = White,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Gelas · -30 pcs", fontSize = 13.sp, color = Color(0xFFE53E3E))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "48 pcs -> 18 pcs", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFEFCBF)
                            ) {
                                Text(
                                    text = "Menipis",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFB7791F),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = White,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Referensi", fontSize = 12.sp, color = TextMuted)
                                Text(text = "OUT-261103-021", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Tujuan", fontSize = 12.sp, color = TextMuted)
                                Text(text = "Produksi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Tanggal / Pencatat", fontSize = 12.sp, color = TextMuted)
                                Text(text = "3 Nov · Salsa", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "30 pesanan minuman · Pemakaian otomatis", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = CardBackground
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MatchaGreen, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Riwayat & notifikasi diperbarui\nDi bawah minimum 20 pcs. Sisa 18 pcs.",
                                fontSize = 12.sp,
                                color = PrimaryDarkGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNavigateToHistory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                    ) {
                        Text(text = "Lihat Riwayat Stok", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = White)
                    ) {
                        Text(text = "Kembali ke Inventaris", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDarkGreen)
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CardBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Stok sebelum pemakaian · Gelas 48 pcs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryDarkGreen,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                Text(text = "Item", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = "Gelas",
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Jumlah", fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = jumlah,
                            onValueChange = { jumlah = it },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Satuan", fontSize = 12.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = "pcs",
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Tanggal", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = { tanggal = it },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Tujuan", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Produksi", "Rusak", "Kedaluwarsa", "Lainnya").forEach { tuj ->
                        val isSelected = selectedTujuan == tuj
                        Button(
                            onClick = { selectedTujuan = tuj },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryDarkGreen else White),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(text = tuj, color = if (isSelected) White else PrimaryDarkGreen, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Catatan", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = catatan,
                    onValueChange = { catatan = it },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "48 pcs – 30 gelas dari 30 pesanan = 18 pcs.\nContoh catatan otomatis, bukan pengurangan kedua.", fontSize = 12.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { isSubmitted = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                ) {
                    Text(text = "Simpan Stok Keluar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = White)
                }
            }
        }
    }
}
