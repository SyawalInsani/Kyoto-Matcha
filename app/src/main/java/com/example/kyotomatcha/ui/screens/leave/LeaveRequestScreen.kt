package com.example.kyotomatcha.ui.screens.leave

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.data.remote.LeaveDto
import com.example.kyotomatcha.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val localeId = Locale.forLanguageTag("id")

/** Tanggal yang bisa dipilih: besok sampai 14 hari ke depan (aturan mitra: izin paling lambat H-1). */
private fun selectableDays(): List<Pair<String, String>> {
    val value = SimpleDateFormat("yyyy-MM-dd", localeId)
    val label = SimpleDateFormat("EEE d MMM", localeId)
    val cal = Calendar.getInstance()
    return (1..14).map {
        cal.add(Calendar.DAY_OF_YEAR, 1)
        value.format(cal.time) to label.format(cal.time)
    }
}

/** "2026-11-17" -> "17 Nov 2026" */
internal fun formatLeaveDate(iso: String): String {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
    val parts = iso.take(10).split("-")
    val month = parts.getOrNull(1)?.toIntOrNull()?.let { months.getOrNull(it - 1) } ?: return iso
    return "${parts[2].toInt()} $month ${parts[0]}"
}

internal fun shiftLabel(shift: String) = if (shift == "pagi") "Shift Pagi" else "Shift Malam"

@Composable
internal fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) PrimaryDarkGreen else White,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DDD0)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) White else PrimaryDarkGreen,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
internal fun LeaveStatusChip(status: String) {
    val (bg, fg, text) = when (status) {
        "disetujui" -> Triple(Color(0xFFC6F6D5), Color(0xFF22543D), "Disetujui")
        "ditolak" -> Triple(Color(0xFFFED7D7), Color(0xFF9B2C2C), "Ditolak")
        else -> Triple(Color(0xFFFEFCBF), Color(0xFFB7791F), "Menunggu")
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bg) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = fg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRequestScreen(
    onNavigateBack: () -> Unit,
    viewModel: LeaveViewModel = viewModel()
) {
    val days = remember { selectableDays() }
    val staff by viewModel.staff.collectAsState()
    val myLeaves by viewModel.myLeaves.collectAsState()
    val saving by viewModel.isSaving.collectAsState()
    val loaded by viewModel.loaded.collectAsState()
    val message by viewModel.message.collectAsState()

    var date by remember { mutableStateOf<String?>(null) }
    var shift by remember { mutableStateOf("pagi") }
    var reason by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf("none") }
    var coveredBy by remember { mutableStateOf<Int?>(null) }
    var replacementName by remember { mutableStateOf("") }
    var staffMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.clearMessage() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Izin Tidak Hadir", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text("Diajukan paling lambat H-1, disetujui pemilik", fontSize = 12.sp, color = TextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = PrimaryDarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundCream)
            )
        },
        containerColor = BackgroundCream
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Tanggal", fontSize = 12.sp, color = TextMuted)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                days.forEach { (value, label) -> Chip(label, date == value) { date = value } }
            }

            Spacer(Modifier.height(16.dp))
            Text("Shift", fontSize = 12.sp, color = TextMuted)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("Shift Pagi (10:00-17:00)", shift == "pagi") { shift = "pagi" }
                Chip("Shift Malam", shift == "malam") { shift = "malam" }
            }

            Spacer(Modifier.height(16.dp))
            Text("Alasan", fontSize = 12.sp, color = TextMuted)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text("Pengganti", fontSize = 12.sp, color = TextMuted)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("Tanpa pengganti", kind == "none") { kind = "none" }
                Chip("Rekan (double shift)", kind == "rekan") { kind = "rekan" }
                Chip("Orang lain", kind == "luar") { kind = "luar" }
            }

            if (kind == "rekan") {
                Spacer(Modifier.height(10.dp))
                Box {
                    OutlinedTextField(
                        value = staff.firstOrNull { it.id == coveredBy }?.name ?: "Pilih rekan",
                        onValueChange = {},
                        readOnly = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(Modifier.matchParentSize().clickable { staffMenu = true })
                    DropdownMenu(expanded = staffMenu, onDismissRequest = { staffMenu = false }) {
                        if (staff.isEmpty()) {
                            DropdownMenuItem(text = { Text("Belum ada rekan lain") }, onClick = { staffMenu = false })
                        }
                        staff.forEach {
                            DropdownMenuItem(
                                text = { Text("${it.name} (${it.role})") },
                                onClick = {
                                    coveredBy = it.id
                                    staffMenu = false
                                }
                            )
                        }
                    }
                }
            }
            if (kind == "luar") {
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = replacementName,
                    onValueChange = { replacementName = it },
                    placeholder = { Text("Nama pengganti (mis. adik)", color = TextMuted) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (message.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message.removePrefix("!"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (message.startsWith("!")) Color(0xFFC53030) else Color(0xFF137333)
                )
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.submit(date, shift, reason, kind, coveredBy, replacementName) {
                        reason = ""
                        date = null
                        kind = "none"
                        coveredBy = null
                        replacementName = ""
                    }
                },
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
            ) {
                Text(if (saving) "Mengirim..." else "Ajukan Izin", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = White)
            }

            Spacer(Modifier.height(28.dp))
            Text("Izin saya", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
            Spacer(Modifier.height(10.dp))
            if (myLeaves.isEmpty()) {
                Text(if (loaded) "Belum ada pengajuan izin." else "Memuat...", fontSize = 13.sp, color = TextMuted)
            }
            myLeaves.forEach { LeaveCard(it, showUser = false) { } ; Spacer(Modifier.height(10.dp)) }
            Spacer(Modifier.height(20.dp))
        }
    }
}

/** Kartu izin. actions dipakai pemilik (tombol setuju/tolak); kosong untuk karyawan. */
@Composable
internal fun LeaveCard(leave: LeaveDto, showUser: Boolean, actions: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = White, shadowElevation = 1.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = if (showUser) (leave.user_name ?: "Karyawan") else formatLeaveDate(leave.date),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.weight(1f)
                )
                LeaveStatusChip(leave.status)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = (if (showUser) "${formatLeaveDate(leave.date)} · " else "") + shiftLabel(leave.shift),
                fontSize = 12.sp,
                color = TextMuted
            )
            Spacer(Modifier.height(6.dp))
            Text(leave.reason, fontSize = 13.sp, color = PrimaryDarkGreen)
            val replacement = leave.covered_by_name?.let { "Pengganti: $it (rekan)" }
                ?: leave.replacement_name?.let { "Pengganti: $it" }
            if (replacement != null) {
                Spacer(Modifier.height(4.dp))
                Text(replacement, fontSize = 12.sp, color = TextMuted)
            }
            if (leave.status == "ditolak" && !leave.reject_reason.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("Alasan ditolak: ${leave.reject_reason}", fontSize = 12.sp, color = Color(0xFF9B2C2C))
            }
            actions()
        }
    }
}
