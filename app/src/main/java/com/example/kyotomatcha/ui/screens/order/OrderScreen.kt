package com.example.kyotomatcha.ui.screens.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.util.CurrencyUtil
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.components.OmochaLogo
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun OrderScreen(
    onNavigateHome: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToSummary: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onInventoryClick: () -> Unit,
    viewModel: OrderViewModel = viewModel()
) {
    val selectedSource by viewModel.selectedSource.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val menuItems by viewModel.menuItems.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val scrollState = rememberScrollState()

    val totalItems = cart.values.sumOf { it.quantity }
    val totalPrice = cart.values.sumOf { it.menuItem.price * it.quantity }

    val filteredItems = menuItems.filter { item ->
        item.category.equals(selectedCategory, ignoreCase = true) &&
                (searchQuery.isBlank() || item.name.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                // Floating Checkout Bar
                if (totalItems > 0) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onNavigateToSummary() },
                        shape = RoundedCornerShape(24.dp),
                        color = MatchaGreen,
                        shadowElevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingBag,
                                    contentDescription = "Pesanan",
                                    modifier = Modifier.size(20.dp),
                                    tint = White
                                )
                                Text(
                                    text = "Lihat Pesanan • $totalItems item • ${CurrencyUtil.formatRupiah(totalPrice)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                            Text(text = "→", fontSize = 16.sp, color = White)
                        }
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
                        BottomNavItem(label = "Absen", icon = Icons.AutoMirrored.Filled.Assignment, selected = false, onClick = onNavigateToAbsen)
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OmochaLogo(size = 32.dp)
                    Column {
                        Text(
                            text = "Catat Pesanan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "${viewModel.userName} • Kyoto-Matcha",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardBackground,
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { onNavigateToList() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Receipt,
                            contentDescription = "Daftar Pesanan",
                            modifier = Modifier.size(18.dp),
                            tint = PrimaryDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Source Selector Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Sumber Pesanan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Offline", "Shopee", "Gojek", "Grab").forEach { source ->
                            val isSelected = selectedSource == source
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) PrimaryDarkGreen else CardBackground,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setSelectedSource(source) }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = source,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) White else PrimaryDarkGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DDD0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Cari",
                        modifier = Modifier.size(18.dp),
                        tint = TextMuted
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text(text = "Cari menu...", color = TextMuted, fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Filter",
                        modifier = Modifier.size(18.dp),
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Chips (Minuman, Dessert)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Minuman", "Dessert").forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MatchaGreen else White,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DDD0)),
                        modifier = Modifier.clickable { viewModel.setSelectedCategory(category) }
                    ) {
                        Text(
                            text = category,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) White else PrimaryDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Grid (2 columns)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in filteredItems.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Item 1
                        val item1 = filteredItems[i]
                        MenuItemCard(
                            item = item1,
                            modifier = Modifier.weight(1f),
                            onAdd = { viewModel.addToCart(item1) }
                        )

                        // Item 2
                        if (i + 1 < filteredItems.size) {
                            val item2 = filteredItems[i + 1]
                            MenuItemCard(
                                item = item2,
                                modifier = Modifier.weight(1f),
                                onAdd = { viewModel.addToCart(item2) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem, modifier: Modifier = Modifier, onAdd: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Image placeholder
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(12.dp),
                color = CardBackground
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = item.imagePlaceholder, fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = CurrencyUtil.formatRupiah(item.price),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryDarkGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stok ${item.stock}",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE6F4EA),
                    modifier = Modifier.clickable { onAdd() }
                ) {
                    Text(
                        text = "Tambah +",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF137333)
                    )
                }
            }
        }
    }
}
