package com.YashDoesCode.comms.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.YashDoesCode.designsystem.theme.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = CommsPrimaryDark,
    onPrimary = CommsOnPrimaryDark,
    primaryContainer = CommsPrimaryContainerDark,
    onPrimaryContainer = CommsOnPrimaryContainerDark,
    secondary = CommsSecondaryDark,
    onSecondary = CommsOnSecondaryDark,
    secondaryContainer = CommsSecondaryContainerDark,
    onSecondaryContainer = CommsOnSecondaryContainerDark,
    tertiary = CommsTertiaryDark,
    onTertiary = CommsOnTertiaryDark,
    tertiaryContainer = CommsTertiaryContainerDark,
    onTertiaryContainer = CommsOnTertiaryContainerDark,
    background = CommsBackgroundDark,
    onBackground = CommsOnBackgroundDark,
    surface = CommsSurfaceDark,
    onSurface = CommsOnSurfaceDark,
    surfaceVariant = CommsSurfaceVariantDark,
    onSurfaceVariant = CommsOnSurfaceVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = CommsPrimaryLight,
    onPrimary = CommsOnPrimaryLight,
    primaryContainer = CommsPrimaryContainerLight,
    onPrimaryContainer = CommsOnPrimaryContainerLight,
    secondary = CommsSecondaryLight,
    onSecondary = CommsOnSecondaryLight,
    secondaryContainer = CommsSecondaryContainerLight,
    onSecondaryContainer = CommsOnSecondaryContainerLight,
    tertiary = CommsTertiaryLight,
    onTertiary = CommsOnTertiaryLight,
    tertiaryContainer = CommsTertiaryContainerLight,
    onTertiaryContainer = CommsOnTertiaryContainerLight,
    background = CommsBackgroundLight,
    onBackground = CommsOnBackgroundLight,
    surface = CommsSurfaceLight,
    onSurface = CommsOnSurfaceLight,
    surfaceVariant = CommsSurfaceVariantLight,
    onSurfaceVariant = CommsOnSurfaceVariantLight
)

@Composable
fun CommsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        customColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = CommsTypography,
        content = content
    )
}
