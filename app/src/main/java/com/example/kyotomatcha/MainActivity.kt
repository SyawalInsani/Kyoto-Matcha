package com.example.kyotomatcha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.kyotomatcha.ui.navigation.AppNavGraph
import com.example.kyotomatcha.ui.theme.KyotoMatchaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KyotoMatchaTheme {
                AppNavGraph()
            }
        }
    }
}
