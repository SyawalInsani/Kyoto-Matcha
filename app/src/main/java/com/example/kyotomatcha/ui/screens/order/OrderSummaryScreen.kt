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
import androidx.compose.ui.platform.LocalContext
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.util.DateUtil
import com.example.kyotomatcha.util.CurrencyUtil
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun OrderSummaryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQris: () -> Unit,
    onNavigateToSavedOrder: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onInventoryClick: () -> Unit,
    viewModel: OrderViewModel = viewModel()
) {
    val cart by viewModel.cart.collectAsState()
    val selectedSource by viewModel.selectedSource.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userName = sessionManager.getUserName()
    val currentDate = remember { DateUtil.getCurrentDateFormatted() }
    val scrollState = rememberScrollState()

    val subtotal = cart.values.sumOf { it.menuItem.price * it.quantity }
    val total = subtotal

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                // Bottom Checkout Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MatchaGreen,
                    shadowElevation = 6.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val isOffline = selectedSource.equals("Offline", ignoreCase = true)
                                val currentStatus = viewModel.selectedOrderStatus.value
                                viewModel.createOrder(currentStatus)
                                if (isOffline && currentStatus.equals("Menunggu bayar", ignoreCase = true)) {
                                    onNavigateToQris()
                                } else {
                                    onNavigateToSavedOrder()
                                }
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Simpan Pesanan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }

                // Bottom Navigation
                Surface(
                    modifier = Modifier.fillMaxWidth(),
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
                        BottomNavItem(label = "Pesanan", icon = Icons.Filled.ShoppingBag, selected = true)
                        BottomNavItem(label = "Inventaris", icon = Icons.Filled.Inventory2, selected = false, onClick = onInventoryClick)
                        BottomNavItem(label = "Profil", icon = Icons.Filled.Person, selected = false, onClick = onNavigateToProfile)
                    }
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
                            text = "Ringkasan Pesanan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "Pesanan KM-261103-038",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Text(text = "🛍️", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metadata card (Sumber, Tanggal, Kasir)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = currentDate, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "$userName • Kyoto-Matcha", fontSize = 12.sp, color = TextMuted)
                }

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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Selection Row
            val selectedOrderStatus by viewModel.selectedOrderStatus.collectAsState()
            Text(text = "Status Pesanan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Diproses", "Menunggu bayar", "Selesai").forEach { statusOpt ->
                    val isSelected = selectedOrderStatus == statusOpt
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) PrimaryDarkGreen else White,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DDD0)),
                        modifier = Modifier.clickable { viewModel.setSelectedOrderStatus(statusOpt) }
                    ) {
                        Text(
                            text = statusOpt,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) White else PrimaryDarkGreen,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cart Items List
            cart.values.forEach { cartItem ->
                CartItemCard(
                    cartItem = cartItem,
                    onIncrement = { viewModel.updateQuantity(cartItem.menuItem.id, 1) },
                    onDecrement = { viewModel.updateQuantity(cartItem.menuItem.id, -1) },
                    onNotesChange = { notes -> viewModel.updateNotes(cartItem.menuItem.id, notes) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Subtotal & Total Card
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Subtotal", fontSize = 13.sp, color = TextMuted)
                        Text(text = CurrencyUtil.formatRupiah(subtotal), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = CurrencyUtil.formatRupiah(total), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onNotesChange: (String) -> Unit
) {
    val itemTotal = cartItem.menuItem.price * cartItem.quantity

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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CardBackground,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = cartItem.menuItem.imagePlaceholder, fontSize = 22.sp)
                        }
                    }

                    Column {
                        Text(
                            text = cartItem.menuItem.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${CurrencyUtil.formatRupiah(cartItem.menuItem.price)} / item",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyUtil.formatRupiah(itemTotal),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    }
                }

                Text(text = "🗑️", fontSize = 16.sp, modifier = Modifier.clickable { onDecrement() })
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quantity Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardBackground
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "−", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onDecrement() })
                        Text(text = "${cartItem.quantity}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "+", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onIncrement() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes field
            OutlinedTextField(
                value = cartItem.notes,
                onValueChange = { onNotesChange(it) },
                placeholder = { Text(text = "Tambahkan catatan...", fontSize = 12.sp, color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE2DDD0),
                    unfocusedBorderColor = Color(0xFFE2DDD0)
                ),
                singleLine = true
            )
        }
    }
}
