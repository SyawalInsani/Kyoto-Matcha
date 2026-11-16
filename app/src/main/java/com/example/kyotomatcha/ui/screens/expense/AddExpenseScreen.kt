package com.example.kyotomatcha.ui.screens.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kyotomatcha.ui.theme.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.data.local.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExpenseViewModel = viewModel()
) {
    val context = LocalContext.current
    val userName = remember { SessionManager(context).getUserName() }
    val saving by viewModel.isSaving.collectAsState()
    val formError by viewModel.formError.collectAsState()
    LaunchedEffect(Unit) { viewModel.clearError() }
    var selectedKategori by remember { mutableStateOf("Bahan") }
    var keterangan by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var dateChoice by remember { mutableStateOf("today") } // today | yesterday
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Catat Pengeluaran", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
                        Text(text = "Kyoto-Matcha · $userName", fontSize = 12.sp, color = TextMuted)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryDarkGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundCream)
            )
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
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CardBackground,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Pengeluaran operasional",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Text(text = "Kolom bertanda * wajib diisi.", fontSize = 12.sp, color = TextMuted)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Kategori *", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Uang makan", "Bahan", "Lainnya").forEach { kat ->
                    val isSelected = selectedKategori == kat
                    Button(
                        onClick = { selectedKategori = kat },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryDarkGreen else White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = kat, color = if (isSelected) White else PrimaryDarkGreen, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Keterangan *", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Nominal (Rp) *", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = nominal,
                onValueChange = { nominal = it.filter { c -> c.isDigit() } },
                placeholder = { Text("Contoh: 17000", color = TextMuted) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Tanggal *", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("today" to "Hari ini", "yesterday" to "Kemarin").forEach { (key, label) ->
                    val isSelected = dateChoice == key
                    Button(
                        onClick = { dateChoice = key },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) PrimaryDarkGreen else White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = label, color = if (isSelected) White else PrimaryDarkGreen, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Dicatat oleh $userName untuk Kyoto-Matcha.", fontSize = 12.sp, color = TextMuted)

            if (formError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = formError, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = androidx.compose.ui.graphics.Color(0xFFC53030))
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    viewModel.submit(selectedKategori, keterangan, nominal, dateChoice == "yesterday") { onNavigateBack() }
                },
                enabled = !saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
            ) {
                Text(text = if (saving) "Menyimpan..." else "Simpan Pengeluaran", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = White)
            }
        }
    }
}
