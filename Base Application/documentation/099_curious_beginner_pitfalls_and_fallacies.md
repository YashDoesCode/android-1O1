# Top 20 Beginner Pitfalls and Fallacies When Learning Android Development

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// The 20 Commandments of Modern Android Architecture:
// 1. Don't block the Main Thread (Use Coroutines + Dispatchers.IO)
// 2. Don't store UI state in Activity member variables (Use rememberSaveable / ViewModel)
// 3. Don't hardcode strings in Kotlin code (Use res/values/strings.xml)
// 4. Don't hardcode colors without alpha (Color(0xFF...) requires FF for opacity!)
// 5. Don't assume the app process lives forever (Design for Process Death & LMKD)
// 6. Don't pass large bitmaps through Intents (Binder buffer is limited to ~1 MB)
// 7. Don't use SharedPreferences for new code (Use Preferences DataStore)
// 8. Don't use raw background Services (Use WorkManager for guaranteed work)
// 9. Don't request dangerous permissions in onCreate() (Request contextually)
// 10. Don't ignore Scaffold innerPadding in edge-to-edge layouts!
```

### Notes, Curious Questions & System Mechanics

- learning Android development can feel overwhelming because the platform combines 15 years of evolutionary history with bleeding-edge modern reactive patterns
- here are the top 20 real-world fallacies and pitfalls every beginner developer stumbles into when studying the system:
- pitfall 1: Confusing Project View with Android View in Android Studio and wondering why build files or assets aren't appearing on disk
- pitfall 2: Forgetting the Alpha channel in Compose `Color(0x123456)` and spending hours debugging why buttons and text are 100% invisible
- pitfall 3: Storing form state in a plain `var text = ""` inside `MainActivity` and wondering why rotating the phone erases everything the user typed
- pitfall 4: Making a network HTTP call directly inside `onCreate()` and crashing with an immediate `NetworkOnMainThreadException`
- pitfall 5: Trying to make an unencrypted `http://` call on Android 9+ and wondering why the connection fails without realizing cleartext HTTP is banned
- pitfall 6: Passing a 5 MB image bitmap as an Intent extra and crashing with a fatal `TransactionTooLargeException` in the Binder driver
- pitfall 7: Forgetting `android:exported="true"` or `"false"` on Activity declarations and failing to build on modern Android 12+ toolchains
- pitfall 8: Assuming an Activity is a single screen and attempting to create 30 Activity classes instead of using single-activity Compose navigation
- pitfall 9: Thinking that closing an app or pressing the Home button exits the process like closing a desktop window
- pitfall 10: Ignoring `innerPadding` from `Scaffold` and having text render underneath the physical camera cutout or status bar clock
- pitfall 11: Swapping modifier order (putting `.clickable` before `.padding`) and wondering why touch ripples behave unpredictably
- pitfall 12: Assuming `allowBackup="true"` is harmless and accidentally backing up secret auth tokens and database passwords to Google Drive
- pitfall 13: Using `GlobalScope.launch` for background work and having tasks abruptly terminated when the user switches apps
- pitfall 14: Modifying the generated `R.java` file manually and having all changes wiped out on the next Gradle sync
- pitfall 15: Putting large photographs in `res/drawable` without density buckets and running out of RAM on budget phones
- pitfall 16: Putting general app graphics in `res/mipmap` instead of reserving it exclusively for launcher icons
- pitfall 17: Forgetting to call `super.onCreate(savedInstanceState)` in an Activity and crashing with a `SuperNotCalledException`
- pitfall 18: Thinking that Kotlin eliminated Java from Android when the entire underlying framework OS is Java and C++
- pitfall 19: Assuming that granted permissions last forever without checking `ContextCompat.checkSelfPermission` before sensor calls
- pitfall 20: Thinking Android is just desktop Linux with a touch skin, ignoring Bionic libc, Binder IPC, Zygote, and SELinux sandboxes
- reviewing this list of fallacies periodically will save you hundreds of hours of debugging frustration and elevate your architectural maturity
