package com.example.kyotomatcha.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import com.example.kyotomatcha.ui.components.OmochaLogo
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White
import com.example.kyotomatcha.util.CurrencyUtil
import com.example.kyotomatcha.util.DateUtil
import com.example.kyotomatcha.util.QuantityUtil
import androidx.compose.runtime.LaunchedEffect

@Composable
fun BusinessSummaryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNotificationClick: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onInventoryClick: () -> Unit,
    viewModel: BusinessSummaryViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val dash by viewModel.dashboard.collectAsState()
    val sales = dash?.sales
    val stockAlerts = dash?.stock
    LaunchedEffect(Unit) { viewModel.refresh() }

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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OmochaLogo(size = 32.dp)
                    Column {
                        Text(
                            text = "Ringkasan usaha",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "Dasbor • Kyoto-Matcha",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = CardBackground,
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { onNotificationClick() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🔔", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card 1: Penjualan hari ini & Chart
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
                            text = "Penjualan hari ini",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "Laporan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MatchaGreen,
                            modifier = Modifier.clickable { /* open report */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = sales?.let { CurrencyUtil.formatRupiah(it.total) } ?: "-",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (dash == null) "Memuat..." else sales?.let { "${it.orders} pesanan lunas • ${it.cups} gelas • ${DateUtil.getCurrentDateFormatted()}" } ?: "Penjualan hanya untuk pemilik dan kasir",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SalesBarChart(dash?.chart.orEmpty())
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card 2: Peringatan stok
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
                            text = "Peringatan stok",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "Lihat semua",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MatchaGreen,
                            modifier = Modifier.clickable { /* see all */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val alertItems = stockAlerts?.items.orEmpty()
                    if (alertItems.isEmpty()) {
                        Text(text = if (stockAlerts == null) "Memuat..." else "Semua stok aman.", fontSize = 13.sp, color = TextMuted)
                    }
                    alertItems.forEachIndexed { index, item ->
                        if (index > 0) Spacer(modifier = Modifier.height(12.dp))
                        StockItemRow(
                            name = item.name,
                            detail = QuantityUtil.withUnit(item.quantity, item.unit),
                            status = if (item.status == "habis") "Habis" else "Menipis",
                            isWarning = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stockAlerts?.let { "${it.low} menipis • ${it.empty} habis" } ?: "",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BarChartItem(day: String, heightFraction: Float, isDark: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(110.dp)
    ) {
        Box(
            modifier = Modifier
                .width(26.dp)
                .fillMaxHeight(heightFraction)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(if (isDark) PrimaryDarkGreen else Color(0xFFD6E2C9))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = day,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

@Composable
fun StockItemRow(name: String, detail: String, status: String, isWarning: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = detail,
                fontSize = 12.sp,
                color = TextMuted
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isWarning) Color(0xFFFDF0ED) else Color(0xFFE9F5ED)
        ) {
            Text(
                text = status,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isWarning) Color(0xFFC53030) else Color(0xFF137333),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}
