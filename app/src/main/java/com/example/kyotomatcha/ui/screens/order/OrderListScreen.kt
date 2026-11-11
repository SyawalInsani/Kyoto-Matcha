package com.example.kyotomatcha.ui.screens.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun OrderListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: OrderViewModel = viewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val selectedTab by viewModel.orderListTab.collectAsState()
    val scrollState = rememberScrollState()

    val filteredOrders = if (selectedTab == "Semua") {
        orders
    } else {
        orders.filter { it.status.equals(selectedTab, ignoreCase = true) }
    }

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
                    BottomNavItem(label = "Absen", icon = Icons.Filled.DateRange, selected = false, onClick = onNavigateToAbsen)
                    BottomNavItem(label = "Pesanan", icon = Icons.Filled.ShoppingBag, selected = true, onClick = onNavigateToOrder)
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
                Column {
                    Text(
                        text = "Daftar Pesanan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Salsa • Kyoto Matcha, 3 Nov 2026",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Text(
                    text = "✕",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Metrics Cards (3 cards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = White
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Menunggu bayar", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "2", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = White
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Pesanan - gelas", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "30 • 30", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = White
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Penjualan", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Rp758.020", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs (Semua, Menunggu bayar, Diproses, Selesai, Batal)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Semua", "Menunggu bayar", "Diproses", "Selesai", "Batal").forEach { tab ->
                    OrderFilterTab(label = tab, selected = selectedTab == tab) { viewModel.setOrderListTab(tab) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Items List
            filteredOrders.forEach { order ->
                OrderCard(order = order, onViewDetail = { onNavigateToDetail(order.id) })
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OrderFilterTab(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) PrimaryDarkGreen else White,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DDD0)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) White else PrimaryDarkGreen,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun OrderCard(order: OrderModel, onViewDetail: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.id,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.time,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = order.source,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFD97706)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.itemsSummary,
                fontSize = 13.sp,
                color = PrimaryDarkGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rp${order.total}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = when (order.status) {
                            "Selesai" -> Color(0xFFE6F4EA)
                            "Menunggu bayar" -> Color(0xFFFEF3C7)
                            "Diproses" -> Color(0xFFE8F0FE)
                            else -> Color(0xFFFDE8E8)
                        }
                    ) {
                        Text(
                            text = order.status,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (order.status) {
                                "Selesai" -> Color(0xFF137333)
                                "Menunggu bayar" -> Color(0xFFD97706)
                                "Diproses" -> Color(0xFF1967D2)
                                else -> Color(0xFFC53030)
                            }
                        )
                    }

                    Text(
                        text = "Lihat detail",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MatchaGreen,
                        modifier = Modifier.clickable { onViewDetail() }
                    )
                }
            }

            if (order.status == "Batal") {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Lihat detail • tidak dihitung penjualan",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}
