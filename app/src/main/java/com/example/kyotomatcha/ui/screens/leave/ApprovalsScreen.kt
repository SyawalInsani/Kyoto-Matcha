package com.example.kyotomatcha.ui.screens.leave

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
import com.example.kyotomatcha.data.remote.UserDto
import com.example.kyotomatcha.ui.theme.*

/** Khusus pemilik: izin tidak hadir yang menunggu dan pendaftar akun baru. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovalsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ApprovalViewModel = viewModel()
) {
    val leaves by viewModel.leaves.collectAsState()
    val users by viewModel.users.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    // Izin yang sedang ditolak (menunggu alasan dari pemilik).
    var rejectingId by remember { mutableStateOf<Int?>(null) }
    var rejectReason by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.reload() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Persetujuan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text("Izin tidak hadir dan pendaftar baru", fontSize = 12.sp, color = TextMuted)
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
            if (message.isNotEmpty()) {
                Text(
                    text = message.removePrefix("!"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (message.startsWith("!")) Color(0xFFC53030) else Color(0xFF137333)
                )
                Spacer(Modifier.height(12.dp))
            }

            Text("Izin menunggu persetujuan (${leaves.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
            Spacer(Modifier.height(10.dp))
            if (leaves.isEmpty()) {
                Text(if (loading) "Memuat..." else "Tidak ada izin yang menunggu.", fontSize = 13.sp, color = TextMuted)
            }
            leaves.forEach { leave ->
                LeaveCard(leave, showUser = true) {
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                rejectingId = leave.id
                                rejectReason = ""
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC53030)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC53030))
                        ) { Text("Tolak") }
                        Button(
                            onClick = { viewModel.decideLeave(leave.id, true) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                        ) { Text("Setujui", color = White) }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(24.dp))
            Text("Pendaftar baru (${users.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
            Spacer(Modifier.height(10.dp))
            if (users.isEmpty()) {
                Text(if (loading) "Memuat..." else "Tidak ada pendaftar yang menunggu.", fontSize = 13.sp, color = TextMuted)
            }
            users.forEach { user ->
                PendingUserCard(user, onApprove = { role -> viewModel.decideUser(user.id, true, role) }, onReject = { viewModel.decideUser(user.id, false, "karyawan") })
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    if (rejectingId != null) {
        AlertDialog(
            onDismissRequest = { rejectingId = null },
            title = { Text("Tolak izin?", fontWeight = FontWeight.Bold, color = PrimaryDarkGreen) },
            text = {
                OutlinedTextField(
                    value = rejectReason,
                    onValueChange = { rejectReason = it },
                    placeholder = { Text("Alasan (opsional)", color = TextMuted) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        rejectingId?.let { viewModel.decideLeave(it, false, rejectReason) }
                        rejectingId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC53030))
                ) { Text("Tolak Izin", color = White) }
            },
            dismissButton = { TextButton(onClick = { rejectingId = null }) { Text("Batal", color = PrimaryDarkGreen) } },
            containerColor = White
        )
    }
}

@Composable
private fun PendingUserCard(user: UserDto, onApprove: (role: String) -> Unit, onReject: () -> Unit) {
    var role by remember { mutableStateOf("karyawan") }
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = White, shadowElevation = 1.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(user.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
            Text(user.email, fontSize = 12.sp, color = TextMuted)
            Spacer(Modifier.height(10.dp))
            Text("Peran", fontSize = 12.sp, color = TextMuted)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("Karyawan", role == "karyawan") { role = "karyawan" }
                Chip("Kasir", role == "kasir") { role = "kasir" }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC53030)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC53030))
                ) { Text("Tolak") }
                Button(
                    onClick = { onApprove(role) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                ) { Text("Setujui", color = White) }
            }
        }
    }
}
