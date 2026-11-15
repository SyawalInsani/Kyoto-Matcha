package com.example.kyotomatcha.ui.screens.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.data.remote.StockItemDto
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.components.OmochaLogo
import com.example.kyotomatcha.ui.theme.*
import com.example.kyotomatcha.util.DateUtil
import com.example.kyotomatcha.util.QuantityUtil

private val categories = listOf("bahan" to "Bahan", "kemasan" to "Kemasan", "dessert" to "Dessert")

@Composable
fun InventoryScreen(
    onNavigateHome: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStockIn: () -> Unit,
    onNavigateToStockOut: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: InventoryViewModel = viewModel()
) {
    var selectedCategory by remember { mutableStateOf("bahan") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    val currentDate = remember { DateUtil.getCurrentDateFormatted() }
    val scrollState = rememberScrollState()

    val items by viewModel.items.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Muat ulang tiap kali layar dibuka (stok bisa berubah dari pesanan atau perangkat lain).
    LaunchedEffect(Unit) { viewModel.refresh() }

    val visible = items.filter {
        it.category == selectedCategory && it.name.contains(searchQuery.trim(), ignoreCase = true)
    }
    val categoryCount = items.count { it.category == selectedCategory }

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
                        Text(text = "Inventaris", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "Kyoto-Matcha · $currentDate", fontSize = 12.sp, color = TextMuted)
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

            // Ringkasan dari server
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
                    SummaryColumn("Total item", summary?.total?.toString() ?: "-", "jenis item", PrimaryDarkGreen)
                    SummaryColumn("Tersedia", summary?.tersedia?.toString() ?: "-", "item siap", PrimaryDarkGreen)
                    SummaryColumn("Menipis", summary?.menipis?.toString() ?: "-", "perlu ditambah", Color(0xFFD69E2E))
                    SummaryColumn("Habis", summary?.habis?.toString() ?: "-", "item", Color(0xFFC53030))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari item...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted) },
                singleLine = true,
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                categories.forEach { (key, label) ->
                    val isSelected = selectedCategory == key
                    Button(
                        onClick = { selectedCategory = key },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryDarkGreen else White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(text = label, color = if (isSelected) White else PrimaryDarkGreen, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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

            if (viewModel.isOwner) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryDarkGreen, containerColor = White)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Tambah Item Baru", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categories.first { it.first == selectedCategory }.second,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                Text(
                    text = "$categoryCount item · Riwayat",
                    fontSize = 12.sp,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.clickable { onNavigateToHistory() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                loadError != null && items.isEmpty() -> MessageBlock(
                    title = "Stok belum bisa dimuat",
                    body = loadError ?: "",
                    action = "Coba lagi",
                    onAction = { viewModel.refresh() }
                )
                isLoading && items.isEmpty() -> Text("Memuat stok...", fontSize = 13.sp, color = TextMuted)
                visible.isEmpty() && searchQuery.isNotBlank() -> MessageBlock(
                    title = "Item tidak ditemukan",
                    body = "Tidak ada item dengan nama \"$searchQuery\" di kategori ini.",
                    action = "Hapus pencarian",
                    onAction = { searchQuery = "" }
                )
                visible.isEmpty() -> MessageBlock(
                    title = "Belum ada item",
                    body = if (viewModel.isOwner) "Tambahkan item lewat tombol \"Tambah Item Baru\"." else "Pemilik dapat menambahkan item di kategori ini.",
                    action = null,
                    onAction = {}
                )
                else -> visible.forEachIndexed { index, item ->
                    if (index > 0) Spacer(modifier = Modifier.height(12.dp))
                    InventoryItemCard(item)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showAddDialog) {
        AddItemDialog(
            defaultCategory = selectedCategory,
            viewModel = viewModel,
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun SummaryColumn(label: String, value: String, caption: String, valueColor: Color) {
    Column {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        Text(text = caption, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
private fun MessageBlock(title: String, body: String, action: String?, onAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(shape = RoundedCornerShape(24.dp), color = CardBackground, modifier = Modifier.size(72.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Inventory2, contentDescription = null, tint = PrimaryDarkGreen, modifier = Modifier.size(36.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = body, fontSize = 13.sp, color = TextMuted, textAlign = TextAlign.Center)
        if (action != null) {
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = White)
            ) {
                Text(text = action, color = PrimaryDarkGreen, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AddItemDialog(defaultCategory: String, viewModel: InventoryViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(defaultCategory) }
    var unit by remember { mutableStateOf(if (defaultCategory == "bahan") "gram" else "pcs") }
    var minStock by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val saving by viewModel.isSaving.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Item Baru", fontWeight = FontWeight.Bold, color = PrimaryDarkGreen) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LabeledField("Nama item", name, { name = it })
                Text("Kategori", fontSize = 12.sp, color = TextMuted)
                ChipRow(categories, category) { category = it }
                Text("Satuan", fontSize = 12.sp, color = TextMuted)
                ChipRow(listOf("gram" to "gram", "ml" to "ml", "pcs" to "pcs", "liter" to "liter"), unit) { unit = it }
                LabeledField("Batas menipis (opsional)", minStock, { minStock = it }, numeric = true)
                ErrorText(error)
            }
        },
        confirmButton = {
            Button(
                enabled = !saving,
                onClick = {
                    viewModel.addItem(name, category, unit, minStock) { result ->
                        if (result == null) onDismiss() else error = result
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
            ) { Text("Simpan", color = White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = PrimaryDarkGreen) } },
        containerColor = White
    )
}

@Composable
private fun ChipRow(options: List<Pair<String, String>>, selected: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { (key, label) ->
            val isSelected = selected == key
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) PrimaryDarkGreen else CardBackground,
                modifier = Modifier.clickable { onSelect(key) }
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = if (isSelected) White else PrimaryDarkGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun InventoryItemCard(item: StockItemDto) {
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
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(shape = RoundedCornerShape(12.dp), color = CardBackground, modifier = Modifier.size(42.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Inventory2, contentDescription = null, tint = PrimaryDarkGreen, modifier = Modifier.size(22.dp))
                    }
                }
                Column {
                    Text(text = item.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (item.min_stock > 0) "Min. ${QuantityUtil.withUnit(item.min_stock, item.unit)}" else "Batas menipis belum diatur",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusChip(item.status)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = QuantityUtil.withUnit(item.quantity, item.unit),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
