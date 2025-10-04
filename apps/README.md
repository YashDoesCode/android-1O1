# Android 101 - Standalone Application Suite

This repository contains modern, self-contained Android applications and shared libraries engineered for learning, reference, and clean architectural best practices. Each application resides in its own isolated subfolder under `/apps/` with its own standalone Gradle configuration (`settings.gradle.kts`, `build.gradle.kts`, Gradle Wrapper, and Version Catalog), allowing it to be opened directly in Android Studio.

---

## 📱 Application & Library Index

| Project | Path | Min SDK | Target / Compile SDK | Key Architecture & Tech Stack | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Design System** | [`/apps/design-system`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/design-system) | 28 (Pie) | 35 (Android 15) | Material 3 Theme, Typography, Reusable Components (`PrimaryButton`, `AppCard`, `SectionHeader`), State Scaffolds (`LoadingState`, `EmptyState`, `ErrorState`), Composite Build Support | ✅ Phase 6 Complete |
| **Scientific Calculator** | [`/apps/calculator`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/calculator) | 28 (Pie) | 35 (Android 15) | Jetpack Compose (M3), MVVM, Shunting-Yard Domain Engine, Haptics | ✅ Phase 1 Complete |
| **Gallery & Media** | [`/apps/gallery`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/gallery) | 28 (Pie) | 35 (Android 15) | MediaStore API, Coil, Room Cache, Pinch-to-Zoom Pager, Permissions Matrix | ✅ Phase 2 Complete |
| **Browser (Full)** | [`/apps/browser`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/browser) | 28 (Pie) | 35 (Android 15) | Multi-tab WebView, Room Bookmarks/History, DownloadManager | ✅ Phase 3 Complete |
| **Browser Lite** | [`/apps/browser`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/browser) | 28 (Pie) | 35 (Android 15) | Single-tab, Data-Saver Mode, Shared Domain Layer | ✅ Phase 3 Complete |
| **Comms (Dialer + Mail)** | [`/apps/comms`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/comms) | 28 (Pie) | 35 (Android 15) | `ContactsContract`, `CallLog`, Room Cache, T9 Dialpad, Intent Actions, In-Memory Mail Engine | ✅ Phase 4 Complete |
| **Banking Suite** | [`/apps/banking`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/banking) | 28 (Pie) | 35 (Android 15) | Offline Room DB, Minor Units (`Long`), Atomic `withTransaction` Transfers, Category Analytics, Custom Compose Canvas Donut Chart | ✅ Phase 5 Complete |

---

## 🛠️ How to Open & Run in Android Studio

Each project is an independent Gradle root. To open any app or library in Android Studio:

1. Launch **Android Studio** (Ladybug / Meerkat or later).
2. Select **File -> Open...** (or choose **Open** from the Welcome screen).
3. Navigate to `apps/<app_name>` (e.g. `apps/calculator` or `apps/design-system`) and select **Open**.
4. Allow Gradle to sync and resolve dependencies.
5. Select the **app** (or library) configuration and click **Run (Shift + F10)** on an emulator or connected device running Android 9.0 (API 28) or higher.

---

## 🧩 Shared Design System & Composite Builds

All applications consume the shared `/apps/design-system` module via Gradle **Composite Builds**.

In each application's `settings.gradle.kts`:
```kotlin
includeBuild("../design-system") {
    dependencySubstitution {
        substitute(module("com.YashDoesCode:design-system")).using(project(":design-system"))
    }
}
```

This enables:
1. **Isolated Development**: `/apps/design-system` opens and builds independently as a pure Android library.
2. **Zero Monorepo Lock-in**: Apps stay self-contained with their own wrapper, version catalog, and settings while seamlessly resolving the shared design system directly from source.

---

## 🏗️ Architectural Foundations

All apps in this suite follow strict architectural principles:

- **Unidirectional Data Flow (UDF)**: UI states are represented as immutable data structures and observed via Kotlin Coroutines `StateFlow`.
- **Clean Layering**: Clear separation of concerns between **UI** (Jetpack Compose / Material 3), **Domain** (pure Kotlin use cases and models without Android framework ties), and **Data** (Room repositories and platform data sources).
- **Modern Build Tooling**: Gradle Kotlin DSL (`.gradle.kts`), Gradle Version Catalog (`libs.versions.toml`), and JDK 21+ toolchains.
