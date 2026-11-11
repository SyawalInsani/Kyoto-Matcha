package com.example.kyotomatcha.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun DashboardScreen(
    onBranchClick: () -> Unit,
    onReportClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAbsenClick: () -> Unit,
    onOrderClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val selectedBranch by viewModel.selectedBranch.collectAsState()
    val isExpanded by viewModel.isDropdownExpanded.collectAsState()
    val branches = viewModel.branches
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            // Bottom Navigation Bar matching reference image
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
                    BottomNavItem(label = "Beranda", icon = Icons.Filled.Home, selected = true)
                    BottomNavItem(label = "Absen", icon = Icons.AutoMirrored.Filled.Assignment, selected = false, onClick = onAbsenClick)
                    BottomNavItem(label = "Pesanan", icon = Icons.Filled.ShoppingBag, selected = false, onClick = onOrderClick)
                    BottomNavItem(label = "Inventaris", icon = Icons.Filled.Inventory2, selected = false)
                    BottomNavItem(label = "Profil", icon = Icons.Filled.Person, selected = false, onClick = onProfileClick)
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
                            text = "Kyoto Matcha",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "Pemilik • 1 cabang",
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
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifikasi",
                            modifier = Modifier.size(18.dp),
                            tint = PrimaryDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Greeting & User Profile Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = CardBackground
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Selamat pagi, $userName",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pemilik • 3 Nov 2026",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    // Avatar placeholder
                    Surface(
                        shape = CircleShape,
                        color = MatchaGreen,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = userName.take(1).uppercase(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Branch Dropdown Card (Clickable to open BusinessSummaryScreen)
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setDropdownExpanded(!isExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    color = White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DDD0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Cabang aktif",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedBranch,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryDarkGreen
                            )
                        }
                        Text(text = "▼", fontSize = 12.sp, color = TextMuted)
                    }
                }

                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { viewModel.setDropdownExpanded(false) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(White)
                ) {
                    branches.forEach { branch ->
                        DropdownMenuItem(
                            text = { Text(text = branch, color = PrimaryDarkGreen, fontSize = 14.sp) },
                            onClick = {
                                viewModel.selectBranch(branch)
                                onBranchClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Grid (2x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Penjualan hari ini
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = White
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Penjualan hari ini", fontSize = 12.sp, color = TextMuted)
                        Column {
                            Text(text = "Rp758.020", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "30 pesanan lunas / 30 gelas", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }

                // Card 2: Pesanan menunggu bayar
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = White
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Pesanan menunggu bayar", fontSize = 12.sp, color = TextMuted)
                        Column {
                            Text(text = "2", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Belum tercatat dalam penjualan", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 3: Karyawan hadir
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = White
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Karyawan hadir", fontSize = 12.sp, color = TextMuted)
                        Column {
                            Text(text = "3 / 3", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "2 tepat waktu - 1 terlambat", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }

                // Card 4: Bahan hampir habis
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = White
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Bahan hampir habis", fontSize = 12.sp, color = TextMuted)
                        Column {
                            Text(text = "2", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC53030))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Perlu ditambah hari ini", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Aksi Cepat Section
            Text(
                text = "Aksi cepat",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    QuickActionButton(label = "Absen", icon = Icons.Filled.LocationOn, onClick = onAbsenClick, modifier = Modifier.weight(1f))
                    QuickActionButton(label = "Catat\nPesanan", icon = Icons.Filled.Add, onClick = onOrderClick, modifier = Modifier.weight(1f))
                    QuickActionButton(label = "Inventaris", icon = Icons.Filled.Inventory2, modifier = Modifier.weight(1f))
                    QuickActionButton(label = "Laporan", icon = Icons.Filled.BarChart, onClick = onReportClick, modifier = Modifier.weight(1f))
                    QuickActionButton(label = "Pengeluaran", icon = Icons.AutoMirrored.Filled.TrendingUp, modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ringkasan Absensi Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ringkasan absensi",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "100%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MatchaGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress bar visual representation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        Box(modifier = Modifier
                            .weight(0.66f)
                            .fillMaxHeight()
                            .background(MatchaGreen))
                        Box(modifier = Modifier
                            .weight(0.34f)
                            .fillMaxHeight()
                            .background(Color(0xFFE2DDD0)))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "2 Tepat waktu", fontSize = 11.sp, color = MatchaGreen)
                        Text(text = "1 Terlambat", fontSize = 11.sp, color = Color(0xFFD97706))
                        Text(text = "0 Tidak hadir", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick?.invoke() }
    ) {
        Surface(
            shape = CircleShape,
            color = CardBackground,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(20.dp),
                    tint = PrimaryDarkGreen
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = PrimaryDarkGreen,
            maxLines = 2,
            textAlign = TextAlign.Center,
            lineHeight = 11.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
