---
tags: [convention]
---

# Theme tokens

Compose UI must use tokens from `core-ui/ui/theme/`. Do not hardcode colors, `.dp`/`.sp`, or UI copy.

| Kind | Where |
|---|---|
| Color | `SemanticColors.kt` via `LocalTaminColors.current` |
| Space, radius, icon, elevation, stroke, header decoration | `Shape.kt` (`Spacing`, `CornerRadius`, `IconSize`, `Elevation`, `Thickness`, `ButtonDimens`, `HeaderDecoration`, `ShimmerSize`) |
| Type | `Type.kt` via `MaterialTheme.typography` |
| Copy | `core-ui/.../composeResources/values/strings.xml` |

If a value is missing, add a token there — do not leave a literal in a feature.

Persian digits: [[Typography]]. Components already in `core-ui/ui/components/`: [[Modules]].

## RTL is hardcoded, not locale-driven

`TaminHamrahTheme.kt` sets `LocalLayoutDirection provides LayoutDirection.Rtl`
unconditionally — this is a Persian-only app today, there is no LTR locale support.
Don't add locale-based direction switching speculatively.

- Any numeric value, tracking code, or ID must render LTR even inside the RTL page:
  wrap it in `NumericText` (`core-ui/.../ui/components/TaminPrimitives.kt`).
  `DetailRow(..., numeric = true)` already does this for you.
- Gradients that must visually run the same direction regardless of layout direction
  use `startToEndGradient(colors)`.
- Icon-vs-text ordering in buttons should use the existing `IconPosition` parameter
  rather than manual `LocalLayoutDirection` branching, unless no such parameter exists
  yet.
- All previews should be wrapped with `@PreviewRtlTheme` / `PreviewRtlThemeContent`
  (`core-ui/.../ui/PreviewTaminTheme.kt`) so they render correctly by construction —
  provide both light and dark preview variants.

## Design-system-only

Screens must use the `Tamin*`-prefixed components and these tokens — not raw Material3
defaults except as a last resort when no equivalent exists yet:

- Root screen backgrounds: `Modifier.background(LocalTaminColors.current.bgPage)`.
  Cards/surfaces: `Modifier.taminSurface()` instead of a raw `Card { }`.
- Top bar: `TaminTopAppBar` + `TaminTopAppBarButton`;
  `Scaffold(contentWindowInsets = WindowInsets(0))` so the top bar draws behind the
  status bar correctly.
- Buttons: `TaminPrimaryButton`, `TaminOutlinedButton`, `TaminFilledButton`.
- Rows/labels: `LabeledBlock`, `DetailRow(numeric = true)`, `SectionLabel`,
  `StatusPill`, `StatTile`, `TaminDivider`, `TaminEmptyState`.
- Blur/glass effects: `dev.chrisbanes.haze` — `HazeState()` +
  `Modifier.hazeSource(state = hazeState)`.
- Scroll-linked header motion: `rememberScrollMotionState(maxMotionDistance = ...)` +
  `motionState.observeLazyListState(lazyListState)` in a `LaunchedEffect` — see
  [[TopArea-System]].

## Localization

Strings/drawables go through Compose Multiplatform resources
(`stringResource` / generated `Res.string.*`), never classic Android
`stringResource(R.string...)`. Inside ViewModels (non-composable context), prefer
hardcoding the Persian string literal over the suspend `getString(Res.string...)`
variant for anything validation/error-message-shaped — see the `getString()`-in-
ViewModel test hazard noted in [[MVI-Pattern]].
