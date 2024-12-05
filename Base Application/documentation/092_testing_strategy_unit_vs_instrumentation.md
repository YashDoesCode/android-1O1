# Testing Strategy - Unit Tests (test/) vs Instrumentation Tests (androidTest/)

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// In Base Application/app/src/:
app/src/
├── main/             <-- Production application code
├── test/             <-- Unit Tests (Runs on local developer host JVM!)
│   └── java/com/keeper/webguard/ExampleUnitTest.kt
└── androidTest/      <-- Instrumentation Tests (Runs on real Android Device/Emulator!)
    └── java/com/keeper/webguard/ExampleInstrumentedTest.kt
```

### Notes, Curious Questions & System Mechanics

- one of the most important architectural divisions in `Base Application` is the separation between `src/test/` and `src/androidTest/`
- many beginners wonder: 'Why does Android Studio create TWO separate test folders? Aren't tests just tests?'
- the two folders represent two fundamentally different testing environments, execution speeds, and verification targets
- Folder 1: `src/test/` (Local Unit Tests)
- tests in `src/test/` run on your development computer's local desktop Java Virtual Machine (macOS, Linux, Windows)
- they do NOT require an Android phone, emulator, or ADB connection to run!
- because they run on the desktop JVM, they execute at lightning speed: hundreds of unit tests can finish in 2 to 5 seconds
- use `src/test/` for pure business logic, data calculations, algorithmic transformations, ViewModels, and repository unit tests
- limitations of `src/test/`: because it runs on a desktop JVM, it has NO access to real Android OS framework classes (`android.os.Bundle`, `Context`)
- if your test calls an Android framework class, it will throw the infamous runtime error: `Method ... not mocked`
- to test Android classes on the desktop JVM without an emulator, developers use 'Robolectric' or mocking libraries like MockK
- Folder 2: `src/androidTest/` (Instrumented / Device Tests)
- tests in `src/androidTest/` are compiled into a separate companion APK (e.g. `app-debug-androidTest.apk`)
- both your app APK and the test APK are installed onto a real physical Android phone or running emulator via ADB
- the test code runs inside the real Android OS process under the control of an instrumentation runner (`androidx.test.runner.AndroidJUnitRunner`)
- use `src/androidTest/` for UI testing with Jetpack Compose (`createComposeRule()`), Espresso click flows, SQLite Room database migrations, and hardware sensor verification
- trade-off: Instrumentation tests are much slower to run (taking 30 seconds to several minutes to boot the emulator and deploy APKs)
- the ideal Android testing pyramid: 70% fast local unit tests in `src/test/`, 20% integration tests, and 10% instrumented UI tests in `src/androidTest/`
- beginner fallacy: i thought i had to run all tests on an emulator to test Android code
- writing decoupled, clean business logic allows you to test 80% of your codebase on the local JVM in seconds without an emulator
- this separation of concerns is a core tenet of professional Android software engineering and continuous integration (CI/CD)
