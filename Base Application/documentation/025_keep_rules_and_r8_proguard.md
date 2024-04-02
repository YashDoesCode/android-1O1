# keepRules/rules.keep - R8 Code Shrinking, Obfuscation, and Reflection Safety

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
# Base Application/app/src/main/keepRules/rules.keep
# Custom R8 / ProGuard rules for code shrinking and obfuscation.
# Prevent R8 from stripping classes accessed via reflection or serialization.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Example: If using reflection or JSON serialization:
# -keep class com.keeper.webguard.models.** { *; }
```

### Notes, Curious Questions & System Mechanics

- in `Base Application/app/src/main/keepRules/`, there is a file called `rules.keep`
- this file is the modern location for ProGuard and R8 compiler keep rules
- when you build a `release` APK, the R8 compiler analyzes all the Kotlin and Java bytecode in your app
- R8 does three critical things: Shrinking (tree shaking), Optimization, and Obfuscation (renaming symbols)
- Shrinking removes all classes, methods, and fields that are never called from code, saving megabytes of APK space
- Obfuscation renames remaining classes from `com.keeper.webguard.MainActivity` to `a.a.a` to make reverse-engineering harder
- however, R8's static analysis has a fatal blind spot: Reflection and JSON serialization!
- if you use a JSON library like Gson, Moshi, or Kotlinx Serialization, field names are parsed at runtime from strings
- if R8 renames the field `userName` to `b`, the JSON parser can no longer match `"userName"` from the API response and crashes!
- that is where keep rules come in: `-keep class com.keeper.webguard.models.** { *; }` tells R8: 'Do not rename or delete anything in this package!'
- look at the rule in `rules.keep`: `-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod`
- this tells R8 to preserve runtime annotations and generic type signatures in the compiled `.dex` files
- without `Signature`, Kotlin reflection and dependency injection libraries (like Hilt or Koin) would fail to resolve generic types
- without `*Annotation*`, Jetpack Compose runtime annotations like `@Composable` and `@Stable` could be stripped out, breaking UI recomposition
- modern Android libraries actually ship with their own internal consumer ProGuard rules packaged inside their AAR archives
- this means Jetpack Compose, Retrofit, and Core KTX automatically tell R8 what rules they need without you typing them manually
- you only need to write custom rules in `rules.keep` for your own custom reflection, JNI native C++ bindings, or data models
- beginner fallacy: i thought R8 was only an optional extra step that wasn't important for real apps
- without R8, real-world production Android apps can exceed 100 MB in size and hit Dalvik multidex method limits
- R8 is an indispensable part of the Android build pipeline that produces small, fast, secure production binaries
- having `rules.keep` in `Base Application` ensures our release builds remain lean and crash-free
