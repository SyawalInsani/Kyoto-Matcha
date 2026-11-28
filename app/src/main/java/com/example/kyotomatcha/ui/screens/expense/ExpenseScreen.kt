package com.example.kyotomatcha.ui.screens.expense

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
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
import com.example.kyotomatcha.ui.components.OmochaLogo
import com.example.kyotomatcha.ui.theme.*
import com.example.kyotomatcha.util.CurrencyUtil
import com.example.kyotomatcha.util.DateUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAbsen: () -> Unit,
    onNavigateToOrder: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onInventoryClick: () -> Unit,
    viewModel: ExpenseViewModel = viewModel()
) {
    val items by viewModel.items.collectAsState()
    val total by viewModel.total.collectAsState()
    val loaded by viewModel.loaded.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    LaunchedEffect(Unit) { viewModel.refresh() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userName = sessionManager.getUserName()

    var showCalendarDialog by remember { mutableStateOf(false) }
    var selectedDayFilter by remember { mutableStateOf<Int?>(null) }
    var selectedDateText by remember { mutableStateOf(DateUtil.getCurrentDateFormatted()) }

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
                    IconButton(onClick = { showCalendarDialog = true }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = "Pilih Tanggal", tint = PrimaryDarkGreen)
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
                    BottomNavItem(label = "Absen", icon = Icons.Filled.DateRange, selected = false, onClick = onNavigateToAbsen)
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
            // Date Filter Pill Row (Clickable)
            Row(
                modifier = Modifier.clickable { showCalendarDialog = true },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CardBackground
                ) {
                    Text(
                        text = if (selectedDayFilter == null) "Filter tanggal" else "Tanggal dipilih",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryDarkGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(text = selectedDateText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDarkGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Total pengeluaran ($selectedDateText)", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = CurrencyUtil.formatRupiah(total), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "${items.size} catatan · $selectedDateText", fontSize = 12.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Catatan pengeluaran", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                if (selectedDayFilter != null) {
                    Text(
                        text = "Reset filter",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MatchaGreen,
                        modifier = Modifier.clickable {
                            selectedDayFilter = null
                            selectedDateText = DateUtil.getCurrentDateFormatted()
                        }
                    )
                } else {
                    Text(text = "${items.size} catatan", fontSize = 12.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (loadError.isNotEmpty() && items.isEmpty()) {
                Text(text = loadError, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFFC53030))
            } else if (items.isEmpty()) {
                Text(text = if (loaded) "Belum ada pengeluaran hari ini." else "Memuat...", fontSize = 13.sp, color = TextMuted)
            }
            items.forEachIndexed { index, expense ->
                if (index > 0) Spacer(modifier = Modifier.height(12.dp))
                ExpenseItemCard(
                    title = expense.description,
                    category = expense.category_label,
                    amount = CurrencyUtil.formatRupiah(expense.amount),
                    time = "Dicatat ${expense.recorded_by} · ${expense.created_at.substring(11, 16)}",
                    date = DateUtil.isoToMedium(expense.expense_date),
                    branch = "Kyoto-Matcha"
                )
            }

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

        // Calendar Grid Dialog
        if (showCalendarDialog) {
            ExpenseCalendarGridDialog(
                initialYear = 2026,
                initialMonth = 10,
                initialDay = selectedDayFilter ?: 28,
                onDateSelected = { day, _, _, formattedDate ->
                    selectedDayFilter = day
                    selectedDateText = formattedDate
                },
                onResetFilter = {
                    selectedDayFilter = null
                    selectedDateText = DateUtil.getCurrentDateFormatted()
                },
                onDismiss = { showCalendarDialog = false }
            )
        }
    }
}

@Composable
fun ExpenseCalendarGridDialog(
    initialYear: Int = 2026,
    initialMonth: Int = 10,
    initialDay: Int? = 28,
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
                modifier = Modifier.weight(1f),
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
            Text(text = amount, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen, maxLines = 1, softWrap = false)
        }
    }
}
