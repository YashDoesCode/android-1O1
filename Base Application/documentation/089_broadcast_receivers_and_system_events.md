# Broadcast Receivers - Listening to Operating System Lifecycle Events

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Dynamic Broadcast Receiver registered in Kotlin:
val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                val isAirplaneMode = intent.getBooleanExtra("state", false)
                // Update network security status in Keeper WebGuard!
            }
        }
    }
}
// Android 14 mandatory export flag:
context.registerReceiver(
    receiver,
    IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
    Context.RECEIVER_NOT_EXPORTED
)
```

### Notes, Curious Questions & System Mechanics

- the `BroadcastReceiver` is one of the four foundational core components of Android (Activity, Service, Receiver, Provider)
- it allows an application to listen for and respond to system-wide broadcast announcements or events emitted by the OS or other apps
- examples of system broadcasts include: `ACTION_BOOT_COMPLETED` (phone finished booting up), `ACTION_BATTERY_LOW`, `ACTION_POWER_CONNECTED`, `ACTION_AIRPLANE_MODE_CHANGED`
- a Broadcast Receiver does NOT have a user interface; it acts as a passive event listener
- when an event fires, the OS instantiates the receiver and calls its `onReceive(context: Context, intent: Intent)` method
- there are two ways to register a Broadcast Receiver: Static Registration and Dynamic Registration
- Way 1: Static Registration (declared in `AndroidManifest.xml`)
- in the past, apps declared receivers in XML for events like `CONNECTIVITY_CHANGE`; when the network dropped, Android would wake up 100 apps simultaneously!
- this caused the 'Thundering Herd' problem: 100 apps waking up at the same split second caused massive CPU spikes and battery drain
- in Android 8.0 Oreo, Google BANNED static XML registration for almost all implicit broadcasts!
- today, you can only register statically for a tiny handful of critical events, such as `ACTION_BOOT_COMPLETED` to reschedule alarms after reboot
- Way 2: Dynamic Registration (registered in Kotlin code at runtime)
- you register the receiver in code via `context.registerReceiver(...)` and unregister it in `onDestroy()` via `context.unregisterReceiver(receiver)`
- dynamically registered receivers only listen for events while your app is alive and running
- as studied in doc 068, Android 14 made specifying `Context.RECEIVER_EXPORTED` or `RECEIVER_NOT_EXPORTED` mandatory for dynamic receivers
- `RECEIVER_NOT_EXPORTED` ensures only the Android OS and your own app can send events to the receiver, blocking malicious spoofed broadcasts
- `onReceive()` runs directly on the Main UI Thread, meaning you must NEVER perform slow network or disk operations inside it
- `onReceive()` has a strict 10-second execution timeout; if you need to do heavy work, pass it to `WorkManager` using `goAsync()`
- beginner fallacy: i thought Broadcast Receivers were deprecated because static receivers were limited in Oreo
- dynamic broadcast receivers remain the primary way modern apps monitor hardware connectivity, headset plug events, and battery status
- they are the essential reactive ear through which an application listens to the heartbeat of the physical hardware device
