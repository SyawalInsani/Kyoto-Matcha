package com.example.kyotomatcha.ui.screens.attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.data.local.SessionManager
import com.example.kyotomatcha.ui.components.BottomNavItem
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class AttendanceRecordItem(
    val id: String,
    val day: Int,
    val month: Int, // 0-based: 10 = Nov, 11 = Des
    val year: Int,  // 2026
    val date: String, // "03 Nov 2026"
    val monthYearTitle: String, // "November 2026", "Desember 2026"
    val detail: String,
    val status: String,
    val statusColor: Color,
    val statusBg: Color
)

@Composable
fun AttendanceHistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onInventoryClick: () -> Unit,
    viewModel: AttendanceViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userName = sessionManager.getUserName().ifEmpty { "Muhammad Syawal Insani" }
    val userRole = sessionManager.getUserRole()

    val selectedTab by viewModel.selectedTab.collectAsState()
    val scrollState = rememberScrollState()

    var showCalendarDialog by remember { mutableStateOf(false) }
    var selectedDayFilter by remember { mutableStateOf<Int?>(null) }
    var selectedMonthFilter by remember { mutableStateOf<Int?>(null) }
    var selectedYearFilter by remember { mutableStateOf<Int?>(null) }
    var selectedDateRangeText by remember { mutableStateOf("Semua Tanggal") }
    var displayedMonthTitle by remember { mutableStateOf("November 2026") }

    val allRecords = remember {
        listOf(
            AttendanceRecordItem("1", 3, 10, 2026, "03 Nov 2026", "November 2026", "Masuk 08:00 • Pulang 17:05 • Telat 0 mnt", "Selesai", Color(0xFF137333), Color(0xFFE6F4EA)),
            AttendanceRecordItem("2", 2, 10, 2026, "02 Nov 2026", "November 2026", "Masuk 08:07 • Pulang 17:03 • Telat 7 mnt", "Terlambat", Color(0xFFD97706), Color(0xFFFEF3C7)),
            AttendanceRecordItem("3", 1, 10, 2026, "01 Nov 2026", "November 2026", "Masuk — • Pulang — • Telat 0 mnt", "Tidak Hadir", Color(0xFFC53030), Color(0xFFFDE8E8)),
            AttendanceRecordItem("4", 1, 11, 2026, "01 Des 2026", "Desember 2026", "Masuk 08:00 • Pulang 17:00 • Telat 0 mnt", "Selesai", Color(0xFF137333), Color(0xFFE6F4EA))
        )
    }

    val filteredRecords = allRecords.filter { record ->
        val matchesTab = when (selectedTab) {
            "Hadir" -> record.status == "Selesai"
            "Terlambat" -> record.status == "Terlambat"
            "Tidak Hadir" -> record.status == "Tidak Hadir"
            else -> true
        }
        val matchesDay = selectedDayFilter == null || record.day == selectedDayFilter
        val matchesMonth = selectedMonthFilter == null || record.month == selectedMonthFilter
        val matchesYear = selectedYearFilter == null || record.year == selectedYearFilter
        matchesTab && matchesDay && matchesMonth && matchesYear
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
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = PrimaryDarkGreen
                        )
                    }
                    Column {
                        Text(
                            text = "Riwayat Absensi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "$userName • $userRole",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                IconButton(onClick = { showCalendarDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Pilih Tanggal",
                        tint = PrimaryDarkGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Filter Pill
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCalendarDialog = true },
                shape = RoundedCornerShape(16.dp),
                color = White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DDD0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Filter tanggal", fontSize = 13.sp, color = TextMuted)
                    Text(text = selectedDateRangeText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDarkGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs (Semua, Hadir, Terlambat, Tidak Hadir)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HistoryTab(label = "Semua", selected = selectedTab == "Semua") { viewModel.setSelectedTab("Semua") }
                HistoryTab(label = "Hadir", selected = selectedTab == "Hadir") { viewModel.setSelectedTab("Hadir") }
                HistoryTab(label = "Terlambat", selected = selectedTab == "Terlambat") { viewModel.setSelectedTab("Terlambat") }
                HistoryTab(label = "Tidak Hadir", selected = selectedTab == "Tidak Hadir") { viewModel.setSelectedTab("Tidak Hadir") }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayedMonthTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                if (selectedDayFilter != null || selectedMonthFilter != null) {
                    Text(
                        text = "Reset filter",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MatchaGreen,
                        modifier = Modifier.clickable {
                            selectedDayFilter = null
                            selectedMonthFilter = null
                            selectedYearFilter = null
                            selectedDateRangeText = "Semua Tanggal"
                            displayedMonthTitle = "November 2026"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // History List Card
            if (filteredRecords.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = White,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tidak ada riwayat absensi untuk tanggal ini.",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                selectedDayFilter = null
                                selectedMonthFilter = null
                                selectedYearFilter = null
                                selectedDateRangeText = "Semua Tanggal"
                                displayedMonthTitle = "November 2026"
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = "Tampilkan Semua Tanggal", color = PrimaryDarkGreen, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        filteredRecords.forEachIndexed { index, record ->
                            HistoryItemRow(
                                date = record.date,
                                detail = record.detail,
                                status = record.status,
                                statusColor = record.statusColor,
                                statusBg = record.statusBg
                            )
                            if (index < filteredRecords.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFFF0EFEA))
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Kehadiran bulan ini Summary Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = CardBackground
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
                            text = "Kehadiran bulan ini",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        Text(
                            text = "1 / 1 / 1",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hadir / Terlambat / Tidak Hadir",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "3 Nov: masuk tepat waktu, selesai 17:05.\n2 Nov: terlambat 7 menit.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Calendar Grid Pop-up Dialog
        if (showCalendarDialog) {
            CalendarGridDialog(
                initialYear = selectedYearFilter ?: 2026,
                initialMonth = selectedMonthFilter ?: 10, // November (0-based)
                initialDay = selectedDayFilter,
                onDateSelected = { day, month, year, formattedDate ->
                    selectedDayFilter = day
                    selectedMonthFilter = month
                    selectedYearFilter = year
                    selectedDateRangeText = formattedDate

                    val cal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                    displayedMonthTitle = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id")).format(cal.time)
                },
                onResetFilter = {
                    selectedDayFilter = null
                    selectedMonthFilter = null
                    selectedYearFilter = null
                    selectedDateRangeText = "Semua Tanggal"
                    displayedMonthTitle = "November 2026"
                },
                onDismiss = { showCalendarDialog = false }
            )
        }
    }
}

@Composable
fun CalendarGridDialog(
    initialYear: Int = 2026,
    initialMonth: Int = 10,
    initialDay: Int? = 3,
    onDateSelected: (day: Int, month: Int, year: Int, formattedDate: String) -> Unit,
    onResetFilter: () -> Unit,
    onDismiss: () -> Unit
) {
    var displayedCalendar by remember {
        mutableStateOf(
            Calendar.getInstance().apply {
                set(Calendar.YEAR, initialYear)
                set(Calendar.MONTH, initialMonth)
                set(Calendar.DAY_OF_MONTH, 1)
            }
        )
    }

    var selectedDay by remember { mutableStateOf(initialDay) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    var selectedYear by remember { mutableStateOf(initialYear) }

    val year = displayedCalendar.get(Calendar.YEAR)
    val month = displayedCalendar.get(Calendar.MONTH)
    val monthName = remember(displayedCalendar.timeInMillis) {
        SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id")).format(displayedCalendar.time)
    }

    val firstDayOfWeek = (displayedCalendar.get(Calendar.DAY_OF_WEEK) - 1) % 7
    val daysInMonth = displayedCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PILIH TANGGAL",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = PrimaryDarkGreen)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Month Header with Prev/Next Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val newCal = displayedCalendar.clone() as Calendar
                            newCal.add(Calendar.MONTH, -1)
                            displayedCalendar = newCal
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NavigateBefore, contentDescription = "Bulan Lalu", tint = PrimaryDarkGreen)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CardBackground
                    ) {
                        Text(
                            text = monthName,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    }

                    IconButton(
                        onClick = {
                            val newCal = displayedCalendar.clone() as Calendar
                            newCal.add(Calendar.MONTH, 1)
                            displayedCalendar = newCal
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.NavigateNext, contentDescription = "Bulan Depan", tint = PrimaryDarkGreen)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Days of week header (Min, Sen, Sel, Rab, Kam, Jum, Sab)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val daysOfWeek = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
                    daysOfWeek.forEachIndexed { idx, dayName ->
                        val isWeekend = idx == 0 || idx == 6
                        Text(
                            text = dayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWeekend) Color(0xFFE53E3E) else TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Grid with empty offset for starting day of week
                val gridCells = mutableListOf<Int?>()
                repeat(firstDayOfWeek) { gridCells.add(null) }
                for (d in 1..daysInMonth) { gridCells.add(d) }

                val rows = gridCells.chunked(7)

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rows.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            week.forEach { dayNum ->
                                if (dayNum == null) {
                                    Spacer(modifier = Modifier.size(32.dp))
                                } else {
                                    val isSelected = selectedDay == dayNum && selectedMonth == month && selectedYear == year
                                    Surface(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable {
                                                selectedDay = dayNum
                                                selectedMonth = month
                                                selectedYear = year
                                                val cal = Calendar.getInstance().apply {
                                                    set(Calendar.YEAR, year)
                                                    set(Calendar.MONTH, month)
                                                    set(Calendar.DAY_OF_MONTH, dayNum)
                                                }
                                                val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id")).format(cal.time)
                                                onDateSelected(dayNum, month, year, formattedDate)
                                            },
                                        shape = CircleShape,
                                        color = if (isSelected) Color(0xFF1976D2) else Color.Transparent
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = dayNum.toString(),
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) White else PrimaryDarkGreen
                                            )
                                        }
                                    }
                                }
                            }
                            repeat(7 - week.size) {
                                Spacer(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedDay = null
                        onResetFilter()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "Semua Tanggal", color = PrimaryDarkGreen, fontSize = 12.sp)
                }

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                ) {
                    Text(text = "FILTER", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = White
    )
}

@Composable
fun HistoryTab(label: String, selected: Boolean, onClick: () -> Unit) {
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
fun HistoryItemRow(date: String, detail: String, status: String, statusColor: Color, statusBg: Color) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusBg
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusColor
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = detail,
            fontSize = 12.sp,
            color = TextMuted
        )
    }
}
