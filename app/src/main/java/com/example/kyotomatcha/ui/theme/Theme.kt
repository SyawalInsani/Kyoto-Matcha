package com.example.kyotomatcha.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MatchaGreen,
    background = BackgroundCream,
    surface = CardBackground,
    onPrimary = White,
    onBackground = PrimaryDarkGreen,
    onSurface = PrimaryDarkGreen
)

@Composable
fun KyotoMatchaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
