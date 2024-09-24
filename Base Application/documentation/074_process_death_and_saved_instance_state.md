# Process Death vs Activity Destruction - Low Memory Killer (LMKD) & OOM Scores

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// How Android decides which process to kill when RAM runs low:
// oom_score_adj (Out Of Memory Adjustment Score):
// Foreground App (User actively looking at it)  -> Score: 0 (Almost NEVER killed!)
// Visible App (Partially visible under dialog)   -> Score: 100
// Perceptible App (Playing background music)     -> Score: 200
// Previous App (App user just switched away from)-> Score: 700
// Cached Background App (In recents, not active) -> Score: 900 - 1000 (Killed FIRST!)
```

### Notes, Curious Questions & System Mechanics

- one of the most insidious bugs in Android development is failing to understand the difference between 'Activity Destruction' and 'Process Death'
- most developers test screen rotation in Android Studio and think: 'Great, my state survives rotation, my app is bug-free!'
- screen rotation is merely an Activity recreation inside an app process that stays continuously alive in RAM
- the real killer on Android is 'Low Memory Killer Daemon' (LMKD)
- suppose a user is using `Base Application`, fills in half of a registration form, and then temporarily switches to Instagram or launches a heavy 3D game
- our app moves to the background; its Activity is stopped (`onStop()`), but its Linux process is still resting in RAM
- as the user plays the heavy 3D game, the phone's RAM fills up to 95% capacity
- the Android Linux kernel's Low Memory Killer daemon wakes up and inspects the `/proc/<pid>/oom_score_adj` of all running processes
- LMKD ruthlessly terminates our app's entire Linux process with `SIGKILL` without calling `onDestroy()`!
- the entire process memory space, all static variables, singletons, and in-memory caches are wiped from existence instantly
- ten minutes later, the user opens the Recents screen and taps on our app again
- what does the Android OS do? It does NOT show a crash dialog! It silently creates a brand-new Linux process from Zygote!
- it re-instantiates `MainActivity` and passes the saved `Bundle` into `onCreate(savedInstanceState)`
- if you stored the form data in a plain variable or in a singleton, it is GONE, and the user sees a blank form or experiences a crash!
- this is why `rememberSaveable` (studied in doc 035) and `SavedStateHandle` in ViewModels are so essential:
- they automatically serialize critical UI state into the OS `savedInstanceState` Bundle BEFORE the process is killed
- when the process is reincarnated, the state is restored seamlessly, and the user has no idea their app was dead for 10 minutes!
- Android Studio allows you to test this exact scenario: in the 'Logcat' or 'Process' tab, click the skull icon labeled 'Kill Process'
- then tap the app icon on the emulator: if your app crashes or loses its state, you have a process death bug!
- beginner fallacy: i thought that keeping data in a static companion object or singleton was safe as long as the phone wasn't rebooted
- Android kills background processes constantly throughout the day to free up RAM; your app must be architected for resilience
- mastering process death survival is the hallmark of a truly battle-tested senior Android engineer
