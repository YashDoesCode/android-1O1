# SharedPreferences vs Preferences DataStore - Why SharedPreferences is Flawed

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Why SharedPreferences is dangerous in modern apps:
// 1. Synchronous I/O: Calling prefs.edit().commit() blocks the Main Thread -> ANR risk!
// 2. Runtime crashes: Parsing errors throw ClassCastException at runtime!
// 3. No Coroutines/Flow: Hard to observe changes reactively.

// Modern Jetpack Preferences DataStore:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
// 100% Asynchronous, built on Kotlin Coroutines and Flow!
val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
```

### Notes, Curious Questions & System Mechanics

- for over 12 years, `SharedPreferences` was the universal default way to save simple key-value pairs (booleans, strings, integers) in Android
- under the hood, `SharedPreferences` saves data as an unencrypted XML file at `/data/data/<package>/shared_prefs/<name>.xml`
- however, `SharedPreferences` has severe architectural flaws that have caused millions of production bugs and ANR freezes
- flaw 1: Synchronous Disk I/O on the Main Thread
- when you first call `getSharedPreferences()`, Android loads and parses the entire XML file synchronously, blocking the Main Thread until parsing finishes
- if you call `prefs.edit().commit()`, it writes synchronously to flash storage, freezing the UI frame rate and risking an immediate ANR
- flaw 2: Silent Data Loss with `.apply()`
- to avoid blocking the main thread, developers used `.apply()` which queues the write in the background
- BUT if the app process is killed or crashes before the background write flushes to disk, your data is silently lost forever!
- flaw 3: Zero Type Safety
- if key `"age"` was saved as an `Int`, but another part of the code reads it as a `String`, `SharedPreferences` crashes with a fatal `ClassCastException`
- flaw 4: Lack of Reactive Flow Support
- it does not integrate with modern Kotlin Coroutines or StateFlow; its change listener (`OnSharedPreferenceChangeListener`) is weak-referenced and easily garbage collected
- Google officially built 'Jetpack DataStore' as the modern replacement for SharedPreferences
- Preferences DataStore is built from the ground up on Kotlin Coroutines and `Flow`: all reading and writing is 100% non-blocking and asynchronous
- it guarantees transactional consistency: if an operation fails or the phone loses power mid-write, the data file cannot get corrupted
- it exposes data as a reactive `Flow`, which plugs seamlessly into Jetpack Compose via `collectAsState()`
- when a preference updates in DataStore, any composable reading that preference recomposes automatically in real time!
- for complex data structures, DataStore also offers 'Proto DataStore', which uses Protocol Buffers for strict compile-time schema validation
- Google provides an automated migration tool (`SharedPreferencesMigration`) that effortlessly transfers legacy prefs to DataStore in one line
- beginner fallacy: i thought SharedPreferences was good enough for small projects so i shouldn't bother learning DataStore
- modern Android projects should never use SharedPreferences for new code; DataStore is safer, reactive, and completely eliminates disk-related ANRs
- it is another prime example of the Android ecosystem evolving toward type-safe, coroutine-native architecture
