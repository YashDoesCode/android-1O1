# Adaptive Icons - Splitting Foreground and Background Layers

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
<!-- Base Application/app/src/main/res/mipmap-anydpi/ic_launcher.xml -->
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

### Notes, Curious Questions & System Mechanics

- in `Base Application/app/src/main/res/mipmap-anydpi/`, look at `ic_launcher.xml`
- it does not contain an image; it defines an `<adaptive-icon>` with three layers: `<background>`, `<foreground>`, and `<monochrome>`
- Adaptive Icons were introduced in Android 8.0 Oreo (API 26) to solve a huge visual chaos problem across Android OEMs
- before Android 8, some apps had square icons, some had circular icons, some had freeform shaped icons with shadows
- home screens looked messy and inconsistent: Samsung launchers wanted squircle shapes, Google Pixel wanted circles, OnePlus wanted squares
- Adaptive Icons solved this by requiring developers to provide TWO distinct visual layers: a background layer and a foreground layer
- both layers are 108dp x 108dp, but the outer 18dp on each side is considered a 'bleed zone' that may be masked out by the launcher
- the center 72dp x 72dp circle is the safe zone where the core logo or graphic must reside
- the phone launcher can now apply ANY shape mask it wants: circle, squircle, rounded square, teardrop, or hexagon!
- the system can also animate the foreground independently from the background, creating physics-based parallax and spring effects as you drag icons
- look at the third tag: `<monochrome android:drawable="@drawable/ic_launcher_foreground" />`
- monochrome icons were added in Android 13 (API 33) for 'Themed Icons' in Material You
- when a user enables Themed Icons in Android settings, Android tints your monochrome icon using colors sampled from the user's wallpaper!
- if an app lacks the `<monochrome>` tag, its icon remains colored, standing out awkwardly in a minimalist themed home screen
- in `Base Application`, `@drawable/ic_launcher_background` and `@drawable/ic_launcher_foreground` are pure vector XML files
- having them in `mipmap-anydpi/` tells Android: 'On any device running Android 8+, ignore raster PNG/WebP files and use this adaptive vector icon'
- on devices older than Android 8 (e.g. Android 7 Nougat), Android falls back to reading the bitmap icons inside `mipmap-xxhdpi/`
- beginner fallacy: i thought an adaptive icon was just a single PNG file with transparent edges
- flattening foreground and background into a single PNG breaks parallax animations and prevents OEM shape masking
- studying this file showed how deeply Android OS design balances platform visual consistency with developer customization
- it is one of the most elegant UI framework inventions in mobile operating system history
