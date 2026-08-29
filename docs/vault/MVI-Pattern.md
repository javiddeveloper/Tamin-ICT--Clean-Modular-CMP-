---
tags: [architecture, convention]
---

# MVI and BaseViewModel

File: `core/core-ui/src/commonMain/kotlin/com/tamin/taminhamrah/base/BaseViewModel.kt`

```kotlin
abstract class BaseViewModel<STATE, PARTIAL_STATE, EVENT, INTENT>(initialState: STATE) : ViewModel()
```

Four type parameters. Every ViewModel in the project extends this.

## Contract

| Member | Direction | Purpose |
|---|---|---|
| `sendIntent(intent)` | UI → VM | the only way user input enters |
| `uiState: StateFlow<STATE>` | VM → UI | single source of truth for the screen |
| `events: Flow<EVENT>` | VM → UI | one-shot effects (navigation, snackbar) |
| `sendEvent(event)` | inside VM | `protected` |
| `doAsyncTask { … }` | inside VM | launches in `viewModelScope` |

Three methods every subclass must override:

```kotlin
protected abstract fun handleIntent(intent: INTENT): Flow<PARTIAL_STATE>
protected abstract fun reduceState(currentState: STATE, partialState: PARTIAL_STATE): STATE
protected abstract fun createErrorState(message: String): PARTIAL_STATE
```

## How the pipeline works

```
intentChannel (UNLIMITED)
  → flatMapMerge { handleIntent(it).catch { emit(createErrorState(...)) } }
    → scan(initialState) { state, partial -> reduceState(state, partial) }
      → _uiState.value = newState
```

Details that bite:

- **`flatMapMerge` means intents run concurrently, not sequentially.** If ordering matters, enforce it yourself inside `handleIntent`. It also means two quick taps can start two in-flight requests — guard with `if (state.isLoading) return@flow` where a duplicate submission would be harmful.
- `catch` is applied per intent, so one failure does not tear down the pipeline. The default error message is `"خطای نامشخص"`.
- `eventChannel` is `BUFFERED` and consumed via `receiveAsFlow`, so it has a single consumer.
- `doAsyncTask` sets no custom dispatcher (there is a `// todo` in the source about this) — move IO work to an appropriate dispatcher yourself.

## The Contract file

Each screen has `contract/<Screen>Contract.kt` declaring four types together: `State` (data class), `PartialState` (sealed), `Event` (sealed), `Intent` (sealed).
Good examples to copy: `feature/profile/.../ui/identity/contract/IdentityInContract.kt` and `.../ui/activeRelation/contract/ActiveRelationContract.kt`.

## Testing

`turbine` is available in `commonTest` of every feature module automatically (via `TaminHamrahKmpFeaturePlugin`) — use it to assert on `uiState` and `events`.

⚠️ **`getString(Res.string...)` inside a ViewModel's `handleIntent`/validation path is unreliable under `testDebugUnitTest`**, even with `:feature:orotez-protez`'s own Robolectric setup (`unitTests.isIncludeAndroidResources = true` + a staged `robolectric-android-all` jar) copied verbatim into the module. The exception it throws gets caught by the `flatMapMerge.catch` in `BaseViewModel` and turned into the generic error `PartialState` instead of the specific one you emitted before/after the `getString` call — confirmed independently in `IssuanceCertificateViewModel` and again while building `:feature:pregnancyPay`. Neither `:feature:orotez-protez`'s nor `:feature:pregnancyPay`'s test suites assert on the specific message/partial state produced by such a call; they only assert on surrounding, `getString`-independent behavior (e.g. "step didn't advance"). Before writing a test that depends on one, run it first — don't assume Robolectric fixes it.

Related: [[Overview]] · [[Navigation]] · [[Adding-a-Feature]]
