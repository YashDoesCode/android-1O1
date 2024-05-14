# rememberSaveable & mutableStateOf - State That Survives Destruction

> *Personal Learning Log & Ecosystem Deep-Dive | Study notes on Base Application skeleton*

### Code Reference / Architecture Snippet

```kotlin
// In Base Application/app/src/main/java/com/keeper/webguard/MainActivity.kt:
var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
```

### Notes, Curious Questions & System Mechanics

- this single line of code in `MainActivity.kt` contains the core secret of how Jetpack Compose handles reactive UI state
- in Compose, UI is a direct mathematical function of State: `UI = f(State)`
- when state changes, Compose automatically re-executes composable functions that read that state—a process called 'Recomposition'
- let's dissect the parts: `mutableStateOf(AppDestinations.HOME)` creates an observable state holder holding the initial tab value
- when `currentDestination = it` is executed in the `onClick` handler, Compose detects the write and schedules a redraw of the navigation items
- now, what does `remember` do? If you wrote `var currentDestination = mutableStateOf(...)` without `remember`, state would reset on every single redraw!
- `remember` stores the object in the Compose Slot Table, preserving it across recompositions as long as the composable stays in the tree
- BUT `remember` has a fatal flaw: if the user rotates the phone from portrait to landscape, the Activity is destroyed, and `remember` is wiped out!
- that is why modern Android templates use `rememberSaveable` instead of plain `remember`!
- `rememberSaveable` does everything `remember` does, PLUS it automatically saves the value into the Android OS `savedInstanceState` Bundle!
- when the activity is recreated after rotation or process death, `rememberSaveable` automatically restores the previous value from the Bundle
- this means that if the user was on the 'Favorites' tab and rotates their screen, they stay on 'Favorites' instead of jumping back to 'Home'!
- how can `rememberSaveable` save an enum like `AppDestinations`? Because Kotlin enums implement `java.io.Serializable` by default
- `rememberSaveable` supports all primitives, Strings, Parcelable, and Serializable objects out of the box
- for custom data classes, you can provide a custom `Saver` object to serialize complex state into a Bundle
- now look at the `by` keyword: `var currentDestination by rememberSaveable { ... }`
- `by` uses Kotlin Property Delegation: it allows you to get and set `currentDestination` directly as an `AppDestinations` value
- without `by`, you would have to type `currentDestination.value` everywhere you read or write the state
- beginner fallacy: i thought `rememberSaveable` saved data to disk permanently like a database
- it only saves state in volatile OS memory (`savedInstanceState`); if the user swipes the app away from recents, the state is cleared
- understanding `rememberSaveable` is the bridge between Compose in-memory state and the Android OS lifecycle persistence contract
