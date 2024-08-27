# Android 11 R - One-Time Permissions, Background Location, and Wireless ADB

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Android 11 Permission Dialog options:
// [ While using the app ]
// [ Only this time ]      <-- Brand new One-Time Permission!
// [ Don't allow ]

// Auto-reset permissions:
// If a user doesn't open an app for 3 months, Android 11 automatically
// revokes ALL granted runtime permissions in the background!
```

### Notes, Curious Questions & System Mechanics

- Android 11 (API level 30), released in late 2020, doubled down on user privacy and fine-grained hardware control
- privacy upgrade 1: 'One-Time Permissions' (`Only this time`)
- whenever an app requests Location, Microphone, or Camera, the system permission dialog displays a new option: 'Only this time'
- if the user taps 'Only this time', Android grants temporary permission that expires the moment the user closes the app or locks their screen
- the next time the app opens, it must request permission again; this prevents apps from retaining indefinite camera and location access
- privacy upgrade 2: 'Permissions Auto-Reset'
- if a user installs an app, grants it Location permissions, and then stops using the app for a few months, Android 11 intervenes
- the OS automatically revokes all previously granted runtime permissions in the background and notifies the user via notification
- this stops dormant apps installed on your phone from continuing to track your activity in the background
- privacy upgrade 3: Background Location lockdown
- apps can no longer request foreground location (`ACCESS_FINE_LOCATION`) and background location (`ACCESS_BACKGROUND_LOCATION`) at the same time
- an app must first request foreground location, explain to the user why background location is needed, and direct them to manual system settings
- developer experience upgrade: Native Wireless ADB Debugging!
- before Android 11, debugging required either a physical USB cable or running arcane terminal commands over adb tcpip
- Android 11 introduced native Wireless Debugging with pairing codes and QR codes directly in Developer Options
- you can pair Android Studio to your physical phone over Wi-Fi in 5 seconds and deploy APKs with zero cables!
- Android 11 also introduced 'Chat Bubbles' API, allowing messaging apps to float floating heads on screen like Facebook Messenger
- it introduced dedicated media controls in the quick settings shade and power menu device controls for smart home devices
- beginner fallacy: i thought that once a user clicked 'Allow', my app could rely on that permission forever
- permissions in modern Android are completely dynamic and ephemeral; your code must check `ContextCompat.checkSelfPermission()` before EVERY sensitive API call
- Android 11 cemented the principle that hardware sensors belong strictly to the user, not the application
- it pushed mobile app architecture toward graceful degradation when permissions are denied or temporarily granted
