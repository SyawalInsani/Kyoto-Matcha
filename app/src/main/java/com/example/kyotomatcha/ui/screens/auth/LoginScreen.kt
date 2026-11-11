package com.example.kyotomatcha.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kyotomatcha.ui.components.OmochaLogo
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val email by viewModel.loginEmail.collectAsState()
    val password by viewModel.loginPassword.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val showBanner by viewModel.showLoginErrorBanner.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val emailErrorText by viewModel.emailErrorText.collectAsState()
    val passwordErrorText by viewModel.passwordErrorText.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OmochaLogo(size = 32.dp)
                    Text(
                        text = "O-MACHA",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Selamat datang kembali",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Masuk ke O-MACHA untuk mulai bekerja.",
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
                                text = "Lengkapi email dan kata sandi untuk masuk.",
                                fontSize = 12.sp,
                                color = Color(0xFFC53030)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field
                Text(
                    text = "Alamat email",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryDarkGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.setLoginEmail(it) },
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
                    Text(
                        text = emailErrorText,
                        fontSize = 11.sp,
                        color = Color(0xFFC53030)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                Text(
                    text = "Kata sandi",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryDarkGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.setLoginPassword(it) },
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
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
                if (passwordError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = passwordErrorText,
                        fontSize = 11.sp,
                        color = Color(0xFFC53030)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.setRememberMe(!rememberMe) }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { viewModel.setRememberMe(it) },
                        colors = CheckboxDefaults.colors(checkedColor = MatchaGreen)
                    )
                    Text(
                        text = "Ingat saya",
                        fontSize = 13.sp,
                        color = PrimaryDarkGreen
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = { viewModel.validateAndLogin(onSuccess = onLoginSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
                ) {
                    Text(
                        text = "Masuk",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Register Navigation Link
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row {
                        Text(text = "Belum punya akun? ", fontSize = 13.sp, color = TextMuted)
                        Text(
                            text = "Daftar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }

            // Footer Tagline
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Satu tempat untuk tim, transaksi, dan persediaan.",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }
    }
}
