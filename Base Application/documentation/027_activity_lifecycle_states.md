# The Activity Lifecycle States - The 7 Callbacks and Process Death

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// The 7 Fundamental Activity Lifecycle Callbacks:
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }
    override fun onStart() { super.onStart() }
    override fun onResume() { super.onResume() }
    override fun onPause() { super.onPause() }
    override fun onStop() { super.onStop() }
    override fun onDestroy() { super.onDestroy() }
    override fun onRestart() { super.onRestart() }
}
```

### Notes, Curious Questions & System Mechanics

- the Activity Lifecycle is the single most tested concept in Android development and interview questions for good reason
- mobile devices have constrained RAM and battery: users receive phone calls, switch apps, or lock their screens at any split second
- the Android OS must be able to pause, stop, or kill your application at any time without asking for permission
- `onCreate()` is called when the activity is first instantiated; this is where UI setup (`setContent`) and one-time initialization occur
- `onStart()` is called when the activity becomes visible to the user on screen, but is not yet interactive (e.g. under a dialog)
- `onResume()` is called when the activity enters the foreground and has user input focus; animations start and sensors register
- `onPause()` is called when the activity loses focus (e.g. a phone call dialog appears, or split-screen multi-window is active)
- in `onPause()`, you should pause CPU-heavy animations or video playback to conserve battery
- `onStop()` is called when the activity is completely hidden from view (e.g. user pressed Home or switched to WhatsApp)
- while in `onStop()`, your app process is still alive in RAM, but it is placed in the background LRU (Least Recently Used) cache
- `onDestroy()` is called when the activity is finishing (user swiped it away) or when the OS destroys it for a configuration change (rotation)
- `onRestart()` is called when the activity was stopped and is coming back to the foreground before `onStart()`
- the biggest gotcha: screen rotation (portrait to landscape) by default DESTROYS the activity and CREATES a brand new one!
- when you rotate the phone, the OS calls `onPause() -> onStop() -> onDestroy()` and immediately calls `onCreate() -> onStart() -> onResume()`
- if you stored data in a plain variable like `var count = 0`, it gets reset to 0 every time the user turns their phone!
- that is why Android created `ViewModel` and `rememberSaveable`: they are designed to survive this configuration destruction cycle
- even scarier than rotation is 'Low Memory Killer' (LMKD): if another heavy game needs RAM, Android kills your stopped process silently
- when the user switches back, Android recreates `MainActivity` from scratch and passes the saved `Bundle` into `onCreate(savedInstanceState)`
- beginner fallacy: i thought that when an app is in the background, it just pauses execution like a minimized desktop window
- mobile OS lifecycle is aggressive and volatile; your code must be designed from day one to handle unexpected death and rebirth
- mastering this lifecycle state machine is what separates amateur Android coders from senior engineers
