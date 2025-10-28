package com.example.shoutless.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = sl_purple,
    secondary = sl_pink,
    tertiary = sl_blue,
    background = sl_greyBg,
    surface = sl_greySurface,
    onPrimary = on_primary,
    onSecondary = on_secondary,
    onTertiary = on_tertiary,
    onBackground = on_background,
    onSurface = on_surface,
    onSurfaceVariant = on_surface_variant_dark
)

private val DisplayColorScheme = darkColorScheme(
    primary = sl_purple,
    secondary = sl_pink,
    tertiary = sl_blue,
    background = black,
    surface = sl_greySurface,
    onPrimary = on_primary,
    onSecondary = on_secondary,
    onTertiary = on_tertiary,
    onBackground = on_background,
    onSurface = on_surface,
    onSurfaceVariant = on_surface_variant_dark
)

private val LightColorScheme = lightColorScheme(
    primary = sl_primary_light,
    secondary = sl_secondary_light,
    tertiary = sl_tertiary_light,
    background = sl_background_light,
    surface = sl_surface_light,
    onPrimary = on_primary,
    onSecondary = on_secondary,
    onTertiary = on_tertiary,
    onBackground = on_background_light,
    onSurface = on_surface_light,
    onSurfaceVariant = on_surface_variant_light
)

@Composable
fun ShoutlessTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ShoutlessDisplayTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DisplayColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
