# Android 101 - Standalone Application Suite

This repository contains modern, self-contained Android applications engineered for learning, reference, and clean architectural best practices. Each application resides in its own isolated subfolder under `/apps/` with its own standalone Gradle configuration (`settings.gradle.kts`, `build.gradle.kts`, Gradle Wrapper, and Version Catalog), allowing it to be opened directly in Android Studio.

---

## 📱 Application Index

| Application | Path | Min SDK | Target / Compile SDK | Key Architecture & Tech Stack | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Scientific Calculator** | [`/apps/calculator`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/calculator) | 28 (Pie) | 35 (Android 15) | Jetpack Compose (M3), MVVM, Shunting-Yard Domain Engine, Haptics | ✅ Phase 1 Complete |
| **Gallery & Media** | [`/apps/gallery`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/gallery) | 28 (Pie) | 35 (Android 15) | MediaStore API, Coil, Room Cache, Pinch-to-Zoom Pager, Permissions Matrix | ✅ Phase 2 Complete |
| **Browser (Full)** | [`/apps/browser`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/browser) | 28 (Pie) | 35 (Android 15) | Multi-tab WebView, Room Bookmarks/History, DownloadManager | ✅ Phase 3 Complete |
| **Browser Lite** | [`/apps/browser`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/browser) | 28 (Pie) | 35 (Android 15) | Single-tab, Data-Saver Mode, Shared Domain Layer | ✅ Phase 3 Complete |
| **Comms (Dialer + Mail)** | [`/apps/comms`](file:///Users/yashsrivastava/File%20System/Coding/Android%20Development/android-1O1/apps/comms) | 28 (Pie) | 35 (Android 15) | `ContactsContract`, `CallLog`, Room Cache, T9 Dialpad, Intent Actions, In-Memory Mail Engine | ✅ Phase 4 Complete |
| **Banking Suite** | `/apps/banking` | 28 (Pie) | 35 (Android 15) | Offline Room DB, Financial Transfers, Category Analytics, Custom Canvas Charts | ⏳ Phase 5 |

---

## 🛠️ How to Open & Run in Android Studio

Each app is an independent Gradle root project. To open an app in Android Studio:

1. Launch **Android Studio** (Ladybug / Meerkat or later).
2. Select **File -> Open...** (or choose **Open** from the Welcome screen).
3. Navigate to `apps/<app_name>` (e.g. `apps/calculator`) and select **Open**.
4. Allow Gradle to sync and resolve dependencies.
5. Select the **app** configuration and click **Run (Shift + F10)** on an emulator or connected device running Android 9.0 (API 28) or higher.

---

## 🏗️ Architectural Foundations

All apps in this suite follow strict architectural principles:

- **Unidirectional Data Flow (UDF)**: UI states are represented as immutable data structures and observed via Kotlin Coroutines `StateFlow`.
- **Clean Layering**: Clear separation of concerns between **UI** (Jetpack Compose / Material 3), **Domain** (pure Kotlin use cases and models without Android framework ties), and **Data** (Room repositories and platform data sources).
- **Modern Build Tooling**: Gradle Kotlin DSL (`.gradle.kts`), Gradle Version Catalog (`libs.versions.toml`), and JDK 21+ toolchains.
