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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val hasMinLength = newPass.length >= 8
    val hasLetterAndNumber = newPass.any { it.isLetter() } && newPass.any { it.isDigit() }
    val isDifferent = newPass.isNotEmpty() && newPass != oldPass

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
                        .clickable { onNavigateBack() },
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
                            text = "Simpan kata sandi",
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
                    text = "Ubah kata sandi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Jaga keamanan akun",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gunakan kata sandi unik dan jangan bagikan kepada siapa pun.",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Kata sandi saat ini", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = oldPass,
                onValueChange = { oldPass = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = { Text(text = "👁️", fontSize = 16.sp) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Kata sandi baru", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = { Text(text = "👁️", fontSize = 16.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MatchaGreen,
                    unfocusedBorderColor = Color(0xFFE2DDD0)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Konfirmasi kata sandi baru", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmPass,
                onValueChange = { confirmPass = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = { Text(text = "👁️", fontSize = 16.sp) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Ketentuan kata sandi Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ketentuan kata sandi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PasswordCheckItem(label = "Minimal 8 karakter", checked = hasMinLength)
                    Spacer(modifier = Modifier.height(8.dp))
                    PasswordCheckItem(label = "Mengandung huruf dan angka", checked = hasLetterAndNumber)
                    Spacer(modifier = Modifier.height(8.dp))
                    PasswordCheckItem(label = "Berbeda dari kata sandi sebelumnya", checked = isDifferent)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun PasswordCheckItem(label: String, checked: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = if (checked) Color(0xFFE6F4EA) else Color(0xFFF1F3F4),
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = if (checked) "✓" else "•", fontSize = 11.sp, color = if (checked) Color(0xFF137333) else TextMuted)
            }
        }
        Text(text = label, fontSize = 12.sp, color = if (checked) PrimaryDarkGreen else TextMuted)
    }
}
