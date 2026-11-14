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
fun StockInScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var isSubmitted by remember { mutableStateOf(false) }
    var jumlah by remember { mutableStateOf("28") }
    var hargaBeli by remember { mutableStateOf("Rp28.000") }
    var pemasok by remember { mutableStateOf("PT Kemasan Nusantara") }
    var tanggal by remember { mutableStateOf("3 November 2026") }
    var catatan by remember { mutableStateOf("Pengadaan kemasan · Lot GL-1103") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Stok Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "Catat penerimaan kemasan", fontSize = 12.sp, color = TextMuted)
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
                    Text(text = "Stok masuk tersimpan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Gelas berhasil ditambahkan.", fontSize = 13.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(24.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = White,
                        shadowElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Gelas · +28 pcs", fontSize = 13.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "20 pcs -> 48 pcs", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFC6F6D5)
                            ) {
                                Text(
                                    text = "Tersedia",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF22543D),
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
                                Text(text = "IN-261103-012", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Pemasok", fontSize = 12.sp, color = TextMuted)
                                Text(text = "PT Kemasan Nusantara", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Harga beli", fontSize = 12.sp, color = TextMuted)
                                Text(text = "Rp28.000", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Tanggal / Pencatat", fontSize = 12.sp, color = TextMuted)
                                Text(text = "3 Nov · Salsa", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "07:30 · Pengadaan kemasan · Lot GL-1103", fontSize = 11.sp, color = TextMuted)
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
                                text = "Riwayat & notifikasi diperbarui\nStok saat penerimaan, sebelum 30 gelas terpakai.",
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
                        text = "Stok saat ini · Gelas 20 pcs",
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

                Text(text = "Harga beli · total", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = hargaBeli,
                    onValueChange = { hargaBeli = it },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Pemasok", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = pemasok,
                    onValueChange = { pemasok = it },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

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

                Text(text = "Setelah disimpan: 20 pcs + 28 pcs = 48 pcs.", fontSize = 12.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { isSubmitted = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                ) {
                    Text(text = "Simpan Stok Masuk", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = White)
                }
            }
        }
    }
}
