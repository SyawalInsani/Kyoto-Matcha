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
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun PrivacyPolicyScreen(
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
                    text = "Kebijakan Privasi",
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
                    text = "Draf kebijakan privasi",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Privasi Anda penting",
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
                    text = "Draf ini menjelaskan pengelolaan data operasional O-MACHA. Kebijakan perlu ditinjau dan disetujui pemilik sebelum diterapkan.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            PolicySectionItem(
                number = "01",
                title = "Informasi akun",
                body = "Nama, email, nomor telepon, peran, dan cabang digunakan untuk mengelola akses akun. Pemilik menentukan peran dan cabang setiap karyawan."
            )

            Spacer(modifier = Modifier.height(16.dp))

            PolicySectionItem(
                number = "02",
                title = "Lokasi absensi",
                body = "Lokasi digunakan saat absensi untuk memeriksa keberadaan di kedai. Izin lokasi diminta melalui perangkat; aturan penyimpanan ditetapkan pemilik."
            )

            Spacer(modifier = Modifier.height(16.dp))

            PolicySectionItem(
                number = "03",
                title = "Akses dan bantuan",
                body = "Hubungi Aulia Putri selaku pemilik untuk meninjau atau memperbarui data akun. Ketentuan akses dan masa penyimpanan perlu disahkan sebelum penggunaan."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tampilan desain ini tidak mengumpulkan data pribadi.",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun PolicySectionItem(number: String, title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$number • $title",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryDarkGreen
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = body,
            fontSize = 13.sp,
            color = TextMuted,
            lineHeight = 18.sp
        )
    }
}
