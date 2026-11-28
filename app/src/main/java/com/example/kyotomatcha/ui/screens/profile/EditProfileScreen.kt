package com.example.kyotomatcha.ui.screens.profile

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
fun rememberBitmapFromUri(context: Context, uriString: String?): ImageBitmap? {
    if (uriString == null || (!uriString.startsWith("content://") && !uriString.startsWith("file://"))) return null
    return remember(uriString) {
        runCatching {
            val uri = android.net.Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap?.asImageBitmap()
        }.getOrNull()
    }
}

@Composable
fun UserAvatarView(
    profileImageUri: String?,
    size: Dp = 80.dp,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val bitmap = rememberBitmapFromUri(context, profileImageUri)

    Surface(
        shape = CircleShape,
        color = CardBackground,
        modifier = Modifier
            .size(size)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Box(contentAlignment = Alignment.Center) {
            when {
                bitmap != null -> {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Foto Profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                profileImageUri != null && profileImageUri.length <= 4 -> {
                    Text(
                        text = profileImageUri,
                        fontSize = (size.value * 0.45f).sp
                    )
                }
                else -> {
                    Text(
                        text = "👩‍🍳",
                        fontSize = (size.value * 0.45f).sp
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentName by viewModel.name.collectAsState()
    val currentEmail by viewModel.email.collectAsState()
    val currentPhone by viewModel.phone.collectAsState()
    val currentRole by viewModel.role.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()

    var nameInput by remember { mutableStateOf(currentName) }
    var emailInput by remember { mutableStateOf(currentEmail) }
    var phoneInput by remember { mutableStateOf(currentPhone) }
    var showPhotoOptionDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateProfileImage(it.toString()) }
    }

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
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = PrimaryDarkGreen
                    )
                }
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
                UserAvatarView(
                    profileImageUri = profileImageUri,
                    size = 90.dp,
                    onClick = { showPhotoOptionDialog = true }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Ubah foto",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MatchaGreen,
                    modifier = Modifier.clickable { showPhotoOptionDialog = true }
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
                    ProfileInfoRow(label = "ID karyawan", value = com.example.kyotomatcha.data.local.SessionManager(context).getEmployeeCode())
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileInfoRow(label = "Peran", value = currentRole)
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

        // Photo Options Dialog
        if (showPhotoOptionDialog) {
            AlertDialog(
                onDismissRequest = { showPhotoOptionDialog = false },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ubah Foto Profil",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                        IconButton(onClick = { showPhotoOptionDialog = false }) {
                            Icon(Icons.Filled.Close, contentDescription = "Tutup", tint = PrimaryDarkGreen)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Option 1: Gallery
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CardBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showPhotoOptionDialog = false
                                    imagePickerLauncher.launch("image/*")
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = PrimaryDarkGreen)
                                Text(
                                    text = "Pilih dari Galeri Foto",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkGreen
                                )
                            }
                        }

                        // Option 2: Character Avatars
                        Text(
                            text = "Atau Pilih Avatar Karakter:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted
                        )

                        val avatars = listOf("👩‍🍳", "🧑‍🍳", "👨‍🍳", "🍵", "☕", "👩‍💼", "🧑‍💼", "👤")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            avatars.take(4).forEach { avatarEmoji ->
                                Surface(
                                    shape = CircleShape,
                                    color = BackgroundCream,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clickable {
                                            viewModel.updateProfileImage(avatarEmoji)
                                            showPhotoOptionDialog = false
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = avatarEmoji, fontSize = 22.sp)
                                    }
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            avatars.drop(4).forEach { avatarEmoji ->
                                Surface(
                                    shape = CircleShape,
                                    color = BackgroundCream,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clickable {
                                            viewModel.updateProfileImage(avatarEmoji)
                                            showPhotoOptionDialog = false
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = avatarEmoji, fontSize = 22.sp)
                                    }
                                }
                            }
                        }

                        // Option 3: Reset
                        if (profileImageUri != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(
                                onClick = {
                                    viewModel.updateProfileImage(null)
                                    showPhotoOptionDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Hapus Foto Profil", color = Color(0xFFC53030), fontSize = 12.sp)
                            }
                        }
                    }
                },
                confirmButton = {},
                containerColor = White
            )
        }
    }
}
