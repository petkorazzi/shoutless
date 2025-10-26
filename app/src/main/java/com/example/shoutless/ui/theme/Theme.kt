package com.example.shoutless.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ShoutlessColorScheme = darkColorScheme(
    primary = sl_purple,
    secondary = sl_pink,
    tertiary = sl_blue,
    background = sl_greyBg,
    surface = sl_greySurface
)

@Composable
fun ShoutlessTheme(
    darkTheme: Boolean = true, // Force dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ShoutlessColorScheme,
        typography = Typography,
        content = content
    )
}
