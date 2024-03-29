# windowSoftInputMode="adjustResize" - Conquering the Virtual Keyboard

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
<!-- Inside AndroidManifest.xml -->
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:theme="@style/Theme.KeeperWebGuard20"
    android:windowSoftInputMode="adjustResize">
    ...
</activity>
```

### Notes, Curious Questions & System Mechanics

- in `Base Application/app/src/main/AndroidManifest.xml`, notice the attribute `android:windowSoftInputMode="adjustResize"`
- the on-screen virtual keyboard (IME - Input Method Editor) is one of the trickiest UX challenges on mobile devices
- when a user taps on a `TextField` or `OutlinedTextField` in Jetpack Compose, the virtual keyboard pops up from the bottom of the screen
- the keyboard typically consumes 40% to 50% of the entire display viewport
- without proper configuration, the keyboard covers up the text input field, preventing the user from seeing what they are typing
- `windowSoftInputMode="adjustResize"` instructs the Android Window Manager to resize the activity's window decor when the keyboard appears
- this allows the `Scaffold` and scrollable containers (like `LazyColumn`) in Compose to adapt their layout to the reduced screen height
- in older Android versions, another option was `adjustPan`, which didn't resize the window but shifted the whole screen up
- `adjustPan` was notorious for hiding the top toolbar or causing ugly visual clipping of UI headers
- with modern edge-to-edge drawing (`enableEdgeToEdge()`), `adjustResize` is the mandatory recommended standard
- Jetpack Compose listens to WindowInsets (`WindowInsets.ime`) to dynamically animate UI padding as the keyboard slides up
- you can use `Modifier.imePadding()` on input forms so buttons automatically float smoothly above the keyboard surface
- you can also combine soft input flags, such as `stateHidden` to ensure the keyboard doesn't open automatically on screen launch
- the Android window manager coordinates with the system input method service over Binder IPC to broadcast keyboard geometry
- beginner fallacy: i thought that keyboard handling was purely a CSS or layout problem inside the Composable function
- the virtual keyboard is an entirely separate system process running outside your application sandbox
- your app cannot resize its window without permission and cooperation from the OS Window Manager via this manifest attribute
- if you omit this attribute, Compose forms on older Android devices can become completely unusable when typing
- this attribute bridges the gap between low-level OS window bounds and high-level Compose reactive layout reflow
- it is one of those small configuration lines that makes the difference between an amateur app and a polished production app
- seeing it included in the base template shows Google's focus on smooth out-of-the-box form ergonomics
