# res/values/colors.xml & themes.xml - Backward Compatibility XML Themes

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
<!-- Base Application/app/src/main/res/values/themes.xml -->
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.KeeperWebGuard20" parent="android:Theme.Material.Light.NoActionBar">
        <!-- Customize your light theme here. -->
    </style>
</resources>
```

### Notes, Curious Questions & System Mechanics

- in `Base Application/app/src/main/res/values/`, look at `themes.xml` and `colors.xml`
- wait a minute: earlier we saw that Jetpack Compose defines its theme in `ui/theme/Theme.kt` and colors in `ui/theme/Color.kt`
- why do we still have XML themes and colors in `res/values/` when our whole UI is written in Jetpack Compose?
- this is one of the most subtle architectural necessities in modern Android development
- remember what happens when a user taps your app icon on the home screen:
- the Android OS has to spawn a new Linux process, initialize ART runtime, load APK classes, and execute `onCreate()`
- this process startup takes anywhere from 100ms to 400ms on real mobile hardware (a 'Cold Start')
- during those 300 milliseconds, your Kotlin code in `MainActivity.kt` and Jetpack Compose have NOT run yet!
- what does the Android OS display on screen during those 300 milliseconds before your first Composable draws?
- it displays a window background drawn directly from `android:theme="@style/Theme.KeeperWebGuard20"` declared in `AndroidManifest.xml`!
- if you delete `themes.xml` or don't set a theme in the manifest, the phone will display a jarring white flash or black screen on cold start
- the XML theme provides the system window attributes: window background color, status bar color, and splash screen brand icon
- notice `parent="android:Theme.Material.Light.NoActionBar"`: this tells the OS window not to draw the old legacy ActionBar toolbar
- if you left the default ActionBar on, an ugly blue bar would appear for a split second before Compose drew your `Scaffold`!
- Android 12+ introduced the `androidx.core:core-splashscreen` library, which configures the system splash animation entirely via this XML theme
- `colors.xml` defines legacy XML color tokens like `<color name="black">#FF000000</color>` for system window drawables
- beginner fallacy: i thought that because Compose is modern, XML files could be 100% deleted from the repo
- the Android operating system window manager operates at a layer below Jetpack Compose and requires this XML theme contract
- once `MainActivity.onCreate()` executes and `setContent` runs, Compose takes over the canvas and draws the dynamic Material 3 theme
- this dual-theme setup ensures a seamless, flicker-free visual transition from OS launch window to interactive Compose UI
- it is a brilliant example of engineering harmony between legacy OS window management and modern UI runtimes
