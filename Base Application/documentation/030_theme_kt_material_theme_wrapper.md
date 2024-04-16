# ui/theme/Theme.kt - Material 3 Theming, Dynamic Color, and Dark Mode

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Base Application/app/src/main/java/com/keeper/webguard/ui/theme/Theme.kt
@Composable
fun KeeperWebGuard20Theme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

### Notes, Curious Questions & System Mechanics

- inside `app/src/main/java/com/keeper/webguard/ui/theme/`, the file `Theme.kt` defines the visual design system of the entire app
- the function `KeeperWebGuard20Theme` is a custom theme wrapper around Google's official `MaterialTheme` composable
- it accepts three parameters: `darkTheme: Boolean`, `dynamicColor: Boolean`, and the trailing lambda `content: @Composable () -> Unit`
- notice the default value: `darkTheme: Boolean = isSystemInDarkTheme()`
- `isSystemInDarkTheme()` is a Compose runtime function that automatically detects whether the user has Dark Mode toggled in Android system settings
- this means the app automatically switches between dark and light themes without writing any manual toggle logic!
- now look at `dynamicColor: Boolean = true` and the check `Build.VERSION.SDK_INT >= Build.VERSION_CODES.S`
- `Build.VERSION_CODES.S` is Android 12 (API level 31), which introduced 'Material You' and the Monet dynamic theming engine
- on Android 12+, Android extracts the dominant color palette from the user's personal home screen wallpaper!
- if `dynamicColor` is enabled, `dynamicDarkColorScheme(context)` and `dynamicLightColorScheme(context)` generate colors matching the user's wallpaper
- this makes your app feel like an organic, custom extension of the user's personal device aesthetic
- on older devices (Android 10 and 11, or if `dynamicColor = false`), the app falls back to our curated `DarkColorScheme` and `LightColorScheme`
- these curated color schemes are defined using color tokens imported from `Color.kt` (like `Purple80`, `PurpleGrey80`, `Pink80`)
- `MaterialTheme(...)` takes this resolved `colorScheme`, along with `Typography` from `Type.kt`, and provides them via `CompositionLocal`
- any child composable anywhere in the app can now simply call `MaterialTheme.colorScheme.primary` or `MaterialTheme.typography.bodyLarge`
- if the theme changes, only composables reading that color token are recomposed, keeping performance optimal
- beginner fallacy: i thought that to support dark mode i had to create duplicate layout files like in old XML (`values/` and `values-night/`)
- in Compose, dark mode is simply a conditional reactive state that swaps a data class of colors in memory!
- you can even provide custom theme attributes (like spacing or elevation) by defining custom `CompositionLocalProvider` wrappers
- this file is a masterclass in how modern Android apps achieve beautiful, adaptive, accessible visual styling with minimal code
- it shows how deeply Jetpack Compose is integrated with Android OS platform capabilities like dynamic wallpaper sampling
