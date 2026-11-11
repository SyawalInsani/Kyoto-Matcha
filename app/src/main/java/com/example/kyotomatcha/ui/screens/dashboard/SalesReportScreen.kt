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
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun SalesReportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: SalesReportViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
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
                    BottomNavItem(label = "Inventoris", icon = Icons.Filled.Inventory2, selected = false)
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "←",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen,
                        modifier = Modifier.clickable { onNavigateBack() }
                    )
                    Column {
                        Text(
                            text = "Laporan Penjualan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "Kyoto Matcha • 3 Nov 2026",
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
                        Text(text = "📥", fontSize = 16.sp)
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
                        text = "Total penjualan hari ini",
                        fontSize = 12.sp,
                        color = Color(0xFFB0C4B1)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Rp758.020",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "30 pesanan lunas • 30 gelas • 2 belum bayar",
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
                            text = "28 Okt – 3 Nov",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        BarChartItem(day = "28", heightFraction = 0.3f, isDark = false)
                        BarChartItem(day = "29", heightFraction = 0.45f, isDark = false)
                        BarChartItem(day = "30", heightFraction = 0.38f, isDark = false)
                        BarChartItem(day = "31", heightFraction = 0.65f, isDark = false)
                        BarChartItem(day = "01", heightFraction = 0.55f, isDark = false)
                        BarChartItem(day = "02", heightFraction = 0.75f, isDark = false)
                        BarChartItem(day = "03", heightFraction = 0.80f, isDark = true)
                    }
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

                    SourceRow(name = "Offline (QRIS)", amount = "Rp60.000", isBold = false)
                    Spacer(modifier = Modifier.height(10.dp))
                    SourceRow(name = "Shopee", amount = "Rp147.478", isBold = false)
                    Spacer(modifier = Modifier.height(10.dp))
                    SourceRow(name = "Gojek", amount = "Rp343.535", isBold = false)
                    Spacer(modifier = Modifier.height(10.dp))
                    SourceRow(name = "Grab", amount = "Rp207.007", isBold = false)

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

                    SourceRow(name = "Uang makan", amount = "Rp30.000", isBold = false)
                    Spacer(modifier = Modifier.height(10.dp))
                    SourceRow(name = "Es batu", amount = "Rp17.000", isBold = false)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(16.dp))

                    SourceRow(name = "Total pengeluaran", amount = "Rp47.000", isBold = true)
                    Spacer(modifier = Modifier.height(12.dp))
                    SourceRow(name = "Sisa setelah pengeluaran", amount = "Rp711.020", isBold = true, textColor = MatchaGreen)
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

                    BestSellerRow(name = "Matcha Latte", count = "12")
                    Spacer(modifier = Modifier.height(10.dp))
                    BestSellerRow(name = "Strawberry Matcha", count = "8")
                    Spacer(modifier = Modifier.height(10.dp))
                    BestSellerRow(name = "Lainnya", count = "10")
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
