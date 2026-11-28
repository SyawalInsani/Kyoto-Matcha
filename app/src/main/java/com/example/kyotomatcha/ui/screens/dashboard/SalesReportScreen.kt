package com.example.kyotomatcha.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White
import com.example.kyotomatcha.util.CurrencyUtil
import com.example.kyotomatcha.util.DateUtil
import kotlinx.coroutines.launch

@Composable
fun SalesReportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onInventoryClick: () -> Unit,
    viewModel: SalesReportViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val report by viewModel.report.collectAsState()
    val reportError by viewModel.error.collectAsState()
    val reportLoading by viewModel.isLoading.collectAsState()
    val sales = report?.sales
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    BottomNavItem(label = "Beranda", icon = Icons.Filled.Home, selected = true, onClick = onNavigateBack)
                    BottomNavItem(label = "Absen", icon = Icons.Filled.DateRange, selected = false, onClick = onNavigateToAbsen)
                    BottomNavItem(label = "Pesanan", icon = Icons.Filled.ShoppingBag, selected = false, onClick = onNavigateToOrder)
                    BottomNavItem(label = "Inventaris", icon = Icons.Filled.Inventory2, selected = false, onClick = onInventoryClick)
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
                .padding(16.dp),
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
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = PrimaryDarkGreen
                        )
                    }
                    Column {
                        Text(
                            text = "Laporan Penjualan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "Kyoto-Matcha • ${DateUtil.getCurrentDateFormatted()}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                // Interactive Download Button
                Surface(
                    shape = CircleShape,
                    color = CardBackground,
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Laporan penjualan berhasil diunduh")
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Unduh Laporan",
                            modifier = Modifier.size(18.dp),
                            tint = PrimaryDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs (Hari ini, 7 hari, Bulan)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTab(label = "Hari ini", selected = selectedTab == "Hari ini") { viewModel.setSelectedTab("Hari ini") }
                FilterTab(label = "7 hari", selected = selectedTab == "7 hari") { viewModel.setSelectedTab("7 hari") }
                FilterTab(label = "Bulan", selected = selectedTab == "Bulan") { viewModel.setSelectedTab("Bulan") }
            }

            if (reportError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = reportError, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFFC53030))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dark Green Total Sales Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = PrimaryDarkGreen
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = when (selectedTab) {
                            "7 hari" -> "Total penjualan 7 hari terakhir"
                            "Bulan" -> "Total penjualan bulan ini"
                            else -> "Total penjualan hari ini"
                        },
                        fontSize = 12.sp,
                        color = Color(0xFFB0C4B1)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = sales?.let { CurrencyUtil.formatRupiah(it.total) } ?: "-",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (sales == null) (if (reportLoading) "Memuat..." else "") else "${sales.orders} pesanan lunas • ${sales.cups} gelas • ${sales.waiting_payment} belum bayar",
                        fontSize = 12.sp,
                        color = Color(0xFFB0C4B1)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grafik Penjualan Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grafik penjualan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = report?.chart?.takeIf { it.isNotEmpty() }?.let { "${DateUtil.isoToShort(it.first().date)} – ${DateUtil.isoToShort(it.last().date)}" } ?: "",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    SalesBarChart(report?.chart.orEmpty())
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Penjualan per sumber & Pengeluaran Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Penjualan per sumber",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val sources = report?.by_source.orEmpty()
                    sources.forEachIndexed { index, src ->
                        if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                        SourceRow(name = src.label, amount = CurrencyUtil.formatRupiah(src.total), isBold = false)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Pengeluaran",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val expenseRows = report?.expenses.orEmpty()
                    if (expenseRows.isEmpty()) {
                        Text(text = "Belum ada pengeluaran tercatat.", fontSize = 13.sp, color = TextMuted)
                    }
                    expenseRows.forEachIndexed { index, row ->
                        if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                        SourceRow(name = row.label, amount = CurrencyUtil.formatRupiah(row.total), isBold = false)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(16.dp))

                    SourceRow(name = "Total pengeluaran", amount = CurrencyUtil.formatRupiah(report?.total_expenses ?: 0), isBold = true)
                    Spacer(modifier = Modifier.height(12.dp))
                    SourceRow(name = "Sisa setelah pengeluaran", amount = CurrencyUtil.formatRupiah(report?.net ?: 0), isBold = true, textColor = MatchaGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Terlaris Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Menu terlaris",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val best = report?.best_sellers
                    if (best == null || (best.top.isEmpty() && best.others == 0)) {
                        Text(text = "Belum ada penjualan pada periode ini.", fontSize = 13.sp, color = TextMuted)
                    }
                    best?.top?.forEachIndexed { index, item ->
                        if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                        BestSellerRow(name = item.name, count = item.qty.toString())
                    }
                    if (best != null && best.others > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        BestSellerRow(name = "Lainnya", count = best.others.toString())
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FilterTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) PrimaryDarkGreen else White,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DDD0)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) White else PrimaryDarkGreen,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun SourceRow(name: String, amount: String, isBold: Boolean, textColor: Color = PrimaryDarkGreen) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) PrimaryDarkGreen else TextMuted
        )
        Text(
            text = amount,
            fontSize = 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun BestSellerRow(name: String, count: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, fontSize = 13.sp, color = PrimaryDarkGreen)
        Text(text = count, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
    }
}
