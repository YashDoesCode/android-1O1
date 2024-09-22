# Permissions Model Evolution - From Install-Time to Runtime Sandboxes

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// How permissions evolved across Android generations:
// Android 1.0 - 5.1: Install-Time Permission Wall (All or nothing! Users clicked Accept blindly)
// Android 6.0 Marshmallow: Runtime Permissions (Request Camera/Location dynamically in code)
// Android 10 Q: Tri-state Location (Allow only while using the app)
// Android 11 R: One-Time Permissions (Only this time) & Permissions Auto-Reset
// Android 13 T: POST_NOTIFICATIONS & Granular Media (Photos, Video, Audio)
// Android 14 U: Partial Photo Access (User selects specific individual pictures)
```

### Notes, Curious Questions & System Mechanics

- the evolution of the Android permissions model is the story of mobile software learning to respect human privacy
- era 1 (Android 1.0 to 5.1): The All-or-Nothing Install-Time Wall
- when a user downloaded an app from Google Play, they were shown a scary list of 20 permissions (Camera, Contacts, SMS, Location)
- the user had only two choices: tap 'Accept and Download' and surrender all private data, or don't install the app at all!
- once installed, the app had permanent, unrestricted access to those sensors 24/7 in the background without ever asking again
- era 2 (Android 6.0 Marshmallow): The Runtime Permissions Revolution
- Google introduced dynamic runtime permissions for 'Dangerous Permissions' (Location, Camera, Microphone, Contacts, Calendar, Phone, SMS, Storage)
- apps could now be installed instantly with zero permission prompts!
- permissions are requested contextually inside the Kotlin code only when the user taps a relevant feature (e.g. tapping the camera button to take a photo)
- if the user denies permission, the app must not crash; it must degrade gracefully and explain why the feature cannot function
- era 3 (Android 10): Granular Background Location
- location permissions were split into 'Allow only while using the app' and 'Allow all the time'
- users overwhelmingly chose 'While using the app', severely curtailing secret background location tracking by ad networks
- era 4 (Android 11 & 12): One-Time and Approximate Permissions
- introduced 'Only this time' ephemeral permissions and approximate 3km fuzzy location circles
- era 5 (Android 13 & 14): Notification Consent and Partial Photo Access
- notifications became an opt-in permission (`POST_NOTIFICATIONS`), and users gained the ability to share individual selected photos rather than whole albums
- in `Base Application`, notice our manifest requests zero dangerous permissions because we only display a basic UI scaffold
- if you ever add a feature that requires a dangerous permission, you must both declare it in `AndroidManifest.xml` AND request it at runtime via `rememberLauncherForActivityResult`
- beginner fallacy: i thought declaring a permission in `AndroidManifest.xml` was enough to use the camera
- if you call the camera on Android 6+ without checking runtime permissions, your app will crash with an immediate `SecurityException`!
- this multi-generational evolution represents one of the greatest victories for consumer privacy in technology history
