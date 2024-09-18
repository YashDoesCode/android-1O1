# SELinux in Android - Mandatory Access Control Against Privilege Escalation

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// How SELinux Enforcing Mode stops Root Exploits:
// Even if an attacker finds a zero-day C++ memory bug in a system daemon and gains UID 0 (root),
// SELinux policy checks the process context vs target context:
// scontext = u:r:untrusted_app:s0
// tcontext = u:object_r:system_file:s0
// policy: allow untrusted_app system_file:file { read };
// policy: DENY untrusted_app system_file:file { write };
// Result: Kernel blocks write! Root exploit thwarted!
```

### Notes, Curious Questions & System Mechanics

- while standard Linux UIDs (Discretionary Access Control - DAC) provide strong isolation, they have one fatal weakness: the `root` user (UID 0)
- in traditional Linux, if a process gains root privileges (UID 0), it has god-like powers: it can bypass all permissions, read any file, and hijack hardware
- in early Android versions (1.0 to 4.2), rooting phones and rooting exploits were rampant: a single buffer overflow in a native daemon gave hackers complete root control
- in Android 4.3 and 5.0, Google integrated Security-Enhanced Linux (SELinux) into the core Android operating system
- SELinux provides 'Mandatory Access Control' (MAC) developed originally by the United States National Security Agency (NSA)
- under SELinux, the concept of an all-powerful root user is completely abolished!
- every single process, file, socket, and device node in the operating system is assigned a cryptographic security label (Context)
- for example, our `Base Application` runs with the SELinux domain: `u:r:untrusted_app:s0`
- system files are labeled: `u:object_r:system_file:s0`
- the Linux kernel contains a hardcoded, immutable SELinux policy matrix that defines which domains can talk to which labels
- even if a hacker compromises our app and somehow escalates to UID 0 (root), the SELinux kernel policy still classifies the process as `untrusted_app`!
- if `untrusted_app` tries to execute a shell script in `/system/bin` or modify the kernel, SELinux immediately blocks the syscall with an `avc: denied` audit log!
- Android runs SELinux in `Enforcing` mode on 100% of production consumer devices
- Google also implemented 'SELinux Per-App Domains' in Android 9 Pie: separating untrusted apps into isolated sub-categories
- SELinux makes it virtually impossible for an exploit in one component (like the media codec) to pivot and attack another component (like the camera or payments)
- it is the reason modern Android devices are extraordinarily difficult to root or jailbreak compared to devices from 2012
- beginner fallacy: i thought antivirus apps were necessary on Android to scan files like on Windows
- Android's security does not rely on third-party antivirus scanners; it relies on kernel-level sandboxing, SELinux mandatory access control, and verified boot
- studying SELinux showed me how modern operating systems achieve military-grade defense in depth against state-level threat actors
- it is one of the most sophisticated security architectures in modern computer science
