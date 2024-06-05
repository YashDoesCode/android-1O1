# Android Vector Drawables - XML Path Data vs Standard SVGs

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
<!-- Base Application/app/src/main/res/drawable/ic_home.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z"/>
</vector>
```

### Notes, Curious Questions & System Mechanics

- in `Base Application/app/src/main/res/drawable/`, look at `ic_home.xml`, `ic_favorite.xml`, and `ic_account_box.xml`
- these are NOT PNG or JPEG images; they are Android Vector Drawables (AVDs) written in XML
- notice the root tag is `<vector>`, defining intrinsic dimensions: `android:width="24dp"` and `android:height="24dp"`
- `android:viewportWidth="24"` and `android:viewportHeight="24"` define the virtual coordinate grid for the SVG path commands
- inside the vector, `<path>` has an `android:pathData` string: `"M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z"`
- what does that cryptic string mean? It is standard SVG path syntax: `M` = Move to, `v` = vertical line, `h` = horizontal line, `z` = close path
- because the icon is defined mathematically as bezier curves and coordinates, it can scale to ANY resolution with ZERO pixelation or blur!
- one single 500-byte XML vector replaces 5 separate PNG files across `mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, and `xxxhdpi` folders
- this dramatically reduces APK download size: replacing 50 PNG icons with Vector Drawables can shave 5-10 MB off an app
- why doesn't Android just render standard `.svg` files directly?
- because the full W3C SVG specification is massive: it supports embedded CSS, JavaScript, animations, filters, and HTML tags
- parsing full SVGs at runtime on a mobile device would be slow, memory-intensive, and a huge security vulnerability
- Android's `<vector>` format is a stripped-down, hardware-accelerated subset of SVG optimized for rapid GPU rasterization
- Android Studio includes a built-in 'Vector Asset Studio' (Right click drawable -> New -> Vector Asset) that auto-converts SVG to AVD XML
- in Jetpack Compose, you load this vector effortlessly using `painterResource(it.icon)` as seen in `MainActivity.kt`
- Compose also supports tinting vector drawables on the fly using `Icon(..., tint = MaterialTheme.colorScheme.primary)`
- you can also animate vector paths using `AnimatedVectorDrawable` to morph a play button into a pause button seamlessly
- beginner fallacy: i thought vector drawables should be used for detailed photographs and complex illustrations
- rendering a photo with 10,000 vector path curves will cause CPU spikes and frame drops; photos must always remain raster WebP/JPEG
- vectors are strictly designed for icons, geometric shapes, badges, and flat illustrations
- understanding vector drawables is the cornerstone of modern, lightweight, high-density Android UI engineering
