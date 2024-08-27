# Android 13 T - POST_NOTIFICATIONS Permission and the System Photo Picker

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
<!-- Declaring Notification Permission in AndroidManifest.xml for Android 13+ -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

// Modern Photo Picker (Zero permissions needed!):
val pickMedia = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia()
) { uri ->
    // User selected photo URI, app never gets storage permission!
}
pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
```

### Notes, Curious Questions & System Mechanics

- Android 13 (API level 33), released in August 2022, tackled two of the most annoying user experience problems on smartphones
- problem 1: Notification Spam from newly installed apps
- for 14 years, any Android app you installed had the automatic right to spam your notification bar with marketing alerts
- users had to manually go into Settings -> Notifications and disable alerts for every annoying app
- Android 13 introduced the `android.permission.POST_NOTIFICATIONS` runtime permission
- now, when an app wants to send a notification, the OS displays an explicit permission popup: 'Allow Keeper WebGuard to send notifications?'
- if the user taps 'Don't allow', all calls to `NotificationManager.notify()` are silently suppressed by the system
- this put users firmly in control of their attention and forced developers to earn the right to send notifications
- problem 2: Excessive storage permissions just to select a profile picture
- in the past, if an app wanted you to upload a profile photo, it asked for full storage access (`READ_EXTERNAL_STORAGE`)
- this was insane: why should an app get access to ALL your 5,000 photos just so you can upload ONE picture?
- Android 13 introduced the official 'Android Photo Picker' (`ActivityResultContracts.PickVisualMedia()`)
- the Photo Picker runs as an out-of-process system dialog managed by `com.google.android.providers.media.module`
- the user browses their photos inside the secure system UI; your app CANNOT see what photos exist in the gallery!
- when the user taps one photo, Android sends back ONLY that single photo's `Uri` to your app with temporary read access
- best of all: your app requires ZERO storage permissions in `AndroidManifest.xml` to use the Photo Picker!
- Android 13 also introduced 'Per-App Language Preferences'
- a multilingual user can set their phone OS to English, but set `Base Application` to Spanish or Hindi in Settings -> Languages
- it also introduced granular media permissions: splitting `READ_EXTERNAL_STORAGE` into `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`, and `READ_MEDIA_AUDIO`
- beginner fallacy: i thought asking for notification permissions in `onCreate()` was a good idea
- bombarding the user with a notification permission popup the moment the app opens leads to an 80% denial rate; request it contextually when the user opts in!
- Android 13 made mobile apps much more respectful of user attention, privacy, and media boundaries
