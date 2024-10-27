# Storage Architecture - Internal vs External, filesDir, cacheDir, MediaStore, and SAF

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Android Storage Hierarchy in Code:
// 1. Internal Storage:
val internalFile = File(context.filesDir, "secret_data.txt")  // /data/user/0/<pkg>/files
val cacheFile    = File(context.cacheDir, "temp_image.jpg")   // OS can delete when disk full!

// 2. App-Specific External:
val extFile      = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

// 3. Shared Public Storage:
// Access strictly through MediaStore or Storage Access Framework (SAF)!
```

### Notes, Curious Questions & System Mechanics

- storage in Android is split into two fundamentally different physical and conceptual realms: Internal Storage and External Storage
- Realm 1: Internal Storage (`context.filesDir` and `context.cacheDir`)
- internal storage is located in the root flash partition at `/data/data/<package>/` (or `/data/user/0/<package>/`)
- this directory is private to your app, encrypted by the Linux kernel using file-based encryption (FBE), and strictly protected by your app's UID sandbox
- no other app on the phone can read or write to this directory without root access
- use `context.filesDir` for persistent private files: databases, user settings, authentication tokens
- use `context.cacheDir` for temporary files: downloaded avatars, image thumbnails
- crucial difference: if the phone's storage runs critically low, the Android OS has the right to automatically delete files inside `cacheDir` without asking!
- files inside `filesDir` are NEVER deleted by the OS; they are only removed when the user uninstalls the app or clears app data in Settings
- Realm 2: App-Specific External Storage (`context.getExternalFilesDir(...)`)
- located at `/sdcard/Android/data/<package>/files/`
- it is stored on the shared flash partition, but is still tied to your app's lifecycle: when the app is uninstalled, this folder is automatically deleted
- since Android 4.4, your app requires ZERO permissions to read or write to its own `getExternalFilesDir()` folder!
- use this folder for larger files (video downloads, offline map packs) that would waste space in the smaller internal partition
- Realm 3: Shared Public Storage (Photos, Downloads, Music)
- if you want a file to survive app uninstallation (e.g. a photo taken by the user or an exported PDF invoice), you must save it to shared storage
- you cannot write directly to `/sdcard/`; you must insert a record into the `MediaStore` API or use Storage Access Framework (SAF)
- when saved via `MediaStore`, the file belongs to the user's personal media library and remains on the phone even if your app is deleted
- beginner fallacy: i thought that internal storage was an SD card inside the phone and external storage was a removable microSD card
- on modern phones, 'external storage' is almost always an emulated virtual directory on the exact same built-in UFS flash chip!
- understanding this storage topology ensures you store sensitive data securely while handling large assets responsibly
