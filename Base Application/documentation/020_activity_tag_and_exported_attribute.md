# The <activity> Tag & android:exported - The Android 12 Security Revolution

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
<!-- Inside AndroidManifest.xml -->
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:theme="@style/Theme.KeeperWebGuard20"
    android:windowSoftInputMode="adjustResize">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

### Notes, Curious Questions & System Mechanics

- the `<activity>` tag registers a specific screen entry point with the Android system's `ActivityManagerService`
- `android:name=".MainActivity"` uses shorthand dot-notation to reference `com.keeper.webguard.MainActivity`
- the dot `.MainActivity` tells the manifest merger to prepend the package `namespace` defined in `build.gradle.kts`
- look at the attribute `android:exported="true"`: this single line represents one of the biggest security overhauls in Android history
- before Android 12 (API 31), if an activity had an `<intent-filter>`, Android would automatically set `exported = true` by default
- this caused massive security vulnerabilities across thousands of apps: internal admin or secret activities became accessible to malicious apps!
- a malicious app installed on the same phone could construct an explicit Intent and launch unexported screens or bypass login flows
- starting in Android 12, Google made `android:exported` MANDATORY for every component that declares an intent filter
- if you forget to declare `android:exported="true"` or `android:exported="false"`, your app will fail to build with a fatal AGP error
- why is `MainActivity` set to `exported="true"` here? Because the phone's home screen launcher is a separate process!
- the launcher app (e.g. Pixel Launcher or Nova Launcher) must be able to send an Intent to start your activity when the user taps your icon
- if you set `android:exported="false"` on `MainActivity`, the home screen launcher would get a `SecurityException` and the app could never open!
- for all other internal activities in your app (e.g. `PaymentActivity`), you should explicitly set `android:exported="false"`
- `exported="false"` guarantees that only components from your own app (sharing the same Linux UID) can launch that activity
- `android:windowSoftInputMode="adjustResize"` tells the window manager to resize the screen when the on-screen keyboard appears
- this ensures text inputs in Jetpack Compose aren't covered up by the keyboard, keeping input fields visible to the user
- the activity theme can override the application theme, allowing specific activities to have custom splash screens or transparent backgrounds
- beginner fallacy: i thought `exported="true"` meant the code was open-source or publicly readable
- `exported` has nothing to do with code visibility; it is strictly an IPC (Inter-Process Communication) firewall rule
- this single attribute demonstrates how the Android security architecture isolates processes while allowing controlled cross-app communication
- it is a prime example of Android evolving to prevent real-world attack vectors on billions of active devices
