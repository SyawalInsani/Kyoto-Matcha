package com.example.kyotomatcha.ui.screens.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.components.OmochaLogo
import androidx.compose.ui.platform.LocalContext
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.util.DateUtil
import com.example.kyotomatcha.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onInventoryClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userName = sessionManager.getUserName()
    val currentDate = remember { DateUtil.getCurrentDateFormatted() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OmochaLogo(size = 32.dp)
                        Column {
                            Text(text = "Pengeluaran", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            Text(text = "$userName · Kyoto-Matcha", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Calendar", tint = PrimaryDarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundCream)
            )
        },
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
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CardBackground
                ) {
                    Text(
                        text = "Hari ini",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryDarkGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(text = currentDate, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Total pengeluaran hari ini", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Rp47.000", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "2 catatan · $currentDate", fontSize = 12.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Catatan pengeluaran", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                Text(text = "2 catatan", fontSize = 12.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExpenseItemCard(
                title = "Uang makan",
                category = "Uang makan",
                amount = "Rp30.000",
                time = "Dicatat $userName · 09:00",
                date = currentDate,
                branch = "Kyoto-Matcha"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExpenseItemCard(
                title = "Es batu",
                category = "Bahan",
                amount = "Rp17.000",
                time = "Dicatat $userName · 08:30",
                date = currentDate,
                branch = "Kyoto-Matcha"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Pengeluaran operasional dicatat terpisah dari penjualan.",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onNavigateToAddExpense,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
            ) {
                Text(text = "Catat Pengeluaran", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = White)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ExpenseItemCard(title: String, category: String, amount: String, time: String, date: String, branch: String) {
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CardBackground,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Receipt, contentDescription = null, tint = PrimaryDarkGreen, modifier = Modifier.size(22.dp))
                    }
                }
                Column {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = category, fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "$time · $date", fontSize = 11.sp, color = TextMuted)
                    Text(text = branch, fontSize = 11.sp, color = TextMuted)
                }
            }
            Text(text = amount, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
        }
    }
}
