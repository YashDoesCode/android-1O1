# Android 12 S - Material You (Monet) Theming, Splash Screens, and Privacy Dashboard

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// In ui/theme/Theme.kt:
dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
    val context = LocalContext.current
    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
}

// Android 12 Privacy Indicators:
// Green dot appears in top right corner of screen whenever ANY app touches Camera or Mic!
```

### Notes, Curious Questions & System Mechanics

- Android 12 (API level 31), released in 2021, was the most dramatic visual and privacy overhaul of Android since Android 5.0 Lollipop
- feature 1: 'Material You' and the Monet Dynamic Color Engine
- as we saw in `Base Application/app/src/main/java/com/keeper/webguard/ui/theme/Theme.kt`, Android 12 extracts colors from the user's wallpaper
- the Monet engine uses sophisticated color science (CAM16 color space) to generate a rich 65-color tonal palette from the wallpaper image
- `dynamicDarkColorScheme(context)` and `dynamicLightColorScheme(context)` make your app seamlessly blend into the user's custom aesthetic
- feature 2: The Unified Splash Screen API (`androidx.core:core-splashscreen`)
- before Android 12, developers built custom `SplashActivity` screens with ugly 3-second timers that frustrated users
- Android 12 made splash screens an official operating system primitive: the OS automatically animates your launcher icon on cold launch
- it ensures a standard, smooth, hardware-accelerated entry animation into `MainActivity` across all devices
- feature 3: Hardware Privacy Indicators (The Green Dot)
- whenever an app accesses the microphone or camera, Android 12 displays a bright green indicator dot in the top right corner of the status bar
- tapping the green dot reveals exactly which app is using the sensor and provides a direct shortcut to revoke the permission
- feature 4: Global Quick Settings Toggles for Camera and Microphone
- users can swipe down quick settings and toggle 'Camera Access' or 'Mic Access' off globally across the entire operating system
- when turned off, any app trying to read the camera gets a black feed, and any app trying to record audio gets silent zeros
- feature 5: Approximate Location (`ACCESS_COARSE_LOCATION`)
- when an app requests location, the user can choose between 'Precise' (exact GPS coordinates) and 'Approximate' (a 3 km fuzzy circle)
- weather apps and shopping apps don't need to know your exact house address to show the local weather forecast!
- feature 6: Mandatory `android:exported` declaration in `AndroidManifest.xml` (which we studied in doc 020)
- beginner fallacy: i thought dynamic theming was only for Google's first-party apps like Gmail and Calendar
- any third-party Jetpack Compose app can support dynamic theming in 10 lines of code via `dynamicLightColorScheme(context)`!
- Android 12 proved that an operating system can be deeply personal, visually playful, and ruthlessly secure at the same time
