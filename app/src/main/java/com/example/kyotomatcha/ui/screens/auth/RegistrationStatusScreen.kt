package com.example.kyotomatcha.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kyotomatcha.ui.theme.BackgroundCream
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted
import com.example.kyotomatcha.ui.theme.White

@Composable
fun RegistrationStatusScreen(
    email: String,
    name: String,
    onBackToLogin: () -> Unit
) {
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "←",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen,
                        modifier = Modifier.clickable { onBackToLogin() }
                    )
                    Text(
                        text = "Status pendaftaran",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryDarkGreen
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Terkirim badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE6F4EA)
                ) {
                    Text(
                        text = "Terkirim",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF137333)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Notifikasi dikirim ke email",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Status Banner Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF1F8F5)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MatchaGreen,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "✓", fontSize = 14.sp, color = White)
                            }
                        }

                        Column {
                            Text(
                                text = "Email verifikasi terkirim",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$email • Kyoto Matcha",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Pendaftaran $name ($email) telah berhasil diproses. Notifikasi, instruksi aktivasi akun, serta detail akses telah dikirimkan langsung ke email Anda. Silakan periksa kotak masuk atau folder spam email Anda.",
                    fontSize = 14.sp,
                    color = TextMuted,
                    lineHeight = 22.sp
                )
            }

            // Bottom Button
            OutlinedButton(
                onClick = onBackToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE2DDD0)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryDarkGreen)
            ) {
                Text(
                    text = "Kembali ke Masuk",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryDarkGreen
                )
            }
        }
    }
}
