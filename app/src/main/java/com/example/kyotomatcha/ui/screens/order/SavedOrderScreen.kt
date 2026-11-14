package com.example.kyotomatcha.ui.screens.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.util.DateUtil
import com.example.kyotomatcha.util.CurrencyUtil
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun SavedOrderScreen(
    onNavigateNewOrder: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToList: () -> Unit,
    viewModel: OrderViewModel = viewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val latestOrder = orders.firstOrNull()
    val currentSelectedSource by viewModel.selectedSource.collectAsState()
    val selectedSource = latestOrder?.source ?: currentSelectedSource
    val isOffline = selectedSource.equals("Offline", ignoreCase = true)
    val orderStatus = if (isOffline) "Menunggu bayar" else "Diproses"
    val orderMessage = if (isOffline) "Bukti bayar belum diterima." else "Pesanan online sudah lunas dari platform."
    val total = latestOrder?.total ?: viewModel.getTotalPrice()
    val orderItems = latestOrder?.items?.takeIf { it.isNotEmpty() } ?: viewModel.cart.collectAsState().value.values.toList()
    val orderId = latestOrder?.id ?: "KM-261103-038"
    val orderTime = latestOrder?.time ?: "09:40"
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userName = sessionManager.getUserName()
    val currentDate = remember { DateUtil.getCurrentDateFormatted() }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Top App Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.clickable { onNavigateHome() }
                )
                Column {
                    Text(
                        text = "Pesanan tersimpan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Text(
                        text = "$orderId • $currentDate, $orderTime",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Offline Badge & Title
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEF3C7)
            ) {
                Text(
                    text = selectedSource,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFD97706)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = orderStatus,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detail Pesanan Card
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
                        text = "Detail Pesanan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    orderItems.forEach { cartItem ->
                        DetailItemRow(
                            name = "${cartItem.quantity} × ${cartItem.menuItem.name}",
                            amount = CurrencyUtil.formatRupiah(cartItem.menuItem.price * cartItem.quantity)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = CurrencyUtil.formatRupiah(total), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Source & Kasir Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Sumber", fontSize = 13.sp, color = TextMuted)
                        Text(text = selectedSource, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDarkGreen)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Kasir / Cabang", fontSize = 13.sp, color = TextMuted)
                        Text(text = "$userName • Kyoto-Matcha", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDarkGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Success Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE6F4EA)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✓", fontSize = 18.sp, color = Color(0xFF137333), fontWeight = FontWeight.Bold)
                    Column {
                        Text(
                            text = "Pesanan dicatat",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF137333)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = orderMessage,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Action Buttons
            Button(
                onClick = { onNavigateNewOrder() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
            ) {
                Text(text = "Pesanan Baru", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onNavigateToList() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MatchaGreen),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MatchaGreen)
            ) {
                Text(text = "Lihat Daftar Pesanan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MatchaGreen)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DetailItemRow(name: String, amount: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, fontSize = 13.sp, color = TextMuted)
        Text(text = amount, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
    }
}
