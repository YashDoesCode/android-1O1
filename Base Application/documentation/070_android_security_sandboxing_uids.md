# App Sandboxing - How Linux UIDs Isolate Every App on the Phone

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// How Linux User IDs (UIDs) enforce the Android Application Sandbox:
// Package: com.keeper.webguard -> Linux UID: 10185 -> /data/data/com.keeper.webguard (rwx------)
// Package: com.attacker.app    -> Linux UID: 10186 -> /data/data/com.attacker.app    (rwx------)

// If com.attacker.app tries to read /data/data/com.keeper.webguard/databases/secrets.db:
// Linux Kernel File Permissions check: UID 10186 != UID 10185
// Immediate Kernel Error: EACCES (Permission denied)!
```

### Notes, Curious Questions & System Mechanics

- at the core of Android's security model lies a brilliantly simple idea: repurposing multi-user Linux permissions for mobile app sandboxing
- on a traditional multi-user Linux server, you might have user `alice` and user `bob`; `alice` cannot read `bob`'s private home folder
- in 2008, Google's security engineers asked: 'Why treat a phone as having one user with many apps? Let's treat every APP as a separate user!'
- when `Base Application` (`com.keeper.webguard`) is installed, the Android OS assigns it a unique Linux User ID (e.g. `u0_a185` / UID 10185)
- when another app (like Spotify or a malicious app) is installed, it gets assigned a different UID (e.g. `u0_a186` / UID 10186)
- the OS creates a private directory for our app on the internal flash storage: `/data/data/com.keeper.webguard/`
- the Linux kernel sets filesystem permissions on this directory to `0700` (`rwx------`), owned strictly by UID 10185
- if an attacker app running under UID 10186 attempts to open or read our app's SQLite database or SharedPreferences files,
- the Linux kernel itself halts the operation and throws `Permission Denied` before the code can even touch the disk!
- this sandbox protection does NOT depend on Java or Kotlin security checks; it is enforced by the unyielding hardware memory MMU and Linux kernel
- furthermore, each app process runs in its own separate memory address space
- app A cannot read the RAM or inspect the variables of app B because virtual memory page tables isolate process memory spaces completely
- what if two apps WANT to share data or run in the same process?
- they can declare a shared `android:sharedUserId` in their manifests, BUT only if both apps are signed with the exact same cryptographic developer key
- this allows a suite of apps from the same company (e.g. Google Maps and Google Earth) to securely share data without going through public APIs
- however, `sharedUserId` is now deprecated because it breaks modern containerization and security boundaries
- beginner fallacy: i thought that mobile apps were like programs on Windows that can freely browse `C:\Program Files`
- on Android, every application lives in an impenetrable, isolated prison cell guarded by the Linux kernel itself
- this multi-user architecture is the reason Android can safely run untrusted third-party apps from the internet without catastrophic system corruption
- it is the bedrock foundation upon which the entire Android security edifice is built
