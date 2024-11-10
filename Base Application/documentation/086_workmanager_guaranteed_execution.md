# WorkManager - Guaranteed Background Processing with System Constraints

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Creating a background worker with WorkManager:
class ThreatSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val success = syncThreatDatabaseWithServer()
        return if (success) Result.success() else Result.retry()
    }
}

// Enqueueing with Constraints:
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.UNMETERED) // Wi-Fi only!
    .setRequiresCharging(true)                     // Only while plugged in!
    .setRequiresBatteryNotLow(true)
    .build()

val syncRequest = OneTimeWorkRequestBuilder<ThreatSyncWorker>()
    .setConstraints(constraints)
    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
    .build()

WorkManager.getInstance(context).enqueue(syncRequest)
```

### Notes, Curious Questions & System Mechanics

- `WorkManager` is the recommended, official Google solution for persistent, deferrable background work in Android
- 'Persistent' means that if the user closes the app, or if the phone runs out of battery and restarts, the work is still guaranteed to execute!
- 'Deferrable' means the task does not have to execute at this exact second; it can wait until optimal system conditions are met
- to create a task, you extend `CoroutineWorker` and implement the `suspend fun doWork(): Result` method
- inside `doWork()`, you write your asynchronous background logic (e.g. uploading analytics, syncing databases, compressing logs)
- you return a `Result`: `Result.success()`, `Result.failure()`, or `Result.retry()`
- if network is flaky or your server returns a 503 error, returning `Result.retry()` automatically triggers exponential backoff scheduling!
- WorkManager will retry the task after 15 minutes, then 30 minutes, then 1 hour, preventing your app from hammering the server
- look at the `Constraints` builder: this is where WorkManager shines for battery conservation
- you can specify: `.setRequiredNetworkType(NetworkType.UNMETERED)` - the task will wait until the user connects to Wi-Fi so mobile data isn't consumed!
- you can add `.setRequiresCharging(true)` and `.setRequiresBatteryNotLow(true)` to ensure battery is never drained during intensive work
- WorkManager persists all worker definitions, constraints, and progress inside an internal Room SQLite database in your app's private sandbox
- the system's `JobScheduler` is configured to trigger your worker when the OS detects that all specified constraints are met
- you can chain workers together in complex directed acyclic graphs (DAGs): `workManager.beginWith(compressImages).then(uploadImages).enqueue()`
- you can also observe work progress and state reactively in Jetpack Compose: `workManager.getWorkInfoByIdLiveData(...).asFlow()`
- WorkManager also supports 'Periodic Work' (`PeriodicWorkRequestBuilder`) for recurring tasks scheduled every 24 hours (minimum interval is 15 minutes)
- it supports 'Expedited Work' with out-of-quota notifications for high-priority tasks that need to start within seconds
- beginner fallacy: i thought WorkManager was an exact clock timer that would execute code at 3:00:00 PM precisely
- WorkManager is NOT an exact alarm clock; to fire an exact alarm at 3:00 PM for an alarm clock app, you must use `AlarmManager.setExactAndAllowWhileIdle()`
- WorkManager is designed for batchable, battery-friendly work, and it is an indispensable component of modern Android architecture
- it is the ultimate synthesis of battery respect, fault tolerance, and developer ergonomics on mobile platforms
