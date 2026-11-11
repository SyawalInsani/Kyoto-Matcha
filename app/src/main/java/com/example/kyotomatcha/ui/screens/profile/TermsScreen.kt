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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun TermsScreen(
    onNavigateBack: () -> Unit
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
                    text = "Syarat & Ketentuan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardBackground
            ) {
                Text(
                    text = "Draf syarat penggunaan",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Bekerja dengan tertib",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Diperbarui 3 November 2026",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Text(
                    text = "Draf ini menjelaskan penggunaan O-MACHA oleh tim Kyoto Matcha Senopati. Ketentuan perlu ditinjau dan disetujui pemilik sebelum diterapkan.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            PolicySectionItem(
                number = "01",
                title = "Penggunaan akun",
                body = "Akun digunakan oleh karyawan yang disetujui pemilik. Jaga kerahasiaan kata sandi dan catat aktivitas operasional secara benar."
            )

            Spacer(modifier = Modifier.height(16.dp))

            PolicySectionItem(
                number = "02",
                title = "Catatan operasional",
                body = "Pemilik meninjau catatan transaksi, absensi, dan stok. Laporkan kesalahan kepada pemilik agar koreksi mengikuti prosedur kedai."
            )

            Spacer(modifier = Modifier.height(16.dp))

            PolicySectionItem(
                number = "03",
                title = "Informasi layanan",
                body = "O-MACHA mendukung absensi berbasis lokasi, pencatatan pesanan, QRIS, stok, dan laporan. Desain ini belum memproses lokasi atau pembayaran nyata."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Baca Kebijakan Privasi untuk informasi data akun.",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
