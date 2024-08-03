# Android 9 Pie Display Cutout & Notches - Designing for Irregular Screens

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// In AndroidManifest.xml (Activity or Application tag):
// android:windowLayoutInDisplayCutoutMode = "default" | "shortEdges" | "never"

// In Jetpack Compose:
// Scaffold automatically consumes WindowInsets.displayCutout!
Box(modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout)) {
    // Content is guaranteed never to be obscured by camera hole-punch or notch!
}
```

### Notes, Curious Questions & System Mechanics

- in late 2017, the smartphone industry underwent a hardware design revolution: physical bezels disappeared and display notches arrived
- suddenly, phone screens were no longer simple rectangular boxes: they had camera cutouts, waterdrop notches, hole-punches, and curved corners
- early Android devices with notches (like early Huawei and Essential phones) had zero OS-level notch standards
- apps were rendering critical buttons directly behind the front-facing camera lens, making them completely impossible to tap!
- Android 9 Pie introduced the official `DisplayCutout` API, bringing first-class operating system support for irregular screen geometries
- Pie introduced the manifest attribute `android:windowLayoutInDisplayCutoutMode` with three primary modes:
- Mode 1: `default` - Content renders below the cutout in portrait, and letterboxes (black bars) in landscape so the notch never touches UI
- Mode 2: `shortEdges` - The app window extends completely into the cutout area on both portrait and landscape short edges (full edge-to-edge)
- Mode 3: `never` - The window is strictly forbidden from ever extending into the cutout area
- in modern Android (especially Android 15's mandatory edge-to-edge), `shortEdges` is the modern standard
- the OS provides the `DisplayCutout` class, which exposes `boundingRectTop`, `boundingRectLeft`, and `safeInsets`
- these bounding rectangles tell your layout engine the exact pixel coordinates and dimensions of the camera punch-hole
- in modern Jetpack Compose, you don't even need to write math calculations: Compose provides `WindowInsets.displayCutout`
- when you use `Scaffold(modifier = Modifier.fillMaxSize())` as seen in `MainActivity.kt`, it coordinates with `displayCutout` automatically
- it applies padding so your text and navigation buttons remain in the 'safe zone' while background colors bleed gracefully around the camera
- Android Studio's emulator even has a setting in Developer Options called 'Simulate display with cutout' (Tall cutout, Corner cutout, Double cutout)
- this allows developers to test their layouts against wild notch shapes without buying 10 physical phones
- beginner fallacy: i thought screen width and height were simple constants like 1080x1920
- modern mobile screens have notches, rounded corners, dynamic navigation bars, and folding hinges that must be queried dynamically
- Android 9's cutout API was the critical bridge that allowed Android apps to transition gracefully from boxy bezels to all-screen displays
- it is another reason why Android 9 was such a monumental release for modern frontend architecture
