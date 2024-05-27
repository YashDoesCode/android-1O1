# res/drawable vs res/mipmap - Why Launcher Icons Have Their Own Folder

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// In Project View under app/src/main/res/:
res/
├── drawable/
│   ├── ic_home.xml
│   ├── ic_favorite.xml
│   ├── ic_account_box.xml
│   ├── ic_launcher_background.xml
│   └── ic_launcher_foreground.xml
└── mipmap-anydpi/
    ├── ic_launcher.xml
    └── ic_launcher_round.xml
```

### Notes, Curious Questions & System Mechanics

- one of the most frequent questions beginners ask in Android Studio is: 'Why do we have both `res/drawable` and `res/mipmap`?'
- they both contain images and icons, so why did Google separate them into two different resource directories?
- the short answer: `res/mipmap` is EXCLUSIVELY reserved for application launcher icons shown on the home screen
- `res/drawable` is for all other in-app graphics, buttons, backgrounds, vectors, and illustration assets
- why the strict separation? It all comes down to APK optimization and device density filtering during installation
- when Google Play generates an optimized split APK for a user's phone, it strips out unused drawable densities to save download size
- if a user has an `xxhdpi` phone, Google Play delivers an APK containing only `xxhdpi` drawables, deleting `mdpi` and `hdpi` drawables
- BUT home screen launcher apps (like Nova Launcher or Pixel Launcher) frequently display app icons at a HIGHER density than the phone screen!
- for example, an `xhdpi` phone might want to render a huge 48x48 icon on a 5x5 home grid, requiring the crisper `xxhdpi` icon asset to prevent blurring
- if launcher icons were placed in `res/drawable`, Google Play's density stripper would delete that higher-density icon, resulting in pixelated icons!
- the build system and Google Play know NEVER to strip densities from `res/mipmap/`
- all density buckets inside `mipmap-mdpi`, `mipmap-hdpi`, `mipmap-xhdpi`, `mipmap-xxhdpi`, and `mipmap-xxxhdpi` are preserved in the APK
- this guarantees that regardless of what custom launcher or zoom setting the user has, their home screen icon is razor sharp
- in `Base Application`, `AndroidManifest.xml` explicitly references `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`
- meanwhile, our in-app navigation icons (`ic_home.xml`, `ic_favorite.xml`, `ic_account_box.xml`) live inside `res/drawable/`
- in modern Android, launcher icons in `mipmap-anydpi/` are adaptive XML files that combine foreground and background layers
- beginner fallacy: i thought i should put all my app's photos and illustrations in `res/mipmap` just to be safe
- putting general app images in `mipmap` bloats your APK size because all density copies remain bundled instead of being stripped
- remember the golden rule: launcher icons go in `mipmap`, everything else goes in `drawable`
- understanding this distinction cleared up years of confusing advice from outdated Android tutorials
- it is a perfect illustration of how Android balances download bandwidth with pixel-perfect visual fidelity
