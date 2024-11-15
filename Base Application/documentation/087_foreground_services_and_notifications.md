# Foreground Services - Persistent Tasks, Notifications, and Android 14 Types

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
<!-- Declaring a Foreground Service in AndroidManifest.xml -->
<service
    android:name=".services.WebGuardProtectionService"
    android:foregroundServiceType="dataSync|connectedDevice"
    android:exported="false" />

<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
```

### Notes, Curious Questions & System Mechanics

- what if your app needs to perform a long-running task that MUST NOT be deferred or killed when the user leaves the screen?
- examples include: playing music in Spotify, tracking a GPS run in Strava, recording audio, or running active network protection in Keeper WebGuard
- since background services are banned, Android provides the 'Foreground Service' mechanism
- a Foreground Service is an Android Service that runs with high operating system priority (OOM score around 50)
- because the OS treats it as nearly equivalent to a foreground activity, the Low Memory Killer will almost never terminate it
- BUT there is a non-negotiable contract with the user: a Foreground Service MUST display an active, persistent notification in the notification bar!
- the user MUST be constantly aware that the app is actively doing work and consuming battery in the background
- inside the service's `onStartCommand()`, you must call `startForeground(NOTIFICATION_ID, notification)` within 5 seconds of creation
- if you fail to call `startForeground()` within 5 seconds, the Android OS kills the process immediately with an `ANR` or `ForegroundServiceDidNotStartInTimeException`!
- the persistent notification cannot be swiped away by the user as long as the service is running, ensuring total transparency
- in Android 14 (API level 34), Google drastically tightened the rules around foreground services by introducing 'Foreground Service Types'
- every foreground service MUST explicitly declare its exact purpose via `android:foregroundServiceType` in `AndroidManifest.xml`!
- supported types include: `camera`, `connectedDevice`, `dataSync`, `location`, `mediaPlayback`, `mediaProjection`, `microphone`, `phoneCall`
- for each type, you must also request a specific permission: `<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />`
- Google Play policy strictly audits these types: if you declare `mediaPlayback` for an app that doesn't play music, your app will be rejected from the store!
- furthermore, Android 14 restricts starting foreground services from the background unless the app was triggered by an approved event (like a high-priority FCM push)
- when the task finishes, the service must call `stopForeground(STOP_FOREGROUND_REMOVE)` and `stopSelf()` to dismiss the notification
- beginner fallacy: i thought i could run a foreground service with an invisible notification
- Android physically forbids hidden notifications; the system notification manager enforces visibility so rogue apps cannot spy on users
- Foreground Services provide the perfect balance: granting apps power for legitimate user-facing background work while preventing sneaky battery abuse
- mastering foreground services and Android 14 types is essential for building media, fitness, navigation, and security apps
