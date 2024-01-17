# Android Studio Project Views - Project vs Android vs Packages

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Actual filesystem tree in Project View vs flattened virtual Android View
Base Application/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/keeper/webguard/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   └── androidTest/
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
└── settings.gradle.kts
```

### Notes, Curious Questions & System Mechanics

- when i first opened android studio i was only looking at the 'Android' view on the top left dropdown and got totally misled about where files actually live
- the Android view creates this artificial illusion where Gradle Scripts look like they sit right next to the app folder, which is not true physically on disk
- switching to the 'Project' view was a huge lightbulb moment because it finally revealed the true POSIX file hierarchy
- in Project view you see the actual root directory `Base Application/` which contains the top-level configuration files and the submodules
- the `app/` folder is actually just an independent Gradle subproject, not the entire Android application as a monolithic unit
- inside `app/src/` there are three distinct source sets: `main`, `test`, and `androidTest`, which completely separated my production code from test runners
- `src/main/` is the golden folder where production code lives, containing `java/` (even though we write Kotlin here!) and `res/`
- why is the Kotlin code located inside a directory literally called `java/`? because historical Gradle conventions mapped JVM source directories to `src/main/java`
- Android Studio automatically registers `.kt` files inside `src/main/java` without needing a separate `src/main/kotlin` folder, though both work
- Packages View on the other hand groups files purely by their reverse-domain package name `com.keeper.webguard`, hiding folder depth
- if you don't know the difference between Project view and Android view, you will wonder why newly created text or config files don't show up in Android Studio
- Android view deliberately filters out non-standard files and build caches so beginners don't get overwhelmed, but it hides critical system files
- the `.idea/` folder inside `Base Application/` holds IntelliJ workspace state, code style configs, and compiler settings that Android Studio uses
- the `build/` directory only appears after running Gradle and contains all intermediate DEX files, transformed AARs, and compiled APKs
- beginner fallacy i had: i thought the Android operating system directly reads files from `app/src/main/` at runtime on the phone
- reality: the phone never sees this folder structure at all, Gradle packages everything into a compressed archive before the phone touches it
- having multiple modules is possible because `settings.gradle.kts` uses `include(":app")`, so you can have `:core`, `:feature-login`, etc.
- the virtual Android view combines all resource folders (`res/values`, `res/drawable`, `res/mipmap`) into single conceptual folders even across build variants
- in Project view, Gradle wrapper files (`gradlew`, `gradlew.bat`, `gradle/wrapper/`) are visible and show how self-contained the build really is
- understanding this file tree on disk gave me confidence that Android apps are just standard JVM/Kotlin Gradle multi-project builds with specialized plugins
- it completely demystified the whole project for me and made me feel like i actually understand what is happening under the hood
