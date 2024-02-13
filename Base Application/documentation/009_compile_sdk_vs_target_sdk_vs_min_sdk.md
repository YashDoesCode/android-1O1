# compileSdk vs targetSdk vs minSdk - The SDK Version Triad

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Base Application/app/build.gradle.kts
android {
    namespace = "com.keeper.webguard"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.keeper.webguard"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}
```

### Notes, Curious Questions & System Mechanics

- the three SDK version numbers (`compileSdk`, `targetSdk`, `minSdk`) are the most misunderstood numbers in the entire Android ecosystem
- in `Base Application`, `compileSdk` is set to 37 (the latest preview/canary release for modern Android features)
- `compileSdk` is purely a compile-time setting for Android Studio and javac/kotlinc on your development machine
- `compileSdk` determines which Android platform APIs, classes, and methods are available for your code to call during compilation
- if `compileSdk = 37`, you can call brand-new Android 15/16 APIs in your Kotlin code without getting compile errors
- importantly, `compileSdk` is NOT included in your compiled APK; the phone running your app has no idea what `compileSdk` was
- `minSdk = 29` means this app requires Android 10 (API level 29) or higher to install and run
- Google Play will physically hide your app from any user whose device is running an OS older than `minSdk 29` (e.g. Android 9 Pie or Oreo)
- if an Android 9 device tries to sideload this APK, the package installer will reject it with `INSTALL_FAILED_OLDER_SDK`
- `minSdk` acts as a hard floor for backward compatibility and determines whether you need runtime SDK checks (`Build.VERSION.SDK_INT >= 29`)
- `targetSdk = 37` is the contract of behavioral compatibility between your app and the Android operating system
- `targetSdk` tells the Android OS: 'I have tested my app against API 37 behaviors and i consent to all new security restrictions of API 37'
- when Android releases a new OS version with breaking changes (like background restrictions or permission changes), it checks `targetSdk`
- if `targetSdk` is lower than the device's OS, Android turns on backward compatibility quirks so older apps don't immediately break
- for example, if `targetSdk < 28`, Android 9 might allow cleartext HTTP, but if `targetSdk >= 28`, Android 9 blocks HTTP strictly
- Google Play requires apps to target recent SDK versions (usually within 1 year of the latest release) to ensure user privacy and security
- beginner fallacy: i thought increasing `targetSdk` would stop the app from running on older phones
- increasing `targetSdk` does NOT change the minimum phone version! The app still runs on `minSdk` (Android 10) even if `targetSdk` is 37
- the relationship is always: `minSdk <= targetSdk <= compileSdk`
- `versionCode = 1` is an internal integer that must increment with every Google Play release; `versionName = "1.0"` is the string users see
- understanding this triad removed 90% of my confusion regarding Android versioning and platform deprecation
