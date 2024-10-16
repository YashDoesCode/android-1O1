# Compose Recomposition - How Jetpack Compose Knows What to Redraw

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// How Smart Recomposition works in Base Application:
@Composable
fun KeeperWebGuard20App() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(...) {
        // When currentDestination changes, Compose recomposes ONLY the navigation items!
        // Greeting() is SKIPPED because its parameter ("Android") hasn't changed!
        Greeting(name = "Android")
    }
}
```

### Notes, Curious Questions & System Mechanics

- the magic of Jetpack Compose centers around one fundamental concept: 'Recomposition'
- Recomposition is the process of re-executing composable functions when their observed state inputs change to emit updated UI data
- a common beginner panic is: 'If Compose re-runs my functions, won't it re-render the whole screen 60 times a second and drain the battery?'
- the answer is a resounding NO, thanks to 'Smart Recomposition' and Stability Inference
- when the Compose compiler processes a `@Composable` function, it analyzes every parameter to determine if it is 'Stable' or 'Immutable'
- primitives (`Int`, `Float`, `Boolean`), `String`, and Kotlin `enum` classes (like `AppDestinations`) are inherently Immutable
- when a state change occurs (e.g. `currentDestination` changes from `HOME` to `FAVORITES` in `MainActivity.kt`), Compose analyzes the call tree
- Compose sees that `currentDestination` is read inside `NavigationSuiteScaffold` items; so it recomposes those specific navigation items
- then, Compose looks at `Greeting(name = "Android")`: its input parameter is the literal string `"Android"`, which has NOT changed!
- Compose performs a 'Skippable Recomposition': it completely skips executing the `Greeting` function, reusing its cached UI nodes from the Slot Table!
- this means Compose updates only the exact tiny slice of the UI tree that actually depends on the changed state
- Recomposition is also 'Optimistic': if a new state change arrives while recomposition is still running, Compose cancels the in-flight recomposition and restarts with the latest state
- this ensures the UI never lags behind fast user input or rapid scrolling gestures
- another critical rule: Recomposition can execute in ANY order!
- if you have three child composables in a `Column`, Compose may execute the third one before the first one if layout passes prioritize it
- therefore, composable functions must be strictly side-effect-free: never increment a global counter or write to disk inside a composable body!
- for side effects (like launching a network call or showing a snackbar), Compose provides dedicated effect handlers like `LaunchedEffect` and `SideEffect`
- beginner fallacy: i thought i had to call a method like `notifyDataSetChanged()` or `invalidate()` to trigger a redraw
- in Compose, simply updating a `mutableStateOf` variable automatically triggers recomposition with zero manual invalidation code
- understanding stability and recomposition skipping is the secret to building silky-smooth 120 FPS Android applications
- it is one of the most sophisticated reactive compiler optimizations ever engineered for mobile devices
