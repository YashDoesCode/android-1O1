# Android 9 Pie App Standby Buckets - Restricting Background Resource Use

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// The 4 App Standby Buckets introduced in Android 9 Pie:
// 1. ACTIVE:      App is currently running in foreground -> Zero restrictions!
// 2. WORKING SET: App used recently -> Minor job execution delays
// 3. FREQUENT:    App used regularly (e.g. daily) -> Jobs delayed up to hours, max 10 FCM msgs/day
// 4. RARE:        App rarely used -> Jobs delayed up to 24h, alarms throttled, no network access!
// 5. RESTRICTED:  (Added in Android 11/12) Severe penalties for battery-hogging apps!
```

### Notes, Curious Questions & System Mechanics

- one of the biggest complaints about Android devices historically was poor battery life caused by background apps
- a rogue app installed on your phone could wake up every 30 seconds, ping a server, run GPS location, and drain your battery while the phone was in your pocket
- in Android 6.0 Marshmallow, Google introduced 'Doze Mode', which froze background apps when the phone was sitting stationary on a table
- but what if the phone was moving around in your pocket or backpack? Doze mode wouldn't trigger, and rogue apps still drained battery!
- Android 9 Pie solved this with a game-changing, AI-powered system called 'App Standby Buckets'
- the Android OS uses machine learning algorithms on the device to analyze your personal app usage habits over time
- it dynamically sorts every installed application into one of four distinct priority buckets based on how often you interact with it:
- Bucket 1: Active (App is currently in the foreground, has an active foreground service, or you tapped a notification)
- Active apps have zero restrictions on CPU, jobs, network, or alarms
- Bucket 2: Working Set (App is used frequently but not right now, e.g. a messaging app you open multiple times a day)
- Working set apps have minor restrictions: background jobs can only run every few minutes
- Bucket 3: Frequent (App you use regularly, e.g. a fitness tracking app you open once a day)
- Frequent apps face stricter limits: high-priority push notifications are capped at 10 per day, and background jobs are delayed for hours
- Bucket 4: Rare (App you haven't opened in days, e.g. a hotel booking app or flight app)
- Rare apps are placed in a virtual coma: they cannot access the internet in the background, alarms can only fire once every 2 hours, and jobs can be delayed up to 24 hours!
- if an app is placed in the Rare bucket, running a background `Thread` or legacy `IntentService` will simply be killed by the OS
- this was a massive wake-up call for developers who assumed their background syncing code would run whenever they wanted
- it forced the entire Android industry to abandon custom background threads and adopt Google's official `WorkManager` library
- `WorkManager` is smart: it checks the active standby bucket and schedules tasks to run during OS-approved maintenance windows
- beginner fallacy: i thought my app's background timer would run forever in the background like on a desktop server
- on modern Android, the OS will ruthlessly kill your background tasks to preserve the user's battery life unless you follow WorkManager rules
- App Standby Buckets saved billions of watt-hours of battery power and transformed Android's reputation for battery efficiency
