---
tags: [architecture]
---

# Navigation

Library: **Compose Multiplatform Navigation** (`org.jetbrains.androidx.navigation:navigation-compose`) with **type-safe routes** backed by `kotlinx.serialization`.
(Note: `androidx.navigation3` is also in the version catalog and in `androidApp`, but the main graph runs on navigation-compose.)

## Key files

| File | Role |
|---|---|
| `shared/.../ui/navigation/TaminHamrahNavGraph.kt` | root graph — every `xxxGraph()` is attached here |
| `shared/.../ui/navigation/NavRoutes.kt` | app-level routes |
| `shared/.../ui/navigation/NavigationTab.kt` | bottom bar tabs |
| `shared/.../ui/navigation/FeatureNavigation.kt` | `NavController.navigateToFeature(flag)` — bridge from `FeatureFlag` to a destination |
| `feature/<x>/.../Navigation.kt` | that feature's routes and graph |

## The per-feature pattern

```kotlin
@Serializable
sealed interface ProfileRoute {
    @Serializable data object Graph : ProfileRoute
    @Serializable data class  Main(val userId: String? = null) : ProfileRoute
    @Serializable data object ElectronicFile : ProfileRoute
    // …
}

fun NavGraphBuilder.profileGraph(
    navController: NavController,
    onNavigateToIdentity: (String?) -> Unit,   // destinations outside this feature → callback
    onOpenUrl: (String) -> Unit,
    onBack: () -> Unit
) {
    navigation<ProfileRoute.Graph>(startDestination = ProfileRoute.Main()) { … }
}
```

The rule: **navigation within a feature** calls `navController.navigate(Route.X)` directly; **navigation to another feature** is passed up as a callback, so feature modules never depend on each other.

## Getting a ViewModel

```kotlin
val vm = koinViewModel<XViewModel>()                               // per-screen scope
val vm = backStackEntry.sharedViewModel<XViewModel>(navController) // shared across one graph
```

`sharedViewModel` is a project-internal extension (`com.tamin.taminhamrah.ui.sharedViewModel`).

## Transitions

`composableWithFadeTransitions<Route>` — a fade variant of `composable`, defined in `core-ui`. Use it for ordinary screens so the app feels consistent; plain `composable` also appears in the codebase but is not the dominant pattern.

## Dynamic routing from the server menu

`FeatureFlag` (from the server) → `navigateToFeature(flag)` → the `navigateToXxx()` function each feature exports. Full detail in [[Feature-Flags]].

`navigateToFeature` does **not** check the flag and returns `false` when no screen exists. Menu taps check the flag in their view models; every link (OS, stories, assistant) goes through `ResolveDeepLinkUseCase` via `LocalDeepLinkHandler` — see [[Deep-Links]]. Never call `navigateToFeature` from a link directly.

## The bottom bar's trailing slot

`FloatingGlassNavigationBar` renders the Agent orb through its `trailingButton` slot — a sibling
of the glass pill, not a row item inside it. That keeps the pill's tab layout and its animated
highlight maths independent of whether the orb is there: `itemCount` and `selectedIndex` describe
the tabs only. (The bar mirrors `selectedIndex` for RTL internally, so callers pass the plain LTR
index.)

The orb itself is `AgentOrb` in `core-ui/ui/components/` — the same composable the assistant's
welcome screen draws at 148 dp. Every metric in it is a fraction of its diameter, so one `size`
parameter covers both places.

It is gated on `ObserveAgentAvailabilityUseCase` (the `AGENT` menu flag **and** the server's chat
permission); when that is off, `trailingButton` is null and the pill takes the full width back.

Related: [[Modules]] · [[Feature-Flags]] · [[AI-Agent]]
