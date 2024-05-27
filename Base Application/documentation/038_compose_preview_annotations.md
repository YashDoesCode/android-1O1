# Compose Previews - @Preview and @PreviewScreenSizes Tooling

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// In Base Application/app/src/main/java/com/keeper/webguard/MainActivity.kt:
@PreviewScreenSizes
@Composable
fun KeeperWebGuard20App() { ... }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KeeperWebGuard20Theme {
        Greeting("Android")
    }
}
```

### Notes, Curious Questions & System Mechanics

- at the bottom of `MainActivity.kt`, there are two preview functions decorated with `@Preview` annotations
- `@Preview` is a tooling annotation provided by `androidx.compose.ui.tooling.preview`
- it allows Android Studio to render a live, pixel-perfect visual preview of your composables directly inside the IDE without launching an emulator!
- this completely eliminates the painful 2-minute 'build, install, launch emulator' cycle just to verify if padding looks right
- notice `GreetingPreview()` wraps `Greeting("Android")` inside `KeeperWebGuard20Theme { ... }`
- wrapping previews in your app's theme is critical so the preview inherits the correct typography, dark mode colors, and font scales
- `showBackground = true` tells Android Studio to render a white/black canvas background behind the text instead of a transparent checkered grid
- now look at `@PreviewScreenSizes` above `KeeperWebGuard20App()`: this is a powerful multi-preview annotation!
- `@PreviewScreenSizes` automatically renders your composable across 5 distinct device form factors simultaneously:
- 1. Standard Phone in portrait
- 2. Phone in landscape
- 3. Foldable device unfolded (square aspect ratio)
- 4. 7-inch Medium Tablet
- 5. 10-inch Expanded Desktop/Tablet
- in Android Studio's 'Split' or 'Design' tab, you see all 5 screen previews side-by-side in real-time!
- this instantly proves that `NavigationSuiteScaffold` works: you see the bottom bar on the phone and the navigation rail on the tablet simultaneously!
- you can also create custom multi-previews, such as `@PreviewDarkLight` to render dark mode and light mode side-by-side
- preview functions must always have zero parameters (or provide parameter providers via `@PreviewParameter`)
- preview code is strictly isolated by Gradle: remember in `build.gradle.kts` we used `debugImplementation(libs.androidx.compose.ui.tooling)`
- this guarantees that preview rendering machinery is completely stripped from release APKs, adding zero bytes to production downloads
- beginner fallacy: i thought previews were just static screenshots that couldn't handle interaction
- in modern Android Studio, you can click the 'Interactive Mode' button on any preview and actually click buttons, navigate tabs, and test animations live!
- this tooling capability transformed Android frontend development from a sluggish chore into a joyful, rapid feedback loop
- it makes reviewing responsive designs and visual edge-cases faster than ever before
