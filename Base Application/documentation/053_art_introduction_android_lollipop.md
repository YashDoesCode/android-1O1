# Android Runtime (ART) in Lollipop 5.0 - Ahead-Of-Time (AOT) Compilation

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// Dalvik (Android 1.0 - 4.4):
// Runs Bytecode -> JIT interprets & compiles hot spots at runtime -> Slow, burns CPU & battery!

// ART in Android 5.0 Lollipop (Pure AOT):
// User installs APK -> dex2oat compiles ENTIRE app to native machine code ELF binary (.oat)!
// App runs at 100% native hardware speed -> Zero JIT overhead!
// Catch: Installation took 5 minutes, and storage usage doubled!
```

### Notes, Curious Questions & System Mechanics

- by 2013 (Android 4.4 KitKat), Dalvik was reaching its theoretical limits: mobile apps were becoming larger and UI stutter was common
- in Android 5.0 Lollipop (API 21), Google introduced a radical, ground-up replacement for Dalvik: ART (Android Runtime)
- ART replaced Dalvik's Just-In-Time (JIT) compilation model with a revolutionary Ahead-Of-Time (AOT) compilation architecture
- under Dalvik's JIT, every time an app ran, the virtual machine had to interpret bytecode and dynamically compile small chunks on the fly
- this JIT compilation consumed CPU cycles, drained battery, and caused micro-stutter (dropped frames) during animations
- ART's initial philosophy in Lollipop was bold: 'Why compile at runtime when we can compile EVERYTHING during installation?'
- when a user downloaded an APK on Android 5.0, a system tool called `dex2oat` intercepted the installation
- `dex2oat` took the `classes.dex` file and compiled 100% of the bytecode directly into native ARM/x86 machine code!
- the compiled output was saved to disk as an ELF shared library binary with the `.oat` file extension
- when the user opened the app, the CPU executed pure native machine code directly at full hardware silicon speed!
- app launch times were cut in half, animations ran at a buttery-smooth 60 FPS, and battery life improved dramatically
- ART also completely overhauled Garbage Collection (GC): it reduced GC pauses from two pauses (up to 75ms) down to a single sub-3ms pause!
- BUT Lollipop's pure AOT compilation had two massive unintended real-world consequences:
- consequence 1: App installation times became unbearable! Installing a large app like Facebook or Google Maps took 3 to 5 minutes of 100% CPU burn
- every time Android received an OS update, the phone spent 45 minutes on the dreaded 'Android is upgrading... Optimizing app 1 of 120' screen!
- consequence 2: Storage bloat! The compiled `.oat` native binary took 2x to 3x more physical flash storage than the original DEX file
- users on budget phones with 8 GB or 16 GB of storage ran out of space simply from installing 10 apps
- beginner fallacy: i thought ART was just a minor update to Dalvik's branding
- ART was a completely brand-new, modern C++ runtime engine that changed how Android code executes forever
- Google realized that while pure AOT was blazingly fast, they needed a smarter hybrid approach to balance install times with performance
- that realization set the stage for the next major revolution in Android compilation in Android 7 Nougat
