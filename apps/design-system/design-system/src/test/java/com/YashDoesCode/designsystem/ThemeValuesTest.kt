package com.YashDoesCode.designsystem

import com.YashDoesCode.designsystem.theme.*
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ThemeValuesTest {

    @Test
    fun defaultLightColorScheme_hasValidValues() {
        assertThat(DefaultLightColorScheme.primary).isEqualTo(LightPrimary)
        assertThat(DefaultLightColorScheme.background).isEqualTo(LightBackground)
        assertThat(DefaultLightColorScheme.surface).isEqualTo(LightSurface)
        assertThat(DefaultLightColorScheme.error).isEqualTo(LightError)
    }

    @Test
    fun defaultDarkColorScheme_hasValidValues() {
        assertThat(DefaultDarkColorScheme.primary).isEqualTo(DarkPrimary)
        assertThat(DefaultDarkColorScheme.background).isEqualTo(DarkBackground)
        assertThat(DefaultDarkColorScheme.surface).isEqualTo(DarkSurface)
        assertThat(DefaultDarkColorScheme.error).isEqualTo(DarkError)
    }

    @Test
    fun typographyHierarchy_isConsistent() {
        assertThat(AppTypography.displayLarge.fontSize.value).isGreaterThan(AppTypography.headlineLarge.fontSize.value)
        assertThat(AppTypography.headlineLarge.fontSize.value).isGreaterThan(AppTypography.titleLarge.fontSize.value)
        assertThat(AppTypography.titleLarge.fontSize.value).isGreaterThan(AppTypography.bodyLarge.fontSize.value)
        assertThat(AppTypography.bodyLarge.fontSize.value).isGreaterThan(AppTypography.bodySmall.fontSize.value)
    }
}
