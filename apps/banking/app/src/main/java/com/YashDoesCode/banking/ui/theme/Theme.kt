package com.YashDoesCode.banking.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.YashDoesCode.designsystem.theme.AppTheme

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
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        customColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = BankingTypography,
        content = content
    )
}
