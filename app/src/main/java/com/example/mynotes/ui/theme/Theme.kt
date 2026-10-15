package com.example.mynotes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.mynotes.data.preferences.ThemeOption

@Composable
fun MyNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    themeOption: ThemeOption = ThemeOption.SYSTEM_DEFAULT,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme: ColorScheme = when {
        // Dynamic Color доступен только на Android 12+ (API 31+)
        dynamicColor && themeOption == ThemeOption.SYSTEM_DEFAULT && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Кастомные темы
        themeOption == ThemeOption.BLUE -> {
            if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
        }
        themeOption == ThemeOption.GREEN -> {
            if (darkTheme) GreenDarkColorScheme else GreenLightColorScheme
        }
        themeOption == ThemeOption.ORANGE -> {
            if (darkTheme) OrangeDarkColorScheme else OrangeLightColorScheme
        }
        themeOption == ThemeOption.PURPLE -> {
            if (darkTheme) PurpleDarkColorScheme else PurpleLightColorScheme
        }
        // Fallback на системную тему
        darkTheme -> BlueDarkColorScheme
        else -> BlueLightColorScheme
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
        content = content
    )
}