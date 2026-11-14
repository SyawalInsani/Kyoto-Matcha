package com.example.kyotomatcha.ui.screens.attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.util.DateUtil
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.components.OmochaLogo
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun AbsensiScreen(
    onNavigateHome: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onInventoryClick: () -> Unit,
    viewModel: AttendanceViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userName = sessionManager.getUserName().ifEmpty { "Muhammad Syawal Insani" }
    val userRole = sessionManager.getUserRole()
    val currentDate = remember { DateUtil.getCurrentDayAndDateFormatted() }

    val status by viewModel.attendanceStatus.collectAsState()
    val hasCheckedIn by viewModel.hasCheckedIn.collectAsState()
    val hasCheckedOut by viewModel.hasCheckedOut.collectAsState()
    val scrollState = rememberScrollState()

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
                    BottomNavItem(label = "Absen", icon = Icons.AutoMirrored.Filled.Assignment, selected = true, onClick = onNavigateToAbsen)
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
                            text = "Absensi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "Kyoto-Matcha",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                // History Clock Icon
                Surface(
                    shape = CircleShape,
                    color = CardBackground,
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { onNavigateToHistory() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Riwayat",
                            modifier = Modifier.size(18.dp),
                            tint = PrimaryDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Time & GPS Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "08:00",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentDate,
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE6F4EA)
                ) {
                    Text(
                        text = "GPS valid",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF137333)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Map / Location Illustration Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                color = CardBackground
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Map,
                            contentDescription = "Peta",
                            modifier = Modifier.size(36.dp),
                            tint = PrimaryDarkGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ilustrasi lokasi • Dalam Radius 100m",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "$userName • $userRole",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Cabang", fontSize = 13.sp, color = TextMuted)
                        Text(text = "Kyoto-Matcha", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDarkGreen)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Jarak / radius izin", fontSize = 13.sp, color = TextMuted)
                        Text(text = "42 m / 100 m", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MatchaGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Status Kehadiran & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status kehadiran",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (status) {
                        "Selesai" -> Color(0xFFE6F4EA)
                        "Terlambat" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFF1F3F4)
                    }
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (status) {
                            "Selesai" -> Color(0xFF137333)
                            "Terlambat" -> Color(0xFFD97706)
                            else -> TextMuted
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Absen Masuk hanya tersedia dalam radius 100 m.",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.checkIn() },
                    enabled = !hasCheckedIn,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatchaGreen,
                        disabledContainerColor = Color(0xFFE2DDD0)
                    )
                ) {
                    Text(
                        text = if (hasCheckedIn) "Sudah Masuk" else "Absen Masuk",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (hasCheckedIn) TextMuted else White
                    )
                }

                Button(
                    onClick = { viewModel.checkOut() },
                    enabled = hasCheckedIn && !hasCheckedOut,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MatchaGreen,
                        disabledContainerColor = Color(0xFFE2DDD0)
                    )
                ) {
                    Text(
                        text = if (hasCheckedOut) "Selesai" else "Absen Pulang",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (hasCheckedOut) TextMuted else White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Riwayat Terbaru Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Riwayat terbaru",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                Text(
                    text = "Lihat semua",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MatchaGreen,
                    modifier = Modifier.clickable { onNavigateToHistory() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                        Text(
                            text = "02 Nov 2026",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "Terlambat",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Masuk 08:07 • Pulang 17:03 • Telat 7 mnt",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
