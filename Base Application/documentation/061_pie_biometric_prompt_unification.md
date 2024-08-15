# Android 9 Pie BiometricPrompt - Unifying Fingerprint, Face, and Iris

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// How BiometricPrompt is invoked in modern Android:
val biometricPrompt = BiometricPrompt(
    activity,
    executor,
    object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            // Cryptographically verified user identity!
        }
    }
)
val promptInfo = BiometricPrompt.PromptInfo.Builder()
    .setTitle("Unlock Keeper WebGuard")
    .setNegativeButtonText("Cancel")
    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    .build()
biometricPrompt.authenticate(promptInfo)
```

### Notes, Curious Questions & System Mechanics

- prior to Android 9 Pie, if you wanted to implement biometric authentication in an app, you had to use `FingerprintManager`
- `FingerprintManager` was deeply flawed: it only worked for physical fingerprint sensors and required developers to draw their own custom UI dialog
- every app drew a completely different looking fingerprint popup: some had blue icons, some had custom buttons, some leaked PIN passwords
- even worse, when phone manufacturers introduced 3D facial recognition (like Face Unlock) or Iris scanners, `FingerprintManager` had no idea how to talk to them
- manufacturers wrote proprietary custom SDKs (Samsung Pass, Huawei Biometrics), fragmenting biometric code across Android
- Android 9 Pie permanently deprecated `FingerprintManager` and introduced `BiometricPrompt`
- `BiometricPrompt` established a single, unified operating system contract for ALL biometric modalities: Fingerprint, Face, and Iris
- the most important architectural change: the biometric dialog UI is NO LONGER rendered by your application!
- the dialog is drawn directly by the Android System UI process (`com.android.systemui`) in a secure, isolated window overlay
- this provides immense security: third-party apps cannot tamper with the dialog, fake fingerprint success prompts, or capture biometric images
- the underlying biometric template (your actual fingerprint ridge data or face map) never leaves the secure hardware TEE (Trusted Execution Environment)
- the system simply hands your app a cryptographic proof (`CryptoObject`) or a boolean success callback
- Pie also introduced Biometric Classes: Class 3 (Strong - Hardware TEE backed), Class 2 (Weak), and Class 1 (Convenience)
- for high-security banking or authentication apps, you specify `BIOMETRIC_STRONG`, ensuring spoof-prone 2D selfie camera face unlocks cannot bypass the gate
- Google provided `androidx.biometric:biometric` to backport this modern UI and security contract all the way back to Android 6.0 Marshmallow
- beginner fallacy: i thought `BiometricPrompt` returned a picture of the user's fingerprint to my Kotlin code so i could store it in a database
- apps NEVER have access to biometric data; doing so would be a catastrophic privacy disaster if the phone were compromised
- your app only receives an authentication confirmation backed by Android Keystore cryptographic signatures
- this design is why you can trust fingerprint authentication across banking, password managers, and enterprise security apps
- it is another landmark innovation introduced in Android 9 Pie that defined modern mobile security standards
