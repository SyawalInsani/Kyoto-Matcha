package com.example.kyotomatcha.ui.screens.inventory

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
import com.example.kyotomatcha.util.CurrencyUtil
import com.example.kyotomatcha.util.DateUtil
import com.example.kyotomatcha.util.QuantityUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInScreen(
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
    var hargaBeli by remember { mutableStateOf("") }
    var pemasok by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val selected = items.firstOrNull { it.id == selectedId }

    // Bersihkan sisa hasil/kesalahan dari pembukaan sebelumnya.
    LaunchedEffect(Unit) { viewModel.clearForm() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Stok Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "Catat penerimaan barang", fontSize = 12.sp, color = TextMuted)
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
                val price = hargaBeli.filter { it.isDigit() }.toIntOrNull()
                StockResultView(
                    title = "Stok masuk tersimpan",
                    result = done,
                    qtyLabel = "+${QuantityUtil.withUnit(QuantityUtil.parse(jumlah) ?: 0.0, done.item.unit)}",
                    details = buildList {
                        if (pemasok.isNotBlank()) add("Pemasok" to pemasok)
                        if (price != null) add("Harga beli" to CurrencyUtil.formatRupiah(price))
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
                LabeledField("Harga beli · total (opsional)", hargaBeli, { hargaBeli = it.filter { c -> c.isDigit() } }, numeric = true, placeholder = "Rp")
                Spacer(modifier = Modifier.height(16.dp))
                LabeledField("Pemasok (opsional)", pemasok, { pemasok = it })
                Spacer(modifier = Modifier.height(16.dp))
                LabeledField("Catatan (opsional)", catatan, { catatan = it })

                Spacer(modifier = Modifier.height(16.dp))

                val qty = QuantityUtil.parse(jumlah)
                if (selected != null && qty != null && qty > 0) {
                    Text(
                        text = "Setelah disimpan: ${QuantityUtil.format(selected.quantity)} + ${QuantityUtil.format(qty)} = ${QuantityUtil.withUnit(selected.quantity + qty, selected.unit)}.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
                Text(text = "Tanggal dan jam dicatat otomatis: ${DateUtil.getCurrentDateLongFormatted()}.", fontSize = 11.sp, color = TextMuted)

                ErrorText(formError)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.submitStockIn(selectedId, jumlah, hargaBeli, pemasok, catatan) },
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                ) {
                    Text(text = if (saving) "Menyimpan..." else "Simpan Stok Masuk", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = White)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
