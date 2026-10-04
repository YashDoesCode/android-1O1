package com.YashDoesCode.gallery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.YashDoesCode.designsystem.theme.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = GalleryDarkPrimary,
    primaryContainer = GalleryDarkPrimaryContainer,
    onPrimaryContainer = GalleryDarkOnPrimaryContainer,
    secondary = GalleryDarkSecondary,
    onSecondary = GalleryDarkOnSecondary,
    background = GalleryDarkBackground,
    surface = GalleryDarkSurface,
    surfaceVariant = GalleryDarkSurfaceVariant,
    onBackground = GalleryDarkTextPrimary,
    onSurface = GalleryDarkTextPrimary,
    onSurfaceVariant = GalleryDarkTextSecondary,
    error = GalleryDarkError
)

private val LightColorScheme = lightColorScheme(
    primary = GalleryLightPrimary,
    primaryContainer = GalleryLightPrimaryContainer,
    onPrimaryContainer = GalleryLightOnPrimaryContainer,
    secondary = GalleryLightSecondary,
    onSecondary = GalleryLightOnSecondary,
    background = GalleryLightBackground,
    surface = GalleryLightSurface,
    surfaceVariant = GalleryLightSurfaceVariant,
    onBackground = GalleryLightTextPrimary,
    onSurface = GalleryLightTextPrimary,
    onSurfaceVariant = GalleryLightTextSecondary,
    error = GalleryLightError
)

@Composable
fun GalleryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    AppTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        customColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = GalleryTypography,
        content = content
    )
}
