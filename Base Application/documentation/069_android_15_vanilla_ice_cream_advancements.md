# Android 15 Vanilla Ice Cream - 16KB Page Sizes & Private Space

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// In Base Application/app/build.gradle.kts:
compileSdk {
    version = release(37) // Modern Android 15 / 16 Canary APIs!
}

// Android 15 Major Milestones:
// 1. Mandatory Edge-to-Edge: Apps can no longer opt out!
// 2. 16 KB Memory Page Size Support: Transitioning from traditional 4 KB pages!
// 3. Private Space: OS-level hidden vault for sensitive apps!
// 4. Low Light Boost & In-App Camera Controls
```

### Notes, Curious Questions & System Mechanics

- Android 15 (API level 35), codenamed 'Vanilla Ice Cream' (released late 2024), is the modern benchmark of the platform
- our `Base Application` is configured with `compileSdk { version = release(37) }`, ready for Android 15 and preview APIs
- advancement 1: Mandatory Edge-to-Edge Drawing
- in Android 15, apps targeting API 35 are forced to draw edge-to-edge by default; the old system bar padding hacks are deprecated and ignored
- this is why `MainActivity.kt` in `Base Application` includes `enableEdgeToEdge()` right in `onCreate()`!
- advancement 2: The 16 KB Memory Page Size Revolution
- for 30 years, Linux kernels and ARM mobile chips operated using 4 KB virtual memory page sizes
- Android 15 introduces support for 16 KB memory pages, which improves overall system performance, app launch speed, and power efficiency
- however, apps containing native C/C++ libraries (`.so` files compiled with NDK) MUST be recompiled with 16 KB page alignment
- apps with unaligned 16 KB native libraries will crash immediately when run on 16 KB-enabled Android 15 devices
- Google is making 16 KB page compliance mandatory for all Google Play submissions starting in 2025
- advancement 3: 'Private Space' (The Built-In App Vault)
- Android 15 allows users to create a hidden, separate profile on their phone called Private Space (for banking, health, or private messaging apps)
- when Private Space is locked, all apps inside it are completely stopped, notifications are suppressed, and they vanish from the launcher and settings
- Private Space runs under a separate Android user profile with isolated encryption keys
- advancement 4: Low Light Boost in Camera2 API
- gives apps direct algorithmic control to brighten the viewfinder in real-time under extreme low-light conditions
- advancement 5: Partial Screen Sharing
- users can share or record a single application window rather than their entire phone screen, keeping notification popups private during screen shares
- beginner fallacy: i thought Android 15 was just an incremental polish update without architectural impact
- the 16 KB memory alignment shift and mandatory edge-to-edge enforcement are monumental changes requiring deep engineering care
- our `Base Application` template is already architected to comply with these modern Android 15 platform standards out of the box
- it demonstrates how starting with modern Google-approved templates protects you from future technical debt
