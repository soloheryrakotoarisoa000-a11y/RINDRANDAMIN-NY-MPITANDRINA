package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Navy800,
    onPrimary = IvoryWhite,
    primaryContainer = Navy100,
    onPrimaryContainer = Navy900,
    secondary = GoldBase,
    onSecondary = Navy900,
    secondaryContainer = GoldContainerLight,
    onSecondaryContainer = GoldDark,
    tertiary = GoldDark,
    onTertiary = IvoryWhite,
    background = IvoryWhite,
    onBackground = TextDarkPrimary,
    surface = SurfaceLight,
    onSurface = TextDarkPrimary,
    surfaceVariant = BorderLight,
    onSurfaceVariant = TextDarkSecondary,
    outline = GoldBase,
    outlineVariant = BorderLight,
    error = StatusRed,
    onError = IvoryWhite,
    errorContainer = StatusRedContainer,
    onErrorContainer = StatusRed
)

private val DarkColorScheme = darkColorScheme(
    primary = Navy200,
    onPrimary = Navy900,
    primaryContainer = Navy700,
    onPrimaryContainer = IvoryWhite,
    secondary = GoldLight,
    onSecondary = DarkBg,
    secondaryContainer = GoldContainerDark,
    onSecondaryContainer = GoldLight,
    tertiary = GoldBase,
    onTertiary = DarkBg,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = GoldBase,
    outlineVariant = DarkBorder,
    error = StatusRed,
    onError = IvoryWhite,
    errorContainer = StatusRedContainer,
    onErrorContainer = StatusRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = (if (darkTheme) DarkBg else Navy800).toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
