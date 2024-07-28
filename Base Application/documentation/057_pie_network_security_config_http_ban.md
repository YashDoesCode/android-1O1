# Android 9 Pie Cleartext HTTP Ban - Forcing TLS/HTTPS by Default

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// In Android 9 Pie (API 28+):
// Making an HTTP request:
val url = URL("http://api.insecure.com/data")
// CRASH: java.io.IOException: Cleartext HTTP traffic to api.insecure.com not permitted!

// How to configure exceptions if absolutely necessary (res/xml/network_security_config.xml):
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">10.0.2.2</domain> <!-- Android Emulator localhost -->
    </domain-config>
</network-security-config>
```

### Notes, Curious Questions & System Mechanics

- one of the most impactful breaking changes in Android 9 Pie was the default ban on cleartext (unencrypted HTTP) network traffic
- prior to Android 9, if you made an HTTP request using `HttpURLConnection` or `OkHttp`, the phone would happily transmit data in plain text over the air
- on public coffee shop Wi-Fi or compromised routers, attackers could easily intercept passwords, session cookies, and private user data via Man-In-The-Middle (MITM) sniffing
- starting with Android 9 Pie (API 28), if your app has `targetSdk >= 28`, the Android networking stack rejects all cleartext HTTP connections by default!
- if your app attempts to connect to `http://example.com`, the OS throws a fatal `java.io.IOException: Cleartext HTTP traffic to example.com not permitted`
- this single change forced millions of web APIs and backend servers worldwide to upgrade their infrastructure to HTTPS and TLS 1.2/1.3
- what if you are a developer testing a local backend running on `http://localhost:8080` or the Android emulator's `http://10.0.2.2:8080`?
- Android provides the 'Network Security Configuration' feature (`res/xml/network_security_config.xml`) to declare explicit, safe exceptions
- in the config XML, you can whitelist specific domains like `10.0.2.2` or `localhost` using `<domain-config cleartextTrafficPermitted="true">`
- you then link this file in `AndroidManifest.xml` via `android:networkSecurityConfig="@xml/network_security_config"`
- importantly, you should NEVER set `android:usesCleartextTraffic="true"` globally in your `<application>` tag in production!
- setting `usesCleartextTraffic="true"` undoes all of Android 9's security protections and will fail automated enterprise security and penetration audits
- Network Security Config also lets you configure Certificate Pinning (pinning the SHA-256 hash of your server's SSL public key)
- certificate pinning prevents attackers from intercepting traffic even if they install a rogue root Certificate Authority on the user's phone
- it also lets you trust custom self-signed debug CA certificates ONLY in debug builds without leaking them into release APKs
- Android 9 also enforced strict modern TLS ciphers, deprecating old, vulnerable SSLv3 and TLS 1.0 protocols
- beginner fallacy: i thought that adding `<uses-permission android:name="android.permission.INTERNET" />` was all i needed to make network calls
- without HTTPS or an explicit Network Security Config, your internet calls will mysteriously fail on all modern Android devices!
- this change alone made the mobile web infinitely safer for billions of everyday smartphone users
- it demonstrated Google's willingness to break backward compatibility when user privacy and data security are on the line
