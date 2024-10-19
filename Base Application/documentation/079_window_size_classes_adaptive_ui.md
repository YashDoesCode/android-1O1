# Window Size Classes - Compact, Medium, Expanded for Foldables & Tablets

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// How Window Size Classes categorize screen widths:
// 1. Compact:  Width < 600 dp  (Standard smartphones in portrait)
//    -> Layout: Bottom Navigation Bar, single-column lists
// 2. Medium:   600 dp <= Width < 840 dp (Foldables unfolded, small tablets, landscape phones)
//    -> Layout: Navigation Rail, master-detail list + preview
// 3. Expanded: Width >= 840 dp (10-inch tablets, desktop ChromeOS)
//    -> Layout: Persistent Navigation Drawer, multi-column grids
```

### Notes, Curious Questions & System Mechanics

- for years, Android developers struggled to design apps that looked good across phones, foldables, and large tablets
- developers wrote messy code checking raw screen resolutions or hardware device models: `if (isTablet())` or `if (screenWidthPx > 1080)`
- this was brittle and failed miserably when foldables arrived or when users resized apps in split-screen multi-window mode
- Google solved this by introducing the 'Window Size Classes' specification (`androidx.compose.material3:material3-window-size-class`)
- Window Size Classes do not care what physical hardware the device is; they measure the CURRENT available window width and height
- the specification defines three opinionated width breakpoints: Compact, Medium, and Expanded
- Breakpoint 1: Compact (Width < 600 dp)
- covers typical smartphones in portrait orientation; UI should prioritize one-handed thumb interaction with a Bottom Navigation Bar
- Breakpoint 2: Medium (Width between 600 dp and 840 dp)
- covers folding phones unfolded, small 7-inch tablets, and smartphones rotated into landscape orientation
- UI should shift navigation to a vertical Navigation Rail on the left side to maximize vertical screen real estate
- Breakpoint 3: Expanded (Width >= 840 dp)
- covers large 10-inch+ tablets, desktop Chromebooks, and Samsung DeX desktop mode
- UI can display persistent Navigation Drawers, multi-pane master-detail layouts, and side-by-side editing surfaces
- in `Base Application`, `NavigationSuiteScaffold` (studied in doc 033) automatically queries these exact Window Size Classes under the hood!
- when your app runs on a compact phone, `NavigationSuiteScaffold` renders a bottom bar; when dragged onto an expanded screen, it becomes a rail
- you can also query size classes directly using `currentWindowAdaptiveInfo().windowSizeClass` to swap between single-pane and two-pane layouts
- this adapts dynamically in real-time: if a user unfolds their Galaxy Z Fold while using your app, the layout smoothly transitions from Compact to Medium without reloading!
- beginner fallacy: i thought responsive design meant scaling font sizes and button widths to stretch across wide screens
- stretching a phone layout across a 12-inch tablet looks horrific with 1000px wide buttons; responsive design means adding more panels and columns!
- Window Size Classes provide the architectural guidelines to build apps that feel natively crafted for every screen size in the Android ecosystem
