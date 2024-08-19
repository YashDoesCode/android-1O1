# Android 10 Scoped Storage - Ending the Wild West of SD Card Access

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// How storage worked before Android 10 (The Wild West):
// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
// App could read/write EVERY file on the entire SD card: /sdcard/Photos, /sdcard/Downloads!

// Android 10+ Scoped Storage (Zero-trust isolation):
// 1. App-specific storage: context.filesDir (Zero permissions needed!)
// 2. Shared Media: MediaStore API (Photos, Videos, Audio)
// 3. Document files: Storage Access Framework (SAF) system picker
```

### Notes, Curious Questions & System Mechanics

- before Android 10, file storage on Android was an absolute wild west
- an app would ask for `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
- if the user granted that permission, the app gained unrestricted read and write access to the entire shared internal storage and SD card!
- a flashlight app or casual game could silently scan `/sdcard/DCIM/Camera`, read all your private personal photos, and upload them to a rogue server
- apps also littered the storage with messy folders (`/sdcard/MyApp_cache`, `/sdcard/temp`), and when you uninstalled the app, those junk files stayed forever
- in Android 10 Q (API 29), Google introduced 'Scoped Storage', completely ending raw filesystem access for third-party apps
- Scoped Storage enforces strict privacy silos based on the principle of least privilege:
- Silo 1: App-Specific Storage (`context.filesDir` and `context.cacheDir`)
- this is your app's private sandbox located at `/data/data/<package>/files`; NO permissions are needed, and files are automatically deleted when the app is uninstalled
- Silo 2: Shared Media Collections via `MediaStore`
- if your app wants to save or read photos, videos, or audio, it must use the `MediaStore` API (`MediaStore.Images`, `MediaStore.Video`)
- your app can save photos to the shared gallery with ZERO permissions! But it cannot read photos taken by OTHER apps without explicit permission
- Silo 3: General Documents via Storage Access Framework (SAF)
- if an app wants to open a PDF or ZIP file from the Downloads folder, it cannot access the file path directly; it must launch the system file picker (`Intent.ACTION_OPEN_DOCUMENT`)
- the user explicitly selects the file in the system UI, and Android grants your app a temporary, cryptographically signed content `Uri`
- in Android 10, Google included a temporary opt-out flag: `android:requestLegacyExternalStorage="true"`
- in Android 11, that flag was permanently disabled for apps targeting API 30+, making Scoped Storage mandatory for everyone
- beginner fallacy: i thought i could still use standard Java `java.io.File("/sdcard/myfile.txt")` to save files
- calling `File.createNewFile()` on root storage will immediately throw an `AccessDeniedException` or `FileNotFoundException` on modern Android
- Scoped Storage protected millions of users from spyware and cleaned up device storage management
- it is one of the most critical privacy architecture changes in modern mobile development
