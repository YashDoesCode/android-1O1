# APK vs Android App Bundle (.aab) - Dynamic Delivery via Google Play

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Traditional Fat APK (Shipped to all devices):
// Contains: arm64-v8a + armeabi-v7a + x86_64 + mdpi + hdpi + xxhdpi + all 50 languages!
// Download Size: 85 MB! (60% of which is completely useless to the user's specific phone!)

// Modern Android App Bundle (.aab) + Dynamic Split APKs:
// Google Play generates custom Split APKs tailored specifically to user's device:
// - Base APK (Code + core assets)           : 15 MB
// - ABI Split APK (ONLY arm64-v8a native lib):  5 MB
// - Density Split APK (ONLY xxhdpi drawables):  4 MB
// - Language Split APK (ONLY English string) :  1 MB
// Total User Download: 25 MB! (Over 65% size reduction!)
```

### Notes, Curious Questions & System Mechanics

- for the first decade of Android, developers built and uploaded a single file format to Google Play: the APK (Android Package)
- the traditional APK was a 'Fat Binary': it had to contain compiled native C++ libraries for every processor architecture (ARMv7, ARM64, x86, x86_64)
- it had to bundle images across all 5 screen density buckets (`mdpi` to `xxxhdpi`), and strings for all 50 supported languages
- this meant a user downloading an app on a standard ARM64 1080p English phone was forced to download 50 megabytes of useless x86 libraries and Spanish strings!
- in 2018, Google introduced the 'Android App Bundle' (`.aab`) format and made it mandatory for all new apps submitted to Google Play in 2021
- an App Bundle is NOT an executable file that can be installed directly on a phone; it is an upload publishing format
- when you run `./gradlew bundleRelease`, Gradle packages your compiled DEX, resources, and native libraries into a `.aab` archive
- you upload the `.aab` to Google Play Console; Google Play's servers take over and run a tool called `bundletool`
- when a user hits 'Install' on Google Play, Google Play inspects the user's exact phone hardware:
- 1. CPU Architecture: ARM64-v8a
- 2. Screen Density: xxhdpi
- 3. System Language: English
- Google Play dynamically slices your App Bundle and generates a custom set of 'Split APKs' tailored exclusively for that user's hardware!
- the user downloads a lightweight Base APK, a tiny CPU split APK, and a tiny density split APK
- the user saves 35% to 65% on download bandwidth, install storage, and update patch sizes
- if the user later changes their phone language to Japanese in Android settings, Google Play silently downloads the Japanese language split APK in the background!
- App Bundles also enable 'Dynamic Feature Modules': modules that are not installed on initial download, but downloaded on-demand only when a user opens a rare feature (like an advanced photo editor)
- for local debugging and testing on your own phone or emulator, Gradle still builds standard `.apk` files directly (`app-debug.apk`)
- beginner fallacy: i thought i could send a `.aab` file to my friend via WhatsApp or email to test on their phone
- the Android OS Package Installer cannot install a `.aab` file directly; you must either generate an APK using `bundletool` or build an APK via `./gradlew assembleDebug`
- the Android App Bundle revolutionized mobile app distribution, saving petabytes of bandwidth worldwide and making app downloads lightning fast
