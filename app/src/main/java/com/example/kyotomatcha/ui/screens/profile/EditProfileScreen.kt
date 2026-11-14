package com.example.kyotomatcha.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.kyotomatcha.ui.theme.CardBackground
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val currentName by viewModel.name.collectAsState()
    val currentEmail by viewModel.email.collectAsState()
    val currentPhone by viewModel.phone.collectAsState()

    var nameInput by remember { mutableStateOf(currentName) }
    var emailInput by remember { mutableStateOf(currentEmail) }
    var phoneInput by remember { mutableStateOf(currentPhone) }

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
                            viewModel.updateProfile(nameInput, emailInput, phoneInput)
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
                            text = "Simpan perubahan",
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
                    text = "Ubah profil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar & Ubah foto
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = CardBackground,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "👩‍🍳", fontSize = 36.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ubah foto",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MatchaGreen,
                    modifier = Modifier.clickable { /* pick image */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields
            Text(text = "Nama lengkap", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MatchaGreen,
                    unfocusedBorderColor = Color(0xFFE2DDD0)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Email", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MatchaGreen,
                    unfocusedBorderColor = Color(0xFFE2DDD0)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Nomor telepon", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = phoneInput,
                onValueChange = { phoneInput = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MatchaGreen,
                    unfocusedBorderColor = Color(0xFFE2DDD0)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Read-only card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    ProfileInfoRow(label = "ID karyawan", value = "KM-002")
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileInfoRow(label = "Peran", value = "Kasir")
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileInfoRow(label = "Cabang", value = "Kyoto-Matcha")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Peran dan cabang hanya dapat diubah oleh pemilik.",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
