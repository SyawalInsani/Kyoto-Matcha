package com.example.kyotomatcha.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val reminderAbsen by viewModel.reminderAbsen.collectAsState()
    val stockWarning by viewModel.stockWarning.collectAsState()
    val stockMovement by viewModel.stockMovement.collectAsState()
    val salesSummary by viewModel.salesSummary.collectAsState()

    var absenVal by remember { mutableStateOf(reminderAbsen) }
    var stockWarnVal by remember { mutableStateOf(stockWarning) }
    var stockMoveVal by remember { mutableStateOf(stockMovement) }
    var salesSumVal by remember { mutableStateOf(salesSummary) }

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
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            viewModel.toggleReminderAbsen(absenVal)
                            viewModel.toggleStockWarning(stockWarnVal)
                            viewModel.toggleStockMovement(stockMoveVal)
                            viewModel.toggleSalesSummary(salesSumVal)
                            onNavigateBack()
                        },
                    shape = RoundedCornerShape(24.dp),
                    color = MatchaGreen,
                    shadowElevation = 6.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Simpan pengaturan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.clickable { onNavigateBack() }
                )
                Text(
                    text = "Pengaturan notifikasi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pilih pembaruan yang ingin Anda terima.",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Operasional",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notification Toggles Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    NotificationSwitchRow(
                        title = "Pengingat absensi",
                        subtitle = "Pengingat masuk dan pulang kerja",
                        checked = absenVal,
                        onCheckedChange = { absenVal = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(12.dp))
                    NotificationSwitchRow(
                        title = "Peringatan stok menipis",
                        subtitle = "Bahan menipis atau habis",
                        checked = stockWarnVal,
                        onCheckedChange = { stockWarnVal = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(12.dp))
                    NotificationSwitchRow(
                        title = "Pergerakan stok",
                        subtitle = "Penerimaan dan penggunaan bahan",
                        checked = stockMoveVal,
                        onCheckedChange = { stockMoveVal = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(12.dp))
                    NotificationSwitchRow(
                        title = "Ringkasan penjualan",
                        subtitle = "Ringkasan penjualan harian",
                        checked = salesSumVal,
                        onCheckedChange = { salesSumVal = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Penyampaian notifikasi",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Notifikasi muncul di aplikasi. Izin notifikasi Android dapat diatur melalui pengaturan perangkat.",
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun NotificationSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = MatchaGreen
            )
        )
    }
}
