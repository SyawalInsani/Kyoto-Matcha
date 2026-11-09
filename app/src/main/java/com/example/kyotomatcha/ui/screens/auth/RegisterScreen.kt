package com.example.kyotomatcha.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun RegisterScreen(
    onRegisterSuccess: (email: String, name: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val fullName by viewModel.fullName.collectAsState()
    val email by viewModel.registerEmail.collectAsState()
    val phone by viewModel.phoneNumber.collectAsState()
    val password by viewModel.registerPassword.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val agreeTerms by viewModel.agreeTerms.collectAsState()

    val showBanner by viewModel.showRegisterErrorBanner.collectAsState()
    val nameError by viewModel.regNameError.collectAsState()
    val emailError by viewModel.regEmailError.collectAsState()
    val phoneError by viewModel.regPhoneError.collectAsState()
    val passwordError by viewModel.regPasswordError.collectAsState()
    val confirmError by viewModel.regConfirmError.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
                Text(
                    text = "Daftar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryDarkGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Buat akun O-MACHA",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryDarkGreen
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Daftar untuk bergabung dengan tim kedai.",
                fontSize = 14.sp,
                color = TextMuted
            )

            if (showBanner) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFDE8E8)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Periksa kembali data Anda",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC53030)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Lengkapi semua form pendaftaran dengan benar.",
                            fontSize = 12.sp,
                            color = Color(0xFFC53030)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nama lengkap
            Text(text = "Nama lengkap", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = fullName,
                onValueChange = { viewModel.setFullName(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (nameError) Color.Red else MatchaGreen,
                    unfocusedBorderColor = if (nameError) Color.Red else Color(0xFFE2DDD0),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                singleLine = true
            )
            if (nameError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Nama lengkap wajib diisi.", fontSize = 11.sp, color = Color(0xFFC53030))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            Text(text = "Email", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.setRegisterEmail(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (emailError) Color.Red else MatchaGreen,
                    unfocusedBorderColor = if (emailError) Color.Red else Color(0xFFE2DDD0),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            if (emailError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Masukkan alamat email yang valid.", fontSize = 11.sp, color = Color(0xFFC53030))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nomor telepon
            Text(text = "Nomor telepon", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { viewModel.setPhoneNumber(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (phoneError) Color.Red else MatchaGreen,
                    unfocusedBorderColor = if (phoneError) Color.Red else Color(0xFFE2DDD0),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            if (phoneError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Nomor telepon wajib diisi.", fontSize = 11.sp, color = Color(0xFFC53030))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kata sandi
            Text(text = "Kata sandi", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.setRegisterPassword(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (passwordError) Color.Red else MatchaGreen,
                    unfocusedBorderColor = if (passwordError) Color.Red else Color(0xFFE2DDD0),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(text = if (passwordVisible) "👁" else "👁‍🗨", fontSize = 14.sp)
                    }
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (passwordError) {
                Text(text = "Kata sandi minimal 8 karakter.", fontSize = 11.sp, color = Color(0xFFC53030))
            } else {
                Text(text = "Gunakan minimal 8 karakter, huruf dan angka.", fontSize = 11.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Konfirmasi kata sandi
            Text(text = "Konfirmasi kata sandi", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PrimaryDarkGreen)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { viewModel.setConfirmPassword(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (confirmError) Color.Red else MatchaGreen,
                    unfocusedBorderColor = if (confirmError) Color.Red else Color(0xFFE2DDD0),
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                ),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Text(text = if (confirmPasswordVisible) "👁" else "👁‍🗨", fontSize = 14.sp)
                    }
                }
            )
            if (confirmError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Konfirmasi kata sandi tidak cocok.", fontSize = 11.sp, color = Color(0xFFC53030))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Terms Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { viewModel.setAgreeTerms(!agreeTerms) }
            ) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { viewModel.setAgreeTerms(it) },
                    colors = CheckboxDefaults.colors(checkedColor = MatchaGreen)
                )
                Text(text = "Saya menyetujui Syarat & Ketentuan", fontSize = 13.sp, color = PrimaryDarkGreen)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = { viewModel.validateAndRegister(onSuccess = onRegisterSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
            ) {
                Text(text = "Daftar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = White)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login Link
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row {
                    Text(text = "Sudah punya akun? ", fontSize = 13.sp, color = TextMuted)
                    Text(
                        text = "Masuk",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Setelah daftar akun, Anda menunggu persetujuan pemilik. Peran dan cabang ditentukan oleh pemilik.",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
