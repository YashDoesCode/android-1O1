# Android 14 Upside Down Cake - Partial Photo Access & Minimum Install SDK

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
<!-- In AndroidManifest.xml: -->
<uses-permission android:name="android.permission.READ_MEDIA_VISUAL_USER_SELECTED" />

// Android 14 Hard Security Gate:
// Android 14 BLOCKS the installation of any APK targeting targetSdk < 23 (Android 5.1 and older)!
// adb install old_app.apk -> INSTALL_FAILED_DEPRECATED_SDK_VERSION!
```

### Notes, Curious Questions & System Mechanics

- Android 14 (API level 34), released in late 2023, introduced aggressive security measures aimed at closing historical malware loopholes
- security milestone 1: Blocking the installation of apps targeting ancient SDKs
- malware authors had a clever trick: they built malicious apps targeting Android 5.1 (API 22) because runtime permissions didn't exist back then!
- when installed on a modern Android 13 phone, the OS used backward compatibility mode and granted the malware all permissions at install time
- Android 14 permanently slammed this door shut: the OS refuses to install ANY APK targeting `targetSdk < 23` (Android 5.1 or older)
- if an attacker tries to sideload an ancient APK, Android Package Manager rejects it with `INSTALL_FAILED_DEPRECATED_SDK_VERSION`
- privacy milestone 2: Partial Photo and Video Access (`READ_MEDIA_VISUAL_USER_SELECTED`)
- in Android 13, apps had to use the Photo Picker to avoid full media access
- Android 14 went further: even if an app requests the standard `READ_MEDIA_IMAGES` permission, the user is presented with a third option: 'Select Photos'
- the user can grant the app access to only 3 specific photos out of their 10,000 photos!
- the app's `MediaStore` queries will physically only return those 3 selected photos; all other photos on the phone remain completely invisible
- security milestone 3: Stricter Intent Redirection and Broadcast Security
- in Android 14, context-registered broadcast receivers MUST explicitly specify whether they are exported: `RECEIVER_EXPORTED` or `RECEIVER_NOT_EXPORTED`
- if you register a receiver without this flag, your app crashes immediately with a `SecurityException`
- this prevents malicious apps on the device from broadcasting fake system events into your internal application components
- Android 14 also introduced Non-Linear Font Scaling up to 200%
- small text scales up rapidly for vision-impaired accessibility, while large titles scale up slowly to prevent breaking UI layouts
- it introduced Predictive Back Gestures, allowing users to peek at the previous screen or home screen as they swipe from the edge
- beginner fallacy: i thought that if an app worked on Android 13, it would automatically pass all Android 14 security rules
- Android 14's strict receiver export rules and partial photo access required thousands of apps to update their codebase contracts
- it represents Android's relentless march toward zero-trust application execution and total user privacy
