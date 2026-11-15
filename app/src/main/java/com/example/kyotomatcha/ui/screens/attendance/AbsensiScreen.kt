package com.example.kyotomatcha.ui.screens.attendance

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.kyotomatcha.data.location.LocationHelper
import com.example.kyotomatcha.data.location.Outlet
import com.example.kyotomatcha.util.DateUtil
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.components.OmochaLogo
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

private fun currentClock(): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

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
    val shift by viewModel.selectedShift.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val gpsStatus by viewModel.gpsStatus.collectAsState()
    val gpsValid by viewModel.gpsValid.collectAsState()
    val message by viewModel.message.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val scrollState = rememberScrollState()

    // Jam berjalan (jam HP).
    var clock by remember { mutableStateOf(currentClock()) }
    LaunchedEffect(Unit) {
        while (true) {
            clock = currentClock()
            delay(1000)
        }
    }

    // Izin lokasi: diminta saat layar dibuka, lalu lokasi dan catatan absen diambil.
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        viewModel.refresh()
    }
    LaunchedEffect(Unit) {
        if (LocationHelper.hasPermission(context)) viewModel.refresh()
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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
                        text = clock,
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
                    color = if (gpsValid) Color(0xFFE6F4EA) else Color(0xFFFDE8E8)
                ) {
                    Text(
                        text = gpsStatus,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (gpsValid) Color(0xFF137333) else Color(0xFFC53030)
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
                            text = distance?.let { "Jarak ke kedai $it m • radius ${Outlet.RADIUS_M} m" } ?: gpsStatus,
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
                        Text(
                            text = "${distance?.toString() ?: "-"} m / ${Outlet.RADIUS_M} m",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (gpsValid) MatchaGreen else Color(0xFFC53030)
                        )
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
                        "Selesai", "Tepat waktu" -> Color(0xFFE6F4EA)
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
                            "Selesai", "Tepat waktu" -> Color(0xFF137333)
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

            Spacer(modifier = Modifier.height(12.dp))

            // Pilihan shift: pagi 10:00-17:00, malam 17:00-23:30.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("pagi" to "Shift Pagi", "malam" to "Shift Malam").forEach { (key, label) ->
                    val selected = shift == key
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (selected) MatchaGreen else CardBackground,
                        modifier = Modifier.clickable { viewModel.selectShift(key) }
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (selected) White else PrimaryDarkGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.checkIn() },
                    enabled = !hasCheckedIn && !isBusy,
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
                    enabled = hasCheckedIn && !hasCheckedOut && !isBusy,
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

            // Pesan dari server (awalan "!" = gagal).
            if (message.isNotEmpty()) {
                val failed = message.startsWith("!")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message.removePrefix("!"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (failed) Color(0xFFC53030) else Color(0xFF137333)
                )
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
