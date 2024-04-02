# backup_rules.xml - Legacy Auto Backup Configuration (Android 11 and Below)

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
<!-- Base Application/app/src/main/res/xml/backup_rules.xml -->
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
    <!-- TODO: Use <include> and <exclude> to control what is backed up.
    <include .../>
    <exclude .../>
    -->
</full-backup-content>
```

### Notes, Curious Questions & System Mechanics

- the file `backup_rules.xml` in `res/xml/` is the companion to `data_extraction_rules.xml`
- it uses the older `<full-backup-content>` root tag introduced back in Android 6.0 Marshmallow (API 23)
- why does `Base Application` have BOTH `backup_rules.xml` and `data_extraction_rules.xml`?
- because `Base Application` specifies `minSdk = 29` (Android 10) in `app/build.gradle.kts`
- devices running Android 10 (API 29) and Android 11 (API 30) do not know how to parse `<data-extraction-rules>`
- when your app runs on an Android 10 or 11 device, the operating system looks exclusively at `android:fullBackupContent="@xml/backup_rules"`
- when your app runs on Android 12, 13, 14, or 15, the OS detects `android:dataExtractionRules` and uses the newer file instead
- this dual-file configuration is the gold standard for backward-compatible Android enterprise architecture
- in `backup_rules.xml`, you also declare `<include>` and `<exclude>` tags with `domain` attributes
- the supported domains are `file`, `database`, `sharedpref`, `external`, and `root`
- for example: `<exclude domain="database" path="encrypted_database.db" />` prevents the SQLite database from syncing to Google Drive
- Auto Backup runs automatically in the background when the phone is charging, connected to Wi-Fi, and has been idle for at least 24 hours
- the backup data is stored in a private folder in the user's Google Drive account, encrypted and inaccessible to other apps
- when the user uninstalls and reinstalls the app, Android restores these files automatically before `MainActivity.onCreate()` is called
- this can cause strange bugs during development: you uninstall the app, reinstall it, and old state is still there!
- you can disable auto-restore during development using ADB: `adb shell bmgr run` or setting `android:allowBackup="false"`
- beginner fallacy: i thought `allowBackup="false"` was always the best option for lazy security
- disabling backup completely ruins the user experience because users lose all their local favorites and settings when switching phones
- the professional solution is keeping `allowBackup="true"` while carefully crafting exclusions in these two XML files
- studying both files taught me how Android maintains backward compatibility across different OS generations
- it is a clean example of defensive, production-ready system configuration in modern Android Studio templates
