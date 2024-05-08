# ui/theme/Type.kt - The Material 3 Typography Scale

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Base Application/app/src/main/java/com/keeper/webguard/ui/theme/Type.kt
package com.keeper.webguard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override:
    titleLarge = TextStyle(...),
    labelSmall = TextStyle(...)
    */
)
```

### Notes, Curious Questions & System Mechanics

- the file `Type.kt` defines the typography scale for all text rendering across the application
- it configures the `androidx.compose.material3.Typography` data structure consumed by `MaterialTheme` in `Theme.kt`
- Material Design 3 defines 15 distinct text styles organized into 5 roles: Display, Headline, Title, Body, and Label
- each role has three sizes: Large, Medium, and Small (e.g. `headlineLarge`, `bodyMedium`, `labelSmall`)
- in `Base Application`, the template overrides `bodyLarge` with a custom `TextStyle` specification
- look at the units used: `fontSize = 16.sp` and `lineHeight = 24.sp`
- why `.sp` instead of `.dp` or `.px`? `sp` stands for 'Scale-independent Pixels'
- `sp` scales automatically based on the user's system font size preference in Android Settings -> Accessibility -> Font Size
- if an elderly user or vision-impaired user increases their system font size by 150%, all `.sp` text automatically scales up proportionally
- if you mistakenly use `.dp` for font sizes, the text will stay fixed, violating Android Accessibility guidelines
- `lineHeight = 24.sp` ensures comfortable vertical rhythm and line-spacing so multi-line paragraphs don't collide
- `letterSpacing = 0.5.sp` adds subtle kerning between characters for enhanced readability on mobile OLED displays
- `fontFamily = FontFamily.Default` uses the system default sans-serif font (typically Roboto on Google Pixel or Samsung Sans on Galaxy)
- you can easily import custom Google Fonts (like Inter, Montserrat, or Outfit) or local TTF/OTF files from `res/font/` here
- when you use custom fonts, you define a `FontFamily` with different weights: `Font(R.font.inter_bold, FontWeight.Bold)`
- in your composable screens, whenever you call `Text(text = "Hello", style = MaterialTheme.typography.bodyLarge)`, it uses this exact style
- this centralizes text styling, preventing hardcoded font sizes scattered across hundreds of individual UI files
- beginner fallacy: i thought typography was just picking a font family and size in each `Text()` component
- having a formal typography scale guarantees visual hierarchy and brand consistency across the entire user experience
- it is another example of Android Studio generating production-grade architectural patterns right from the start
- it ensures your app is accessible, beautiful, and effortless to maintain as it grows
