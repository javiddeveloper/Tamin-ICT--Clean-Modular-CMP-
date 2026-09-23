---
tags: [architecture, ui, convention]
---

# TopArea System — Scroll-Driven Collapsing Headers

Package: `core/core-ui/src/commonMain/kotlin/com/tamin/taminhamrah/ui/toparea/`

```
TopAreaState.kt        — the state holder + the NestedScrollConnection that drives it
TopAreaConnectors.kt   — @Composable factories + Modifier.driveTopArea(...)
TopAreaBehaviors.kt    — Modifier extensions a header's children use to react to fold progress
```

Tests: `core/core-ui/src/commonTest/.../ui/toparea/TopAreaStateTest.kt` — covers the drag
arithmetic (fold/unfold, clamping at both edges). Good place to check expected behavior before
changing anything here.

## The problem it solves

A screen has a tall header (title, stats, an icon) sitting above a scrollable list. As the user
scrolls the list down, the header should **fold** — shrink, fade its extra content, and settle
into a slim bar — and it should fold *from the drag itself*, not from a separate scroll-offset
listener, so it moves under the finger with zero lag. Release mid-fold and it should **snap** to
whichever edge (fully open / fully closed) the gesture was heading toward.

This is a common enough interaction (collapsing toolbars) that the project has a shared package
for it instead of every feature hand-rolling its own `NestedScrollConnection`.

> ⚠️ There is an **older, separate** system for header motion: `ScrollMotionState`
> (`core-ui/.../ui/motion/ScrollMotionState.kt`, comments in Persian), still used by
> `feature/profile/ProfileScreen`, `feature/my-inbox/MyInboxScreen`, and
> `feature/history/HistoryJobInfoScreen`. TopArea is the newer replacement. Don't mix the
> two in one screen; when touching one of the three older screens, that's a separate decision
> (ask the user) rather than a silent migration.

Real consumers so far: `feature/profile`'s `ActiveRelationScreen` (see the use case below, no
straddling card); `feature/workshops`'s `LegalRepresentativeWorkshopsScreen` (معرفی نماینده اشخاص
حقوقی's hub page) and `ObjectionStatusScreen` (پیگیری وضعیت اعتراض's list page), and
`feature/taminServices`'s `EmployerOnlineServicesScreen` — all three re-declared an identical
file-local `straddlePreviousSibling` for a "folding header + pinned card that rides up into its
seam" shape; and `feature/calculateWagePension`'s `CalculateWagePensionScreen`
(`CalculateWagePensionTopArea`, folding header + `CalculateWagePensionStatsCard`), the fourth such
screen, which is what finally triggered the extraction: **`Modifier.straddlePreviousSibling(overlap)`
now lives in `TopAreaBehaviors.kt` itself**, public. The three earlier screens still carry their own
identical private copy — migrating them to the shared one is a small, safe follow-up, not done yet
since it wasn't required to add the fourth.

## Mental model

One number drives everything: **`progress`**, a `Float` from `0f` (fully expanded) to `1f`
(fully collapsed). Every visual effect on the header — fading a subtitle, shrinking an icon,
hiding a stats row — is just "some `Modifier` reading `progress` and mapping it to alpha / scale
/ translation / height."

```
rawOffsetPx   — how many px the header has been dragged closed, clamped to [0, maxOffsetPx]
maxOffsetPx   — the total drag budget (expandedHeight − collapsedHeight)
progress      — rawOffsetPx / maxOffsetPx, clamped to [0, 1]           ← what everything reads
```

`progress` is backed by Compose snapshot state, so **only read it inside a layout/draw phase**
(`graphicsLayer {}`, `layout {}`, `drawBehind {}`) — the behavior modifiers below already do this
correctly. Reading it during plain composition would recompose the whole header on every scrolled
pixel.

## The building blocks

| Piece | What it does |
|---|---|
| `TopAreaState` | Holds `rawOffsetPx` / `progress` / `measuredHeightPx`. Constructed only through the two `remember*` functions below — never directly. |
| `rememberTopAreaState(expandedHeight, collapsedHeight)` | Creates the state from two **guessed** `Dp` constants. Simple, but drifts out of sync if the header's real content (a longer string, an extra line) changes its height. |
| `rememberMeasuredTopAreaState { state -> Header(...) }` | Creates the state by **measuring** the header composable itself at `progress = 0` and `progress = 1` (off-screen, unplaced). The drag budget is always exactly right. **Preferred** — use this unless there's a reason not to. |
| `Modifier.driveTopArea(state, listState)` | Wires the header's fold to a `LazyListState`'s own drag. Overloads also exist for `LazyGridState` and `ScrollState`. Apply to the scrollable itself. |
| `Modifier.reportTopAreaHeight(state)` | Put on the real (interactive) header. Feeds its actual rendered height back into `state` every time it changes. |
| `Modifier.topAreaContentSpacer(state)` / `topAreaContentPadding(state, rest)` | Reserves the header's current height as space for the list underneath (the header floats on top, overlapping the list). Spacer = a leading list item; content-padding = `LazyColumn`'s `contentPadding` (avoids double-gapping with `verticalArrangement.spacedBy`). |
| `Modifier.straddlePreviousSibling(overlap)` | Shifts a child (typically a card) up by `overlap` to ride into the previous sibling's (the header's) reserved bottom space, reporting a height reduced by that same amount so `reportTopAreaHeight` sees the true visual footprint. The header must itself reserve at least `overlap` of empty bottom space (e.g. via its own `bottomPadding`) or the card rides onto real content instead of blank gradient. |

### Behavior modifiers (put these on the header's *children*)

| Modifier | Effect as `progress` goes 0 → 1 |
|---|---|
| `Modifier.topAreaAlpha(state, from, to, startProgress, endProgress)` | Fades alpha `from → to` |
| `Modifier.topAreaTranslateY(state, distance, ...)` | Slides up by up to `distance` |
| `Modifier.topAreaScale(state, from, to, ...)` | Scales `from → to` |
| `Modifier.topAreaHide(state, ...)` | Fades out **and** shrinks height to zero — for pieces that should stop occupying space and stop being tappable once collapsed (e.g. a stats row) |
| `Modifier.topAreaFixed()` | No-op. Purely documents "this child intentionally never moves" next to siblings that do. |

All of them take `startProgress`/`endProgress` (default `0f..1f`) so different children can
animate on different sub-ranges of the fold — e.g. a title fades out over `0f..0.5f` while a
status line fades in over `0.5f..1f`, so they hand off instead of overlapping mid-fade.

## How a drag actually flows through it

`state.connection(contentCanScrollForward)` returns the `NestedScrollConnection` that
`driveTopArea` wires up. In plain terms:

1. **`onPreScroll`** (runs *before* the list scrolls) — if the drag is heading toward later
   content (`available.y < 0`) **and** the list still has more to reveal
   (`contentCanScrollForward()`), fold the header by that amount first. This is what makes the
   header keep the list fully expanded until the list has actually scrolled to its start — no
   separate "list is too short to scroll" special case needed anywhere.
2. **`onPostScroll`** (runs *after* the list scrolls, with whatever it didn't consume) — if the
   list has hit its own start and there's still upward drag left over (`available.y > 0`), unfold
   the header by that amount.
3. **`onPreFling`** — on release mid-fold, springs to whichever edge (open/closed) the fling
   velocity implies (or the nearer edge, for a slow release). Fire-and-forget into a
   `CoroutineScope` rather than suspended on directly, so releasing the drag doesn't hold up the
   list's own fling handling.

Net effect: drag down into content → header folds under the finger, in lock-step, until it's
fully closed, then the list itself starts scrolling. Drag back up → list scrolls to its top first,
then the header unfolds. Let go mid-gesture → header snaps to whichever side it was headed.

## Use case: `feature/profile`'s Active Relation screen

This is the one real screen wired up today — a solid template to copy.

**The header** (`ActiveRelationHeader.kt`) is a rounded-bottom gradient card. Expanded, it shows a
big icon, a status dot + text, and an active/inactive/last-checked-time summary line. Collapsed,
it's just the slim top-app-bar with a title.

```kotlin
// Two texts occupy the same spot: title fades out, status fades in, handing off at progress 0.5
Text(title, modifier = Modifier.topAreaAlpha(topAreaState, from = 1f, to = 0f, endProgress = 0.5f))
Text(statusText, modifier = Modifier.topAreaAlpha(topAreaState, from = 0f, to = 1f, startProgress = 0.5f))

// The icon + status-dot + summary-line block folds away entirely and stops taking space
Column(
    modifier = Modifier.topAreaHide(topAreaState).padding(bottom = Spacing.xl),
) { /* icon, dot, summary text */ }
```

**The screen** (`ActiveRelationScreen.kt`) wires it together:

```kotlin
// 1. Create the state by measuring the real header at both extremes — no guessed heights.
val topArea = rememberMeasuredTopAreaState { state ->
    ActiveRelationHeader(activeCount = ..., topAreaState = state, onBackClicked = {})
}
val listState = rememberLazyListState()

Box(Modifier.fillMaxSize().background(taminColors.bgPage)) {
    LazyColumn(
        state = listState,
        // 2. Drive the fold from the list's own drag.
        modifier = Modifier.fillMaxSize().driveTopArea(topArea, listState),
        // 3. Reserve the header's live height as leading space, instead of a spacer item.
        contentPadding = topAreaContentPadding(state = topArea, rest = PaddingValues(bottom = ...)),
    ) { /* list items */ }

    // 4. The real, interactive header floats on top of the list and reports its own height back.
    ActiveRelationHeader(
        activeCount = ..., topAreaState = topArea, onBackClicked = { onIntent(...) },
        modifier = Modifier.align(Alignment.TopCenter).reportTopAreaHeight(topArea),
    )
}
```

Why a `Box` overlay instead of a `Scaffold` top bar: the header needs to draw edge-to-edge behind
the status bar, and a `Scaffold`'s own content padding would double up with `TaminTopAppBar`'s
inset handling and push the header down.

## Gotchas worth knowing before touching this

- **The header's fold-away content must span the header's full `0f..1f` progress range**, the
  same range `maxOffsetPx` was measured against — a narrower range makes the header visually
  finish collapsing before the drag budget runs out (finger keeps dragging, nothing moves).
- **Prefer `rememberMeasuredTopAreaState` over `rememberTopAreaState`** unless you have a specific
  reason to hardcode the two heights — hardcoded heights silently drift out of sync with real
  content changes (longer strings, font-size tweaks).
- **Sign convention is scroll-delta, not "finger direction"** — matches
  `ScrollableState.scrollBy`: scrolling toward later content dispatches `available.y < 0`;
  scrolling back dispatches `available.y > 0`. Getting this backwards makes the header fold on the
  wrong drag direction, and it's easy to miss in a quick manual test. If in doubt, re-derive from
  `TopAreaState.kt`'s class doc rather than guessing.
- `TopAreaState` is only ever constructed via the two `remember*` factories (its constructor is
  `internal`) — don't try to instantiate it directly outside tests.
- **A header passed to `rememberMeasuredTopAreaState` must not contain anything that keeps
  requesting animation frames on its own** — most commonly an infinite-repeat animation
  (`rememberInfiniteTransition`/`infiniteRepeatable`), but a `LaunchedEffect` tied to first
  composition or a live video/Lottie player is the same problem. The probe composes `header` twice,
  off-screen and unplaced, every time it re-measures — that content is never drawn, so a running
  infinite animation there is pure wasted work for as long as Compose keeps the probe slot
  composed. `ActiveRelationHeader`'s `AnimatedRingHeaderIcon` hit exactly this on the system's first
  real use; the fix was a `TopAreaState.isMeasureProbe` flag (`TopAreaState.kt`) threaded down to an
  `animated: Boolean` parameter on `AnimatedRingHeaderIcon`/`RippleRing` (`GlassIconTile.kt`) so the
  probe instances render statically instead. If a future header adopts another infinitely-animating
  child, gate it the same way rather than assuming "side-effect-free" alone will catch it.
- **`Modifier.driveTopArea(state, scrollState)` must sit *above* (before, i.e. outer to)
  `Modifier.verticalScroll(scrollState)` in the chain** — a `nestedScroll` modifier only sees
  dispatch from scrollables *nested inside* it, so `.verticalScroll(scrollState).driveTopArea(...)`
  compiles fine but never folds anything. `CalculateWagePensionScreen` is the first screen to use
  the `ScrollState` overload (a plain `Column`, not a `LazyColumn`); its body orders this as
  `.driveTopArea(topArea, scrollState).verticalScroll(scrollState)`. The `LazyColumn`/`LazyGridState`
  overloads don't have this footgun — the caller's whole `modifier` is inherently outer to the
  list's own internal scrolling implementation regardless of chain order.

Related: [[Overview]] · [[MVI-Pattern]] · [[Adding-a-Feature]]
