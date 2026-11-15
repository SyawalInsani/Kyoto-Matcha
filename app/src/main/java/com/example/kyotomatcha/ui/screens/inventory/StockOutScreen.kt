package com.example.kyotomatcha.ui.screens.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.ui.theme.*
import com.example.kyotomatcha.util.DateUtil
import com.example.kyotomatcha.util.QuantityUtil

// Tujuan stok keluar: teks di layar -> nilai yang dikirim ke server.
private val purposes = listOf("Produksi" to "produksi", "Rusak" to "rusak", "Kedaluwarsa" to "kedaluwarsa", "Lainnya" to "lainnya")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockOutScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: InventoryViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val result by viewModel.result.collectAsState()
    val formError by viewModel.formError.collectAsState()
    val saving by viewModel.isSaving.collectAsState()

    var selectedId by remember { mutableStateOf<Int?>(null) }
    var jumlah by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("produksi") }
    var catatan by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val selected = items.firstOrNull { it.id == selectedId }

    LaunchedEffect(Unit) { viewModel.clearForm() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Stok Keluar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "Catat pemakaian atau barang terbuang", fontSize = 12.sp, color = TextMuted)
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
            val done = result
            if (done != null) {
                StockResultView(
                    title = "Stok keluar tersimpan",
                    result = done,
                    qtyLabel = "-${QuantityUtil.withUnit(QuantityUtil.parse(jumlah) ?: 0.0, done.item.unit)}",
                    details = buildList {
                        add("Tujuan" to purposes.first { it.second == purpose }.first)
                        if (catatan.isNotBlank()) add("Catatan" to catatan)
                    },
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateBack = onNavigateBack
                )
            } else {
                if (selected != null) {
                    Surface(shape = RoundedCornerShape(10.dp), color = CardBackground, modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = "Stok saat ini · ${selected.name} ${QuantityUtil.withUnit(selected.quantity, selected.unit)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PrimaryDarkGreen,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                ItemPicker(items = items, selected = selected, onSelect = { selectedId = it.id })

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
                    LabeledField("Jumlah", jumlah, { jumlah = it }, modifier = Modifier.weight(1f), numeric = true)
                    LabeledField("Satuan", selected?.unit ?: "-", {}, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Tujuan", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    purposes.forEach { (label, value) ->
                        val isSelected = purpose == value
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PrimaryDarkGreen else White,
                            modifier = Modifier.clickable { purpose = value }
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) White else PrimaryDarkGreen,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                LabeledField("Catatan (opsional)", catatan, { catatan = it })

                Spacer(modifier = Modifier.height(16.dp))

                val qty = QuantityUtil.parse(jumlah)
                if (selected != null && qty != null && qty > 0) {
                    val after = selected.quantity - qty
                    Text(
                        text = if (after < 0) "Stok tidak cukup: tersisa ${QuantityUtil.withUnit(selected.quantity, selected.unit)}."
                        else "Setelah disimpan: ${QuantityUtil.format(selected.quantity)} - ${QuantityUtil.format(qty)} = ${QuantityUtil.withUnit(after, selected.unit)}.",
                        fontSize = 12.sp,
                        color = if (after < 0) androidx.compose.ui.graphics.Color(0xFFC53030) else TextMuted
                    )
                }
                Text(text = "Tanggal dan jam dicatat otomatis: ${DateUtil.getCurrentDateLongFormatted()}.", fontSize = 11.sp, color = TextMuted)

                ErrorText(formError)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.submitStockOut(selectedId, jumlah, purpose, catatan) },
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                ) {
                    Text(text = if (saving) "Menyimpan..." else "Simpan Stok Keluar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = White)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
