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
import com.example.kyotomatcha.ui.components.OmochaLogo
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun AboutAppScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    text = "Tentang Aplikasi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    OmochaLogo(size = 48.dp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "O-MACHA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Operasional kedai, lebih mudah.",
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE6F4EA)
            ) {
                Text(
                    text = "Versi 1.0.0",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF137333)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Operasional kedai dalam satu aplikasi",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "O-MACHA adalah aplikasi operasional kedai Kyoto-Matcha untuk absensi berbasis lokasi, pencatatan pesanan dan pembayaran QRIS, inventaris, serta laporan harian.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF0EFEA))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Cabang", fontSize = 13.sp, color = TextMuted)
                        Text(text = "Kyoto-Matcha", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDarkGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Options Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    ProfileMenuRow(title = "Kebijakan Privasi", onClick = onNavigateToPrivacy)
                    ProfileMenuRow(title = "Syarat & Ketentuan", onClick = onNavigateToTerms)
                    AboutInfoMenuRow(title = "Pembuat aplikasi", value = "Tim O-MACHA")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "O-MACHA • 1 cabang • 3 karyawan", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Sekitar 30 pesanan per hari", fontSize = 12.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun AboutInfoMenuRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
        Text(text = value, fontSize = 14.sp, color = TextMuted)
    }
}
