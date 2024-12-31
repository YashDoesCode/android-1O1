# Android OS Integration & Lifecycle Compatibility

## 1. Operating System Compatibility Matrix

Keeper LoLock is engineered to run seamlessly across Android 9 through Android 15 (API Levels 28 to 35):

| Android Version | API Level | Key Framework Integration |
| :--- | :--- | :--- |
| **Android 9 (Pie)** | 28 | Legacy storage compatibility (`READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`) |
| **Android 10 (Q)** | 29 | Scoped Storage initialization, `requestLegacyExternalStorage="true"` fallback |
| **Android 11 (R)** | 30 | `MANAGE_EXTERNAL_STORAGE` permission support for all-files access |
| **Android 12 (S / S_V2)**| 31–32 | Modern notification channels, strict PendingIntent immutability flags |
| **Android 13 (Tiramisu)**| 33 | Granular media permissions (`READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`), `POST_NOTIFICATIONS` runtime consent |
| **Android 14 (UpsideDownCake)** | 34 | Foreground service type constraints (`FOREGROUND_SERVICE_DATA_SYNC`) |
| **Android 15 (VanillaIceCream)**| 35 | Strict 16KB page alignment compatibility, modern predictive back support |

---

## 2. Storage Access Framework (SAF) & Scoped Storage

Rather than relying on brittle direct filesystem paths that fail on modern Scoped Storage environments, Keeper LoLock embraces the Android Storage Access Framework (SAF):
- `ActivityResultContracts.OpenDocument`: Grants persistent, secure URI access to any user-selected file regardless of where it resides (internal storage, SD card, cloud providers).
- `ActivityResultContracts.OpenDocumentTree`: Grants granular directory tree access, allowing `DirectoryCryptoEngine` to traverse and encrypt entire folders recursively via `DocumentFile`.

---

## 3. Native Intent Interception & Overlay Flow

When an encrypted container (`.lolock`) is tapped inside any file manager (e.g., Files by Google, Samsung My Files, Solid Explorer):
1. The Android package manager resolves the intent filters configured in `AndroidManifest.xml`:
   ```xml
   <intent-filter>
       <action android:name="android.intent.action.VIEW" />
       <category android:name="android.intent.category.DEFAULT" />
       <category android:name="android.intent.category.BROWSABLE" />
       <data android:scheme="file" />
       <data android:scheme="content" />
       <data android:mimeType="*/*" />
       <data android:pathPattern=".*\\.lolock" />
   </intent-filter>
   ```
2. Android launches `UnlockActivity` directly on top of the active file manager as a translucent, floating native dialog (`Theme.KeeperLoLock.Overlay`).
3. Upon entering the correct password, `UnlockActivity` streams the decrypted data to a secure private sandbox cache (`getCacheDir()/decrypted/`).
4. `FileProvider` converts the decrypted file into a content URI:
   ```java
   Uri contentUri = FileProvider.getUriForFile(
       this,
       "com.keeper.lolock.fileprovider",
       decryptedFile
   );
   ```
5. Android's native `Intent.ACTION_VIEW` is invoked with `FLAG_GRANT_READ_URI_PERMISSION`, opening the file in the user's preferred viewer without any external file leakage.

---

## 4. Background Resilience & Low-Memory Optimization

1. **Foreground Service (`FileCryptoService`)**:
   During long directory or batch encryption runs, `FileCryptoService` is started with `FOREGROUND_SERVICE_DATA_SYNC`. This prevents Android's Low Memory Killer (LMK) or Doze Mode from terminating the encryption pipeline mid-stream.
2. **Periodic Security Notification Cycle (`PasswordExpiryWorker`)**:
   Registered with Android `WorkManager`, the worker runs every 24 hours in the background. It calculates elapsed time since the master password was set:
   - If $< 180$ days: Silent no-op.
   - If $\ge 180$ days: Triggers a non-intrusive reminder notification suggesting routine rotation.
   - Per security guidelines, **no notifications** are sent on individual file unlocks.
