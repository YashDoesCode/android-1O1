package com.YashDoesCode.banking.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = BankingPrimaryDark,
    onPrimary = BankingOnPrimaryDark,
    primaryContainer = BankingPrimaryContainerDark,
    onPrimaryContainer = BankingOnPrimaryContainerDark,
    secondary = BankingSecondaryDark,
    onSecondary = BankingOnSecondaryDark,
    secondaryContainer = BankingSecondaryContainerDark,
    onSecondaryContainer = BankingOnSecondaryContainerDark,
    tertiary = BankingTertiaryDark,
    onTertiary = BankingOnTertiaryDark,
    tertiaryContainer = BankingTertiaryContainerDark,
    onTertiaryContainer = BankingOnTertiaryContainerDark,
    background = BankingBackgroundDark,
    onBackground = BankingOnBackgroundDark,
    surface = BankingSurfaceDark,
    onSurface = BankingOnSurfaceDark,
    surfaceVariant = BankingSurfaceVariantDark,
    onSurfaceVariant = BankingOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = BankingPrimaryLight,
    onPrimary = BankingOnPrimaryLight,
    primaryContainer = BankingPrimaryContainerLight,
    onPrimaryContainer = BankingOnPrimaryContainerLight,
    secondary = BankingSecondaryLight,
    onSecondary = BankingOnSecondaryLight,
    secondaryContainer = BankingSecondaryContainerLight,
    onSecondaryContainer = BankingOnSecondaryContainerLight,
    tertiary = BankingTertiaryLight,
    onTertiary = BankingOnTertiaryLight,
    tertiaryContainer = BankingTertiaryContainerLight,
    onTertiaryContainer = BankingOnTertiaryContainerLight,
    background = BankingBackgroundLight,
    onBackground = BankingOnBackgroundLight,
    surface = BankingSurfaceLight,
    onSurface = BankingOnSurfaceLight,
    surfaceVariant = BankingSurfaceVariantLight,
    onSurfaceVariant = BankingOnSurfaceVariantLight
)

@Composable
fun BankingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BankingTypography,
        content = content
    )
}
