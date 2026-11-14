package com.example.kyotomatcha

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import com.example.kyotomatcha.ui.navigation.AppNavGraph
import com.example.kyotomatcha.ui.theme.KyotoMatchaTheme

class MainActivity : ComponentActivity() {

    // Android 17 (API 37) memblokir akses ke alamat jaringan lokal (mis. 10.0.2.2 atau IP komputer)
    // sebelum pengguna mengizinkan. Perlu hanya saat server berjalan di komputer sendiri;
    // server di internet (HTTPS) tidak membutuhkannya.
    private val localNetworkPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 37 &&
            checkSelfPermission(LOCAL_NETWORK) != PackageManager.PERMISSION_GRANTED
        ) {
            localNetworkPermission.launch(LOCAL_NETWORK)
        }
        enableEdgeToEdge()
        setContent {
            KyotoMatchaTheme {
                AppNavGraph()
            }
        }
    }

    private companion object {
        const val LOCAL_NETWORK = "android.permission.ACCESS_LOCAL_NETWORK"
    }
}
