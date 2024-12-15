# APK Signing Schemes - v1 (JAR), v2 (APK Hash), v3 (Rotation), and v4 (fs-verity)

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// The 4 Generations of Android APK Signing Schemes:
// Scheme v1 (JAR Signing): Verifies individual files in META-INF/ -> Slow, vulnerable to ZIP tampering!
// Scheme v2 (APK Signing Block): Cryptographic SHA-256 Merkle tree over the ENTIRE binary -> Fast!
// Scheme v3 (Key Rotation): Includes signing lineage proof -> Allows developers to change signing keys!
// Scheme v4 (Streaming / fs-verity): Linux kernel page-level hashing -> Play games WHILE downloading!
```

### Notes, Curious Questions & System Mechanics

- the Android operating system will refuse to install any APK that is not cryptographically signed with a valid digital signature
- signing proves the identity of the developer, ensures that the APK binary has not been tampered with or modified by malware, and establishes update authority
- over the last 15 years, Google has developed 4 successive generations of APK signing schemes
- Scheme v1: JAR Signing (Android 1.0+)
- based on standard Java JAR signing: the signer hashes every individual file inside the APK and writes signature entries into `META-INF/MANIFEST.MF`
- flaw: during installation, the phone had to uncompress and parse every single file to verify signatures, making installation slow
- worse: an attacker could alter ZIP metadata and headers without invalidating the v1 signature (the infamous 'Master Key' vulnerability!)
- Scheme v2: APK Signing Block (Android 7.0 Nougat+)
- instead of hashing individual files, v2 hashes the ENTIRE binary file content using a cryptographic Merkle tree
- the signature is inserted into a dedicated 'APK Signing Block' right before the ZIP Central Directory
- if an attacker modifies even a single byte anywhere in the APK, ZIP headers, or resources, the signature is instantly invalidated!
- verification during installation is blazing fast because the OS doesn't need to uncompress files; it simply hashes the binary blocks in parallel
- Scheme v3: Key Rotation (Android 9.0 Pie+)
- prior to Android 9, if a developer's private signing key was compromised, or if an old 1024-bit RSA key needed upgrading, they were doomed!
- an app could NEVER change its signing key; changing keys meant releasing a completely new app and losing all existing users
- Scheme v3 solved this by introducing 'Key Rotation' with a cryptographic proof of lineage
- the new key is signed by the old key in a certificate chain, allowing the app to upgrade its signing key while preserving seamless updates for existing users!
- Scheme v4: Streaming / fs-verity (Android 11+)
- generates a separate `.idsig` file containing an `fs-verity` Merkle tree hash of all pages
- it enables 'Install on Streaming': on large 2 GB games, users can launch and start playing the game WHILE the remaining assets are still streaming down over Wi-Fi!
- modern Android tools (`apksigner`) sign APKs using all schemes simultaneously for universal backward compatibility
- beginner fallacy: i thought debug keystores were safe to use for releasing apps to friends
- the default debug keystore (`~/.android/debug.keystore`) uses a public password (`android`) and should NEVER be used for production; always create a secure private release keystore!
- understanding APK signing is the ultimate safeguard ensuring your software cannot be hijacked or impersonated
