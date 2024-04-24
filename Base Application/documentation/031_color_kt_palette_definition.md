# ui/theme/Color.kt - Color Tokens and Hex Representation

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Base Application/app/src/main/java/com/keeper/webguard/ui/theme/Color.kt
package com.keeper.webguard.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
```

### Notes, Curious Questions & System Mechanics

- the file `Color.kt` in the `ui/theme` package is the color token repository for the application
- notice the color definitions use the Compose `Color` class: `val Purple80 = Color(0xFFD0BCFF)`
- this is completely different from the old XML way of defining colors in `res/values/colors.xml` with `<color name="purple">#D0BCFF</color>`
- why `0xFFD0BCFF`? The `0x` denotes a hexadecimal integer literal in Kotlin
- the first two characters `FF` represent the Alpha channel (opacity): `FF` in hexadecimal is 255 in decimal, meaning 100% fully opaque
- the next two characters `D0` represent Red (208 in decimal)
- the next two characters `BC` represent Green (188 in decimal)
- the final two characters `FF` represent Blue (255 in decimal)
- in Android, color integers are ALWAYS formatted as `AARRGGBB` (Alpha, Red, Green, Blue), NOT standard CSS `RRGGBBAA`!
- if you accidentally forget the `FF` alpha prefix and write `Color(0xD0BCFF)`, the alpha is 0, making your color 100% completely invisible and transparent!
- this is one of the most common beginner gotchas in Android: spending 2 hours debugging why a button is invisible because alpha was 0
- notice the naming convention: `Purple80` vs `Purple40`
- the numbers 80 and 40 refer to Tonal Luminance in the Material Design 3 color palette specification
- higher luminance numbers (like 80) represent lighter, pastel shades designed for high contrast on Dark Theme backgrounds
- lower luminance numbers (like 40) represent deeper, saturated shades designed for Light Theme backgrounds
- defining colors as top-level Kotlin `val` properties makes them compile-time constants that are easily inlined by the compiler
- these raw color constants should never be called directly in your UI screens (e.g. don't do `Text(color = Purple80)`)
- instead, map them to semantic slots in `Theme.kt` (`primary = Purple80`), and in your UI call `MaterialTheme.colorScheme.primary`
- semantic mapping allows you to rebrand the entire app from purple to blue by editing only these theme files without touching screens
- you can also define color gradients, translucent scrims (`Color.Black.copy(alpha = 0.5f)`), and custom brand tokens here
- studying `Color.kt` taught me how Material 3 achieves mathematical color harmony across both light and dark display modes
