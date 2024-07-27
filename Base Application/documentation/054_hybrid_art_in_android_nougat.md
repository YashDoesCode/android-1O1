# Hybrid ART in Android 7 Nougat - JIT, AOT, and Profile-Guided Compilation

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Android 7.0 Nougat's Hybrid JIT + AOT Architecture:
// 1. Install APK -> INSTANT install! (No dex2oat compilation, pure DEX saved)
// 2. First Run   -> Fast JIT interpreter runs code immediately, records 'Profile'
// 3. Profile     -> ART logs which methods are called frequently (Hot code)
// 4. Overnight   -> Device is charging & idle -> Background AOT compiles ONLY hot methods!
// 5. Next Run    -> App runs at full native speed for screens you actually use!
```

### Notes, Curious Questions & System Mechanics

- to fix the long install times and storage bloat of Lollipop's pure AOT, Google engineered a masterstroke in Android 7.0 Nougat (API 24)
- they introduced the 'Hybrid Compilation Model', combining the best of Dalvik's JIT and ART's AOT into a unified system
- step 1: Instant Installation
- when you install an app on Android 7+, `dex2oat` does NOT compile the entire app! The APK installs in 2 seconds with zero delay
- the dreaded 'Android is upgrading... Optimizing apps' screen was permanently eradicated from the operating system
- step 2: JIT Interpretation with Profiling
- when the user opens `Base Application` for the first time, ART executes the DEX bytecode using a high-speed JIT interpreter
- while running, ART actively monitors execution and generates a 'Profile' (`.prof` file)
- the profile records which classes and methods are actually executed by the user (the 'Hot Code')
- think about it: in a typical 50 MB app, an average user only touches 20% of the code! The other 80% (registration, settings, edge cases) is rarely used
- compiling that unused 80% to native machine code during installation was a massive waste of CPU, battery, and storage space
- step 3: Background Profile-Guided AOT Compilation
- when the user plugs their phone into the charger at night and the phone is idle, Android runs a background maintenance job
- the job reads the recorded `.prof` profile and calls `dex2oat` to AOT-compile ONLY the hot methods into native code!
- rarely used code remains in DEX format to save storage, while critical UI code (like `MainActivity.onCreate`) runs as pure native machine code
- over time, as the user interacts with the app, the profile grows smarter and the app becomes faster and more responsive
- this hybrid architecture achieved the holy grail: instantaneous app installation, minimal storage usage, and maximum runtime performance
- it is one of the most sophisticated runtime compilation architectures ever deployed to billions of consumer devices
- beginner fallacy: i thought that if an app was fast on day 1, it couldn't get any faster
- on Android, an app actually gets FASTER after you use it for 3 days as ART learns your usage patterns and optimizes the hot path!
- understanding Profile-Guided Compilation explained why Android performance feels so smooth on modern devices
