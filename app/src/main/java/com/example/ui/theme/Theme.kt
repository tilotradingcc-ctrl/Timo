package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),      // Soft Indigo for dark theme
    secondary = Color(0xFF38BDF8),    // Soft Cyan for dark theme
    tertiary = Color(0xFFF43F5E),    // Soft Magenta for dark theme
    background = DarkBg,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    outlineVariant = DarkBorderColor
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo600,             // Brand Indigo
    secondary = CyanPrint,           // Refined Cyan
    tertiary = MagentaPrint,         // Refined Magenta
    background = LightBg,            // Clean Slate Gray/Blue (0xFFF7F9FC)
    surface = LightSurface,          // Clean White Cards
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF0F172A), // Deep Slate body text
    onSurface = Color(0xFF1E293B),    // Rich Slate text
    outlineVariant = BorderColor     // Slate 200 borders
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color support
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
