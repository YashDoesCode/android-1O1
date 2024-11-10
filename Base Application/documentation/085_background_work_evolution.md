# Background Processing Evolution - From Services to JobScheduler to WorkManager

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// The 15-Year Evolution of Android Background Processing:
// 1. Android 1.0 - 7.1: Unlimited background Services & IntentService (Killed user battery!)
// 2. Android 5.0 Lollipop: JobScheduler introduced (Batched jobs on Wi-Fi/Charging)
// 3. Android 8.0 Oreo: Background Execution Limits (Background services BANNED when app closed!)
// 4. Android 9.0 Pie: App Standby Buckets & severe alarm throttling
// 5. Modern Jetpack: WorkManager (The official universal standard for persistent background work)
```

### Notes, Curious Questions & System Mechanics

- the history of background execution on Android is a continuous tug-of-war between developer convenience and user battery life
- in the early days (Android 1.0 to 7.1), developers could start a background `Service` using `context.startService(...)`
- that background service could run indefinitely 24 hours a day, 7 days a week, even when the user had swiped the app away!
- every app on the phone (social media, weather, shopping) ran its own secret background service, constantly keeping the CPU awake
- users complained that their phone battery drained from 100% to 0% in 4 hours while sitting untouched on a desk
- Google realized they had to step in and aggressively limit background execution to save the Android ecosystem
- milestone 1: Android 5.0 Lollipop introduced `JobScheduler`
- `JobScheduler` allowed apps to declare criteria: 'Run this sync ONLY when the phone is plugged into a charger AND connected to unmetered Wi-Fi'
- the OS intelligently batched jobs from multiple apps together, waking the CPU once instead of 50 times an hour
- milestone 2: Android 8.0 Oreo dropped the hammer with 'Background Execution Limits'
- Oreo made it completely ILLEGAL for an app to start a background service when the app is not in the foreground!
- if an app tried to call `startService()` from the background, Android threw a fatal `IllegalStateException` crash
- developers were confused and fragmented: should they use `JobScheduler`, `FirebaseJobDispatcher`, `AlarmManager`, or `BroadcastReceivers`?
- milestone 3: Google created 'WorkManager' as part of Android Jetpack to unify all background processing under a single API
- `WorkManager` is backward-compatible all the way to API 14: on Android 8+ it delegates to `JobScheduler`, and on older devices it falls back gracefully to `AlarmManager`
- WorkManager guarantees execution: even if your app process is killed or the phone is rebooted, WorkManager persists the job in an internal SQLite database and resumes it!
- today, modern Android recognizes only three valid categories of work: Immediate (Coroutines), Long-running/User-facing (Foreground Services), and Deferrable (WorkManager)
- beginner fallacy: i thought i could just launch a Kotlin coroutine in `GlobalScope` to do background syncing when the app closes
- when the user leaves the app, Android will kill your process, and any running `GlobalScope` coroutine is instantly terminated mid-execution!
- for persistent background tasks that MUST finish (like uploading logs or syncing offline drafts), `WorkManager` is the only safe solution
- understanding this evolution explains why background work is so strictly governed by modern Android operating systems
