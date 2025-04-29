package com.vitizen.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Schéma de couleurs pour l'extérieur
private val OutdoorColorScheme = lightColorScheme(
    primary = VitizenBrown,
    onPrimary = VitizenBeige,
    primaryContainer = VitizenDarkBeige,
    onPrimaryContainer = VitizenBrown,
    secondary = VitizenOutdoorSecondary,
    onSecondary = VitizenOutdoorSurface,
    secondaryContainer = VitizenDarkBeige,
    onSecondaryContainer = VitizenBrown,
    tertiary = VitizenOutdoorBorder,
    onTertiary = VitizenOutdoorSurface,
    tertiaryContainer = VitizenDarkBeige,
    onTertiaryContainer = VitizenBrown,
    error = VitizenOutdoorError,
    onError = VitizenOutdoorSurface,
    errorContainer = VitizenOutdoorError.copy(alpha = 0.1f),
    onErrorContainer = VitizenOutdoorError,
    background = VitizenOutdoorBackground,
    onBackground = VitizenBrown,
    surface = VitizenOutdoorSurface,
    onSurface = VitizenBrown,
    surfaceVariant = VitizenDarkBeige,
    onSurfaceVariant = VitizenBrown,
    outline = VitizenOutdoorBorder
)

// Schéma de couleurs clair standard
private val LightColorScheme = lightColorScheme(
    primary = VitizenBrown,
    onPrimary = VitizenBeige,
    primaryContainer = VitizenDarkBeige,
    onPrimaryContainer = VitizenBrown,
    secondary = VitizenOutdoorSecondary,
    onSecondary = VitizenOutdoorSurface,
    secondaryContainer = VitizenDarkBeige,
    onSecondaryContainer = VitizenBrown,
    tertiary = VitizenOutdoorBorder,
    onTertiary = VitizenOutdoorSurface,
    tertiaryContainer = VitizenDarkBeige,
    onTertiaryContainer = VitizenBrown,
    error = VitizenOutdoorError,
    onError = VitizenOutdoorSurface,
    errorContainer = VitizenOutdoorError.copy(alpha = 0.1f),
    onErrorContainer = VitizenOutdoorError,
    background = VitizenOutdoorBackground,
    onBackground = VitizenBrown,
    surface = VitizenOutdoorSurface,
    onSurface = VitizenBrown,
    surfaceVariant = VitizenDarkBeige,
    onSurfaceVariant = VitizenBrown,
    outline = VitizenOutdoorBorder
)

// Schéma de couleurs sombre
private val DarkColorScheme = darkColorScheme(
    primary = VitizenBeige,
    onPrimary = VitizenBrown,
    primaryContainer = VitizenDarkBeige,
    onPrimaryContainer = VitizenBrown,
    secondary = VitizenBeige,
    onSecondary = VitizenBrown,
    secondaryContainer = VitizenDarkBeige,
    onSecondaryContainer = VitizenBrown,
    tertiary = VitizenBeige,
    onTertiary = VitizenBrown,
    tertiaryContainer = VitizenDarkBeige,
    onTertiaryContainer = VitizenBrown,
    error = VitizenOutdoorError,
    onError = VitizenOutdoorSurface,
    errorContainer = VitizenOutdoorError.copy(alpha = 0.1f),
    onErrorContainer = VitizenOutdoorError,
    background = VitizenBrown,
    onBackground = VitizenBeige,
    surface = VitizenBrown,
    onSurface = VitizenBeige,
    surfaceVariant = VitizenDarkBeige,
    onSurfaceVariant = VitizenBrown,
    outline = VitizenDarkBeige
)

@Composable
fun VitizenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isOutdoorMode: Boolean = true,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isOutdoorMode -> OutdoorColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
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